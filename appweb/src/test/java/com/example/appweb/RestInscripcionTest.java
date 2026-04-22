package com.example.appweb;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class RestInscripcionTest {

	@Autowired
	MockMvc mockMvc;

	// ── GET /inscripciones ────────────────────────────────────────────────────

	// Caso 1: Listar devuelve estado 200 OK
	@Test
	public void testListarRetornaOK() throws Exception {
		MvcResult result = mockMvc.perform(get("/inscripciones").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 2: Listar devuelve un arreglo JSON
	@Test
	public void testListarRetornaArregloJSON() throws Exception {
		MvcResult result = mockMvc.perform(get("/inscripciones").accept(APPLICATION_JSON))
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertTrue(body.startsWith("["));
	}

	// Caso 3: La inscripcion registrada aparece en la lista
	@Test
	public void testListarContieneInscripcionRegistrada() throws Exception {
		String json = "{\"id\":4001,\"estudianteId\":1001,\"cursoId\":2001,"
				+ "\"fecha\":\"2026-03-01\",\"calificacion\":0.0}";
		mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/inscripciones").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id==4001)]").isNotEmpty())
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("4001"));
	}

	// ── GET /inscripciones/{id} ───────────────────────────────────────────────

	// Caso 4: Consultar inscripcion existente devuelve 200
	@Test
	public void testConsultarInscripcionExistente() throws Exception {
		String json = "{\"id\":4010,\"estudianteId\":1010,\"cursoId\":2010,"
				+ "\"fecha\":\"2026-03-10\",\"calificacion\":8.5}";
		mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/inscripciones/4010").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 5: Consultar inscripcion inexistente devuelve 404
	@Test
	public void testConsultarInscripcionInexistente() throws Exception {
		MvcResult result = mockMvc.perform(get("/inscripciones/9999").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 6: Consultar devuelve los datos correctos de la inscripcion
	@Test
	public void testConsultarVerificaDatos() throws Exception {
		String json = "{\"id\":4020,\"estudianteId\":1020,\"cursoId\":2020,"
				+ "\"fecha\":\"2026-03-20\",\"calificacion\":9.0}";
		mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/inscripciones/4020").accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.estudianteId").value(1020))
				.andExpect(jsonPath("$.cursoId").value(2020))
				.andExpect(jsonPath("$.calificacion").value(9.0))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertTrue(body.contains("2026-03-20"));
		Assertions.assertTrue(body.contains("9.0"));
	}

	// ── POST /inscripciones ───────────────────────────────────────────────────

	// Caso 7: Registrar devuelve 200 OK
	@Test
	public void testRegistrarRetornaOK() throws Exception {
		String json = "{\"id\":4030,\"estudianteId\":1030,\"cursoId\":2030,"
				+ "\"fecha\":\"2026-04-01\",\"calificacion\":0.0}";
		MvcResult result = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 8: Registrar devuelve el cuerpo con los datos de la inscripcion
	@Test
	public void testRegistrarRetornaDatosInscripcion() throws Exception {
		String json = "{\"id\":4040,\"estudianteId\":1040,\"cursoId\":2040,"
				+ "\"fecha\":\"2026-04-10\",\"calificacion\":7.5}";
		MvcResult result = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(4040))
				.andExpect(jsonPath("$.estudianteId").value(1040))
				.andExpect(jsonPath("$.fecha").value("2026-04-10"))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertNotNull(body);
		Assertions.assertTrue(body.contains("2026-04-10"));
	}

	// Caso 9: La inscripcion queda persistida tras el registro
	@Test
	public void testRegistrarInscripcionPersistida() throws Exception {
		String json = "{\"id\":4050,\"estudianteId\":1050,\"cursoId\":2050,"
				+ "\"fecha\":\"2026-04-15\",\"calificacion\":6.0}";
		mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/inscripciones/4050").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(4050))
				.andReturn();
		Assertions.assertNotNull(result.getResponse().getContentAsString());
		Assertions.assertFalse(result.getResponse().getContentAsString().isEmpty());
	}
}

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
public class RestEstudianteTest {

	@Autowired
	MockMvc mockMvc;

	// ── GET /estudiantes ─────────────────────────────────────────────────────

	// Caso 1: Listar devuelve estado 200 OK
	@Test
	public void testListarRetornaOK() throws Exception {
		MvcResult result = mockMvc.perform(get("/estudiantes").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 2: Listar devuelve un arreglo JSON
	@Test
	public void testListarRetornaArregloJSON() throws Exception {
		MvcResult result = mockMvc.perform(get("/estudiantes").accept(APPLICATION_JSON))
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertTrue(body.startsWith("["));
	}

	// Caso 3: El estudiante registrado aparece en la lista
	@Test
	public void testListarContieneEstudianteRegistrado() throws Exception {
		String json = "{\"id\":1001,\"nombre\":\"Ana Gomez\",\"email\":\"ana@u.edu\","
				+ "\"carrera\":\"Sistemas\",\"semestre\":3}";
		mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/estudiantes").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id==1001)]").isNotEmpty())
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Ana Gomez"));
	}

	// ── GET /estudiantes/{id} ─────────────────────────────────────────────────

	// Caso 4: Consultar estudiante existente devuelve 200
	@Test
	public void testConsultarEstudianteExistente() throws Exception {
		String json = "{\"id\":1010,\"nombre\":\"Carlos Ruiz\",\"email\":\"carlos@u.edu\","
				+ "\"carrera\":\"Civil\",\"semestre\":5}";
		mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/estudiantes/1010").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 5: Consultar estudiante inexistente devuelve 404
	@Test
	public void testConsultarEstudianteInexistente() throws Exception {
		MvcResult result = mockMvc.perform(get("/estudiantes/9999").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 6: Consultar devuelve los datos correctos del estudiante
	@Test
	public void testConsultarVerificaDatos() throws Exception {
		String json = "{\"id\":1020,\"nombre\":\"Maria Lopez\",\"email\":\"maria@u.edu\","
				+ "\"carrera\":\"Medicina\",\"semestre\":2}";
		mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/estudiantes/1020").accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.nombre").value("Maria Lopez"))
				.andExpect(jsonPath("$.carrera").value("Medicina"))
				.andExpect(jsonPath("$.semestre").value(2))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertTrue(body.contains("Maria Lopez"));
		Assertions.assertTrue(body.contains("Medicina"));
	}

	// ── POST /estudiantes ─────────────────────────────────────────────────────

	// Caso 7: Registrar devuelve 200 OK
	@Test
	public void testRegistrarRetornaOK() throws Exception {
		String json = "{\"id\":1030,\"nombre\":\"Pedro Diaz\",\"email\":\"pedro@u.edu\","
				+ "\"carrera\":\"Derecho\",\"semestre\":4}";
		MvcResult result = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 8: Registrar devuelve el cuerpo con los datos del estudiante
	@Test
	public void testRegistrarRetornaDatosEstudiante() throws Exception {
		String json = "{\"id\":1040,\"nombre\":\"Laura Vega\",\"email\":\"laura@u.edu\","
				+ "\"carrera\":\"Biologia\",\"semestre\":6}";
		MvcResult result = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(1040))
				.andExpect(jsonPath("$.nombre").value("Laura Vega"))
				.andExpect(jsonPath("$.email").value("laura@u.edu"))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertNotNull(body);
		Assertions.assertTrue(body.contains("laura@u.edu"));
	}

	// Caso 9: El estudiante queda persistido tras el registro
	@Test
	public void testRegistrarEstudiantePersistido() throws Exception {
		String json = "{\"id\":1050,\"nombre\":\"Jose Mora\",\"email\":\"jose@u.edu\","
				+ "\"carrera\":\"Arquitectura\",\"semestre\":1}";
		mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/estudiantes/1050").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1050))
				.andReturn();
		Assertions.assertNotNull(result.getResponse().getContentAsString());
		Assertions.assertFalse(result.getResponse().getContentAsString().isEmpty());
	}

	// ── DELETE /estudiantes/{id} ──────────────────────────────────────────────

	// Caso 10: Eliminar estudiante existente devuelve 200 con sus datos
	@Test
	public void testEliminarEstudianteExistente() throws Exception {
		String json = "{\"id\":1060,\"nombre\":\"Sofia Torres\",\"email\":\"sofia@u.edu\","
				+ "\"carrera\":\"Quimica\",\"semestre\":3}";
		mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(delete("/estudiantes/1060").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1060))
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Sofia Torres"));
	}

	// Caso 11: Eliminar estudiante inexistente devuelve 404
	@Test
	public void testEliminarEstudianteInexistente() throws Exception {
		MvcResult result = mockMvc.perform(delete("/estudiantes/8888").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 12: Tras eliminar, el estudiante ya no es accesible
	@Test
	public void testEliminarEstudianteYaNoAccesible() throws Exception {
		String json = "{\"id\":1070,\"nombre\":\"Diego Pena\",\"email\":\"diego@u.edu\","
				+ "\"carrera\":\"Fisica\",\"semestre\":7}";
		mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON).content(json));
		mockMvc.perform(delete("/estudiantes/1070"));

		MvcResult result = mockMvc.perform(get("/estudiantes/1070").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}
}

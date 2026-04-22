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
public class RestProfesorTest {

	@Autowired
	MockMvc mockMvc;

	// ── GET /profesores ───────────────────────────────────────────────────────

	// Caso 1: Listar devuelve estado 200 OK
	@Test
	public void testListarRetornaOK() throws Exception {
		MvcResult result = mockMvc.perform(get("/profesores").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 2: Listar devuelve un arreglo JSON
	@Test
	public void testListarRetornaArregloJSON() throws Exception {
		MvcResult result = mockMvc.perform(get("/profesores").accept(APPLICATION_JSON))
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertTrue(body.startsWith("["));
	}

	// Caso 3: El profesor registrado aparece en la lista
	@Test
	public void testListarContieneProfesorRegistrado() throws Exception {
		String json = "{\"id\":3001,\"nombre\":\"Dr. Fernandez\",\"email\":\"fernandez@u.edu\","
				+ "\"departamento\":\"Informatica\",\"especialidad\":\"Redes\"}";
		mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/profesores").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id==3001)]").isNotEmpty())
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Dr. Fernandez"));
	}

	// ── GET /profesores/{id} ──────────────────────────────────────────────────

	// Caso 4: Consultar profesor existente devuelve 200
	@Test
	public void testConsultarProfesorExistente() throws Exception {
		String json = "{\"id\":3010,\"nombre\":\"Dra. Salinas\",\"email\":\"salinas@u.edu\","
				+ "\"departamento\":\"Matematicas\",\"especialidad\":\"Algebra\"}";
		mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/profesores/3010").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 5: Consultar profesor inexistente devuelve 404
	@Test
	public void testConsultarProfesorInexistente() throws Exception {
		MvcResult result = mockMvc.perform(get("/profesores/9999").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 6: Consultar devuelve los datos correctos del profesor
	@Test
	public void testConsultarVerificaDatos() throws Exception {
		String json = "{\"id\":3020,\"nombre\":\"Dr. Ramirez\",\"email\":\"ramirez@u.edu\","
				+ "\"departamento\":\"Fisica\",\"especialidad\":\"Termodinamica\"}";
		mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/profesores/3020").accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.nombre").value("Dr. Ramirez"))
				.andExpect(jsonPath("$.departamento").value("Fisica"))
				.andExpect(jsonPath("$.especialidad").value("Termodinamica"))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertTrue(body.contains("Dr. Ramirez"));
		Assertions.assertTrue(body.contains("Termodinamica"));
	}

	// ── POST /profesores ──────────────────────────────────────────────────────

	// Caso 7: Registrar devuelve 200 OK
	@Test
	public void testRegistrarRetornaOK() throws Exception {
		String json = "{\"id\":3030,\"nombre\":\"Dra. Castro\",\"email\":\"castro@u.edu\","
				+ "\"departamento\":\"Quimica\",\"especialidad\":\"Organica\"}";
		MvcResult result = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 8: Registrar devuelve el cuerpo con los datos del profesor
	@Test
	public void testRegistrarRetornaDatosProfesor() throws Exception {
		String json = "{\"id\":3040,\"nombre\":\"Dr. Arias\",\"email\":\"arias@u.edu\","
				+ "\"departamento\":\"Ingenieria\",\"especialidad\":\"Software\"}";
		MvcResult result = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(3040))
				.andExpect(jsonPath("$.nombre").value("Dr. Arias"))
				.andExpect(jsonPath("$.email").value("arias@u.edu"))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertNotNull(body);
		Assertions.assertTrue(body.contains("arias@u.edu"));
	}

	// Caso 9: El profesor queda persistido tras el registro
	@Test
	public void testRegistrarProfesorPersistido() throws Exception {
		String json = "{\"id\":3050,\"nombre\":\"Dra. Soto\",\"email\":\"soto@u.edu\","
				+ "\"departamento\":\"Biologia\",\"especialidad\":\"Genetica\"}";
		mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/profesores/3050").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(3050))
				.andReturn();
		Assertions.assertNotNull(result.getResponse().getContentAsString());
		Assertions.assertFalse(result.getResponse().getContentAsString().isEmpty());
	}
}

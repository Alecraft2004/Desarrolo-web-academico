package com.example.appweb;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
public class RestEstudianteTest {

	@Autowired
	MockMvc mockMvc;

	private int extractId(MvcResult result) throws Exception {
		return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
	}

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
		String json = "{\"nombre\":\"Ana Gomez\",\"email\":\"ana@u.edu\","
				+ "\"carrera\":\"Sistemas\",\"semestre\":3}";
		mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/estudiantes").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Ana Gomez"));
	}

	// ── GET /estudiantes/{id} ─────────────────────────────────────────────────

	// Caso 4: Consultar estudiante existente devuelve 200
	@Test
	public void testConsultarEstudianteExistente() throws Exception {
		String json = "{\"nombre\":\"Carlos Ruiz\",\"email\":\"carlos@u.edu\","
				+ "\"carrera\":\"Civil\",\"semestre\":5}";
		MvcResult post = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/estudiantes/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 5: Consultar estudiante inexistente devuelve 404
	@Test
	public void testConsultarEstudianteInexistente() throws Exception {
		MvcResult result = mockMvc.perform(get("/estudiantes/99999999").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 6: Consultar devuelve los datos correctos del estudiante
	@Test
	public void testConsultarVerificaDatos() throws Exception {
		String json = "{\"nombre\":\"Maria Lopez\",\"email\":\"maria@u.edu\","
				+ "\"carrera\":\"Medicina\",\"semestre\":2}";
		MvcResult post = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/estudiantes/" + id).accept(APPLICATION_JSON))
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
		String json = "{\"nombre\":\"Pedro Diaz\",\"email\":\"pedro@u.edu\","
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
		String json = "{\"nombre\":\"Laura Vega\",\"email\":\"laura@u.edu\","
				+ "\"carrera\":\"Biologia\",\"semestre\":6}";
		MvcResult result = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
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
		String json = "{\"nombre\":\"Jose Mora\",\"email\":\"jose@u.edu\","
				+ "\"carrera\":\"Arquitectura\",\"semestre\":1}";
		MvcResult post = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/estudiantes/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andReturn();
		Assertions.assertNotNull(result.getResponse().getContentAsString());
		Assertions.assertFalse(result.getResponse().getContentAsString().isEmpty());
	}

	// ── DELETE /estudiantes/{id} ──────────────────────────────────────────────

	// Caso 10: Eliminar estudiante existente devuelve 200 con sus datos
	@Test
	public void testEliminarEstudianteExistente() throws Exception {
		String json = "{\"nombre\":\"Sofia Torres\",\"email\":\"sofia@u.edu\","
				+ "\"carrera\":\"Quimica\",\"semestre\":3}";
		MvcResult post = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(delete("/estudiantes/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Sofia Torres"));
	}

	// Caso 11: Eliminar estudiante inexistente devuelve 404
	@Test
	public void testEliminarEstudianteInexistente() throws Exception {
		MvcResult result = mockMvc.perform(delete("/estudiantes/99999998").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 12: Tras eliminar, el estudiante ya no es accesible
	@Test
	public void testEliminarEstudianteYaNoAccesible() throws Exception {
		String json = "{\"nombre\":\"Diego Pena\",\"email\":\"diego@u.edu\","
				+ "\"carrera\":\"Fisica\",\"semestre\":7}";
		MvcResult post = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);
		mockMvc.perform(delete("/estudiantes/" + id));

		MvcResult result = mockMvc.perform(get("/estudiantes/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// ── PUT /estudiantes/{id} ─────────────────────────────────────────────────

	// Caso 13: Actualizar estudiante existente devuelve 200
	@Test
	public void testActualizarEstudianteExistente() throws Exception {
		String json = "{\"nombre\":\"Roberto Paz\",\"email\":\"roberto@u.edu\","
				+ "\"carrera\":\"Economia\",\"semestre\":2}";
		MvcResult post = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		String update = "{\"nombre\":\"Roberto Paz Jr\",\"email\":\"rpaz@u.edu\","
				+ "\"carrera\":\"Finanzas\",\"semestre\":3}";
		MvcResult putResult = mockMvc.perform(put("/estudiantes/" + id).contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, putResult.getResponse().getStatus());
	}

	// Caso 14: Actualizar devuelve los datos modificados del estudiante
	@Test
	public void testActualizarRetornaDatosActualizados() throws Exception {
		String json = "{\"nombre\":\"Elena Rios\",\"email\":\"elena@u.edu\","
				+ "\"carrera\":\"Psicologia\",\"semestre\":4}";
		MvcResult post = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		String update = "{\"nombre\":\"Elena Rios Mora\",\"email\":\"erios@u.edu\","
				+ "\"carrera\":\"Neurociencias\",\"semestre\":5}";
		MvcResult putResult = mockMvc.perform(put("/estudiantes/" + id).contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Elena Rios Mora"))
				.andExpect(jsonPath("$.carrera").value("Neurociencias"))
				.andReturn();
		Assertions.assertTrue(putResult.getResponse().getContentAsString().contains("Elena Rios Mora"));
	}

	// Caso 15: Actualizar estudiante inexistente devuelve 404
	@Test
	public void testActualizarEstudianteInexistente() throws Exception {
		String update = "{\"nombre\":\"No existe\",\"email\":\"nada@u.edu\","
				+ "\"carrera\":\"Nada\",\"semestre\":1}";
		MvcResult putResult = mockMvc.perform(put("/estudiantes/99999997").contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, putResult.getResponse().getStatus());
	}
}

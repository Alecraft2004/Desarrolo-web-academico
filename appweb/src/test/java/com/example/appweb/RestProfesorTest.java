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
public class RestProfesorTest {

	@Autowired
	MockMvc mockMvc;

	private int extractId(MvcResult result) throws Exception {
		return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
	}

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
		String json = "{\"nombre\":\"Dr. Fernandez\",\"email\":\"fernandez@u.edu\","
				+ "\"departamento\":\"Informatica\",\"especialidad\":\"Redes\"}";
		mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/profesores").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Dr. Fernandez"));
	}

	// ── GET /profesores/{id} ──────────────────────────────────────────────────

	// Caso 4: Consultar profesor existente devuelve 200
	@Test
	public void testConsultarProfesorExistente() throws Exception {
		String json = "{\"nombre\":\"Dra. Salinas\",\"email\":\"salinas@u.edu\","
				+ "\"departamento\":\"Matematicas\",\"especialidad\":\"Algebra\"}";
		MvcResult post = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/profesores/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 5: Consultar profesor inexistente devuelve 404
	@Test
	public void testConsultarProfesorInexistente() throws Exception {
		MvcResult result = mockMvc.perform(get("/profesores/99999999").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 6: Consultar devuelve los datos correctos del profesor
	@Test
	public void testConsultarVerificaDatos() throws Exception {
		String json = "{\"nombre\":\"Dr. Ramirez\",\"email\":\"ramirez@u.edu\","
				+ "\"departamento\":\"Fisica\",\"especialidad\":\"Termodinamica\"}";
		MvcResult post = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/profesores/" + id).accept(APPLICATION_JSON))
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
		String json = "{\"nombre\":\"Dra. Castro\",\"email\":\"castro@u.edu\","
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
		String json = "{\"nombre\":\"Dr. Arias\",\"email\":\"arias@u.edu\","
				+ "\"departamento\":\"Ingenieria\",\"especialidad\":\"Software\"}";
		MvcResult result = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
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
		String json = "{\"nombre\":\"Dra. Soto\",\"email\":\"soto@u.edu\","
				+ "\"departamento\":\"Biologia\",\"especialidad\":\"Genetica\"}";
		MvcResult post = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/profesores/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andReturn();
		Assertions.assertNotNull(result.getResponse().getContentAsString());
		Assertions.assertFalse(result.getResponse().getContentAsString().isEmpty());
	}

	// ── DELETE /profesores/{id} ───────────────────────────────────────────────

	// Caso 10: Eliminar profesor existente devuelve 200 con sus datos
	@Test
	public void testEliminarProfesorExistente() throws Exception {
		String json = "{\"nombre\":\"Dr. Vargas\",\"email\":\"vargas@u.edu\","
				+ "\"departamento\":\"Sistemas\",\"especialidad\":\"IA\"}";
		MvcResult post = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(delete("/profesores/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Dr. Vargas"));
	}

	// Caso 11: Eliminar profesor inexistente devuelve 404
	@Test
	public void testEliminarProfesorInexistente() throws Exception {
		MvcResult result = mockMvc.perform(delete("/profesores/99999998").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 12: Tras eliminar, el profesor ya no es accesible
	@Test
	public void testEliminarProfesorYaNoAccesible() throws Exception {
		String json = "{\"nombre\":\"Dra. Luna\",\"email\":\"luna@u.edu\","
				+ "\"departamento\":\"Arte\",\"especialidad\":\"Pintura\"}";
		MvcResult post = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);
		mockMvc.perform(delete("/profesores/" + id));

		MvcResult result = mockMvc.perform(get("/profesores/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// ── PUT /profesores/{id} ──────────────────────────────────────────────────

	// Caso 13: Actualizar profesor existente devuelve 200
	@Test
	public void testActualizarProfesorExistente() throws Exception {
		String json = "{\"nombre\":\"Dr. Herrera\",\"email\":\"herrera@u.edu\","
				+ "\"departamento\":\"Historia\",\"especialidad\":\"Medieval\"}";
		MvcResult post = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		String update = "{\"nombre\":\"Dr. Herrera P\",\"email\":\"herrerp@u.edu\","
				+ "\"departamento\":\"Historia\",\"especialidad\":\"Contemporanea\"}";
		MvcResult putResult = mockMvc.perform(put("/profesores/" + id).contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, putResult.getResponse().getStatus());
	}

	// Caso 14: Actualizar devuelve los datos modificados del profesor
	@Test
	public void testActualizarRetornaDatosActualizados() throws Exception {
		String json = "{\"nombre\":\"Dra. Campos\",\"email\":\"campos@u.edu\","
				+ "\"departamento\":\"Derecho\",\"especialidad\":\"Civil\"}";
		MvcResult post = mockMvc.perform(post("/profesores").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		String update = "{\"nombre\":\"Dra. Campos V\",\"email\":\"camposv@u.edu\","
				+ "\"departamento\":\"Derecho\",\"especialidad\":\"Penal\"}";
		MvcResult putResult = mockMvc.perform(put("/profesores/" + id).contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.especialidad").value("Penal"))
				.andReturn();
		Assertions.assertTrue(putResult.getResponse().getContentAsString().contains("Penal"));
	}

	// Caso 15: Actualizar profesor inexistente devuelve 404
	@Test
	public void testActualizarProfesorInexistente() throws Exception {
		String update = "{\"nombre\":\"No existe\",\"email\":\"n@u.edu\","
				+ "\"departamento\":\"Nada\",\"especialidad\":\"Nada\"}";
		MvcResult putResult = mockMvc.perform(put("/profesores/99999997").contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, putResult.getResponse().getStatus());
	}
}

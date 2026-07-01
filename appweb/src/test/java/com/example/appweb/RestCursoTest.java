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
public class RestCursoTest {

	@Autowired
	MockMvc mockMvc;

	private int extractId(MvcResult result) throws Exception {
		return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
	}

	// ── GET /cursos ───────────────────────────────────────────────────────────

	// Caso 1: Listar devuelve estado 200 OK
	@Test
	public void testListarRetornaOK() throws Exception {
		MvcResult result = mockMvc.perform(get("/cursos").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 2: Listar devuelve un arreglo JSON
	@Test
	public void testListarRetornaArregloJSON() throws Exception {
		MvcResult result = mockMvc.perform(get("/cursos").accept(APPLICATION_JSON))
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertTrue(body.startsWith("["));
	}

	// Caso 3: El curso registrado aparece en la lista
	@Test
	public void testListarContieneCursoRegistrado() throws Exception {
		String json = "{\"nombre\":\"Calculo I\",\"descripcion\":\"Limites y derivadas\","
				+ "\"creditos\":4,\"cupo\":30}";
		mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(get("/cursos").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Calculo I"));
	}

	// ── GET /cursos/{id} ──────────────────────────────────────────────────────

	// Caso 4: Consultar curso existente devuelve 200
	@Test
	public void testConsultarCursoExistente() throws Exception {
		String json = "{\"nombre\":\"Algebra Lineal\",\"descripcion\":\"Matrices y vectores\","
				+ "\"creditos\":3,\"cupo\":25}";
		MvcResult post = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/cursos/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 5: Consultar curso inexistente devuelve 404
	@Test
	public void testConsultarCursoInexistente() throws Exception {
		MvcResult result = mockMvc.perform(get("/cursos/99999999").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 6: Consultar devuelve los datos correctos del curso
	@Test
	public void testConsultarVerificaDatos() throws Exception {
		String json = "{\"nombre\":\"Programacion\",\"descripcion\":\"Java y POO\","
				+ "\"creditos\":5,\"cupo\":40}";
		MvcResult post = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/cursos/" + id).accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.nombre").value("Programacion"))
				.andExpect(jsonPath("$.creditos").value(5))
				.andExpect(jsonPath("$.cupo").value(40))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertTrue(body.contains("Programacion"));
		Assertions.assertTrue(body.contains("Java y POO"));
	}

	// ── POST /cursos ──────────────────────────────────────────────────────────

	// Caso 7: Registrar devuelve 200 OK
	@Test
	public void testRegistrarRetornaOK() throws Exception {
		String json = "{\"nombre\":\"Fisica I\",\"descripcion\":\"Mecanica clasica\","
				+ "\"creditos\":4,\"cupo\":35}";
		MvcResult result = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 8: Registrar devuelve el cuerpo con los datos del curso
	@Test
	public void testRegistrarRetornaDatosCurso() throws Exception {
		String json = "{\"nombre\":\"Quimica General\",\"descripcion\":\"Atomos y moleculas\","
				+ "\"creditos\":3,\"cupo\":20}";
		MvcResult result = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.nombre").value("Quimica General"))
				.andExpect(jsonPath("$.descripcion").value("Atomos y moleculas"))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertNotNull(body);
		Assertions.assertTrue(body.contains("Atomos y moleculas"));
	}

	// Caso 9: El curso queda persistido tras el registro
	@Test
	public void testRegistrarCursoPersistido() throws Exception {
		String json = "{\"nombre\":\"Historia Universal\",\"descripcion\":\"Historia del mundo\","
				+ "\"creditos\":2,\"cupo\":50}";
		MvcResult post = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/cursos/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andReturn();
		Assertions.assertNotNull(result.getResponse().getContentAsString());
		Assertions.assertFalse(result.getResponse().getContentAsString().isEmpty());
	}

	// ── DELETE /cursos/{id} ───────────────────────────────────────────────────

	// Caso 10: Eliminar curso existente devuelve 200 con sus datos
	@Test
	public void testEliminarCursoExistente() throws Exception {
		String json = "{\"nombre\":\"Estadistica\",\"descripcion\":\"Probabilidad\","
				+ "\"creditos\":3,\"cupo\":30}";
		MvcResult post = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(delete("/cursos/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("Estadistica"));
	}

	// Caso 11: Eliminar curso inexistente devuelve 404
	@Test
	public void testEliminarCursoInexistente() throws Exception {
		MvcResult result = mockMvc.perform(delete("/cursos/99999998").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 12: Tras eliminar, el curso ya no es accesible
	@Test
	public void testEliminarCursoYaNoAccesible() throws Exception {
		String json = "{\"nombre\":\"Economia\",\"descripcion\":\"Micro y macro\","
				+ "\"creditos\":3,\"cupo\":45}";
		MvcResult post = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);
		mockMvc.perform(delete("/cursos/" + id));

		MvcResult result = mockMvc.perform(get("/cursos/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// ── PUT /cursos/{id} ──────────────────────────────────────────────────────

	// Caso 13: Actualizar curso existente devuelve 200
	@Test
	public void testActualizarCursoExistente() throws Exception {
		String json = "{\"nombre\":\"Estadistica Basica\",\"descripcion\":\"Datos\","
				+ "\"creditos\":3,\"cupo\":20}";
		MvcResult post = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		String update = "{\"nombre\":\"Estadistica Avanzada\",\"descripcion\":\"Datos avanzados\","
				+ "\"creditos\":4,\"cupo\":25}";
		MvcResult putResult = mockMvc.perform(put("/cursos/" + id).contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, putResult.getResponse().getStatus());
	}

	// Caso 14: Actualizar devuelve los datos modificados del curso
	@Test
	public void testActualizarRetornaDatosActualizados() throws Exception {
		String json = "{\"nombre\":\"Bioquimica\",\"descripcion\":\"Original\","
				+ "\"creditos\":4,\"cupo\":30}";
		MvcResult post = mockMvc.perform(post("/cursos").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		String update = "{\"nombre\":\"Bioquimica Clinica\",\"descripcion\":\"Clinica avanzada\","
				+ "\"creditos\":5,\"cupo\":20}";
		MvcResult putResult = mockMvc.perform(put("/cursos/" + id).contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Bioquimica Clinica"))
				.andExpect(jsonPath("$.creditos").value(5))
				.andReturn();
		Assertions.assertTrue(putResult.getResponse().getContentAsString().contains("Bioquimica Clinica"));
	}

	// Caso 15: Actualizar curso inexistente devuelve 404
	@Test
	public void testActualizarCursoInexistente() throws Exception {
		String update = "{\"nombre\":\"No existe\",\"descripcion\":\"...\","
				+ "\"creditos\":1,\"cupo\":5}";
		MvcResult putResult = mockMvc.perform(put("/cursos/99999997").contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, putResult.getResponse().getStatus());
	}
}

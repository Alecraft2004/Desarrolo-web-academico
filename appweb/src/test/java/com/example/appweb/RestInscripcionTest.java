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
public class RestInscripcionTest {

	@Autowired
	MockMvc mockMvc;

	private int extractId(MvcResult result) throws Exception {
		return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
	}

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
		String json = "{\"estudianteId\":1,\"cursoId\":1,"
				+ "\"fecha\":\"2026-03-01\",\"calificacion\":0.0}";
		MvcResult post = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/inscripciones").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id==" + id + ")]").isNotEmpty())
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains(String.valueOf(id)));
	}

	// ── GET /inscripciones/{id} ───────────────────────────────────────────────

	// Caso 4: Consultar inscripcion existente devuelve 200
	@Test
	public void testConsultarInscripcionExistente() throws Exception {
		String json = "{\"estudianteId\":2,\"cursoId\":2,"
				+ "\"fecha\":\"2026-03-10\",\"calificacion\":8.5}";
		MvcResult post = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/inscripciones/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 5: Consultar inscripcion inexistente devuelve 404
	@Test
	public void testConsultarInscripcionInexistente() throws Exception {
		MvcResult result = mockMvc.perform(get("/inscripciones/99999999").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 6: Consultar devuelve los datos correctos de la inscripcion
	@Test
	public void testConsultarVerificaDatos() throws Exception {
		String json = "{\"estudianteId\":3,\"cursoId\":3,"
				+ "\"fecha\":\"2026-03-20\",\"calificacion\":9.0}";
		MvcResult post = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/inscripciones/" + id).accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.estudianteId").value(3))
				.andExpect(jsonPath("$.cursoId").value(3))
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
		String json = "{\"estudianteId\":4,\"cursoId\":4,"
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
		String json = "{\"estudianteId\":5,\"cursoId\":5,"
				+ "\"fecha\":\"2026-04-10\",\"calificacion\":7.5}";
		MvcResult result = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.estudianteId").value(5))
				.andExpect(jsonPath("$.fecha").value("2026-04-10"))
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Assertions.assertNotNull(body);
		Assertions.assertTrue(body.contains("2026-04-10"));
	}

	// Caso 9: La inscripcion queda persistida tras el registro
	@Test
	public void testRegistrarInscripcionPersistida() throws Exception {
		String json = "{\"estudianteId\":6,\"cursoId\":6,"
				+ "\"fecha\":\"2026-04-15\",\"calificacion\":6.0}";
		MvcResult post = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(get("/inscripciones/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andReturn();
		Assertions.assertNotNull(result.getResponse().getContentAsString());
		Assertions.assertFalse(result.getResponse().getContentAsString().isEmpty());
	}

	// ── DELETE /inscripciones/{id} ────────────────────────────────────────────

	// Caso 10: Eliminar inscripcion existente devuelve 200 con sus datos
	@Test
	public void testEliminarInscripcionExistente() throws Exception {
		MvcResult estPost = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content("{\"nombre\":\"Est Baja\",\"email\":\"baja@u.edu\",\"carrera\":\"Test\",\"semestre\":1}"))
				.andReturn();
		int estudianteId = extractId(estPost);

		String json = "{\"estudianteId\":" + estudianteId + ",\"cursoId\":1,"
				+ "\"fecha\":\"2026-05-01\",\"calificacion\":8.0}";
		MvcResult post = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		MvcResult result = mockMvc.perform(delete("/inscripciones/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 11: Eliminar inscripcion inexistente devuelve 404
	@Test
	public void testEliminarInscripcionInexistente() throws Exception {
		MvcResult result = mockMvc.perform(delete("/inscripciones/99999998").accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// Caso 12: Tras eliminar, la inscripcion ya no es accesible
	@Test
	public void testEliminarInscripcionYaNoAccesible() throws Exception {
		String json = "{\"estudianteId\":8,\"cursoId\":8,"
				+ "\"fecha\":\"2026-05-10\",\"calificacion\":5.0}";
		MvcResult post = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);
		mockMvc.perform(delete("/inscripciones/" + id));

		MvcResult result = mockMvc.perform(get("/inscripciones/" + id).accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, result.getResponse().getStatus());
	}

	// ── PUT /inscripciones/{id} ───────────────────────────────────────────────

	// Caso 13: Actualizar inscripcion existente devuelve 200
	@Test
	public void testActualizarInscripcionExistente() throws Exception {
		MvcResult estPost = mockMvc.perform(post("/estudiantes").contentType(APPLICATION_JSON)
				.content("{\"nombre\":\"Est Upd\",\"email\":\"upd@u.edu\",\"carrera\":\"Test\",\"semestre\":1}"))
				.andReturn();
		int estudianteId = extractId(estPost);

		String json = "{\"estudianteId\":" + estudianteId + ",\"cursoId\":1,"
				+ "\"fecha\":\"2026-05-15\",\"calificacion\":6.0}";
		MvcResult post = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		String update = "{\"estudianteId\":" + estudianteId + ",\"cursoId\":1,"
				+ "\"fecha\":\"2026-05-20\",\"calificacion\":9.5}";
		MvcResult putResult = mockMvc.perform(put("/inscripciones/" + id).contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, putResult.getResponse().getStatus());
	}

	// Caso 14: Actualizar devuelve los datos modificados de la inscripcion
	@Test
	public void testActualizarRetornaDatosActualizados() throws Exception {
		String json = "{\"estudianteId\":10,\"cursoId\":10,"
				+ "\"fecha\":\"2026-05-25\",\"calificacion\":7.0}";
		MvcResult post = mockMvc.perform(post("/inscripciones").contentType(APPLICATION_JSON)
				.content(json)).andReturn();
		int id = extractId(post);

		String update = "{\"estudianteId\":10,\"cursoId\":10,"
				+ "\"fecha\":\"2026-06-01\",\"calificacion\":10.0}";
		MvcResult putResult = mockMvc.perform(put("/inscripciones/" + id).contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.calificacion").value(10.0))
				.andExpect(jsonPath("$.fecha").value("2026-06-01"))
				.andReturn();
		Assertions.assertTrue(putResult.getResponse().getContentAsString().contains("10.0"));
	}

	// Caso 15: Actualizar inscripcion inexistente devuelve 404
	@Test
	public void testActualizarInscripcionInexistente() throws Exception {
		String update = "{\"estudianteId\":1,\"cursoId\":1,"
				+ "\"fecha\":\"2026-01-01\",\"calificacion\":0.0}";
		MvcResult putResult = mockMvc.perform(put("/inscripciones/99999997").contentType(APPLICATION_JSON)
				.content(update).accept(APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		Assertions.assertEquals(404, putResult.getResponse().getStatus());
	}
}

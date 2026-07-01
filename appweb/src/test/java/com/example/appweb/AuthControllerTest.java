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
public class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	// ── POST /auth/register ───────────────────────────────────────────────────

	// Caso 1: Registrar nuevo usuario devuelve 200 OK
	@Test
	public void testRegistrarNuevoUsuarioRetornaOK() throws Exception {
		String unique = "test" + System.currentTimeMillis();
		String json = "{\"username\":\"" + unique + "\",\"password\":\"pass123\",\"rol\":\"USER\"}";
		MvcResult result = mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 2: Registrar devuelve token JWT y datos del usuario
	@Test
	public void testRegistrarRetornaTokenYDatos() throws Exception {
		String unique = "adm" + System.currentTimeMillis();
		String json = "{\"username\":\"" + unique + "\",\"password\":\"pass123\",\"rol\":\"ADMIN\"}";
		MvcResult result = mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.username").value(unique))
				.andExpect(jsonPath("$.rol").value("ADMIN"))
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("token"));
	}

	// Caso 3: Registrar usuario duplicado devuelve 409 Conflict
	@Test
	public void testRegistrarUsuarioDuplicadoRetornaConflict() throws Exception {
		String unique = "dup" + System.currentTimeMillis();
		String json = "{\"username\":\"" + unique + "\",\"password\":\"pass123\",\"rol\":\"USER\"}";
		mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(json));

		MvcResult result = mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andReturn();
		Assertions.assertEquals(409, result.getResponse().getStatus());
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("error"));
	}

	// Caso 4: Registrar con rol invalido devuelve 400 Bad Request
	@Test
	public void testRegistrarRolInvalidoRetornaBadRequest() throws Exception {
		String unique = "bad" + System.currentTimeMillis();
		String json = "{\"username\":\"" + unique + "\",\"password\":\"pass\",\"rol\":\"SUPERADMIN\"}";
		MvcResult result = mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON)
				.content(json).accept(APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();
		Assertions.assertEquals(400, result.getResponse().getStatus());
	}

	// ── POST /auth/login ──────────────────────────────────────────────────────

	// Caso 5: Login con credenciales validas devuelve 200 OK
	@Test
	public void testLoginCredencialesValidasRetornaOK() throws Exception {
		String unique = "log" + System.currentTimeMillis();
		String regJson = "{\"username\":\"" + unique + "\",\"password\":\"miClave123\",\"rol\":\"USER\"}";
		mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(regJson));

		String loginJson = "{\"username\":\"" + unique + "\",\"password\":\"miClave123\"}";
		MvcResult result = mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON)
				.content(loginJson).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals(200, result.getResponse().getStatus());
	}

	// Caso 6: Login devuelve token JWT y datos del usuario
	@Test
	public void testLoginRetornaTokenYDatos() throws Exception {
		String unique = "tok" + System.currentTimeMillis();
		String regJson = "{\"username\":\"" + unique + "\",\"password\":\"tokenPass!\",\"rol\":\"ADMIN\"}";
		mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(regJson));

		String loginJson = "{\"username\":\"" + unique + "\",\"password\":\"tokenPass!\"}";
		MvcResult result = mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON)
				.content(loginJson).accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.username").value(unique))
				.andExpect(jsonPath("$.tipo").value("Bearer"))
				.andReturn();
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("token"));
	}

	// Caso 7: Login con contrasena incorrecta devuelve 401
	@Test
	public void testLoginContrasenaIncorrectaRetornaUnauthorized() throws Exception {
		String unique = "wrg" + System.currentTimeMillis();
		String regJson = "{\"username\":\"" + unique + "\",\"password\":\"correcta123\",\"rol\":\"USER\"}";
		mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(regJson));

		String loginJson = "{\"username\":\"" + unique + "\",\"password\":\"incorrecta999\"}";
		MvcResult result = mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON)
				.content(loginJson).accept(APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andReturn();
		Assertions.assertEquals(401, result.getResponse().getStatus());
	}

	// Caso 8: Login con usuario inexistente devuelve 401
	@Test
	public void testLoginUsuarioInexistenteRetornaUnauthorized() throws Exception {
		String loginJson = "{\"username\":\"noexiste_xyz_99999\",\"password\":\"cualquier\"}";
		MvcResult result = mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON)
				.content(loginJson).accept(APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andReturn();
		Assertions.assertEquals(401, result.getResponse().getStatus());
		Assertions.assertTrue(result.getResponse().getContentAsString().contains("error"));
	}
}

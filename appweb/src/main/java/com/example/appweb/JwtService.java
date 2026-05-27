package com.example.appweb;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long expiration;

	private SecretKey getKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generarToken(String username, String rol) {
		return Jwts.builder()
				.subject(username)
				.claim("rol", rol)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getKey())
				.compact();
	}

	public Claims extraerClaims(String token) {
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public String extraerUsername(String token) {
		return extraerClaims(token).getSubject();
	}

	public boolean esValido(String token) {
		try {
			return extraerClaims(token).getExpiration().after(new Date());
		} catch (Exception e) {
			return false;
		}
	}
}

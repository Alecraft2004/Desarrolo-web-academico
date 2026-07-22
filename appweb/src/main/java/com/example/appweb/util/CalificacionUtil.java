package com.example.appweb.util;

public final class CalificacionUtil {

	public static final double NOTA_MINIMA = 11.0;

	public static String estadoDe(double calificacion) {
		if (calificacion <= 0) return "EN_CURSO";
		if (calificacion >= NOTA_MINIMA) return "APROBADO";
		return "REPROBADO";
	}

	private CalificacionUtil() {}
}

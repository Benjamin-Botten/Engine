package engine.utility.math.linearalgebra;

import engine.utility.math.geometry.Vertex;

public class Transform {
	
	public static Vertex rotate2(double angle, double x, double y) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new Vertex((x * cos - y * sin), (x * sin + y * cos));
	}
	
	public static Vertex rotate2(double angle, Vertex v) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new Vertex((v.x * cos - v.y * sin), (v.x * sin + v.y * cos));
	}
	
//	public static Vertex rotateCenter2(double angle, Vertex v) {
//		double cos = Math.cos(angle);
//		double sin = Math.sin(angle);
//		return new Vertex((v.x * cos - v.y * sin), (v.x * sin + v.y * cos));
//	}
}

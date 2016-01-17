package engine.utility.math.linearalgebra;

import engine.utility.math.geometry.Vertex;

//3D vector with support for homogenous coordinates represented by w
public class Vector {
	public double x, y, z, w;
	
	public Vector() {
		x = y = z = w = 0;
	}
	
	/** Equivalent of v = B - A */
	public Vector(Vertex A, Vertex B) {
		x = B.x - A.x;
		y = B.y - A.y;
		z = B.z - A.z;
		w = 0;
	}
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		w = 0;
	}
	
	public Vector(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public void set(double x0, double x1, double y0, double y1) {
		set(x0, x1, y0, y1, 0, 0);
	}
	
	public void set(double x0, double x1, double y0, double y1, double z0, double z1) {
		set(x0, x1, y0, y1, z0, z1, 0, 0);
	}

	public void set(double x0, double x1, double y0, double y1, double z0, double z1, double w0, double w1) {
		x = x1 - x0;
		y = y1 - y0;
		z = z1 - z0;
		w = w1 - w0;
	}
	
	public Vector(Vector vec) {
		x = vec.x;
		y = vec.y;
		z = vec.y;
		w = vec.w;
	}
	
	public double dot(Vector vec) {
		return dot(this, vec);
	}

	public void perp() {
		double tmp = x;
		x = -y;
		y = tmp;
	}
	
	/** @return dot product / scalar product of the two given vectors. */
	public static double dot(Vector vec1, Vector vec2) {
		return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z;
	}
	
	/** @return given vectors perpendicular vector (CCW) . */
	public static Vector perp(Vector vec) {
		return new Vector(-vec.y, vec.x, 0, 0);
	}
	
	/** @return given vectors perpendicular vector (CW) . */
	public static Vector cperp(Vector vec) {
		return new Vector(vec.y, -vec.x, 0, 0);
	}
	
	//TODO: Implement Cross-product
	public static double cross(Vector vec1, Vector vec2) {
		return 0.0;
	}
	
	public void mul(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}
	
	public void add(Vector v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	public Vector inverse() {
		return new Vector(-x, -y, -z);
	}
	
	public void normalize() {
		double mag = mag(this);
		x /= mag;
		y /= mag;
		z /= mag;
	}
	
	public double mag() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double mag(Vector v) {
		return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
	}
	
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}
}

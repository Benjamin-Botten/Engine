package engine.utility.math.geometry;

import engine.utility.math.linearalgebra.Vector;

public class Vertex {
	public double x, y, z;
	
	public Vertex(Vertex v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public Vertex(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void add(Vector vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")"; // + ", " + z + ")";
	}
	
	public boolean equals(Object object) {
		if(object instanceof Vertex) {
			return (((Vertex) object).x == x && ((Vertex) object).y == y);
		}
		return false;
	}
}

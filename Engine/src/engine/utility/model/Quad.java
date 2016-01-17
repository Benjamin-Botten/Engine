package engine.utility.model;

import engine.utility.math.geometry.Line;
import engine.utility.math.geometry.Vertex;
import engine.utility.math.linearalgebra.Vector;

public class Quad extends Polygon {

	public static final int EDGES = 4;
	public static final int VERTICES = 4;
	
	public Quad() {
		super();
	}
	
	public Quad(Vertex[] vertices, Line[] edges) {
		super(vertices, edges);
		if(edges.length > EDGES || vertices.length > VERTICES) throw new IllegalArgumentException("Attempted to make different object than a quad in Quad!");
	}
	
	public Quad(Vertex[] vertices, Line[] edges, Vector[] normals) {
		super(vertices, edges, normals);
		if(edges.length > EDGES || vertices.length > VERTICES) throw new IllegalArgumentException("Attempted to make different object than a quad in Quad!");
	}
}

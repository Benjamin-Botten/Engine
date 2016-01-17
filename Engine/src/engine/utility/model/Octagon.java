package engine.utility.model;

import engine.utility.math.geometry.Line;
import engine.utility.math.geometry.Vertex;
import engine.utility.math.linearalgebra.Vector;

public class Octagon extends Polygon {
	public static final int EDGES = 8;
	public static final int VERTICES = 8;
	
	public Octagon() {
		super();
	}
	
	public Octagon(Vertex[] vertices, Line[] edges) {
		super(vertices, edges);
		if(edges.length > EDGES || vertices.length > VERTICES) throw new IllegalArgumentException("Too many vertices / edges for an octagon!");
	}
	
	public Octagon(Vertex[] vertices, Line[] edges, Vector[] normals) {
		super(vertices, edges, normals);
		if(edges.length > EDGES || vertices.length > VERTICES) throw new IllegalArgumentException("Too many vertices / edges for an octagon!");
	}
}

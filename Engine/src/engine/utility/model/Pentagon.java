package engine.utility.model;

import engine.utility.math.geometry.Line;
import engine.utility.math.geometry.Vertex;
import engine.utility.math.linearalgebra.Vector;

public class Pentagon extends Polygon {
	
	public static final int EDGES = 5;
	public static final int VERTICES = 5;
	
	public Pentagon() {
		super();
	}
	
	public Pentagon(Vertex[] vertices, Line[] edges) {
		super(vertices, edges);
		if(edges.length > EDGES || vertices.length > VERTICES) throw new IllegalArgumentException("Too many vertices / edges for a pentagon!");
	}
	
	public Pentagon(Vertex[] vertices, Line[] edges, Vector[] normals) {
		super(vertices, edges, normals);
		if(edges.length > EDGES || vertices.length > VERTICES) throw new IllegalArgumentException("Too many vertices / edges for a pentagon!");
	}
}

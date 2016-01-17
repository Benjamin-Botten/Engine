package engine.utility.model;

import java.util.List;

import engine.utility.math.EngMath;
import engine.utility.math.geometry.Line;
import engine.utility.math.geometry.Vertex;
import engine.utility.math.linearalgebra.Transform;
import engine.utility.math.linearalgebra.Vector;

/** NOTE: _ALL_ Polygons have to be defined in CCW (Counter-clockwise) order, because of how the vector maths are set up! 
 *  ^ May not be true anymore as of 03.01.2016! */
public abstract class Polygon {
	public Line[] edges; //Hmmph
	public Vertex[] vertices;
	public Vector[] normals;
	protected int colorFill;
	protected int colorFrame;
	
	public double scale = 1;
	
	protected Polygon() {
		edges = null;
		vertices = null;
	}
	
	protected Polygon(Vertex[] vertices, Line[] edges) {
		assert(edges.length > 0 && vertices.length > 0);
		this.edges = edges;
		this.vertices = vertices;
		this.normals = EngMath.createNormals(vertices);
	}
	
	protected Polygon(Vertex[] vertices, Line[] edges, Vector[] normals) {
		assert(edges.length > 0 && vertices.length > 0 && normals.length > 0);
		this.edges = edges;
		this.vertices = vertices;
		this.normals = normals;
	}
	
	public static final double ONE_SIXTH = 1/6D;
	public double getCenterX() {
		double sum = 0;
		int len = vertices.length;
		for(int i = 0; i < len - 1; ++i) {
			sum += (vertices[i].x + vertices[i + 1].x) * (vertices[i].x * vertices[i + 1].y - vertices[i + 1].x * vertices[i].y);
			
		}
		
		sum += (vertices[len - 1].x + vertices[0].x) * (vertices[len - 1].x * vertices[0].y - vertices[0].x * vertices[len - 1].y);
		
		return (1D / (getSignedArea() * 6D)) * sum;
	}
	
	public double getCenterY() {
		double sum = 0;
		int len = vertices.length;
		for(int i = 0; i < len - 1; ++i) {
			sum += (vertices[i].y + vertices[i + 1].y) * (vertices[i].x * vertices[i + 1].y - vertices[i + 1].x * vertices[i].y);
			System.out.println("Getting center: current edge = " + i);
		}
		
		sum += (vertices[len - 1].y + vertices[0].y) * (vertices[len - 1].x * vertices[0].y - vertices[0].x * vertices[len - 1].y);
		 
		return (1D / (getSignedArea() * 6D)) * sum;
	}
	
	public double getSignedArea() {
		double sum = 0;
		int len = vertices.length;
		for(int i = 0; i < len - 1; ++i) {
			sum += vertices[i].x * vertices[i + 1].y - vertices[i + 1].x * vertices[i].y;
		}
		
		sum += vertices[len - 1].x * vertices[0].y - vertices[0].x + vertices[len - 1].y;
		
		sum *= 0.5;
		
		return sum;
	}
	
	public void setScale(double scale) {
		this.scale = scale;
		double minX = EngMath.minXInLines(edges);
		double minY = EngMath.minYInLines(edges);
		System.out.println("Scaling polygon, min(" + minX + ", " + minY + ")");
		for(int i = 0; i < edges.length; ++i) {
			
			edges[i].v0.x = ((edges[i].v0.x - minX) * scale) + minX;
			edges[i].v0.y = ((edges[i].v0.y - minY) * scale) + minY;
			
			edges[i].v1.x = ((edges[i].v1.x - minX) * scale) + minX;
			edges[i].v1.y = ((edges[i].v1.y - minY) * scale) + minY;
			
			vertices[i].x = ((vertices[i].x - minX) * scale) + minX;
			vertices[i].y = ((vertices[i].y - minY) * scale) + minY;
		}
	}
	
	public void rotate(double angle) {
		
		double centerX = getCenterX();
		double centerY = getCenterY();
		System.out.println("Center of polygon: " + centerX + ", " + centerY);

		for(int i = 0; i < vertices.length; ++i) {
			edges[i].v0.x -= centerX;
			edges[i].v0.y -= centerY;
			edges[i].v1.x -= centerX;
			edges[i].v1.y -= centerY;
			vertices[i].x -= centerX;
			vertices[i].y -= centerY;
			
			edges[i].v0 = Transform.rotate2(angle, edges[i].v0);
			edges[i].v1 = Transform.rotate2(angle, edges[i].v1);
			vertices[i] = Transform.rotate2(angle, vertices[i]);
			
			edges[i].v0.x += centerX;
			edges[i].v0.y += centerY;
			edges[i].v1.x += centerX;
			edges[i].v1.y += centerY;
			vertices[i].x += centerX;
			vertices[i].y += centerY;
		}
		System.out.println(vertices[0] + ", " + vertices[1] + ", " + vertices[2]);
	}
}

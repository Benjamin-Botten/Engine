package engine.utility.math.geometry;

public class Line {
	public Vertex v0 = new Vertex(0, 0, 0), v1 = new Vertex(0, 0, 0);
	
	public Line() {
	}
	
	public Line(Line l) {
		v0.x = l.v0.x;
		v0.y = l.v0.y;
		v1.x = l.v1.x;
		v1.y = l.v1.y;
	}
	
	public Line(Vertex v0, Vertex v1) {
		this.v0.x = v0.x;
		this.v0.y = v0.y;
		this.v1.x = v1.x;
		this.v1.y = v1.y;
	}
	
	public void set(Vertex v0, Vertex v1) {
		this.v0.x = v0.x;
		this.v0.y = v0.y;
		this.v1.x = v1.x;
		this.v1.y = v1.y;
	}
	
	public String toString() {
		return "[A" + v0 + ", B" + v1 + "]";
	}
}

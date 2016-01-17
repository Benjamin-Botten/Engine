package engine.utility.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import engine.system.gfx.Color;
import engine.utility.math.geometry.Line;
import engine.utility.math.geometry.Vertex;
import engine.utility.math.linearalgebra.Vector;

public class EngMath {
	
	/** Constants */
	public static final int PAIR = 0;
	public static final int ODD = 1;
	
	public static Vertex getRayIntersectionPoint(Line seg1, Line seg2) {
		Vertex A = new Vertex(seg1.v0);
		Vector b = new Vector(A, new Vertex(seg1.v1));

		double t = getIntersectionTime(seg1, seg2);
		if (t < 0)
			return null;
		
		b.mul(t);
		
		if(t >= 0)
			A.add(b);

		return A;
	}
	
	public static Vertex getIntersectionPoint(Line seg1, Line seg2) {
		Vertex A = new Vertex(seg1.v0);
		Vector b = new Vector(A, new Vertex(seg1.v1));

		double t = getIntersectionTime(seg1, seg2);
		if (t < 0 || t > 1)
			return null;
		
		b.mul(t);
		
		if(t >= 0 && t <= 1)
			A.add(b);

		return A;
	}


	/**
	 * If the return value is not within the interval [0, 1], there is no
	 * collision on the line-segs!
	 */
	public static double getIntersectionTime(Line seg1, Line seg2) {
		double 	Ax = seg1.v0.x, Ay = seg1.v0.y, Bx = seg1.v1.x, By = seg1.v1.y, 
				Cx = seg2.v0.x, Cy = seg2.v0.y, Dx = seg2.v1.x, Dy = seg2.v1.y;
		Vertex A = new Vertex(Ax, Ay);
		Vertex B = new Vertex(Bx, By);
		Vertex C = new Vertex(Cx, Cy);
		Vertex D = new Vertex(Dx, Dy);
		Vector b, c, d;

		b = new Vector(A, B);
		c = new Vector(A, C);
		d = new Vector(C, D);

		d.perp();

		return (d.dot(c) / d.dot(b));
	}
	
	public static double getRayIntersectionTime(Line seg1, Line seg2) {
		double 	Ax = seg1.v0.x, Ay = seg1.v0.y, Bx = seg1.v1.x, By = seg1.v1.y, 
				Cx = seg2.v0.x, Cy = seg2.v0.y, Dx = seg2.v1.x, Dy = seg2.v1.y;
		Vertex A = new Vertex(Ax, Ay);
		Vertex B = new Vertex(Bx, By);
		Vertex C = new Vertex(Cx, Cy);
		Vertex D = new Vertex(Dx, Dy);
		Vector b, c, d;

		b = new Vector(A, B);
		c = new Vector(A, C);
		d = new Vector(C, D);

		d.perp();

		return (d.dot(c) / d.dot(b));
	}
	
	private static void pointerChange(Line seg1) {
//		double Ax, Ay;
//		Ax = seg1.v0.x;
//		Ay = seg1.v1.y;
		Vertex A = new Vertex(seg1.v0);
		A.x += 100;
	}

	/** TODO: This is bad! Fix! */
	public static boolean isOverlappingScanline(Line seg1, Line seg2) {
		Vertex A = seg1.v0, B = seg1.v1, C = seg2.v0, D = seg2.v1;
		Vector b, c, d;

		b = new Vector(A, B);
		c = new Vector(A, C);
		d = new Vector(C, D);

		d.perp();
		double num = d.dot(c);
		double den = d.dot(b);
		if(num == 0 && den == 0) return true;
		return false;
	}
	
	/** Returns 0 if there is no overlapping */
	public static double getOverlapWidth(Line seg1, Line seg2) {
		if(!isOverlappingScanline(seg1, seg2)) return 0;
		double minimum = min(seg1.v0.x, seg1.v1.x);
		double maximum = max(seg2.v0.x, seg2.v1.x);
		return (maximum - (maximum - minimum));
	}
	
	public static double abs(double a) {
		return a < 0 ? -a : a;
	}
	
	public static double max(double a, double b) {
		return a > b ? a : b;
	}
	
	public static double min(double a, double b) {
		return a < b ? a : b;
	}

	public static double maxX(Vertex[] vertices) {
		double cur = vertices[0].x;
		for (int i = 0; i < vertices.length; ++i) {
			for (int j = i; j < vertices.length; ++j) {
				if (cur < vertices[j].x)
					cur = vertices[j].x;
			}
		}
		return cur;
	}

	public static double maxY(Vertex[] vertices) {
		double cur = vertices[0].y;
		for (int i = 0; i < vertices.length; ++i) {
			for (int j = i; j < vertices.length; ++j) {
				if (cur < vertices[j].y)
					cur = vertices[j].y;
			}
		}
		return cur;
	}

	public static double minX(Vertex[] vertices) {
		double cur = vertices[0].x;
		for (int i = 0; i < vertices.length; ++i) {
			for (int j = i; j < vertices.length; ++j) {
				if (cur > vertices[j].x)
					cur = vertices[j].x;
			}
		}
		return cur;
	}

	public static double minY(Vertex[] vertices) {
		double cur = vertices[0].y;
		for (int i = 0; i < vertices.length; ++i) {
			for (int j = i; j < vertices.length; ++j) {
				if (cur > vertices[j].y)
					cur = vertices[j].y;
			}
		}
		return cur;
	}
	
	public static double minXInLine(Line edge) {
		return minX(new Vertex[] {edge.v0, edge.v1});
	}
	
	public static double maxXInLine(Line edge) {
		return maxX(new Vertex[] {edge.v0, edge.v1});
	}
	
	public static double minYInLine(Line edge) {
		return minY(new Vertex[] {edge.v0, edge.v1});
	}
	
	public static double maxYInLine(Line edge) {
		return maxY(new Vertex[] {edge.v0, edge.v1});
	}
	
	public static double minXInLines(Line[] edges) {
		Vertex[] vertices = new Vertex[edges.length * 2];
		int c = 0;
		for(int i = 0; i < edges.length; ++i) {
			vertices[c] = edges[i].v0;
			vertices[c + 1] = edges[i].v1;
			c += 2;
		}
		return minX(vertices);
	}
	
	public static double maxXInLines(Line[] edges) {
		Vertex[] vertices = new Vertex[edges.length * 2];
		int c = 0;
		for(int i = 0; i < edges.length; ++i) {
			vertices[c] = edges[i].v0;
			vertices[c + 1] = edges[i].v1;
			c += 2;
		}
		return maxX(vertices);
	}
	
	public static double minYInLines(Line[] edges) {
		Vertex[] vertices = new Vertex[edges.length * 2];
		int c = 0;
		for(int i = 0; i < edges.length; ++i) {
			vertices[c] = edges[i].v0;
			vertices[c + 1] = edges[i].v1;
			c += 2;
		}
		return minY(vertices);
	}
	
	public static double maxYInLines(Line[] edges) {
		Vertex[] vertices = new Vertex[edges.length * 2];
		int c = 0;
		for(int i = 0; i < edges.length; ++i) {
			vertices[c] = edges[i].v0;
			vertices[c + 1] = edges[i].v1;
			c += 2;
		}
		return maxY(vertices);
	}
	
	/** One-dimensional bounds-check */
	public static boolean isBounded(double a, double min, double max) {
		return (a >= min && a <= max);
	}
	
	public static boolean isBounded(double x, double y, double minX, double minY, double maxX, double maxY) {
		return isBounded(x, minX, maxX) && isBounded(y, minY, maxY);
	}
	
	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}
	
	public static Vertex lerp(Vertex a, Vertex b, double t) {
		return new Vertex((a.x + (b.x - a.x) * t), (a.y + (b.y - a.y) * t));
	}
	
	public static Color lerp(Color a, Color b, double t) {
		return new Color((a.a + (b.a - a.a) * t), (a.r + (b.r - a.r) * t), (a.g + (b.g - a.g) * t), (a.b + (b.b - a.b) * t));
	}
	
	public static double getDistance2D(Vertex a, Vertex b) {
		double x = b.x - a.x;
		double y = b.y - a.y;
		return Math.sqrt(x * x + y * y);
	}
	
	public static double getDistance(Vertex a, Vertex b) {
		double x = b.x - a.x;
		double y = b.y - a.y;
		double z = b.z - a.z;
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	/** Returns the parity of an integer.
	 * 0: Pair
	 * 1: Odd
	 */
	public static int parity(int a) {
		return (a % 2) == PAIR ? PAIR : ODD;
	}
	
	public static final int FACULTY_3 = 3 * 2;
	public static final int FACULTY_4 = 4 * 3 * 2;
	public static final int FACULTY_5 = 5 * 4 * 3 * 2;
	public static final int FACULTY_6 = 6 * 5 * 4 * 3 * 2;
	public static final int FACULTY_7 = 7 * 6 * 5 * 4 * 3 * 2;
	public static final int FACULTY_8 = 8 * 7 * 6 * 5 * 4 * 3 * 2;
	public static final int FACULTY_9 = 9 * 8 * 7 * 6 * 5 * 4 * 3 * 2;
	public static final int FACULTY_10 = 10 * 9 * 8 * 7 * 6 * 5 * 4 * 3 * 2;
	public static final int FACULTY_11 = 11 * 10 * 9 * 8 * 7 * 6 * 5 * 4 * 3 * 2;
	
	public static final double PI = 3.141592653589793;
	public static final double PI_HALF = PI / 2D;
	
	/** Calculates cos as 5 terms of the taylor-series expansion at x = 0 */
	public static double cos(double a) {
		double square = (a * a);
		double quad = square * square;
		return (1D 
				- (square / 2D) 
				+ ((quad) / FACULTY_4)
				- ((quad * square) / FACULTY_6)
				+ ((quad * quad) / FACULTY_8)
				- ((quad * quad * square) / FACULTY_10));
	}
	
	public static double sin(double a) {
		
		double square = (a * a);
		double quad = square * square;
		return (a 
				- ((square * a) / FACULTY_3) 
				+ ((quad * a) / FACULTY_5)
				- ((quad * square * a) / FACULTY_7)
				+ ((quad * quad * a) / FACULTY_9)
				- ((quad * quad * square * a) / FACULTY_11));
	}
	
	/** Calculates the inverse sin as 3-terms of the taylor-series expansion at x = 0 */
	public static double asin(double a) {
		//x + x^3 / 3 * 2 + 3x^5 / 2*4*5
		double cubed = a * a * a;
		return 	a 
				+ ((cubed) / 6D) 
				+ ((3D * (cubed * a * a)) / (40D)) 
				+ ((15D * (cubed * cubed * a)) / (336D));
	}
	
	/** The inverse of cos(a) is simply pi/2 - asin(a) */
	public static double acos(double a) {
		return PI_HALF - asin(a);
	}
	
	public static Vector[] createNormals(Vertex[] vertices) {
		Vector[] result = new Vector[vertices.length];
		for(int i = 0; i < vertices.length; ++i) {
			result[i] = Vector.perp(new Vector(vertices[(i + 1) % vertices.length], vertices[i]));
		}
		return result;
	}
	
	/** Returns the point on a parametric line given t 
	 *  L(t) = A + bt, where b = [B - A]*/
	public Vertex getPointOnLine(Vertex A, Vector b, double t) {
		return new Vertex(A.x + b.x * t, A.y + b.y * t, A.z + b.z * t);
	}
	
	public static Vertex round(Vertex v) {
		if(v == null) return null;
		return new Vertex(Math.round(v.x), Math.round(v.y), Math.round(v.z));
	}
	
	public static double round(double a) {
		return Math.round(a);
	}
	
	public static double ceil(double a) {
		return Math.ceil(a);
	}
	
	public static double floor(double a) {
		return Math.floor(a);
	}
	
	public static double round(double a, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(a);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static double getDiffuseValue(Vector surfaceNormal, Vector lightVector) {
		double magnitudeNormal = surfaceNormal.mag();
		double magnitudeLight = lightVector.mag();
		return EngMath.max(0D, Vector.dot(surfaceNormal, lightVector) / (magnitudeNormal * magnitudeLight));
	}
}

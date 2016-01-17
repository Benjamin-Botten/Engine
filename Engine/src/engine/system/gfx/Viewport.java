package engine.system.gfx;

import engine.system.gfx.light.Light;
import engine.utility.IO.Input;
import engine.utility.math.EngMath;
import engine.utility.math.geometry.Line;
import engine.utility.math.geometry.Vertex;
import engine.utility.math.linearalgebra.Vector;
import engine.utility.model.Polygon;
import engine.utility.model.Quad;
import engine.utility.model.Triangle;

public class Viewport {
	private static final byte STENCIL_SHADOW_BIT = 0;
	private static final byte STENCIL_LIGHT_BIT = 1;
	private static final int SHADING_ON = 0x01;
	private static final int SHADING_MODE_GOURAUD = 0x02;
	private static final int SHADING_MODE_FLAT = 0x03;
	private static final int FLAG_OFF = 0x00;
	private Raster raster;
	private int pixels[];
	private double depth[];
	private int w, h;
	private byte sharedShadows[];
	private byte[][] shadow;
	private boolean isProjectingShadow, isRenderingLight;
	private int shadingMode;

	public static final int MAX_LIGHTS = 32;
	public static final int SHADOW_COLOR = 0xff777777;

	public final Vertex CORNER_BOTTOM_LEFT, CORNER_TOP_LEFT, CORNER_TOP_RIGHT, CORNER_BOTTOM_RIGHT;
	public final int INDEX_BOTTOM = 0, INDEX_LEFT = 1, INDEX_TOP = 2, INDEX_RIGHT = 3;
	public final int INDEX_BOTTOM_LEFT = 4, INDEX_TOP_LEFT = 5, INDEX_TOP_RIGHT = 6, INDEX_BOTTOM_RIGHT = 7;
	public final Line BOTTOM, LEFT, TOP, RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT; // Used
																								// for
																								// ray-intersections
	// with screen-border
	private final Line[] LINE_BOUNDS;
	private final Vector[] BOUND_NORMALS;

	public Viewport(int w, int h, int scale, String title) {
		raster = new Raster(w, h, scale, title);
		this.pixels = raster.getPixels();
		this.w = w;
		this.h = h;

		shadow = new byte[MAX_LIGHTS][w * h];
		for (int i = 0; i < MAX_LIGHTS; ++i) {
			for (int y = 0; y < h; ++y) {
				for (int x = 0; x < w; ++x) {
					shadow[i][x + y * w] = STENCIL_LIGHT_BIT;
				}
			}
		}

		sharedShadows = new byte[w * h];
		for (int i = 0; i < sharedShadows.length; ++i) {
			sharedShadows[i] = STENCIL_LIGHT_BIT;
		}

		/** CCW order */
		BOTTOM = new Line(new Vertex(0, h), new Vertex(w, h));
		LEFT = new Line(new Vertex(0, h), new Vertex(0, 0));
		TOP = new Line(new Vertex(w, 0), new Vertex(0, 0));
		RIGHT = new Line(new Vertex(w, h), new Vertex(w, 0));

		BOTTOM_RIGHT = new Line(new Vertex(0, h), new Vertex(w, 0));
		TOP_RIGHT = new Line(new Vertex(0, 0), new Vertex(w, h));
		TOP_LEFT = new Line(new Vertex(w, h), new Vertex(0, 0));
		BOTTOM_LEFT = new Line(new Vertex(w, 0), new Vertex(0, h));

		CORNER_BOTTOM_LEFT = new Vertex(0, h);
		CORNER_TOP_LEFT = new Vertex(0, 0);
		CORNER_TOP_RIGHT = new Vertex(w, 0);
		CORNER_BOTTOM_RIGHT = new Vertex(w, h);

		LINE_BOUNDS = new Line[] { BOTTOM, LEFT, TOP, RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT };
		BOUND_NORMALS = new Vector[] { new Vector(0, 1, 0), new Vector(-1, 0, 0), new Vector(0, -1, 0),
				new Vector(1, 0, 0), new Vector(-0.707, 0.707, 0), new Vector(-0.707, -0.707, 0),
				new Vector(0.707, -0.707, 0), new Vector(0.707, 0.707, 0) };

		raster.configureFrame(false, true);
	}

	public void setSize(int w, int h) {
		this.w = w;
		this.h = h;
	}
	
	public void setShadingMode(int mode) {
		shadingMode = mode;
	}
	
	public int width() {
		return w;
	}

	public int height() {
		return h;
	}

	public int[] pixels() {
		return pixels;
	}

	public void renderRectFilled(int startX, int startY, int endX, int endY, int color) {
		for (int y = startY; y < endY; ++y) {
			for (int x = startX; x < endX; ++x) {
				pixels[x + y * w] = color;
			}
		}
	}

	public void renderRect(int startX, int startY, int endX, int endY, int color) {
		for (int y = startY; y < endY; ++y) {
			for (int x = startX; x < endX; ++x) {
				if ((x == startX || x == endX - 1) || (y == startY || y == endY - 1))
					pixels[x + y * w] = color;
			}
		}
	}

	public void renderRectOutlined(int startX, int startY, int endX, int endY, int color, int outlineColor) {
		for (int y = startY; y < endY; ++y) {
			for (int x = startX; x < endX; ++x) {
				if ((x == startX || x == endX - 1) || (y == startY || y == endY - 1))
					pixels[x + y * w] = outlineColor;
				else
					pixels[x + y * w] = color;
			}
		}
	}

	public void renderWireTriangle(Triangle triangle) {

		int startX = (int) EngMath.minX(triangle.vertices);
		int startY = (int) EngMath.minY(triangle.vertices);
		int endX = (int) EngMath.maxX(triangle.vertices);
		int endY = (int) EngMath.maxY(triangle.vertices);

		// System.out.println("Polygon width: " + (endX - startX) + ", height: "
		// + (endY - startY));

		Vertex scanLeft, scanRight;
		Line scanline;
		for (int y = startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);

			for (int i = 0; i < triangle.edges.length; ++i) {
				if (EngMath.isOverlappingScanline(scanline, triangle.edges[i]) && (y == 0 || y == endY)) {
					int minX = (int) EngMath.minXInLines(triangle.edges);
					int maxX = (int) EngMath.maxXInLines(triangle.edges);
					for (int x = minX; x <= maxX; ++x) {
						pixels[x + y * w] = 0xff000000;
					}
					break;
				}

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, triangle.edges[i]);
				if (curIntersection != null) {
					pixels[(int) (curIntersection.x + curIntersection.y * w)] = 0xff000000;
				}
			}
		}
	}

	public void renderWireQuad(Quad quad) {

		int startX = (int) EngMath.minX(quad.vertices);
		int startY = (int) EngMath.minY(quad.vertices);
		int endX = (int) EngMath.maxX(quad.vertices);
		int endY = (int) EngMath.maxY(quad.vertices);

		// System.out.println("Polygon width: " + (endX - startX) + ", height: "
		// + (endY - startY));

		Vertex scanLeft, scanRight;
		Line scanline;
		for (int y = startY; y <= endY; ++y) {
			scanLeft = new Vertex(0D, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);

			for (int i = 0; i < quad.edges.length; ++i) {
				if (EngMath.isOverlappingScanline(scanline, quad.edges[i])) {
					int minX = (int) EngMath.minXInLines(quad.edges);
					int maxX = (int) EngMath.maxXInLines(quad.edges);
					for (int x = minX; x <= maxX; ++x) {
						pixels[x + y * w] = 0xff000000;
					}
					break;
				}

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, quad.edges[i]);
				if (curIntersection != null) {
					pixels[(int) (curIntersection.x + curIntersection.y * w)] = 0xff000000;
				}
			}
		}
	}

	public void renderWirePolygon(Polygon polygon) {

		int startX = (int) EngMath.minX(polygon.vertices);
		int startY = (int) EngMath.minY(polygon.vertices);
		int endX = (int) EngMath.maxX(polygon.vertices);
		int endY = (int) EngMath.maxY(polygon.vertices);

		System.out.println("Polygon width: " + (endX - startX) + ", height: " + (endY - startY));

		Vertex scanLeft, scanRight;
		Line scanline;
		for (int y = startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);

			for (int i = 0; i < polygon.edges.length; ++i) {
				if (EngMath.isOverlappingScanline(scanline, polygon.edges[i]) && (y == startY || y == endY)) {
					int minX = (int) EngMath.min(polygon.edges[i].v0.x, polygon.edges[i].v1.x);
					int maxX = (int) EngMath.max(polygon.edges[i].v0.x, polygon.edges[i].v1.x);
					for (int x = minX; x <= maxX; ++x) {
						pixels[x + y * w] = 0xff000000;
					}
					break;
				}

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, polygon.edges[i]);
				if (curIntersection != null) {
					pixels[(int) (curIntersection.x + curIntersection.y * w)] = 0xff000000;
				}
			}
		}
	}

	

	public void renderWirePolygonFromVertexList(Vertex[] vertices) {

		int startX = (int) EngMath.minX(vertices);
		int startY = (int) EngMath.minY(vertices);
		int endX = (int) EngMath.maxX(vertices);
		int endY = (int) EngMath.maxY(vertices);

		Vertex scanLeft, scanRight;
		Line scanline;

		Line[] edges = new Line[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			edges[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length]);
		}

		for (int y = startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);

			for (int i = 0; i < edges.length; ++i) {
				if (EngMath.isOverlappingScanline(scanline, edges[i]) && (y == startY || y == endY)) {
					int minX = (int) EngMath.min(edges[i].v0.x, edges[i].v1.x);
					int maxX = (int) EngMath.max(edges[i].v0.x, edges[i].v1.x);
					for (int x = minX; x <= maxX; ++x) {
						pixels[x + y * w] = 0xff000000;
					}
					break;
				}

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, edges[i]);
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX, endX)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(edges[i]), EngMath.maxYInLine(edges[i]))) {
					pixels[(int) (curIntersection.x + curIntersection.y * w)] = 0xff000000;
				}
			}
		}
	}

	public void renderPolygonFromVertexList(Vertex[] vertices) {

		int startX = (int) EngMath.minX(vertices);
		int startY = (int) EngMath.minY(vertices);
		int endX = (int) EngMath.maxX(vertices);
		int endY = (int) EngMath.maxY(vertices);

		Vertex scanLeft, scanRight;
		Line scanline;

		Line[] edges = new Line[vertices.length];
		Vertex[] intersection;
		for (int i = 0; i < vertices.length; ++i) {
			edges[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length]);
		}

		int intersectionCount;
		final int MAX_INTERSECTIONS_PER_SCANLINE = 4;
		for (int y = startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			intersection = new Vertex[MAX_INTERSECTIONS_PER_SCANLINE];
			intersectionCount = 0;
			double leftmost = endX, rightmost = startX;
			for (int i = 0; i < edges.length; ++i) {

				// if (EngMath.isOverlappingScanline(scanline, edges[i]) && (y
				// == startY || y == endY)) {
				// int minX = (int) EngMath.min(edges[i].v0.x, edges[i].v1.x);
				// int maxX = (int) EngMath.max(edges[i].v0.x, edges[i].v1.x);
				// for (int x = minX; x <= maxX; ++x) {
				// pixels[x + y * w] = 0xff000000;
				// }
				// break;
				// }

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, edges[i]);
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX, endX)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(edges[i]), EngMath.maxYInLine(edges[i]))) {
					intersection[intersectionCount++] = curIntersection;
					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				}
			}

			for (int x = (int) leftmost; x < rightmost; ++x) {
				pixels[x + y * w] = 0xff0000ff;
			}
		}
	}

	public void renderPolygonFromVertexList(Vertex[] vertices, int color) {

		double startX = EngMath.minX(vertices);
		double startY = EngMath.minY(vertices);
		double endX = EngMath.maxX(vertices);
		double endY = EngMath.maxY(vertices);

		Vertex scanLeft, scanRight;
		Line scanline;

		Line[] edges = new Line[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			edges[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length]);
		}

		for (int y = (int) startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			double leftmost = endX, rightmost = startX;
			for (int i = 0; i < edges.length; ++i) {

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, edges[i]);
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX-0.1, endX+0.1)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(edges[i]), EngMath.maxYInLine(edges[i]))) {

					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				}
			}
			for (int x = (int) leftmost; x < rightmost; ++x) {
				setPixel(x, y, color);
			}
		}
	}
	
	public void renderPolygonWithLerpTexture(Vertex[] vertices, Texture tex) {
		if(tex == null) renderPolygonFromVertexList(vertices);
		double startX = EngMath.minX(vertices);
		double startY = EngMath.minY(vertices);
		double endX = EngMath.maxX(vertices);
		double endY = EngMath.maxY(vertices);

		Vertex scanLeft, scanRight;
		Line scanline;

		Line[] edges = new Line[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			edges[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length]);
		}

		for (int y = (int) startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			double leftmost = endX, rightmost = startX;
			for (int i = 0; i < edges.length; ++i) {

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, edges[i]);
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX, endX)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(edges[i]), EngMath.maxYInLine(edges[i]))) {

					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				}
			}
			for (int x = (int) leftmost; x < rightmost; ++x) {
				double width = endX - startX; //Width of current in-polygon line-segment
				double height = endY - startY;
				double yy = (int) (y - startY);
				double xx = (int) (x - leftmost);
				double	t = (int) xx / width;
				double	u = (int) yy / height;
				
				if(t > 1) t = 1;
				else if(t < 0) t = 0;
				if(u > 1) u = 1;
				else if(u < 0) u = 0;

				
				int texX = (int) EngMath.lerp(0, tex.getWidth(), t);
				int texY = (int) EngMath.lerp(0, tex.getHeight(), u);
				int color = tex.getPixel(texX, texY);
				
				Color leftColor = Color.getColor(tex.getPixel(texX - 1, texY));
				Color rightColor = Color.getColor(tex.getPixel(texX + 1, texY));
				Color bottomColor = Color.getColor(tex.getPixel(texX, texY + 1));
				Color topColor = Color.getColor(tex.getPixel(texX, texY - 1));
				
				Color bottomLeftColor = Color.getColor(tex.getPixel(texX - 1, texY + 1));
				Color bottomRightColor = Color.getColor(tex.getPixel(texX + 1, texY + 1));
				Color topLeftColor = Color.getColor(tex.getPixel(texX - 1, texY - 1));
				Color topRightColor = Color.getColor(tex.getPixel(texX + 1, texY - 1));
				
				Color col = Color.getColor(color);
				
				col = col.weightedAverage(leftColor, rightColor, bottomColor, topColor, bottomLeftColor, bottomRightColor, topLeftColor, topRightColor);
				
				color = Color.getColor(col);
				setPixel(x, y, color);
			}
		}
	}
	
	public void renderPolygonWithTexture(Vertex[] vertices, Texture tex) {
		if(tex == null) renderPolygonFromVertexList(vertices);
		double startX = EngMath.minX(vertices);
		double startY = EngMath.minY(vertices);
		double endX = EngMath.maxX(vertices);
		double endY = EngMath.maxY(vertices);
		double width = endX - startX;
		double height = endY - startY;

		Vertex scanLeft, scanRight;
		Line scanline;

		Line[] edges = new Line[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			edges[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length]);
		}

		for (int y = (int) startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			double leftmost = endX, rightmost = startX;
			
			for (int i = 0; i < edges.length; ++i) {

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, edges[i]);
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX, endX)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(edges[i]), EngMath.maxYInLine(edges[i]))) {
					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				}
			}
			
			double yy = (int) (y - startY);
			
			for (int x = (int) leftmost; x < rightmost; ++x) {
				double xx = (int) (x - startX);
				double t = (int) xx / width;
				double u = (int) yy / height;
				
//				if(t > 1) t = 1;
//				else if(t < 0) t = 0;
//				if(u > 1) u = 1;
//				else if(u < 0) u = 0;
				
				int texX = (int) EngMath.round(EngMath.lerp(0, tex.getWidth(), t));
				int texY = (int) EngMath.round(EngMath.lerp(0, tex.getHeight(), u));
				int color = tex.getPixel(texX, texY);
				setPixel(x, y, color);
			}
		}
	}
	
	public void renderPolygonWithTexture(Polygon poly, Texture tex) {
		if(tex == null) renderPolygonFromVertexList(poly.vertices);
		double startX = EngMath.minX(poly.vertices);
		double startY = EngMath.minY(poly.vertices);
		double endX = EngMath.maxX(poly.vertices);
		double endY = EngMath.maxY(poly.vertices);
		double width = endX - startX;
		double height = endY - startY;

		Vertex scanLeft, scanRight;
		Line scanline, curEdge;

		for (int y = (int) startY; y <= endY; ++y) {

			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			double leftmost = endX, rightmost = startX;
			double yy = (int) (y - startY); //Coordinate system normalized y
			
			for (int i = 0; i < poly.edges.length; ++i) {
				curEdge = poly.edges[i];
				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, curEdge);
				/** Check that the intersection found is not null and is bounded on the polygon's width, on its current edge's height and the polygon's height */
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX-0.1, endX+0.1)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(curEdge), EngMath.maxYInLine(curEdge))) {
					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				}
			}
			
			for (int x = (int) leftmost; x < rightmost; ++x) {
				double xx = (int) (x - startX);
				double t = (int) xx / width;
				double u = (int) yy / height;
				
				int texX = (int) EngMath.round(EngMath.lerp(0, tex.getWidth(), t));
				int texY = (int) EngMath.round(EngMath.lerp(0, tex.getHeight(), u));
				int color = tex.getPixel(texX, texY);
				setPixel(x, y, color);
			}
		}
	}
	
	public void renderPolygon(Polygon poly, int color) {

		double startX = EngMath.minX(poly.vertices);
		double startY = EngMath.minY(poly.vertices);
		double endX = EngMath.maxX(poly.vertices);
		double endY = EngMath.maxY(poly.vertices);

		Vertex scanLeft, scanRight;
		Line scanline;

		for (int y = (int) startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			double leftmost = endX, rightmost = startX;
			for (int i = 0; i < poly.edges.length; ++i) {

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, poly.edges[i]);
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX-0.1, endX+0.1)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(poly.edges[i]), EngMath.maxYInLine(poly.edges[i]))) {

					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				}
			}
			for (int x = (int) leftmost; x < rightmost; ++x) {
				setPixel(x, y, color);
			}
		}
	}
	
	public void renderPolygonWithTextureAndLight(Polygon poly, Texture tex, Light[] lights) {
		if(tex == null) renderPolygonFromVertexList(poly.vertices);
		double startX = EngMath.minX(poly.vertices);
		double startY = EngMath.minY(poly.vertices);
		double endX = EngMath.maxX(poly.vertices);
		double endY = EngMath.maxY(poly.vertices);
		double width = endX - startX;
		double height = endY - startY;

		Vertex scanLeft, scanRight;
		Line scanline, curEdge;

		for (int y = (int) startY; y <= endY; ++y) {

			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			double leftmost = endX, rightmost = startX;
			double yy = (int) (y - startY); //Coordinate system normalized y
			
			for (int i = 0; i < poly.edges.length; ++i) {
				curEdge = poly.edges[i];
				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, curEdge);
				/** Check that the intersection found is not null and is bounded on the polygon's width, on its current edge's height and the polygon's height */
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX-0.1, endX+0.1)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(curEdge), EngMath.maxYInLine(curEdge))) {
					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				} else if(curIntersection != null && y == 30) {
					System.out.println(EngMath.isBounded(curIntersection.x, startX-0.1, endX+0.1));
					System.out.println(EngMath.isBounded(curIntersection.y, startY, endY));
					System.out.println(EngMath.isBounded(curIntersection.y,
							EngMath.minYInLine(curEdge), EngMath.maxYInLine(curEdge)));
					setPixel(50, y);
				}
			}
			
			for (int x = (int) leftmost; x < rightmost; ++x) {
				double xx = (int) (x - startX);
				double t = (int) xx / width;
				double u = (int) yy / height;
				
				int texX = (int) EngMath.round(EngMath.lerp(0, tex.getWidth(), t));
				int texY = (int) EngMath.round(EngMath.lerp(0, tex.getHeight(), u));
				int color = tex.getPixel(texX, texY);
				
				Vector surfaceNormal = new Vector(0, 0, 1);
				double r0 = (color >> 16) & 0xff;
				double g0 = (color >> 8) & 0xff;
				double b0 = (color >> 0) & 0xff;
				
				double r = 0, g = 0, b = 0;
				for(int i = 0; i < lights.length; ++i) {
					Vector lightVector = new Vector(lights[i].x - x, lights[i].y - y, 5);
					double diffuseCoeff = EngMath.getDiffuseValue(surfaceNormal, lightVector);
					
					
					r += ((color >> 16) & 0xff) * diffuseCoeff;
					g += ((color >> 8) & 0xff) * diffuseCoeff;
					b += (color & 0xff) * diffuseCoeff;
					if(r > 0xff) r = 0xff;
					else if(r < 0) r = 0;
					if(g > 0xff) g = 0xff;
					else if(g < 0) g = 0;
					if(b > 0xff) b = 0xff;
					else if(b < 0) b = 0;
					
//					System.out.println("RGB: " + r + ", " + g + ", " + b);
				}
				color = ((0xff << 24) | ((int) r << 16) | ((int)g << 8) | (int)b);
				setPixel(x, y, color);
			}
		}
	}
	

	private void clearShadowBuffer(int lightIndex) {
		for(int y = 0; y < h; ++y) {
			for(int x = 0; x < w; ++x) {
				shadow[lightIndex][x + y * w] = STENCIL_LIGHT_BIT;
			}
		}
	}
	
	private void putShadow(int lightIndex, int x, int y) {
		if(x < 0 || x >= w || y < 0 || y >= h) return;
		shadow[lightIndex][x + y * w] = STENCIL_SHADOW_BIT;
	}
	
	public void bufferShadows(Vertex[] vertices, int lightIndex) {
		if (lightIndex >= MAX_LIGHTS)
			return;

		double startX = EngMath.minX(vertices);
		double startY = EngMath.minY(vertices);
		double endX = EngMath.maxX(vertices);
		double endY = EngMath.maxY(vertices);

		Vertex scanLeft, scanRight;
		Line scanline;

		Line[] edges = new Line[vertices.length];
		Vertex[] intersection;
		for (int i = 0; i < vertices.length; ++i) {
			edges[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length]);
		}

		int intersectionCount;
		final int MAX_INTERSECTIONS_PER_SCANLINE = 6;

		for (int y = (int) startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			intersection = new Vertex[MAX_INTERSECTIONS_PER_SCANLINE];
			intersectionCount = 0;
			double leftmost = endX, rightmost = startX;
			for (int i = 0; i < edges.length; ++i) {

//				Vertex curIntersection = EngMath.round(EngMath.getIntersectionPoint(scanline, edges[i]));
				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, edges[i]);
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX-0.1, endX+0.1)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(edges[i]), EngMath.maxYInLine(edges[i]))) {

					intersection[intersectionCount++] = curIntersection;
					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				} else if(curIntersection != null) { // for debugging
//					if(y == 230) {
//						setPixel(174, y, 0xffff0000);
//						System.out.println("No drawing at y: " + y);
//						System.out.println("Current Intersection: " + curIntersection);
//						System.out.println("Polygon min and max along x: " + startX + ", " + endX);
//						System.out.println("Is bounded inside the polygon: " + EngMath.isBounded(curIntersection.y, startY, endY));
//						System.out.println(EngMath.isBounded(curIntersection.y, EngMath.minYInLine(edges[i]), EngMath.maxYInLine(edges[i])));
//						System.out.println(EngMath.isBounded(curIntersection.x, startX, endX));
//					}
				}
			}
			for (int x = (int) Math.floor(leftmost); x < Math.ceil(rightmost); ++x) {
				putShadow(lightIndex, x, y);
			}
		}
	}
	
	public void renderProcessedShadows(Light[] lights) {
		boolean foundLit = false;
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				for (int i = 0; i < lights.length; ++i) {
					foundLit = false;
					if (shadow[i][x + y * w] == STENCIL_LIGHT_BIT) { //if light bit here go to next pixel
						foundLit = true;
						break;
					}
					else if(shadow[i][x + y * w] == STENCIL_SHADOW_BIT){ // Pixel is shadow, see if there is another light that has buffered a lit pixel here
						for (int j = 0; j < lights.length; ++j) {
							if(j == i) continue;
							if (shadow[j][x + y * w] == STENCIL_LIGHT_BIT) {
								foundLit = true;
								break;
							}
						}
					}
					if(foundLit) break;
				}
				if(!foundLit) {
					setPixel(x, y, SHADOW_COLOR);
				}
			}
		}
	}
	
	public void renderProcessedShadows(Light[] lights, boolean renderAmbiance) {
		if(renderAmbiance) {
			int shadowDegree;
			for (int y = 0; y < h; ++y) {
				for (int x = 0; x < w; ++x) {
					shadowDegree = 0;
					for (int i = 0; i < lights.length; ++i) {
						if(shadow[i][x + y * w] == STENCIL_SHADOW_BIT) shadowDegree++;
					}
					if(shadowDegree == 1) {
						setPixel(x, y, SHADOW_COLOR);
					} else if(shadowDegree == 2) {
						setPixel(x, y, 0xff444444);
					} else if(shadowDegree == 3) {
						setPixel(x, y, 0xff111111);
					}
				}
			}
		} else {
			boolean foundLit = false;
			for (int y = 0; y < h; ++y) {
				for (int x = 0; x < w; ++x) {
					for (int i = 0; i < lights.length; ++i) {
						foundLit = false;
						if (shadow[i][x + y * w] == STENCIL_LIGHT_BIT) { //if light bit here go to next pixel
							foundLit = true;
							break;
						}
						else if(shadow[i][x + y * w] == STENCIL_SHADOW_BIT){ // Pixel is shadow, see if there is another light that has buffered a lit pixel here
							for (int j = 0; j < lights.length; ++j) {
								if(j == i) continue;
								if (shadow[j][x + y * w] == STENCIL_LIGHT_BIT) {
									foundLit = true;
									break;
								}
							}
						}
						if(foundLit) break;
					}
					if(!foundLit) {
						setPixel(x, y, SHADOW_COLOR);
					}
				}
			}
		}
	}

	public void renderShadow(Polygon poly, Light[] lights) {
		/** Clear all shadows */
		for(int i = 0; i < lights.length; ++i) {
			clearShadowBuffer(i);
		}
		/** Buffer all the shadows */
		for (int i = 0; i < lights.length; ++i) {
			if(isInsidePolygon(poly, lights[i])) { //Make sure to not render shadows for this light because it's inside the polygon
				continue;
			}
			renderShadow(poly, poly.normals, lights[i], i);
		}
		renderProcessedShadows(lights, true);
	}

	/**
	 * Renders a polygon with projective shadows according to light-sources
	 * NOTE: The normals _HAVE_ to coincide with the edges, otherwise the
	 * algorithm will fail!
	 */
	public void renderShadow(Polygon poly, Vector[] normals, Light light, int lightIndex) {

		Line[] unlitEdge = new Line[poly.edges.length];
		Vector currentNormal = null;
		Line currentEdge = null;
		
		/** First pass, determine the unlit edges */
		for (int i = 0; i < poly.edges.length; ++i) {

			currentEdge = poly.edges[i];

			Vector lightVector = new Vector(currentEdge.v0, new Vertex(light.x, light.y));
			lightVector.normalize();

			unlitEdge[i] = currentEdge;

			double scalar = Vector.dot(lightVector, normals[i]);
			if (scalar >= 0) {
				unlitEdge[i] = null;
			}
		}

		/** Second pass, determine the relevant lights */
		for (int i = 0; i < unlitEdge.length; ++i) {

			if (unlitEdge[i] == null)
				continue;

			currentEdge = poly.edges[i];
			currentNormal = normals[i];
			currentNormal.normalize();

			Vector v0Normal = Vector.perp(currentNormal);
			Vector v1Normal = Vector.cperp(currentNormal);
			v0Normal.normalize();
			v1Normal.normalize();

			Line ray0 = new Line(new Vertex(light.x, light.y), currentEdge.v0);
			Line ray1 = new Line(new Vertex(light.x, light.y), currentEdge.v1);

			Vertex collision0 = null, collision1 = null, tmp = null;
			int indexBoundsCollided0 = -1, indexBoundsCollided1 = -2;
			for (int k = 0; k < 4; ++k) {
				tmp = EngMath.getRayIntersectionPoint(ray0, LINE_BOUNDS[k]);
				if (tmp != null) {
					if ((int)tmp.x >= 0 && (int)tmp.x <= w && (int)tmp.y >= 0 && (int)tmp.y <= h) {
						collision0 = tmp;
						indexBoundsCollided0 = k;
						break;
					}
				}
			}
			
			for (int k = 0; k < 4; ++k) {
				tmp = EngMath.getRayIntersectionPoint(ray1, LINE_BOUNDS[k]);
				if (tmp != null) {
					if ((int)tmp.x >= -2 && (int)tmp.x <= w && (int)tmp.y >= -2 && (int)tmp.y <= h) {
						collision1 = tmp;
						indexBoundsCollided1 = k;
						break;
					}
				}
			}

			if (collision0 != null && collision1 != null) {
				double scalar = 0;
				double tmpScalar = 0;
				int indexBoundsCollided2 = 0;
				
				collision0 = EngMath.round(collision0);
				collision1 = EngMath.round(collision1);

				if (EngMath.abs(indexBoundsCollided0 - indexBoundsCollided1) == 2) {
					// Solve for opposite edge cases
					for (int k = 0; k < 4; ++k) {
						tmpScalar = scalar;
						scalar = Vector.dot(currentNormal, BOUND_NORMALS[k]);
						if (scalar < tmpScalar) {
							scalar = tmpScalar;
						} else {
							indexBoundsCollided2 = k;
						}
					}

					Vertex collision2 = LINE_BOUNDS[indexBoundsCollided2].v0;
					Vertex collision3 = LINE_BOUNDS[indexBoundsCollided2].v1;
					
					bufferShadows(new Vertex[] { currentEdge.v0, currentEdge.v1, collision1, collision3, collision2, collision0 }, lightIndex);

				} else if (indexBoundsCollided0 - indexBoundsCollided1 == 0) {
					//same edge case, don't have to deal with this any way.
					bufferShadows(new Vertex[] { currentEdge.v0, currentEdge.v1, collision1, collision0 }, lightIndex);

				} else {
					// Solve for adjacent cases
					Vertex collision2 = getCorner(indexBoundsCollided0, indexBoundsCollided1);
//					System.out.println("Collisions found: " + collision1 + ", " + collision0);
					bufferShadows(new Vertex[] { currentEdge.v0, currentEdge.v1, collision1, collision2, collision0 }, lightIndex);
				}
			}
		}

//		renderDot((int) light.x, (int) light.y, 0xff0000ff);
		setPixel((int) light.x, (int) light.y, 0xff0000ff);
	}

	private Vertex getCorner(int index0, int index1) {
		if(index0 == INDEX_TOP && index1 == INDEX_RIGHT || index1 == INDEX_TOP && index0 == INDEX_RIGHT) {
			return CORNER_TOP_RIGHT;
		}
		if(index0 == INDEX_TOP && index1 == INDEX_LEFT || index1 == INDEX_TOP && index0 == INDEX_LEFT) {
			return CORNER_TOP_LEFT;
		}
		if(index0 == INDEX_BOTTOM && index1 == INDEX_RIGHT || index1 == INDEX_BOTTOM && index0 == INDEX_RIGHT) {
			return CORNER_BOTTOM_RIGHT;
		}
		if(index0 == INDEX_BOTTOM && index1 == INDEX_LEFT || index1 == INDEX_BOTTOM && index0 == INDEX_LEFT) {
			return CORNER_BOTTOM_LEFT;
		}
		return null; //Indices not appropriate for method, no such combination
	}

	private String boundName(int index) {
		if (index == INDEX_BOTTOM)
			return "Bottom";
		else if (index == INDEX_LEFT)
			return "Left";
		else if (index == INDEX_TOP)
			return "Top";
		else if (index == INDEX_RIGHT)
			return "Right";
		else if (index == INDEX_BOTTOM_RIGHT)
			return "Bottom_Right";
		else if (index == INDEX_TOP_RIGHT)
			return "Top_Right";
		else if (index == INDEX_TOP_LEFT)
			return "Top_Left";
		else if (index == INDEX_BOTTOM_LEFT)
			return "Bottom_Left";
		else
			return "Invalid Index!";
	}

	public void renderDot(int x, int y, int col) {
		setPixel(x, y, col);
		setPixel(x - 1, y, col);
		setPixel(x - 1, y - 1, col);
		setPixel(x, y - 1, col);
		setPixel(x + 1, y, col);
		setPixel(x + 1, y + 1, col);
		setPixel(x, y + 1, col);
	}

	public void renderPolygonFromVertexList(Vertex[] vertices, Color[] colors) {

		int startX = (int) EngMath.minX(vertices);
		int startY = (int) EngMath.minY(vertices);
		int endX = (int) EngMath.maxX(vertices);
		int endY = (int) EngMath.maxY(vertices);

		Vertex scanLeft, scanRight;
		Line scanline;

		Line[] edges = new Line[vertices.length];
		Vertex[] intersection;
		for (int i = 0; i < vertices.length; ++i) {
			edges[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length]);
		}

		int intersectionCount;
		final int MAX_INTERSECTIONS_PER_SCANLINE = 4;
		for (int y = startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);
			intersection = new Vertex[MAX_INTERSECTIONS_PER_SCANLINE];
			intersectionCount = 0;
			double leftmost = endX, rightmost = startX;
			for (int i = 0; i < edges.length; ++i) {

				// if (EngMath.isOverlappingScanline(scanline, edges[i]) && (y
				// == startY || y == endY)) {
				// int minX = (int) EngMath.min(edges[i].v0.x, edges[i].v1.x);
				// int maxX = (int) EngMath.max(edges[i].v0.x, edges[i].v1.x);
				// for (int x = minX; x <= maxX; ++x) {
				// pixels[x + y * w] = 0xff000000;
				// }
				// break;
				// }

				Vertex curIntersection = EngMath.getIntersectionPoint(scanline, edges[i]);
				if (curIntersection != null && EngMath.isBounded(curIntersection.x, startX, endX)
						&& EngMath.isBounded(curIntersection.y, startY, endY) && EngMath.isBounded(curIntersection.y,
								EngMath.minYInLine(edges[i]), EngMath.maxYInLine(edges[i]))) {
					intersection[intersectionCount++] = curIntersection;
					leftmost = curIntersection.x < leftmost ? curIntersection.x : leftmost;
					rightmost = curIntersection.x > rightmost ? curIntersection.x : rightmost;
				}
			}

			for (int x = (int) leftmost; x < rightmost; ++x) {
				pixels[x + y * w] = 0xff00;
			}
		}
	}

	/** TODO: Implement tessellation! */
	private int[][] tessellate(Vertex[] vertices) {
		int[][] triangles;
		int[] shared;
		int numTris = 0;
		if (EngMath.parity(vertices.length) == EngMath.PAIR) {
			for (int i = 0; i < vertices.length; ++i) {
				if (i % 3 == 0) {
				}
			}
		} else {
			int[] unshared;
			for (int i = 0; i < vertices.length; ++i) {

			}
		}
		return null;
	}

	public void renderPolygonEdge(Line edge) {
		int startX = (int) EngMath.minXInLine(edge);
		int startY = (int) EngMath.minYInLine(edge);
		int endX = (int) EngMath.maxXInLine(edge);
		int endY = (int) EngMath.maxYInLine(edge);

		Vertex scanLeft, scanRight;
		Line scanline;
		for (int y = startY; y <= endY; ++y) {
			scanLeft = new Vertex(0, y);
			scanRight = new Vertex(endX, y);
			scanline = new Line(scanLeft, scanRight);

			if (EngMath.isOverlappingScanline(scanline, edge) && (y == startY || y == endY)) {
				int minX = (int) EngMath.min(edge.v0.x, edge.v1.x);
				int maxX = (int) EngMath.max(edge.v0.x, edge.v1.x);
				for (int x = minX; x <= maxX; ++x) {
					pixels[x + y * w] = 0xff000000;
				}
				break;
			}

			Vertex intersection = EngMath.getIntersectionPoint(scanline, edge);
			if (intersection != null) {
				pixels[(int) (intersection.x + intersection.y * w)] = 0xff000000;
			}
		}
	}

	public void renderLight(Polygon[] polygons, Light[] lights) {
		isRenderingLight = true;

		final int color = 0xff10ff30;

		for (int i = 0; i < lights.length; ++i) {
			for (int j = 0; j < polygons.length; ++j) {
				renderPolygonFromVertexList(polygons[j].vertices, color);
			}
		}
	}
	
	/** Checks if a light source is inside polygon by dotting a line from the edge to the source with the coinciding normal,
	 * if the dot is greater than 0 it has to be outside the polygon, if all dots are >= 0 then it's inside it */
	public boolean isInsidePolygon(Polygon poly, Light light) {
		for(int i = 0; i < poly.normals.length; ++i) {
			double dot = Vector.dot(poly.normals[i], new Vector(poly.edges[i].v0, new Vertex(light.x, light.y)));
			if(dot > 0) {
				return false;
			}
		}
		return true;
	}

	public void clear(int color) {
		for (int i = 0; i < pixels.length; ++i) {
			pixels[i] = color;
		}
	}

	public void swap() {
		raster.swap();
	}

	/** Sets pixel at (x, y) to be a specified color (ARGB) */
	public void setPixel(int x, int y, int col) {
		if (x < 0 || x >= w || y < 0 || y >= h)
			return;
		pixels[x + y * w] = col;
	}

	/** Sets pixel at (x, y) to be the default color black. */
	public void setPixel(int x, int y) {
		if (x < 0 || x >= w || y < 0 || y >= h)
			return;
		pixels[x + y * w] = 0xff000000;
	}

	public Raster getRaster() {
		return raster;
	}
}

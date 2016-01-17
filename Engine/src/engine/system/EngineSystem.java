package engine.system;

import java.awt.Canvas;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.system.gfx.Color;
import engine.system.gfx.Texture;
import engine.system.gfx.Viewport;
import engine.system.gfx.light.Light;
import engine.utility.math.EngMath;
import engine.utility.math.geometry.Line;
import engine.utility.math.geometry.Vertex;
import engine.utility.math.linearalgebra.Vector;
import engine.utility.model.Octagon;
import engine.utility.model.Pentagon;
import engine.utility.model.Quad;
import engine.utility.model.Triangle;

public class EngineSystem implements Runnable {
	private Viewport viewport;
	private boolean running;
	
	public static final int DEFAULT_WIDTH = 320 * 1;
	public static final int DEFAULT_HEIGHT = 240 * 1;
	public static final int DEFAULT_SCALE = 3;
	public static final String TITLE = "Engine";
	
	public EngineSystem() {
		viewport = new Viewport(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_SCALE, TITLE);
	}
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void run() {
		
		/** TRIANGLE */
		int t = 100; //offset
		Vertex[] verts = new Vertex[Triangle.VERTICES];
		verts[0] = new Vertex(26.0 + t, 0.0 + t);
		verts[1] = new Vertex(25 + t, 80 + t);
		verts[2] = new Vertex(80.0 + t, 0.0 + t);
		
		Line[] edges = new Line[Triangle.EDGES];
		edges[0] = new Line(verts[0], verts[1]);
		edges[1] = new Line(verts[1], verts[2]);
		edges[2] = new Line(verts[2], verts[0]);
		
		Vector[] normals = new Vector[Triangle.EDGES];
		normals[0] = Vector.perp(new Vector(verts[0], verts[1]));
		normals[1] = Vector.perp(new Vector(verts[1], verts[2]));
		normals[2] = Vector.perp(new Vector(verts[2], verts[0]));
		
		
		/** QUAD */
		Vertex[] verts2 = new Vertex[Quad.VERTICES];
		verts2[0] = new Vertex(70 + 40, 40 + 0);
		verts2[1] = new Vertex(10 * 4 + 40, 40 * 3 + 0);
		verts2[2] = new Vertex(15 * 7 + 40, 30 * 5 + 0);
		verts2[3] = new Vertex(15 * 9 + 40, 10 * 4 + 0);
//		verts2[0] = new Vertex(70 + 40, 40 + 0);
//		verts2[1] = new Vertex(10 * 7 + 40, 40 * 3 + 0);
//		verts2[2] = new Vertex(170 + 40, 40 * 3 + 0);
//		verts2[3] = new Vertex(170 + 40, 10 * 4 + 0);
		
		
		Line[] edges2 = new Line[Quad.EDGES];
		edges2[0] = new Line(verts2[0], verts2[1]);
		edges2[1] = new Line(verts2[1], verts2[2]);
		edges2[2] = new Line(verts2[2], verts2[3]);
		edges2[3] = new Line(verts2[3], verts2[0]);
		
		Vector[] normals2 = new Vector[Quad.EDGES];
		normals2[0] = Vector.perp(new Vector(verts2[0], verts2[1]));
		normals2[1] = Vector.perp(new Vector(verts2[1], verts2[2]));
		normals2[2] = Vector.perp(new Vector(verts2[2], verts2[3]));
		normals2[3] = Vector.perp(new Vector(verts2[3], verts2[0]));
		
//		Line test = new Line(verts2[1], verts2[2]);
//		Line scanlineMock = new Line(new Vertex(0, 30), new Vertex(15, 30));
//		System.out.println("Testline: " + test);
//		System.out.println("In EngineSystem.java: " +  EngMath.isOverlappingScanline(scanlineMock, test));
		
		/** PENTAGON */
		Vertex[] verts3 = new Vertex[Pentagon.VERTICES];
		verts3[0] = new Vertex(100, 0);
		verts3[1] = new Vertex(80, 25);
		verts3[2] = new Vertex(90, 50);
		verts3[3] = new Vertex(110, 50);
		verts3[4] = new Vertex(120, 25);
		
		Line[] edges3 = new Line[Pentagon.EDGES];
		edges3[0] = new Line(verts3[0], verts3[1]);
		edges3[1] = new Line(verts3[1], verts3[2]);
		edges3[2] = new Line(verts3[2], verts3[3]);
		edges3[3] = new Line(verts3[3], verts3[4]);
		edges3[4] = new Line(verts3[4], verts3[0]);
		
		/** OCTAGON */
		Vertex[] verts4 = new Vertex[Octagon.VERTICES];
		verts4[0] = new Vertex(10 + 100, 0 + 100);
		verts4[1] = new Vertex(0 + 100, 10 + 100);
		verts4[2] = new Vertex(0 + 100, 20 + 100);
		verts4[3] = new Vertex(10 + 100, 30 + 100);
		verts4[4] = new Vertex(20 + 100, 30 + 100);
		verts4[5] = new Vertex(30 + 100, 20 + 100);
		verts4[6] = new Vertex(30 + 100, 10 + 100);
		verts4[7] = new Vertex(20 + 100, 0 + 100);
		
		Line[] edges4 = new Line[Octagon.EDGES];
		edges4[0] = new Line(verts4[0], verts4[1]);
		edges4[1] = new Line(verts4[1], verts4[2]);
		edges4[2] = new Line(verts4[2], verts4[3]);
		edges4[3] = new Line(verts4[3], verts4[4]);
		
		edges4[4] = new Line(verts4[4], verts4[5]);
		edges4[5] = new Line(verts4[5], verts4[6]);
		edges4[6] = new Line(verts4[6], verts4[7]);
		edges4[7] = new Line(verts4[7], verts4[0]);
		
		Vector[] normals4 = new Vector[Octagon.EDGES];
		normals4[0] = Vector.perp(new Vector(verts4[0], verts4[1]));
		normals4[1] = Vector.perp(new Vector(verts4[1], verts4[2]));
		normals4[2] = Vector.perp(new Vector(verts4[2], verts4[3]));
		normals4[3] = Vector.perp(new Vector(verts4[3], verts4[4]));
		normals4[4] = Vector.perp(new Vector(verts4[4], verts4[5]));
		normals4[5] = Vector.perp(new Vector(verts4[5], verts4[6]));
		normals4[6] = Vector.perp(new Vector(verts4[6], verts4[7]));
		normals4[7] = Vector.perp(new Vector(verts4[7], verts4[0]));
		
		System.out.println("Triangle normals: " + normals.length + ", edges: " + edges.length);
		Triangle triangle1 = new Triangle(verts, edges, normals);
		Quad quad1 = new Quad(verts2, edges2, normals2);
		Pentagon penta1 = new Pentagon(verts3, edges3);
		Octagon octagon1 = new Octagon(verts4, edges4, normals4);
		
		Light testLight0 = new Light(new Color(0, 0, 0), 0, 120, 150, 0);
		Light testLight1 = new Light(new Color(0, 0, 0), 0, 126, 45, 0);
		Light testLight2 = new Light(new Color(0, 0, 0), 0, 126, 215, 0);
		
		Light[] lights = new Light[] {
				testLight0, testLight1, testLight2
		};
		
		System.out.println("Math.cos(3.14 / 3D): " + Math.cos(3.14 / 3D) + ", EngMath.cos(3.14 / 3D): " + EngMath.cos(3.14 / 3D));
		System.out.println("Math.acos(0.5): " + Math.acos(0.5) + ", EngMath.acos(0.5): " + EngMath.acos(0.5));
		
		Line ray = new Line(new Vertex(160, 120), new Vertex(300, 30));
		System.out.println(EngMath.getRayIntersectionPoint(ray, viewport.TOP));
		System.out.println(EngMath.getRayIntersectionPoint(ray, viewport.BOTTOM));
		System.out.println(EngMath.getRayIntersectionPoint(ray, viewport.LEFT));
		System.out.println(EngMath.getRayIntersectionPoint(ray, viewport.RIGHT));
		
//		triangle1.setScale(1.0);
		triangle1.rotate(0);
		
		long time = System.currentTimeMillis();
		int frames = 0;
		
		Texture tex0 = null, tex1 = null;
		try {
			tex0 = new Texture(ImageIO.read(EngineSystem.class.getResource("/testTexture2.png")));
			tex1 = new Texture(ImageIO.read(EngineSystem.class.getResource("/brick256_0.jpg")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while(running) {
			viewport.clear(0xffddeeff);
//			viewport.renderRectFilled(50, 50, 80, 80, 0xff00f033);
//			viewport.renderRect(100, 50, 130, 80, 0xff0033f0);
			
			
			tick();
			
			
//			viewport.renderPolygonFromVertexList(triangle1.vertices, 0xffff0000);
//			viewport.renderPolygonFromVertexList(quad1.vertices, 0xff00ff00);
//			viewport.renderPolygonFromVertexList(penta1.vertices, 0xffff00ff);
//			viewport.renderPolygonFromVertexList(octagon1.vertices, 0xff0000ff);
			
//			testLight0.y -= 1.1 * Math.sin(tics / 100D);
//			testLight0.x -= 0.1 * Math.cos(tics / 100D);
			
			
			if(tics % 5 == 0) {
				testLight2.x -= 0.1;
				testLight1.y += 0.1;
				testLight0.x += 0.1;
//				triangle1.rotate(0.01);
			}
			
//			viewport.renderPolygonWithLights(new Light[] { testLight }, quad1.vertices, 0xff00ff00);
			
//			viewport.renderShadow(triangle1, lights);
//			viewport.renderShadow(quad1, lights);
//			viewport.renderShadow(octagon1, lights);

//			viewport.renderPolygonFromVertexList(triangle1.vertices, 0xff0000ff);
//			viewport.renderPolygonFromVertexList(quad1.vertices, 0xff00ff00);
//			viewport.renderPolygonFromVertexList(octagon1.vertices, 0xffffff00);
			
//			viewport.renderShadow(quad1, lights);
//			viewport.renderShadow(triangle1, lights);
			
//			viewport.renderPolygonWithTextureAndLight(quad1, tex0, lights);
//			viewport.renderPolygonWithTextureAndLight(triangle1, tex1, lights);
			viewport.renderPolygonWithTextureAndLight(triangle1, tex1, lights);
//			viewport.renderPolygonWithTextureAndLight(quad1, tex1, lights);
			
			viewport.swap();
			
			frames++;
			if(System.currentTimeMillis() - time >= 1000) {
				System.out.println("FPS: " + frames);
				time = System.currentTimeMillis();
				frames = 0;
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int tics = 0;
	private void tick() {
		tics++;
	}
	
	public static void main(String[] args) {
		EngineSystem engsys = new EngineSystem();
		engsys.start();
	}
}

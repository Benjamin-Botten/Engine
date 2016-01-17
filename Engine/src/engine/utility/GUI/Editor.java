package engine.utility.GUI;

import java.util.ArrayList;
import java.util.List;

import engine.system.gfx.Viewport;
import engine.utility.IO.Input;
import engine.utility.math.geometry.Vertex;

public class Editor implements Runnable {

	public static final int DEFAULT_WIDTH = 320 * 3;
	public static final int DEFAULT_HEIGHT = 240 * 3;
	public static final int DEFAULT_SCALE = 1;
	public static final String TITLE = "Engine Editor!";
	private boolean running = false;
	private boolean paused = false; //TODO: Ability to pause the graphics by hit of key.
	
	
	private int tics;

	private Viewport viewport;
	private Input input;
	
	public final Menu defaultOptions;
	
	public Editor() {
		viewport = new Viewport(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_SCALE, TITLE);
		input = new Input(viewport.getRaster());
		defaultOptions = new MenuOption(0, 0, viewport);
	}
	
	public void init() {
		MenuElement clearVertexList = new MenuActionBox(0, 0, "Clear Vertex List");
		MenuElement finishVertexInput = new MenuActionBox(0, 0, "Render The Polygon");
//		clearVertexList.setFill(0xffff0000);
//		finishVertexInput.setFill(0xff00fff0);
		defaultOptions.add(clearVertexList);
		defaultOptions.add(finishVertexInput);
		defaultOptions.add(new MenuActionBox(0, 0, "Some Button0"));
		defaultOptions.add(new MenuActionBox(0, 0, "Some Button1"));
		defaultOptions.add(new MenuActionBox(0, 0, "Some Button2"));
		defaultOptions.add(new MenuActionBox(0, 0, "Some Button3"));
	}
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	private List<Vertex> vertexList = new ArrayList<Vertex>();
	private Vertex[] vertices;
	private int vertCount = 0;
	private Menu menu = new Menu(0, 0, viewport);
	
	
	public void run() {
		
		init();
		
		boolean shouldRender = false;
		
		while(running) {
			viewport.clear(0xffddeeff);
			
			if(input.ml) {
				if(shouldRender) { 
					vertexList.clear();
					shouldRender = false;
				}
				vertexList.add(new Vertex(input.mx, input.my));
			} else if(input.mr) {
				vertices = new Vertex[vertexList.size()];
				for(int i = 0; i < vertices.length; ++i) {
					vertices[i] = vertexList.get(i);
				}
				if(vertices.length > 0) shouldRender = true;
//				System.out.println("Opening option-menu!");
//				menu = defaultOptions;
//				menu.setActive(true);
			}
			
			if(shouldRender) {
				viewport.renderPolygonFromVertexList(vertices, 0xffff00ff);
			}
			
			if(menu.isActive()) {
//				System.out.println("The menu is active and now handles input!");
				menu.handle(input);
			}
			
			viewport.swap();
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void tick() {
		tics++;
	}
	
	
	public static void main(String[] args) {
		new Editor().start();
	}

}

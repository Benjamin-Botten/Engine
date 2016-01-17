package engine.utility.GUI;

import engine.system.gfx.Viewport;
import engine.utility.IO.Input;

public class MenuElement {
	
	protected int x, y; //position on the screen for element
	protected int w, h; //width & height
	protected boolean filled = true; //if the element has color filling
	protected boolean enabled = true;
	protected int fillColor = 0xffeeeeee;
	protected String description;
	
	public MenuElement(int x, int y, String description) {
		this.x = x;
		this.y = y;
		this.description = description;
	}
	
	public void render(Viewport viewport) {
	}
	
	public void handle(Input input) {
	}
	
	public void setAction(MenuAction action) {
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setDimension(int w, int h) {
		this.w = w;
		this.h = h;
	}
	
	public void setFill(int col) {
		fillColor = col;
	}
}

package engine.utility.GUI;

import java.util.ArrayList;
import java.util.List;

import engine.system.gfx.Viewport;
import engine.utility.IO.Input;

public class Menu {
	protected int x, y;
	protected Viewport viewport;
	protected boolean active;
	
	public Menu(int x, int y, Viewport viewport) {
		this.x = x;
		this.y = y;
		this.viewport = viewport;
		active = false;
	}
	
	public void handle(Input input) {
	}
	
	protected void render(Viewport viewport) {
		
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void add(MenuElement menuElement) {
		
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void initElements() {
	}
}

package engine.utility.GUI;

import engine.system.gfx.Viewport;
import engine.utility.IO.Input;

public class MenuActionBox extends MenuElement {

	public MenuActionBox(int x, int y, String description) {
		super(x, y, description);
	}
	
	public void render(Viewport viewport) {
		System.out.println("Rendering menu actionbox with color: " + fillColor);
		viewport.renderRectOutlined(x, y, (x + w), (y + h), fillColor, 0xff888888);
	}
	
	public void handle(Input input) {
		System.out.println("Pressed the -" + description + "- menu actionbox!");
	}
	
	public void setAction(MenuAction action) {
		action.execute(this);
	}
}

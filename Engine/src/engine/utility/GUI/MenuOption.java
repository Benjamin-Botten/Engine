package engine.utility.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.system.gfx.Viewport;
import engine.utility.IO.Input;

/**
 * TODO: Add some clever thing to make right-clicking sensitive to context of
 * what you're clicking at? Maybe?
 */
public class MenuOption extends Menu {

	private Map<Integer, MenuElement> options = new HashMap<>();
	private int size;

	private int actionBoxWidth = 75, actionBoxHeight = 15;

	public MenuOption(int x, int y, Viewport viewport) {
		super(x, y, viewport);
	}

	public void handle(Input input) {
		if (active) {
			setPosition(input.mx, input.my);
			initElements();
			render();
		}
	}

	protected void render() {
		for (int i = 0; i < size; ++i) {
			options.get(i).render(viewport);
		}
	}

	public void add(MenuElement menuElement) {
		if (menuElement == null)
			return;
		options.put(size++, menuElement);
	}

	public void initElements() {
		for (int i = 0; i < size; ++i) {
			options.get(i).setDimension(actionBoxWidth, actionBoxHeight);
			options.get(i).setPosition(x, y + (actionBoxHeight * i));
		}
	}
}

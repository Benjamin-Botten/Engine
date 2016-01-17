package engine.system.gfx.light;

import engine.system.gfx.Color;
import engine.system.gfx.Viewport;

public class Light {
	public Color color;
	public double x, y, z;
	public double r; //Radius
	
	public Light(Color color, double r, double x, double y, double z) {
		this.color = color;
		this.r = r;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return "Light Position: (" + x + ", " + y + ", " + z + ")";
	}
}

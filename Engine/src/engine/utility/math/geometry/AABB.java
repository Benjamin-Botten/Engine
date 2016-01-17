package engine.utility.math.geometry;

/** Axis-Aligned Bounding-Box */

public class AABB {
	public double left, right, top, bottom;
	private double w, h;
	
	public AABB(double left, double right, double top, double bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		w = right - left;
		h = bottom - top;
	}
	
	public void set(double left, double right, double top, double bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		w = right - left;
		h = bottom - top;
	}
	
	public double getWidth() {
		return w;
	}
	
	public double getHeight() {
		return h;
	}
}

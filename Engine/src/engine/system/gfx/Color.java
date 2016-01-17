package engine.system.gfx;

public class Color {
	public double a, r, g, b;

	public Color(double a, double r, double g, double b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(double r, double g, double b) {
		a = 0xff;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public static Color getColor(int color) {
		int a = (color >> 24) & 0xff;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;
		return new Color(a, r, g, b);
	}

	public static int getColor(Color color) {
		return (((int) color.a << 24) | ((int) color.r << 16) | ((int) color.g << 8) | (int) color.b);
	}

	public void add(Color color) {
		a += color.a;
		r += color.r;
		g += color.g;
		b += color.b;
		clamp(0xff);
	}

	public void addNoClamp(Color color) {
		
	}
	
	public void clamp(int value) {
		if(a > value) a = value;
		if(r > value) r = value;
		if(g > value) g = value;
		if(b > value) b = value;
	}
	
	public void divide(int divisor) {
		r /= divisor;
		g /= divisor;
		b /= divisor;
	}
	
	public void mul(double scalar) {
		r *= scalar;
		g *= scalar;
		b *= scalar;
	}
	
	public Color average(Color... c) {
		if(c == null) return null;
		int mag = c.length;
		Color result = c[0];
		double r = c[0].r;
		double g = c[0].g;
		double b = c[0].b;
		for(int i = 1; i < mag; ++i) {
			if(c[i] == null) continue;
			r += c[i].r;
			g += c[i].g;
			b += c[i].b;
		}
		result = new Color(r, g, b);
		result.divide(mag);
		return result;
	}
	
	public Color weightedAverage(Color... c) {
		if(c == null) return null;
		int mag = c.length;
		int[] nOfColor = new int[mag];
		for(int i = 0; i < mag; ++i) {
			if(c[i] == null) continue;
			for(int j = 0; j < mag; ++j) {
				if(c[i].equals(c[j]) && i != j) {
					nOfColor[i]++;
				}
			}
		}
		
		Color result = null;
		for(int i = 0; i < mag; ++i) {
			if(c[i] == null) continue;
			result = c[i];
			result.mul(nOfColor[i]);
			result.divide(mag);
		}
		
		return result;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Color) {
			Color c = (Color) o;
			return this.r == c.r && this.g == c.g && this.b == c.b && this.a == c.a;
		}
		return false;
	}
	
	public String toString() {
		return "(" + a + ", " + r + ", " + g + ", " + b + ")";
	}
}

package engine.system.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
	private BufferedImage tex;
	
//	public static final Texture TEST = loadTexture("testTexture.png");
	
	public Texture(BufferedImage tex) {
		this.tex = tex;
	}
	
	public static Texture loadTexture(String filename) {
		try {
			return new Texture(ImageIO.read(Texture.class.getResource(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getWidth() {
		return tex.getWidth();
	}
	
	public int getHeight() {
		return tex.getHeight();
	}
	
	public int getPixel(int x, int y) {
		if(x < 0 || x >= tex.getWidth() || y < 0 || y >= tex.getHeight()) return 0xffffffff;
		return tex.getRGB(x, y);
	}
}

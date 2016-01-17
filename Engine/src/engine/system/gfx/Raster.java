package engine.system.gfx;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;


import engine.system.EngineSystem;
import engine.utility.IO.Input;

public class Raster extends Canvas {
	private int w, h, scale;
	private BufferedImage raster;
	private int pixels[];
	private JFrame frame;
	private BufferStrategy bs;
	
	public final String title;
	
	public Raster(int w, int h, int scale, String title) {
		this.w = w;
		this.h = h;
		this.scale = scale;
		this.title = title;
		raster = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) raster.getRaster().getDataBuffer()).getData();
	}
	
	public void configureFrame(boolean resizable, boolean visible) {
		setSize(w * scale, h * scale); 
		frame = new JFrame(title);
        frame.add(this);
        frame.setResizable(resizable);
        frame.pack();
        frame.setVisible(visible);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	public void swap() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(raster, 0, 0, w * scale, h * scale, null);
		bs.show();
		g.dispose();
	}
}

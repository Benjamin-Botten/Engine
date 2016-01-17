package test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import engine.utility.math.EngMath;
import engine.utility.math.geometry.Line;
import engine.utility.math.geometry.Vertex;

public class ArbitraryClassTest {

	@Test
	public void maxX_VertexArray_ShouldReturnTheBiggestXValue() {
		Vertex[] vertices = new Vertex[] {
				new Vertex(0D, 0D),
				new Vertex(0D, 30D),
				new Vertex(15D, 15D)
		};
		
		assertThat(EngMath.maxX(vertices), is(equalTo(15D)));
	}

	@Test
	public void maxY_VertexArray_ShouldReturnTheBiggestXValue() {
		Vertex[] vertices = new Vertex[] {
				new Vertex(0D, 0D),
				new Vertex(0D, 30D),
				new Vertex(15D, 15D)
		};
		
		assertThat(EngMath.maxY(vertices), is(equalTo(30D)));
	}
	
	@Test
	public void minX_VertexArray_ShouldReturnTheBiggestXValue() {
		Vertex[] vertices = new Vertex[] {
				new Vertex(0D, 0D),
				new Vertex(0D, 30D),
				new Vertex(15D, 15D)
		};
		
		assertThat(EngMath.minX(vertices), is(equalTo(0D)));
	}
	
	@Test
	public void minY_VertexArray_ShouldReturnTheBiggestXValue() {
		Vertex[] vertices = new Vertex[] {
				new Vertex(0D, 0D),
				new Vertex(0D, 30D),
				new Vertex(15D, 15D)
		};
		
		assertThat(EngMath.minY(vertices), is(equalTo(0D)));
	}
	
	@Test
	public void max_3And5_ShouldReturn5() {
		double a = 3.0;
		double b = 5.0;
		assertThat(EngMath.max(a, b), is(equalTo(b)));
	}
	
	@Test
	public void min_3And5_ShouldReturn3() {
		double a = 3.0;
		double b = 5.0;
		assertThat(EngMath.min(a, b), is(equalTo(a)));
	}
	
	@Test
	public void equals_Vertex_ShouldReturnTrue() {
		assertThat(new Vertex(3, 0).equals(new Vertex(3, 0)), is(equalTo(true)));
	}
	
	@Test
	public void isOverlapping_Lines_ShouldReturnTrue() {
		Vertex v0 = new Vertex(0, 0);
		Vertex v1 = new Vertex(5, 0);
		Vertex v2 = new Vertex(5, 0);
		Vertex v3 = new Vertex(0, 0);
		Line seg0 = new Line(v0, v1);
		Line seg1 = new Line(v2, v3);
		assertThat(EngMath.isOverlappingScanline(seg0, seg1), is(equalTo(true)));
	}
	
	@Test
	public void getIntersectionTime_Lines_ShouldOutputParallelAndOverlapping() {
		Vertex v0 = new Vertex(4, 0);
		Vertex v1 = new Vertex(0, 0);
		Vertex v2 = new Vertex(5, 0);
		Vertex v3 = new Vertex(0, 0);
		Line seg0 = new Line(v0, v1);
		Line seg1 = new Line(v2, v3);
		EngMath.getIntersectionTime(seg0, seg1);
	}
	
	@Test
	public void EngMathcos_PiDividedByTwo_ShouldReturnSomething() {
		for(int i = 0; i < 1000000; ++i) {
			EngMath.cos(3.14D / 3D);
		}
		assertThat(EngMath.isBounded(EngMath.cos(3.14D / 3D), 0.5D, 0.6D), is(equalTo(true)));
	}
	
	@Test
	public void Mathcos_PiDividedByTwo_ShouldReturnSomething() {
		for(int i = 0; i < 1000000; ++i) {
			Math.cos(3.14D / 3D);
		}
		assertThat(EngMath.isBounded(Math.cos(3.14D / 3D), 0.5D, 0.6D), is(equalTo(true)));
	}
}

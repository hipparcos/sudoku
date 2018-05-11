package net.lecnam.sudoku.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import net.lecnam.sudoku.Grid;

class GridTest {
	
	private final String testGridInput = String.join(""
			, "1........"
			, ".2......."
			, "..3......"
			, "...4....."
			, "....5...."
			, ".....6..."
			, "......7.."
			, ".......8."
			, "........9"
			);
	private final String testGridExpected = String.join(""
			, "1........"
			, ".2......."
			, "..3......"
			, "...4....."
			, "....5...."
			, ".....6..."
			, "......7.."
			, ".......8."
			, "........9"
			);
	private final String testGridSolved = String.join(""
			, "145237698"
			, "627189345"
			, "893564127"
			, "216478953"
			, "374951862"
			, "589326471"
			, "451892736"
			, "962713584"
			, "738645219"
			);
	
	private final String invalidGrid = String.join(""
			, "123456789"
			, "123456789"
			, "123456789"
			, "123456789"
			, "123456789"
			, "123456789"
			, "123456789"
			, "123456789"
			, "123456789"
			);

	@Test
	void test_inputoutput() {
		Grid g1 = new Grid();
		try {
			g1.read(new StringReader(testGridInput), true);
			StringWriter output = new StringWriter();
			g1.write(output, Grid.FLAG_INLINE);
			assertEquals(testGridExpected, output.toString());
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
		
	}
	
	@Test
	void test_solved() {
		try {
			Grid g1 = new Grid();
			g1.read(new StringReader(testGridInput), true);
			assertFalse(g1.isSolved());
			
			Grid g2 = new Grid();
			g2.read(new StringReader(invalidGrid), true);
			assertFalse(g2.isSolved());
			
			Grid g3 = new Grid();
			g3.read(new StringReader(testGridSolved), true);
			assertTrue(g3.isSolved());
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}

}

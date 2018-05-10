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
			, "1........\n"
			, ".2.......\n"
			, "..3......\n"
			, "...4.....\n"
			, "....5....\n"
			, ".....6...\n"
			, "......7..\n"
			, ".......8.\n"
			, "........9\n"
			);
	private final String testGridExpected = String.join(""
			, "1........\n"
			, ".2.......\n"
			, "..3......\n"
			, "...4.....\n"
			, "....5....\n"
			, ".....6...\n"
			, "......7..\n"
			, ".......8.\n"
			, "........9\n"
			);
	private final String testGridSolved = String.join(""
			, "145237698\n"
			, "627189345\n"
			, "893564127\n"
			, "216478953\n"
			, "374951862\n"
			, "589326471\n"
			, "451892736\n"
			, "962713584\n"
			, "738645219\n"
			);
	
	private final String invalidGrid = String.join(""
			, "123456789\n"
			, "123456789\n"
			, "123456789\n"
			, "123456789\n"
			, "123456789\n"
			, "123456789\n"
			, "123456789\n"
			, "123456789\n"
			, "123456789\n"
			);

	@Test
	void test_inputoutput() {
		Grid g1 = new Grid();
		try {
			g1.read(new StringReader(testGridInput));
			StringWriter output = new StringWriter();
			g1.write(output, false);
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
			g1.read(new StringReader(testGridInput));
			assertFalse(g1.solved());
			
			Grid g2 = new Grid();
			g2.read(new StringReader(invalidGrid));
			assertFalse(g2.solved());
			
			Grid g3 = new Grid();
			g3.read(new StringReader(testGridSolved));
			assertTrue(g3.solved());
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}

}

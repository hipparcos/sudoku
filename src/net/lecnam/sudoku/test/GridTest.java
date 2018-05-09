package net.lecnam.sudoku.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import net.lecnam.sudoku.Grid;

class GridTest {
	
	private final String grid1Input = String.join(""
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
	private final String grid1Expected = String.join(""
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

	@Test
	void test() {
		Grid g1 = new Grid();
		try {
			g1.read(new StringReader(grid1Input));
			StringWriter output = new StringWriter();
			g1.write(output, false);
			assertEquals(grid1Expected, output.toString());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception");
		}
		
	}

}

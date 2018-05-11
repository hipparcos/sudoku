package net.lecnam.sudoku.solver.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import net.lecnam.sudoku.Grid;
import net.lecnam.sudoku.solver.ConstraintPropagationSolver;

class ConstraintPropagationSolverTest {

	private final String easy1 = String.join(""
			, "..3.2.6.."
			, "9..3.5..1"
			, "..18.64.."
			, "..81.29.."
			, "7.......8"
			, "..67.82.."
			, "..26.95.."
			, "8..2.3..9"
			, "..5.1.3.."
			);
	private final String easy1solved = String.join(""
			, "483921657"
			, "967345821"
			, "251876493"
			, "548132976"
			, "729564138"
			, "136798245"
			, "372689514"
			, "814253769"
			, "695417382"
			);
	
	private final String hard1 = String.join(""
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
	private final String hard1Solved = String.join(""
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
	
	@Test
	void test() {
		Grid g1 = new Grid();
		Grid g2 = new Grid();
		try {
			g1.read(new StringReader(easy1), true);
			assertTrue(g1.solve(new ConstraintPropagationSolver()));
			StringWriter output1 = new StringWriter();
			g1.write(output1, Grid.FLAG_INLINE);
			assertEquals("easy1", easy1solved, output1.toString());
			
			g2.read(new StringReader(hard1), true);
			assertTrue(g2.solve(new ConstraintPropagationSolver()));
			StringWriter output2 = new StringWriter();
			g2.write(output2, Grid.FLAG_INLINE);
			assertEquals("hard1", hard1Solved, output2.toString());
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}

}

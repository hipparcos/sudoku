package net.lecnam.sudoku.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import net.lecnam.sudoku.Grid;
import net.lecnam.sudoku.Solver;

public abstract class SolverTester {

	protected final String fileEasy    = "test/sudoku-easy50.txt";
	protected final String fileHard    = "test/sudoku-hard95.txt";
	protected final String fileHardest = "test/sudoku-hardest11.txt";
	
	protected List<String> easy50 = null;
	protected List<String> hard95 = null;
	protected List<String> hardest11 = null;
	
	protected SolverTester() {
		try {
			easy50 = Files.readAllLines(Paths.get(fileEasy));
			hard95 = Files.readAllLines(Paths.get(fileHard));
			hardest11 = Files.readAllLines(Paths.get(fileHardest));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected final String easy1 = String.join(""
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
	protected final String easy1solved = String.join(""
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

	void testString(Solver solver, String message, String input, String expected) {
		Grid g = new Grid();
		try {
			g.read(new StringReader(input), true);
			assertTrue(g.solve(solver), message);
			if (!expected.isEmpty()) {
				StringWriter output = new StringWriter();
				g.write(output, Grid.FLAG_INLINE);
				assertEquals(expected, output.toString(), message);
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}

	void testStringList(Solver solver, List<String> input) {
		for (String s: input) {
			testString(solver, s, s, "");
		}
	}

	void testFile(Solver solver, String filename) {
		Grid g = new Grid();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			int line = 1;
			while(g.read(reader, true)) {
				StringWriter output = new StringWriter();
				g.write(output, Grid.FLAG_INLINE);
				assertTrue(g.solve(solver),
						String.format("grid %d: %s", line, output.toString()));
				line++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}

}

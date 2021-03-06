package net.lecnam.sudoku;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.lecnam.sudoku.solver.ConstraintPropagationSolver;

/**
 * This Java program is a sudoku solver.<br>
 * I developed it as required to complete the NFP136 module of my computer
 * science evening class at IPST CNAM.<br>
 * <br>
 * Input: the first argument must be a file containing a sudoku grid.<br>
 * Output: the completed sudoku grid.<br>
 * <br>
 * Requirements:<br>
 *   - Must solve any sudoku grid;<br>
 *   - Must be tested using JUnit;<br>
 *   - If multiple solutions exist, show 2 on them.
 *
 * @author Adrien Aucher
 *
 */
public class Main {

	/**
	 * Entry point.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			usage();
		}
		String filename = args[0];

		Grid grid = new Grid();
		Grid gridp = null; // Used to find an other solution for grid.

		// Input.
		try (FileReader fileReader = new FileReader(filename)) {
			grid.read(fileReader);
			// Display input grid.
			System.out.println("Input grid:");
			grid.write(new PrintWriter(System.out));
		} catch (IOException e) {
			System.err.println("Can't read grid.");
			e.printStackTrace();
		}

		// Solve.
		if (!grid.solve(new ConstraintPropagationSolver())) {
			System.err.println("Can't solve grid.");
			System.exit(125);
		}

		// Try to find an other solution (candidates are generated backwards).
		gridp = grid.clone();
		gridp.reset();
		if (!gridp.solve(new ConstraintPropagationSolver(true))) {
			System.err.println("Can't solve grid backwards.");
			System.exit(125);
		}

		// Output.
		try {
			System.out.println("Solution:");
			grid.write(new PrintWriter(System.out));
			if (!grid.equals(gridp)) {
				System.out.println("An other solution:");
				gridp.write(new PrintWriter(System.out));
			}
		} catch (IOException e) {
			System.err.println("Can't write grid.");
			e.printStackTrace();
		}
	}

	/**
	 * Show usage message.
	 */
	public static void usage() {
		System.out.println(String.join(""
				, "This Java program is a sudoku solver.\n"
				, "The first argument must be a valid grid file.\n"
				, "\n"
				, "Usage:\n"
				, "    java -jar sudoku.jar test/grid.txt\n"
				, "\n"
				, "Sample grid file:\n"
				, "    # This line is a comment.\n"
				, "    1........\n"
				, "    .2.......\n"
				, "    ..3......\n"
				, "    ...4.....\n"
				, "    ....5....\n"
				, "    .....6...\n"
				, "    ......7..\n"
				, "    .......8.\n"
				, "    ........9\n"
				));
		System.exit(1);
	}

}

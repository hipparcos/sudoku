package net.lecnam.sudoku;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.lecnam.sudoku.solver.ConstraintPropagationSolver;

public class Main {
	
	public static void main(String[] args) {
		Grid grid = new Grid();
		
		if (args.length < 1) {
			usage();
		}
		String filename = args[0];
		
		// Input.
		try {
			FileReader fileReader = new FileReader(filename);
			grid.read(fileReader);
			fileReader.close();
		} catch (IOException e) {
			System.err.println("Can't read grid.");
			e.printStackTrace();
		}
		
		grid.solve(new ConstraintPropagationSolver());
		
		// Output.
		try {
			grid.write(new PrintWriter(System.out));
		} catch (IOException e) {
			System.err.println("Can't write grid.");
			e.printStackTrace();
		}
	}
	
	public static void usage() {
		System.out.println(String.join(""
				, "This Java program is a sudoku solver.\n"
				, "The first argument must be a valid grid file.\n"
				, "\n"
				, "Usage:\n"
				, "    java -jar sudoku.jar demogrid.txt\n"
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

package net.lecnam.sudoku;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.lecnam.sudoku.solver.ConstraintPropagationSolver;

public class Main {
	private static final String FILENAME = "grid1.txt";
	
	public static void main(String[] args) {
		Grid grid = new Grid();
		
		// Input.
		try {
			FileReader fileReader = new FileReader(FILENAME);
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
}

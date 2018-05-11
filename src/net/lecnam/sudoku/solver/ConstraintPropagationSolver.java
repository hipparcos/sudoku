package net.lecnam.sudoku.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.lecnam.sudoku.Grid;
import net.lecnam.sudoku.Solver;
import net.lecnam.sudoku.Square;

public class ConstraintPropagationSolver implements Solver {
	
	public static final String NAME = "ConstraintPropagationSolver";
	
	public String toString() {
		return NAME;
	}
	
	public ConstraintPropagationSolver() {
	}
	
	public boolean solve(Grid grid) {
		int[] source = grid.cloneSource();
		
		// Init candidates.
		for (int i = 0; i < Square.SIZE; i++) {
			List<Integer> candidates = grid.getCandidates(Square.indexToSquare(i));
			candidates.clear();
			for (int d = 1; d <= Square.SQUARE_MAX_VALUE; d++) {
				candidates.add(d);
			}
		}
		
		for (int i = 0; i < source.length; i++) {
			if (source[i] > 0 && !assign(grid, Square.indexToSquare(i), source[i])) {
				return false;
			}
		}
		
		if (!search(grid)) {
			return false;
		}
		
		int[] solution = new int[Square.SIZE];
		for (int i = 0; i < Square.SIZE; i++) {
			List<Integer> candidates = grid.getCandidates(Square.indexToSquare(i));
			solution[i] = candidates.get(0);
		}
		grid.setSolution(solution);
		
		return true;
	}
	
	private boolean assign(Grid grid, Square square, int digit) {
		List<Integer> candidates = grid.getCandidates(square);
		List<Integer> possibilities = new ArrayList<Integer>();
		possibilities.addAll(candidates);
		for (int d: possibilities) {
			if (d != digit && !eliminate(grid, square, d)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Strategy:
	 * (1) If a square has only one possible value, then eliminate that value from the square's peers.
	 * (2) If a unit has only one possible place for a value, then put the value there.
	 * @param square
	 * @param digit
	 * @return
	 */
	private boolean eliminate(Grid grid, Square square, int digit) {
		List<Integer> candidates = grid.getCandidates(square);
		
		if (!candidates.contains(digit)) {
			return true;
		}
		
		candidates.remove(Integer.valueOf(digit));
		
		// (1) If a square s is reduced to one value, then eliminate it from the peers.
		switch (candidates.size()) {
		case 0:
			return false; // Contradiction: removed last value.
		case 1:
			return eliminate(grid, square.getPeers(), candidates.get(0));
		}
		
		// (2) If a unit u is reduced to only one place for a value d, then put it there.
		for (Square[] unit: square.getUnits()) {
			List<Square> places = new ArrayList<>();
			for (Square s: unit) {
				if (grid.getCandidates(s).contains(digit)) {
					places.add(s);
				}
			}
			switch (places.size()) {
			case 0:
				return false; // Contradiction: no place for this digit.
			case 1:
				// This digit can only be in one place in unit; assign it there.
				if (!assign(grid, places.get(0), digit)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean eliminate(Grid grid, Set<Square> set, int digit) {
		for (Square s: set) {
			if (!eliminate(grid, s, digit)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean search(Grid grid) {
		return true;
	}
}

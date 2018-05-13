package net.lecnam.sudoku.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lecnam.sudoku.Grid;
import net.lecnam.sudoku.Solver;
import net.lecnam.sudoku.Square;

public class ConstraintPropagationSolver implements Solver {
	
	private static final String SOLVER_NAME = "ConstraintPropagationSolver";
	private static Logger logger = LogManager.getLogger(SOLVER_NAME);
	
	public String toString() {
		return SOLVER_NAME;
	}
	
	public boolean solve(Grid grid) {
		int[] source = grid.cloneSource();
		
		logger.info("solve() begin.");
		
		// Init candidates.
		for (Square s: Square.asArray()) {
			List<Integer> candidates = grid.getCandidates(s);
			candidates.clear();
			for (int d = 1; d <= Square.SQUARE_MAX_VALUE; d++) {
				candidates.add(d);
			}
		}
		
		logger.info("solve() phase 1: assign & eliminate.");
		for (Square s: Square.asArray()) {
			if (source[s.ordinal()] > 0 && !assign(grid, s, source[s.ordinal()])) {
				logger.error("solve() failed.");
				return false;
			}
		}
		
		logger.info("solve() phase 2: brute force search.");
		Grid cloned = grid;
		if ((cloned = search(grid)) == null) {
			logger.error("solve() failed.");
			return false;
		}
		
		// Selection the correct grid to get the solution from.
		Grid updater = grid;
		if (cloned != grid) {
			updater = cloned;
		}
		// Create solution array.
		int[] solution = new int[Square.SIZE];
		for (Square s: Square.asArray()) {
			List<Integer> candidates = updater.getCandidates(s);
			solution[s.ordinal()] = candidates.get(0);
		}
		// Update the original grid.
		grid.setSolution(solution);
		
		logger.info("solve() end successfully.");
		
		return true;
	}
	
	private boolean assign(Grid grid, Square square, int digit) {
		logger.debug(String.format("assign() digit %d to square %s.", digit, square));
		// Must copy the list of candidates before because eliminate may
		// remove some during the loop.
		List<Integer> candidates = grid.getCandidates(square);
		List<Integer> possibilities = new ArrayList<Integer>();
		possibilities.addAll(candidates);
		for (int d: possibilities) {
			if (d != digit && !eliminate(grid, square, d)) {
				logger.error(String.format("assign() failed for square %s digit %d.", square, digit));
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
		
		logger.debug(String.format("eliminate() digit %d from square %s.", digit, square));
		candidates.remove(Integer.valueOf(digit));
		
		// (1) If a square s is reduced to one value, then eliminate it from the peers.
		switch (candidates.size()) {
		case 0:
			logger.error(
					String.format("eliminate() strategy 1 failed for square %s digit %d, cause: no more candidates.", square, digit));
			return false; // Contradiction: removed last value.
		case 1:
			logger.debug(String.format("eliminate() from peers of square %s.", square));
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
				logger.error(
						String.format("eliminate() strategy 2 failed for square %s digit %d, cause: no places.", square, digit));
				logger.error(String.format("    Current unit: %s", Arrays.toString(unit)));
				return false; // Contradiction: no place for this digit.
			case 1:
				// This digit can only be in one place in unit; assign it there.
				logger.debug(String.format("eliminate() assign digit %d to place %s.", digit, places.get(0)));
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

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

/**
 * Solve a sudoku grid using a combination of two simple strategies and a brute
 * forcing approach.<br>
 * <br>
 * Phase 1: solve using strategies.<br>
 *   Strategies:<br>
 *   (1) If a square has only one possible value, then eliminate that value
 *       from the square's peers.<br>
 *   (2) If a unit has only one possible place for a value, then put the value
 *       there.<br>
 * <br>
 * Phase 2: solve using brute force.<br>
 * <br>
 * Heavily inspired by http://norvig.com/sudoku.html
 *
 * @author Adrien Aucher
 *
 */
public class ConstraintPropagationSolver implements Solver {

	private static final String SOLVER_NAME = "ConstraintPropagationSolver";
	private static Logger logger = LogManager.getLogger(SOLVER_NAME);

	/**
	 * Set to true to generate candidates backwards.
	 */
	private boolean backward = false;

	public ConstraintPropagationSolver() {
	}
	/**
	 * @param backward set to true to generate candidates backwards.
	 */
	public ConstraintPropagationSolver(boolean backward) {
		this.backward = backward;
	}

	public String toString() {
		return SOLVER_NAME;
	}

	public boolean solve(Grid grid) {
		int[] source = grid.cloneSource();

		logger.info("solve() begin.");

		// Initialize candidates.
		for (Square s: Square.asArray()) {
			List<Integer> candidates = grid.getCandidates(s);
			candidates.clear();
			// If backward is set, generate candidates from higher to lower.
			int d = (backward) ? Square.SQUARE_MAX_VALUE : 1;
			while (backward && d > 0 || !backward && d <= Square.SQUARE_MAX_VALUE) {
				candidates.add(d);
				d = d + ((backward) ? -1 : 1);
			}
		}

		logger.info("solve() phase 1: assign & eliminate.");
		for (Square s: Square.asArray()) {
			int digit = source[s.ordinal()];
			if (digit > 0 && !assign(grid, s, digit)) {
				logger.error("solve() failed.");
				return false;
			}
		}

		logger.info("solve() phase 2: brute force search.");
		Grid brutalized = grid;
		if ((brutalized = search(grid)) == null) {
			logger.error("solve() failed.");
			return false;
		}

		// Select the correct grid to get the solution from.
		Grid updater;
		if (brutalized != grid) {
			updater = brutalized;
		} else {
			updater = grid;
		}
		// Create solution array.
		int[] solution = new int[Square.SIZE];
		for (Square s: Square.asArray()) {
			if (updater.getCandidates(s).size() == 1)
				solution[s.ordinal()] = updater.getCandidates(s).get(0);
		}
		// Update the original grid.
		grid.setSolution(solution);

		logger.info("solve() end successfully.");

		return true;
	}

	/**
	 * Tries to assign a digit to a square.<br>
	 * Calls eliminate on other candidates.
	 *
	 * @param grid
	 * @param square
	 * @param digit to be assigned
	 * @return
	 */
	private boolean assign(Grid grid, Square square, int digit) {
		logger.debug(String.format("assign() digit %d to square %s.",
				digit, square));
		// Must copy the list of candidates before because eliminate may
		// remove some during the loop. Explicit copying to avoid messing
		// with references.
		List<Integer> candidates = grid.getCandidates(square);
		List<Integer> possibilities = new ArrayList<Integer>();
		for (int d: candidates) {
			possibilities.add(d);
		}
		// Eliminate other digits.
		for (int d: possibilities) {
			if (d != digit && !eliminate(grid, square, d)) {
				logger.error(String.format("assign() failed for square %s digit %d."
						, square, digit));
				return false;
			}
		}
		return true;
	}

	/**
	 * Eliminate a candidate from a square.<br>
	 * Apply strategies 1 & 2 to peers.
	 *
	 * @param grid
	 * @param square
	 * @param digit to remove from candidates
	 * @return
	 */
	private boolean eliminate(Grid grid, Square square, int digit) {		
		List<Integer> candidates = grid.getCandidates(square);
	
		if (!candidates.contains(digit)) {
			return true;
		}

		logger.debug(String.format("eliminate() digit %d from square %s."
				, digit, square));
		candidates.remove(Integer.valueOf(digit));
		
		// (1) If a square s is reduced to one value, then eliminate it from
		//     the peers.
		switch (candidates.size()) {
		case 0: // Contradiction: removed last value.
			logger.error(
					String.format("eliminate() strategy 1 failed for square "
							+ "%s digit %d, cause: no more candidates."
							, square, digit));
			return false;
		case 1:
			logger.debug(String.format("eliminate() from peers of square %s."
					, square));
			return eliminate(grid, square.getPeers(), candidates.get(0));
		}
		
		// (2) If a unit u is reduced to only one place for a value d, then put
		//     it there.
		for (Square[] unit: square.getUnits()) {
			List<Square> places = new ArrayList<>();
			for (Square s: unit) {
				if (grid.getCandidates(s).contains(digit)) {
					places.add(s);
				}
			}
			switch (places.size()) {
			case 0: // Contradiction: no place for this digit.
				logger.error(
						String.format("eliminate() strategy 2 failed for square"
								+ " %s digit %d, cause: no places."
								, square, digit));
				logger.error(String.format("    Current unit: %s"
						, Arrays.toString(unit)));
				return false;
			case 1:
				// This digit can only be in one place in unit; assign it there.
				logger.debug(String.format("eliminate() assign digit %d to"
						+ "place %s.", digit, places.get(0)));
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
	
	/**
	 * Find a solution using brute force for the remaining candidates.
	 * @param grid
	 * @return
	 */
	private Grid search(Grid grid) {
		if (grid == null) {
			return null;
		}

		// Checks if multiple candidates exists in the grid.
		boolean ok = true;
		for (Square s: Square.asArray()) {
			ok = ok && (grid.getCandidates(s).size() == 1);
		}
		if (ok) {
			logger.info("search() brute force done, 1 candidate per square.");
			return grid;
		}

		// Using depth-first search and propagation, try all possible values.
		Square square = getSquareWithFewestCandidates(grid);
		for (int digit: grid.getCandidates(square)) {
			Grid cloned = grid.clone();
			// Try to assign this digit or jump to next one.
			if (!assign(cloned, square, digit)) {
				continue;
			}
			// If assign succeed, continue to brutalize the grid.
			if ((cloned = search(cloned)) != null) {
				return cloned;
			}
		}

		// Failed.
		return null;
	}

	private Square getSquareWithFewestCandidates(Grid grid) {
		Square squareFewestCandidates = Square.A1;
		int n = 0;
		for (Square s: Square.asArray()) {
			int ns = grid.getCandidates(s).size();
			if (ns > 1 && (n == 0 || ns < n)) {
				squareFewestCandidates = s;
				n = ns;
			}
		}
		return squareFewestCandidates;
	}
}

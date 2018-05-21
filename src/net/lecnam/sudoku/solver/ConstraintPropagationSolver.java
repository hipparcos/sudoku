package net.lecnam.sudoku.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import net.lecnam.sudoku.Grid;
import net.lecnam.sudoku.Solver;
import net.lecnam.sudoku.Square;

/**
 * Solve a sudoku grid using a combination of two simple strategies and a brute-force
 * search approach.<br>
 * <br>
 * Phase 1: solve using strategies.<br>
 *   Strategies:<br>
 *   (1) If a square has only one possible value, then eliminate that value
 *       from the square's peers.<br>
 *   (2) If a unit has only one possible place for a value, then put the value
 *       there.<br>
 * <br>
 * Phase 2: solve using brute-force search.<br>
 * <br>
 * Heavily inspired by http://norvig.com/sudoku.html
 *
 * @author Adrien Aucher
 *
 */
public class ConstraintPropagationSolver implements Solver {

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
		return this.getClass().getName();
	}

	public boolean solve(Grid grid) {
		return solve(grid, this::search);
	}

	protected boolean solve(Grid grid, Function<Grid, Grid> searchFunc) {
		int[] source = grid.cloneSource();

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

		for (Square s: Square.asArray()) {
			int digit = source[s.ordinal()];
			if (digit > 0 && !assign(grid, s, digit)) {
				return false;
			}
		}

		Grid brutalized = grid;
		if ((brutalized = searchFunc.apply(grid)) == null) {
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
	protected boolean assign(Grid grid, Square square, int digit) {
		// Must copy the list of candidates before because eliminate may
		// remove some during the loop.
		List<Integer> candidates = grid.getCandidates(square);
		List<Integer> possibilities = new ArrayList<Integer>();
		possibilities.addAll(candidates);
		// Eliminate other digits.
		for (int d: possibilities) {
			if (d != digit && !eliminate(grid, square, d)) {
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
	protected boolean eliminate(Grid grid, Square square, int digit) {		
		List<Integer> candidates = grid.getCandidates(square);
	
		if (!candidates.contains(digit)) {
			return true;
		}

		candidates.remove(Integer.valueOf(digit));
		
		// (1) If a square s is reduced to one value, then eliminate it from
		//     the peers.
		switch (candidates.size()) {
		case 0: // Contradiction: removed last value.
			return false;
		case 1:
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
				return false;
			case 1:
				// This digit can only be in one place in unit; assign it there.
				if (!assign(grid, places.get(0), digit)) {
					return false;
				}
			}
		}
		
		return true;
	}

	protected boolean eliminate(Grid grid, Set<Square> set, int digit) {
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
	protected Grid search(Grid grid) {
		if (grid == null)
			return null;
		if (check(grid))
			return grid;

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

	protected boolean check(Grid grid) {
		boolean ok = true;
		for (Square s: Square.asArray()) {
			ok = ok && (grid.getCandidates(s).size() == 1);
		}
		return ok;
	}

	protected Square getSquareWithFewestCandidates(Grid grid) {
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

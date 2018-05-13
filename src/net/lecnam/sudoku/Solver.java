package net.lecnam.sudoku;

/**
 * An interface used by Grid to solve itself.<br>
 * A solver must implement this interface.
 *
 * @author Adrien Aucher
 *
 */
public interface Solver {
	/**
	 * A call to this function must solve the given grid in place.
	 *
	 * @param grid the grid to be solved
	 * @return true is solving succeeded, false otherwise
	 */
	public boolean solve(Grid grid);

	/**
	 * Must returns the name of the solver.
	 *
	 * @return the name of the solver
	 */
	public String toString();
}

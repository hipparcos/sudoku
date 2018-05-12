package net.lecnam.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * A typical sudoku grid:<br>
 *   Rows are designated by a letter.<br>
 *   Columns are designated by a number.<br>
 *   A square is the intersection of a row and a column.<br>
 *   A unit is either a row, a column, a box.<br>
 *   Peers are all squares contained in all units for a particular square.<br>
 *   Each of the squares has 3 units: a row, a column and a box.<br>
 * <br>
 * Rows count = 9<br>
 * Columns count = 9<br>
 * Squares count = 81<br>
 * Box count = 9<br>
 * Unit count per square = 3<br>
 * Peers count per square = 20<br>
 * <br>
 * Rule: A puzzle is solved if the squares in each unit are filled with a
 * permutation of the digits 1 to 9.<br>
 * <br>
 * <code>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;2&nbsp;&nbsp;3
 * &nbsp;&nbsp;&nbsp;4&nbsp;&nbsp;5&nbsp;&nbsp;6
 * &nbsp;&nbsp;&nbsp;7&nbsp;&nbsp;8&nbsp;&nbsp;9<br>  
 * - ┌──────────┬──────────┬──────────┐<br>
 * A │ A1 A2 A3 │ A4 A5 A6 │ A7 A8 A9 │<br>
 * B │ B1 B2 B3 │ B4 B5 B6 │ B7 B8 B9 │<br>
 * C │ C1 C2 C3 │ C4 C5 C6 │ C7 C8 C9 │<br>
 * - ├──────────┼──────────┼──────────┤<br>
 * D │ D1 D2 D3 │ D4 D5 D6 │ D7 D8 D9 │<br>
 * E │ E1 E2 E3 │ E4 E5 E6 │ E7 E8 E9 │<br>
 * F │ F1 F2 F3 │ F4 F5 F6 │ F7 F8 F9 │<br>
 * - ├──────────┼──────────┼──────────┤<br>
 * G │ G1 G2 G3 │ G4 G5 G6 │ G7 G8 G9 │<br>
 * H │ H1 H2 H3 │ H4 H5 H6 │ H7 H8 H9 │<br>
 * I │ I1 I2 I3 │ I4 I5 I6 │ I7 I8 I9 │<br>
 * - └──────────┴──────────┴──────────┘<br>
 * </code>
 * @author Adrien Aucher
 * 
 */
public class Grid {

	/**
	 * The original grid to solve.
	 */
	private int[] source;
	/**
	 * The proposed solution.
	 */
	private int[] solution;
	/**
	 * A list of candidates for each square.
	 * To be used by solvers.
	 */
	private Vector<List<Integer>> candidates;

	private final String rowSeperator = "-";
	private final String colSeparator = "|";
	private final String comment = "#";
	
	/**
	 * Use this flag to render grid with separators.
	 * Can't be use along with FLAG_INLINE.
	 */
	public static final int FLAG_DECORATE = 1<<1;
	/**
	 * Use this flag to render the grid in one line.
	 */
	public static final int FLAG_INLINE = 1<<2;
	/**
	 * Use this flag to render the list of candidates.
	 * Can't be use along with FLAG_INLINE.
	 */
	public static final int FLAG_DEBUG = 1<<3;

	public Grid() {
		source = new int[Square.SIZE];
		solution = new int[Square.SIZE];
		candidates = new Vector<>(Square.SIZE);
		for (int i = 0; i < Square.SIZE; i++) {
			candidates.add(new ArrayList<Integer>());
		}
	}

	/**
	 * Tells if a the possible values list of a given square will be taken into
	 * account when telling if the grid is solved.
	 * 
	 * @param square
	 * @return
	 */
	public boolean isModifiable(Square square) {
		return source[square.ordinal()] == 0;
	}
	
	public int[] cloneSource() {
		return source.clone();
	}
	
	public void setSolution(int[] solution) {
		if (solution == null || solution.length != Square.SIZE) {
			return;
		}
		
		this.solution = solution;
	}
	
	public List<Integer> getCandidates(Square square) {
		return candidates.get(square.ordinal());
	}
	
	public boolean solve(Solver solver) {
		if (!solver.solve(this)) {
			System.out.println(solver + " can't solve this grid.");
			try {
				debug(new PrintWriter(System.out));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		return isSolved();
	}

	/**
	 * Tells is the grid is solved in the current state. The grid is solved when
	 * each units is the permutation of 1 to 9 digits.
	 * 
	 * @return
	 */
	public boolean isSolved() {
		/* 
		 * This function uses a binary set to track square values. Each
		 * digit that is present in a unit is set to 1 at the corresponding
		 * bit position.
		 */
		
		// This variable represents a valid unit.
		// For a 9x9 sudoku: valid = 0b111111111.
		int valid = 0;
		for (int i = 0; i < Square.SQUARE_MAX_VALUE; i++) {
			valid = (valid << 1) | 0x1;
		}
		
		// Checks if each units has exactly each digits once.
		for (Square[] unit: Square.getAllUnits()) {
			int check = 0;
			for (int i = 0; i < unit.length; i++) {
				int value = getValue(unit[i]);
				if (value > 0) {
					// Set the corresponding digit in the check flag.
					check |= 0x1 << (value - 1);
				}
			}
			if (check != valid) {
				return false;
			}
		}
		return true;
	}
	
	private int getValue(Square square) {
		int idx = square.ordinal();
		if (source[idx] > 0) {
			return source[idx];
		}
		return solution[idx];
	}

	public boolean read(Reader r) throws IOException {
		return read(new BufferedReader(r), false);
	}
	
	public boolean read(Reader r, boolean inline) throws IOException {
		return read(new BufferedReader(r), inline);
	}

	public boolean read(BufferedReader r, boolean inline) throws IOException {
		reset();

		String line = null;
		int col = 0, row = 0;
		boolean assignment = false;
		while (row < Square.ROW_COUNT && (line = r.readLine()) != null) {
			String trimmed = line.trim();
			// Skip blank line.
			if (trimmed.length() == 0)
				continue;
			// Skip line if it's a comment.
			if (trimmed.startsWith(comment))
				continue;
			// Fill source array.
			for (int i = 0; i < trimmed.length() && row < Square.ROW_COUNT; i++) {
				int value = trimmed.charAt(i) - '0';
				int idx = Square.GridCoordToLinear(col, row);
				if (value > 0 && value <= Square.SQUARE_MAX_VALUE) {
					source[idx] = value;
					assignment = true;
				} else {
					source[idx] = 0;
				}
				col++;
				if (inline && col >= Square.COL_COUNT) {
					col = 0; row++;
				}
				if (!inline && col >= Square.COL_COUNT) {
					col = 0;
					break;
				}
			}
			if (!inline)
				row++;
		}
		return assignment;
	}

	private void reset() {
		solution = new int[Square.SIZE];
		for (List<Integer> c: candidates) {
			c.clear();
		}
	}
	
	public void debug(Writer w) throws IOException {
		write(w, FLAG_DEBUG|FLAG_DECORATE);
	}

	public void write(Writer w) throws IOException {
		write(w, FLAG_DECORATE);
	}

	public void write(Writer w, int flags) throws IOException {
		boolean inline   = 0 < (flags & FLAG_INLINE);
		boolean decorate = 0 < (flags & FLAG_DECORATE) && !inline;
		boolean debug    = 0 < (flags & FLAG_DEBUG) && !inline;
		
		String formatSquare, formatEmpty;
		if (decorate && !debug) {
			formatSquare = " %d ";
			formatEmpty = "   ";
		} else {
			formatSquare = "%d";
			formatEmpty = ".";
		}
		
		// Used in debug mode to center candidates.
		int width = 0;
		if (debug) {
			for (List<Integer> l: candidates)
				width = Math.max(width, l.size());
				width++;
		}

		String line = String.join("", // Line decorator.
				Collections.nCopies(
						((width > 0) ? width : 3) * Square.COL_COUNT
						+ Square.COL_COUNT / Square.BOX_SIZE + 1
						, rowSeperator));
		
		// Print grid.
		for (int row = 0; row < Square.ROW_COUNT; row++) {
			if (row % Square.BOX_SIZE == 0) {
				if (decorate)
					w.write(line);
				if (!inline)
					w.write("\n");
			}
			for (int col = 0; col < Square.COL_COUNT; col++) {
				if (decorate && col % Square.BOX_SIZE == 0) {
					w.write(colSeparator);
				}
				
				int idx = Square.GridCoordToLinear(col, row);
				List<Integer> digits = candidates.get(idx);
				
				// In debug mode to center values.
				int nspacebef = 0, nspaceaft = 0;
				if (debug) {
					int size = digits.size();
					if (source[idx] > 0)
						size = 1;
					nspacebef = (width - size) / 2;
					nspaceaft = width - nspacebef - size;
				}
				for (int s = 0; s < nspacebef; s++)	
					w.append(" ");
				
				// Print value(s).
				if (source[idx] > 0) {
					w.write(String.format(formatSquare, source[idx]));
				} else if (solution[idx] > 0) {
					w.write(String.format(formatSquare, solution[idx]));
				} else if (debug && !digits.isEmpty()) {
					for (int d: digits)
						w.write(String.format(formatSquare, d));
				} else {
					w.write(formatEmpty);
				}
				
				// In debug mode to center values.
				for (int s = 0; s < nspaceaft; s++)	
					w.append(" ");
			}
			if (decorate)
				w.write(colSeparator);
			if (!inline)
				w.write("\n");
		}
		if (decorate)
			w.write(line);
		if (!inline)
			w.write("\n");
		
		w.flush();
	}
}

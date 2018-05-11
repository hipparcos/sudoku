package net.lecnam.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;

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
	 * The list of candidates for each squares.
	 */
	private int[] candidates;

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

	public Grid() {
		source = new int[Square.SIZE];
		candidates = new int[Square.SIZE];
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
		return candidates[idx];
	}
	
	public void read(Reader r) throws IOException {
		read(r, false);
	}

	public void read(Reader r, boolean inline) throws IOException {
		BufferedReader br = new BufferedReader(r);
		String line = null;
		int col = 0, row = 0;
		while ((line = br.readLine()) != null && row < Square.ROW_COUNT) {
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
				if (value > 0 && value <= Square.SQUARE_MAX_VALUE) {
					int idx = Square.GridCoordToLinear(col, row);
					source[idx] = value;
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
	}

	public void write(Writer w) throws IOException {
		write(w, FLAG_DECORATE);
	}

	public void write(Writer w, int flags) throws IOException {
		String line = String.join("", // Line decorator.
				Collections.nCopies(3 * Square.COL_COUNT + Square.COL_COUNT / Square.BOX_SIZE + 1, rowSeperator));

		boolean inline =   0 < (flags & FLAG_INLINE);
		boolean decorate = 0 < (flags & FLAG_DECORATE) && !inline;
		
		String formatSquare, formatEmpty;
		if (decorate) {
			formatSquare = " %d ";
			formatEmpty = "   ";
		} else {
			formatSquare = "%d";
			formatEmpty = ".";
		}

		for (int col = 0; col < Square.COL_COUNT; col++) {
			if (col % Square.BOX_SIZE == 0) {
				if (decorate)
					w.write(line);
				if (!inline)
					w.write("\n");
			}
			for (int row = 0; row < Square.ROW_COUNT; row++) {
				if (decorate && row % Square.BOX_SIZE == 0) {
					w.write(colSeparator);
				}
				int idx = Square.GridCoordToLinear(col, row);
				if (source[idx] > 0) {
					w.write(String.format(formatSquare, source[idx]));
				} else if (candidates[idx] > 0) {
					w.write(String.format(formatSquare, candidates[idx]));
				} else {
					w.write(formatEmpty);
				}
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

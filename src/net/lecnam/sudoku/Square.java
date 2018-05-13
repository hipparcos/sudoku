package net.lecnam.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * A square is the intersection between a row & a column in a sudoku grid.
 * This enumeration provides utility methods to deal with squares.
 * 
 * Inspired by http://norvig.com/sudoku.html
 * 
 * @author Adrien Aucher
 *
 */
public enum Square {
	
	// Squares.
	A1, A2, A3, A4, A5, A6, A7, A8, A9,
	B1, B2, B3, B4, B5, B6, B7, B8, B9,
	C1, C2, C3, C4, C5, C6, C7, C8, C9,
	D1, D2, D3, D4, D5, D6, D7, D8, D9,
	E1, E2, E3, E4, E5, E6, E7, E8, E9,
	F1, F2, F3, F4, F5, F6, F7, F8, F9,
	G1, G2, G3, G4, G5, G6, G7, G8, G9,
	H1, H2, H3, H4, H5, H6, H7, H8, H9,
	I1, I2, I3, I4, I5, I6, I7, I8, I9;
	
	// Gird dimensions & constants.
	/**
	 * Number of squares in the grid.<br>
	 * Must have an integral square root.
	 */
	public static final int SIZE = 81;
	/**
	 * Number of columns in the grid.<br>
	 * COL_COUNT * ROW_COUNT must be equal to SIZE. 
	 */
	public static final int COL_COUNT = 9;
	/**
	 * Number of rows in the grid.<br>
	 * COL_COUNT * ROW_COUNT must be equal to SIZE.
	 */
	public static final int ROW_COUNT = 9;
	/**
	 * The size of a box.<br>
	 * Must be a divisor of SIZE.
	 */
	public static final int BOX_SIZE = 3;
	private static final int BOX_COUNT = SIZE / (BOX_SIZE * BOX_SIZE);
	private static final int BOXES_PER_ROW = COL_COUNT / BOX_SIZE;
	/**
	 * Max value of a digit of a square.
	 */
	public static final int SQUARE_MAX_VALUE = 9;
	
	// Units caches.
	private static Square[][] columns;
	private static Square[][] rows;
	private static Square[][] boxes;
	private static Vector<Set<Square>> peers;
	private static Square[] squares = Square.values();
	
	// Initializes units caches.
	static {
		// Assertions.
		assert((int) Math.pow(Math.sqrt(SIZE), 2) == SIZE);
		assert(COL_COUNT * ROW_COUNT == SIZE);
		assert(BOX_SIZE * BOX_SIZE == COL_COUNT);
		
		// Cache columns.
		columns = new Square[COL_COUNT][];
		for (int i = 0; i < COL_COUNT; i++) {
			columns[i] = squares[i].computeColumn();
		}

		// Cache rows.
		rows = new Square[ROW_COUNT][];
		for (int i = 0; i < ROW_COUNT; i++) {
			rows[i] = squares[i * COL_COUNT].computeRow();
		}
		
		// Cache boxes.
		boxes = new Square[BOX_COUNT][];
		for (int row = 0; row < ROW_COUNT; row += BOX_SIZE) {
			for (int col = 0; col < COL_COUNT; col += BOX_SIZE) {
				Square s = getSquareFromCoord(col, row);
				boxes[s.getBoxIndex()] = s.computeBox();
			}
		}

		// Cache peers.
		peers = new Vector<>(SIZE);
		for (Square s: Square.values()) {
			peers.add(s.computePeers());
		}
	}
	
	/**
	 * Returns all the squares in the column of this square. 
	 * 
	 * @return an array of all the squares of the associated column
	 */
	public Square[] getColumn() {
		int idx = getColIndex();
		return columns[idx];
	}
	
	/**
	 * Returns all the squares in the row of this square. 
	 * 
	 * @return an array of all the squares of the associated row
	 */
	public Square[] getRow() {
		int idx = getRowIndex();
		return rows[idx];
	}
	
	/**
	 * Returns all the squares in the box of this square. 
	 * 
	 * @return an array of all the squares of the associated box
	 */
	public Square[] getBox() {
		int idx = getBoxIndex();
		return boxes[idx];
	}
	
	/**
	 * Returns all units associated with this square.
	 * An array of 3 units are returned: column, row & box. 
	 * 
	 * @return an array of size 3
	 */
	public Square[][] getUnits() {
		Square[][] units = new Square[3][];
		units[0] = getColumn();
		units[1] = getRow();
		units[2] = getBox();
		return units;
	}
	
	/**
	 * Returns a list of all the units of the grid.
	 * Return all the columns, rows & boxes.
	 * 
	 * @return list of all the units of the grid
	 */
	public static List<Square[]> getAllUnits() {
		List<Square[]> units = new ArrayList<>();
		units.addAll(Arrays.asList(columns));
		units.addAll(Arrays.asList(rows));
		units.addAll(Arrays.asList(boxes));
		return units;
	}
	
	/**
	 * Returns all squares contained in the units associated with this square.
	 * 
	 * @return a set of all the squares related to this square.
	 */
	public Set<Square> getPeers() {
		return peers.get(this.ordinal());
	}

	/**
	 * Returns an array containing all squares.
	 * Must be considered as read-only.
	 * 
	 * @return an array of all squares
	 */
	public static Square[] asArray() {
		return squares;
	}

	/**
	 * Returns a square from its coordinates.
	 * 
	 * @param col column index (0 indexed)
	 * @param row row index (0 indexed)
	 * @return the corresponding square
	 */
	public static Square getSquareFromCoord(int col, int row) {
		return squares[row * COL_COUNT + col];
	}
	
	// Private --------------------------------------------------------------
	
	private int getColIndex() {
		return this.ordinal() % COL_COUNT;
	}
	
	private int getRowIndex() {
		return this.ordinal() / COL_COUNT;
	}
	
	private int getBoxIndex() {
		int col = getColIndex();
		int row = getRowIndex();
		int idx = (row / BOX_SIZE) * BOXES_PER_ROW + (col / BOX_SIZE);
		return idx;
	}
	
	private Square[] computeColumn() {
		int col = getColIndex();
		Square[] squares = new Square[COL_COUNT];
		for (int row = 0; row < ROW_COUNT; row++) {
			squares[row] = getSquareFromCoord(col, row);
		}
		return squares;
	}
	private Square[] computeRow() {
		int row = getRowIndex();
		Square[] squares = new Square[ROW_COUNT];
		for (int col = 0; col < COL_COUNT; col++) {
			squares[col] = getSquareFromCoord(col, row);
		}
		return squares;
	}
	
	private Square[] computeBox() {
		Square[] box = new Square[BOX_COUNT];
		int startCol = getColIndex() / BOX_SIZE * BOX_SIZE;
		int startRow = getRowIndex() / BOX_SIZE * BOX_SIZE;
		for (int row = 0; row < BOX_SIZE; row++) {
			for (int col = 0; col < BOX_SIZE; col++) {
				box[row * BOX_SIZE + col] =
						getSquareFromCoord(startCol + col, startRow + row);
			}
		}
		return box;
	}
	
	private Set<Square> computePeers() {
		Set<Square> set = new TreeSet<>();
		for (Square[] u: getUnits()) {
			set.addAll(Arrays.asList(u));
		}
		set.remove(this);
		return set;
	}

}
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
	
	// Dimensions.
	public static final int SIZE = 81;
	public static final int COL_COUNT = 9;
	public static final int ROW_COUNT = 9;
	public static final int BOX_SIZE = 3;
	public static final int BOX_COUNT = SIZE / (BOX_SIZE * BOX_SIZE);
	public static final int SQUARE_MAX_VALUE = 9;
	
	// Units caches.
	private static Square[][] columns;
	private static Square[][] rows;
	private static Square[][] boxes;
	private static Vector<Set<Square>> peers;
	private static Square[] squares = Square.values();
	
	// Initializes units caches.
	static {
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
		int box = 0;
		for (int col = 0; col < COL_COUNT; col += BOX_SIZE) {
			for (int row = 0; row < ROW_COUNT; row += BOX_SIZE) {
				boxes[box] = getSquareFromGridCoord(col, row).computeBox();
				box++;
			}
		}

		// Cache peers.
		peers = new Vector<>(SIZE);
		for (Square s: Square.values()) {
			peers.add(s.computePeers());
		}
	}
	
	/**
	 * Get all the squares in the column of this square. 
	 * 
	 * @return an array of all the squares of the associated column
	 */
	public Square[] getColumn() {
		int idx = getColumnIndex();
		return columns[idx];
	}
	
	/**
	 * Get all the squares in the row of this square. 
	 * 
	 * @return an array of all the squares of the associated row
	 */
	public Square[] getRow() {
		int idx = getRowIndex();
		return rows[idx];
	}
	
	/**
	 * Get all the squares in the box of this square. 
	 * 
	 * @return an array of all the squares of the associated box
	 */
	public Square[] getBox() {
		int idx = getBoxIndex();
		return boxes[idx];
	}
	
	/**
	 * Get all units associated with this square.
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
	 * Get a list of all the units of the grid.
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
	 * Get all squares contained in the units associated with this square.
	 * 
	 * @return a set of all the squares related to this square.
	 */
	public Set<Square> getPeers() {
		return peers.get(this.ordinal());
	}
	
	public static Square indexToSquare(int idx) {
		return squares[idx];
	}
	
	public static int GridCoordToLinear(int col, int row) {
		return row * COL_COUNT + col;
	}
	
	// Private --------------------------------------------------------------
	
	private int getColumnIndex() {
		return this.ordinal() % COL_COUNT;
	}
	
	private int getRowIndex() {
		return this.ordinal() / COL_COUNT;
	}
	
	private static Square getSquareFromGridCoord(int col, int row) {
		return squares[GridCoordToLinear(col, row)];
	}
	
	private static int GridCoordToLinear(int col, int row, int size) {
		return row * size + col;
	}
	
	private int getBoxIndex() {
		int col = getColumnIndex();
		int row = getRowIndex();
		int idx = GridCoordToLinear(col / BOX_SIZE, row / BOX_SIZE, BOX_SIZE);
		return idx;
	}
	
	private int getBoxStartCol() {
		int idx = getBoxIndex();
		return (idx % BOX_SIZE) * BOX_SIZE;
	}
	
	private int getBoxStartRow() {
		int idx = getBoxIndex();
		return (idx / BOX_SIZE) * BOX_SIZE;
	}
	
	private Square[] computeColumn() {
		int col = getColumnIndex();
		Square[] squares = new Square[COL_COUNT];
		for (int row = 0; row < ROW_COUNT; row++) {
			squares[row] = getSquareFromGridCoord(col, row);
		}
		return squares;
	}
	private Square[] computeRow() {
		int row = getRowIndex();
		Square[] squares = new Square[ROW_COUNT];
		for (int col = 0; col < COL_COUNT; col++) {
			squares[col] = getSquareFromGridCoord(col, row);
		}
		return squares;
	}
	
	private Square[] computeBox() {
		Square[] box = new Square[BOX_SIZE * BOX_SIZE];
		for (int row = 0; row < BOX_SIZE; row++) {
			for (int col = 0; col < BOX_SIZE; col++) {
				box[GridCoordToLinear(col, row, BOX_SIZE)] =
						getSquareFromGridCoord(getBoxStartCol() + col, getBoxStartRow() + row);
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
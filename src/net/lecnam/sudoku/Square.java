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
	
	/**
	 * Get all the squares in the column of a given square. 
	 * 
	 * @param square
	 * @return an array of all the squares of the corresponding column
	 */
	public static Square[] getColumn(Square square) {
		cacheColumns();
		int idx = getColumnIndex(square);
		return columnsCache[idx];
	}
	
	/**
	 * Get all the squares in the row of a given square. 
	 * 
	 * @param square
	 * @return an array of all the squares of the corresponding row
	 */
	public static Square[] getRow(Square square) {
		cacheRows();
		int idx = getRowIndex(square);
		return rowsCache[idx];
	}
	
	/**
	 * Get all the squares in the box of a given square. 
	 * 
	 * @param square
	 * @return an array of all the squares of the corresponding box
	 */
	public static Square[] getBox(Square square) {
		cacheBoxes();
		int idx = getBoxIndex(square);
		return boxesCache[idx];
	}
	
	/**
	 * Get all units associated with a given square.
	 * An array of 3 units are returned: column, row & box. 
	 * 
	 * @param square
	 * @return an array of size 3
	 */
	public static Square[][] getUnits(Square square) {
		Square[][] units = new Square[3][];
		units[0] = getColumn(square);
		units[1] = getRow(square);
		units[2] = getBox(square);
		return units;
	}
	
	/**
	 * Get a list of all the units of the grid.
	 * Return all the columns, rows & boxes.
	 * 
	 * @return list of all the units of the grid
	 */
	public static List<Square[]> getAllUnits() {
		cacheUnits();
		List<Square[]> units = new ArrayList<>();
		units.addAll(Arrays.asList(columnsCache));
		units.addAll(Arrays.asList(rowsCache));
		units.addAll(Arrays.asList(boxesCache));
		return units;
	}
	
	/**
	 * Get all squares contained in the units associated with a given square.
	 * 
	 * @param square
	 * @return a set of all the squares related to the given square.
	 */
	public static Set<Square> getPeers(Square square) {
		cachePeers();
		return peersCache.get(square.ordinal());
	}
	
	public String toString() {
		return this.name();
	}
	
	public int toIndex() {
		return this.ordinal();
	}
	
	// Private --------------------------------------------------------------
	
	private static Square[][] columnsCache = new Square[COL_COUNT][];
	private static Square[][] rowsCache = new Square[COL_COUNT][];
	private static Square[][] boxesCache = new Square[BOX_COUNT][];
	private static Vector<Set<Square>> peersCache = new Vector<>(SIZE);
	private static Square[] squaresAsArray = Square.values();
	
	private static int getColumnIndex(Square square) {
		return square.ordinal() % COL_COUNT;
	}
	
	private static int getRowIndex(Square square) {
		return square.ordinal() / COL_COUNT;
	}
	
	private static Square getSquareFromCoord(int col, int row) {
		return squaresAsArray[CoordToLinear(col, row)];
	}
	
	public static int CoordToLinear(int col, int row) {
		return row * COL_COUNT + col;
	}
	
	private static int CoordToLinear(int col, int row, int size) {
		return row * size + col;
	}
	
	private static int getBoxIndex(Square square) {
		int col = getColumnIndex(square);
		int row = getRowIndex(square);
		int idx = CoordToLinear(col / BOX_SIZE, row / BOX_SIZE, BOX_SIZE);
		return idx;
	}
	
	private static int getBoxStartCol(Square square) {
		int idx = getBoxIndex(square);
		return (idx % BOX_SIZE) * BOX_SIZE;
	}
	
	private static int getBoxStartRow(Square square) {
		int idx = getBoxIndex(square);
		return (idx / BOX_SIZE) * BOX_SIZE;
	}
	
	private static Square[] _getColumn(Square square) {
		int col = getColumnIndex(square);
		Square[] squares = new Square[COL_COUNT];
		for (int row = 0; row < ROW_COUNT; row++) {
			squares[row] = getSquareFromCoord(col, row);
		}
		return squares;
	}
	public static Square[] _getRow(Square square) {
		int row = getRowIndex(square);
		Square[] squares = new Square[ROW_COUNT];
		for (int col = 0; col < COL_COUNT; col++) {
			squares[col] = getSquareFromCoord(col, row);
		}
		return squares;
	}
	
	private static Square[] _getBox(Square square) {
		Square[] box = new Square[BOX_SIZE * BOX_SIZE];
		for (int row = 0; row < BOX_SIZE; row++) {
			for (int col = 0; col < BOX_SIZE; col++) {
				box[CoordToLinear(col, row, BOX_SIZE)] =
						getSquareFromCoord(getBoxStartCol(square) + col, getBoxStartRow(square) + row);
			}
		}
		return box;
	}
	
	public static Set<Square> _getPeers(Square square) {
		Set<Square> set = new TreeSet<>();
		for (Square[] u: getUnits(square)) {
			set.addAll(Arrays.asList(u));
		}
		set.remove(square);
		return set;
	}
	
	private static void cacheColumns() {
		if (columnsCache[0] == null) {
			for (int i = 0; i < COL_COUNT; i++) {
				columnsCache[i] = _getColumn(squaresAsArray[i]);
			}
		}
	}
	
	private static void cacheRows() {
		if (rowsCache[0] == null) {
			for (int i = 0; i < ROW_COUNT; i++) {
				rowsCache[i] = _getRow(squaresAsArray[i * COL_COUNT]);
			}
		}
	}
	
	private static void cacheBoxes() {
		if (boxesCache[0] == null) {
			int box = 0;
			for (int col = 0; col < COL_COUNT; col += BOX_SIZE) {
				for (int row = 0; row < ROW_COUNT; row += BOX_SIZE) {
					boxesCache[box] = _getBox(getSquareFromCoord(col, row));
					box++;
				}
			}
		}
	}
	
	private static void cacheUnits() {
		cacheColumns();
		cacheRows();
		cacheBoxes();
	}
	
	private static void cachePeers() {
		if (peersCache.size() == 0) {
			for (Square s: Square.values()) {
				peersCache.add(_getPeers(s));
			}
		}
	}
}
package net.lecnam.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * A typical sudoku grid:
 *   Rows are designated by a letter.
 *   Columns are designated by a number.
 *   A square is the intersection of a row and a column.
 *   A unit is either a row, a column, a box.
 *   Peers are all squares contained in all units for a particular square. 
 *   Each of the squares has 3 units: a row, a column and a box.
 *   
 *   Rows count = 9
 *   Columns count = 9
 *   Squares count = 81
 *   Box count = 9
 *   Unit count per square = 3
 *   Peers count per square = 20
 * 
 * Rule:
 *   A puzzle is solved if the squares in each unit are filled with a permutation of the digits 1 to 9.
 * 
 *       1  2  3    4  5  6    7  8  9
 *    ┌──────────┬──────────┬──────────┐
 *  A │ A1 A2 A3 │ A4 A5 A6 │ A7 A8 A9 │
 *  B │ B1 B2 B3 │ B4 B5 B6 │ B7 B8 B9 │
 *  C │ C1 C2 C3 │ C4 C5 C6 │ C7 C8 C9 │
 *    ├──────────┼──────────┼──────────┤
 *  D │ D1 D2 D3 │ D4 D5 D6 │ D7 D8 D9 │
 *  E │ E1 E2 E3 │ E4 E5 E6 │ E7 E8 E9 │
 *  F │ F1 F2 F3 │ F4 F5 F6 │ F7 F8 F9 │
 *    ├──────────┼──────────┼──────────┤
 *  G │ G1 G2 G3 │ G4 G5 G6 │ G7 G8 G9 │
 *  H │ H1 H2 H3 │ H4 H5 H6 │ H7 H8 H9 │
 *  I │ I1 I2 I3 │ I4 I5 I6 │ I7 I8 I9 │
 *    └──────────┴──────────┴──────────┘
 *
 * @author Adrien Aucher
 * 
 */
public class Grid {
	
	private Vector<List<Integer>> grid;
	private final String rowSeperator = "-";
	private final String colSeparator = "|";
	private final String comment = "#";
	
	public Grid() {
		grid = new Vector<>(Square.SIZE);
		for (int i = 0; i < Square.SIZE; i++) {
			grid.add(new ArrayList<Integer>());
		}
	}
	
	public void read(Reader r) throws IOException {
		read(r, "\n");
	}
	
	public void read(Reader r, String separator) throws IOException {
		BufferedReader br = new BufferedReader(r);
		String line = null;
		int row = 0;
        while((line = br.readLine()) != null && row < Square.ROW_COUNT) {
            String trimmed = line.trim();
            if (!trimmed.startsWith(comment)) {
            	for (int col = 0; col < Square.COL_COUNT; col++) {
            		int value = trimmed.charAt(col) - '0';
            		if (value >= 1 && value <= 9) {
	            		List<Integer> l = grid.get(Square.CoordToLinear(col, row));
	            		l.clear();
	            		l.add(value);
            		}
            	}
            	row++;
            }
        }
	}
	
	public void write(Writer w) throws IOException {
		String line = String.join("",
				Collections.nCopies(3 * Square.COL_COUNT + Square.COL_COUNT / Square.BOX_SIZE + 1, rowSeperator));
		for (int col = 0; col < Square.COL_COUNT; col++) {
			if (col % Square.BOX_SIZE == 0) {
				w.write(line);
				w.write("\n");
			}
			for (int row = 0; row < Square.ROW_COUNT; row++) {
				if (row % Square.BOX_SIZE == 0) {
					w.write(colSeparator);
				}
				List<Integer> l = grid.get(Square.CoordToLinear(col, row));
				if (!l.isEmpty()) {
					w.write(String.format(" %d ", l.get(0)));
				} else {
					w.write("   ");
				}
			}
			w.write(colSeparator);
			w.write("\n");
		}
		w.write(line);
		w.write("\n");
		w.flush();
	}
	
	public boolean solved() {
		return false;
	}
}

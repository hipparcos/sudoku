package net.lecnam.sudoku.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.lecnam.sudoku.Square;

class SquareTest {
	@Test
	void test_all() {
		assertEquals(81, Square.values().length);
	}
	
	@Test
	void test_getAllUnits() {
		assertEquals(27, Square.getAllUnits().size());
	}
	
	@Test
	void test_getUnits() {
		Square[][] unitsForC2 =
			{{Square.A2, Square.B2, Square.C2, Square.D2, Square.E2, Square.F2, Square.G2, Square.H2, Square.I2},
	         {Square.C1, Square.C2, Square.C3, Square.C4, Square.C5, Square.C6, Square.C7, Square.C8, Square.C9},
	         {Square.A1, Square.A2, Square.A3, Square.B1, Square.B2, Square.B3, Square.C1, Square.C2, Square.C3}};
		
	    assertArrayEquals(unitsForC2, Square.getUnits(Square.C2));
	    
		Square[][] unitsForE5 =
			{{Square.A5, Square.B5, Square.C5, Square.D5, Square.E5, Square.F5, Square.G5, Square.H5, Square.I5},
	         {Square.E1, Square.E2, Square.E3, Square.E4, Square.E5, Square.E6, Square.E7, Square.E8, Square.E9},
	         {Square.D4, Square.D5, Square.D6, Square.E4, Square.E5, Square.E6, Square.F4, Square.F5, Square.F6}};
		
	    assertArrayEquals(unitsForE5, Square.getUnits(Square.E5));
	}
	
	@Test
	void test_getPeers() {
	    Set<Square> peersForC2 = new HashSet<>();
	    Square[] C2 = {Square.A2, Square.B2, Square.D2, Square.E2, Square.F2, Square.G2, Square.H2, Square.I2,
	    		       Square.C1, Square.C3, Square.C4, Square.C5, Square.C6, Square.C7, Square.C8, Square.C9,
	    		       Square.A1, Square.A3, Square.B1, Square.B3};
		peersForC2.addAll(Arrays.asList(C2));
		assertTrue(Square.getPeers(Square.C2).equals(peersForC2));
		
	    Set<Square> peersForE5 = new HashSet<>();
	    Square[] E5 = {Square.A5, Square.B5, Square.C5, Square.D5, Square.F5, Square.G5, Square.H5, Square.I5,
	    		       Square.E1, Square.E2, Square.E3, Square.E4, Square.E6, Square.E7, Square.E8, Square.E9,
	    		       Square.D4, Square.D6, Square.F4, Square.F6};
	    peersForE5.addAll(Arrays.asList(E5));
		assertTrue(Square.getPeers(Square.E5).equals(peersForE5));
	}

}

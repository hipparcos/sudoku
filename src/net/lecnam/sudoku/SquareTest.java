package net.lecnam.sudoku;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

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
		
	    assertArrayEquals(unitsForC2, Square.C2.getUnits(), "unitsForC2");
	    
		Square[][] unitsForE8 =
			{{Square.A8, Square.B8, Square.C8, Square.D8, Square.E8, Square.F8, Square.G8, Square.H8, Square.I8},
	         {Square.E1, Square.E2, Square.E3, Square.E4, Square.E5, Square.E6, Square.E7, Square.E8, Square.E9},
	         {Square.D7, Square.D8, Square.D9, Square.E7, Square.E8, Square.E9, Square.F7, Square.F8, Square.F9}};
		
	    assertArrayEquals(unitsForE8, Square.E8.getUnits(), "unitsForE8");
	}
	
	@Test
	void test_getPeers() {
	    Set<Square> peersForC2 = new HashSet<>();
	    Square[] C2 = {Square.A2, Square.B2, Square.D2, Square.E2, Square.F2, Square.G2, Square.H2, Square.I2,
	    		       Square.C1, Square.C3, Square.C4, Square.C5, Square.C6, Square.C7, Square.C8, Square.C9,
	    		       Square.A1, Square.A3, Square.B1, Square.B3};
		peersForC2.addAll(Arrays.asList(C2));
		assertTrue(Square.C2.getPeers().equals(peersForC2), "peersForC2");
		
	    Set<Square> peersForE8 = new HashSet<>();
	    Square[] E8 = {Square.A8, Square.B8, Square.C8, Square.D8, Square.F8, Square.G8, Square.H8, Square.I8,
	    		       Square.E1, Square.E2, Square.E3, Square.E4, Square.E5, Square.E6, Square.E7, Square.E9,
	    		       Square.D7, Square.D9, Square.F7, Square.F9};
	    peersForE8.addAll(Arrays.asList(E8));
		assertTrue(Square.E8.getPeers().equals(peersForE8), "peersForE8");
	}

}

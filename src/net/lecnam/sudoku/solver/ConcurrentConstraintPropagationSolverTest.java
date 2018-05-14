package net.lecnam.sudoku.solver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.lecnam.sudoku.Solver;

class ConcurrentConstraintPropagationSolverTest extends SolverTester {

	private Solver solver = null;

	@BeforeEach
	void setup() {
		solver = new ConcurrentConstraintPropagationSolver();
	}

	@Test
	void test_easy() {
		testString(solver, "easy1", easy1, easy1solved);
	}

	@Test
	void test_easyAll() {
		testStringList(solver, easy50);
	}

	@Test
	void test_hardAll() {
		testStringList(solver, hard95);
	}

	@Test
	void test_hardestAll() {
		testStringList(solver, hardest11);
	}

}

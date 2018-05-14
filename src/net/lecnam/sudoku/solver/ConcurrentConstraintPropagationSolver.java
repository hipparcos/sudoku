package net.lecnam.sudoku.solver;

import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.lecnam.sudoku.Grid;
import net.lecnam.sudoku.Solver;
import net.lecnam.sudoku.Square;

public class ConcurrentConstraintPropagationSolver
	extends ConstraintPropagationSolver implements Solver {

	public String toString() {
		return this.getClass().getName();
	}

	public boolean solve(Grid grid) {
		return super.solve(grid, this::brutalize);
	}

	private Grid brutalize(Grid grid) {
		if (grid == null)
			return null;
		if (check(grid))
			return grid;

		ExecutorService executor = Executors.newWorkStealingPool();
		Vector<CompletableFuture<Grid>> futures = new Vector<>();
		
		Square square = getSquareWithFewestCandidates(grid);
		for (int digit: grid.getCandidates(square)) {
			CompletableFuture<Grid> future = new CompletableFuture<Grid>();
			executor.submit(() -> {
				Grid cloned = grid.clone();
				// Try to assign this digit or jump to next one.
				if (!assign(cloned, square, digit)) {
					// future is never completed.
					return;
				}
				// If assign succeed, continue to brutalize the grid.
				if ((cloned = search(cloned)) != null) {
					future.complete(cloned);
					return;
				}
				// Fail.
				// future is never completed.
			});
			futures.add(future);
		}

	    @SuppressWarnings("rawtypes")
		CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);
	    CompletableFuture<Grid> allFutures = CompletableFuture.anyOf(cfs)
	            .thenApply((r) -> {
	            	futures.stream()
            			.filter((f) -> !f.isDone())
            			.map((f) -> f.cancel(true));
	            	return futures.stream()
	            			.filter((f) -> f.isDone() && !f.isCancelled())
	            			.map(CompletableFuture::join)
	            			.findFirst()
	            			.orElse(null);
	            });

	    executor.shutdown();
	    
	    try {
	    	return allFutures.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return null;
	}

}

# sudoku

A sudoku solver developed in Java.
I developed it as required to complete the NFP136 module of my computer science evening class at IPST CNAM.

## Build

This project has been developed & build using Eclipse.
Basic workflow involves the export of a runnable jar named sudoku.jar in the project /bin/ directory.

## Usage

Input: the first argument must be a file containing a sudoku grid.
Output: the completed sudoku grid.

```
java -jar bin/sudoku.jar test/grid.txt
```

## Principle

The solver solves a sudoku grid using a combination of two simple strategies and a brute-force search approach.

Phase 1: solve using strategies.
Strategies:
  (1) If a square has only one possible value, then eliminate that value
      from the square's peers.
  (2) If a unit has only one possible place for a value, then put the value
      there.

Phase 2: solve using brute-force search.

Heavily inspired by http://norvig.com/sudoku.html
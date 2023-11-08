/*
 * AUTHORS: Anisha Munjal, Anupama Hazarika, Jenan Meri
 * FILE: Grammar.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372 Fall 2023
 * Description: This file contains the Nonterminal symbol class. 
 */
public class Nonterminal {
	private String symbol;

	/**
	 * Default Constructor
	 * 
	 * @param symbol, the string symbol
	 */
	public Nonterminal(String symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * Returns the String representation of the non-terminal symbol
	 */
	public String toString() {
		return symbol;
	}

}

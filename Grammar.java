
/*
 * AUTHORS: Anisha Munjal, Anupama Hazarika, Jenan Meri
 * FILE: Grammar.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372 Fall 2023
 * Description: This file contains the grammar class. It reads the grammar 
 * file and builds HashMap of non-terminal symbols to production
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// reads grammar file, builds HashMap of non-terminal symbols to production
public class Grammar {
	private static final String grammar = "grammar.txt";

	// Default Constructor
	public Grammar() {
	}

	/**
	 * Main method
	 * 
	 * @param args (not used here)
	 */
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(grammar));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			System.out.println(line);

			String[] tokens = line.split(" ");
			System.out.println("Production for Nonterminal Symbol: " + tokens[0] + " is:");
			for (int i = 0; i < tokens.length; i++) {
				System.out.print(tokens[i] + " ");
			}
			System.out.println();
			System.out.println();
		}
	}
}

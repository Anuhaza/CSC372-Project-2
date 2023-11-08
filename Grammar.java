
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

// reads grammar file, builds HashMap of non-terminal symbols to production
public class Grammar {
	private static final String grammar = "grammar.txt";
	private static HashMap<Nonterminal, LinkedList<Nonterminal>> map = new HashMap<>();
	private static boolean debugGrammar = true;

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

			String[] tokens = line.split(" ");
			LinkedList<Nonterminal> list = new LinkedList<>();
			Nonterminal symbol = null;

			if (!tokens[1].equals("::=")) {
				System.out.println("Error in grammar file. Aborting!");
				return;
			}

			for (int i = 0; i < tokens.length; i++) {

				if (!tokens[i].equals("::=")) {
					if (i == 0) {
						// non-terminal symbol definition
						symbol = new Nonterminal(tokens[i]);
						map.put(symbol, list);
					} else {

						// add to LinkedList
						list.add(new Nonterminal(tokens[i]));
					}

				}
			}
			System.out.println();
			if (debugGrammar) {
				System.out.print(symbol + " ::= ");
				for (int i = 0; i < list.size(); i++) {
					System.out.print("Q[" + i + "]:" + list.get(i) + " ");
				}
			}
		}
	}
}

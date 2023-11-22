
/*
 * AUTHORS: Anisha Munjal, Anupama Hazarika, Jenan Meri
 * FILE: Grammar.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372 Fall 2023
 * Description: This file contains the grammar class. It reads the grammar 
 * file and builds HashMap of non-terminal symbols to production. It can
 * detect errors in the grammar file.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

// reads grammar file, builds HashMap of non-terminal symbols to production
public class Grammar {
	private static final String filename = "grammar.txt";
	
	// HashMap containing Grammar
	// key: non-terminal symbol
	// value: production string
	private static HashMap<String, LinkedList<String>> map = new HashMap<>();
	
	// debug flag to show parsing of grammar (enabled for now)
	private static boolean showGrammar = false;
 
	/**
	 * Default Constructor
	 * @param showGrammar 
	 */
	public Grammar(boolean showGrammar) {
		this.showGrammar = showGrammar;
		parse();
	}

	/**
	 * Get Grammar HashMap
	 * 
	 * @return map, the Grammar HashMap
	 */
	public HashMap<String, LinkedList<String>> getMap() {
		return map;
	}
	
	/**
	 * Parses Grammar file to initialize HashMap containing productions
	 */
	private static void parse() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Retrieve contents of grammar file line by line
		while (scanner.hasNext()) {
			String line = scanner.nextLine();

			String[] tokens = line.split(" ");
			LinkedList<String> list = new LinkedList<>();
			Nonterminal symbol = null;

			// check for errors in grammar i.e. missing ::= in production
			if (!tokens[1].equals("::=")) {
				System.out.println("Error in grammar file. Aborting!");
				return;
			}

			// parse all words in the current production string
			for (int i = 0; i < tokens.length; i++) {

				if (!tokens[i].equals("::=")) {
					if (i == 0) {
						// non-terminal symbol definition
						map.put(tokens[i], list);
					} else {

						// add to LinkedList
						list.add(tokens[i]);
					}

				}
			}			
			// display grammar information when showGrammar is set
			if (showGrammar) {
				System.out.print(tokens[0] + " ::= ");
				for (int i = 0; i < list.size(); i++) {
					System.out.print(list.get(i) + " ");
				}
				System.out.println();
			}
		}
	}
}

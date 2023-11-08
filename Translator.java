import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * AUTHORS: Anisha Munjal, Anupama Hazarika, Jenan Meri
 * FILE: Grammar.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372 Fall 2023
 * Description: This file contains the grammar class. It reads the grammar 
 * file and builds HashMap of non-terminal symbols to production
 */
public class Translator {
	private static Grammar grammar = new Grammar();
	private static ArrayList<String> sourceFiles = new ArrayList<>();

	public Translator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Parses Source file to translate
	 */
	private static void parseSource(String filename) {
		System.out.println("\nTranslating source file: " + filename);
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		

		while (scanner.hasNext()) {
			String output = "";
			String line = scanner.nextLine();
			if (line.startsWith(":"))
				continue;

			String[] tokens = line.split(" ");
			if (tokens[0].equals("int")) {
				System.out.println(grammar.getMap().get("<intstmt>"));
				output += tokens[0];
			}

			for (int i = 0; i < tokens.length; i++) {
				System.out.print("T[" + i + "] = " + tokens[i] + ", ");
			}
			System.out.println();
			System.out.println("Translated output: " + output);
		}
	}

	/**
	 * Main method
	 * 
	 * @param args (not used here)
	 */
	public static void main(String[] args) {

		System.out.println("\nParsed " + grammar.getMap().size() + " grammar productions into HashMap.");

		// List of sourcefiles to translate
		sourceFiles.add("sample1.src");

		for (String file : sourceFiles)
			parseSource(file);

	}

}

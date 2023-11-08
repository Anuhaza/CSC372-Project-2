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
import java.util.ArrayList;
import java.util.Scanner;

public class Translator {
	private static Grammar grammar = new Grammar();
	private static ArrayList<String> sourceFiles = new ArrayList<>();

	/**
	 * Default constructor
	 */
	public Translator() {

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
			}

			for (int i = 0; i < tokens.length; i++) {
				System.out.print("T[" + i + "] = " + tokens[i] + ", ");
				
				if (tokens[i].equals("->")) {
					output += "= ";
					continue;
				}
				if (tokens[i].equals("add")) {
					output += "+ ";
					continue;
				}
				if (tokens[i].equals("sub")) {
					output += "- ";
					continue;
				}
				if (tokens[i].equals("div")) {
					output += "/ ";
					continue;
				}
				if (tokens[i].equals("mod")) {
					output += "% ";
					continue;
				}
				output += tokens[i] + " ";
			}
			output += ";";
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

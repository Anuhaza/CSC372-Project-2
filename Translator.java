
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
import java.io.FileWriter;
import java.io.IOException;
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
	private static String parseSource(String filename) {
		String outputStr = "";
		
		outputStr += "public class ";
		outputStr += filename.substring(0, filename.lastIndexOf('.')) + " {\n";
		outputStr += "\n\tpublic static void main(String[] args) {\n";

		System.out.println("\nTranslating source file: " + filename);

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.startsWith(":")) {
				outputStr += "\t\t//" + line.substring(1) + "\n";
				continue;
			}

			String[] tokens = line.split(" ");
			
			for (int i = 0; i < tokens.length; i++) {
				System.out.print("T[" + i + "] = " + tokens[i] + ", ");
				
				if (tokens[i].equals("int")) {
					outputStr += "\t\t";
					System.out.println(grammar.getMap().get("<intstmt>"));
					outputStr += tokens[i] + " ";
					continue;
				}
				
				if (tokens[i].equals("dec")) {
					outputStr += "\t\t";
					System.out.println(grammar.getMap().get("<decstmt>"));
					outputStr += "double" + " ";
					continue;
				}

				if (tokens[i].equals("->")) {
					outputStr += "= ";
					continue;
				}
				if (tokens[i].equals("add")) {
					outputStr += "+ ";
					continue;
				}
				if (tokens[i].equals("sub")) {
					outputStr += "- ";
					continue;
				}
				if (tokens[i].equals("div")) {
					outputStr += "/ ";
					continue;
				}
				if (tokens[i].equals("mod")) {
					outputStr += "% ";
					continue;
				}
				outputStr += tokens[i] + " ";
			}
			outputStr += ";\n";
			System.out.println();
		}

		outputStr += "\t}\n}\n";
		return outputStr;
	}

	/**
	 * Main method
	 * 
	 * @param args (not used)
	 */
	public static void main(String[] args) {

		System.out.println("\nParsed " + grammar.getMap().size() + " grammar productions into HashMap.");

		// List of sourcefiles to translate
		sourceFiles.add("sample1.src");
		sourceFiles.add("sample2.src");

		for (String file : sourceFiles) {
			String outputFileContents = parseSource(file);
			outputFile(file, outputFileContents);
		}

	}

	/**
	 * Generate output file
	 * 
	 * @param filename, the source file name
	 * @param outputFileContent, the translator string contents of the output file
	 */
	private static void outputFile(String filename, String outputFileContents) {

		int i = filename.lastIndexOf('.');
		String name = filename.substring(0, i);
		String outputFileName = name + ".java";

		try {
			FileWriter myWriter = new FileWriter(new File(outputFileName));
			myWriter.write(outputFileContents);
			myWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

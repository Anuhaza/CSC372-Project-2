
/*
 * AUTHORS: Anisha Munjal, Anupama Hazarika, Jenan Meri
 * FILE: Translator.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372 Fall 2023
 * Description: This file contains translator code for translating the source 
 * files written in the new language into java output files. The translation
 * is done using the parsed grammar.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Translator {
	private static Grammar grammar = new Grammar();
	
	// Using Arraylist for file list for now (filename will come from cmdline)
	private static ArrayList<String> sourceFiles = new ArrayList<>();
	
	// File extension for generating output files
	private static final String OUTPUT_FILE_EXTENSION = ".java";

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

		// parse the source file in the new language (line by line)
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.startsWith(":")) {
				outputStr += "\t\t//" + line.substring(1) + "\n";
				continue;
			}

			// split the line of source in new language into token strings
			String[] tokens = line.split(" ");
			
			// iterate through tokens in the source file & translate into Java
			for (int i = 0; i < tokens.length; i++) {
				System.out.print("T[" + i + "] = " + tokens[i] + ", ");
				
				// check if token equals int
				if (tokens[i].equals("int")) {
					outputStr += "\t\t";
					System.out.println(grammar.getMap().get("<intstmt>"));
					outputStr += tokens[i] + " ";
					continue;
				}
				
				// check if token equals dec
				if (tokens[i].equals("dec")) {
					outputStr += "\t\t";
					System.out.println(grammar.getMap().get("<decstmt>"));
					outputStr += "double" + " ";
					continue;
				}

				// convert -> to =
				if (tokens[i].equals("->")) {
					outputStr += "= ";
					continue;
				}
				// convert add into +
				if (tokens[i].equals("add")) {
					outputStr += "+ ";
					continue;
				}
				// convert sub into -
				if (tokens[i].equals("sub")) {
					outputStr += "- ";
					continue;
				}
				// convert div into /
				if (tokens[i].equals("div")) {
					outputStr += "/ ";
					continue;
				}
				// convert mod into %
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
		
		// TODO: file name should come via cmd line args

		System.out.println("\nParsed " + grammar.getMap().size() + " grammar productions into HashMap.");

		// List of sourcefiles to translate (for now) - use cmd line args later
		sourceFiles.add("sample1.src");
		sourceFiles.add("sample2.src");

		// parse source files (filename will come from cmd line args later)
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

		String name = filename.substring(0, filename.lastIndexOf('.'));
		String outputFileName = name + OUTPUT_FILE_EXTENSION;

		try {
			// create new output file
			FileWriter myWriter = new FileWriter(new File(outputFileName));
			
			// write the contents into the Java output file
			myWriter.write(outputFileContents);
			myWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

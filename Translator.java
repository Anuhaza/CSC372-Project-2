
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
	// private static Grammar grammar = new Grammar();

	// Using Arraylist for file list for now (filename will come from cmdline)
	private static ArrayList<String> sourceFiles = new ArrayList<>();

	// File extension for generating output files
	private static final String OUTPUT_FILE_EXTENSION = ".java";
	private static final String SYNTAX_ERROR = "Syntax Error";

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

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// parse the source file in the new language (line by line)
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.length() == 0)
				continue;

			if (line.startsWith(":")) {
				outputStr += "\t\t//" + line.substring(1) + "\n";
				continue;
			}

			MyLanguageParser parser1 = new MyLanguageParser(line);
			try {
				outputStr += "\t\t";

				// System.out.println("Parsing input line: " + line);
				if (line.startsWith("*") && line.endsWith("*")) {
					outputStr += translateFirstToken(line);
				} else {
					outputStr += parser1.parse();
				}
				outputStr += ";\n";
			} catch (Exception e) {
				System.err.println("Error parsing input 1: " + e.getMessage());
			}
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
		// Source file name comes from command line
		if (args.length == 0) {
			System.out.println("Error: Missing source filename in command line args");
			return;
		}

		String outputFileContents = parseSource(args[0]);
		System.out.println(outputFileContents);
	}

	/**
	 * Generate output file
	 * 
	 * @param filename,          the source file name
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

	/**
	 * Translate operator from new language to Java
	 * 
	 * @param op, String operator to translate
	 * @return String, the translated String operator
	 */
	public static String translateOperator(String op) {
		// convert add into +
		if (op.equals("add"))
			return "+ ";

		if (op.equals("sub"))
			return "- ";

		if (op.equals("mul"))
			return "* ";

		if (op.equals("div"))
			return "/ ";

		if (op.equals("mod"))
			return "% ";

		return SYNTAX_ERROR;
	}

	/**
	 * Translate assign from new language to Java
	 * 
	 * @param op, String assign to translate
	 * @return String, the translated String assign
	 */
	public static String translateAssign(String assign) {
		if (assign.equals("->"))
			return "= ";

		return SYNTAX_ERROR;
	}

	/**
	 * Translate boolean value from new language to Java
	 * 
	 * @param op, String boolean to translate
	 * @return String, the translated String boolean
	 */
	public static String translateBoolean(String value) {
		if (value.equals("T"))
			return "true";

		if (value.equals("F"))
			return "false";

		return SYNTAX_ERROR;
	}

	/**
	 * Translate logical operator from new language to Java
	 * 
	 * @param op, String logical operator to translate
	 * @return String, the translated String logical operator
	 */
	public static String translateLogical(String value) {
		if (value.equals("and"))
			return "&& ";

		if (value.equals("or"))
			return "|| ";

		if (value.equals("not"))
			return "!";

		return SYNTAX_ERROR;
	}

	/**
	 * Translate comparison operator from new language to Java
	 * 
	 * @param op, String comparison operator to translate
	 * @return String, the translated String comparison operator
	 */
	public static String translateComparison(String value) {
		if (value.equals("gt"))
			return "> ";

		if (value.equals("lt"))
			return "< ";

		if (value.equals("equals"))
			return "== ";

		return SYNTAX_ERROR;
	}

	/**
	 * Translate first production token from new language to Java
	 * 
	 * @param op, String first production token to translate
	 * @return String, the translated String first production token
	 */
	public static String translateFirstToken(String value) {
		if (value.equals("int"))
			return "int ";

		if (value.equals("dec"))
			return "double ";

		if (value.equals("bool"))
			return "boolean ";

		if (value.equals("string"))
			return "String ";

		if (value.startsWith("*") && value.endsWith("*")) {
			String retStr = "System.out.println(\"";
			retStr += value.substring(1, value.length() - 1);
			retStr += "\")";
			return retStr;
		}

		if (value.equals("as"))
			return "for (";
		
		return SYNTAX_ERROR;
	}

	/**
	 * Translate string token from new language to Java
	 * 
	 * @param op, String token to translate
	 * @return String, the translated String token
	 */
	public static String translateString(String value) {
		String retStr = "\"";
		if (value.startsWith("\\")) {
			retStr += value.substring(2, value.length() - 2);
		} else {
			return SYNTAX_ERROR;
		}
		retStr += "\"";
		return retStr;
	}

	/**
	 * Translate <cmdline> token from new language to Java
	 * 
	 * @param op, <cmdline> token to translate
	 * @return String, the translated <cmdline> token
	 */
	public static String translateCmdline(String value) {
		if (value.startsWith("<") && value.endsWith(">")) {
			if (value.contains("cmdline"))
				return "args[" + value.substring(8, 9) + "]";
		}

		
		return SYNTAX_ERROR;
	}
}


/*
 * AUTHORS: Anisha Munjal, Anupama Hazarika, Jenan Meri
 * FILE: Translator.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372 Fall 2023
 * Description: This file contains translator code for translating the source 
 * files written in our new language into java source code. 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Translator {
	private static Grammar grammar;

	// File extension for generating output files
	private static final String SYNTAX_ERROR = "Syntax Error: Line# ";

	private static boolean showParsing = false;
	private static boolean showGrammar = false;
	private static boolean showSource = false;
	private static int lineNumber = 0;

	/**
	 * Default constructor
	 */
	public Translator() {

	}

	/**
	 * Parses Source file to translate
	 * 
	 * @param filename, program file to parse
	 * @return String, string for the translated code
	 */
	private static String parseSource(String filename) {
		String outputStr = "";
		String firstLetter = filename.substring(0,1);
				
		outputStr += "public class ";
		outputStr += firstLetter.toUpperCase();	
		outputStr += filename.substring(1, filename.lastIndexOf('.')) + " {\n";
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
			lineNumber++;
			if (showSource)
				System.out.println(line);
			if (line.length() == 0)
				continue;

			if (line.startsWith(":")) {
				outputStr += "\t\t//" + line.substring(1) + "\n";
				continue;
			}

			MyLanguageParser parser = new MyLanguageParser(line, showParsing, lineNumber);
			try {
				outputStr += "\t\t";

				// System.out.println("Parsing input line: " + line);
				if (line.startsWith("*") && line.endsWith("*")) {
					outputStr += translateFirstToken(line);
				} else {
					outputStr += parser.parse();
				}
				outputStr += ";\n";
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		outputStr += "\t}\n}\n";
		return outputStr;
	}

	/**
	 * Main method
	 * 
	 * @param args, String array containing command line arguments
	 */
	public static void main(String[] args) {
		// Source file name comes from command line
		if (args.length == 0) {
			System.out.println("Error: Missing source filename in command line args");
			return;
		}

		if (args.length >= 2) {
			// 2nd cmdline argument “-v” (optional) for verbose mode
			if (args[1].equals("-v")) {
				// Verbose mode shows the token parsing process
				// Debug messages are enabled in this mode showing step by step parsing
				showParsing = true;
				System.out.println("Displaying parsing details as '-v' cmdline arg is set");
			}
		}

		if (args.length >= 3) {
			// 3rd cmdline argument “-g” (optional) for displaying grammar
			if (args[2].equals("-g")) {
				// Grammar productions in the grammar file are displayed
				// Users can see the grammar behind the translation process
				showGrammar = true;
				System.out.println("Displaying grammar as '-g' cmdline arg is set");
			}
		}

		if (args.length >= 4) {
			// 4th cmdline arg “-s” (optional) for displaying new lang src file
			if (args[3].equals("-s")) {
				// Users can see the new language source code when using this option
				showSource = true;
				System.out.println("Displaying source as '-s' cmdline arg is set");
			}
		}

		grammar = new Grammar(showGrammar);
		String outputFileContents = parseSource(args[0]);
		System.out.println(outputFileContents);
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

		return SYNTAX_ERROR + lineNumber + " Unexpected operator " + op;
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

		return SYNTAX_ERROR + lineNumber + " Unexpected assign token " + assign;
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

		return SYNTAX_ERROR + lineNumber + " Unexpected boolean value " + value;
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

		return SYNTAX_ERROR + lineNumber + " Unexpected logical operator " + value;
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

		if (value.equals("gte"))
			return ">= ";

		if (value.equals("lte"))
			return "<= ";

		if (value.equals("notequals"))
			return "!= ";

		return SYNTAX_ERROR + lineNumber + " Unexpected comparison operator " + value;
	}

	/**
	 * Translate conditional operator from new language to Java
	 * 
	 * @param op, String conditional operator to translate
	 * @return String, the translated String conditional operator
	 */
	public static String translateConditional(String value) {
		if (value.equals("then"))
			return "";

		if (value.equals("else"))
			return "else ";

		if (value.equals("["))
			return "{ ";

		if (value.equals("]"))
			return "} ";

		return SYNTAX_ERROR + lineNumber + " Unexpected conditional token " + value;
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
			if (value.substring(1,length(value)).startsWith("\\") && value.substring(1,length(value) - 1).endsWith("\\")){
				String retStr = "System.out.println(\"";
				retStr += value.substring(3, value.length() - 2);
				retStr += "\")";
				return retStr;
			}
			else{
				String retStr = "System.out.println(";
				retStr += value.substring(1, value.length() - 1);
				retStr += ")";
				return retStr;
			}
		}

		if (value.equals("as"))
			return "for (";

		if (value.equals("if"))
			return "if (";
		
		if (value.equals("during")) 
			return "while (";

		return SYNTAX_ERROR + lineNumber + " Unexpected first token " + value;
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
			return SYNTAX_ERROR + lineNumber + " Unexpected string representation " + value;
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

		return SYNTAX_ERROR + lineNumber + " Unexpected cmdline argument " + value;
	}
}

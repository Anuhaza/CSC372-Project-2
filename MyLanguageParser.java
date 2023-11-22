
/*
 * AUTHORS: Anisha Munjal, Anupama Hazarika, Jenan Meri
 * FILE: MyLanguageParser.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372 Fall 2023
 * Description: This file contains parser for the our new programming language
 */
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MyLanguageParser {
	private List<String> tokens;
	private int currentTokenIndex;
	private int nestedLoopCount = 0;
	private int nestedConditionalCount = 0;

	private final String SYNTAX_ERROR = "Syntax Error: ";
	private final String INVALID_VARIABLE_NAME = "Invalid variable name (must start with char)";
	
	private boolean showParsing = false;

	/**
	 * Constructor
	 * 
	 * @param inputStatement, String source line of our new programming language
	 * @param showParsing, boolean true shows parsing information
	 */
	public MyLanguageParser(String inputStatement, boolean showParsing) {
		this.tokens = tokenizeInput(inputStatement);
		this.currentTokenIndex = 0;
		this.showParsing = showParsing;
	}

	/**
	 * Tokenize line of source code of the new language
	 * 
	 * @param inputStatement, String input line
	 * @return List<String>, the list of tokens
	 */
	private List<String> tokenizeInput(String inputStatement) {
		List<String> tokens = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(inputStatement, " ");
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextToken());
		}
		return tokens;
	}

	/*
	 * Statement parser
	 */
	public String parse() throws ParseException {
		return stmt();
	}

	/**
	 * Get next token
	 * 
	 * @return String, the next token string
	 */
	private String getNextToken() {
		if (currentTokenIndex < tokens.size()) {
			return tokens.get(currentTokenIndex++);
		}
		return null;
	}

	/*
	 * Parser for the grammar rules
	 */
	private String stmt() throws ParseException {
		String token = getNextToken();
		if (token != null) {
			switch (token) {
			case "int":
				return intstmt(token);
			case "dec":
				return decstmt(token);
			case "string":
				return strstmt(token);
			case "bool":
				return boolstmt(token);
			case "as":
				return forloop(token);
			case "if":
				return fullif(token);
			default:
				throw new ParseException(SYNTAX_ERROR + "Unexpected token: " + token);
			}
		}

		return "Syntax error";
	}

	/**
	 * Parser for fullif
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException
	 */
	private String fullif(String firstToken) throws ParseException {
		String variable = getNextToken();
		String then = getNextToken();
		String squareBracketOpen = getNextToken();

		nestedConditionalCount++;

		String outputStr = Translator.translateFirstToken(firstToken);

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + INVALID_VARIABLE_NAME);
		}
		outputStr += variable + ") ";

		if (!then.equals("then")) {
			throw new ParseException(SYNTAX_ERROR + "'then' token expected");
		}
		outputStr += Translator.translateConditional(then);

		if (!squareBracketOpen.equals("[")) {
			throw new ParseException(SYNTAX_ERROR + "'[' token expected");
		}
		outputStr += Translator.translateConditional(squareBracketOpen);

		outputStr += " \n\t\t";

		// add tabs based on number of nested loops
		int nestedConditionals = nestedConditionalCount;
		while (nestedConditionals-- > 0)
			outputStr += "\t";

		String line = "";
		while (true) {
			String nextToken = getNextToken();
			if (nextToken == null)
				break;
			line += nextToken + " ";
		}

		this.tokens = tokenizeInput(line);
		this.currentTokenIndex = 0;

		outputStr += parse();

		outputStr += ";\n\t\t";

		// add tabs based on number of nested loops
		nestedConditionals = nestedConditionalCount - 1;
		while (nestedConditionals-- > 0)
			outputStr += "\t";

		String squareBracketClosed = getNextToken();
		String elseToken = getNextToken();
		if (!squareBracketClosed.equals("]")) {
			throw new ParseException(SYNTAX_ERROR + "']' token expected");
		}
		outputStr += Translator.translateConditional(squareBracketClosed);

		if (!elseToken.equals("else")) {
			throw new ParseException(SYNTAX_ERROR + "'else' token expected");
		}
		outputStr += Translator.translateConditional(elseToken);

		squareBracketOpen = getNextToken();

		if (!squareBracketOpen.equals("[")) {
			throw new ParseException(SYNTAX_ERROR + "'[' token expected");
		}
		outputStr += Translator.translateConditional(squareBracketOpen);
		outputStr += " \n\t\t";

		// add tabs based on number of nested loops
		nestedConditionals = nestedConditionalCount;
		while (nestedConditionals-- > 0)
			outputStr += "\t";

		line = "";
		while (true) {
			String nextToken = getNextToken();
			if (nextToken == null)
				break;
			line += nextToken + " ";
		}

		this.tokens = tokenizeInput(line);
		this.currentTokenIndex = 0;

		outputStr += parse();

		outputStr += ";\n\t\t";

		// add tabs based on number of nested loops
		nestedConditionals = nestedConditionalCount - 1;
		while (nestedConditionals-- > 0)
			outputStr += "\t";

		squareBracketClosed = getNextToken();
		if (!squareBracketClosed.equals("]")) {
			throw new ParseException(SYNTAX_ERROR + "']' token expected");
		}
		outputStr += Translator.translateConditional(squareBracketClosed);

		nestedConditionalCount--;
		return outputStr;
	}

	/**
	 * Parser for forloop
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException
	 */
	private String forloop(String firstToken) throws ParseException {
		String intToken = getNextToken();
		String variable = getNextToken();
		String loopsToken = getNextToken();
		String throughToken = getNextToken();
		String loopStart = getNextToken();
		String loopEnd = getNextToken();

		nestedLoopCount++;

		String outputStr = Translator.translateFirstToken(firstToken);

		if (!intToken.equals("int")) {
			throw new ParseException(SYNTAX_ERROR + "int token expected");
		}
		outputStr += intToken + " ";

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + INVALID_VARIABLE_NAME);
		}
		outputStr += variable + " = ";

		if (!loopsToken.equals("loops") || !throughToken.equals("through")) {
			throw new ParseException(SYNTAX_ERROR + "'loops through' tokens expected");
		}

		if (!loopStart.startsWith("(")) {
			throw new ParseException(SYNTAX_ERROR + "'(' token expected");
		}
		outputStr += Integer.parseInt(loopStart.substring(1, 2)) + "; " + variable + " < ";
		outputStr += Integer.parseInt(loopEnd.substring(0, 1)) + "; " + variable + "++)";
		outputStr += " {\n\t\t";

		// add tabs based on number of nested loops
		int nestedLoops = nestedLoopCount;
		while (nestedLoops-- > 0)
			outputStr += "\t";

		String line = "";
		while (true) {
			String nextToken = getNextToken();
			if (nextToken == null)
				break;
			line += nextToken + " ";
		}

		this.tokens = tokenizeInput(line);
		this.currentTokenIndex = 0;

		outputStr += parse();

		outputStr += ";\n\t\t";

		// add tabs based on number of nested loops
		nestedLoops = nestedLoopCount - 1;
		while (nestedLoops-- > 0)
			outputStr += "\t";

		outputStr += "}";

		nestedLoopCount--;
		return outputStr;

	}

	/**
	 * Parser for intstmt
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException
	 */
	private String intstmt(String firstToken) throws ParseException {
		String variable = getNextToken();
		String assign = getNextToken();
		String value1 = getNextToken();
		String outputStr = Translator.translateFirstToken(firstToken);

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + INVALID_VARIABLE_NAME);
		}

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + "Expected '->' after variable declaration");
		}
		if (!isInteger(value1) && !isVariable(value1)) {
			throw new ParseException(SYNTAX_ERROR + "Invalid integer values");
		}

		outputStr += variable + " ";
		outputStr += Translator.translateAssign(assign); // translate "->"
		outputStr += value1;

		String op = getNextToken();
		if (op != null) {
			String value2 = getNextToken();
			if (value2 == null || !isInteger(value2) && !isVariable(value2)) {
				throw new ParseException(SYNTAX_ERROR + "Missing second operand for arithmetic operation");
			}
			outputStr += " " + Translator.translateOperator(op);
			outputStr += value2;
		}

		return outputStr;
	}

	/**
	 * Parser for decstmt
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException
	 */
	private String decstmt(String firstToken) throws ParseException {
		String variable = getNextToken();
		String assign = getNextToken();
		String value1 = getNextToken();
		String outputStr = Translator.translateFirstToken(firstToken);

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + INVALID_VARIABLE_NAME);
		}

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + "Expected '->' after variable declaration");
		}

		if (!isDecimal(value1) && !isVariable(value1)) {
			throw new ParseException(SYNTAX_ERROR + "Invalid decimal values");
		}

		outputStr += variable + " ";
		outputStr += Translator.translateAssign(assign); // translate "->"
		outputStr += value1;

		String op = getNextToken();
		if (op != null) {
			String value2 = getNextToken();
			if (value2 == null || !isDecimal(value2) && !isVariable(value2)) {
				throw new ParseException(SYNTAX_ERROR + "Missing correct second operand for arithmetic operation");
			}
			outputStr += " " + Translator.translateOperator(op);
			outputStr += value2;
		}

		return outputStr;
	}

	/**
	 * Parser for strstmt
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException
	 */
	private String strstmt(String firstToken) throws ParseException {
		String variable = getNextToken();
		String assign = getNextToken();
		String value1 = getNextToken();
		String outputStr = Translator.translateFirstToken(firstToken);

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + "Expected '->' after string declaration");
		}

		if (value1 == null || (!value1.startsWith("\\")) && !isVariable(value1)) {
			throw new ParseException(SYNTAX_ERROR + "String value must be enclosed in backslashes");
		}

		while (!isVariable(value1) && (!value1.endsWith("\\"))) {
			String nVal = getNextToken();
			if (nVal == null)
				break;
			value1 += " " + nVal;
		}

		if (!isVariable(value1) && !value1.endsWith("\\")) {
			throw new ParseException(SYNTAX_ERROR + "String value must be enclosed in backslashes");
		}

		outputStr += variable + " ";
		outputStr += Translator.translateAssign(assign); // translate "->"
		if (value1.startsWith("\\") && value1.endsWith("\\")) {
			outputStr += Translator.translateString(value1) + " ";
		} else {
			outputStr += value1 + " ";
		}

		String operation = getNextToken();
		if (operation != null && !operation.equals("add")) {
			throw new ParseException(SYNTAX_ERROR + "Invalid string operation: " + operation);
		}

		if (operation != null) {
			String value2 = getNextToken();
			if (value2 == null || (!value2.startsWith("\\")) && !isVariable(value2) && !isCmdLine(value2)) {
				throw new ParseException(SYNTAX_ERROR + "String value must be enclosed in backslashes");
			}

			while (!isVariable(value2) && (!value2.endsWith("\\"))) {
				String nVal = getNextToken();
				if (nVal == null)
					break;
				value2 += " " + nVal;
			}

			if (!isVariable(value2) && !value2.endsWith("\\") && !isCmdLine(value2)) {
				throw new ParseException(SYNTAX_ERROR + "String value must be enclosed in backslashes");
			}
			outputStr += Translator.translateOperator(operation);
			if (isCmdLine(value2)) {
				outputStr += Translator.translateCmdline(value2);
			} else {
				outputStr += Translator.translateString(value2);
			}
		}

		return outputStr;
	}

	/**
	 * Parser for boolstmt
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException
	 */
	private String boolstmt(String firstToken) throws ParseException {
		String variable = getNextToken();
		String assign = getNextToken();
		String value = getNextToken();
		String outputStr = Translator.translateFirstToken(firstToken);

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + "Expected '->' after boolean declaration");
		}

		if (value == null) {
			throw new ParseException(SYNTAX_ERROR + "Invalid boolean value or statement");
		}

		outputStr += variable + " ";
		outputStr += Translator.translateAssign(assign); // translate "->"

		if (!value.equals("T") && !value.equals("F")) {
			if (value.equals("not")) {
				String value2 = getNextToken();
				if (value2 == null || !value2.equals("T") && !value2.equals("F")) {
					throw new ParseException(SYNTAX_ERROR + "boolean value must follow not.");
				}
				outputStr += Translator.translateLogical(value);
				outputStr += Translator.translateBoolean(value2);

			} else if (isVariable(value)) {
				String operator = getNextToken();
				if (operator == null || !isLogical(operator)) {
					throw new ParseException(SYNTAX_ERROR + "Operator needs to be logical based.");
				}
				String value2 = getNextToken();
				if (!isVariable(value)) {
					throw new ParseException(SYNTAX_ERROR + "Second operand is not a variable.");
				}

				outputStr += value + " ";
				outputStr += Translator.translateLogical(operator);
				outputStr += value2;

			} else if (isDecimal(value) || isInteger(value)) {
				String operator = getNextToken();
				if (operator == null || !isComparison(operator)) {
					throw new ParseException(SYNTAX_ERROR + "Operator needs to be comparison based.");
				}
				String value2 = getNextToken();
				if (!isDecimal(value2) && !isInteger(value2) && !isVariable(value2)) {
					throw new ParseException(SYNTAX_ERROR + "Second operand is not comparison based.");
				}
				outputStr += value + " ";
				outputStr += Translator.translateComparison(operator);
				outputStr += value2;

			} else {
				throw new ParseException(SYNTAX_ERROR + "First operand is not comparison based or a boolean value.");
			}
		} else {
			String operator = getNextToken();
			if (operator == null) {
				outputStr += Translator.translateBoolean(value);
			} else {
				if (!isLogical(operator)) {
					throw new ParseException(SYNTAX_ERROR + "Operand is not logical.");
				}
				String value2 = getNextToken();
				if (value2 == null || !isVariable(value) && !isVariable(value2)) {
					throw new ParseException(SYNTAX_ERROR + "Second value is not a boolean value.");
				}

				outputStr += value;
				outputStr += Translator.translateLogical(operator);
				outputStr += value2;
			}
		}

		return outputStr;
	}

	/**
	 * Is the token string a variable
	 * 
	 * @param str, String token
	 * @return boolean, true if passed String is a var, false otherwise
	 */
	public static boolean isVariable(String str) {
		if (str.isEmpty()) {
			return false;
		}
		char firstChar = str.charAt(0);
		if (!((firstChar >= 'a' && firstChar <= 'z') || (firstChar >= 'A' && firstChar <= 'Z'))) {
			return false;
		}

		for (int i = 1; i < str.length(); i++) {
			char currentChar = str.charAt(i);
			if (!((currentChar >= 'a' && currentChar <= 'z') || (currentChar >= 'A' && currentChar <= 'Z')
					|| (currentChar >= '0' && currentChar <= '9'))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Is the token string a comparison token
	 * 
	 * @param str, String token
	 * @return boolean, true if passed String is a comparator, false otherwise
	 */
	private boolean isComparison(String token) {
		return token.equals("gt") || token.equals("lt") || token.equals("equals") || token.equals("gte")
				|| token.equals("lte") || token.equals("notequals");
	}

	/**
	 * Is the token string a logical operation
	 * 
	 * @param str, String token
	 * @return boolean, true if passed String is a logical operation, false
	 *         otherwise
	 */
	private boolean isLogical(String token) {
		return token.equals("and") || token.equals("or");
	}

	/**
	 * Is the token string a decimal
	 * 
	 * @param str, String token
	 * @return boolean, true if passed String is a decimal, false otherwise
	 */
	private boolean isDecimal(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Is the token a command line argument
	 * 
	 * @param value2, String token
	 * @return boolean, true if passed String is a cmdline arg, false otherwise
	 */
	private boolean isCmdLine(String value) {
		if (value.startsWith("<") && value.endsWith(">")) {
			if (value.contains("cmdline"))
				return true;
		}
		return false;
	}

	/**
	 * Is the token string an integer
	 * 
	 * @param str, String token
	 * @return boolean, true if passed String is an integer, false otherwise
	 */
	private boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * ParseException (extends Exception)
	 */
	private static class ParseException extends Exception {
		public ParseException(String message) {
			super(message);
		}
	}
}

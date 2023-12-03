
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
	private String inputStatement;

	private int lineNumber = 0;
	private final String SYNTAX_ERROR = "Syntax Error: Line# ";
	private final String INVALID_VARIABLE_NAME = " Invalid variable name (must start with char)";

	private boolean showParsing = false;

	/**
	 * Constructor
	 * 
	 * @param inputStatement, String source line of our new programming language
	 * @param showParsing,    boolean true shows parsing information
	 * @param lineNumber2
	 */
	public MyLanguageParser(String inputStatement, boolean showParsing, int lineNumber) {
		this.tokens = tokenizeInput(inputStatement);
		this.currentTokenIndex = 0;
		this.showParsing = showParsing;
		this.lineNumber = lineNumber;
	}

	/**
	 * Tokenize line of source code of the new language
	 * 
	 * @param inputStatement, String input line
	 * @return List<String>, the list of tokens
	 */
	private List<String> tokenizeInput(String inputStatement) {
		this.inputStatement = inputStatement;
		List<String> tokens = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(inputStatement, " ");
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextToken());
		}
		if (showParsing) {
			System.out.println("\tTokens: " + tokens);
		}
		return tokens;
	}

	/**
	 * Generate statement
	 * 
	 * @return String, parsed string
	 * @throws ParseException, exception thrown for parse errors
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

	/**
	 * Statement Parser for the grammar rules
	 * 
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String stmt() throws ParseException {
		String token = getNextToken();
		if (token != null) {
			switch (token) {
			case "int":
				if (showParsing)
					System.out.println("Parsing <int-stmt>");
				return intstmt(token);
			case "dec":
				if (showParsing)
					System.out.println("Parsing <dec-stmt>");
				return decstmt(token);
			case "string":
				if (showParsing)
					System.out.println("Parsing <string-stmt>");
				return strstmt(token);
			case "*": // Handle print statements
				if (showParsing)
					System.out.println("Parsing <print-stmt>");
				return printstmt(token);
			case "bool":
				if (showParsing)
					System.out.println("Parsing <bool-stmt>");
				return boolstmt(token);
			case "as":
				if (showParsing)
					System.out.println("Parsing <for-loop>");
				return forloop(token);
			case "if":
				if (showParsing)
					System.out.println("Parsing <full-if>");
				return fullif(token);
			case ":": // Handle comments
				if (showParsing)
					System.out.println("Parsing <comment>");
				return comment1Colon(token);
			case "::": // Handle comments
				if (showParsing)
					System.out.println("Parsing <comment>");
				return comment2Colons(token);
			case "during": // Handle while loops
				if (showParsing)
					System.out.println("Parsing <while-loop>");
				return whileloop(token);
			default:
				if (showParsing)
					System.out.println("Parsing <var-assign>");
				return varassign(token);
			// throw new ParseException(SYNTAX_ERROR + lineNumber + " Unexpected token: " +
			// token);
			}
		}

		return "Syntax error";
	}

	private String varassign(String firstToken) throws ParseException {
		String assign = getNextToken();
		String value1 = getNextToken();

		if (!isVariable(firstToken)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + INVALID_VARIABLE_NAME);
		}

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Expected '->' after variable declaration");
		}
		if (!isInteger(value1) && !isVariable(value1)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Invalid integer values");
		}

		String outputStr = "";
		outputStr += firstToken + " ";
		outputStr += Translator.translateAssign(assign); // translate "->"
		outputStr += value1;

		String op = getNextToken();
		if (op != null) {
			String value2 = getNextToken();
			if (value2 == null || !isInteger(value2) && !isVariable(value2)) {
				throw new ParseException(
						SYNTAX_ERROR + lineNumber + " Missing second operand for arithmetic operation");
			}
			outputStr += " " + Translator.translateOperator(op);
			outputStr += value2;
		}

		return outputStr;
	}

	/**
	 * Parser for fullif
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String fullif(String firstToken) throws ParseException {
		String variable = getNextToken();
		String then = getNextToken();

		nestedConditionalCount++;

		String outputStr = Translator.translateFirstToken(firstToken);

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + INVALID_VARIABLE_NAME);
		}
		outputStr += variable;

		if (!then.equals("then")) {
			// check for expression such as x gt y
			if (isComparison(then)) {
				outputStr += " " + Translator.translateComparison(then);
				variable = getNextToken();
				outputStr += variable;
			}
			then = getNextToken();
			if (!then.equals("then")) {
				throw new ParseException(SYNTAX_ERROR + lineNumber + " 'then' token expected");
			}
		}

		outputStr += ") ";

		outputStr += Translator.translateConditional(then);

		String squareBracketOpen = getNextToken();
		if (!squareBracketOpen.equals("[")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " '[' token expected");
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
		if (!squareBracketClosed.equals("]")) {
			squareBracketClosed = getNextToken();
			squareBracketClosed = getNextToken();
		}

		if (!squareBracketClosed.equals("]")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " ']' token expected");
		}
		outputStr += Translator.translateConditional(squareBracketClosed);

		String elseToken = getNextToken();
		if (!elseToken.equals("else")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " 'else' token expected");
		}
		outputStr += Translator.translateConditional(elseToken);

		squareBracketOpen = getNextToken();

		if (!squareBracketOpen.equals("[")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " '[' token expected");
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
			squareBracketClosed = getNextToken();
			squareBracketClosed = getNextToken();
			if (!squareBracketClosed.equals("]")) {
				throw new ParseException(SYNTAX_ERROR + lineNumber + " ']' token expected");
			}
		}
		outputStr += Translator.translateConditional(squareBracketClosed);

		nestedConditionalCount--;
		return outputStr;
	}

	/**
	 * Parser for comment
	 * 
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String comment1Colon(String firstToken) throws ParseException {
		String outputStr = "// ";
		if (!firstToken.startsWith(":")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Comments must start with ':'");
		}
		// Print the comment with * * around it
		while (true) {
			String nextToken = getNextToken();
			if (nextToken == null)
				break;
			outputStr += nextToken + " ";
		}
		return outputStr;
	}

	/**
	 * Parser for comment
	 * 
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String comment2Colons(String firstToken) throws ParseException {
		String outputStr = "// ";
		if (!firstToken.startsWith("::")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Comments must be enclosed in '::'");
		}
		// Print the comment with * * around it
		while (true) {
			String nextToken = getNextToken();
			if (nextToken == null)
				break;
			if (nextToken.equals("::"))
				break;
			outputStr += nextToken + " ";
		}
		return outputStr;
	}

	/**
	 * Parser for whileloop
	 * 
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String whileloop(String firstToken) throws ParseException {
		String intToken = getNextToken();

		String outputStr = "";
		String whileStr = Translator.translateFirstToken(firstToken);

		if (isVariable(intToken) && !intToken.equals("int")) {
			return whileloopComparison(firstToken, intToken);
		}

		String variable = getNextToken();
		String loopsToken = getNextToken();
		String throughToken = getNextToken();
		String loopStart = getNextToken();
		String loopEnd = getNextToken();

		if (!intToken.equals("int")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " int token expected");
		}
		outputStr += intToken + " ";

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + INVALID_VARIABLE_NAME);
		}
		outputStr += variable + " = ";

		if (!loopsToken.equals("loops") || !throughToken.equals("through")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " 'loops through' tokens expected");
		}

		if (!loopStart.startsWith("(")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " '(' token expected");
		}

		outputStr += Integer.parseInt(loopStart.substring(1, 2)) + ";\n";
		int nestedLoops = nestedLoopCount;
		while (nestedLoops-- > 0)
			outputStr += "\t";
		nestedLoopCount++;

		outputStr += "\t\t" + whileStr + variable + " < ";
		outputStr += Integer.parseInt(loopEnd.substring(0, 1)) + "){\n\t\t";

		// Add tabs based on the number of nested loops
		nestedLoops = nestedLoopCount;
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
		// Add tabs based on the number of nested loops
		nestedLoops = nestedLoopCount;
		while (nestedLoops-- > 0)
			outputStr += "\t";

		outputStr += variable + "++;\n\t\t";

		// Add tabs based on the number of nested loops
		nestedLoops = nestedLoopCount - 1;
		while (nestedLoops-- > 0)
			outputStr += "\t";

		outputStr += "}";
		nestedLoopCount--;

		return outputStr;
	}

	private String whileloopComparison(String firstToken, String intToken) throws ParseException {
		String outputStr = Translator.translateFirstToken(firstToken);
		outputStr += intToken + " ";

		String comparison = getNextToken();

		if (!isComparison(comparison)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Expecting comparison operator");
		}

		outputStr += Translator.translateComparison(comparison);

		String variable = getNextToken();
		
		boolean newStatement = false;
		if (variable.endsWith(",")) {
			newStatement = true;
			variable = variable.substring(0, variable.length()-1);
		}

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + INVALID_VARIABLE_NAME);
		}
		outputStr += variable + ") {\n\t\t";

		// while loop has nested statements
		if (newStatement == true) {
			String str = whileloopComparisonNestedStatements(outputStr) + "\n\t\t" + intToken + "++;\n\t\t}\n";

			return str;
		}
		
		String loopsToken = getNextToken();

		if (!loopsToken.equals("loops,")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " loops token expected");
		}

		nestedLoopCount++;

		// Add tabs based on the number of nested loops
		int nestedLoops = nestedLoopCount;
		while (nestedLoops-- > 0)
			outputStr += "\t";

		// new statement starts
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
		// Add tabs based on the number of nested loops
		nestedLoops = nestedLoopCount;
		while (nestedLoops-- > 0)
			outputStr += "\t";

		outputStr += "}";
		nestedLoopCount--;
		return outputStr;
	}

	private String whileloopComparisonNestedStatements(String outputStr) throws ParseException {

		int nestedLoops = nestedLoopCount;
		while (nestedLoops-- > 0)
			outputStr += "\t";

		// new statement starts
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
		// Add tabs based on the number of nested loops
		nestedLoops = nestedLoopCount;
		while (nestedLoops-- > 0)
			outputStr += "\t";
		
		return outputStr;
	}

	/**
	 * Parser for print
	 * 
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String printstmt(String firstToken) throws ParseException {
		String retStr = "";
		if (!firstToken.startsWith("*")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Print statements must be enclosed in '*'");
		}

		// handle case of print statements within conditionals
		if (inputStatement.startsWith("*") && !inputStatement.endsWith("*")) {

			if (inputStatement.contains("\\")) {
				inputStatement = inputStatement.substring(0, inputStatement.length() - 1);

				if (inputStatement.substring(3, inputStatement.length() - 2).startsWith("\\")
						&& inputStatement.substring(3, inputStatement.length() - 2).endsWith("\\")) {
					String retStr2 = "System.out.print(\"";
					retStr2 += inputStatement.substring(5, inputStatement.length() - 5);
					retStr2 += "\")";
					return retStr2;
				} else {
					String retStr2 = "System.out.print(";
					retStr2 += inputStatement.substring(2, inputStatement.length() - 2);
					retStr2 += ")";
					return retStr2;
				}
			}

			String substr1 = inputStatement.substring(2, 3);
			retStr = "System.out.print(" + substr1 + ")";
			return retStr;
		}

		if (!inputStatement.startsWith("*") || !inputStatement.endsWith("*")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Print statements must be enclosed in '*'");
		}

		if (inputStatement.substring(3, inputStatement.length() - 2).startsWith("\\")
				&& inputStatement.substring(3, inputStatement.length() - 2).endsWith("\\")) {
			retStr = "System.out.print(\"";
			retStr += inputStatement.substring(5, inputStatement.length() - 5);
			retStr += "\")";
			return retStr;
		} else {
			retStr = "System.out.print(";
			retStr += inputStatement.substring(2, inputStatement.length() - 2);
			retStr += ")";
			return retStr;
		}
	}

	/**
	 * Parser for forloop
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
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
			throw new ParseException(SYNTAX_ERROR + lineNumber + " int token expected");
		}
		outputStr += intToken + " ";

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + INVALID_VARIABLE_NAME);
		}
		outputStr += variable + " = ";

		if (!loopsToken.equals("loops") || !throughToken.equals("through")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " 'loops through' tokens expected");
		}

		if (!loopStart.startsWith("(")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " '(' token expected");
		}

		if (Character.isDigit(loopStart.charAt(1))) {
			outputStr += Integer.parseInt(loopStart.substring(1, 2)) + "; " + variable + " < ";
			outputStr += Integer.parseInt(loopEnd.substring(0, 1)) + "; " + variable + "++)";
		} else {
			outputStr += loopStart.substring(1, loopStart.length() - 1) + "; " + variable + " <= ";
			outputStr += loopEnd.substring(0, loopEnd.length() - 2) + "; " + variable + "++)";
		}

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
		if (nestedLoopCount > 1)
			outputStr += "\n\t\t\tSystem.out.println();";

		nestedLoopCount--;
		return outputStr;

	}

	/**
	 * Parser for intstmt
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String intstmt(String firstToken) throws ParseException {
		boolean newStatement = false;
		String variable = getNextToken();
		String assign = getNextToken();
		String value1 = getNextToken();
		String outputStr = Translator.translateFirstToken(firstToken);

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + INVALID_VARIABLE_NAME);
		}

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Expected '->' after variable declaration");
		}
		if (!isInteger(value1) && !isVariable(value1)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Invalid integer values");
		}

		outputStr += variable + " ";
		outputStr += Translator.translateAssign(assign); // translate "->"
		outputStr += value1;

		String op = getNextToken();
		if (op != null) {
			String value2 = getNextToken();
			
			// another statement follows
			if (value2.endsWith(",")) {
				newStatement = true;
				value2 = value2.substring(0, value2.length() - 1);
			}

			if (value2 == null || !isInteger(value2) && !isVariable(value2)) {
				throw new ParseException(
						SYNTAX_ERROR + lineNumber + " Missing second operand for arithmetic operation");
			}
			outputStr += " " + Translator.translateOperator(op);
			outputStr += value2;

		}
		
		if (newStatement) {
			outputStr += ";\n\t\t";
			
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
		}

		return outputStr;
	}

	/**
	 * Parser for decstmt
	 * 
	 * @param String, the first token in the production
	 * @return String, the parsed and translated string
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String decstmt(String firstToken) throws ParseException {
		String variable = getNextToken();
		String assign = getNextToken();
		String value1 = getNextToken();
		String outputStr = Translator.translateFirstToken(firstToken);

		if (!isVariable(variable)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + INVALID_VARIABLE_NAME);
		}

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Expected '->' after variable declaration");
		}

		if (!isDecimal(value1) && !isVariable(value1)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Invalid decimal values");
		}

		outputStr += variable + " ";
		outputStr += Translator.translateAssign(assign); // translate "->"
		outputStr += value1;

		String op = getNextToken();
		if (op != null) {
			String value2 = getNextToken();
			if (value2 == null || !isDecimal(value2) && !isVariable(value2)) {
				throw new ParseException(
						SYNTAX_ERROR + lineNumber + " Missing correct second operand for arithmetic operation");
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
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String strstmt(String firstToken) throws ParseException {
		String variable = getNextToken();
		String assign = getNextToken();
		String value1 = getNextToken();
		String outputStr = Translator.translateFirstToken(firstToken);

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Expected '->' after string declaration");
		}

		if (value1 == null || (!value1.startsWith("\\")) && !isVariable(value1)) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " String value must be enclosed in backslashes");
		}

		while (!isVariable(value1) && (!value1.endsWith("\\"))) {
			String nVal = getNextToken();
			if (nVal == null)
				break;
			value1 += " " + nVal;
		}

		if (!isVariable(value1) && !value1.endsWith("\\")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " String value must be enclosed in backslashes");
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
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Invalid string operation: " + operation);
		}

		if (operation != null) {
			String value2 = getNextToken();
			if (value2 == null || (!value2.startsWith("\\")) && !isVariable(value2) && !isCmdLine(value2)) {
				throw new ParseException(SYNTAX_ERROR + lineNumber + " String value must be enclosed in backslashes");
			}

			while (!isVariable(value2) && (!value2.endsWith("\\"))) {
				String nVal = getNextToken();
				if (nVal == null)
					break;
				value2 += " " + nVal;
			}

			if (!isVariable(value2) && !value2.endsWith("\\") && !isCmdLine(value2)) {
				throw new ParseException(SYNTAX_ERROR + lineNumber + " String value must be enclosed in backslashes");
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
	 * @throws ParseException, exception thrown for parse errors
	 */
	private String boolstmt(String firstToken) throws ParseException {
		String variable = getNextToken();
		String assign = getNextToken();
		String value = getNextToken();
		String outputStr = Translator.translateFirstToken(firstToken);

		if (!assign.equals("->")) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Expected '->' after boolean declaration");
		}

		if (value == null) {
			throw new ParseException(SYNTAX_ERROR + lineNumber + " Invalid boolean value or statement");
		}

		outputStr += variable + " ";
		outputStr += Translator.translateAssign(assign); // translate "->"

		if (!value.equals("T") && !value.equals("F")) {
			if (value.equals("not")) {
				String value2 = getNextToken();
				if (value2 == null || !value2.equals("T") && !value2.equals("F")) {
					throw new ParseException(SYNTAX_ERROR + lineNumber + " boolean value must follow not.");
				}
				outputStr += Translator.translateLogical(value);
				outputStr += Translator.translateBoolean(value2);

			} else if (isVariable(value)) {
				String operator = getNextToken();
				if (operator == null || (!isLogical(operator) && !isComparison(operator))) {
					throw new ParseException(SYNTAX_ERROR + lineNumber + " Logical operator expected.");
				}
				String value2 = getNextToken();
				if (!isVariable(value)) {
					throw new ParseException(SYNTAX_ERROR + lineNumber + " Second operand is not a variable.");
				}

				outputStr += value + " ";
				if (isLogical(operator)) {
					outputStr += Translator.translateLogical(operator);
				}
				if (isComparison(operator)) {
					outputStr += Translator.translateComparison(operator);
				}
				outputStr += value2;

			} else if (isDecimal(value) || isInteger(value)) {
				String operator = getNextToken();
				if (operator == null || !isComparison(operator)) {
					throw new ParseException(SYNTAX_ERROR + lineNumber + " Comparison operator expected.");
				}
				String value2 = getNextToken();
				if (!isDecimal(value2) && !isInteger(value2) && !isVariable(value2)) {
					throw new ParseException(SYNTAX_ERROR + lineNumber + " Second operand is not comparison based.");
				}
				outputStr += value + " ";
				outputStr += Translator.translateComparison(operator);
				outputStr += value2;

			} else {
				throw new ParseException(
						SYNTAX_ERROR + lineNumber + " First operand is not comparison based or a boolean value.");
			}
		} else {
			String operator = getNextToken();
			if (operator == null) {
				outputStr += Translator.translateBoolean(value);
			} else {
				if (!isLogical(operator)) {
					throw new ParseException(SYNTAX_ERROR + lineNumber + " Logical operator expected.");
				}
				String value2 = getNextToken();
				if (value2 == null || !isVariable(value) && !isVariable(value2)) {
					throw new ParseException(SYNTAX_ERROR + lineNumber + " Second value is not a boolean value.");
				}

				outputStr += value;
				outputStr += Translator.translateLogical(operator);
				outputStr += value2;
			}
		}
		
		// check if another statement follows
		if (outputStr.endsWith(",")) {
			outputStr = outputStr.substring(0, outputStr.length() - 1);
			
			outputStr += ";\n\t\t";
			
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

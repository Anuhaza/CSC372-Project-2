import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MyLanguageParser {
    private List<String> tokens;
    private int currentTokenIndex;

    public MyLanguageParser(String inputStatement) {
        this.tokens = tokenizeInput(inputStatement);
        this.currentTokenIndex = 0;
    }

    public static void main(String[] args) {
        // Example input statements
        String inputStatement1 = "bool invalid -> 5 gt 3";
        String inputStatement2 = "string variable -> x add \\hi\\";

        MyLanguageParser parser1 = new MyLanguageParser(inputStatement1);
        MyLanguageParser parser2 = new MyLanguageParser(inputStatement2);

        try {
            System.out.println("Parsing input 1:");
            parser1.parse();
        } catch (ParseException e) {
            System.err.println("Error parsing input 1: " + e.getMessage());
        }

        try {
            System.out.println("\nParsing input 2:");
            parser2.parse();
        } catch (ParseException e) {
            System.err.println("Error parsing input 2: " + e.getMessage());
        }
    }

    private List<String> tokenizeInput(String inputStatement) {
        List<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(inputStatement, " ");
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    public void parse() throws ParseException {
        stmt();
    }

    private String getNextToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex++);
        }
        return null;
    }

    private void stmt() throws ParseException {
        String token = getNextToken();
        if (token != null) {
            switch (token) {
                case "int":
                    intstmt();
                    break;
                case "dec":
                    decstmt();
                    break;
                case "string":
                    strstmt();
                    break;
                case "bool":
                    boolstmt();
                    break;
                default:
                    throw new ParseException("Unexpected token: " + token);
            }
        }
    }

    private void intstmt() throws ParseException {
        String variable = getNextToken();
        String assign = getNextToken();
        String value1 = getNextToken();

        System.out.println("Variable: " + variable);
        System.out.println("Assignment: " + assign);
        System.out.println("Value 1: " + value1);
        
        if (!isInteger(value1) && !isVariable(value1)) {
            throw new ParseException("Invalid integer values");
        }

        String op = getNextToken();
        if (op != null) {
            String value2 = getNextToken();
            if (value2 == null || !isInteger(value2) && !isVariable(value2)) {
                throw new ParseException("Missing second operand for arithmetic operation");
            }
            System.out.println("Operator: " + op);
            System.out.println("Value 2: " + value2);
        }
    }

    private void decstmt() throws ParseException {
        String variable = getNextToken();
        String assign = getNextToken();
        String value1 = getNextToken();

        if (!assign.equals("->")) {
            throw new ParseException("Expected '->' after variable declaration");
        }

        if (!isDecimal(value1) && !isVariable(value1)) {
            throw new ParseException("Invalid decimal values");
        }

        System.out.println("Variable: " + variable);
        System.out.println("Assignment: " + assign);
        System.out.println("Value 1: " + value1);

        String op = getNextToken();
        if (op != null) {
            String value2 = getNextToken();
            if (value2 == null || !isDecimal(value2) && !isVariable(value2)) {
                throw new ParseException("Missing correct second operand for arithmetic operation");
            }
            System.out.println("Operator: " + op);
            System.out.println("Value 2: " + value2);
        }
    }

    private void strstmt() throws ParseException {
        String variable = getNextToken();
        String assign = getNextToken();
        String value1 = getNextToken();

        if (!assign.equals("->")) {
            throw new ParseException("Expected '->' after string declaration");
        }
        
        if (value1 == null || (!value1.startsWith("\\")) && !isVariable(value1)){
            throw new ParseException("String value must be enclosed in backslashes");
        }
        
        while (!isVariable(value1) && (!value1.endsWith("\\"))){
        	String nVal = getNextToken();
        	if (nVal == null)
        		break;
        	value1 += " " + nVal;
        }

        if (!isVariable(value1) && !value1.endsWith("\\")) {
            throw new ParseException("String value must be enclosed in backslashes");
        }

        System.out.println("Variable: " + variable);
        System.out.println("Assignment: " + assign);
        System.out.println("String Value 1: " + value1);

        String operation = getNextToken();
        if (operation != null && !operation.equals("add")) {
            throw new ParseException("Invalid string operation: " + operation);
        }

        if (operation != null) {
            String value2 = getNextToken();
            if (value2 == null || (!value2.startsWith("\\")) && !isVariable(value2)){
                throw new ParseException("String value must be enclosed in backslashes");
            }
            
            while (!isVariable(value2) && (!value2.endsWith("\\"))){
            	String nVal = getNextToken();
            	if (nVal == null)
            		break;
            	value2 += " " + nVal;
            }

            if (!isVariable(value2) && !value2.endsWith("\\")) {
                throw new ParseException("String value must be enclosed in backslashes");
            }
            System.out.println("Operation: " + operation);
            System.out.println("String Value 2: " + value2);
        }
    }
    
    private void boolstmt() throws ParseException {
        String variable = getNextToken();
        String assign = getNextToken();
        String value = getNextToken();

        if (!assign.equals("->")) {
            throw new ParseException("Expected '->' after boolean declaration");
        }
        
        if (value == null) {
            throw new ParseException("Invalid boolean value or statement");
        }
        
        if (!value.equals("T") && !value.equals("F")) {
        	if (value.equals("not")) {
        		String value2 = getNextToken();
        		if (value2 == null || !value2.equals("T") && !value2.equals("F")) {
        			throw new ParseException("Value after not is not a boolean value.");
        		}
        		System.out.println("Variable: " + variable);
                System.out.println("Assignment: " + assign);
                System.out.println("Boolean Operation: " + value);
                System.out.println("Value2: " + value2);
        	}
        	else if (isDecimal(value) || isInteger(value) || isVariable(value)) {
        		String operator = getNextToken();
        		if (operator == null || !isComparison(operator)) {
        			throw new ParseException("Operator needs to be comparison based.");
        		}
        		String value2 = getNextToken();
        		if (!isDecimal(value2) && !isInteger(value2) && !isVariable(value2)) {
        			throw new ParseException("Second operand is not comparison based.");
        		}
        		System.out.println("Variable: " + variable);
                System.out.println("Assignment: " + assign);
                System.out.println("Value 1: " + value);
                System.out.println("Boolean Operation: " + operator);
                System.out.println("Value2: " + value2);
        	}
        	else {
        		throw new ParseException("First operand is not comparison based or a boolean value.");
        	}
        }
        else {
        	String operator = getNextToken();
        	if (operator == null) {
        		System.out.println("Variable: " + variable);
        		System.out.println("Assignment: " + assign);
        		System.out.println("Value: " + value);
        	}
        	else {
        		if (!isLogical(operator)) {
        			throw new ParseException("Operand is not logical.");
        		}
        		String value2 = getNextToken();
        		if (value2 == null || !value2.equals("T") && !value2.equals("F")) {
        			throw new ParseException("Second value is not a boolean value.");
        		}
        		System.out.println("Variable: " + variable);
                System.out.println("Assignment: " + assign);
                System.out.println("Value 1: " + value);
                System.out.println("Boolean Operation: " + operator);
                System.out.println("Value2: " + value2);
        	}
        }

    }

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


    private boolean isComparison(String token) {
        return token.equals("gt") || token.equals("lt") || token.equals("equals");
    }
    
    private boolean isLogical(String token) {
    	return token.equals("and") || token.equals("or");
    }

    
    private boolean isDecimal(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
}

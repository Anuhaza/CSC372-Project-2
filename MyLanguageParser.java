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
        String inputStatement1 = "string var -> \\32\\ add \\7\\";
        String inputStatement2 = "dec variable -> 42.5";

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
        
        if (!isInteger(value1)) {
            throw new ParseException("Invalid integer values");
        }

        String op = getNextToken();
        if (op != null) {
            String value2 = getNextToken();
            if (value2 == null || !isInteger(value2)) {
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

        if (!isDecimal(value1)) {
            throw new ParseException("Invalid decimal values");
        }

        System.out.println("Variable: " + variable);
        System.out.println("Assignment: " + assign);
        System.out.println("Value 1: " + value1);

        String op = getNextToken();
        if (op != null) {
            String value2 = getNextToken();
            if (value2 == null || !isDecimal(value2)) {
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
        
        if (value1 == null || (!value1.startsWith("\\"))){
            throw new ParseException("String value must be enclosed in backslashes");
        }
        
        while ((!value1.endsWith("\\"))){
        	String nVal = getNextToken();
        	if (nVal == null)
        		break;
        	value1 += " " + nVal;
        }

        if (!value1.endsWith("\\")) {
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
            if (value2 == null || (!value2.startsWith("\\"))){
                throw new ParseException("String value must be enclosed in backslashes");
            }
            
            while ((!value2.endsWith("\\"))){
            	String nVal = getNextToken();
            	if (nVal == null)
            		break;
            	value2 += " " + nVal;
            }

            if (!value2.endsWith("\\")) {
                throw new ParseException("String value must be enclosed in backslashes");
            }
            System.out.println("Operation: " + operation);
            System.out.println("String Value 2: " + value2);
        }
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

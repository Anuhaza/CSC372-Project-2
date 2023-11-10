import java.util.Scanner;

public class CustomParser {
    private static Scanner scanner;
    private static String input;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        System.out.print("Enter input: ");
        input = scanner.nextLine();

        try {
            parseComment();
            parsePrintStmt();
            parseWhileLoop();
            parseStmt2();

            if (input.isEmpty()) {
                System.out.println("Parsing successful!");
            } else {
                System.out.println("Parsing failed. Unexpected input: " + input);
            }
        } catch (ParseException e) {
            System.out.println("Parsing error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void parseComment() throws ParseException {
        match(":");
        parseCommentContent();
        match(":");
    }

    private static void parseCommentContent() throws ParseException {
        while (!input.isEmpty() && input.charAt(0) != ':') {
            input = input.substring(1);
        }
    }

    private static void parsePrintStmt() throws ParseException {
        match("*");
        parseVar();
        match("*");
    }

    private static void parseWhileLoop() throws ParseException {
        match("during");
        match("int");
        parseVar();
        match("loops");
        match("through");
        match("(");
        parseNum();
        match(",");
        parseNum();
        match(")");
    }

    private static void parseVar() throws ParseException {
        if (input.isEmpty() || !Character.isLetter(input.charAt(0))) {
            throw new ParseException("Variable expected");
        }

        input = input.substring(1);
    }

    private static void parseNum() throws ParseException {
        if (input.isEmpty() || !Character.isDigit(input.charAt(0))) {
            throw new ParseException("Number expected");
        }

        while (!input.isEmpty() && Character.isDigit(input.charAt(0))) {
            input = input.substring(1);
        }
    }

    private static void match(String expected) throws ParseException {
        if (input.startsWith(expected)) {
            input = input.substring(expected.length());
        } else {
            throw new ParseException("Expected '" + expected + "' but found '" + input.substring(0, Math.min(10, input.length())) + "'");
        }
    }

    private static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
}

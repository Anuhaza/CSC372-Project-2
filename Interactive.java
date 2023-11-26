/*
 * AUTHORS: Anisha Munjal, Anupama Hazarika, Jenan Meri
 * FILE: Interactive.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372 Fall 2023
 * Description: This file contains interactive system that allows user to type
 * in commands in our new language and see the results. 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Interactive {

	/**
	 * Default constructor
	 */
	public Interactive() {
		
	}

	/**
	 * Main method
	 * 
	 * @param args, String array containing command line arguments
	 */
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			System.out.println("Enter a line of source code of new language:");
			boolean showParsing = false;			
			try {	
				String line = br.readLine();
				MyLanguageParser parser = new MyLanguageParser(line, showParsing, 1);
				System.out.println("\t\t" + parser.parse() + "; \n");
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
}

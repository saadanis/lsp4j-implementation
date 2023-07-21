package com.saadanis.calc.calc;

import java.util.List;

//import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.saadanis.calc.ast.*;
import com.saadanis.calc.ast.CalcParser.SyntaxError;

public class CalcRun {
	
	public CalcRun() {
		
	}

	public static ParseTree parse(String text) throws Exception {
//		InputStream source = new FileInputStream(args[0]);
//		CalcLexer lexer = new CalcLexer(
//			CharStreams.fromFileName(args[0]));
		CalcLexer lexer = new CalcLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		ParseTree tree = parser.prog();
		
		return tree;
		
//		CommonTokenStream tokens = 
//		   new CommonTokenStream(lexer);
//		CalcParser parser = new CalcParser(tokens);
//		ParseTree tree = parser.prog();
//		return tree.toString();
//		
//		if (parser.getNumberOfSyntaxErrors() == 0) {
//			ExecVisitor exec = new ExecVisitor();
//			exec.visit(tree);
//		}
	}
	
	public static List<SyntaxError> parseErrors(String text) throws Exception {

		CalcLexer lexer = new CalcLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		parser.prog();
		
		return parser.getListOfSyntaxErrors();
		
//		List<String> errors = parser.progErrors();
//		return errors;
	}
	
	public static String testRun() {
		return "test text";
	}
}
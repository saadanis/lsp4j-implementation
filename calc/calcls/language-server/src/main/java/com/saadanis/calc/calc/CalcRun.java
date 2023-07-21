package com.saadanis.calc.calc;

import java.util.List;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.saadanis.calc.ast.*;
import com.saadanis.calc.ast.CalcParser.SyntaxError;

public class CalcRun {

	public static ParseTree parse(String text) throws Exception {
		
		CalcLexer lexer = new CalcLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		ParseTree tree = parser.prog();
		
		if (parser.getNumberOfSyntaxErrors() == 0) {
			ExecVisitor exec = new ExecVisitor();
			exec.visit(tree);
		}
		
		return tree;
	}
	
	public static List<SyntaxError> parseErrors(String text) throws Exception {

		CalcLexer lexer = new CalcLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		
		parser.prog();
		
		return parser.getListOfSyntaxErrors();
	}
}
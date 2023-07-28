package mods;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ast.FunLexer;
import fun.FunCheckerVisitor;
import fun.FunEncoderVisitor;
import fun.FunRun;
import fun.SVM;
import mods.ModifiedFunParser.ContextualError;
import mods.ModifiedFunParser.ContextualWarning;
import mods.ModifiedFunParser.DiagnosticError;
import mods.ModifiedFunParser.SyntaxError;

public class ModifiedFunRun extends FunRun {
	
	private static boolean tracing = false;
	
    public static List<DiagnosticError> diagnostics (String text) throws Exception {
    	
    	List<DiagnosticError> errors = new ArrayList<>();
    	
    	FunLexer lexer = new FunLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ModifiedFunParser parser = new ModifiedFunParser(tokens);
		ParseTree tree = parser.program();
	
		List<SyntaxError> syntaxErrors = parser.getListOfSyntaxErrors();
    	
    	FunCheckerVisitor checker = new FunCheckerVisitor(tokens);
    	checker.visit(tree);
    	
    	List<ContextualError> contextualErrors = checker.getListOfContextualErrors();
    	List<ContextualWarning> contextualWarnings = checker.getListOfContextualWarnings();
    	
    	errors.addAll(syntaxErrors);
    	errors.addAll(contextualErrors);
    	if (checker.getNumberOfContextualErrors() == 0)
    		errors.addAll(contextualWarnings);
    	
    	return errors;
    }
    
    public static String runTheEntireThing (String text) throws Exception {
    	
    	FunLexer lexer = new FunLexer(CharStreams.fromString(text));
    	CommonTokenStream tokens = new CommonTokenStream(lexer);
    	
    	ModifiedFunParser parser = new ModifiedFunParser(tokens);
    	ParseTree tree = parser.program();
    	
    	FunCheckerVisitor checker = new FunCheckerVisitor(tokens);
    	checker.visit(tree);
    	
    	FunEncoderVisitor encoder = new FunEncoderVisitor();
    	encoder.visit(tree);
    	SVM objectprog = encoder.getSVM();
    	
    	objectprog.interpret(tracing);
    	
		return null;
  
    }
}

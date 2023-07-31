package mods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.Position;

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
	private static boolean showWarningsWithErrors = true;
	
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

    	if (showWarningsWithErrors)
    		errors.addAll(contextualWarnings);
    	else
    		if (errors.isEmpty())
    			errors.addAll(contextualWarnings);
    	
    	return errors;
    }
    
    public static List<String> test(String filename, Position position) {
    	
		try {
			
			FunLexer lexer = new FunLexer(CharStreams.fromFileName(filename));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			ModifiedFunParser parser = new ModifiedFunParser(tokens);
			FunCheckerVisitor checker = new FunCheckerVisitor(tokens, position);
			
			ParseTree tree = parser.program();
			checker.visit(tree);
			
			return checker.getListOfCompletionVariables();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.err.println(e.getLocalizedMessage());
		}
		
		return new ArrayList<String>();
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

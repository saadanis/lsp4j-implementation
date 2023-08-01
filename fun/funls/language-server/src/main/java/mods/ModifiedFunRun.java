package mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.Position;

import ast.FunLexer;
import fun.FunCheckerVisitor;
import fun.FunEncoderVisitor;
import fun.FunRun;
import fun.SVM;
import fun.Type;
import mods.ModifiedFunParser.ContextualError;
import mods.ModifiedFunParser.ContextualWarning;
import mods.ModifiedFunParser.DiagnosticError;
import mods.ModifiedFunParser.SyntaxError;

public class ModifiedFunRun extends FunRun {
	
	private static boolean tracing = false;
	private static boolean showWarningsWithErrors = true;
	
    public static List<DiagnosticError> diagnostics (String text) throws Exception {
    	
    	List<DiagnosticError> errors = new ArrayList<>();
    	
    	if (text.length() > 0)
    		
	    	try {
	    	
		    	FunLexer lexer = new FunLexer(CharStreams.fromString(text));
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				
				ModifiedFunParser parser = new ModifiedFunParser(tokens);
				ParseTree tree = parser.program();
			
				List<SyntaxError> syntaxErrors = parser.getListOfSyntaxErrors();
				errors.addAll(syntaxErrors);
		    	
		    	FunCheckerVisitor checker = new FunCheckerVisitor(tokens);
		    	checker.visit(tree);
		    	
		    	List<ContextualError> contextualErrors = checker.getListOfContextualErrors();
		    	errors.addAll(contextualErrors);
		    	
		    	List<ContextualWarning> contextualWarnings = checker.getListOfContextualWarnings();
	
	    	if (showWarningsWithErrors)
	    		errors.addAll(contextualWarnings);
	    	else
	    		if (errors.isEmpty())
	    			errors.addAll(contextualWarnings);
	    	
	    	} catch (Exception e) {
	    		
	    		System.err.println("ModifiedFunRun:diagnostics: " + e.getLocalizedMessage());
	    	}
    	
    	return errors;
    }
    
    public static HashMap<String,String> completion(String text, Position position) {
    	
    	try {
    		
	    	FunLexer lexer = new FunLexer(CharStreams.fromString(text));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			
			ModifiedFunParser parser = new ModifiedFunParser(tokens);
			ParseTree tree = parser.program();
			
			FunCheckerVisitor checker = new FunCheckerVisitor(tokens, position);
			checker.visit(tree);
			
			return checker.getCompletionItems();
		
    	} catch (Exception e) {
			
    		System.err.println("ModifiedFunRun:completion: " + e.getLocalizedMessage());
		}
    	
    	return new HashMap<String,String>();
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

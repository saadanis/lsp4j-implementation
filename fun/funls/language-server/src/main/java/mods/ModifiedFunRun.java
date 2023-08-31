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
import mods.DiagnosticError.ContextualError;
import mods.DiagnosticError.ContextualWarning;
import mods.DiagnosticError.SyntaxError;

/**
 * The ModifiedFunRun class provides extensions to FunRun to support diagnostics, completion, and execution of Fun code.
 */
public class ModifiedFunRun extends FunRun {
	
	private static boolean tracing = false;
	private static boolean showWarningsWithErrors = true;
	
	/**
     * Analyzes the given text for syntax and contextual errors, returning a list of diagnostic errors.
     *
     * @param text The input Fun code to analyze.
     * @return A list of DiagnosticError instances representing syntax and contextual errors.
     * @throws Exception If an exception occurs during analysis.
     */
    public static List<DiagnosticError> diagnostics (String text) throws Exception {
    	
    	List<DiagnosticError> errors = new ArrayList<>();
    	
    	if (text.length() > 0)
    		
	    	try {
	    	
	    		// Convert to token stream.
		    	FunLexer lexer = new FunLexer(CharStreams.fromString(text));
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				
				// Parse the token stream.
				ModifiedFunParser parser = new ModifiedFunParser(tokens);
				ParseTree tree = parser.program();
			
				// Get list of syntax errors.
				List<SyntaxError> syntaxErrors = parser.getListOfSyntaxErrors();
				errors.addAll(syntaxErrors);
		    	
				if (tree != null) {
					
					// Visit tree.
					FunCheckerVisitor checker = new FunCheckerVisitor(tokens);
			    	checker.visit(tree);
			    	
			    	// Get list of contextual errors.
			    	List<ContextualError> contextualErrors = checker.getListOfContextualErrors();
			    	errors.addAll(contextualErrors);
			    	
			    	// Get list of contextual warnings.
			    	List<ContextualWarning> contextualWarnings = checker.getListOfContextualWarnings();
			    	
			    	// Include warnings based on the configuration.
			    	if (showWarningsWithErrors)
			    		errors.addAll(contextualWarnings);
			    	else
			    		if (errors.isEmpty())
			    			errors.addAll(contextualWarnings);
				}

	    	} catch (Exception e) {
	    		System.err.println("ModifiedFunRun:diagnostics: " + e.getLocalizedMessage());
	    	}
    	
    	return errors;
    }
    
    /**
     * Retrieves completion items for the given text and position.
     *
     * @param text     The input Fun code to analyze for completions.
     * @param position The position in the code where completions are requested.
     * @return A map of completion items.
     */
    public static HashMap<String,String> completion(String text, Position position) {
    	
    	try {
    		
    		// Convert to token stream.
	    	FunLexer lexer = new FunLexer(CharStreams.fromString(text));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			
			// Parse the token stream.
			ModifiedFunParser parser = new ModifiedFunParser(tokens);
			ParseTree tree = parser.program();
			
			// Visit tree.
			if (tree != null) {
				FunCheckerVisitor checker = new FunCheckerVisitor(tokens, position);
				checker.visit(tree);
				return checker.getCompletionItems();
			}
		
    	} catch (Exception e) {
			
    		System.err.println("ModifiedFunRun:completion: " + e.getLocalizedMessage());
		}
    	
    	return new HashMap<String,String>();
    }
    
    /**
     * Executes the given Fun code.
     *
     * @param text The input Fun code to execute.
     * @return Always returns `null`.
     * @throws Exception If an exception occurs during execution.
     */
    public static String run (String text) throws Exception {
    	
    	FunLexer lexer = new FunLexer(CharStreams.fromString(text));
    	CommonTokenStream tokens = new CommonTokenStream(lexer);
    	
    	ModifiedFunParser parser = new ModifiedFunParser(tokens);
    	ParseTree tree = parser.program();
    	
    	FunCheckerVisitor checker = new FunCheckerVisitor(tokens);
    	checker.visit(tree);
    	
    	FunEncoderVisitor encoder = new FunEncoderVisitor();
    	encoder.visit(tree);
    	SVM objectprog = encoder.getSVM();
    	
    	// Execute code.
    	objectprog.interpret(tracing);
    	
		return null;
  
    }
}

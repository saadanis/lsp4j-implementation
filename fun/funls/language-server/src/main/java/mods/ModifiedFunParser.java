package mods;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import ast.FunParser;
import mods.DiagnosticError.SyntaxError;

/**
 * The ModifiedFunParser class extends the FunParser class and provides additional functionality to capture syntax errors during parsing.
 */
public class ModifiedFunParser extends FunParser {
	
    // List to store syntax errors encountered during parsing.
	protected List<SyntaxError> _listOfSyntaxErrors = new ArrayList<>(); 

    /**
     * Constructor for ModifiedFunParser.
     *
     * @param input The token stream containing the input tokens.
     */
	public ModifiedFunParser(TokenStream input) {
		super(input);
	}
	
	@Override
	public void notifyErrorListeners(Token offendingToken, String msg, RecognitionException e) {
		
		// Call the parent class's method to notify error listeners.
		super.notifyErrorListeners(offendingToken, msg, e);
		
		 // Add the syntax error to the list
		_listOfSyntaxErrors.add(new SyntaxError(offendingToken, msg));
	}
	
    /**
     * Get the list of syntax errors encountered during parsing.
     *
     * @return The list of syntax errors.
     */
	public List<SyntaxError> getListOfSyntaxErrors() {
		return _listOfSyntaxErrors;
	}
	
	@Override
	public void reset() {
		
		// Call the parent class's method to reset the parser.
		super.reset();
		
		// Reset the list of syntax errors.
		_listOfSyntaxErrors = new ArrayList<>();
	}
}

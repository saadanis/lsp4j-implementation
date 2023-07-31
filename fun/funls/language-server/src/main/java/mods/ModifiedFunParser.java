package mods;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import ast.FunParser;

public class ModifiedFunParser extends FunParser {
	
	protected List<SyntaxError> _listOfSyntaxErrors = new ArrayList<>(); 

	public ModifiedFunParser(TokenStream input) {
		super(input);
	}
	
	@Override
	public void notifyErrorListeners(Token offendingToken, String msg, RecognitionException e) {
		
		_syntaxErrors++;
		int line = -1;
		int charPositionInLine = -1;
		line = offendingToken.getLine();
		charPositionInLine = offendingToken.getCharPositionInLine();

		ANTLRErrorListener listener = getErrorListenerDispatch();
		listener.syntaxError(this, offendingToken, line, charPositionInLine, msg, e);
		
		_listOfSyntaxErrors.add(new SyntaxError(offendingToken, msg));
	}
	
	public List<SyntaxError> getListOfSyntaxErrors() {
		return _listOfSyntaxErrors;
	}
	
	@Override
	public void reset() {
		super.reset();
		_listOfSyntaxErrors = new ArrayList<>();
	}
	
	public static class DiagnosticError {
		public String message;
		public int line;
		public int charStartPositionInLine;
		public int charEndPositionInLine;
	}
	
	public static class SyntaxError extends DiagnosticError {
		public SyntaxError(Token token, String message) {
			this.message = message;
			this.line = token.getLine();
			this.charStartPositionInLine = token.getCharPositionInLine();
			this.charEndPositionInLine = charStartPositionInLine + 1 +
					(token.getStopIndex() - token.getStartIndex());
		}
	}
	
	public static class ContextualError extends DiagnosticError {
		public ContextualError(String message, int line, int charStartPositionInLine, int charEndPositionInLine) {
			this.message = message;
			this.line = line;
			this.charStartPositionInLine = charStartPositionInLine;
			this.charEndPositionInLine = charEndPositionInLine + 1;
		}
	}
	
	public static class ContextualWarning extends DiagnosticError {
		public ContextualWarning(String message, int line, int charStartPositionInLine, int charEndPositionInLine) {
			this.message = message;
			this.line = line;
			this.charStartPositionInLine = charStartPositionInLine;
//			this.charEndPositionInLine = charEndPositionInLine + 1;
			this.charEndPositionInLine = 1000;
		}
	}
}

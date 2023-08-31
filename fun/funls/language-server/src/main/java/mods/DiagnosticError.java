package mods;

import org.antlr.v4.runtime.Token;

/**
 * The DiagnosticError class represents a diagnostic message that includes
 * an error or warning message and its location in the source code.
 */
public class DiagnosticError {
	public String message; // The error or warning message.
	public int line; // The line number where the error or warning occurred.
	public int charStartPositionInLine; // The starting character position in the line.
	public int charEndPositionInLine; // The ending character position in the line.
	
	/**
     * The SyntaxError subclass of DiagnosticError represents a syntax error in the source code.
     */
	public static class SyntaxError extends DiagnosticError {
		public SyntaxError(Token token, String message) {
			this.message = message;
			this.line = token.getLine();
			this.charStartPositionInLine = token.getCharPositionInLine();
			this.charEndPositionInLine = charStartPositionInLine + 1 +
					(token.getStopIndex() - token.getStartIndex());
		}
	}
	
	/**
     * The ContextualError subclass of DiagnosticError represents a contextual error in the source code.
     */
	public static class ContextualError extends DiagnosticError {
		public ContextualError(String message, int line, int charStartPositionInLine, int charEndPositionInLine) {
			this.message = message;
			this.line = line;
			this.charStartPositionInLine = charStartPositionInLine;
			this.charEndPositionInLine = charEndPositionInLine + 1;
		}
	}

	/**
     * The ContextualWarning subclass of DiagnosticError represents a contextual warning in the source code..
     */
	public static class ContextualWarning extends DiagnosticError {
		public ContextualWarning(String message, int line, int charStartPositionInLine, int charEndPositionInLine) {
			this.message = message;
			this.line = line;
			this.charStartPositionInLine = charStartPositionInLine;
			this.charEndPositionInLine = 1000; // A large value to indicate end of line.
		}
	}
}

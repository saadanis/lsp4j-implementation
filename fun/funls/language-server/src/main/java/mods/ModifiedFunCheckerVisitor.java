package mods;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import fun.FunCheckerVisitor;
import mods.ModifiedFunParser.ContextualError;

public class ModifiedFunCheckerVisitor extends FunCheckerVisitor {
	
	private List<ContextualError> contextualErrors = new ArrayList<>();

	public ModifiedFunCheckerVisitor(CommonTokenStream toks) {
		super(toks);
	}
	
	@Override
	protected void reportError(String message, ParserRuleContext ctx) {
		
		// Function as written in FunCheckerVisitor.
		Interval interval = ctx.getSourceInterval();
	    Token start = tokens.get(interval.a);
	    Token finish = tokens.get(interval.b);
	    int startLine = start.getLine();
	    int startCol = start.getCharPositionInLine();
	    int finishLine = finish.getLine();
	    int finishCol = finish.getCharPositionInLine();
	    System.err.println(startLine + ":" + startCol + "-" +
	    		finishLine + ":" + finishCol + " " + message);
		errorCount++;
		
		// Add error to list of contextual errors.
		contextualErrors.add(new ContextualError(message, startLine, startCol, finishCol));
	}

	public List<ContextualError> getListOfContextualErrors() {
		
		// Return list of contextual errors.
		return contextualErrors;
	}
}

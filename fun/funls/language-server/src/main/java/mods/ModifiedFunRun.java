package mods;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ast.FunLexer;
import fun.FunRun;
import mods.ModifiedFunParser.ContextualError;
import mods.ModifiedFunParser.DiagnosticError;
import mods.ModifiedFunParser.SyntaxError;

public class ModifiedFunRun extends FunRun {
	
    public static List<DiagnosticError> diagnostics (String text) throws Exception {
    	
    	List<DiagnosticError> errors = new ArrayList<>();
    	
    	FunLexer lexer = new FunLexer(CharStreams.fromString(text));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ModifiedFunParser parser = new ModifiedFunParser(tokens);
		ParseTree tree = parser.program();
	
		List<SyntaxError> syntaxErrors = parser.getListOfSyntaxErrors();
    	
    	ModifiedFunCheckerVisitor checker = new ModifiedFunCheckerVisitor(tokens);
    	checker.visit(tree);
    	
    	List<ContextualError> contextualErrors = checker.getListOfContextualErrors();
    	
    	errors.addAll(syntaxErrors);
    	errors.addAll(contextualErrors);
    	
    	return errors;
    }
}

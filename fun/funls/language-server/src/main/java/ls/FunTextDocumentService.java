package ls;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.antlr.v4.runtime.Vocabulary;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import mods.ModifiedFunParser;
import mods.ModifiedFunParser.ContextualError;
import mods.ModifiedFunParser.ContextualWarning;
import mods.ModifiedFunParser.DiagnosticError;
import mods.ModifiedFunParser.SyntaxError;
import mods.ModifiedFunRun;

//import com.saadanis.calc.ast.CalcParser.SyntaxError;
//import com.saadanis.calc.calc.CalcRun;

public class FunTextDocumentService implements TextDocumentService {

	private FunLanguageServer languageServer;
	private FunClientLogger clientLogger;
	
	private String documentText = "";
	private String[] keywords = { "else", "false", "true", "func", "if",
			"proc", "return", "not", "while", "bool", "int"};
	private String[] types = {"bool", "int"};
	
	public FunTextDocumentService(FunLanguageServer languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = FunClientLogger.getInstance();
    }
	
	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		
		String fileUri = params.getTextDocument().getUri();
		
		this.clientLogger.logMessage("Operation '" + "text/didOpen" +
                "' {fileUri: '" + fileUri + "'} opened");
		if (params.getTextDocument() != null) {
			documentText = params.getTextDocument().getText();
			parseAndPublishDiagnostics(fileUri, documentText);
		}
	
	}
	
	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		
		String fileUri = params.getTextDocument().getUri();
		
		this.clientLogger.logMessage("Operation '" + "text/didChange" +
                "' {fileUri: '" + fileUri + "'} Changed");
		
		if (!params.getContentChanges().isEmpty()) {
			String changedText = params.getContentChanges().get(0).getText();
			documentText = changedText;
			parseAndPublishDiagnostics(fileUri, documentText);
		}
		
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		this.clientLogger.logMessage("Operation '" + "text/didClose" +
                "' {fileUri: '" + params.getTextDocument().getUri() + "'} Closed");

	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		
		String fileUri = params.getTextDocument().getUri();
		
        this.clientLogger.logMessage("Operation '" + "text/didSave" +
                "' {fileUri: '" + fileUri + "'} Saved");
	}
	
	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams completionParams) {
		
        return CompletableFuture.supplyAsync(() -> {
        	
            List<CompletionItem> completionItems = new ArrayList<>();
            
			Position position = completionParams.getPosition();
			HashMap<String,String> completionVariables = ModifiedFunRun.completion(documentText, position);
          
			completionVariables.forEach((id, type) -> {
				CompletionItem completionItem = new CompletionItem();
                completionItem.setLabel(id);
                
                if (type.equals("int") || type.equals("bool")) {
                	completionItem.setKind(CompletionItemKind.Variable);
                	completionItem.setInsertText(id);
                } else {
                	completionItem.setKind(CompletionItemKind.Function);
                	if (type.startsWith("void")) {
                		completionItem.setInsertText(id + "()");
                	} else {
                		completionItem.setInsertText(id + "($1)");
                		completionItem.setInsertTextFormat(InsertTextFormat.Snippet);
                	}
                }
                
                completionItems.add(completionItem);
			});
            
            for (String keyword: keywords) {
            	CompletionItem completionItem = new CompletionItem();
                completionItem.setInsertText(keyword);
                completionItem.setLabel(keyword);
                completionItem.setKind(CompletionItemKind.Keyword);
                completionItems.add(completionItem);
            }
            
            for (String type: types) {
            	CompletionItem completionItem = new CompletionItem();
                completionItem.setInsertText(type);
                completionItem.setLabel(type);
                completionItem.setKind(CompletionItemKind.Class);
                completionItems.add(completionItem);
            }
            
//         	main function snippet generation completion item.
//            {
//                CompletionItem completionItem = new CompletionItem();
//                completionItem.setInsertText("main():\n\t$1\n.");
//                completionItem.setLabel("main()");
//                completionItem.setDetail("Generate main() snippet.");
//                completionItem.setKind(CompletionItemKind.Snippet);
//                completionItem.setInsertTextFormat(InsertTextFormat.Snippet);
//                completionItems.add(completionItem);
//            }
            
            // Return the list of completion items.
            return Either.forLeft(completionItems);
        });
	}
	

	@Override
	public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
		// TODO Auto-generated method stub
		
		this.clientLogger.logMessage(params.toString());
		
		return TextDocumentService.super.semanticTokensFull(params);
	}

	
	private void parseAndPublishDiagnostics(String fileUri, String text) {
		
		List<Diagnostic> diagnostics = new ArrayList<>();
		
		try {
			
			List <DiagnosticError> errors = ModifiedFunRun.diagnostics(text);
//			
//			// For each syntax error, log the error and create a diagnostic.
			for (DiagnosticError error: errors) {
				
				// Get the error information.
				String message = error.message;
				int line = error.line;
				int charStartPositionInLine = error.charStartPositionInLine;
				int charEndPositionInLine = error.charEndPositionInLine;
				
				DiagnosticSeverity severity = DiagnosticSeverity.Error;
				String errorType = "";
				if (error instanceof ContextualWarning) {
					severity = DiagnosticSeverity.Warning;
					errorType = "Warning";
				}
				else if (error instanceof ContextualError)
					errorType = "Context";
				else
					errorType = "Syntax";
				
				// Create a diagnostic for the error.
		        diagnostics.add(new Diagnostic(
		        		new Range(
		        				new Position(
		        						line - 1, charStartPositionInLine),
		        				new Position(
		        						line - 1, charEndPositionInLine)
		        				),
		        		message,
		        		severity,
		        		errorType));
			}

		} catch (Exception e) {
			this.clientLogger.logMessage("Caught Exception: " + e.getMessage());
		} finally {
	        PublishDiagnosticsParams publishDiagnosticsParams = new PublishDiagnosticsParams(fileUri, diagnostics);
	        languageServer.languageClient.publishDiagnostics(publishDiagnosticsParams);
		}
	}

}

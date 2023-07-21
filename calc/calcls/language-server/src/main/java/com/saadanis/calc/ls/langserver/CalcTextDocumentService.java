package com.saadanis.calc.ls.langserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import com.saadanis.calc.ast.CalcParser.SyntaxError;
import com.saadanis.calc.calc.CalcRun;

public class CalcTextDocumentService implements TextDocumentService {

	private CalcLanguageServer languageServer;
	private LSClientLogger clientLogger;
	
	public CalcTextDocumentService(CalcLanguageServer languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = LSClientLogger.getInstance();
    }
	
	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		// TODO Auto-generated method stub
		this.clientLogger.logMessage("Operation '" + "text/didOpen" +
                "' {fileUri: '" + params.getTextDocument().getUri() + "'} opened");

	}
	
	private void parseAndPublishDiagnostics(String fileUri, String text) {
		
		List<Diagnostic> diagnostics = new ArrayList<>();
		
		try {
			List<SyntaxError> errors = CalcRun.parseErrors(text);
			
			// For each syntax error, log the error and create a diagnostic.
			for (SyntaxError error: errors) {
				
				String message = error.msg;
				int line = error.line - 1;
				
				int firstSingleQuote = message.indexOf("'");
				int secondSingleQuote = message.indexOf("'", firstSingleQuote + 1) - 1;
				
				int charStartPositionInLine = error.charPositionInLine;
				int charEndPositionInLine = charStartPositionInLine + (secondSingleQuote - firstSingleQuote);
				
				
				this.clientLogger.logMessage("firstSingleQuote: " + firstSingleQuote + "\n" +
						"secondSingleQuote: " + secondSingleQuote);

//				if (firstSingleQuote != -1) {
//					secondSingleQuote = message.indexOf("'", firstSingleQuote);
//				}
//				if (secondSingleQuote != -1)
//					charEndPositionInLine = charStartPositionInLine + (secondSingleQuote - firstSingleQuote);
//				
				
				// Log the error.
				this.clientLogger.logMessage("error: " +
					line + ":" +
					charStartPositionInLine + ":" +
					charEndPositionInLine + " " +
					message);
				
				// Create a diagnostic for the error.
		        diagnostics.add(new Diagnostic(
		        		new Range(
		        				new Position(
		        						line, charStartPositionInLine),
		        				new Position(
		        						line, charEndPositionInLine)
		        				),
		        		message,
		        		DiagnosticSeverity.Error,
		        		""));
			}
		} catch (Exception e) {
			this.clientLogger.logMessage("Caught Exception: " + e.getMessage());
		} finally {
	        PublishDiagnosticsParams publishDiagnosticsParams = new PublishDiagnosticsParams(fileUri, diagnostics);
	        languageServer.languageClient.publishDiagnostics(publishDiagnosticsParams);
		}
	}
	
	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		
		String fileUri = params.getTextDocument().getUri();
		
		this.clientLogger.logMessage("Operation '" + "text/didChange" +
                "' {fileUri: '" + fileUri + "'} Changed");
		
		if (!params.getContentChanges().isEmpty()) {
			String changedText = params.getContentChanges().get(0).getText();
			parseAndPublishDiagnostics(fileUri, changedText);
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
		// Provide completion item.
        return CompletableFuture.supplyAsync(() -> {
            List<CompletionItem> completionItems = new ArrayList<>();
            try {
            	
                // Sample Completion item for sayHello
                CompletionItem completionItem = new CompletionItem();
                // Define the text to be inserted in to the file if the completion item is selected.
                completionItem.setInsertText("set m = (c*2) + (e*8) // oh wow, a comment.");
                // Set the label that shows when the completion drop down appears in the Editor.
                completionItem.setLabel("set m");
                // Set the completion kind. This is a snippet.
                // That means it replace character which trigger the completion and
                // replace it with what defined in inserted text.
                completionItem.setKind(CompletionItemKind.Snippet);
                // This will set the details for the snippet code which will help user to
                // understand what this completion item is.
                completionItem.setDetail("set m\n this will complete a card-coded function.");

                // Add the sample completion item to the list.
                completionItems.add(completionItem);
            } catch (Exception e) {
                //TODO: Handle the exception.
            }

            // Return the list of completion items.
            return Either.forLeft(completionItems);
        });
	}

}

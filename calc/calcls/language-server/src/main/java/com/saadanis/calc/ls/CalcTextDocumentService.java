package com.saadanis.calc.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import com.saadanis.calc.ast.CalcParser.SyntaxError;
import com.saadanis.calc.calc.CalcRun;

public class CalcTextDocumentService implements TextDocumentService {

	private CalcLanguageServer languageServer;
	private CalcClientLogger clientLogger;
	
	public CalcTextDocumentService(CalcLanguageServer languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = CalcClientLogger.getInstance();
    }
	
	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		this.clientLogger.logMessage("Operation '" + "text/didOpen" +
                "' {fileUri: '" + params.getTextDocument().getUri() + "'} opened");

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
                CompletionItem completionItem = new CompletionItem();
                completionItem.setInsertText("set m = (c*2) + (e*8) // oh wow, a comment.");
                completionItem.setLabel("set m");
                completionItem.setKind(CompletionItemKind.Snippet);
                completionItem.setDetail("set m\n this will complete a card-coded function.");
                completionItems.add(completionItem);
            } catch (Exception e) {
                //TODO: Handle the exception.
            }

            // Return the list of completion items.
            return Either.forLeft(completionItems);
        });
	}
	
	private void parseAndPublishDiagnostics(String fileUri, String text) {
		
		List<Diagnostic> diagnostics = new ArrayList<>();
		
		try {
			
			// Get the list of syntax errors.
			List<SyntaxError> errors = CalcRun.parseErrors(text);
			
			// For each syntax error, log the error and create a diagnostic.
			for (SyntaxError error: errors) {
				
				// Get the error message and line number.
				String message = error.msg;
				Token token = error.offendingToken;
				
				int line = token.getLine() - 1;
				
				int charStartPositionInLine = token.getCharPositionInLine();
				int charEndPositionInLine = charStartPositionInLine + 1 +
						(token.getStopIndex() - token.getStartIndex());
				
				// Log the error.
				this.clientLogger.logMessage("error: " +
					line + ":" +
					charStartPositionInLine + ":" +
					charEndPositionInLine + " " +
					message + " " +
					error.offendingToken.getStartIndex() + ":" +
					error.offendingToken.getStopIndex());
				
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
			this.clientLogger.logMessage("Caught Exception: " + e.getLocalizedMessage());
		} finally {
	        PublishDiagnosticsParams publishDiagnosticsParams = new PublishDiagnosticsParams(fileUri, diagnostics);
	        languageServer.languageClient.publishDiagnostics(publishDiagnosticsParams);
		}
	}

}

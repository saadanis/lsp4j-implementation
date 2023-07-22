package ls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import ast.FunParser.DiagnosticError;
import ast.FunParser.SyntaxError;
import fun.FunRun;

//import com.saadanis.calc.ast.CalcParser.SyntaxError;
//import com.saadanis.calc.calc.CalcRun;

public class FunTextDocumentService implements TextDocumentService {

	private FunLanguageServer languageServer;
	private FunClientLogger clientLogger;
	
	public FunTextDocumentService(FunLanguageServer languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = FunClientLogger.getInstance();
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
//            try {
//                CompletionItem completionItem = new CompletionItem();
//                completionItem.setInsertText("set m = (c*2) + (e*8) // oh wow, a comment.");
//                completionItem.setLabel("set m");
//                completionItem.setKind(CompletionItemKind.Snippet);
//                completionItem.setDetail("set m\n this will complete a card-coded function.");
//                completionItems.add(completionItem);
//            } catch (Exception e) {
//                //TODO: Handle the exception.
//            }

            // Return the list of completion items.
            return Either.forLeft(completionItems);
        });
	}
	
	private void parseAndPublishDiagnostics(String fileUri, String text) {
		
		List<Diagnostic> diagnostics = new ArrayList<>();
		
		try {
			
//			// Get the list of syntax errors.
//			List<SyntaxError> errors = FunParse.syntacticParse(text);
			// TODO: contextual analysis.
//			this.clientLogger.logMessage("contextual errors: " + FunCheck.contextualDiagnostics(text));
			
			List <DiagnosticError> errors = FunRun.diagnostics(text);
//			
//			// For each syntax error, log the error and create a diagnostic.
			for (DiagnosticError error: errors) {
				
				// Get the error information.
				String message = error.message;
				int line = error.line;
				int charStartPositionInLine = error.charStartPositionInLine;
				int charEndPositionInLine = error.charEndPositionInLine;
				
				String errorType = "";
				if (error instanceof SyntaxError)
					errorType = "SyntaxError";
				else
					errorType = "ContextualError";
				

				// Log the error.
				this.clientLogger.logMessage(
					errorType + ": " +
					line + ":" +
					charStartPositionInLine + ":" +
					charEndPositionInLine + " " +
					message);
				
				// Create a diagnostic for the error.
		        diagnostics.add(new Diagnostic(
		        		new Range(
		        				new Position(
		        						line - 1, charStartPositionInLine),
		        				new Position(
		        						line - 1, charEndPositionInLine)
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

}

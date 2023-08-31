package ls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import mods.DiagnosticError;
import mods.DiagnosticError.ContextualError;
import mods.DiagnosticError.ContextualWarning;
import mods.ModifiedFunRun;

/**
 * The FunTextDocumentService class implements the TextDocumentService interface to handle various
 * operations related to text documents, such as opening, changing, and closing a document, as well
 * as providing completion and publishing diagnostics.
 */
public class FunTextDocumentService implements TextDocumentService {

	private FunLanguageServer languageServer;
	private FunClientLogger clientLogger;
	
	private String documentText = "";
	
	// Hard-coded keywords and types of the Fun language.
	private String[] keywords = { "else", "false", "true", "func", "if", "proc", "return", "not", "while"};
	private String[] types = {"bool", "int"};
	
	/**
     * Constructor for FunWorkspaceService.
     */
	public FunTextDocumentService(FunLanguageServer languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = FunClientLogger.getInstance();
    }
	
	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		
		this.clientLogger.logMessage("FunTextDocumentService:didOpen");
		
		// Get the URI of the opened text document.
		String fileUri = params.getTextDocument().getUri();
		
		// Check if the text document is not null.
		if (params.getTextDocument() != null) {
			
	        // Retrieve the content of the text document.
			documentText = params.getTextDocument().getText();
			
			// Parse the content and publish diagnostics.
			parseAndPublishDiagnostics(fileUri, documentText);
		}
	
	}
	
	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		
		this.clientLogger.logMessage("FunTextDocumentService:didChange");
		
		// Get the URI of the changed text document.
		String fileUri = params.getTextDocument().getUri();
		
		// Check if there are content changes.
		if (!params.getContentChanges().isEmpty()) {
			
			// Get the changed text content.
			String changedText = params.getContentChanges().get(0).getText();
			documentText = changedText;
			
			// Parse the changed content and publish diagnostics.
			parseAndPublishDiagnostics(fileUri, documentText);
		}
		
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		this.clientLogger.logMessage("FunTextDocumentService:didClose");
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		this.clientLogger.logMessage("FunTextDocumentService:didSave");
	}
	
	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams completionParams) {
		
        return CompletableFuture.supplyAsync(() -> {
        	
        	// Create a list to store completion items.
            List<CompletionItem> completionItems = new ArrayList<>();
            
            // Get the current cursor position in the text document.
			Position position = completionParams.getPosition();
			
	        // Get a map of completion variables and their types.
			HashMap<String,String> completionVariables = ModifiedFunRun.completion(documentText, position);
          
	        // Iterate through the completion variables and create corresponding completion items.
			completionVariables.forEach((id, type) -> {
				
	            // Create a new completion item.
				CompletionItem completionItem = new CompletionItem();
                completionItem.setLabel(id);
                
                // Determine the kind of completion item based on the variable type.
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
                
                // Add the completion item to the list.
                completionItems.add(completionItem);
			});
            
			// Add predefined keywords as completion items.
            for (String keyword: keywords) {
                completionItems.add(createCompletionItem(keyword, CompletionItemKind.Keyword));
            }
            
            // Add predefined types as completion items.
            for (String type: types) {
                completionItems.add(createCompletionItem(type, CompletionItemKind.Class));
            }
            
            // Return the list of completion items.
            return Either.forLeft(completionItems);
        });
	}

	/**
	 * Creates a CompletionItem based on the provided text and kind.
	 *
	 * @param text The text to be inserted when the completion item is selected.
	 * @param kind The kind of completion item, indicating its category (variable, function, etc.).
	 * @return A new CompletionItem with the specified properties.
	 */
	private CompletionItem createCompletionItem(String text, CompletionItemKind kind) {
		
		// Create a new CompletionItem instance.
		CompletionItem completionItem = new CompletionItem();
		
		// Set the completionItems attributes.
		completionItem.setInsertText(text);
        completionItem.setLabel(text);
        completionItem.setKind(kind);
        
		return completionItem;
	}

	/**
	 * Parses the provided text, detects errors using ModifiedFunRun, and publishes diagnostics for the detected errors.
	 *
	 * @param fileUri The URI of the text document.
	 * @param text The content of the text document.
	 */
	private void parseAndPublishDiagnostics(String fileUri, String text) {
		
		List<Diagnostic> diagnostics = new ArrayList<>();
		
		try {
			
			List <DiagnosticError> errors = ModifiedFunRun.diagnostics(text);

			// For each syntax error, log the error and create a diagnostic.
			for (DiagnosticError error: errors) {
				
				// Get the error information.
				String message = error.message;
				int line = error.line;
				int charStartPositionInLine = error.charStartPositionInLine;
				int charEndPositionInLine = error.charEndPositionInLine;
				
				// Determine the severity and type of the error.
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
			
			// Publish the detected diagnostics to the language client.
	        PublishDiagnosticsParams publishDiagnosticsParams = new PublishDiagnosticsParams(fileUri, diagnostics);
	        languageServer.languageClient.publishDiagnostics(publishDiagnosticsParams);
		}
	}

}

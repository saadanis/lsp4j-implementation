package ls;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
			String text = params.getTextDocument().getText();
			parseAndPublishDiagnostics(fileUri, text);
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
			try {
//				ModifiedFunRun.runTheEntireThing(changedText);
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				this.clientLogger.logMessage(e.getLocalizedMessage());
			}
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
//        this.clientLogger.logMessage(params.toString());

	}
	
	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams completionParams) {
		// Provide completion item.
		
		System.err.println(completionParams.getPosition().getLine());
		System.err.println(completionParams.getTextDocument().getUri());
		
        return CompletableFuture.supplyAsync(() -> {
            List<CompletionItem> completionItems = new ArrayList<>();
            
            try {
				String filename = new File(new URI(completionParams.getTextDocument().getUri())).getPath();
				Position position = completionParams.getPosition();
				List<String> completionVariables = ModifiedFunRun.test(filename, position);
				
	            
	            for (String variable: completionVariables) {
	            	CompletionItem completionItem = new CompletionItem();
	                completionItem.setInsertText(variable);
	                completionItem.setLabel(variable);
	                completionItem.setKind(CompletionItemKind.Variable);
	                completionItems.add(completionItem);
	            }
				
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
			}
            
            for (String item: new String[] {
            		"else", "false", "true", "func", "if", "proc", "return",
            		"not", "while", "bool", "int", "read()"
        		}) {
            	CompletionItem completionItem = new CompletionItem();
                completionItem.setInsertText(item);
                completionItem.setLabel(item);
                if (item.equals("bool") || item.equals("int"))
                	completionItem.setKind(CompletionItemKind.Class);
                else if (item.equals("write()") || item.equals("read()"))
                	completionItem.setKind(CompletionItemKind.Function);
                else
                	completionItem.setKind(CompletionItemKind.Keyword);
                completionItems.add(completionItem);
            }
            
            {
            	CompletionItem completionItem = new CompletionItem();
                completionItem.setInsertText("write($1)");
                completionItem.setLabel("write()");
                completionItem.setKind(CompletionItemKind.Function);
                completionItem.setInsertTextFormat(InsertTextFormat.Snippet);
                completionItems.add(completionItem);
            }
            
            {
                CompletionItem completionItem = new CompletionItem();
                completionItem.setInsertText("main():\n\t$1\n.");
                completionItem.setLabel("main()");
                completionItem.setKind(CompletionItemKind.Snippet);
//                completionItem.setInsertTextMode(InsertTextMode.AdjustIndentation);
                completionItem.setInsertTextFormat(InsertTextFormat.Snippet);
                completionItems.add(completionItem);
            }
            
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

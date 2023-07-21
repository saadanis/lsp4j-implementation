package com.saadanis.calc.ls.langserver;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.CompletionRegistrationOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.Registration;
import org.eclipse.lsp4j.RegistrationParams;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentClientCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

public class CalcLanguageServer implements LanguageServer, LanguageClientAware {

	private TextDocumentService textDocumentService;
	private WorkspaceService workspaceService;
	private ClientCapabilities clientCapabilities;
	LanguageClient languageClient;
	private int errorCode = 1;
	
	public CalcLanguageServer() {
		this.textDocumentService = new CalcTextDocumentService(this);
		this.workspaceService = new CalcWorkspaceService(this);
	}

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		// Initialize the InitializeResult for this LS.
		final InitializeResult response = new InitializeResult(new ServerCapabilities());
		
		// Set the capabilities of the LS to inform the client.
        response.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
        this.clientCapabilities = params.getCapabilities();
        
        if (!isDynamicCompletionRegistration()) {
        	response.getCapabilities().setCompletionProvider(new CompletionOptions());
        }
        
        return CompletableFuture.supplyAsync(() -> response);
        
//        CompletionOptions completionOptions = new CompletionOptions();
//        initializeResult.getCapabilities().setCompletionProvider(completionOptions);
//        return CompletableFuture.supplyAsync(()->initializeResult);
	}
	
//    @Override
//    public void initialized(InitializedParams params) {
//        //Check if dynamic completion support is allowed, if so register.
//        if (isDynamicCompletionRegistration()) {
//            CompletionRegistrationOptions completionRegistrationOptions = new CompletionRegistrationOptions();
//            Registration completionRegistration = new Registration(UUID.randomUUID().toString(),
//                    "textDocument/completion", completionRegistrationOptions);
//            languageClient.registerCapability(new RegistrationParams(List.of(completionRegistration)));
//        }
//    }
	
	@Override
	public CompletableFuture<Object> shutdown() {
		// If shutdown request comes from client, set the error code to 0.
		errorCode = 0;
		return CompletableFuture.supplyAsync(Object::new);
	}

	@Override
	public void exit() {
		// Kill the LS on exit request from client.
		System.exit(errorCode);
	}

	@Override
	public TextDocumentService getTextDocumentService() {
        return this.textDocumentService;
	}

	@Override
	public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
	}
	
	@Override
	public void connect(LanguageClient client) {
		// Get the client which started this LS.
		this.languageClient = client;
		LSClientLogger.getInstance().initialize(this.languageClient);
	}
	
    private boolean isDynamicCompletionRegistration() {
        TextDocumentClientCapabilities textDocumentCapabilities =
                clientCapabilities.getTextDocument();
        return textDocumentCapabilities != null && textDocumentCapabilities.getCompletion() != null
                && Boolean.FALSE.equals(textDocumentCapabilities.getCompletion().getDynamicRegistration());
    }

}

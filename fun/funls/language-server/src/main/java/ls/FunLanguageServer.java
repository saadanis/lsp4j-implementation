package ls;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

/**
 * The FunLanguageServer class implements the LanguageServer interface to provide the main functionality of the language server.
 */
public class FunLanguageServer implements LanguageServer, LanguageClientAware {

	private TextDocumentService textDocumentService;
	private WorkspaceService workspaceService;
	LanguageClient languageClient;
	private int errorCode = 1;

    /**
     * Constructor for FunLanguageServer.
     */
	public FunLanguageServer() {
		this.textDocumentService = new FunTextDocumentService(this);
		this.workspaceService = new FunWorkspaceService();
	}

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
	
		// Create a new result object with basic server capabilities.
		final InitializeResult response = new InitializeResult(new ServerCapabilities());

		// Set text document synchronization to Full and set up capabilities.
		response.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
		response.getCapabilities().setCompletionProvider(new CompletionOptions());

		return CompletableFuture.supplyAsync(() -> response);
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		
		// Set the error code to 0 and return.
		errorCode = 0;
		return CompletableFuture.supplyAsync(Object::new);
	}

	@Override
	public void exit() {
		
        // Exit the system with the given error code.
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
		
        // Connect the language client to the server.
		this.languageClient = client;
		
        // Initialize the FunClientLogger.
		FunClientLogger.getInstance().initialize(this.languageClient);
	}

}

package ls;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.RenameFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class FunWorkspaceService implements WorkspaceService {

	private FunLanguageServer languageServer;
	FunClientLogger clientLogger;
	
    public FunWorkspaceService(FunLanguageServer languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = FunClientLogger.getInstance();
    }
	
    @Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {
		this.clientLogger.logMessage("Operation 'workspace/didChangeConfiguration' Ack");

	}
	
	@Override
	public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
		this.clientLogger.logMessage("Operation 'workspace/didChangeWatchedFiles' Ack");
	}
	
	@Override
	public void didRenameFiles(RenameFilesParams params) {
		this.clientLogger.logMessage("Operation 'workspace/didRenameFiles' Ack");
	}

	@Override
	public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
		// TODO Auto-generated method stub
		this.clientLogger.logMessage("executedCommand.");
		return WorkspaceService.super.executeCommand(params);
	}
	

}

package ls;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.RenameFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

import mods.ModifiedFunRun;

/**
 * The FunWorkspaceService class implements the WorkspaceService interface to handle workspace-related actions.
 */
public class FunWorkspaceService implements WorkspaceService {

	FunClientLogger clientLogger;
	
    /**
     * Constructor for FunWorkspaceService.
     */
    public FunWorkspaceService() {
        this.clientLogger = FunClientLogger.getInstance();
    }
	
    @Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {
		this.clientLogger.logMessage("FunWorkspaceService:didChangeConfiguration");

	}
	
	@Override
	public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
		this.clientLogger.logMessage("FunWorkspaceService:didChangeWatchedFiles");
	}
	
	@Override
	public void didRenameFiles(RenameFilesParams params) {
		this.clientLogger.logMessage("FunWorkspaceService:didRenameFiles");
	}

	@Override
	public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
		
		// Handle 'run' command execution.
		if (params.getCommand().equals("run"))
		{
			try {
				String code = params.getArguments().get(0).toString();
				
				// Convert escaped characters back to their original form.
				code = code.substring(1, code.length() - 1);
				code = code.replace("\\n", "\n");
				code = code.replace("\\r", "\r");
				code = code.replace("\\t", "\t");
				
				// Call the ModifiedFunRun to execute the provided code.
				ModifiedFunRun.run(code);
				
			} catch (Exception e) {
				System.err.println("FunWorkspaceService:executeCommand: " + e.getLocalizedMessage());
				return CompletableFuture.completedFuture(false);
			}
			
			return CompletableFuture.completedFuture(true);
		}
		
		return CompletableFuture.completedFuture(new Object());
	}
}

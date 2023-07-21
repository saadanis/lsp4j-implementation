package com.saadanis.calc.ls.langserver;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.RenameFilesParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class CalcWorkspaceService implements WorkspaceService {

	private CalcLanguageServer languageServer;
	LSClientLogger clientLogger;
	
    public CalcWorkspaceService(CalcLanguageServer languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = LSClientLogger.getInstance();
    }
	
//	@Override
//	public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams workspaceSymbolParams) {
//        return null;
//    }
	
    @Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {
		// TODO Auto-generated method stub
		this.clientLogger.logMessage("Operation 'workspace/didChangeConfiguration' Ack");

	}
	
	@Override
	public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
		// TODO Auto-generated method stub
		this.clientLogger.logMessage("Operation 'workspace/didChangeWatchedFiles' Ack");
	}
	
	@Override
	public void didRenameFiles(RenameFilesParams params) {
		this.clientLogger.logMessage("Operation 'workspace/didRenameFiles' Ack");
	}

}

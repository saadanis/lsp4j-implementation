package com.saadanis.calc.ls;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.RenameFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class CalcWorkspaceService implements WorkspaceService {

	private CalcLanguageServer languageServer;
	CalcClientLogger clientLogger;
	
    public CalcWorkspaceService(CalcLanguageServer languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = CalcClientLogger.getInstance();
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

}

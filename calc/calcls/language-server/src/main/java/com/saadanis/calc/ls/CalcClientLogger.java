package com.saadanis.calc.ls;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;

public class CalcClientLogger {
	
	private static CalcClientLogger INSTANCE;
	private LanguageClient client;
	private boolean isInitialized;
	
	private CalcClientLogger() {
		
    }

    public void initialize(LanguageClient client) {
        if (!Boolean.TRUE.equals(isInitialized)) {
            this.client = client;
        }
        
        isInitialized = true;
    }

    public static CalcClientLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CalcClientLogger();
        }
        return INSTANCE;
    }

    public void logMessage(String message) {
        if (!isInitialized) {
            return;
        }
        client.logMessage(new MessageParams(MessageType.Info, message));
    }
}

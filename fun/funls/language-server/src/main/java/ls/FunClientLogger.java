package ls;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;

public class FunClientLogger {
	
	private static FunClientLogger INSTANCE;
	private LanguageClient client;
	private boolean isInitialized;
	
	private FunClientLogger() {
		
    }

    public void initialize(LanguageClient client) {
        if (!Boolean.TRUE.equals(isInitialized)) {
            this.client = client;
        }
        
        isInitialized = true;
    }

    public static FunClientLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FunClientLogger();
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

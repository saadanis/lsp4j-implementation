package ls;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * FunClientLogger is a singleton utility class that provides logging functionality to
 * communicate messages from the language server to the client.
 */
public class FunClientLogger {
	
	private static FunClientLogger INSTANCE;
	private LanguageClient client;
	private boolean isInitialized;
	
	/**
	 * Private constructor to prevent external instantiation.
	 */
	private FunClientLogger() {
		
    }

	/**
    * Initializes the FunClientLogger with the given LanguageClient instance.
    *
    * @param client The LanguageClient instance to send log messages to.
    */
    public void initialize(LanguageClient client) {
        if (!Boolean.TRUE.equals(isInitialized)) {
            this.client = client;
        }
        
        isInitialized = true;
    }

    /**
     * Returns the instance of FunClientLogger.
     *
     * @return The FunClientLogger instance.
     */
    public static FunClientLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FunClientLogger();
        }
        return INSTANCE;
    }

    /**
     * Logs a message as an information-level message and sends to the language client.
     *
     * @param message The message to be logged.
     */
    public void logMessage(String message) {
        if (!isInitialized) {
            return;
        }
        client.logMessage(new MessageParams(MessageType.Info, message));
    }
}

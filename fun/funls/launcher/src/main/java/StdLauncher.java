import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

import ls.FunLanguageServer;

/**
 * Entry point for launching the FunLanguageServer using the standard input and output streams.
 */
public class StdLauncher {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		startServer(System.in, System.out);
	}

    /**
     * Starts the FunLanguageServer using the provided input and output streams.
     *
     * @param in  The input stream to receive messages from the language client.
     * @param out The output stream to send messages to the language client.
     * @throws ExecutionException   If an execution error occurs while launching the server.
     * @throws InterruptedException If the current thread is interrupted while waiting for the server to finish.
     */
	private static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
        
		// Create the server and launcher.
		FunLanguageServer server = new FunLanguageServer();
		Launcher<LanguageClient> launcher = Launcher.createLauncher(server, LanguageClient.class, in, out);
		
		// Connect to the client.
		LanguageClient client = launcher.getRemoteProxy();
		server.connect(client);

		// Start listening.
		Future<?> startListening = launcher.startListening();
		startListening.get();
	}

}

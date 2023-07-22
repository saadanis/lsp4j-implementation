import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

import ls.FunLanguageServer;

public class StdLauncher {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		startServer(System.in, System.out);
	}

	private static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
		FunLanguageServer server = new FunLanguageServer();

		Launcher<LanguageClient> launcher = Launcher.createLauncher(server, LanguageClient.class, in, out);
		
		LanguageClient client = launcher.getRemoteProxy();
		server.connect(client);

		Future<?> startListening = launcher.startListening();
		startListening.get();
	}

}

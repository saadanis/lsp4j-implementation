import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

import com.saadanis.calc.ls.CalcLanguageServer;

public class StdLauncher {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		startServer(System.in, System.out);
	}

	private static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
		CalcLanguageServer server = new CalcLanguageServer();

		Launcher<LanguageClient> launcher = Launcher.createLauncher(server, LanguageClient.class, in, out);
		
		LanguageClient client = launcher.getRemoteProxy();
		server.connect(client);

		Future<?> startListening = launcher.startListening();
		startListening.get();
	}

}

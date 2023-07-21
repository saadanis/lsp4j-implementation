import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import com.saadanis.calc.ls.langserver.CalcLanguageServer;

public class StdLauncher {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
//		LogManager.getLogManager().reset();
//		Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
//		globalLogger.setLevel(Level.OFF);

		startServer(System.in, System.out);
	}

	private static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
		CalcLanguageServer server = new CalcLanguageServer();

//		Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);

		Launcher<LanguageClient> launcher = Launcher.createLauncher(server, LanguageClient.class, in, out);
		
		LanguageClient client = launcher.getRemoteProxy();
		server.connect(client);

		Future<?> startListening = launcher.startListening();
		startListening.get();
	}

}

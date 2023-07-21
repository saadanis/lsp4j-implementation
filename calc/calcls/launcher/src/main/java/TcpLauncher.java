import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import com.saadanis.calc.ls.langserver.CalcLanguageServer;

public class TcpLauncher {

	public static void main(String[] args) {
		
		try {
			Socket clientSocket = new Socket("127.0.0,1", 9925);
			startServer(clientSocket.getInputStream(), clientSocket.getOutputStream());
		} catch (IOException | InterruptedException | ExecutionException e) {
            // Failed to start the server
        }
	}
	
	private static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
		
		CalcLanguageServer calcLanguageServer = new CalcLanguageServer();
		Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(calcLanguageServer, in, out);
		calcLanguageServer.connect(launcher.getRemoteProxy());
		Future<?> startListening = launcher.startListening();
		startListening.get();
		
	}

}

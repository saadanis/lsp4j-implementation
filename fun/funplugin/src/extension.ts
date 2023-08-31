// Import the required modules and APIs from the 'vscode' and 'vscode-languageclient' packages.
import * as path from 'path';
import * as vscode from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, RevealOutputChannelOn } from 'vscode-languageclient';

// Define the main entry point for the Fun language.
const main: string = 'StdLauncher';

// Create output channels for displaying information and execution output.
const outputChannel = vscode.window.createOutputChannel("Fun");
const executionChannel = vscode.window.createOutputChannel("Fun Execution");

// This method is called when the extension is activated
export function activate(context: vscode.ExtensionContext) {

	// Log a message indicating the extension has been activated.
	console.log('Extension "funplugin" is now active!');

	// Retrieve the JAVA_HOME environment variable.
	const { JAVA_HOME } = process.env;
	console.log(`Using java from JAVA_HOME: ${JAVA_HOME}`);

	if (JAVA_HOME) {
		// Construct the path to the Java executable.
		let executable: string = path.join(JAVA_HOME, 'bin', 'java');

		// Construct the classpath to the launcher JAR.
		let classPath = path.join(__dirname, '..', 'launcher', 'launcher.jar');
		const args: string[] = ['-cp', classPath];

		// Configure the server options.
		let serverOptions: ServerOptions = {
			command: executable,
			args: [...args, main],
			options: {}
		};

		// Configure the client options.
		let clientOptions: LanguageClientOptions = {
			documentSelector: [{ scheme: 'file', language: 'fun' }],
			outputChannel: outputChannel,
			revealOutputChannelOn: RevealOutputChannelOn.Never,
		};

		// Create the language client and start the client.
		let client = new LanguageClient('funls', 'Fun Language Server', serverOptions, clientOptions);
		context.subscriptions.push(client.start());

		// Register the 'fun.run' command.
		let disposable2 = vscode.commands.registerCommand('fun.run', () => {

			// Get the text from the current editor.
			let text = vscode.window.activeTextEditor?.document.getText();

			// Send a request to execute the 'run' command with the selected text.
			client.sendRequest('workspace/executeCommand', { command: 'run', arguments: text}).then((result) => {

				// Display the result in the execution channel.
				executionChannel.append('result: ' + result);
			});

        });

		// Register the 'fun.run' command.
        context.subscriptions.push(disposable2);
	}
}

// This method is called when the extension is deactivated.
export function deactivate() {
	console.log('Extension "funplugin" is now deactivated!')
}

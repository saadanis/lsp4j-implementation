// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below

import * as path from 'path';
import * as vscode from 'vscode';

import { LanguageClient, LanguageClientOptions, ServerOptions, RevealOutputChannelOn } from 'vscode-languageclient';

const main: string = 'StdLauncher';
const outputChannel = vscode.window.createOutputChannel("Fun");

// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed
export function activate(context: vscode.ExtensionContext) {

	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('Congratulations, your extension "funplugin" is now active!');

	const { JAVA_HOME } = process.env;

	console.log(`Using java from JAVA_HOME: ${JAVA_HOME}`);

	if (JAVA_HOME) {
		let executable: string = path.join(JAVA_HOME, 'bin', 'java');

		let classPath = path.join(__dirname, '..', 'launcher', 'launcher.jar');
		const args: string[] = ['-cp', classPath];

		let serverOptions: ServerOptions = {
			command: executable,
			args: [...args, main],
			options: {}
		};

		let clientOptions: LanguageClientOptions = {
			documentSelector: [{ scheme: 'file', language: 'fun' }],
			outputChannel: outputChannel,
			revealOutputChannelOn: RevealOutputChannelOn.Never,
		};

		let disposable = new LanguageClient('funls', 'Fun Language Server', serverOptions, clientOptions).start();
	
		context.subscriptions.push(disposable);
	}
}

// This method is called when your extension is deactivated
export function deactivate() {
	console.log('Your extension "funplugin" is now deactivated!')
}

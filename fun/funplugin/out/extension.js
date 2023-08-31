"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.deactivate = exports.activate = void 0;
// Import the required modules and APIs from the 'vscode' and 'vscode-languageclient' packages.
const path = require("path");
const vscode = require("vscode");
const vscode_languageclient_1 = require("vscode-languageclient");
// Define the main entry point for the Fun language.
const main = 'StdLauncher';
// Create output channels for displaying information and execution output.
const outputChannel = vscode.window.createOutputChannel("Fun");
const executionChannel = vscode.window.createOutputChannel("Fun Execution");
// This method is called when the extension is activated
function activate(context) {
    // Log a message indicating the extension has been activated.
    console.log('Extension "funplugin" is now active!');
    // Retrieve the JAVA_HOME environment variable.
    const { JAVA_HOME } = process.env;
    console.log(`Using java from JAVA_HOME: ${JAVA_HOME}`);
    if (JAVA_HOME) {
        // Construct the path to the Java executable.
        let executable = path.join(JAVA_HOME, 'bin', 'java');
        // Construct the classpath to the launcher JAR.
        let classPath = path.join(__dirname, '..', 'launcher', 'launcher.jar');
        const args = ['-cp', classPath];
        // Configure the server options.
        let serverOptions = {
            command: executable,
            args: [...args, main],
            options: {}
        };
        // Configure the client options.
        let clientOptions = {
            documentSelector: [{ scheme: 'file', language: 'fun' }],
            outputChannel: outputChannel,
            revealOutputChannelOn: vscode_languageclient_1.RevealOutputChannelOn.Never,
        };
        // Create the language client and start the client.
        let client = new vscode_languageclient_1.LanguageClient('funls', 'Fun Language Server', serverOptions, clientOptions);
        context.subscriptions.push(client.start());
        // Register the 'fun.run' command.
        let disposable2 = vscode.commands.registerCommand('fun.run', () => {
            // Get the text from the current editor.
            let text = vscode.window.activeTextEditor?.document.getText();
            // Send a request to execute the 'run' command with the selected text.
            client.sendRequest('workspace/executeCommand', { command: 'run', arguments: text }).then((result) => {
                // Display the result in the execution channel.
                executionChannel.append('result: ' + result);
            });
        });
        // Register the 'fun.run' command.
        context.subscriptions.push(disposable2);
    }
}
exports.activate = activate;
// This method is called when the extension is deactivated.
function deactivate() {
    console.log('Extension "funplugin" is now deactivated!');
}
exports.deactivate = deactivate;
//# sourceMappingURL=extension.js.map
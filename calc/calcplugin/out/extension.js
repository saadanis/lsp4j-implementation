"use strict";
// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
Object.defineProperty(exports, "__esModule", { value: true });
exports.deactivate = exports.activate = void 0;
const path = require("path");
const vscode = require("vscode");
const vscode_languageclient_1 = require("vscode-languageclient");
const main = 'StdLauncher';
const outputChannel = vscode.window.createOutputChannel("Calc");
// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed
function activate(context) {
    // Use the console to output diagnostic information (console.log) and errors (console.error)
    // This line of code will only be executed once when your extension is activated
    console.log('Congratulations, your extension "calcplugin" is now active!');
    const { JAVA_HOME } = process.env;
    console.log(`Using java from JAVA_HOME: ${JAVA_HOME}`);
    if (JAVA_HOME) {
        let executable = path.join(JAVA_HOME, 'bin', 'java');
        let classPath = path.join(__dirname, '..', 'launcher', 'launcher.jar');
        const args = ['-cp', classPath];
        let serverOptions = {
            command: executable,
            args: [...args, main],
            options: {}
        };
        let clientOptions = {
            documentSelector: [{ scheme: 'file', language: 'calc' }],
            outputChannel: outputChannel,
            revealOutputChannelOn: vscode_languageclient_1.RevealOutputChannelOn.Never,
        };
        let disposable = new vscode_languageclient_1.LanguageClient('calcls', 'Calc Language Server', serverOptions, clientOptions).start();
        context.subscriptions.push(disposable);
    }
}
exports.activate = activate;
// This method is called when your extension is deactivated
function deactivate() {
    console.log('Your extension "calcplugin" is now deactivated!');
}
exports.deactivate = deactivate;
//# sourceMappingURL=extension.js.map
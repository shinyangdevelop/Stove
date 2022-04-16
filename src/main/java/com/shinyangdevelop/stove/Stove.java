package com.shinyangdevelop.stove;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Stove {
    public static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
//        If no args input, run prompt
        if (args.length > 2) {
            System.err.println("Usage: \nstove : run prompt\nstove [file] [charset(default=UTF-8)] : run file");
            System.exit(64);
        } else if (args.length == 0) {
            runPrompt();
        } else if (args.length == 1) {
            runFile(args[0], "UTF-8");
        } else {
            runFile(args[0], args[1]);
        }
    }

    private static void runFile(String path, String charset) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String temp = new String(bytes, Charset.forName(charset));
        run(temp);
        if (hadError) {
            System.exit(65);
        }
        if(hadRuntimeError) {
            System.exit(70);
        }
    }

    private static void runPrompt() throws IOException {
        try (InputStreamReader input = new InputStreamReader(System.in);
             BufferedReader reader = new BufferedReader(input)) {
            for(;;) {
                System.out.print("\n> ");
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                run(line);
                hadError = false;
            }
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if (hadError) {
            return;
        }
        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        }
        else {
            report(token.line, " at '"+token.lexeme+"'", message);
        }
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error " + where + " : " + message);
        System.err.flush();
        hadError = true;
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line]" + error.token.line + "]");
        hadRuntimeError = true;
    }
}

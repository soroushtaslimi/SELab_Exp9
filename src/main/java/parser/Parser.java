package parser;

import codegenerator.CodeGenerator;
import errorhandler.ErrorHandler;
import scanner.LexicalAnalyzer;
import scanner.token.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private List<Rule> rules;
    private Stack<Integer> parsStack;
    private ParseTable parseTable;
    private CodeGenerator cg;

    public Parser() {
        parsStack = new Stack<Integer>();
        parsStack.push(0);
        try {
            parseTable = new ParseTable(Files.readAllLines(Paths.get("src/main/resources/parseTable")).get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        rules = new ArrayList<Rule>();
        try {
            for (String stringRule : Files.readAllLines(Paths.get("src/main/resources/Rules"))) {
                rules.add(new Rule(stringRule));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cg = new CodeGenerator();
    }

    public void startParse(java.util.Scanner sc) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(sc);
        Token lookAhead = lexicalAnalyzer.getNextToken();
        boolean finish = false;
        Action currentAction;
        while (!finish) {
            try {
                currentAction = parseTable.getActionTable(parsStack.peek(), lookAhead);

                switch (currentAction.action) {
                    case SHIFT:
                        parsStack.push(currentAction.number);
                        lookAhead = lexicalAnalyzer.getNextToken();
                        break;
                    case REDUCE:
                        Rule rule = rules.get(currentAction.number);
                        for (int i = 0; i < rule.RHS.size(); i++) {
                            parsStack.pop();
                        }

                        parsStack.push(parseTable.getGotoTable(parsStack.peek(), rule.LHS));
                        try {
                            cg.semanticFunction(rule.semanticAction, lookAhead);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ACCEPT:
                        finish = true;
                        break;
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        if (!ErrorHandler.hasError)
            cg.printMemory();
    }
}

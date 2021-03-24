package com.greatgitsby.hlc;

import java.util.*;

/**
 * TODO Description
 */
public class Parser {
    private final LexicalAnalyzer _lexicalAnalyzer;
    private final Map<Symbol, Map<Symbol, List<Symbol>>> _parseTable;
    private final Stack<Symbol> _labelStack;
    private final Stack<Symbol> _operandStack;
    private final Stack<Symbol> _operatorStack;
    private final Stack<Symbol> _parseStack;

    /**
     * Construct a new Parser
     */
    public Parser(LexicalAnalyzer lexer) {
        _lexicalAnalyzer = lexer;

        _labelStack = new Stack<>();
        _operandStack = new Stack<>();
        _operatorStack = new Stack<>();
        _parseStack = new Stack<>();
        _parseTable = new HashMap<>();
    }

    /**
     * TODO Description
     */
    public boolean isValidSyntax() {
        ArrayDeque<Symbol> symbolStack;
        Symbol currentSymbol;
        boolean isValidSyntax = false;

        symbolStack = getLexicalAnalyzer().getSymbolStack();

        while (!symbolStack.isEmpty()) {
            currentSymbol = symbolStack.peek();

            System.out.println(currentSymbol);

            symbolStack.pop();
        }

        return isValidSyntax;
    }

    /**
     * Returns the lexical analyzer of this Parser
     *
     * @return the lexical analyzer of this Parser
     */
    public LexicalAnalyzer getLexicalAnalyzer() {
        return _lexicalAnalyzer;
    }

    /**
     * Returns the parse table of this Parser
     *
     * @return the parse table of this Parser
     */
    public Map<Symbol, Map<Symbol, List<Symbol>>> getParseTable() {
        return _parseTable;
    }

    /**
     * Returns the operand stack of this Parser
     *
     * @return the operand stack of this Parser
     */
    public Stack<Symbol> getOperandStack() {
        return _operandStack;
    }

    /**
     * Returns the operator stack of this Parser
     *
     * @return the operator stack of this Parser
     */
    public Stack<Symbol> getOperatorStack() {
        return _operatorStack;
    }
}

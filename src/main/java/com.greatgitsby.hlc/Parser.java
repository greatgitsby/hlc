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
    private Symbol _currentLexerSymbol;

    /**
     * Construct a new Parser
     */
    public Parser(LexicalAnalyzer lexer) {
        _lexicalAnalyzer = lexer;

        _currentLexerSymbol = null;
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
        Symbol topOfParseStack;
        boolean isValidSyntax;

        isValidSyntax = false;
        symbolStack = getLexicalAnalyzer().getSymbolStack();

        // Push the end symbol ($) onto the parse stack
        getParseStack().push(Token.END_OF_INPUT);



        while (!symbolStack.isEmpty() && !getParseStack().isEmpty()) {
            // Set the current lexer symbol to the top of the symbol stack
            setCurrentLexerSymbol(symbolStack.peek());

            // Get the top of parse stack
            topOfParseStack = getParseStack().peek();

            // Tell top of parse stack to do its thing
            topOfParseStack.doTheThing(this);

            // TESTING
            symbolStack.pop();
        }

        return isValidSyntax;
    }

    /**
     * Returns the current lexer symbol
     *
     * @return the current lexer symbol
     */
    public Symbol getCurrentLexerSymbol() {
        return _currentLexerSymbol;
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

    /**
     * Returns the parse stack of this Parser
     *
     * @return the parse stack of this Parser
     */
    public Stack<Symbol> getParseStack() {
        return _parseStack;
    }

    /**
     * Sets the current lexer symbol
     */
    private void setCurrentLexerSymbol(Symbol currentSymbol) {
        _currentLexerSymbol = currentSymbol;
    }
}

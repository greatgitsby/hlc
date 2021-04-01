package com.greatgitsby.hlc;

/**
 * TerminalToken
 *
 * Each Token is an element of the Hansen programming
 * language
 */
public enum TerminalToken implements Symbol {
    VARIABLE,
    IDENTIFIER,
    NUMBER,
    PRINT,
    IF,
    THEN,
    ELSE,
    WHILE,
    DO,
    BEGIN,
    END,
    STRING_CONST,
    MULTIPLICATIVE_OP,
    RELATIONAL_OP,
    ASSIGNMENT_OP,
    ADDITIVE_OP,
    STATEMENT_SEP,
    LEFT_PAREN,
    RIGHT_PAREN,
    WHITESPACE,
    COMMENT,
    END_OF_INPUT;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTheThing(Parser theParser) throws SyntaxErrorException {
        Symbol lexerSymbolToTest = theParser.getTopOfLexerStack();

        if (theParser.getTopOfLexerStack() instanceof Lexeme) {
            lexerSymbolToTest = ((Lexeme) theParser.getTopOfLexerStack()).getTokenType();
        }

        if (theParser.getTopOfParseStack().equals(lexerSymbolToTest)) {
            theParser.getLexicalAnalyzer().getSymbolStack().pop();
            theParser.getParseStack().pop();
        } else {
            throw new SyntaxErrorException("Unexpected Terminal");
        }
    }
}
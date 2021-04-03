package com.greatgitsby.hlc;

/**
 * TerminalToken
 *
 * Represents the set of terminal tokens in the HansenLite language
 */
public enum TerminalToken implements Symbol {
    ADDITIVE_OP,
    ASSIGNMENT_OP,
    BEGIN,
    COMMENT,
    DO,
    ELSE,
    END,
    END_OF_INPUT,
    IDENTIFIER,
    IF,
    LEFT_PAREN,
    MULTIPLICATIVE_OP,
    NUMBER,
    PRINT,
    RELATIONAL_OP,
    RIGHT_PAREN,
    STATEMENT_SEP,
    STRING_CONST,
    THEN,
    VARIABLE,
    WHILE,
    WHITESPACE;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTheThing(Parser theParser) throws SyntaxErrorException {
        // Local variables
        Symbol theCurrentLexerSymbol;

        // Get the current parser lexer symbol
        theCurrentLexerSymbol = theParser.getCurrentLexerSymbol();

        // If this lexer symbol is a Lexeme, we must acquire the token
        // type of the Lexeme
        //
        // TODO Achieve a better OOP approach when we start to utilize
        //  this output for code generation
        //
        if (theParser.getCurrentLexerSymbol() instanceof Lexeme) {
            theCurrentLexerSymbol =
                ((Lexeme) theParser.getCurrentLexerSymbol()).getTokenType();
        }

        // If the top of the parse stack equals the current lexer symbol,
        // we know it matched the grammar description and can get the
        // next symbol from the lexical analyzer
        if (theParser.getTopOfParseStack().equals(theCurrentLexerSymbol)) {
            // Pop the top of parse stack
            theParser.getParseStack().pop();

            // Set the current lexer symbol to the next one in the stream
            theParser.setCurrentLexerSymbol(
                theParser.getLexicalAnalyzer().nextSymbol()
            );
        }
        // Hit a case where we received an unexpected terminal
        // from the lexer stream
        else {
            throw new SyntaxErrorException(
                String.format(
                    "Line %d Char %d - Expected %s, Got %s",
                    theParser.getLexicalAnalyzer().getLineNumber(),
                    theParser.getLexicalAnalyzer().getCharacterNumber(),
                    theParser.getCurrentLexerSymbol(),
                    theParser.getTopOfParseStack()
                )
            );
        }
    }
}

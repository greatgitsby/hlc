package com.greatgitsby.hlc;

/**
 * TerminalToken
 *
 * Represents the set of terminal tokens in the HansenLite language
 */
public enum TerminalToken implements Token {
    ADDITIVE_OP() {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            theParser.getOperatorStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );
            super.doTheThing(theParser);
        }
    },
    ASSIGNMENT_OP() {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            theParser.getOperatorStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );
            super.doTheThing(theParser);
        }
    },
    BEGIN,
    COMMENT,
    DO,
    ELSE,
    END,
    END_OF_INPUT,
    IDENTIFIER() {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            if (theParser.getLexicalAnalyzer().getSymbolTable().containsKey(theParser.getCurrentLexeme().getValue())) {
               theParser.getOperandStack().push(theParser.getLexicalAnalyzer().getSymbolTable().get(theParser.getCurrentLexeme().getValue()));
            } else {
                // Throw error
            }

            super.doTheThing(theParser);
        }
    },
    IF,
    LEFT_PAREN,
    MULTIPLICATIVE_OP() {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            theParser.getOperatorStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );
            super.doTheThing(theParser);
        }
    },
    NUMBER() {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            theParser.getOperandStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );
            super.doTheThing(theParser);
        }
    },
    PRINT,
    RELATIONAL_OP() {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            theParser.getOperatorStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );
            super.doTheThing(theParser);
        }
    },
    RIGHT_PAREN,
    STATEMENT_SEP,
    STRING_CONST() {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            Symbol newStringSymbol = new Symbol(new Lexeme(
                    String.format("string%d", theParser.getStringConstants().size() + 1),
                    TerminalToken.STRING_CONST
            ));

            // Put the new lexeme's value as the key,
            // old lexeme's actual value as value
            theParser.getStringConstants().putIfAbsent(newStringSymbol.getLexeme().getValue(), theParser.getCurrentLexeme().getValue());
            theParser.getOperandStack().push(newStringSymbol);

            super.doTheThing(theParser);
        }
    },
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
        Token theCurrentLexerSymbol;

        // Get the current parser lexer symbol
        theCurrentLexerSymbol = theParser.getCurrentLexeme().getTokenType();

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
                    theParser.getCurrentLexeme(),
                    theParser.getTopOfParseStack()
                )
            );
        }
    }
}

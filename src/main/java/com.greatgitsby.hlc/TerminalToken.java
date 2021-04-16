package com.greatgitsby.hlc;

/**
 * TerminalToken
 *
 * Represents the set of terminal tokens in the HansenLite language
 */
public enum TerminalToken implements Token {
    ADDITIVE_OP() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Create a new symbol from the Lexeme and and push it
            // onto the operator stack
            theParser.getOperatorStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );

            // Perform the default behavior of the Terminal Token
            super.doTheThing(theParser);
        }
    },
    ASSIGNMENT_OP() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Create a new symbol from the Lexeme and and push it
            // onto the operator stack
            theParser.getOperatorStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );

            // Perform the default behavior of the Terminal Token
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Get the symbol from the symbol table and push it onto the
            // operand stack
            theParser
                .getOperandStack()
                .push(
                    theParser
                        .getLexicalAnalyzer()
                        .getSymbolTable()
                        .get(theParser.getCurrentLexeme().getValue())
                );

            // Perform the default behavior for Terminal Tokens
            super.doTheThing(theParser);
        }
    },
    IF,
    LEFT_PAREN,
    MULTIPLICATIVE_OP() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Create a new symbol from the Lexeme and and push it
            // onto the operator stack
            theParser.getOperatorStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );

            // Perform the default behavior of the Terminal Token
            super.doTheThing(theParser);
        }
    },
    NUMBER() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Create a new symbol from the Lexeme and and push it
            // onto the operand stack
            theParser.getOperandStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );

            // Perform the default behavior of the Terminal Token
            super.doTheThing(theParser);
        }
    },
    PRINT,
    RELATIONAL_OP() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Create a new symbol from the Lexeme and and push it
            // onto the operator stack
            theParser.getOperatorStack().push(
                new Symbol(theParser.getCurrentLexeme())
            );

            // Perform the default behavior of the Terminal Token
            super.doTheThing(theParser);
        }
    },
    RIGHT_PAREN,
    STATEMENT_SEP,
    STRING_CONST() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            Symbol newStringSymbol;

            // Add the string to the string constant pool
            if (
                !theParser
                    .getStringConstants()
                    .containsKey(theParser.getCurrentLexeme().getValue())
            ) {
                // Construct a new symbol and lexeme to take the place
                // of the string constant from the Lexical Analyzer
                newStringSymbol = new Symbol(new Lexeme(
                    String.format(
                        "%s%d",
                        Parser.STRING_CONST_PREFIX,
                        // some variable number
                        theParser.getStringConstants().size()
                    ),
                    TerminalToken.STRING_CONST
                ));

                // Put this new string constant symbol into the
                // constant pool
                theParser
                    .getStringConstants()
                    .put(
                        theParser.getCurrentLexeme().getValue(),
                        newStringSymbol
                    );
            }

            // Get the string constant from the pool based on the
            // value of the string from the Lexical Analyzer
            newStringSymbol =
                theParser
                    .getStringConstants()
                    .get(theParser.getCurrentLexeme().getValue());

            // Push this new string symbol onto the operand stack
            theParser.getOperandStack().push(newStringSymbol);

            // Perform the default Terminal Token behavior
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
    public void doTheThing(Parser theParser) throws CompilerException {

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
            throw new CompilerException(
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

package com.greatgitsby.hlc;

/**
 * Token
 *
 * Each Token is an element of the Hansen programming
 * language
 */
public enum Token implements Symbol {
    // Terminals
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
    END_OF_INPUT,

    // Non-terminals
    STATEMENT {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    ELSE_CLAUSE {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    STATEMENT_LIST {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    SEPARATED_LIST {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    PRINT_EXPRESSION {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    BOOLEAN_EXPRESSION {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    EXPRESSION {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    ADDITION {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    TERM {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    MULTIPLICATION {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    FACTOR {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    },
    SIGNED_TERM {
        @Override
        public void doTheThing(Parser theParser) {
            doTheNonTerminalThing(theParser);
        }
    };

    /**
     * General behavior of non-terminals
     */
    protected void doTheNonTerminalThing(Parser theParser) {
        if (
            theParser.getParseTable().containsKey(theParser.getTopOfParseStack()) &&
            theParser.getParseTable().get(theParser.getTopOfParseStack()).containsKey(
                theParser.getTopOfLexerStack()
            )
        ) {
            theParser.getParseStack().pop();

            for (
                Symbol s :
                theParser.getParseTable()
                    .get(theParser.getTopOfParseStack())
                    .get(theParser.getTopOfLexerStack())
            ) {
                theParser.getParseStack().push(s);
            }
        } else {
            //                throw new SyntaxErrorException("Invalid Syntax");
            System.err.println("ERROR!!!");
        }
    }

    /**
     * General behavior for terminals
     */
    @Override
    public void doTheThing(Parser theParser) {
        // TODO Implement general behavior

        theParser.getLexicalAnalyzer().getSymbolStack().pop();
        theParser.getParseStack().pop();
    }
}
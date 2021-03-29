package com.greatgitsby.hlc;

import java.util.List;
import java.util.ListIterator;

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
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    ELSE_CLAUSE {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    STATEMENT_LIST {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    SEPARATED_LIST {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    PRINT_EXPRESSION {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    BOOLEAN_EXPRESSION {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    EXPRESSION {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    ADDITION {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    TERM {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    MULTIPLICATION {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    FACTOR {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    },
    SIGNED_TERM {
        @Override
        public void doTheThing(Parser theParser) throws SyntaxErrorException {
            doTheNonTerminalThing(theParser);
        }
    };

    /**
     * General behavior of non-terminals
     */
    protected void doTheNonTerminalThing(Parser theParser) throws SyntaxErrorException {
        ListIterator<Symbol> listIterator;
        List<Symbol> theListOfSymbols;

        theParser.getParseStack().pop();

        if (
            theParser.getParseTable().containsKey(theParser.getTopOfParseStack()) &&
            theParser.getParseTable().get(theParser.getTopOfParseStack()).containsKey(
                theParser.getTopOfLexerStack()
            )
        ) {
            theListOfSymbols = theParser.getParseTable()
                .get(theParser.getTopOfParseStack())
                .get(theParser.getTopOfLexerStack());

            listIterator = theListOfSymbols.listIterator(theListOfSymbols.size());

            while (listIterator.hasPrevious()) {
                theParser.getParseStack().push(listIterator.previous());
            }
        } else {
            throw new SyntaxErrorException("Unexpected Non-Terminal");
        }
    }

    /**
     * General behavior for terminals
     */
    @Override
    public void doTheThing(Parser theParser) throws SyntaxErrorException {
        // TODO Implement general behavior

        if (theParser.getTopOfParseStack().equals(theParser.getTopOfLexerStack())) {
            theParser.getLexicalAnalyzer().getSymbolStack().pop();
            theParser.getParseStack().pop();
        } else {
            throw new SyntaxErrorException("Unexpected Terminal");
        }
    }
}
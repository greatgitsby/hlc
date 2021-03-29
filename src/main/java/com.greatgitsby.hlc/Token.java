package com.greatgitsby.hlc;

import java.util.*;

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

        Symbol lexerSymbolToTest = theParser.getTopOfLexerStack();

        if (theParser.getTopOfLexerStack() instanceof Lexeme) {
            lexerSymbolToTest = ((Lexeme) theParser.getTopOfLexerStack()).getTokenType();
        }

        if (
            theParser.getParseTable().containsKey(theParser.getTopOfParseStack()) &&
            theParser.getParseTable().get(theParser.getTopOfParseStack()).containsKey(
                lexerSymbolToTest
            )
        ) {
            theListOfSymbols = theParser.getParseTable()
                .get(theParser.getTopOfParseStack())
                .get(lexerSymbolToTest);

            listIterator = theListOfSymbols.listIterator(theListOfSymbols.size());

            while (listIterator.hasPrevious()) {
                theParser.getParseStack().push(listIterator.previous());
            }
        } else {
            StringBuilder expected = new StringBuilder();
            Set<Symbol> possibleSymbols =
                theParser
                    .getParseTable()
                    .get(theParser.getTopOfParseStack())
                    .keySet();

            Iterator<Symbol> theIterator = possibleSymbols.iterator();

            expected.append(theIterator.next());

            if (possibleSymbols.size() == 2) {
                expected.append(" or ").append(theIterator.next());
            } else {
                expected.append(", ");
            }

            while (theIterator.hasNext()) {
                Symbol nextSymbol = theIterator.next();

                if (!theIterator.hasNext()) {
                    expected.append("or ").append(nextSymbol);
                } else {
                    expected.append(nextSymbol).append(", ");
                }
            }

            throw new SyntaxErrorException(String.format("Expected %s", expected));
        }
    }

    /**
     * General behavior for terminals
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
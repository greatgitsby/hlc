package com.greatgitsby.hlc;

import java.util.*;

/**
 * NonTerminalToken
 *
 * TODO Description
 */
public enum NonTerminalToken implements Symbol {
    STATEMENT,
    ELSE_CLAUSE,
    STATEMENT_LIST,
    SEPARATED_LIST,
    PRINT_EXPRESSION,
    BOOLEAN_EXPRESSION,
    EXPRESSION,
    ADDITION,
    TERM,
    MULTIPLICATION,
    FACTOR,
    SIGNED_TERM;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTheThing(Parser theParser) throws SyntaxErrorException {
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
}

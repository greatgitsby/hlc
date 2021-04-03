package com.greatgitsby.hlc;

import java.util.*;

/**
 * NonTerminalToken
 *
 * Represents the set of non-terminal tokens in the HansenLite language.
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
        // Local variables
        Symbol currentLexerSymbol;
        Symbol nextPossibleSymbol;
        StringBuilder expected;
        List<Symbol> theListOfSymbols;
        ListIterator<Symbol> listIterator;
        Set<Symbol> possibleSymbols;
        Iterator<Symbol> possibleSymbolsIterator;

        // Pop the TOS off the parse stack
        theParser.getParseStack().pop();

        // Get the current parser lexer symbol
        currentLexerSymbol = theParser.getCurrentLexerSymbol();

        // If this lexer symbol is a Lexeme, we must acquire the token
        // type of the Lexeme
        //
        // TODO Achieve a better OOP approach when we start to utilize
        //  this output for code generation
        //
        if (theParser.getCurrentLexerSymbol() instanceof Lexeme) {
            currentLexerSymbol =
                ((Lexeme) theParser.getCurrentLexerSymbol()).getTokenType();
        }

        // If the parse table has an entry for the top of stack of the
        // parse stack and in that entry, there is an entry for the
        // next lexeme symbol, then it should push the list of symbols
        // onto the parse stack
        if (
            theParser.getParseTable().containsKey(
                theParser.getTopOfParseStack()
            ) &&
            theParser.getParseTable().get(theParser.getTopOfParseStack())
                .containsKey(currentLexerSymbol)
        ) {
            // Get the symbols to push onto the stack
            theListOfSymbols = theParser.getParseTable()
                .get(theParser.getTopOfParseStack())
                .get(currentLexerSymbol);

            // Get the iterator for the list
            listIterator = theListOfSymbols.listIterator(
                theListOfSymbols.size()
            );

            // Push the set of terminals and non-terminals onto the stack
            // in reverse order
            while (listIterator.hasPrevious()) {
                theParser.getParseStack().push(listIterator.previous());
            }
        }
        // If either entry is missing, a syntax error was encountered.
        // Formulate a helpful error message to help identify where the
        // error occurred
        else {
            expected = new StringBuilder();
            // Output current line and char number
            expected.append(
                String.format(
                    "Line %d Char %d - Expected ",
                        theParser.getLexicalAnalyzer().getLineNumber(),
                        theParser.getLexicalAnalyzer().getCharacterNumber()
                )
            );

            // Get the set of possible symbols (every key in the parse table
            // for this entry)
            possibleSymbols =
                theParser
                    .getParseTable()
                    .get(theParser.getTopOfParseStack())
                    .keySet();

            // Get the iterator for the set of possible symbols
            possibleSymbolsIterator = possibleSymbols.iterator();

            // Push the first possible symbol
            expected.append(possibleSymbolsIterator.next());

            // Two options should be "this OR that"
            if (possibleSymbols.size() == 2) {
                expected.append(" or ").append(possibleSymbolsIterator.next());
            }
            // Prepare to comma separate
            else {
                expected.append(", ");
            }

            // Emit the other possible symbols
            while (possibleSymbolsIterator.hasNext()) {
                nextPossibleSymbol = possibleSymbolsIterator.next();

                // End of statement
                if (!possibleSymbolsIterator.hasNext()) {
                    expected.append("or ").append(nextPossibleSymbol);
                }
                // Comma separate next value (Oxford comma included)
                else {
                    expected.append(nextPossibleSymbol).append(", ");
                }
            }

            // Throw the error with the expected string
            throw new SyntaxErrorException(expected.toString());
        }
    }
}

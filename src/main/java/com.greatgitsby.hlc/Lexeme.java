package com.greatgitsby.hlc;

/**
 * Lexeme
 *
 * Represents a lexeme in a language. It can store its
 * token type as well as the value associated with it
 * as interpreted file via the Lexical Analyzer
 */
public class Lexeme implements Symbol {
    private final String _value;
    private final Token _tokenType;

    /**
     * Constructs a new Lexeme
     *
     * @param value the value from the file
     * @param tokenType the type of Lexeme this is
     */
    public Lexeme(String value, Token tokenType) {
        _value = value;
        _tokenType = tokenType;
    }

    /**
     * Retrieves the token type of this Lexeme
     *
     * @return the token type of the lexeme
     */
    public Token getTokenType() {
        return _tokenType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTheThing(Parser theParser) {
        getTokenType().doTheThing(theParser);
    }

    /**
     * Creates a string representation of this lexeme.
     *
     * It includes the token type and the value of the
     * lexeme in the string
     *
     * @return a string representation of the lexeme
     */
    @Override
    public String toString() {
        return String.format(
            "%s (%s)",
            _tokenType,
            _value
        );
    }
}

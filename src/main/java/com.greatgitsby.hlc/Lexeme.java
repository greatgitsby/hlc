package com.greatgitsby.hlc;

/**
 * Lexeme
 *
 * Represents a lexeme in a language. It can store its
 * token type as well as the value associated with it
 * as interpreted file via the Lexical Analyzer
 */
public class Lexeme {

    // Private immutable instance variables
    private final String _value;
    private final Token  _tokenType;

    private int    _lineNumber;
    private int    _beginningCharNumber;

    /**
     * Constructs a new Lexeme
     *
     * @param value the value from the file
     * @param tokenType the type of Lexeme this is
     */
    public Lexeme(String value, Token tokenType) {
        _value = value;
        _tokenType = tokenType;
        _lineNumber = 0;
        _beginningCharNumber = 0;
    }

    /**
     * Constructs a new Lexeme with beginning char and line numbers
     *
     * @param value the value from the file
     * @param tokenType the type of Lexeme this is
     * @param lineNumber the line number this Lexeme resides on
     * @param beginningCharNumber the location of the first character
     */
    public Lexeme(
        String value,
        Token tokenType,
        int lineNumber,
        int beginningCharNumber
    ) {
        _value = value;
        _tokenType = tokenType;
        _lineNumber = lineNumber;
        _beginningCharNumber = beginningCharNumber;
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
     * Retrieves the value of the Lexeme, typically the representation
     * of the Lexeme as depicted in the source code file
     *
     * @return the value of the Lexeme
     */
    public String getValue() {
        return _value;
    }

    /**
     *
     * @return
     */
    public int getLineNumber() {
        return _lineNumber;
    }

    /**
     *
     * @return
     */
    public int getBeginningCharNumber() {
        return _beginningCharNumber;
    }

    public void setLineNumber(int lineNumber) {
        _lineNumber = lineNumber;
    }

    public void setBeginningCharNumber(int beginningCharNumber) {
        _beginningCharNumber = beginningCharNumber;
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

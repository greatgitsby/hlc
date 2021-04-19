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
    private final int    _lineNumber;
    private final int    _beginningCharNumber;

    /**
     * Constructs a new Lexeme with beginning char and line numbers
     *
     * @param value the value from the file
     * @param tokenType the type of Lexeme this is
     * @param lineNum the line number this Lexeme resides on
     * @param charNum the location of the first character
     */
    public Lexeme(String value, Token tokenType, int lineNum, int charNum) {
        _value               = value;
        _tokenType           = tokenType;
        _lineNumber          = lineNum;
        _beginningCharNumber = charNum;
    }

    /**
     * Constructs a new Lexeme. Initialize line and beginning character
     * numbers as 0
     *
     * @param value the value from the file
     * @param tokenType the type of Lexeme this is
     */
    public Lexeme(String value, Token tokenType) {
        this(value, tokenType, 0, 0);
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
     * Returns the line number that this Lexeme resides on
     *
     * @return the line number that this Lexeme resides on
     */
    public int getLineNumber() {
        return _lineNumber;
    }

    /**
     * Returns the character location of the beginning of this Lexeme
     *
     * @return the character location of the beginning of this Lexeme
     */
    public int getBeginningCharNumber() {
        return _beginningCharNumber;
    }

    /**
     * Creates a string representation of this Lexeme.
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

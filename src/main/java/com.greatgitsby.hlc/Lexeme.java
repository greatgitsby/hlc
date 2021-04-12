package com.greatgitsby.hlc;

/**
 * Lexeme
 *
 * Represents a lexeme in a language. It can store its
 * token type as well as the value associated with it
 * as interpreted file via the Lexical Analyzer
 */
public class Lexeme implements Symbol {

    // Private immutable instance variables
    private final String _value;
    private final TerminalToken _tokenType;

    private int _varNum;
    private int _register;

    /**
     * Constructs a new Lexeme
     *
     * @param value the value from the file
     * @param tokenType the type of Lexeme this is
     */
    public Lexeme(String value, TerminalToken tokenType) {
        _value = value;
        _tokenType = tokenType;
        _varNum = 0;
    }

    /**
     * Retrieves the token type of this Lexeme
     *
     * @return the token type of the lexeme
     */
    public TerminalToken getTokenType() {
        return _tokenType;
    }

    public void setVariableNumber(int varNum) {
        _varNum = varNum;
    }

    public void setRegister(int register) {
        _register = register;
    }

    public int getRegister() {
        return _register;
    }

    public int getVariableNumber() {
        return _varNum;
    }

    public String getValue() {
        return _value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTheThing(Parser theParser)  throws SyntaxErrorException {
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

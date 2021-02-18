package com.greatgitsby.hlc;

public class Lexeme implements Symbol {
    private String _value;
    private Token _tokenType;

    public Lexeme(String value, Token tokenType) {
        _value = value;
        _tokenType = tokenType;
    }

    public Token getTokenType() {
        return _tokenType;
    }

    @Override
    public void doTheThing() {

    }

    @Override
    public String toString() {
        return String.format("%s (%s)", _tokenType.toString(), _value.toString().trim());
    }
}

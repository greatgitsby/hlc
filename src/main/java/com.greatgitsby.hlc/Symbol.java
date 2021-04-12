package com.greatgitsby.hlc;

/**
 * Symbol
 *
 * Represents a generic element of the HansenLite language
 */
public class Symbol {

    private final Lexeme _lexeme;
    private int _variableNumber;
    private int _register;

    public Symbol() {
        _lexeme = null;
        _variableNumber = 0;
    }

    public Symbol(Lexeme lexeme) {
        _lexeme = lexeme;
        _variableNumber = 0;
    }

    public Symbol(Lexeme lexeme, int variableNumber) {
        _lexeme = lexeme;
        _variableNumber = variableNumber;
    }

    public int getVariableNumber() {
        return _variableNumber;
    }

    public void setVariableNumber(int variableNumber) {
        _variableNumber = variableNumber;
    }

    public int getRegister() {
        return _register;
    }

    public void setRegister(int register) {
        _register = register;
    }

    public Lexeme getLexeme() {
        return _lexeme;
    }
}

package com.greatgitsby.hlc;

/**
 * Symbol
 *
 * Represents a generic element of the HansenLite language. A Symbol
 * can be anonymous (a placeholder for a computed temporary in a register)
 * or associated with an IDENTIFIER, NUMBER, STRING_CONST, and more as
 * the language morphs
 */
public class Symbol {

    // Private mutable state
    private Lexeme _lexeme;
    private int     _variableNumber;
    private int     _register;

    /**
     * Construct a Symbol with a Lexeme
     *
     * @param lexeme the Lexeme associated with the Symbol
     */
    public Symbol(Lexeme lexeme) {
        _lexeme = lexeme;
        _variableNumber = Parser.NOT_ALLOCATED;
        _register = Parser.NOT_ALLOCATED;
    }

    /**
     * No-arg Symbol constructor (used for anonymous Symbols)
     */
    public Symbol() {
        this(null);
    }

    /**
     * Returns the Lexeme associated with the Symbol
     *
     * @return the Lexeme associated with the Symbol
     */
    public Lexeme getLexeme() {
        return _lexeme;
    }

    public void setLexeme(Lexeme theLexeme) {
        _lexeme = theLexeme;
    }

    /**
     * Return the variable number for this Symbol
     *
     * -1 if not initialized
     *
     * @return the variable number for this Symbol
     */
    public int getVariableNumber() {
        return _variableNumber;
    }

    /**
     * Sets the new variable number for this Symbol
     *
     * @param variableNumber the new variable number for this Symbol
     */
    public void setVariableNumber(int variableNumber) {
        _variableNumber = variableNumber;
    }

    /**
     * Gets the register that the Symbol currently occupies
     *
     * @return the register that the Symbol currently occupies
     */
    public int getRegister() {
        return _register;
    }

    /**
     * Sets the new register that the Symbol currently occupies
     *
     * @param register the new register that the Symbol currently occupies
     */
    public void setRegister(int register) {
        _register = register;
    }
}

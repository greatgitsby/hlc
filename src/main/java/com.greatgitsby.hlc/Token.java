package com.greatgitsby.hlc;

/**
 * Token
 *
 * TODO Add description
 */
public enum Token implements Symbol {
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
    ADDITIVE_OP() {
        public void doTheThing() {
            // Overrides - custom behavior
        }
    },
    STATEMENT_SEP,
    LEFT_PAREN,
    RIGHT_PAREN,
    WHITESPACE,
    COMMENT,
    END_OF_INPUT;

    public void doTheThing() {
        // General behavior
    }
}
package com.greatgitsby.hlc;

/**
 * State
 *
 * Defines each state the Lexical Analyzer can be in
 */
public enum State {
    START() {
        @Override
        public boolean isAccepting() { return false; }
    },
    SYMBOL,
    COLON() {
        @Override
        public boolean isAccepting() { return false; }
    },
    ASSIGNMENT_OP,
    NUMBER,
    ADDITIVE_OP,
    MULTIPLICATIVE_OP,
    LEFT_PAREN,
    RIGHT_PAREN,
    STATEMENT_SEP,
    WHITESPACE,
    EQUAL_TO,
    NOT_EQUAL_TO,
    GREATER_THAN,
    GREATER_THAN_EQUAL_TO,
    LESS_THAN,
    LESS_THAN_EQUAL_TO,
    IN_COMMENT() {
        @Override
        public boolean isAccepting() { return false; }
    },
    IN_STRING() {
        @Override
        public boolean isAccepting() { return false; }
    },
    COMMENT,
    STRING_CONST;

    /**
     * Determines if the State is accepting
     *
     * @return true if the State is accepting
     */
    public boolean isAccepting() { return true; }
}

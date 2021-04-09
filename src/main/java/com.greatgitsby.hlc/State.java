package com.greatgitsby.hlc;

/**
 * State
 *
 * Defines each state the Lexical Analyzer can be in
 */
public enum State {
    ADDITIVE_OP,
    ASSIGNMENT_OP,
    COLON() {
        @Override
        public boolean isAccepting() {
            return false;
        }
    },
    COMMENT,
    EQUAL_TO,
    GREATER_THAN,
    GREATER_THAN_EQUAL_TO,
    IN_COMMENT() {
        @Override
        public boolean isAccepting() {
            return false;
        }
    },
    IN_STRING() {
        @Override
        public boolean isAccepting() {
            return false;
        }
    },
    LEFT_PAREN,
    LESS_THAN,
    LESS_THAN_EQUAL_TO,
    MULTIPLICATIVE_OP,
    NOT_EQUAL_TO,
    NUMBER,
    RIGHT_PAREN,
    START() {
        @Override
        public boolean isAccepting() {
            return false;
        }
    },
    STATEMENT_SEP,
    STRING_CONST,
    SYMBOL,
    WHITESPACE;

    /**
     * Determines if the State is accepting
     *
     * @return true if the State is accepting
     */
    public boolean isAccepting() {
        return true;
    }
}

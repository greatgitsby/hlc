package com.greatgitsby.hlc;

/**
 * LexicalAnalyzerState
 *
 * TODO Add description
 */
public enum State {
    START() {
        @Override
        public boolean isAccepting() {
            return false;
        }
    },
    SYMBOL,
    NUMBER,
    ADDITIVE_OP,
    LEFT_PAREN,
    RIGHT_PAREN,
    STATEMENT_SEP,
    WHITESPACE,
    COMMENT;

    /**
     * TODO description
     * @return
     */
    public boolean isAccepting() {
        return true;
    }
}

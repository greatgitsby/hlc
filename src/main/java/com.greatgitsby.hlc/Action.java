package com.greatgitsby.hlc;

/**
 * Action
 *
 * Holds the behavior associated with code generation. These symbols
 * will be interleaved into the stream of lexemes to manage the code
 * generation phase of the compiler
 */
public enum Action implements Symbol {
    BEGIN_LABEL,
    CLEAR_REGS,
    COMPUTE,
    DECLARE,
    END_LABEL,
    GEN_LABELS,
    GOTO_BEGIN,
    GOTO_END,
    LOAD,
    POP_LABELS,
    PRINT_IFMT,
    PRINT_PRINTF,
    PRINT_SFMT,
    PUSH_OP,
    SIGN,
    STORE;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTheThing(Parser theParser) {
        // TODO Implement default Action behavior
    }
}

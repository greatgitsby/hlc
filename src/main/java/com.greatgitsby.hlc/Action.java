package com.greatgitsby.hlc;

import java.util.Date;

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
    PUSH_OP() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            System.out.printf("Pushed %s onto the operator stack", theParser.getCurrentLexerSymbol());

            theParser.getOperatorStack().push(theParser.getCurrentLexerSymbol());
        }
    },
    SIGN,
    STORE,

    PROLOGUE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) {
            // TODO Implement prologue

            super.doTheThing(theParser);

            emitTo(theParser, "/******************************************");
            emitTo(theParser, " *                                        *");
            emitTo(theParser, " *  AUTO-GENERATED, DO NOT MODIFY         *");
            emitTo(theParser, String.format(" *  DATE: %s    *", new Date()));
            emitTo(theParser, " *                                        *");
            emitTo(theParser, " ******************************************/");

            // External glibc calls
            emitTo(theParser, ".extern fgetc");
            emitTo(theParser, ".extern fopen");
            emitTo(theParser, ".extern fclose");
            emitTo(theParser, ".extern printf");
            emitTo(theParser, ".extern __aeabi_idiv");

            // Define global entry point
            emitTo(theParser, ".global main");
            emitTo(theParser, "");

            // Define static data section
            emitTo(theParser, ".balign 4");
            emitTo(theParser, "ifmt: .asciz \"%d\\n\"");

            emitTo(theParser, ".balign 4");
            emitTo(theParser, "sfmt: .asciz \"%s\\n\"");

            // Setup main
            emitTo(theParser, "");
            emitTo(theParser, ".balign 4");
            emitTo(theParser, ".text");
            emitTo(theParser, "main:");
            emitTo(theParser, "push { r0, lr }");
        }
    },
    EPILOGUE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) {
            // TODO Implement epilogue

            super.doTheThing(theParser);

            emitTo(theParser, "pop { r0, lr }");
            emitTo(theParser, "mov r0, #0");
            emitTo(theParser, "bx lr");
        }
    };

    /**
     * TODO Description
     * @param theParser
     * @param line
     */
    public void emitTo(Parser theParser, String line) {
        theParser.getOutput()
            .append(line)
            .append(System.lineSeparator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTheThing(Parser theParser) {
        // Remove action from parse stack
        theParser.getParseStack().pop();

        // TODO Implement default Action behavior
    }
}

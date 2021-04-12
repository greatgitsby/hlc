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
    BEGIN_LABEL() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            theParser.emitToOutput(
                String.format("begin%d:", theParser.getLabelStack().peek())
            );
        }
    },
    CLEAR_REGS() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            System.out.println("CLEAR REGISTERS!");
            theParser.clearRegisters();
        }
    },
    COMPUTE() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            System.out.println("COMPUTING!");
        }
    },
    DECLARE() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            Lexeme operand = theParser.getOperandStack().pop();

            if (!theParser.getLexicalAnalyzer().getSymbolTable().containsKey(operand.getValue())) {

                operand.setVariableNumber(theParser.incrementVariableNumber());

                theParser
                    .getLexicalAnalyzer()
                    .getSymbolTable()
                    .put(
                        operand.getValue(),
                        operand
                    );
            } else {
                // Throw error, multiple declaration
            }

            // TODO Add scope-aware semantics
            theParser.emitToOutput("\tadd sp, sp, #-4");
        }
    },
    END_LABEL() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            theParser.emitToOutput(
                    String.format("end%d:", theParser.getLabelStack().peek())
            );
        }
    },
    GEN_LABELS() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);
            theParser.getLabelStack().push(
                theParser.getLabelStack().size() + 1
            );
        }
    },
    GOTO_BEGIN() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            theParser.emitToOutput(
                String.format("\tb begin%d", theParser.getLabelStack().peek())
            );
        }
    },
    GOTO_END() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            theParser.emitToOutput(
                String.format("\tb end%d", theParser.getLabelStack().peek())
            );
        }
    },
    LOAD() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            Lexeme l = theParser.getOperandStack().pop();

            // It's a number
            if (l.getTokenType().equals(TerminalToken.NUMBER)) {
                theParser.getOperandStack().push(l);
            }
            // It's a string const
            else if (l.getTokenType().equals(TerminalToken.STRING_CONST)) {
                theParser.getStringConstants().putIfAbsent(l.getValue(), theParser.getStringConstants().size() + 1);

                theParser.getOperandStack().push(new Lexeme(
                    String.format("string%d", theParser.getStringConstants().get(l.getValue())),
                    TerminalToken.STRING_CONST
                ));
            }
            // It's an identifier (local variable), push the thingy in the symbol table thingy
            else {
                theParser.getOperandStack().push(theParser.getLexicalAnalyzer().getSymbolTable().get(l.getValue()));
            }

            // TODO Do!
        }
    },
    POP_LABELS() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);
            theParser.getLabelStack().pop();
        }
    },
    PRINT_IFMT() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);
            theParser.emitToOutput("\tldr r0, =ifmt");
        }
    },
    PRINT_PRINTF() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            theParser.emitToOutput("\tpush { r0-r3, lr }");

            // TODO Implement reg alloc and get the thing

            theParser.emitToOutput(String.format("\tmov r1, r%d", 0));
            theParser.emitToOutput("\tbl printf");
            theParser.emitToOutput("\tpop { r0-r3, lr }");
        }
    },
    PRINT_SFMT() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            theParser.emitToOutput("\tldr r0, =sfmt");
        }
    },
    PUSH_OP() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);

            // TODO Ask why we need this. Can't they push themselves?
        }
    },
    SIGN,
    STORE() {
        @Override
        public void doTheThing(Parser theParser) {
            super.doTheThing(theParser);
            System.out.println(theParser.getOperandStack());

            Lexeme rhs = theParser.getOperandStack().pop();
            Lexeme lhs = theParser.getOperandStack().pop();

            lhs = theParser.getLexicalAnalyzer().getSymbolTable().get(lhs.getValue());

            int registerForRhs = theParser.getRegister(rhs);
            int registerForLhs = theParser.getRegister(lhs);

            theParser.emitToOutput(
                String.format("\tstr r%d, [fp, #%d]", registerForRhs, -4 * lhs.getVariableNumber())
            );
        }
    },

    PROLOGUE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) {
            // TODO Implement prologue

            super.doTheThing(theParser);

            theParser.emitToOutput("/******************************************");
            theParser.emitToOutput(" *                                        *");
            theParser.emitToOutput(" *  AUTO-GENERATED, DO NOT MODIFY         *");
            theParser.emitToOutput(" *                                        *");
            theParser.emitToOutput(String.format(" *  DATE: %s    *", new Date()));
            theParser.emitToOutput(" *                                        *");
            theParser.emitToOutput(" ******************************************/");

            // External glibc calls
            theParser.emitToOutput(".extern printf");
            theParser.emitToOutput(".extern __aeabi_idiv");

            // Define global entry point
            theParser.emitToOutput(".global main");
            theParser.emitToOutput("");

            // Define static data section
            theParser.emitToOutput(".balign 4");
            theParser.emitToOutput("ifmt: .asciz \"%d\\n\"");

            theParser.emitToOutput(".balign 4");
            theParser.emitToOutput("sfmt: .asciz \"%s\\n\"");

            // Setup main
            theParser.emitToOutput("");
            theParser.emitToOutput(".balign 4");
            theParser.emitToOutput(".text");
            theParser.emitToOutput("main:");
            theParser.emitToOutput("\tpush { r0, lr }");
            theParser.emitToOutput("\tmov fp, sp");
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

            theParser.emitToOutput("");
            theParser.emitToOutput(".balign 4");
            theParser.emitToOutput(".text");
            theParser.emitToOutput("quit:");
            theParser.emitToOutput("\tmov sp, fp");
            theParser.emitToOutput("\tpop { r0, lr }");
            theParser.emitToOutput("\tmov r0, #0");
            theParser.emitToOutput("\tbx lr");
        }
    };

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

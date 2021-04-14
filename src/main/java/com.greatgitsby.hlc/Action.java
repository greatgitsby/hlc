package com.greatgitsby.hlc;

import java.util.Date;

/**
 * Action
 *
 * Holds the behavior associated with code generation. These symbols
 * will be interleaved into the stream of lexemes to manage the code
 * generation phase of the compiler
 */
public enum Action implements Token {

    BEGIN_LABEL() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.emitToOutput();
            theParser.emitToOutput(".balign 4");
            theParser.emitToOutput(
                String.format("begin%d:", theParser.getLabelStack().peek())
            );
        }
    },

    CLEAR_REGS() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);
            theParser.clearRegisters();
        }
    },

    COMPUTE() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            Symbol rhs = theParser.getOperandStack().pop();
            Symbol lhs = theParser.getOperandStack().pop();
            Symbol operator = theParser.getOperatorStack().pop();
            Symbol result = new Symbol();

            int rhsRegister = theParser.getRegister(rhs);
            int lhsRegister = theParser.getRegister(lhs);
            int resultRegister = theParser.getRegister(result);

            switch ((TerminalToken) operator.getLexeme().getTokenType()) {
            case ADDITIVE_OP:
                if (operator.getLexeme().getValue().equals("-")) {
                    theParser.emitToOutput(
                        String.format("\tsub r%d, r%d, r%d", resultRegister, lhsRegister, rhsRegister)
                    );
                } else {
                    theParser.emitToOutput(
                        String.format("\tadd r%d, r%d, r%d", resultRegister, lhsRegister, rhsRegister)
                    );
                }
                break;
            case MULTIPLICATIVE_OP:
                if (operator.getLexeme().getValue().equals("/")) {
                    theParser.emitToOutput("\tpush { r0-r3, lr }");
                    theParser.emitToOutput(String.format("\tmov r0, r%d", lhsRegister));
                    theParser.emitToOutput(String.format("\tmov r1, r%d", rhsRegister));
                    theParser.emitToOutput("\tbl __aeabi_idiv");
                    theParser.emitToOutput(String.format("\tmov r%d, r0", resultRegister));
                    theParser.emitToOutput("\tpop { r0-r3, lr }");
                } else {
                    theParser.emitToOutput(
                        String.format("\tmul r%d, r%d, r%d", resultRegister, lhsRegister, rhsRegister)
                    );
                }
                break;
            case RELATIONAL_OP:
                theParser.emitToOutput(String.format("\tcmp r%d, r%d", lhsRegister, rhsRegister));

                switch (operator.getLexeme().getValue()) {
                case "<":
                    theParser.emitToOutput(String.format("\tbge end%s", theParser.getLabelStack().peek()));
                    break;
                case "<=":
                    theParser.emitToOutput(String.format("\tbgt end%s", theParser.getLabelStack().peek()));
                    break;
                case "<>":
                    theParser.emitToOutput(String.format("\tbeq end%s", theParser.getLabelStack().peek()));
                    break;
                case "=":
                    theParser.emitToOutput(String.format("\tbne end%s", theParser.getLabelStack().peek()));
                    break;
                case ">":
                    theParser.emitToOutput(String.format("\tble end%s", theParser.getLabelStack().peek()));
                    break;
                case ">=":
                    theParser.emitToOutput(String.format("\tblt end%s", theParser.getLabelStack().peek()));
                    break;
                }
                break;
            default:
                // Error
            }

            theParser.freeRegister(lhs);
            theParser.freeRegister(rhs);
            theParser.getOperandStack().push(result);
        }
    },

    DECLARE() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            Symbol theOperand = theParser.getOperandStack().pop();
            Symbol theSymbolInSymbolTable =
                theParser.getLexicalAnalyzer().getSymbolTable().get(theOperand.getLexeme().getValue());

            if (theSymbolInSymbolTable != null) {
                if (theSymbolInSymbolTable.getVariableNumber() == -1) {
                    theSymbolInSymbolTable.setVariableNumber(theParser.incrementVariableNumber());
                } else {
                    // Throw error, already declared
                }

            } else {
                // Throw error, not in symbol table. Shouldn't be issue normally
            }

            // TODO Add scope-aware semantics
            theParser.emitToOutput("\tadd sp, sp, #-4");
        }
    },

    END_LABEL() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.emitToOutput();
            theParser.emitToOutput(".balign 4");
            theParser.emitToOutput(
                String.format("end%d:", theParser.getLabelStack().peek())
            );
        }
    },

    GEN_LABELS() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.getLabelStack().push(
                theParser.incrementNumLabels()
            );
        }
    },

    GOTO_BEGIN() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.emitToOutput(
                String.format("\tb begin%d", theParser.getLabelStack().peek())
            );
        }
    },

    POP_LABELS() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);
            theParser.getLabelStack().pop();
        }
    },

    PRINT_IFMT() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);
            theParser.emitToOutput("\tldr r0, =ifmt");
        }
    },

    PRINT_PRINTF() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.emitToOutput("\tpush { r0-r3, lr }");

            Symbol theSymbolToPrint = theParser.getOperandStack().pop();
            int register = theParser.getRegister(theSymbolToPrint);

            theParser.emitToOutput(String.format("\tmov r1, r%d", register));
            theParser.emitToOutput("\tbl printf");
            theParser.emitToOutput("\tpop { r0-r3, lr }");

            theParser.freeRegister(theSymbolToPrint);
        }
    },

    PRINT_SFMT() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.emitToOutput("\tldr r0, =sfmt");
        }
    },

    SIGN() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            Symbol theSign = theParser.getOperatorStack().pop();
            Symbol theOperand = theParser.getOperandStack().pop();
            Symbol newAnonymousSymbol = new Symbol();

            int operandRegister = theParser.getRegister(theOperand);
            int newAnonymousRegister = theParser.getRegister(newAnonymousSymbol);

            // Negate if the operator is a negative sign
            if (theSign.getLexeme().getValue().equals("-")) {
                theParser.emitToOutput(
                    String.format("\tneg r%d, r%d", newAnonymousRegister, operandRegister)
                );
            }

            theParser.getOperandStack().push(newAnonymousSymbol);

            System.out.println("SIGNED THING!");
        }
    },

    STORE() {
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);
            Symbol rhs = theParser.getOperandStack().pop();
            Symbol lhs = theParser.getOperandStack().pop();

            lhs = theParser.getLexicalAnalyzer().getSymbolTable().get(lhs.getLexeme().getValue());

            int registerForRhs = theParser.getRegister(rhs);

            theParser.emitToOutput(
                String.format("\tstr r%d, [fp, #%d]", registerForRhs, -4 * lhs.getVariableNumber())
            );

            theParser.freeRegister(lhs);
            theParser.freeRegister(rhs);
        }
    },

    PROLOGUE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Handle default behavior
            super.doTheThing(theParser);

            // Output beginning of header
            theParser.emitToOutput(new String[]{
                "/******************************************",
                " *                                        *",
                " *  AUTO-GENERATED, DO NOT MODIFY         *",
                " *                                        *"
            });

            // Output header file name
            theParser.emitToOutput(
                String.format(
                    " *  FILE: %-32s*",
                    theParser.getLexicalAnalyzer().getFileName()
                )
            );

            // Output date compiled
            theParser.emitToOutput(
                String.format(" *  DATE: %s    *", new Date())
            );

            // Output ending of header
            theParser.emitToOutput(new String[]{
                " *                                        *",
                " ******************************************/"
            });

            // External glibc calls
            theParser.emitToOutput(".extern printf");
            theParser.emitToOutput(".extern __aeabi_idiv");

            // Define global entry point
            theParser.emitToOutput(".global main");
            theParser.emitToOutput();

            // Setup main
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
        public void doTheThing(Parser theParser)
            throws CompilerException
        {
            // TODO Implement epilogue

            super.doTheThing(theParser);

            theParser.emitToOutput();
            theParser.emitToOutput(".balign 4");
            theParser.emitToOutput(".text");
            theParser.emitToOutput("quit:");
            theParser.emitToOutput("\tmov sp, fp");
            theParser.emitToOutput("\tpop { r0, lr }");
            theParser.emitToOutput("\tmov r0, #0");
            theParser.emitToOutput("\tbx lr");
            theParser.emitToOutput();
            theParser.emitToOutput(".balign 4");
            theParser.emitToOutput(".data");

            for (String key : theParser.getStringConstants().keySet()) {
                theParser.emitToOutput("");
                theParser.emitToOutput(".balign 4");
                theParser.emitToOutput(
                    String.format(
                        "%s: .asciz %s",
                        theParser.getStringConstants().get(key)
                            .getLexeme().getValue(),
                        key
                    )
                );
            }
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTheThing(Parser theParser)
        throws CompilerException
    {
        // Remove action from parse stack
        theParser.getParseStack().pop();
    }
}

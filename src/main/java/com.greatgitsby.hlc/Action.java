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

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Perform the default behavior for the Action symbol
            super.doTheThing(theParser);

            // Output the begin label. Use the top of the stack
            // to determine which label to output
            theParser.emitToOutput(
                String.format(
                    """
                    
                    .balign 4
                    %s%d:
                    """,
                    Parser.BEGIN_LABEL_PREFIX,
                    theParser.getLabelStack().peek()
                )
            );
        }
    },

    CLEAR_REGS() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);
            theParser.clearRegisters();
        }
    },

    COMPUTE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Perform the default behavior for Actions
            super.doTheThing(theParser);

            Symbol rhs;
            Symbol lhs;
            Symbol operator;
            Symbol result;
            String operation;

            result = new Symbol();
            operation = "";

            // Get the right hand side, left hand side, and operator
            // from their respective stacks
            rhs = theParser.getOperandStack().pop();
            lhs = theParser.getOperandStack().pop();
            operator = theParser.getOperatorStack().pop();

            // Additive op [ +, - ]
            if (
                TerminalToken.ADDITIVE_OP
                    .equals(operator.getLexeme().getTokenType())
            ) {

                // Subtraction
                if (operator.getLexeme().getValue().equals("-")) {

                    // Emit the subtract operation
                    // sub Rd, Rs, Rs
                    theParser.emitToOutput(
                        String.format(
                            "\tsub r%d, r%d, r%d\n",
                            theParser.getRegister(result),
                            theParser.getRegister(lhs),
                            theParser.getRegister(rhs)
                        )
                    );
                }
                // Addition
                else {

                    // Emit the add operation
                    // add Rd, Rs, Rs
                    theParser.emitToOutput(
                        String.format(
                            "\tadd r%d, r%d, r%d\n",
                            theParser.getRegister(result),
                            theParser.getRegister(lhs),
                            theParser.getRegister(rhs)
                        )
                    );
                }
            }
            // Multiplicative operation [ *, / ]
            else if (
                TerminalToken.MULTIPLICATIVE_OP
                    .equals(operator.getLexeme().getTokenType())
            ) {

                // Divide
                if (operator.getLexeme().getValue().equals("/")) {

                    // Output the divide instruction with the boilerplate
                    // to branch to the libgcc __aeabi_idiv function that
                    // handles integer division
                    theParser.emitToOutput(
                        String.format(
                            """
                            \tmov r0, r%d
                            \tmov r1, r%d
                            \tbl __aeabi_idiv
                            \tmov r%d, r0
                            """,
                            theParser.getRegister(lhs),
                            theParser.getRegister(rhs),
                            theParser.getRegister(result)
                        )
                    );
                }
                // Multiply
                else {

                    // Output multiply instruction with lhs and rhs and result
                    // mul Rd, Rs, Rs
                    theParser.emitToOutput(
                        String.format(
                            "\tmul r%d, r%d, r%d\n",
                            theParser.getRegister(result),
                            theParser.getRegister(lhs),
                            theParser.getRegister(rhs)
                        )
                    );
                }
            }
            // Relational operator [ <, <=, <>, =, >, >= ]
            else if (
                TerminalToken.RELATIONAL_OP
                    .equals(operator.getLexeme().getTokenType())
            ) {

                // Emit the comparison for the right hand side and the left
                // hand side
                theParser.emitToOutput(
                    String.format(
                        "\tcmp r%d, r%d\n",
                        theParser.getRegister(lhs),
                        theParser.getRegister(rhs)
                    )
                );

                // Determine which "branch on false" should be selected
                // based on their
                switch (operator.getLexeme().getValue()) {
                case "<":
                    operation = "bge";
                    break;
                case "<=":
                    operation = "bgt";
                    break;
                case "<>":
                    operation = "beq";
                    break;
                case "=":
                    operation = "bne";
                    break;
                case ">":
                    operation = "ble";
                    break;
                case ">=":
                    operation = "blt"; // Yum
                    break;
                }

                // Emit the branch instruction to the current end label
                theParser.emitToOutput(
                    String.format(
                        "\t%s end%s\n",
                        operation,
                        theParser.getLabelStack().peek()
                    )
                );
            }

            // Release the left hand side and right hand side symbols from
            // their registers
            theParser.freeRegister(lhs);
            theParser.freeRegister(rhs);

            // Push the result temporary register onto the operand stack
            theParser.getOperandStack().push(result);
        }
    },

    DECLARE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Perform the default behavior for Action symbols
            super.doTheThing(theParser);

            Symbol theOperand;

            // Pop the operand from the top of the operand stack
            theOperand = theParser.getOperandStack().pop();

            // Mark the operand as allocated by getting the next
            // variable number from the parser
            if (theOperand.getVariableNumber() == Parser.NOT_ALLOCATED) {
                theOperand.setVariableNumber(
                    theParser.incrementVariableNumber()
                );
            }
            // Variable is already declared, throw an exception
            else {

                // TODO Add scope to message
                throw new VariableAlreadyDefinedException(
                    String.format(
                        "Variable %s is already defined",
                        theOperand.getLexeme().getValue()
                    )
                );
            }

            // TODO Add scope-aware semantics
            // TODO Aggregate all stack ptr add instructions
            theParser.emitToOutput("\tadd sp, sp, #-4\n");
        }
    },

    END_LABEL() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Perform the default behavior for the Action symbol
            super.doTheThing(theParser);

            // Output the end label. Use the top of the stack
            // to determine which label to output
            theParser.emitToOutput(
               String.format(
                   """
                   
                   .balign 4
                   %s%d:
                   """,
                   Parser.END_LABEL_PREFIX,
                   theParser.getLabelStack().peek()
               )
            );
        }
    },

    GEN_LABELS() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.getLabelStack().push(
                theParser.incrementNumLabels()
            );
        }
    },

    GOTO_BEGIN() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.emitToOutput(
                String.format("\tb begin%d\n", theParser.getLabelStack().peek())
            );
        }
    },

    POP_LABELS() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);
            theParser.getLabelStack().pop();
        }
    },

    PRINT_IFMT() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);
            theParser.emitToOutput("\tldr r0, =ifmt\n");
        }
    },

    PRINT_PRINTF() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            Symbol theSymbolToPrint = theParser.getOperandStack().pop();
            int register = theParser.getRegister(theSymbolToPrint);

            theParser.emitToOutput(String.format("\tmov r1, r%d\n", register));
            theParser.emitToOutput("\tbl printf\n");

            theParser.freeRegister(theSymbolToPrint);
        }
    },

    PRINT_SFMT() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            theParser.emitToOutput("\tldr r0, =sfmt\n");
        }
    },

    SIGN() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            super.doTheThing(theParser);

            Symbol theSign = theParser.getOperatorStack().pop();
            Symbol theOperand = theParser.getOperandStack().pop();
            Symbol newAnonymousSymbol = new Symbol();

            // Negate if the operator is a negative sign
            if (theSign.getLexeme().getValue().equals("-")) {
                theParser.emitToOutput(
                    String.format(
                        "\tneg r%d, r%d\n",
                        theParser.getRegister(newAnonymousSymbol),
                        theParser.getRegister(theOperand)
                    )
                );
            }
            // Otherwise, it is a positive number, use a simple move
            else {
                theParser.emitToOutput(
                    String.format(
                        "\tmov r%d, r%d\n",
                        theParser.getRegister(newAnonymousSymbol),
                        theParser.getRegister(theOperand)
                    )
                );
            }

            // Free the operand's register
            theParser.freeRegister(theOperand);

            // Push the new anonymous symbol onto the operand stack
            theParser.getOperandStack().push(newAnonymousSymbol);
        }
    },

    STORE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {
            Symbol rhs;
            Symbol lhs;

            // Perform the common behavior for all Action symbols
            super.doTheThing(theParser);

            // Get the first operand off the stack, the "right hand side"
            rhs = theParser.getOperandStack().pop();

            // Get the second operand off the stack and get the
            lhs = theParser.getOperandStack().pop();

            if (lhs.getVariableNumber() != Parser.NOT_ALLOCATED) {
                // Emit the str instruction, getting the variable number of the
                // left hand side and computing the offset to store the rhs
                // in the proper memory location
                theParser.emitToOutput(
                    String.format(
                        "\tstr r%d, [fp, #%d]\n",
                        theParser.getRegister(rhs),
                        Parser.LOCAL_VAR_OFFSET * lhs.getVariableNumber()
                    )
                );
            } else {
                throw new VariableNotDefinedException(
                    String.format(
                        "Line %d Char %d - Variable %s is not defined",
                        theParser.getLexicalAnalyzer().getLineNumber(),
                        theParser.getLexicalAnalyzer().getCharacterNumber(),
                        lhs.getLexeme().getValue()
                    )
                );
            }

            // Free the left hand and right hand sides from their registers
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
            theParser.emitToOutput(
                String.format(
                    """
                    /****************************************
                     *                                      *
                     *  AUTO-GENERATED, DO NOT MODIFY       *
                     *                                      *
                     *  FILE: %-30s*
                     *  DATE: %s  *
                     *                                      *
                     ****************************************/
                     
                    .extern printf
                    .extern __aeabi_idiv
                     
                    .global main
                    .text
                    
                    .balign 4
                    main:
                    \tpush { lr }
                    \tmov fp, sp
                    """,

                    theParser.getLexicalAnalyzer().getFileName(),
                    new Date()
                )
            );
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

            // Perform the default
            super.doTheThing(theParser);

            // Emit the program quit boilerplate
            theParser.emitToOutput(
                """
                
                .text
                .balign 4
                quit:
                \tmov sp, fp
                \tpop { lr }
                \tmov r0, #0
                \tbx lr
                
                .data
                """
            );

            for (String key : theParser.getStringConstants().keySet()) {

                // Emit each string in the constant pool in the data
                // section
                theParser.emitToOutput(
                    String.format(
                        """
                        
                        .balign 4
                        %s:
                        \t.asciz %s
                        """,
                        theParser
                            .getStringConstants()
                            .get(key)
                            .getLexeme()
                            .getValue(),
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

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

            // Do default Action behavior
            super.doTheThing(theParser);

            // Tell the parser to clear all registers
            theParser.clearRegisters();
        }
    },

    COMPUTE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Variables
            Symbol rhs;
            Symbol lhs;
            Symbol operator;
            Symbol newAnonymousSymbol;
            String operation;

            // Perform the default behavior for Actions
            super.doTheThing(theParser);

            // Create a new anonymous symbol
            newAnonymousSymbol = new Symbol();
            operation = "";

            // Pop the operator off of the top of stack
            operator = theParser.getOperatorStack().pop();

            // Get the right hand side from the operand stack
            rhs = theParser.getOperandStack().pop();

            // Get the left hand side from the operand stack
            // if the stack is not empty
            if (!theParser.getOperandStack().isEmpty()) {
                lhs = theParser.getOperandStack().pop();
            } else {

                // If operand stack is empty, this means the LHS is missing
                // from the source code since there was only one thing
                // pushed onto the stack
                throw new CompilerException(
                    String.format(
                        "Line %d Char %d - " +
                        "Missing RHS of expression \"%s %s ???\"",

                        rhs.getLexeme().getLineNumber(),
                        rhs.getLexeme().getBeginningCharNumber(),
                        rhs.getLexeme().getValue(),
                        operator.getLexeme().getValue()
                    )
                );
            }

            // Additive op [ +, - ]
            if (
                operator.getLexeme().getTokenType()
                    .equals(TerminalToken.ADDITIVE_OP)
            ) {

                // Default operation is "add"
                operation = "add";

                // If the operator is a "-" then the operation is
                // a subtraction
                if (operator.getLexeme().getValue().equals("-")) {
                    operation = "sub";
                }

                // Emit the operation
                // "operation" Rd, Rs, Rs
                theParser.emitToOutput(
                    String.format(
                        """
                        \t%s r%d, r%d, r%d
                        """,

                        operation,
                        theParser.getRegister(newAnonymousSymbol),
                        theParser.getRegister(lhs),
                        theParser.getRegister(rhs)
                    )
                );

                // Push the new temporary symbol onto the operand stack
                // to be used in a future operation
                theParser.getOperandStack().push(newAnonymousSymbol);
            }
            // Multiplicative operation [ *, / ]
            else if (
                operator.getLexeme().getTokenType()
                    .equals(TerminalToken.MULTIPLICATIVE_OP)
            ) {

                // Divide
                if (operator.getLexeme().getValue().equals("/")) {

                    // Output the divide instruction with the boilerplate
                    // to branch to the libgcc __aeabi_idiv function that
                    // handles integer division. The numerator and denominator
                    // must be placed in r0 and r1 respectively
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
                            theParser.getRegister(newAnonymousSymbol)
                        )
                    );
                }
                // Multiply
                else {

                    // Output multiply instruction with lhs and rhs and result
                    // mul Rd, Rs, Rs
                    theParser.emitToOutput(
                        String.format(
                            """
                            \tmul r%d, r%d, r%d
                            """,

                            theParser.getRegister(newAnonymousSymbol),
                            theParser.getRegister(lhs),
                            theParser.getRegister(rhs)
                        )
                    );
                }

                // Push the result temporary register onto the operand stack
                theParser.getOperandStack().push(newAnonymousSymbol);
            }
            // Relational operator [ <, <=, <>, =, >, >= ]
            else if (
                operator.getLexeme().getTokenType()
                    .equals(TerminalToken.RELATIONAL_OP)
            ) {

                // Emit the comparison for the right hand side and the left
                // hand side
                theParser.emitToOutput(
                    String.format(
                        """
                        \tcmp r%d, r%d
                        """,

                        theParser.getRegister(lhs),
                        theParser.getRegister(rhs)
                    )
                );

                // Determine which "branch on false" should be selected
                // based on the operation. It should be the opposite of the
                // operation as indicated by the source code
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
                        """
                        \t%s %s%s
                        """,

                        operation,
                        Parser.END_LABEL_PREFIX,
                        theParser.getLabelStack().peek()
                    )
                );
            }

            // Release the left hand side and right hand side symbols from
            // their registers. We will not free the temporary register
            // as it will be used immediately following this operation
            theParser.freeRegister(lhs);
            theParser.freeRegister(rhs);
        }
    },

    DECLARE() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Variables
            Symbol theOperand;

            // Perform the default behavior for Action symbols
            super.doTheThing(theParser);

            // Pop the operand from the top of the operand stack
            theOperand = theParser.getOperandStack().pop();

            // Mark the operand as allocated by getting the next
            // variable number from the parser
            if (theOperand.getVariableNumber() == Parser.NOT_ALLOCATED) {
                theOperand.setVariableNumber(
                    theParser.incrementVariableNumber()
                );
            } else {

                // Variable is already declared, throw an exception
                throw new VariableAlreadyDefinedException(
                    String.format(
                        "Line %d Char %d - Variable %s is already defined",

                        theOperand.getLexeme().getLineNumber(),
                        theOperand.getLexeme().getBeginningCharNumber(),
                        theOperand.getLexeme().getValue()
                    )
                );
            }

            // "Bump" the stack pointer
            // TODO Add scope-aware semantics
            // TODO Aggregate all stack ptr add instructions
            theParser.emitToOutput(
                """
                \tadd sp, sp, #-4
                """
            );
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

            // Do the default Action behavior
            super.doTheThing(theParser);

            // Push a new label number onto the label stack
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

            // Do default Action behavior
            super.doTheThing(theParser);

            // Branch to the top of stack begin label
            theParser.emitToOutput(
                String.format(
                    """
                    \tb %s%d
                    """,

                    Parser.BEGIN_LABEL_PREFIX,
                    theParser.getLabelStack().peek()
                )
            );
        }
    },

    POP_LABELS() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Do the default behavior of the Action
            super.doTheThing(theParser);

            // Pop the top labels off the stack
            theParser.getLabelStack().pop();
        }
    },

    PRINT_IFMT() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Do the default behavior of the Action
            super.doTheThing(theParser);

            // Emit the string format load into r0
            theParser.emitToOutput(
                String.format(
                    """
                    \tldr r0, =%s
                    """,

                    Parser.INTEGER_FORMAT_NAME
                )
            );
        }
    },

    PRINT_PRINTF() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Variables
            Symbol theSymbolToPrint;

            // Do the default Action behavior
            super.doTheThing(theParser);

            // Pop the symbol to print off the operand stack
            theSymbolToPrint = theParser.getOperandStack().pop();

            // Move it into the register r1
            theParser.emitToOutput(
                String.format(
                    """
                    \tmov r1, r%d
                    """,

                    theParser.getRegister(theSymbolToPrint)
                )
            );

            // Branch-and-link to printf
            theParser.emitToOutput("\tbl printf\n");

            // Free the symbol from its register
            theParser.freeRegister(theSymbolToPrint);
        }
    },

    PRINT_SFMT() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Do the default behavior of the Action
            super.doTheThing(theParser);

            // Emit the string format load into r0
            theParser.emitToOutput(
                String.format(
                    """
                    \tldr r0, =%s
                    """,

                    Parser.STRING_FORMAT_NAME
                )
            );
        }
    },

    SIGN() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void doTheThing(Parser theParser) throws CompilerException {

            // Variables
            Symbol theSign;
            Symbol theOperand;
            Symbol newAnonymousSymbol;
            String operation;

            // Perform the default Action behavior
            super.doTheThing(theParser);

            // Pop from the operator and operand stacks and create
            // a new anonymous symbol
            theSign = theParser.getOperatorStack().pop();
            theOperand = theParser.getOperandStack().pop();
            newAnonymousSymbol = new Symbol();

            // Default operation is to move; the sign is the
            // add operator
            operation = "mov";

            // Perform the negation operation if the operator
            // is a negative sign
            if (theSign.getLexeme().getValue().equals("-")) {
                operation = "neg";
            }

            // Emit the move or negation instruction to the Parser
            theParser.emitToOutput(
                String.format(
                    """
                    \t%s r%d, r%d
                    """,

                    operation,
                    theParser.getRegister(newAnonymousSymbol),
                    theParser.getRegister(theOperand)
                )
            );

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

            // Variables
            Symbol rhs;
            Symbol lhs;
            Symbol operator;

            // Perform the common behavior for all Action symbols
            super.doTheThing(theParser);

            // Pop the assignment operator off the operator stack
            operator = theParser.getOperatorStack().pop();

            // Get the first operand off the stack, the right hand side
            // of the assignment expression
            rhs = theParser.getOperandStack().pop();

            // Get the second operand off the stack, the "left hand side"
            // of the assignment expression if the stack is not empty
            if (!theParser.getOperandStack().isEmpty()) {
                lhs = theParser.getOperandStack().pop();
            } else {

                // If operand stack is empty, this means the RHS is missing
                // from the source code since there was only one thing
                // pushed onto the stack. This can happen because the Action
                // immediately follows the ASSIGNMENT_OP
                throw new CompilerException(
                    String.format(
                        "Line %d Char %d - " +
                        "Missing RHS of expression \"%s %s ???\"",

                        rhs.getLexeme().getLineNumber(),
                        rhs.getLexeme().getBeginningCharNumber(),
                        rhs.getLexeme().getValue(),
                        operator.getLexeme().getValue()
                    )
                );
            }

            // Store the rhs into the lhs if it is allocated, else
            // throw an exception indicating the lhs is not defined
            if (lhs.getVariableNumber() != Parser.NOT_ALLOCATED) {

                // Emit the str instruction, getting the variable number of the
                // left hand side and computing the offset to store the rhs
                // in the proper memory location
                theParser.emitToOutput(
                    String.format(
                        """
                        \tstr r%d, [fp, #%d]
                        """,
                        theParser.getRegister(rhs),
                        Parser.LOCAL_VAR_OFFSET * lhs.getVariableNumber()
                    )
                );
            } else {

                // Variable is not defined, throw an exception indicating
                // where the
                throw new VariableNotDefinedException(
                    String.format(
                        "Line %d Char %d - Variable %s is not defined",

                        lhs.getLexeme().getLineNumber(),
                        lhs.getLexeme().getBeginningCharNumber(),
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

            // Perform the default behavior of the Action token
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

            // Emit each string in the constant pool in the data
            // section
            for (String key : theParser.getStringConstants().keySet()) {
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

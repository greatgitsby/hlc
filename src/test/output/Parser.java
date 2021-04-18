package com.greatgitsby.hlc;

import java.io.IOException;
import java.util.*;

/**
 * Parser
 *
 * Defines the behavior of the parser for the HansenLite compiler.
 * It is the following stage, processing the stream of tokens from
 * the LexicalAnalyzer. As well, it will perform code generation during the
 * parse stage in the form of ARM assembly.
 */
public class Parser {

    // 4 is the min, r0-r3 can be augmented by function calls
    // as per ARM convention
    private static final int FIRST_REGISTER_LOC = 4;

    // 10 is max, 11 and on have special meaning (r11 = FP, etc.)
    private static final int LAST_REGISTER_LOC = 10;

    // Indicates if a register is allocated or not
    public static final int NOT_ALLOCATED = -1;

    // The local offset for variables
    public static final int LOCAL_VAR_OFFSET = -4;

    // Emitted label prefixes and format names, can alter if needed
    public static final String BEGIN_LABEL_PREFIX  = "begin_";
    public static final String END_LABEL_PREFIX    = "end_";
    public static final String STRING_CONST_PREFIX = "str_";
    public static final String STRING_FORMAT_NAME  = "sfmt";
    public static final String INTEGER_FORMAT_NAME = "ifmt";

    // Parser immutable internal state
    private final LexicalAnalyzer                     _lexicalAnalyzer;
    private final Map<Token, Map<Token, List<Token>>> _parseTable;
    private final ArrayDeque<Integer>                 _labelStack;
    private final ArrayDeque<Symbol>                  _operandStack;
    private final ArrayDeque<Symbol>                  _operatorStack;
    private final ArrayDeque<Token>                   _parseStack;
    private final Symbol[]                            _registers;
    private final StringBuilder                       _asmCode;
    private final Map<String, Symbol>                 _stringConstants;

    // Parser mutable internal state
    private Lexeme _currentLexeme;
    private Token  _currentParserSymbol;
    private int    _numVars;
    private int    _numLabels;

    /**
     * Construct a new Parser
     */
    public Parser(LexicalAnalyzer lexer) throws CompilerException, IOException {

        // Initialize lexer in Parser
        _lexicalAnalyzer = lexer;
        _currentLexeme = null;
        _currentParserSymbol = null;
        _numVars = 0;
        _numLabels = 0;

        // Construct the parse table
        _parseTable = buildParseTable();
        _stringConstants = new HashMap<>();

        // Assembly code aggregator
        _asmCode = new StringBuilder();

        // Initialize data structures
        _labelStack = new ArrayDeque<>();
        _operandStack = new ArrayDeque<>();
        _operatorStack = new ArrayDeque<>();
        _parseStack = new ArrayDeque<>();

        // Create the register array
        _registers = new Symbol[LAST_REGISTER_LOC - FIRST_REGISTER_LOC];

        // Put the integer format into the constant pool
        _stringConstants.put("\"%d\"", new Symbol(
            new Lexeme(
                INTEGER_FORMAT_NAME,
                TerminalToken.STRING_CONST
            )
        ));

        // Put the string format into the constant pool
        _stringConstants.put("\"%s\"", new Symbol(
            new Lexeme(
                STRING_FORMAT_NAME,
                TerminalToken.STRING_CONST
            )
        ));

        // Begin parsing the output of the Lexical Analyzer
        parse();
    }

    private void parse() throws CompilerException, IOException {
        // Push the epilogue action onto the parse stack
        getParseStack().push(Action.EPILOGUE);

        // Push the end symbol ($) onto the parse stack
        getParseStack().push(TerminalToken.END_OF_INPUT);

        // Push the entry point into the grammar, in this case the
        // STATEMENT non-terminal, onto the parse stack
        getParseStack().push(NonTerminalToken.STATEMENT);

        // Push the prologue action onto the parse stack
        getParseStack().push(Action.PROLOGUE);

        // Get the first symbol from the lexical analyzer
        setCurrentLexerSymbol(getLexicalAnalyzer().nextSymbol());

        // Process symbols on the parse stack until the stack is empty
        while (!getParseStack().isEmpty()) {
            // Get the top of parse stack
            setTopOfParseStack(getParseStack().peek());

            // Tell the symbol on the top of the parse stack to
            // do its thing. If it does not succeed, it is due to a syntax
            // error
            getTopOfParseStack().doTheThing(this);
        }
    }

    /**
     * Returns the current lexeme from the Lexical Analyzer
     *
     * @return the current lexeme from the Lexical Analyzer
     */
    public Lexeme getCurrentLexeme() {
        return _currentLexeme;
    }

    /**
     * Returns the current top of stack of the parser symbol
     *
     * @return the current top of stack of the parser symbol
     */
    public Token getTopOfParseStack() {
        return _currentParserSymbol;
    }

    /**
     * Returns the lexical analyzer of this Parser
     *
     * @return the lexical analyzer of this Parser
     */
    public LexicalAnalyzer getLexicalAnalyzer() {
        return _lexicalAnalyzer;
    }

    /**
     * Returns the parse table of this Parser
     *
     * @return the parse table of this Parser
     */
    public Map<Token, Map<Token, List<Token>>> getParseTable() {
        return _parseTable;
    }

    /**
     * Returns the label stack of this Parser
     *
     * @return the label stack of this Parser
     */
    public ArrayDeque<Integer> getLabelStack() {
        return _labelStack;
    }

    /**
     * Returns the operand stack of this Parser
     *
     * @return the operand stack of this Parser
     */
    public ArrayDeque<Symbol> getOperandStack() {
        return _operandStack;
    }

    /**
     * Returns the operator stack of this Parser
     *
     * @return the operator stack of this Parser
     */
    public ArrayDeque<Symbol> getOperatorStack() {
        return _operatorStack;
    }

    /**
     * Returns the parse stack of this Parser
     *
     * @return the parse stack of this Parser
     */
    public ArrayDeque<Token> getParseStack() {
        return _parseStack;
    }

    /**
     * Returns the output "stream" where the assembled code will go
     *
     * @return the output "stream" where the assembled code will go
     */
    public StringBuilder getOutput() {
        return _asmCode;
    }

    /**
     * Dump the output string
     *
     * @return the compiler output
     */
    public String dumpOutput() {
        return _asmCode.toString();
    }

    /**
     * Sets the new top of lexer stack
     *
     * @param theNewLexeme the new top of lexer stack
     */
    public void setCurrentLexerSymbol(Lexeme theNewLexeme) {
        _currentLexeme = theNewLexeme;
    }

    /**
     * Sets the new top of parse stack
     *
     * @param theNewTopOfStack the new top of parse stack
     */
    public void setTopOfParseStack(Token theNewTopOfStack) {
        _currentParserSymbol = theNewTopOfStack;
    }

    /**
     * Returns the pool of string constants
     *
     * @return the pool of string constants
     */
    public Map<String, Symbol> getStringConstants() {
        return _stringConstants;
    }

    /**
     * Load a symbol into a register. Emits the proper ARM instructions based
     * on the type of the symbol
     *
     * @param theSymbol the symbol to load
     * @throws CompilerException if the variable was not defined
     */
    private void loadSymbolIntoRegister(Symbol theSymbol)
        throws CompilerException
    {

        // Symbol is a number or string const
        if (
            theSymbol
                .getLexeme()
                .getTokenType()
                .equals(TerminalToken.NUMBER) ||
                theSymbol
                    .getLexeme()
                    .getTokenType()
                    .equals(TerminalToken.STRING_CONST)
        ) {
            // Emit the load instruction for string
            // labels or immediate values (numbers)
            emitToOutput(
                String.format(
                    "\tldr r%d, =%s\n",
                    theSymbol.getRegister(),
                    theSymbol.getLexeme().getValue()
                )
            );
        }
        // Symbol has been declared
        else if (
            theSymbol.getVariableNumber() != NOT_ALLOCATED
        ) {
            // Emit the load instruction for a local variable,
            // obtaining the offset from the symbol
            emitToOutput(
                String.format(
                    "\tldr r%d, [fp, #%d]\n",

                    theSymbol.getRegister(),
                    LOCAL_VAR_OFFSET *
                        theSymbol.getVariableNumber()
                )
            );
        }
        // Symbol is not defined, must throw exception
        else {
            throw new VariableNotDefinedException(
                String.format(
                    "Line %d Char %d - " +
                    "Variable %s is not defined",

                    getLexicalAnalyzer().getLineNumber(),
                    getLexicalAnalyzer().getCharacterNumber(),
                    theSymbol.getLexeme().getValue()
                )
            );
        }
    }

    /**
     * TODO Description
     *
     * @return the register, -1 if it could not provision
     */
    public int getRegister(Symbol theSymbol) throws CompilerException {
        boolean foundFree = false;
        int register = theSymbol.getRegister();

        // If the register is not already allocated, attempt to find
        // a location for it
        if (register == NOT_ALLOCATED) {
            for (int i = 0; i < _registers.length && !foundFree; i++) {

                // Indicates this register is not currently occupied
                if (_registers[i] == null) {
                    foundFree = true;

                    // Place the symbol in the register
                    _registers[i] = theSymbol;

                    // Add the lower offset to the register number
                    register = i += FIRST_REGISTER_LOC;

                    // Set the symbol's register for quick returns later
                    theSymbol.setRegister(register);

                    // Load the symbol into memory if it is not anonymous
                    // (it contains a lexeme)
                    if (theSymbol.getLexeme() != null) {
                        loadSymbolIntoRegister(theSymbol);
                    }
                }
            }

            // If a free register was not found, all registers have been
            // consumed, throw exception
            if (!foundFree) {
                throw new RegisterAllocationException("No free registers");
            }
        }

        return register;
    }

    /**
     * Free the register that a given Symbol holds
     *
     * @param theSymbol the symbol to free
     */
    public void freeRegister(Symbol theSymbol) {
        for (int i = 0; i < _registers.length; i++) {
            if (_registers[i] != null && _registers[i].equals(theSymbol)) {

                // "Deallocate" the register from the symbol's
                // perspective
                _registers[i].setRegister(NOT_ALLOCATED);

                // Remove the symbol from the register array
                _registers[i] = null;
            }
        }
    }

    /**
     * Clear all registers
     */
    public void clearRegisters() {

        // Deallocate every symbol from its position in the
        // register bank
        for (Symbol theSymbol : _registers) {
            if (theSymbol != null) {
                theSymbol.setRegister(NOT_ALLOCATED);
            }
        }

        // Nullify the contents of each register
        Arrays.fill(_registers, null);
    }

    /**
     * Increment the number of variables counter, to be used
     * as an offset on the ARM stack
     *
     * @return the new variable number count
     */
    public int incrementVariableNumber() {
        return ++_numVars;
    }

    /**
     * Increment the number of labels for the begin and end labels
     *
     * @return the new number of labels
     */
    public int incrementNumLabels() {
        return ++_numLabels;
    }

    /**
     * Emit some ARM assembly or other data to the "output"
     *
     * @param line the line to output
     */
    public void emitToOutput(String line) {
        getOutput().append(line);
    }

    /**
     * Build the parser table as defined by the HansenLite grammar.
     *
     * This function is very verbose. Above each internal table entry
     * is the associated grammar entry in the language.
     *
     * @return the completed parse table
     */
    private Map<Token, Map<Token, List<Token>>> buildParseTable() {
        return new HashMap<>() {{

            // statement
            put(NonTerminalToken.STATEMENT, new HashMap<>() {{

                // statement -> identifier assignment_operator expression
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(TerminalToken.IDENTIFIER);
                    add(TerminalToken.ASSIGNMENT_OP);
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.STORE);
                }});

                // statement ->
                //     if boolean_expression then statement else_clause
                put(TerminalToken.IF, new ArrayList<>() {{
                    add(TerminalToken.IF);
                    add(Action.GEN_LABELS);
                    add(NonTerminalToken.BOOLEAN_EXPRESSION);
                    add(TerminalToken.THEN);
                    add(NonTerminalToken.STATEMENT);
                    add(Action.GOTO_BEGIN);
                    add(Action.END_LABEL);
                    add(NonTerminalToken.ELSE_CLAUSE);
                    add(Action.BEGIN_LABEL);
                    add(Action.POP_LABELS);
                    add(Action.CLEAR_REGS);
                }});

                // statement -> while boolean_expression do statement
                put(TerminalToken.WHILE, new ArrayList<>() {{
                    add(TerminalToken.WHILE);
                    add(Action.GEN_LABELS);
                    add(Action.BEGIN_LABEL);
                    add(Action.CLEAR_REGS);
                    add(NonTerminalToken.BOOLEAN_EXPRESSION);
                    add(TerminalToken.DO);
                    add(NonTerminalToken.STATEMENT);
                    add(Action.GOTO_BEGIN);
                    add(Action.END_LABEL);
                    add(Action.POP_LABELS);
                    add(Action.CLEAR_REGS);
                }});

                // statement -> print print_expression
                put(TerminalToken.PRINT, new ArrayList<>() {{
                    add(TerminalToken.PRINT);
                    add(NonTerminalToken.PRINT_EXPRESSION);
                    add(Action.PRINT_PRINTF);
                }});

                // statement -> begin statement_list end
                put(TerminalToken.BEGIN, new ArrayList<>() {{
                    add(TerminalToken.BEGIN);
                    add(NonTerminalToken.STATEMENT_LIST);
                    add(TerminalToken.END);
                }});

                // statement -> variable identifier
                put(TerminalToken.VARIABLE, new ArrayList<>() {{
                    add(TerminalToken.VARIABLE);
                    add(TerminalToken.IDENTIFIER);
                    add(Action.DECLARE);
                }});

                // else is in follow(statement), won't push anything new
                put(TerminalToken.ELSE, new ArrayList<>());

                // statement_sep is in follow(statement),
                // won't push anything new
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>());

                // end_of_input is in follow(statement),
                // won't push anything new
                put(TerminalToken.END_OF_INPUT, new ArrayList<>());

                // end is in follow(statement), won't push anything new
                put(TerminalToken.END, new ArrayList<>());
            }});

            // else_clause
            put(NonTerminalToken.ELSE_CLAUSE, new HashMap<>() {{

                // else_clause -> else statement
                put(TerminalToken.ELSE, new ArrayList<>() {{
                    add(TerminalToken.ELSE);
                    add(NonTerminalToken.STATEMENT);
                }});

                // statement_sep is in follow(else_clause),
                // won't push anything new
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>());

                // end_of_input is in follow(else_clause),
                // won't push anything new
                put(TerminalToken.END_OF_INPUT, new ArrayList<>());

                // end is in follow(else_clause),
                // won't push anything new
                put(TerminalToken.END, new ArrayList<>());
            }});

            // statement_list
            put(NonTerminalToken.STATEMENT_LIST, new HashMap<>() {{

                // statement_list -> statement sep_list
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(TerminalToken.IF, new ArrayList<>() {{
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(TerminalToken.WHILE, new ArrayList<>() {{
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(TerminalToken.PRINT, new ArrayList<>() {{
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(TerminalToken.BEGIN, new ArrayList<>() {{
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(TerminalToken.VARIABLE, new ArrayList<>() {{
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.SEPARATED_LIST);
                }});

                // end is in follow(statement), won't push anything new
                put(TerminalToken.END, new ArrayList<>());
            }});

            // separated_list
            put(NonTerminalToken.SEPARATED_LIST, new HashMap<>() {{

                // separated_list -> statement_sep statement sep_list
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>() {{
                    add(TerminalToken.STATEMENT_SEP);
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.SEPARATED_LIST);
                }});

                // end is in follow(sep_list), won't push anything new
                put(TerminalToken.END, new ArrayList<>());
            }});

            // print_expression
            put(NonTerminalToken.PRINT_EXPRESSION, new HashMap<>() {{

                // print_expression -> expression
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.PRINT_IFMT);
                }});

                // print_expression -> expression
                put(TerminalToken.LEFT_PAREN, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.PRINT_IFMT);
                }});

                // print_expression -> expression
                put(TerminalToken.NUMBER, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.PRINT_IFMT);
                }});

                // print_expression -> expression
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.PRINT_IFMT);
                }});

                // print_expression -> string_const
                put(TerminalToken.STRING_CONST, new ArrayList<>() {{
                    add(TerminalToken.STRING_CONST);
                    add(Action.PRINT_SFMT);
                }});

                // statement_sep is in follow(sep_list),
                // won't push anything new
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>());

                // end_of_input is in follow(sep_list),
                // won't push anything new
                put(TerminalToken.END_OF_INPUT, new ArrayList<>());

                // else is in follow(sep_list), won't push anything new
                put(TerminalToken.ELSE, new ArrayList<>());

                // end is in follow(sep_list), won't push anything new
                put(TerminalToken.END, new ArrayList<>());
            }});

            // boolean_expression
            put(NonTerminalToken.BOOLEAN_EXPRESSION, new HashMap<>() {{

                // boolean_expression -> expression relational_op expression
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RELATIONAL_OP);
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.COMPUTE);
                }});

                // boolean_expression -> expression relational_op expression
                put(TerminalToken.LEFT_PAREN, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RELATIONAL_OP);
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.COMPUTE);
                }});

                // boolean_expression -> expression relational_op expression
                put(TerminalToken.NUMBER, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RELATIONAL_OP);
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.COMPUTE);
                }});

                // boolean_expression -> expression relational_op expression
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RELATIONAL_OP);
                    add(NonTerminalToken.EXPRESSION);
                    add(Action.COMPUTE);
                }});

                // then is in follow(boolean_expression),
                // won't push anything new
                put(TerminalToken.THEN, new ArrayList<>());

                // do is in follow(boolean_expression),
                // won't push anything new
                put(TerminalToken.DO, new ArrayList<>());
            }});

            // expression
            put(NonTerminalToken.EXPRESSION, new HashMap<>() {{

                // expression -> term addition
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(NonTerminalToken.TERM);
                    add(NonTerminalToken.ADDITION);
                }});

                // expression -> term addition
                put(TerminalToken.LEFT_PAREN, new ArrayList<>() {{
                    add(NonTerminalToken.TERM);
                    add(NonTerminalToken.ADDITION);
                }});

                // expression -> term addition
                put(TerminalToken.NUMBER, new ArrayList<>() {{
                    add(NonTerminalToken.TERM);
                    add(NonTerminalToken.ADDITION);
                }});

                // expression -> term addition
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(NonTerminalToken.TERM);
                    add(NonTerminalToken.ADDITION);
                }});

                // end_of_input is in follow(expression),
                // won't push anything new
                put(TerminalToken.END_OF_INPUT, new ArrayList<>());

                // else is in follow(expression),
                // won't push anything new
                put(TerminalToken.ELSE, new ArrayList<>());

                // do is in follow(expression),
                // won't push anything new
                put(TerminalToken.DO, new ArrayList<>());

                // end is in follow(expression),
                // won't push anything new
                put(TerminalToken.END, new ArrayList<>());

                // statement_sep is in follow(expression),
                // won't push anything new
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(expression),
                // won't push anything new
                put(TerminalToken.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(expression),
                // won't push anything new
                put(TerminalToken.THEN, new ArrayList<>());

                // right_paren is in follow(expression),
                // won't push anything new
                put(TerminalToken.RIGHT_PAREN, new ArrayList<>());
            }});

            // addition
            put(NonTerminalToken.ADDITION, new HashMap<>() {{

                // addition -> additive_op term addition
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(TerminalToken.ADDITIVE_OP);
                    add(NonTerminalToken.TERM);
                    add(Action.COMPUTE);
                    add(NonTerminalToken.ADDITION);
                }});

                // end_of_input is in follow(addition),
                // won't push anything new
                put(TerminalToken.END_OF_INPUT, new ArrayList<>());

                // else is in follow(addition),
                // won't push anything new
                put(TerminalToken.ELSE, new ArrayList<>());

                // do is in follow(addition),
                // won't push anything new
                put(TerminalToken.DO, new ArrayList<>());

                // end is in follow(addition),
                // won't push anything new
                put(TerminalToken.END, new ArrayList<>());

                // statement_sep is in follow(addition),
                // won't push anything new
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(addition),
                // won't push anything new
                put(TerminalToken.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(addition),
                // won't push anything new
                put(TerminalToken.THEN, new ArrayList<>());

                // right_paren is in follow(addition),
                // won't push anything new
                put(TerminalToken.RIGHT_PAREN, new ArrayList<>());
            }});

            // term
            put(NonTerminalToken.TERM, new HashMap<>() {{

                // term -> factor multiplication
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(NonTerminalToken.FACTOR);
                    add(NonTerminalToken.MULTIPLICATION);
                }});

                // term -> factor multiplication
                put(TerminalToken.LEFT_PAREN, new ArrayList<>() {{
                    add(NonTerminalToken.FACTOR);
                    add(NonTerminalToken.MULTIPLICATION);
                }});

                // term -> factor multiplication
                put(TerminalToken.NUMBER, new ArrayList<>() {{
                    add(NonTerminalToken.FACTOR);
                    add(NonTerminalToken.MULTIPLICATION);
                }});

                // term -> factor multiplication
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(NonTerminalToken.FACTOR);
                    add(NonTerminalToken.MULTIPLICATION);
                }});

                // end_of_input is in follow(term),
                // won't push anything new
                put(TerminalToken.END_OF_INPUT, new ArrayList<>());

                // else is in follow(term),
                // won't push anything new
                put(TerminalToken.ELSE, new ArrayList<>());

                // do is in follow(term),
                // won't push anything new
                put(TerminalToken.DO, new ArrayList<>());

                // end is in follow(term),
                // won't push anything new
                put(TerminalToken.END, new ArrayList<>());

                // statement_sep is in follow(term),
                // won't push anything new
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(term),
                // won't push anything new
                put(TerminalToken.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(term),
                // won't push anything new
                put(TerminalToken.THEN, new ArrayList<>());

                // right_paren is in follow(term),
                // won't push anything new
                put(TerminalToken.RIGHT_PAREN, new ArrayList<>());

                // multiplicative_op is in follow(term),
                // won't push anything new
                put(TerminalToken.MULTIPLICATIVE_OP, new ArrayList<>());
            }});

            // multiplication
            put(NonTerminalToken.MULTIPLICATION, new HashMap<>() {{

                // multiplication -> multiplicative_op factor multiplication
                put(TerminalToken.MULTIPLICATIVE_OP, new ArrayList<>() {{
                    add(TerminalToken.MULTIPLICATIVE_OP);
                    add(NonTerminalToken.FACTOR);
                    add(Action.COMPUTE);
                    add(NonTerminalToken.MULTIPLICATION);
                }});

                // end_of_input is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.END_OF_INPUT, new ArrayList<>());

                // else is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.ELSE, new ArrayList<>());

                // do is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.DO, new ArrayList<>());

                // end is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.END, new ArrayList<>());

                // statement_sep is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.THEN, new ArrayList<>());

                // right_paren is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.RIGHT_PAREN, new ArrayList<>());

                // add_op is in follow(multiplication),
                // won't push anything new
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>());
            }});

            // factor
            put(NonTerminalToken.FACTOR, new HashMap<>() {{

                // factor -> identifier
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(TerminalToken.IDENTIFIER);
                }});

                // factor -> left_paren expression right_paren
                put(TerminalToken.LEFT_PAREN, new ArrayList<>() {{
                    add(TerminalToken.LEFT_PAREN);
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RIGHT_PAREN);
                }});

                // factor -> number
                put(TerminalToken.NUMBER, new ArrayList<>() {{
                    add(TerminalToken.NUMBER);
                }});

                // factor -> signed_term
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(NonTerminalToken.SIGNED_TERM);
                }});

                // end_of_input is in follow(factor),
                // won't push anything new
                put(TerminalToken.END_OF_INPUT, new ArrayList<>());

                // else is in follow(factor),
                // won't push anything new
                put(TerminalToken.ELSE, new ArrayList<>());

                // do is in follow(factor),
                // won't push anything new
                put(TerminalToken.DO, new ArrayList<>());

                // end is in follow(factor),
                // won't push anything new
                put(TerminalToken.END, new ArrayList<>());

                // statement_sep is in follow(factor),
                // won't push anything new
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(factor),
                // won't push anything new
                put(TerminalToken.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(factor),
                // won't push anything new
                put(TerminalToken.THEN, new ArrayList<>());

                // right_paren is in follow(factor),
                // won't push anything new
                put(TerminalToken.RIGHT_PAREN, new ArrayList<>());

                // multiplicative_op is in follow(factor),
                // won't push anything new
                put(TerminalToken.MULTIPLICATIVE_OP, new ArrayList<>());
            }});

            // signed_term
            put(NonTerminalToken.SIGNED_TERM, new HashMap<>() {{

                // signed_term -> additive_op term
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(TerminalToken.ADDITIVE_OP);
                    add(NonTerminalToken.TERM);
                    add(Action.SIGN);
                }});
            }});
        }};
    }
}

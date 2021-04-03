package com.greatgitsby.hlc;

import java.io.*;
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

    // Parser immutable internal state
    private final LexicalAnalyzer _lexicalAnalyzer;
    private final Map<Symbol, Map<Symbol, List<Symbol>>> _parseTable;
    private final Stack<Symbol> _labelStack;
    private final Stack<Symbol> _operandStack;
    private final Stack<Symbol> _operatorStack;
    private final Stack<Symbol> _parseStack;

    // Parser mutable internal state
    private Symbol _currentLexerSymbol;
    private Symbol _currentParserSymbol;

    /**
     * Construct a new Parser
     */
    public Parser(LexicalAnalyzer lexer) {

        // Initialize lexer in Parser
        _lexicalAnalyzer = lexer;
        _currentLexerSymbol = null;
        _currentParserSymbol = null;

        // Construct the parse table
        _parseTable = buildParseTable();

        // Initialize data structures
        _labelStack = new Stack<>();
        _operandStack = new Stack<>();
        _operatorStack = new Stack<>();
        _parseStack = new Stack<>();
    }

    /**
     * Determines if the syntax from the stream of symbols from the
     * LexicalAnalyzer is valid or not
     *
     * @return true if the syntax is valid. An exception will be thrown if
     *         the parser encountered a syntax error
     */
    public boolean isValidSyntax() throws SyntaxErrorException, IOException {
        // Push the end symbol ($) onto the parse stack
        getParseStack().push(TerminalToken.END_OF_INPUT);

        // Push the entry point into the grammar, in this case the
        // STATEMENT non-terminal, onto the parse stack
        getParseStack().push(NonTerminalToken.STATEMENT);

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

        return true;
    }

    /**
     * Returns the current lexer symbol
     *
     * @return the current lexer symbol
     */
    public Symbol getCurrentLexerSymbol() {
        return _currentLexerSymbol;
    }

    /**
     * Returns the current top of stack of the parser symbol
     *
     * @return the current top of stack of the parser symbol
     */
    public Symbol getTopOfParseStack() {
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
    public Map<Symbol, Map<Symbol, List<Symbol>>> getParseTable() {
        return _parseTable;
    }

    /**
     * Returns the label stack of this Parser
     *
     * @return the label stack of this Parser
     */
    public Stack<Symbol> getLabelStack() {
        return _labelStack;
    }

    /**
     * Returns the operand stack of this Parser
     *
     * @return the operand stack of this Parser
     */
    public Stack<Symbol> getOperandStack() {
        return _operandStack;
    }

    /**
     * Returns the operator stack of this Parser
     *
     * @return the operator stack of this Parser
     */
    public Stack<Symbol> getOperatorStack() {
        return _operatorStack;
    }

    /**
     * Returns the parse stack of this Parser
     *
     * @return the parse stack of this Parser
     */
    public Stack<Symbol> getParseStack() {
        return _parseStack;
    }

    /**
     * Sets the new top of lexer stack
     *
     * @param theNewSymbol the new top of lexer stack
     */
    public void setCurrentLexerSymbol(Symbol theNewSymbol) {
        _currentLexerSymbol = theNewSymbol;
    }

    /**
     * Sets the new top of parse stack
     *
     * @param theNewTopOfStack the new top of parse stack
     */
    public void setTopOfParseStack(Symbol theNewTopOfStack) {
        _currentParserSymbol = theNewTopOfStack;
    }

    /**
     * Build the parser table as defined by the HansenLite grammar.
     *
     * This function is very verbose. Above each internal table entry
     * is the associated grammar entry in the language.
     *
     * @return the completed parse table
     */
    private Map<Symbol, Map<Symbol, List<Symbol>>> buildParseTable() {
        return new HashMap<>() {{

            // STATEMENT
            put(NonTerminalToken.STATEMENT, new HashMap<>() {{

                // statement -> identifier assignment_operator expression
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(TerminalToken.IDENTIFIER);
                    add(TerminalToken.ASSIGNMENT_OP);
                    add(NonTerminalToken.EXPRESSION);
                }});

                // statement ->
                //     if boolean_expression then statement else_clause
                put(TerminalToken.IF, new ArrayList<>() {{
                    add(TerminalToken.IF);
                    add(NonTerminalToken.BOOLEAN_EXPRESSION);
                    add(TerminalToken.THEN);
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.ELSE_CLAUSE);
                }});

                // statement -> while boolean_expression do statement
                put(TerminalToken.WHILE, new ArrayList<>() {{
                    add(TerminalToken.WHILE);
                    add(NonTerminalToken.BOOLEAN_EXPRESSION);
                    add(TerminalToken.DO);
                    add(NonTerminalToken.STATEMENT);
                }});

                // statement -> print print_expression
                put(TerminalToken.PRINT, new ArrayList<>() {{
                    add(TerminalToken.PRINT);
                    add(NonTerminalToken.PRINT_EXPRESSION);
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

            // ELSE_CLAUSE
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

            // STATEMENT_LIST
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

            // SEPARATED_LIST
            put(NonTerminalToken.SEPARATED_LIST, new HashMap<>() {{

                // sep_list -> statement_sep statement sep_list
                put(TerminalToken.STATEMENT_SEP, new ArrayList<>() {{
                    add(TerminalToken.STATEMENT_SEP);
                    add(NonTerminalToken.STATEMENT);
                    add(NonTerminalToken.SEPARATED_LIST);
                }});

                // end is in follow(sep_list), won't push anything new
                put(TerminalToken.END, new ArrayList<>());
            }});

            // PRINT_EXPRESSION
            put(NonTerminalToken.PRINT_EXPRESSION, new HashMap<>() {{

                // print_expression -> expression
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                }});

                // print_expression -> expression
                put(TerminalToken.LEFT_PAREN, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                }});

                // print_expression -> expression
                put(TerminalToken.NUMBER, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                }});

                // print_expression -> expression
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                }});

                // print_expression -> string_const
                put(TerminalToken.STRING_CONST, new ArrayList<>() {{
                    add(TerminalToken.STRING_CONST);
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

            // BOOLEAN_EXPRESSION
            put(NonTerminalToken.BOOLEAN_EXPRESSION, new HashMap<>() {{

                // boolean_expression -> expression relational_op expression
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RELATIONAL_OP);
                    add(NonTerminalToken.EXPRESSION);
                }});

                // boolean_expression -> expression relational_op expression
                put(TerminalToken.LEFT_PAREN, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RELATIONAL_OP);
                    add(NonTerminalToken.EXPRESSION);
                }});

                // boolean_expression -> expression relational_op expression
                put(TerminalToken.NUMBER, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RELATIONAL_OP);
                    add(NonTerminalToken.EXPRESSION);
                }});

                // boolean_expression -> expression relational_op expression
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(NonTerminalToken.EXPRESSION);
                    add(TerminalToken.RELATIONAL_OP);
                    add(NonTerminalToken.EXPRESSION);
                }});

                // then is in follow(boolean_expression),
                // won't push anything new
                put(TerminalToken.THEN, new ArrayList<>());

                // do is in follow(boolean_expression),
                // won't push anything new
                put(TerminalToken.DO, new ArrayList<>());
            }});

            // EXPRESSION
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

            // ADDITION
            put(NonTerminalToken.ADDITION, new HashMap<>() {{

                // addition -> additive_op term addition
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(TerminalToken.ADDITIVE_OP);
                    add(NonTerminalToken.TERM);
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

            // TERM
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

            // MULTIPLICATION
            put(NonTerminalToken.MULTIPLICATION, new HashMap<>() {{

                // multiplication -> multiplicative_op factor multiplication
                put(TerminalToken.MULTIPLICATIVE_OP, new ArrayList<>() {{
                    add(TerminalToken.MULTIPLICATIVE_OP);
                    add(NonTerminalToken.FACTOR);
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

            // FACTOR
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

            // SIGNED_TERM
            put(NonTerminalToken.SIGNED_TERM, new HashMap<>() {{

                // signed_term -> additive_op term
                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(TerminalToken.ADDITIVE_OP);
                    add(NonTerminalToken.TERM);
                }});
            }});
        }};
    }
}

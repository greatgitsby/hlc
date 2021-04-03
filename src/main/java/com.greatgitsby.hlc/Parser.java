package com.greatgitsby.hlc;

import java.io.*;
import java.util.*;

/**
 * TODO Description
 */
public class Parser {
    private final LexicalAnalyzer _lexicalAnalyzer;
    private final Map<Symbol, Map<Symbol, List<Symbol>>> _parseTable;
    private final Stack<Symbol> _labelStack;
    private final Stack<Symbol> _operandStack;
    private final Stack<Symbol> _operatorStack;
    private final Stack<Symbol> _parseStack;

    private Symbol _currentLexerSymbol;
    private Symbol _currentParserSymbol;

    /**
     * Construct a new Parser
     */
    public Parser(LexicalAnalyzer lexer) {
        _lexicalAnalyzer = lexer;

        _currentLexerSymbol = null;
        _currentParserSymbol = null;

        _labelStack = new Stack<>();
        _operandStack = new Stack<>();
        _operatorStack = new Stack<>();
        _parseStack = new Stack<>();
        _parseTable = buildParseTable();
    }

    /**
     * TODO Description
     */
    public boolean isValidSyntax() throws SyntaxErrorException, IOException {
        boolean isValidSyntax = true;

        // Push the end symbol ($) onto the parse stack
        getParseStack().push(TerminalToken.END_OF_INPUT);
        getParseStack().push(NonTerminalToken.STATEMENT);

        while (
            getLexicalAnalyzer().hasNextLexeme() &&
            !getParseStack().isEmpty()
        ) {
            // Set the current lexer symbol to the top of the symbol stack
            setTopOfLexerStack(getLexicalAnalyzer().nextLexeme());

            // Get the top of parse stack
            setTopOfParseStack(getParseStack().peek());

            // Tell the symbol on the top of the parse stack to
            // do its thing. If it does not succeed, it is due to a syntax
            // error. Print where the error occurred
            getTopOfParseStack().doTheThing(this);
        }

        if (!getLexicalAnalyzer().hasNextLexeme()) {
            isValidSyntax = false;
        }

        return isValidSyntax;
    }

    /**
     * Returns the current lexer symbol
     *
     * @return the current lexer symbol
     */
    public Symbol getTopOfLexerStack() {
        return _currentLexerSymbol;
    }

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
     * @param theNewTopOfStack the new top of lexer stack
     */
    public void setTopOfLexerStack(Symbol theNewTopOfStack) {
        _currentLexerSymbol = theNewTopOfStack;
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
     * TODO Description
     *
     * @return the parse table
     */
    private Map<Symbol, Map<Symbol, List<Symbol>>> buildParseTable() {
        return new HashMap<>() {{
            put(NonTerminalToken.STATEMENT, new HashMap<>() {{

                // statement -> identifier assignment_operator expression
                put(TerminalToken.IDENTIFIER, new ArrayList<>() {{
                    add(TerminalToken.IDENTIFIER);
                    add(TerminalToken.ASSIGNMENT_OP);
                    add(NonTerminalToken.EXPRESSION);
                }});

                // statement -> if boolean_expression then statement else_clause
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

            put(NonTerminalToken.SIGNED_TERM, new HashMap<>() {{

                put(TerminalToken.ADDITIVE_OP, new ArrayList<>() {{
                    add(TerminalToken.ADDITIVE_OP);
                    add(NonTerminalToken.TERM);
                }});
            }});
        }};
    }
}

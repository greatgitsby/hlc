package com.greatgitsby.hlc;

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
    public boolean isValidSyntax() throws SyntaxErrorException {
        boolean isValidSyntax;

        isValidSyntax = true;

        // Push the end symbol ($) onto the parse stack
        getParseStack().push(Token.END_OF_INPUT);
        getParseStack().push(Token.STATEMENT);

        while (!getLexicalAnalyzer().getSymbolStack().isEmpty() && !getParseStack().isEmpty() && isValidSyntax) {
            // Set the current lexer symbol to the top of the symbol stack
            setTopOfLexerStack(getLexicalAnalyzer().getSymbolStack().peek());

            // Get the top of parse stack
            setTopOfParseStack(getParseStack().peek());

            // Tell the symbol on the top of the parse stack to
            // do its thing. If it does not succeed, it is due to a syntax
            // error. Print where the error occurred
            try {
                getTopOfParseStack().doTheThing(this);
            } catch (SyntaxErrorException e) {
                e.printStackTrace();
                isValidSyntax = false;
            }
        }

        if (!getLexicalAnalyzer().getSymbolStack().isEmpty()) {
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
    private void setTopOfLexerStack(Symbol theNewTopOfStack) {
        _currentLexerSymbol = theNewTopOfStack;
    }

    /**
     * Sets the new top of parse stack
     *
     * @param theNewTopOfStack the new top of parse stack
     */
    private void setTopOfParseStack(Symbol theNewTopOfStack) {
        _currentParserSymbol = theNewTopOfStack;
    }

    private Map<Symbol, Map<Symbol, List<Symbol>>> buildParseTable() {
        return new HashMap<>() {{
            put(Token.STATEMENT, new HashMap<>() {{

                // statement -> identifier assignment_operator expression
                put(Token.IDENTIFIER, new ArrayList<>() {{
                    add(Token.IDENTIFIER);
                    add(Token.ASSIGNMENT_OP);
                    add(Token.EXPRESSION);
                }});

                // statement -> if boolean_expression then statement else_clause
                put(Token.IF, new ArrayList<>() {{
                    add(Token.IF);
                    add(Token.BOOLEAN_EXPRESSION);
                    add(Token.THEN);
                    add(Token.STATEMENT);
                    add(Token.ELSE_CLAUSE);
                }});

                // statement -> while boolean_expression do statement
                put(Token.WHILE, new ArrayList<>() {{
                    add(Token.WHILE);
                    add(Token.BOOLEAN_EXPRESSION);
                    add(Token.DO);
                    add(Token.STATEMENT);
                }});

                // statement -> print print_expression
                put(Token.PRINT, new ArrayList<>() {{
                    add(Token.PRINT);
                    add(Token.PRINT_EXPRESSION);
                }});

                // statement -> begin statement_list end
                put(Token.BEGIN, new ArrayList<>() {{
                    add(Token.BEGIN);
                    add(Token.STATEMENT_LIST);
                    add(Token.END);
                }});

                // statement -> variable identifier
                put(Token.VARIABLE, new ArrayList<>() {{
                    add(Token.VARIABLE);
                    add(Token.IDENTIFIER);
                }});

                // else is in follow(statement), won't push anything new
                put(Token.ELSE, new ArrayList<>());

                // statement_sep is in follow(statement),
                // won't push anything new
                put(Token.STATEMENT_SEP, new ArrayList<>());

                // end_of_input is in follow(statement),
                // won't push anything new
                put(Token.END_OF_INPUT, new ArrayList<>());

                // end is in follow(statement), won't push anything new
                put(Token.END, new ArrayList<>());
            }});

            put(Token.ELSE_CLAUSE, new HashMap<>() {{

                // else_clause -> else statement
                put(Token.ELSE, new ArrayList<>() {{
                    add(Token.ELSE);
                    add(Token.STATEMENT);
                }});

                // statement_sep is in follow(else_clause),
                // won't push anything new
                put(Token.STATEMENT_SEP, new ArrayList<>());

                // end_of_input is in follow(else_clause),
                // won't push anything new
                put(Token.END_OF_INPUT, new ArrayList<>());

                // end is in follow(else_clause),
                // won't push anything new
                put(Token.END, new ArrayList<>());

                // else is in follow(else_clause),
                // won't push anything new
//                put(Token.ELSE, new ArrayList<>()); // ??????????????????????????????????????????????????????????????????????????????????????
            }});

            put(Token.STATEMENT_LIST, new HashMap<>() {{

                // statement_list -> statement sep_list
                put(Token.IDENTIFIER, new ArrayList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(Token.IF, new ArrayList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(Token.WHILE, new ArrayList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(Token.PRINT, new ArrayList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(Token.BEGIN, new ArrayList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                // statement_list -> statement sep_list
                put(Token.VARIABLE, new ArrayList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                // end is in follow(statement), won't push anything new
                put(Token.END, new ArrayList<>());
            }});

            put(Token.SEPARATED_LIST, new HashMap<>() {{

                // sep_list -> statement_sep statement sep_list
                put(Token.STATEMENT_SEP, new ArrayList<>() {{
                    add(Token.STATEMENT_SEP);
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                // end is in follow(sep_list), won't push anything new
                put(Token.END, new ArrayList<>());
            }});

            put(Token.PRINT_EXPRESSION, new HashMap<>() {{

                // print_expression -> expression
                put(Token.IDENTIFIER, new ArrayList<>() {{
                    add(Token.EXPRESSION);
                }});

                // print_expression -> expression
                put(Token.LEFT_PAREN, new ArrayList<>() {{
                    add(Token.EXPRESSION);
                }});

                // print_expression -> expression
                put(Token.NUMBER, new ArrayList<>() {{
                    add(Token.EXPRESSION);
                }});

                // print_expression -> expression
                put(Token.ADDITIVE_OP, new ArrayList<>() {{
                    add(Token.EXPRESSION);
                }});

                // print_expression -> string_const
                put(Token.STRING_CONST, new ArrayList<>() {{
                    add(Token.STRING_CONST);
                }});

                // statement_sep is in follow(sep_list),
                // won't push anything new
                put(Token.STATEMENT_SEP, new ArrayList<>());

                // end_of_input is in follow(sep_list),
                // won't push anything new
                put(Token.END_OF_INPUT, new ArrayList<>());

                // else is in follow(sep_list), won't push anything new
                put(Token.ELSE, new ArrayList<>());

                // end is in follow(sep_list), won't push anything new
                put(Token.END, new ArrayList<>());
            }});

            put(Token.BOOLEAN_EXPRESSION, new HashMap<>() {{

                // boolean_expression -> expression relational_op expression
                put(Token.IDENTIFIER, new ArrayList<>() {{
                    add(Token.EXPRESSION);
                    add(Token.RELATIONAL_OP);
                    add(Token.EXPRESSION);
                }});

                // boolean_expression -> expression relational_op expression
                put(Token.LEFT_PAREN, new ArrayList<>() {{
                    add(Token.EXPRESSION);
                    add(Token.RELATIONAL_OP);
                    add(Token.EXPRESSION);
                }});

                // boolean_expression -> expression relational_op expression
                put(Token.NUMBER, new ArrayList<>() {{
                    add(Token.EXPRESSION);
                    add(Token.RELATIONAL_OP);
                    add(Token.EXPRESSION);
                }});

                // boolean_expression -> expression relational_op expression
                put(Token.ADDITIVE_OP, new ArrayList<>() {{
                    add(Token.EXPRESSION);
                    add(Token.RELATIONAL_OP);
                    add(Token.EXPRESSION);
                }});

                // then is in follow(boolean_expression),
                // won't push anything new
                put(Token.THEN, new ArrayList<>());

                // do is in follow(boolean_expression),
                // won't push anything new
                put(Token.DO, new ArrayList<>());
            }});

            put(Token.EXPRESSION, new HashMap<>() {{

                // expression -> term addition
                put(Token.IDENTIFIER, new ArrayList<>() {{
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});

                // expression -> term addition
                put(Token.LEFT_PAREN, new ArrayList<>() {{
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});

                // expression -> term addition
                put(Token.NUMBER, new ArrayList<>() {{
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});

                // expression -> term addition
                put(Token.ADDITIVE_OP, new ArrayList<>() {{
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});

                // end_of_input is in follow(expression),
                // won't push anything new
                put(Token.END_OF_INPUT, new ArrayList<>());

                // else is in follow(expression),
                // won't push anything new
                put(Token.ELSE, new ArrayList<>());

                // do is in follow(expression),
                // won't push anything new
                put(Token.DO, new ArrayList<>());

                // end is in follow(expression),
                // won't push anything new
                put(Token.END, new ArrayList<>());

                // statement_sep is in follow(expression),
                // won't push anything new
                put(Token.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(expression),
                // won't push anything new
                put(Token.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(expression),
                // won't push anything new
                put(Token.THEN, new ArrayList<>());

                // right_paren is in follow(expression),
                // won't push anything new
                put(Token.RIGHT_PAREN, new ArrayList<>());
            }});

            put(Token.ADDITION, new HashMap<>() {{

                // addition -> additive_op term addition
                put(Token.ADDITIVE_OP, new ArrayList<>() {{
                    add(Token.ADDITIVE_OP);
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});

                // end_of_input is in follow(addition),
                // won't push anything new
                put(Token.END_OF_INPUT, new ArrayList<>());

                // else is in follow(addition),
                // won't push anything new
                put(Token.ELSE, new ArrayList<>());

                // do is in follow(addition),
                // won't push anything new
                put(Token.DO, new ArrayList<>());

                // end is in follow(addition),
                // won't push anything new
                put(Token.END, new ArrayList<>());

                // statement_sep is in follow(addition),
                // won't push anything new
                put(Token.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(addition),
                // won't push anything new
                put(Token.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(addition),
                // won't push anything new
                put(Token.THEN, new ArrayList<>());

                // right_paren is in follow(addition),
                // won't push anything new
                put(Token.RIGHT_PAREN, new ArrayList<>());
            }});

            put(Token.TERM, new HashMap<>() {{

                // term -> factor multiplication
                put(Token.IDENTIFIER, new ArrayList<>() {{
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});

                // term -> factor multiplication
                put(Token.LEFT_PAREN, new ArrayList<>() {{
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});

                // term -> factor multiplication
                put(Token.NUMBER, new ArrayList<>() {{
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});

                // term -> factor multiplication
                put(Token.ADDITIVE_OP, new ArrayList<>() {{
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});

                // end_of_input is in follow(term),
                // won't push anything new
                put(Token.END_OF_INPUT, new ArrayList<>());

                // else is in follow(term),
                // won't push anything new
                put(Token.ELSE, new ArrayList<>());

                // do is in follow(term),
                // won't push anything new
                put(Token.DO, new ArrayList<>());

                // end is in follow(term),
                // won't push anything new
                put(Token.END, new ArrayList<>());

                // statement_sep is in follow(term),
                // won't push anything new
                put(Token.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(term),
                // won't push anything new
                put(Token.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(term),
                // won't push anything new
                put(Token.THEN, new ArrayList<>());

                // right_paren is in follow(term),
                // won't push anything new
                put(Token.RIGHT_PAREN, new ArrayList<>());
            }});

            put(Token.MULTIPLICATION, new HashMap<>() {{

                // multiplication -> multiplicative_op factor multiplication
                put(Token.MULTIPLICATIVE_OP, new ArrayList<>() {{
                    add(Token.MULTIPLICATIVE_OP);
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});

                // end_of_input is in follow(multiplication),
                // won't push anything new
                put(Token.END_OF_INPUT, new ArrayList<>());

                // else is in follow(multiplication),
                // won't push anything new
                put(Token.ELSE, new ArrayList<>());

                // do is in follow(multiplication),
                // won't push anything new
                put(Token.DO, new ArrayList<>());

                // end is in follow(multiplication),
                // won't push anything new
                put(Token.END, new ArrayList<>());

                // statement_sep is in follow(multiplication),
                // won't push anything new
                put(Token.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(multiplication),
                // won't push anything new
                put(Token.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(multiplication),
                // won't push anything new
                put(Token.THEN, new ArrayList<>());

                // right_paren is in follow(multiplication),
                // won't push anything new
                put(Token.RIGHT_PAREN, new ArrayList<>());

                // add_op is in follow(multiplication),
                // won't push anything new
                put(Token.ADDITIVE_OP, new ArrayList<>()); // ?????
            }});

            put(Token.FACTOR, new HashMap<>() {{

                // factor -> identifier
                put(Token.IDENTIFIER, new ArrayList<>() {{
                    add(Token.IDENTIFIER);
                }});

                // factor -> left_paren expression right_paren
                put(Token.LEFT_PAREN, new ArrayList<>() {{
                    add(Token.LEFT_PAREN);
                    add(Token.EXPRESSION);
                    add(Token.RIGHT_PAREN);
                }});

                // factor -> number
                put(Token.NUMBER, new ArrayList<>() {{
                    add(Token.NUMBER);
                }});

                // factor -> signed_term
                put(Token.ADDITIVE_OP, new ArrayList<>() {{
                    add(Token.SIGNED_TERM);
                }});

                // end_of_input is in follow(factor),
                // won't push anything new
                put(Token.END_OF_INPUT, new ArrayList<>());

                // else is in follow(factor),
                // won't push anything new
                put(Token.ELSE, new ArrayList<>());

                // do is in follow(factor),
                // won't push anything new
                put(Token.DO, new ArrayList<>());

                // end is in follow(factor),
                // won't push anything new
                put(Token.END, new ArrayList<>());

                // statement_sep is in follow(factor),
                // won't push anything new
                put(Token.STATEMENT_SEP, new ArrayList<>());

                // relational_op is in follow(factor),
                // won't push anything new
                put(Token.RELATIONAL_OP, new ArrayList<>());

                // then is in follow(factor),
                // won't push anything new
                put(Token.THEN, new ArrayList<>());

                // right_paren is in follow(factor),
                // won't push anything new
                put(Token.RIGHT_PAREN, new ArrayList<>());

                // multiplicative_op is in follow(factor),
                // won't push anything new
                put(Token.MULTIPLICATIVE_OP, new ArrayList<>());
            }});

            put(Token.SIGNED_TERM, new HashMap<>() {{

                put(Token.ADDITIVE_OP, new ArrayList<>() {{
                    add(Token.ADDITIVE_OP);
                    add(Token.TERM);
                }});
            }});
        }};
    }
}

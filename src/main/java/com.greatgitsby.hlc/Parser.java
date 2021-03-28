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

            try {
                getTopOfParseStack().doTheThing(this);
            } catch (SyntaxErrorException e) {
                isValidSyntax = false;
            }
        }

        if (getLexicalAnalyzer().hasNextLexeme()) {
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

                put(Token.IDENTIFIER, new LinkedList<>() {{
                    add(Token.IDENTIFIER);
                    add(Token.ASSIGNMENT_OP);
                    add(Token.EXPRESSION);
                }});

                put(Token.IF, new LinkedList<>() {{
                    add(Token.IF);
                    add(Token.BOOLEAN_EXPRESSION);
                    add(Token.THEN);
                    add(Token.STATEMENT);
                    add(Token.ELSE_CLAUSE);
                }});

                put(Token.WHILE, new LinkedList<>() {{
                    add(Token.WHILE);
                    add(Token.BOOLEAN_EXPRESSION);
                    add(Token.DO);
                    add(Token.STATEMENT);
                }});

                put(Token.PRINT, new LinkedList<>() {{
                    add(Token.PRINT);
                    add(Token.PRINT_EXPRESSION);
                }});

                put(Token.BEGIN, new LinkedList<>() {{
                    add(Token.BEGIN);
                    add(Token.STATEMENT_LIST);
                    add(Token.END);
                }});

                put(Token.VARIABLE, new LinkedList<>() {{
                    add(Token.VARIABLE);
                    add(Token.IDENTIFIER);
                }});

//                put(Token.ELSE, new LinkedList<>());
//
//                put(Token.STATEMENT_SEP, new LinkedList<>());
//
//                put(Token.END_OF_INPUT, new LinkedList<>());
//
//                put(Token.END, new LinkedList<>());
            }});

            put(Token.ELSE_CLAUSE, new HashMap<>() {{

                put(Token.ELSE, new LinkedList<>() {{
                    add(Token.ELSE);
                    add(Token.STATEMENT);
                }});
            }});

            put(Token.STATEMENT_LIST, new HashMap<>() {{

                put(Token.IDENTIFIER, new LinkedList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                put(Token.IF, new LinkedList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                put(Token.WHILE, new LinkedList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                put(Token.PRINT, new LinkedList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                put(Token.BEGIN, new LinkedList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                put(Token.VARIABLE, new LinkedList<>() {{
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                put(Token.END, new LinkedList<>());
            }});

            put(Token.SEPARATED_LIST, new HashMap<>() {{

                put(Token.STATEMENT_SEP, new LinkedList<>() {{
                    add(Token.STATEMENT_SEP);
                    add(Token.STATEMENT);
                    add(Token.SEPARATED_LIST);
                }});

                put(Token.END, new LinkedList<>());
            }});

            put(Token.PRINT_EXPRESSION, new HashMap<>() {{

                put(Token.IDENTIFIER, new LinkedList<>() {{
                    add(Token.EXPRESSION);
                }});

                put(Token.LEFT_PAREN, new LinkedList<>() {{
                    add(Token.EXPRESSION);
                }});

                put(Token.NUMBER, new LinkedList<>() {{
                    add(Token.EXPRESSION);
                }});

                put(Token.ADDITIVE_OP, new LinkedList<>() {{
                    add(Token.EXPRESSION);
                }});

                put(Token.STRING_CONST, new LinkedList<>() {{
                    add(Token.STRING_CONST);
                }});
            }});

            put(Token.BOOLEAN_EXPRESSION, new HashMap<>() {{

                put(Token.IDENTIFIER, new LinkedList<>() {{
                    add(Token.EXPRESSION);
                    add(Token.RELATIONAL_OP);
                    add(Token.EXPRESSION);
                }});

                put(Token.LEFT_PAREN, new LinkedList<>() {{
                    add(Token.EXPRESSION);
                    add(Token.RELATIONAL_OP);
                    add(Token.EXPRESSION);
                }});

                put(Token.NUMBER, new LinkedList<>() {{
                    add(Token.EXPRESSION);
                    add(Token.RELATIONAL_OP);
                    add(Token.EXPRESSION);
                }});

                put(Token.ADDITIVE_OP, new LinkedList<>() {{
                    add(Token.EXPRESSION);
                    add(Token.RELATIONAL_OP);
                    add(Token.EXPRESSION);
                }});
            }});

            put(Token.EXPRESSION, new HashMap<>() {{

                put(Token.IDENTIFIER, new LinkedList<>() {{
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});

                put(Token.LEFT_PAREN, new LinkedList<>() {{
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});

                put(Token.NUMBER, new LinkedList<>() {{
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});

                put(Token.ADDITIVE_OP, new LinkedList<>() {{
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});
            }});

            put(Token.ADDITION, new HashMap<>() {{

                put(Token.ADDITIVE_OP, new LinkedList<>() {{
                    add(Token.ADDITIVE_OP);
                    add(Token.TERM);
                    add(Token.ADDITION);
                }});
            }});

            put(Token.TERM, new HashMap<>() {{

                put(Token.IDENTIFIER, new LinkedList<>() {{
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});

                put(Token.LEFT_PAREN, new LinkedList<>() {{
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});

                put(Token.NUMBER, new LinkedList<>() {{
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});

                put(Token.ADDITIVE_OP, new LinkedList<>() {{
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});
            }});

            put(Token.MULTIPLICATION, new HashMap<>() {{

                put(Token.MULTIPLICATIVE_OP, new LinkedList<>() {{
                    add(Token.MULTIPLICATIVE_OP);
                    add(Token.FACTOR);
                    add(Token.MULTIPLICATION);
                }});
            }});

            put(Token.FACTOR, new HashMap<>() {{

                put(Token.IDENTIFIER, new LinkedList<>() {{
                    add(Token.IDENTIFIER);
                }});

                put(Token.LEFT_PAREN, new LinkedList<>() {{
                    add(Token.LEFT_PAREN);
                    add(Token.EXPRESSION);
                    add(Token.RIGHT_PAREN);
                }});

                put(Token.NUMBER, new LinkedList<>() {{
                    add(Token.NUMBER);
                }});

                put(Token.ADDITIVE_OP, new LinkedList<>() {{
                    add(Token.SIGNED_TERM);
                }});
            }});

            put(Token.SIGNED_TERM, new HashMap<>() {{

                put(Token.ADDITIVE_OP, new LinkedList<>() {{
                    add(Token.ADDITIVE_OP);
                    add(Token.TERM);
                }});
            }});
        }};
    }
}

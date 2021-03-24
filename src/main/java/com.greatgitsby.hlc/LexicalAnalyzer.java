package com.greatgitsby.hlc;

import java.io.*;
import java.util.*;

/**
 * LexicalAnalyzer
 *
 * Performs the lexical analysis of an input file as conforming
 * to the Hansen programming language. It is a table-driven
 * finite state automata backed by a symbol table that remembers
 * variables from the input file and reserved keywords of the
 * Hansen language. Once complete, a queue of Symbols is left
 * in the _symbols deque for the programmer to utilize
 * (in preparation of passing to a Parser...)
 */
public class LexicalAnalyzer implements Iterable<Symbol> {
    private final ArrayDeque<Symbol> _symbolQueue;
    private final HashMap<State, HashMap<Character, State>> _stateTable;
    private final HashMap<String, Symbol> _symbolTable;

    // Static variables
    private static final char CARRIAGE_RETURN = '\r';
    private static final char END_COMMENT = '}';
    private static final char END_STRING_CONST = '"';
    private static final int  EOF = -1;
    private static final char DIGIT = '0';
    private static final char LETTER = 'a';
    private static final char NEWLINE = '\n';
    private static final char TAB = '\t';

    /**
     * Constructs a new Lexical Analyzer, parsing an input file
     * into a set of Symbols
     *
     * @param filepath the path to the input file
     * @throws IOException if the file does not exist or a read error occurs
     * @throws IllegalArgumentException if the filepath is null
     * @throws SyntaxErrorException if the input file
     */
    public LexicalAnalyzer(String filepath)
        throws IOException, IllegalArgumentException, SyntaxErrorException
    {
        _symbolQueue = new ArrayDeque<>();
        _stateTable = new HashMap<>();

        // Insert all of our reserved keywords as "symbols" in
        // our symbol table
        _symbolTable = new HashMap<>() {{
            put("variable", Token.VARIABLE);
            put("print", Token.PRINT);
            put("if", Token.IF);
            put("then", Token.THEN);
            put("else", Token.ELSE);
            put("while", Token.WHILE);
            put("do", Token.DO);
            put("begin", Token.BEGIN);
            put("end", Token.END);
            put("+", Token.ADDITIVE_OP);
            put("-", Token.ADDITIVE_OP);
            put("<", Token.RELATIONAL_OP);
            put("<=", Token.RELATIONAL_OP);
            put("<>", Token.RELATIONAL_OP);
            put("=", Token.RELATIONAL_OP);
            put(">", Token.RELATIONAL_OP);
            put(">=", Token.RELATIONAL_OP);
            put("*", Token.MULTIPLICATIVE_OP);
            put("/", Token.MULTIPLICATIVE_OP);
            put(":=", Token.ASSIGNMENT_OP);
            put(";", Token.STATEMENT_SEP);
            put("(", Token.LEFT_PAREN);
            put(")", Token.RIGHT_PAREN);
        }};

        // START state map
        HashMap<Character, State> startMap = new HashMap<>();

        // NUMBER state map
        HashMap<Character, State> numberMap = new HashMap<>();

        // SYMBOL state machine map
        HashMap<Character, State> symbolMap = new HashMap<>();

        // GREATER_THAN state map
        HashMap<Character, State> greaterThanMap = new HashMap<>();

        // LESS_THAN state map
        HashMap<Character, State> lessThanMap = new HashMap<>();

        // COLON state map
        HashMap<Character, State> colonMap = new HashMap<>();

        // START machine states
        startMap.put(' ', State.WHITESPACE);
        startMap.put(CARRIAGE_RETURN, State.WHITESPACE);
        startMap.put(NEWLINE, State.WHITESPACE);
        startMap.put(TAB, State.WHITESPACE);
        startMap.put(LETTER, State.SYMBOL);
        startMap.put(DIGIT, State.NUMBER);
        startMap.put('+', State.ADDITIVE_OP);
        startMap.put('-', State.ADDITIVE_OP);
        startMap.put('*', State.MULTIPLICATIVE_OP);
        startMap.put('/', State.MULTIPLICATIVE_OP);
        startMap.put('(', State.LEFT_PAREN);
        startMap.put(')', State.RIGHT_PAREN);
        startMap.put(';', State.STATEMENT_SEP);
        startMap.put('=', State.EQUAL_TO);
        startMap.put('>', State.GREATER_THAN);
        startMap.put('<', State.LESS_THAN);
        startMap.put('{', State.IN_COMMENT);
        startMap.put('"', State.IN_STRING);
        startMap.put(':', State.COLON);

        // Insert START state machine
        getStateTable().put(State.START, startMap);

        // SYMBOL machine states
        symbolMap.put(LETTER, State.SYMBOL);
        symbolMap.put(DIGIT, State.SYMBOL);
        symbolMap.put('_', State.SYMBOL);

        // Insert SYMBOL state machine
        getStateTable().put(State.SYMBOL, symbolMap);

        // NUMBER machine states
        numberMap.put(DIGIT, State.NUMBER);

        // Insert NUMBER state machine
        getStateTable().put(State.NUMBER, numberMap);

        // GREATER_THAN machine states
        greaterThanMap.put('=', State.GREATER_THAN_EQUAL_TO);

        // Insert GREATER_THAN state machine into state table
        getStateTable().put(State.GREATER_THAN, greaterThanMap);

        // Insert GREATER_THAN_EQUAL_TO state machine (no transitions other
        // than START)
        getStateTable().put(State.GREATER_THAN_EQUAL_TO, new HashMap<>());

        // LESS_THAN machine states
        lessThanMap.put('>', State.NOT_EQUAL_TO);
        lessThanMap.put('=', State.LESS_THAN_EQUAL_TO);

        // Insert LESS_THAN state machine into state table
        getStateTable().put(State.LESS_THAN, lessThanMap);

        // COLON machine states
        colonMap.put('=', State.ASSIGNMENT_OP);

        // Insert COLON state map into the state table
        getStateTable().put(State.COLON, colonMap);

        // Insert LESS_THAN_EQUAL_TO state machine (no transitions other than
        // START)
        getStateTable().put(State.LESS_THAN_EQUAL_TO, new HashMap<>());

        // Insert NOT_EQUAL_TO state machine (no transitions other than START)
        getStateTable().put(State.NOT_EQUAL_TO, new HashMap<>());

        // Insert ADDITIVE_OP state machine (no transitions other than START)
        getStateTable().put(State.ADDITIVE_OP, new HashMap<>());

        // Insert MULTIPLICATIVE_OP state machine (no transitions other than
        // START)
        getStateTable().put(State.MULTIPLICATIVE_OP, new HashMap<>());

        // Insert WHITESPACE state machine (no transitions other than START)
        getStateTable().put(State.WHITESPACE, new HashMap<>());

        // Insert LEFT_PAREN state machine (no transitions other than START)
        getStateTable().put(State.LEFT_PAREN, new HashMap<>());

        // Insert RIGHT_PAREN state machine (no transitions other than START)
        getStateTable().put(State.RIGHT_PAREN, new HashMap<>());

        // Insert STATEMENT_SEP state machine (no transitions other than START)
        getStateTable().put(State.STATEMENT_SEP, new HashMap<>());

        // Insert STATEMENT_SEP state machine (no transitions other than START)
        getStateTable().put(State.IN_COMMENT, new HashMap<>());

        // Insert STATEMENT_SEP state machine (no transitions other than START)
        getStateTable().put(State.COMMENT, new HashMap<>());

        // Insert STATEMENT_SEP state machine (no transitions other than START)
        getStateTable().put(State.IN_STRING, new HashMap<>());

        // Insert STATEMENT_SEP state machine (no transitions other than START)
        getStateTable().put(State.STRING_CONST, new HashMap<>());

        // Insert ASSIGNMENT_OP state machine (no transitions other than START)
        getStateTable().put(State.ASSIGNMENT_OP, new HashMap<>());

        // Analyze the input file and prepare the set of symbols
        // for processing
        this.analyze(filepath);
    }

    /**
     * Analyze a file and generate a set of representative Symbols
     *
     * @param filepath the file to process
     * @throws IOException if the file does not exist or a read error occurs
     * @throws IllegalArgumentException if the filepath is null
     * @throws SyntaxErrorException if the input file has a syntax error
     */
    private void analyze(String filepath)
        throws IOException, IllegalArgumentException, SyntaxErrorException
    {
        // Variable declarations
        File theFile;
        PushbackReader theReader;
        State currentState;
        String lexeme;
        StringBuilder lexemeValue;
        Symbol theSymbol;
        int lineNumber;
        int charNumber;
        int theChar;
        char normalizedChar;

        boolean running = true;

        // Initialize lexeme
        lexemeValue = new StringBuilder();

        // Move state machine to START state
        currentState = State.START;

        // Initialize the line and character locations at 1, 1
        lineNumber = 1;
        charNumber = 1;

        // We can't have a null filepath
        if (filepath == null) {
            throw new IllegalArgumentException("File path must be not null");
        }

        // Create file and file reader
        theFile = new File(filepath);
        theReader = new PushbackReader(
            new FileReader(theFile.getAbsoluteFile())
        );

        // Process each character
        while (running) {
            // Read in the next character from the reader
            theChar = theReader.read();

            // If the read-in char is EOF, we should stop running
            running = theChar != EOF;

            // Copy the read-in char so we can normalize it
            // if it is alphanumeric
            normalizedChar = (char) theChar;

            // Normalize letters and numbers for the
            // state table
            if (Character.isDigit(normalizedChar)) {
                normalizedChar = DIGIT;
            } else if (Character.isLetter(normalizedChar)) {
                normalizedChar = LETTER;
            }

            // Handle the IN_COMMENT and IN_STRING states, consume all
            // characters until we hit either of the characters that
            // signal the end of a comment or a string const
            if (
                currentState == State.IN_COMMENT ||
                currentState == State.IN_STRING
            ) {
                // Add the incoming character to the lexeme
                lexemeValue.append(Character.toString(theChar));
                charNumber++;

                // If we have hit the END_COMMENT character,
                // it is time to move to the COMMENT state
                if (normalizedChar == END_COMMENT) {
                    currentState = State.COMMENT;
                }

                // If we have hit the END_STRING_CONST character,
                // it is time to move to the STRING_CONST state
                if (normalizedChar == END_STRING_CONST) {
                    currentState = State.STRING_CONST;
                }
            }
            // Check if there is a next state, if not, we've hit a dead end
            // and need to determine next steps. is this state accepting?
            // If it isn't, we certainly have read a syntactically
            // incorrect line of code
            else if (
                getStateTable().containsKey(currentState) &&
                getStateTable().get(currentState).containsKey(normalizedChar)
            ) {
                // Has next state, get it!
                currentState = getStateTable()
                    .get(currentState)
                    .get(normalizedChar);

                // Add the character to the lexeme
                lexemeValue.append(Character.toString(theChar));
                charNumber++;
            } else if (currentState.isAccepting()) {
                // Push the last character back onto the pushback
                // reader
                theReader.unread(theChar);

                // If the character is a new line, "back up
                // to the previous line"
                if (theChar == NEWLINE) {
                    lineNumber--;
                }

                // Get the current value of the lexeme
                lexeme = lexemeValue.toString();

                // Determine what identifier
                theSymbol = getSymbolTable().get(lexeme);

                // If the symbol was not in the symbol table, it is an
                // identifier and should be populated into the table
                if (theSymbol == null) {
                    // If we're in the SYMBOL state, this must have been
                    // an unknown symbol, so we can infer it to be a
                    // new identifier
                    if (currentState == State.SYMBOL) {
                        theSymbol = new Lexeme(lexeme, Token.IDENTIFIER);
                    }
                    // It's whitespace if we were in the WHITESPACE state
                    else if (currentState == State.WHITESPACE) {
                        theSymbol = new Lexeme(lexeme, Token.WHITESPACE);
                    }
                    // It's a comment if we were in the COMMENT state
                    else if (currentState == State.COMMENT) {
                        theSymbol = new Lexeme(lexeme, Token.COMMENT);
                    }
                    // It's a STRING_CONST if we were in the STRING_CONST
                    // state
                    else if (currentState == State.STRING_CONST) {
                        theSymbol = new Lexeme(lexeme, Token.STRING_CONST);
                    }
                    // It's a NUMBER if we were in the NUMBER state
                    else if (currentState == State.NUMBER) {
                        theSymbol = new Lexeme(lexeme, Token.NUMBER);
                    }

                    // Insert the new symbol into the symbol table
                    getSymbolTable().put(lexeme, theSymbol);
                }

                // Don't add whitespace or comments to symbol stack
                if (
                    currentState != State.WHITESPACE &&
                    currentState != State.COMMENT
                ) {
                    // Add lexeme to the "stream"
                    queueSymbol(theSymbol);
                }

                // Clear old StringBuilder value
                lexemeValue = new StringBuilder();

                // Go back to START state
                currentState = State.START;
            } else {
                // Invalid syntax, print location of error
                throw new SyntaxErrorException(
                    String.format(
                        "Line %d, Char %d: Invalid Syntax",
                        lineNumber, charNumber
                    )
                );
            }

            // "Go to next line"
            if (normalizedChar == NEWLINE) {
                lineNumber++;
                charNumber = 1;
            }
        }

        // If we hit EOF and are still IN_STRING or IN_COMMENT,
        // then the syntax was incorrect -- either a string
        // or a comment was not closed
        if (currentState == State.IN_STRING) {
            throw new SyntaxErrorException("String not closed");
        } else if (currentState == State.IN_COMMENT) {
            throw new SyntaxErrorException("Comment not closed");
        }

        // Push the final END_OF_INPUT char onto the stack
        // for our end state
        queueSymbol(Token.END_OF_INPUT);
    }

    /**
     * Get the Lexical Analyzer's symbol table
     *
     * @return the symbol table
     */
    public HashMap<String, Symbol> getSymbolTable() {
        return _symbolTable;
    }

    /**
     * Determines if the Lexical Analyzer has another lexeme to return
     *
     * @return true if there is another lexeme to return
     */
    public boolean hasNextLexeme() {
        return !_symbolQueue.isEmpty();
    }

    /**
     * Provides the next lexeme in the stream if there is another
     * to return
     *
     * @return the next lexeme
     * @throws NoSuchElementException if there are no more lexemes to return
     */
     public Symbol nextLexeme() throws NoSuchElementException {
        return _symbolQueue.pop();
    }

    /**
     * Returns the stack of symbols that the lexical analyzer formulates
     *
     * @return the stack of symbols that the lexical analyzer formulates
     */
    public ArrayDeque<Symbol> getSymbolStack() {
         return _symbolQueue;
    }

    /**
     * Gets the Lexical Analyzer's state table and returns it
     *
     * @return the state table
     */
    private HashMap<State, HashMap<Character, State>> getStateTable() {
        return _stateTable;
    }

    /**
     * Add a new symbol into the "stream" of tokens
     * (can be a Lexeme or a Token)
     *
     * @param symbolToAdd the symbol to add
     */
    private void queueSymbol(Symbol symbolToAdd) {
        _symbolQueue.add(symbolToAdd);
    }

    @Override
    public Iterator<Symbol> iterator() {
        return _symbolQueue.iterator();
    }
}

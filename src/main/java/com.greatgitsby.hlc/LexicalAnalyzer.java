package com.greatgitsby.hlc;

import java.io.*;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * LexicalAnalyzer
 *
 * Analyzes a file lexically
 * TODO Add description
 */
public class LexicalAnalyzer {
    private final ArrayDeque<Symbol> _lexemes;
    private final HashMap<String, Symbol> _symbolTable;
    private final HashMap<State, HashMap<Character, State>> _stateTable;

    private static final char DIGIT = '0';
    private static final char LETTER = 'a';
    private static final char END_COMMENT = '}';
    private static final char END_STRING_CONST = '"';

    public LexicalAnalyzer() {
        _lexemes = new ArrayDeque<>();
        _symbolTable = new HashMap<>();
        _stateTable = new HashMap<>();

        getSymbolTable().put("variable", Token.VARIABLE);
        getSymbolTable().put("print", Token.PRINT);
        getSymbolTable().put("if", Token.IF);
        getSymbolTable().put("then", Token.THEN);
        getSymbolTable().put("else", Token.ELSE);
        getSymbolTable().put("while", Token.WHILE);
        getSymbolTable().put("do", Token.DO);
        getSymbolTable().put("begin", Token.BEGIN);
        getSymbolTable().put("end", Token.END);
        getSymbolTable().put("+", Token.ADDITIVE_OP);
        getSymbolTable().put("-", Token.ADDITIVE_OP);
        getSymbolTable().put("<", Token.RELATIONAL_OP);
        getSymbolTable().put("<=", Token.RELATIONAL_OP);
        getSymbolTable().put("<>", Token.RELATIONAL_OP);
        getSymbolTable().put("=", Token.RELATIONAL_OP);
        getSymbolTable().put(">", Token.RELATIONAL_OP);
        getSymbolTable().put(">=", Token.RELATIONAL_OP);
        getSymbolTable().put("*", Token.MULTIPLICATIVE_OP);
        getSymbolTable().put("/", Token.MULTIPLICATIVE_OP);
        getSymbolTable().put(":=", Token.ASSIGNMENT_OP);
        getSymbolTable().put(";", Token.STATEMENT_SEP);
        getSymbolTable().put("(", Token.LEFT_PAREN);
        getSymbolTable().put(")", Token.RIGHT_PAREN);

        // START state map
        HashMap<Character, State> startMap = new HashMap<>();

        // START machine states
        startMap.put(' ', State.WHITESPACE);
        startMap.put('\n', State.WHITESPACE);
        startMap.put('\t', State.WHITESPACE);
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

        // SYMBOL state machine map
        HashMap<Character, State> symbolMap = new HashMap<>();

        // SYMBOL machine states
        symbolMap.put(LETTER, State.SYMBOL);
        symbolMap.put(DIGIT, State.SYMBOL);
        symbolMap.put('_', State.SYMBOL);

        // Insert SYMBOL state machine
        getStateTable().put(State.SYMBOL, symbolMap);

        // NUMBER state map
        HashMap<Character, State> numberMap = new HashMap<>();

        // NUMBER machine states
        numberMap.put(DIGIT, State.NUMBER);

        // Insert NUMBER state machine
        getStateTable().put(State.NUMBER, numberMap);

        // GREATER_THAN state map
        HashMap<Character, State> greaterThanMap = new HashMap<>();

        // GREATER_THAN machine states
        greaterThanMap.put('=', State.GREATER_THAN_EQUAL_TO);

        // Insert GREATER_THAN state machine into state table
        getStateTable().put(State.GREATER_THAN, greaterThanMap);

        // Insert GREATER_THAN_EQUAL_TO state machine (no transitions other than START)
        getStateTable().put(State.GREATER_THAN_EQUAL_TO, new HashMap<>());

        // LESS_THAN state map
        HashMap<Character, State> lessThanMap = new HashMap<>();

        // LESS_THAN machine states
        lessThanMap.put('>', State.NOT_EQUAL_TO);
        lessThanMap.put('=', State.LESS_THAN_EQUAL_TO);

        // Insert LESS_THAN state machine into state table
        getStateTable().put(State.LESS_THAN, lessThanMap);

        // Insert LESS_THAN_EQUAL_TO state machine (no transitions other than START)
        getStateTable().put(State.LESS_THAN_EQUAL_TO, new HashMap<>());

        // Insert NOT_EQUAL_TO state machine (no transitions other than START)
        getStateTable().put(State.NOT_EQUAL_TO, new HashMap<>());

        // Insert ADDITIVE_OP state machine (no transitions other than START)
        getStateTable().put(State.ADDITIVE_OP, new HashMap<>());

        // Insert MULTIPLICATIVE_OP state machine (no transitions other than START)
        getStateTable().put(State.MULTIPLICATIVE_OP, new HashMap<>());

        // Insert WHITESPACE state machine (no transitions other than START)
        getStateTable().put(State.WHITESPACE, new HashMap<>());

        // Insert LEFT_PAREN state machine (no transitions other than START)
        getStateTable().put(State.LEFT_PAREN, new HashMap<>());

        // Insert RIGHT_PAREN state machine (no transitions other than START)
        getStateTable().put(State.RIGHT_PAREN, new HashMap<>());

        // Insert STATEMENT_SEP state machine (no transitions other than START)
        getStateTable().put(State.STATEMENT_SEP, new HashMap<>());

        getStateTable().put(State.IN_COMMENT, new HashMap<>());
        getStateTable().put(State.COMMENT, new HashMap<>());

        getStateTable().put(State.IN_STRING, new HashMap<>());
        getStateTable().put(State.STRING_CONST, new HashMap<>());

        // COLON state map
        HashMap<Character, State> colonMap = new HashMap<>();

        colonMap.put('=', State.ASSIGNMENT_OP);

        getStateTable().put(State.COLON, colonMap);

        getStateTable().put(State.ASSIGNMENT_OP, new HashMap<>());
    }

    public void analyze(String filename) throws Exception {
        int theChar;
        PushbackReader theReader;
        File theFile;
        String lexeme;
        Symbol theSymbol;

        int lineNumber = 1;
        int charNumber = 1;

        State currentState = State.START;
        StringBuilder lexemeValue = new StringBuilder();

        if (filename == null) {
            throw new IllegalArgumentException("Filename must be not null");
        }

        theFile = new File(filename);
        theReader = new PushbackReader(new FileReader(theFile.getAbsoluteFile()));

        // Process each character
        while ((theChar = theReader.read()) != -1) {
            //
            char normalizedChar = (char) theChar;

            // Normal letters and numbers for the
            // state table
            if (Character.isLetter(normalizedChar)) {
                normalizedChar = LETTER;
            } else if (Character.isDigit(normalizedChar)) {
                normalizedChar = DIGIT;
            }

            // TODO determine if this works
            if (normalizedChar == '\n') {
                lineNumber++;
                charNumber = 1;
            }

            // check if there is a next state, if not, we've hit a dead end and
            // need to determine next steps. is this state accepting? if it isn't,
            // we certainly have read a syntactically incompatible line of code
            if (getStateTable().get(currentState).containsKey(normalizedChar)) {
                // has next state, get it!
                currentState = getStateTable().get(currentState).get(normalizedChar);
                lexemeValue.append(Character.toString(theChar));
                charNumber++;
            } else if (currentState == State.IN_COMMENT) {
                lexemeValue.append(Character.toString(theChar));
                charNumber++;

                if (normalizedChar == END_COMMENT) {
                    currentState = State.COMMENT;
                }
            } else if (currentState == State.IN_STRING) {
                lexemeValue.append(Character.toString(theChar));
                charNumber++;

                // If we have hit the END_STRING_CONST character,
                // it is time to move to the STRING_CONST state
                if (normalizedChar == END_STRING_CONST) {
                    currentState = State.STRING_CONST;
                }
            } else if (currentState.isAccepting()) {
                theReader.unread(theChar);

                // Get the current value of the lexeme
                lexeme = lexemeValue.toString();

                // Determine what identifier
                theSymbol = getSymbolTable().get(lexeme);

                // If the symbol was not in the symbol table, it is an identifier and
                // should be populated into the table
                if (theSymbol == null) {
                    if (currentState == State.SYMBOL) {
                        // If we're in the SYMBOL state, this must have been
                        // an unknown symbol, so we can infer it to be a new identifier
                        theSymbol = new Lexeme(lexeme, Token.IDENTIFIER);
                    } else if (currentState == State.WHITESPACE) {
                        // It's whitespace if we were in the WHITESPACE state
                        theSymbol = new Lexeme(lexeme, Token.WHITESPACE);
                    } else if (currentState == State.COMMENT) {
                        // It's a comment if we were in the COMMENT state
                        theSymbol = new Lexeme(lexeme, Token.COMMENT);
                    } else if (currentState == State.STRING_CONST) {
                        // It's a STRING_CONST if we were in the STRING_CONST state
                        theSymbol = new Lexeme(lexeme, Token.STRING_CONST);
                    } else if (currentState == State.NUMBER) {
                        // It's a NUMBER if we were in the NUMBER state
                        theSymbol = new Lexeme(lexeme, Token.NUMBER);
                    }

                    getSymbolTable().put(lexeme, theSymbol);
                }

                // Add lexeme to the "stream"
                addLexeme(theSymbol);

                // Clear old StringBuilder value
                lexemeValue = new StringBuilder();

                // Go back to START state
                currentState = State.START;
            } else {
                // error
                // TODO print line number and char number along with error state
                throw new Exception(String.format("Line %d, Char %d: Bad state, got %s", lineNumber, charNumber, (char) theChar));
            }
        }

        // Push the final END_OF_INPUT char onto the stack
        // for our end state
        addLexeme(Token.END_OF_INPUT);
    }

    /**
     * Gets the state table and returns it
     * @return the state table
     */
    private HashMap<State, HashMap<Character, State>> getStateTable() {
        return _stateTable;
    }

    private HashMap<String, Symbol> getSymbolTable() {
        return _symbolTable;
    }

    private void addLexeme(Symbol lexemeToAdd) {
        _lexemes.add(lexemeToAdd);
    }

    /**
     * Determines if the Lexical Analyzer has another lexeme to return
     *
     * @return true if there is another lexeme to return
     */
    public boolean hasNextLexeme() {
        return !_lexemes.isEmpty();
    }

    /**
     * Provides the next lexeme in the stream if there is another
     * to return
     *
     * @return the next lexeme
     * @throws NoSuchElementException if there are no more lexemes to return
     */
    public Symbol nextLexeme() throws NoSuchElementException {
        return _lexemes.remove();
    }
}

package com.greatgitsby.hlc;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * LexicalAnalyzer
 * <p>
 * Performs the lexical analysis of an input file as conforming
 * to the Hansen programming language. It is a table-driven
 * finite state automata backed by a symbol table that remembers
 * variables from the input file and reserved keywords of the
 * Hansen language
 */
public class LexicalAnalyzer {

    // Static variables
    private static final char CARRIAGE_RETURN  = '\r';
    private static final char END_COMMENT      = '}';
    private static final char END_STRING_CONST = '"';
    private static final int  EOF              = -1;
    private static final int  EMPTY_BUF        = 65535;
    private static final char DIGIT            = '0';
    private static final char LETTER           = 'a';
    private static final char NEWLINE          = '\n';
    private static final char TAB              = '\t';

    // Private immutable instance variables
    private final HashMap<State, HashMap<Character, State>> _stateTable;
    private final HashMap<String, TerminalToken>            _keywordTable;
    private final HashMap<String, Symbol>                   _symbolTable;
    private final LineNumberReader                          _fileReader;
    private final String                                    _filename;

    // Private mutable instance variables
    private State   _currentState;
    private int     _charNumber;
    private int     _lineNumber;
    private boolean _hasNextLexeme;
    private int     _currentChar;

    /**
     * Constructs a new Lexical Analyzer, parsing an input file
     * into a set of Symbols
     *
     * @param filepath the path to the input file
     * @throws IOException              if the file does not exist or a read
     *                                  error occurs
     * @throws IllegalArgumentException if the filepath is null
     * @throws SyntaxErrorException     if the input file is invalid
     */
    public LexicalAnalyzer(String filepath)
        throws IOException, IllegalArgumentException, SyntaxErrorException
    {
        File theFile;
        HashMap<Character, State> startMap;
        HashMap<Character, State> numberMap;
        HashMap<Character, State> symbolMap;
        HashMap<Character, State> lessThanMap;
        HashMap<Character, State> greaterThanMap;
        HashMap<Character, State> colonMap;

        // Initialize internal state
        _stateTable    = new HashMap<>();
        _symbolTable   = new HashMap<>();
        _currentState  = State.START;
        _charNumber    = 1;
        _lineNumber    = 1;
        _hasNextLexeme = true;

        theFile = new File(filepath).getAbsoluteFile();

        // Create file and file reader
        _fileReader = new LineNumberReader(
            new FileReader(theFile)
        );

        // Set line number to first line, 1
        _fileReader.setLineNumber(1);

        _filename = theFile.getName();

        // Read in the first character from the file reader
        _currentChar = _fileReader.read();

        // Insert all of our reserved keywords as "symbols" in
        // our symbol table
        _keywordTable = new HashMap<>() {{
            put("variable", TerminalToken.VARIABLE);
            put("print", TerminalToken.PRINT);
            put("if", TerminalToken.IF);
            put("then", TerminalToken.THEN);
            put("else", TerminalToken.ELSE);
            put("while", TerminalToken.WHILE);
            put("do", TerminalToken.DO);
            put("begin", TerminalToken.BEGIN);
            put("end", TerminalToken.END);
            put("+", TerminalToken.ADDITIVE_OP);
            put("-", TerminalToken.ADDITIVE_OP);
            put("<", TerminalToken.RELATIONAL_OP);
            put("<=", TerminalToken.RELATIONAL_OP);
            put("<>", TerminalToken.RELATIONAL_OP);
            put("=", TerminalToken.RELATIONAL_OP);
            put(">", TerminalToken.RELATIONAL_OP);
            put(">=", TerminalToken.RELATIONAL_OP);
            put("*", TerminalToken.MULTIPLICATIVE_OP);
            put("/", TerminalToken.MULTIPLICATIVE_OP);
            put(":=", TerminalToken.ASSIGNMENT_OP);
            put(";", TerminalToken.STATEMENT_SEP);
            put("(", TerminalToken.LEFT_PAREN);
            put(")", TerminalToken.RIGHT_PAREN);
        }};

        // START state map
        startMap = new HashMap<>();

        // NUMBER state map
        numberMap = new HashMap<>();

        // SYMBOL state machine map
        symbolMap = new HashMap<>();

        // GREATER_THAN state map
        greaterThanMap = new HashMap<>();

        // LESS_THAN state map
        lessThanMap = new HashMap<>();

        // COLON state map
        colonMap = new HashMap<>();

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
    }

    /**
     * Provides the next symbol if there is another to return
     *
     * @return the next symbol
     * @throws NoSuchElementException if there are no more lexemes to return
     */
    public Lexeme nextSymbol() throws CompilerException, IOException {
        // Local variables
        Lexeme theLexeme;
        StringBuilder lexemeValue;
        String lexeme;
        Symbol theSymbol;
        boolean hasAcquiredSymbol;
        char normalizedChar;

        // Initialize locals
        theLexeme         = null;
        lexemeValue       = new StringBuilder();
        hasAcquiredSymbol = false;

        // Loop, consuming characters until a valid lexeme
        // is formed (or an error occurs)
        while (!hasAcquiredSymbol) {

            // If the read-in char is EOF, we should stop running
            // Copy the read-in char so we can normalize it
            // if it is alphanumeric
            normalizedChar = (char) _currentChar;

            // Normalize letters and numbers for the
            // state table
            if (Character.isDigit(normalizedChar)) {
                normalizedChar = DIGIT;
            } else if (Character.isLetter(normalizedChar)) {
                normalizedChar = LETTER;
            }

            // Handle the IN_COMMENT state, consume all
            // characters until we hit the character that
            // signals the end of a comment
            if (_currentState == State.IN_COMMENT) {

                // Add the incoming character to the lexeme
                lexemeValue.append(Character.toString(_currentChar));

                // If we have hit the END_COMMENT character,
                // it is time to move to the COMMENT state
                if (normalizedChar == END_COMMENT) {
                    _currentState = State.COMMENT;
                }

                readNextCharacter();
            }
            // Handle the IN_STRING state, consume all
            // characters until we hit the character that
            // signals the end of a string
            else if (_currentState == State.IN_STRING) {

                // Add the incoming character to the lexeme
                lexemeValue.append(Character.toString(_currentChar));

                // If we have hit the END_STRING_CONST character,
                // it is time to move to the STRING_CONST state
                if (normalizedChar == END_STRING_CONST) {
                    _currentState = State.STRING_CONST;
                }

                readNextCharacter();
            }
            // Check if there is a next state, if not, we've hit a dead end
            // and need to determine next steps. is this state accepting?
            // If it isn't, we certainly have read a syntactically
            // incorrect line of code
            else if (
                getStateTable().containsKey(_currentState) &&
                getStateTable().get(_currentState).containsKey(normalizedChar)
            ) {

                // Has next state, get it!
                _currentState = getStateTable()
                    .get(_currentState)
                    .get(normalizedChar);

                // Add the character to the lexeme
                lexemeValue.append(Character.toString(_currentChar));

                readNextCharacter();
            }
            // State is an accepting state, formulate the new lexeme
            else if (_currentState.isAccepting()) {

                // Get the current value of the lexeme
                lexeme = lexemeValue.toString();

                // If the symbol was not in the symbol table, it is an
                // identifier and should be populated into the table
                if (!getKeywordTable().containsKey(lexeme)) {

                    // If we're in the SYMBOL state, this must have been
                    // an unknown symbol, so we can infer it to be a
                    // new identifier
                    if (_currentState == State.SYMBOL) {
                        theLexeme = new Lexeme(
                            lexeme,
                            TerminalToken.IDENTIFIER,
                            getLineNumber(),
                            getCharacterNumber() - lexeme.length()
                        );
                    }
                    // It's whitespace if we were in the WHITESPACE state
                    else if (_currentState == State.WHITESPACE) {
                        theLexeme = new Lexeme(
                            lexeme,
                            TerminalToken.WHITESPACE,
                            getLineNumber(),
                            getCharacterNumber() - lexeme.length()
                        );
                    }
                    // It's a comment if we were in the COMMENT state
                    else if (_currentState == State.COMMENT) {
                        theLexeme = new Lexeme(
                            lexeme,
                            TerminalToken.COMMENT,
                            getLineNumber(),
                            getCharacterNumber() - lexeme.length()
                        );
                    }
                    // It's a STRING_CONST if we were in the STRING_CONST
                    // state
                    else if (_currentState == State.STRING_CONST) {
                        theLexeme = new Lexeme(
                            lexeme,
                            TerminalToken.STRING_CONST,
                            getLineNumber(),
                            getCharacterNumber() - lexeme.length()
                        );
                    }
                    // It's a NUMBER if we were in the NUMBER state
                    else if (_currentState == State.NUMBER) {
                        theLexeme = new Lexeme(
                            lexeme,
                            TerminalToken.NUMBER,
                            getLineNumber(),
                            getCharacterNumber() - lexeme.length()
                        );
                    }

                    // Initialize as "not declared"
                    theSymbol = new Symbol(theLexeme);

                    // Add to symbol table if this is a SYMBOL
                    if (_currentState == State.SYMBOL) {
                        getSymbolTable().putIfAbsent(lexeme, theSymbol);
                    }
                } else {
                    theLexeme = new Lexeme(
                        lexeme,
                        getKeywordTable().get(lexeme)
                    );
                }

                // End the state transition loop if the tokenized
                // symbol is not a comment, string literal, or whitespace.
                // Otherwise, have the lexical analyzer throw this out
                // and continue to find the next lexeme
                if (
                    _currentState != State.COMMENT &&
                    _currentState != State.WHITESPACE
                ) {
                    hasAcquiredSymbol = true;
                }

                // Clear old StringBuilder value
                lexemeValue = new StringBuilder();

                // Go back to START state
                _currentState = State.START;
            }
            // We've exhausted the buffer, it is now time to
            // return to the program the END_OF_INPUT terminal
            else if (_currentChar == EOF || _currentChar == EMPTY_BUF) {

                // Return the EOI terminal
                theLexeme = new Lexeme(
                    TerminalToken.END_OF_INPUT.toString(),
                    TerminalToken.END_OF_INPUT
                );

                // No longer any more lexemes to consume from the file
                _hasNextLexeme = false;

                // Exit the loop
                hasAcquiredSymbol = true;

                readNextCharacter();
            } else {

                // Invalid syntax, print location of error
                throw new SyntaxErrorException(
                    String.format(
                        "Line %d Char %d - Invalid Syntax",
                        getLineNumber(), getCharacterNumber()
                    )
                );
            }
        }

        return theLexeme;
    }

    private void readNextCharacter() throws IOException {
        try {
            // Read the next character in
            _currentChar = _fileReader.read();

            // If we have moved to a new line, reset the
            // char counter
            if (_fileReader.getLineNumber() != _lineNumber) {
                _lineNumber = _fileReader.getLineNumber();
                _charNumber = 0;
            }

            // Increment the character counter
            _charNumber++;
        }
        // If there is an issue reading, throw an exception
        catch (IOException e) {
            throw new IOException(
                String.format(
                    "Line %d Char %d - I/O Error - %s",
                    getLineNumber(),
                    getCharacterNumber(),
                    e.getMessage()
                )
            );
        }
    }

    /**
     * Get the Lexical Analyzer's keyword table
     *
     * @return the symbol table
     */
    private HashMap<String, TerminalToken> getKeywordTable() {
        return _keywordTable;
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
     * Get the file name associated with the Lexical Analyzer
     *
     * @return the file name
     */
    public String getFileName() {
        return _filename;
    }

    /**
     * Determines if the Lexical Analyzer has another lexeme to return
     *
     * @return true if there is another lexeme to return
     */
    public boolean hasNextSymbol() {
        return _hasNextLexeme;
    }

    /**
     * Returns the line number the lexical analyzer is currently on
     *
     * @return the line number the lexical analyzer is currently on
     */
    public int getLineNumber() {
        return _fileReader.getLineNumber();
    }

    /**
     * Returns the character number the lexical analyzer is currently on
     *
     * @return the character number the lexical analyzer is currently on
     */
    public int getCharacterNumber() {
        return _charNumber;
    }

    /**
     * Gets the Lexical Analyzer's state table and returns it
     *
     * @return the state table
     */
    private HashMap<State, HashMap<Character, State>> getStateTable() {
        return _stateTable;
    }
}

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
 * Hansen language
 */
public class LexicalAnalyzer {

    // Private immutable instance variables
    private final HashMap<State, HashMap<Character, State>> _stateTable;
    private final HashMap<String, Symbol> _symbolTable;
    private final PushbackReader _fileReader;

    // Private mutable instance variables
    private State _currentState;
    private int _lineNumber;
    private int _charNumber;
    private boolean _hasNextLexeme;
    private int _currentChar;

    // Static variables
    private static final char CARRIAGE_RETURN   = '\r';
    private static final char END_COMMENT       = '}';
    private static final char END_STRING_CONST  = '"';
    private static final int  EOF               = -1;
    private static final int  EMPTY_BUF         = 65535;
    private static final char DIGIT             = '0';
    private static final char LETTER            = 'a';
    private static final char NEWLINE           = '\n';
    private static final char TAB               = '\t';

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
        // Initialize internal state
        _stateTable = new HashMap<>();
        _currentState = State.START;
        _lineNumber = 0;
        _charNumber = 0;
        _hasNextLexeme = true;

        // Create file and file reader
        _fileReader = new PushbackReader(
            new FileReader(new File(filepath).getAbsoluteFile())
        );

        // Read in the first character from the file reader
        _currentChar = _fileReader.read();

        // Insert all of our reserved keywords as "symbols" in
        // our symbol table
        _symbolTable = new HashMap<>() {{
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
    }

    /**
     * Provides the next symbol if there is another to return
     *
     * @return the next symbol
     * @throws NoSuchElementException if there are no more lexemes to return
     */
     public Symbol nextSymbol() throws SyntaxErrorException {
         // Local variables
         Symbol theSymbol;
         StringBuilder lexemeValue;
         String lexeme;
         boolean hasAcquiredSymbol;
         char normalizedChar;

         // Initialize locals
         theSymbol = null;
         lexemeValue = new StringBuilder();
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
                 _charNumber++;

                 // If we have hit the END_COMMENT character,
                 // it is time to move to the COMMENT state
                 if (normalizedChar == END_COMMENT) {
                     _currentState = State.COMMENT;
                 }
             } else if (_currentState == State.IN_STRING) {

                 // Add the incoming character to the lexeme
                 lexemeValue.append(Character.toString(_currentChar));
                 _charNumber++;

                 // If we have hit the END_STRING_CONST character,
                 // it is time to move to the STRING_CONST state
                 if (normalizedChar == END_STRING_CONST) {
                     _currentState = State.STRING_CONST;
                 }
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
                 _charNumber++;
             }
             // State is an accepting state, formulate the new lexeme
             else if (_currentState.isAccepting()) {

                 // Push the last character back onto the pushback
                 // reader
                 try {
                     _fileReader.unread(_currentChar);
                 } catch (IOException e) {
                     throw new SyntaxErrorException(
                         String.format(
                             "Line %d Char %d - I/O Error",
                             getLineNumber(),
                             getCharacterNumber()
                         )
                     );
                 }

                 // If the character is a new line, "back up
                 // to the previous line"
                 if (_currentChar == NEWLINE) {
                     _lineNumber--;
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
                     if (_currentState == State.SYMBOL) {
                         theSymbol = new Lexeme(
                             lexeme,
                             TerminalToken.IDENTIFIER
                         );
                     }
                     // It's whitespace if we were in the WHITESPACE state
                     else if (_currentState == State.WHITESPACE) {
                         theSymbol = new Lexeme(
                             lexeme,
                             TerminalToken.WHITESPACE
                         );
                     }
                     // It's a comment if we were in the COMMENT state
                     else if (_currentState == State.COMMENT) {
                         theSymbol = new Lexeme(
                             lexeme,
                             TerminalToken.COMMENT
                         );
                     }
                     // It's a STRING_CONST if we were in the STRING_CONST
                     // state
                     else if (_currentState == State.STRING_CONST) {
                         theSymbol = new Lexeme(
                             lexeme,
                             TerminalToken.STRING_CONST
                         );
                     }
                     // It's a NUMBER if we were in the NUMBER state
                     else if (_currentState == State.NUMBER) {
                         theSymbol = new Lexeme(
                             lexeme,
                             TerminalToken.NUMBER
                         );
                     }

                     // Insert the new symbol into the symbol table
                     getSymbolTable().putIfAbsent(lexeme, theSymbol);
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
                 theSymbol = TerminalToken.END_OF_INPUT;

                 // No longer any more lexemes to consume from the file
                 _hasNextLexeme = false;

                 // Exit the loop
                 hasAcquiredSymbol = true;
             } else {
                 // Invalid syntax, print location of error
                 throw new SyntaxErrorException(
                     String.format(
                         "Line %d, Char %d: Invalid Syntax",
                         _lineNumber, _charNumber
                     )
                 );
             }

             // "Go to next line"
             if (normalizedChar == NEWLINE) {
                 _lineNumber++;
                 _charNumber = 1;
             }

             // Read the next character in
             try {
                 _currentChar = _fileReader.read();
             } catch (IOException e) {
                 throw new SyntaxErrorException(
                     String.format(
                         "Line %d Char %d - I/O Error",
                         getLineNumber(),
                         getCharacterNumber()
                     )
                 );
             }
         }

         return theSymbol;
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
    public boolean hasNextSymbol() {
        return _hasNextLexeme;
    }

    /**
     * Returns the line number the lexical analyzer is currently on
     *
     * @return the line number the lexical analyzer is currently on
     */
    public int getLineNumber() {
        return _lineNumber;
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

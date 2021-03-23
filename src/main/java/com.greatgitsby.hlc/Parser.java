package com.greatgitsby.hlc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO Description
 */
public class Parser {
    private final LexicalAnalyzer _lexicalAnalyzer;
    private final Map<Symbol, Map<Symbol, List<Symbol>>> _parseTable;

    /**
     * Construct a new Parser
     */
    public Parser(LexicalAnalyzer lexer) {
        _lexicalAnalyzer = lexer;
        _parseTable = new HashMap<>();
    }

    /**
     * TODO Description
     */
    public boolean isValidSyntax() {
        return false;
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
}

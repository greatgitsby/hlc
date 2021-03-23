package com.greatgitsby.hlc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO Description
 */
public class Parser {
    private final LexicalAnalyzer _lexer;
    private final Map<Symbol, Map<Symbol, List<Symbol>>> _parseTable;

    /**
     * TODO Description
     */
    public Parser(LexicalAnalyzer lexer) {
        _lexer = lexer;
        _parseTable = new HashMap<>();
    }

    /**
     * TODO Description
     */
    public boolean isValidSyntax() {
        return false;
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

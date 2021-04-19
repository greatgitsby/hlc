package com.greatgitsby.hlc;

import java.io.IOException;

/**
 * Token
 *
 * This interface guarantees the presence of a defined behavior
 * depending on the implementing class
 */
public interface Token {

    /**
     * Perform the behavior associated with a Token
     */
    void doTheThing(Parser theParser) throws CompilerException, IOException;
}

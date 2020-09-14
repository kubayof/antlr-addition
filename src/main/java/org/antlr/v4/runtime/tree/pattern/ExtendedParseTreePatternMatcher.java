package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class ExtendedParseTreePatternMatcher extends ParseTreePatternMatcher {
    protected final Parser parser;
    protected final Lexer lexer;
    private int lexerMode = 0;
    public ExtendedParseTreePatternMatcher(Lexer lexer, Parser parser) {
        super(lexer, parser);
        this.parser = parser;
        this.lexer = lexer;
    }

    @Override
    public List<? extends Token> tokenize(String pattern) {
        // split pattern into chunks: sea (raw input) and islands (<ID>, <expr>)
        List<Chunk> chunks = split(pattern);

        // create token stream from text and tags
        List<Token> tokens = new ArrayList<Token>();
        for (Chunk chunk : chunks) {
            if ( chunk instanceof TagChunk ) {
                TagChunk tagChunk = (TagChunk)chunk;
                // add special rule token or conjure up new token from name
                if ( Character.isUpperCase(tagChunk.getTag().charAt(0)) ) {
                    int type = parser.getTokenType(tagChunk.getTag());
                    if ( type==Token.INVALID_TYPE ) {
                        throw new IllegalArgumentException("Unknown token "+tagChunk.getTag()+" in pattern: "+pattern);
                    }
                    TokenTagToken t = new TokenTagToken(tagChunk.getTag(), type, tagChunk.getLabel());
                    tokens.add(t);
                }
                else if ( Character.isLowerCase(tagChunk.getTag().charAt(0)) ) {
                    int ruleIndex = parser.getRuleIndex(tagChunk.getTag());
                    if ( ruleIndex==-1 ) {
                        throw new IllegalArgumentException("Unknown rule "+tagChunk.getTag()+" in pattern: "+pattern);
                    }
                    int ruleImaginaryTokenType = parser.getATNWithBypassAlts().ruleToTokenType[ruleIndex];
                    tokens.add(new RuleTagToken(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel()));
                }
                else {
                    throw new IllegalArgumentException("invalid tag: "+tagChunk.getTag()+" in pattern: "+pattern);
                }
            }
            else {
                TextChunk textChunk = (TextChunk)chunk;
                ANTLRInputStream in = new ANTLRInputStream(textChunk.getText());
                lexer.setInputStream(in);
                lexer.pushMode(lexerMode);
                Token t = lexer.nextToken();
                while ( t.getType()!=Token.EOF ) {
                    tokens.add(t);
                    t = lexer.nextToken();
                }
            }
        }

//		System.out.println("tokens="+tokens);
        return tokens;
    }

    public ParseTreePattern compile(String pattern, int patternRuleIndex, int lexerMode) {
        this.lexerMode = lexerMode;
        return super.compile(pattern, patternRuleIndex);
    }
}

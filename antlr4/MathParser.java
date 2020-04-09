// Generated from Math.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MathParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, NUM=7, VAR=8, WHITESPACE=9;
	public static final int
		RULE_expr = 0, RULE_term = 1;
	public static final String[] ruleNames = {
		"expr", "term"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'*'", "'/'", "'+'", "'-'", "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, "NUM", "VAR", "WHITESPACE"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Math.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MathParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AddExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public AddExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).enterAddExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).exitAddExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathVisitor ) return ((MathVisitor<? extends T>)visitor).visitAddExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TerminalContext extends ExprContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public TerminalContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).enterTerminal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).exitTerminal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathVisitor ) return ((MathVisitor<? extends T>)visitor).visitTerminal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FactorContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public FactorContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).enterFactor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).exitFactor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathVisitor ) return ((MathVisitor<? extends T>)visitor).visitFactor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new TerminalContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(5);
			term();
			}
			_ctx.stop = _input.LT(-1);
			setState(21);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(19);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
					case 1:
						{
						_localctx = new FactorContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(7);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(8);
						match(T__0);
						setState(9);
						term();
						}
						break;
					case 2:
						{
						_localctx = new FactorContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(10);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(11);
						match(T__1);
						setState(12);
						term();
						}
						break;
					case 3:
						{
						_localctx = new AddExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(13);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(14);
						match(T__2);
						setState(15);
						term();
						}
						break;
					case 4:
						{
						_localctx = new AddExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(16);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(17);
						match(T__3);
						setState(18);
						term();
						}
						break;
					}
					} 
				}
				setState(23);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
	 
		public TermContext() { }
		public void copyFrom(TermContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ParExprContext extends TermContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParExprContext(TermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).enterParExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).exitParExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathVisitor ) return ((MathVisitor<? extends T>)visitor).visitParExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarContext extends TermContext {
		public TerminalNode VAR() { return getToken(MathParser.VAR, 0); }
		public VarContext(TermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).enterVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).exitVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathVisitor ) return ((MathVisitor<? extends T>)visitor).visitVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumContext extends TermContext {
		public TerminalNode NUM() { return getToken(MathParser.NUM, 0); }
		public NumContext(TermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).enterNum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathListener ) ((MathListener)listener).exitNum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathVisitor ) return ((MathVisitor<? extends T>)visitor).visitNum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_term);
		try {
			setState(30);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUM:
				_localctx = new NumContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(24);
				match(NUM);
				}
				break;
			case VAR:
				_localctx = new VarContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(25);
				match(VAR);
				}
				break;
			case T__4:
				_localctx = new ParExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(26);
				match(T__4);
				setState(27);
				expr(0);
				setState(28);
				match(T__5);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 5);
		case 1:
			return precpred(_ctx, 4);
		case 2:
			return precpred(_ctx, 3);
		case 3:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13#\4\2\t\2\4\3\t"+
		"\3\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\7\2\26"+
		"\n\2\f\2\16\2\31\13\2\3\3\3\3\3\3\3\3\3\3\3\3\5\3!\n\3\3\3\2\3\2\4\2\4"+
		"\2\2\2&\2\6\3\2\2\2\4 \3\2\2\2\6\7\b\2\1\2\7\b\5\4\3\2\b\27\3\2\2\2\t"+
		"\n\f\7\2\2\n\13\7\3\2\2\13\26\5\4\3\2\f\r\f\6\2\2\r\16\7\4\2\2\16\26\5"+
		"\4\3\2\17\20\f\5\2\2\20\21\7\5\2\2\21\26\5\4\3\2\22\23\f\4\2\2\23\24\7"+
		"\6\2\2\24\26\5\4\3\2\25\t\3\2\2\2\25\f\3\2\2\2\25\17\3\2\2\2\25\22\3\2"+
		"\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27\30\3\2\2\2\30\3\3\2\2\2\31\27\3\2"+
		"\2\2\32!\7\t\2\2\33!\7\n\2\2\34\35\7\7\2\2\35\36\5\2\2\2\36\37\7\b\2\2"+
		"\37!\3\2\2\2 \32\3\2\2\2 \33\3\2\2\2 \34\3\2\2\2!\5\3\2\2\2\5\25\27 ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
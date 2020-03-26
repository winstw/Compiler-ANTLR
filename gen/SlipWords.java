// Generated from /Users/emil/Documents/Unamur/BAC3/Syntaxe et Sémantique/Projet/src/main/antlr4/tmp/SlipWords.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SlipWords extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BOOLEANTYPE=1, INTEGERTYPE=2, CHARTYPE=3, VOIDTYPE=4, NAT=5, CHAR=6, ID=7, 
		COMMENT=8, WS=9, FILENAME=10, STRING=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"BOOLEANTYPE", "INTEGERTYPE", "CHARTYPE", "VOIDTYPE", "DIGIT", "LETTER", 
			"NAT", "CHAR", "ID", "COMMENT", "WS", "FILENAME", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'boolean'", "'integer'", "'char'", "'void'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BOOLEANTYPE", "INTEGERTYPE", "CHARTYPE", "VOIDTYPE", "NAT", "CHAR", 
			"ID", "COMMENT", "WS", "FILENAME", "STRING"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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


	public SlipWords(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SlipWords.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\r\u0089\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6"+
		"\3\6\3\7\3\7\3\b\6\b=\n\b\r\b\16\b>\3\t\3\t\3\t\3\t\6\tE\n\t\r\t\16\t"+
		"F\3\t\3\t\3\n\3\n\3\n\7\nN\n\n\f\n\16\nQ\13\n\3\13\3\13\3\13\3\13\7\13"+
		"W\n\13\f\13\16\13Z\13\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13b\n\13\f\13"+
		"\16\13e\13\13\3\13\5\13h\n\13\3\13\3\13\5\13l\n\13\5\13n\n\13\3\13\3\13"+
		"\3\f\6\fs\n\f\r\f\16\ft\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\16\3\16\6\16\u0084\n\16\r\16\16\16\u0085\3\16\3\16\4Xc\2\17\3\3\5\4\7"+
		"\5\t\6\13\2\r\2\17\7\21\b\23\t\25\n\27\13\31\f\33\r\3\2\6\4\2C\\c|\6\2"+
		"((\60\61<=^^\4\2\13\f\"\"\6\2\f\f\17\17..^^\2\u0093\2\3\3\2\2\2\2\5\3"+
		"\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\3\35\3\2\2\2\5%\3"+
		"\2\2\2\7-\3\2\2\2\t\62\3\2\2\2\13\67\3\2\2\2\r9\3\2\2\2\17<\3\2\2\2\21"+
		"@\3\2\2\2\23J\3\2\2\2\25m\3\2\2\2\27r\3\2\2\2\31x\3\2\2\2\33\u0081\3\2"+
		"\2\2\35\36\7d\2\2\36\37\7q\2\2\37 \7q\2\2 !\7n\2\2!\"\7g\2\2\"#\7c\2\2"+
		"#$\7p\2\2$\4\3\2\2\2%&\7k\2\2&\'\7p\2\2\'(\7v\2\2()\7g\2\2)*\7i\2\2*+"+
		"\7g\2\2+,\7t\2\2,\6\3\2\2\2-.\7e\2\2./\7j\2\2/\60\7c\2\2\60\61\7t\2\2"+
		"\61\b\3\2\2\2\62\63\7x\2\2\63\64\7q\2\2\64\65\7k\2\2\65\66\7f\2\2\66\n"+
		"\3\2\2\2\678\4\62;\28\f\3\2\2\29:\t\2\2\2:\16\3\2\2\2;=\5\13\6\2<;\3\2"+
		"\2\2=>\3\2\2\2><\3\2\2\2>?\3\2\2\2?\20\3\2\2\2@D\7)\2\2AE\5\13\6\2BE\5"+
		"\r\7\2CE\t\3\2\2DA\3\2\2\2DB\3\2\2\2DC\3\2\2\2EF\3\2\2\2FD\3\2\2\2FG\3"+
		"\2\2\2GH\3\2\2\2HI\7)\2\2I\22\3\2\2\2JO\5\r\7\2KN\5\r\7\2LN\5\13\6\2M"+
		"K\3\2\2\2ML\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2P\24\3\2\2\2QO\3\2\2"+
		"\2RS\7\61\2\2ST\7,\2\2TX\3\2\2\2UW\13\2\2\2VU\3\2\2\2WZ\3\2\2\2XY\3\2"+
		"\2\2XV\3\2\2\2Y[\3\2\2\2ZX\3\2\2\2[\\\7,\2\2\\n\7\61\2\2]^\7\61\2\2^_"+
		"\7\61\2\2_c\3\2\2\2`b\13\2\2\2a`\3\2\2\2be\3\2\2\2cd\3\2\2\2ca\3\2\2\2"+
		"dk\3\2\2\2ec\3\2\2\2fh\7\17\2\2gf\3\2\2\2gh\3\2\2\2hi\3\2\2\2il\7\f\2"+
		"\2jl\7\2\2\3kg\3\2\2\2kj\3\2\2\2ln\3\2\2\2mR\3\2\2\2m]\3\2\2\2no\3\2\2"+
		"\2op\b\13\2\2p\26\3\2\2\2qs\t\4\2\2rq\3\2\2\2st\3\2\2\2tr\3\2\2\2tu\3"+
		"\2\2\2uv\3\2\2\2vw\b\f\2\2w\30\3\2\2\2xy\7$\2\2yz\5\23\n\2z{\7\60\2\2"+
		"{|\7o\2\2|}\7c\2\2}~\7r\2\2~\177\3\2\2\2\177\u0080\7$\2\2\u0080\32\3\2"+
		"\2\2\u0081\u0083\7$\2\2\u0082\u0084\n\5\2\2\u0083\u0082\3\2\2\2\u0084"+
		"\u0085\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087\3\2"+
		"\2\2\u0087\u0088\7$\2\2\u0088\34\3\2\2\2\17\2>DFMOXcgkmt\u0085\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
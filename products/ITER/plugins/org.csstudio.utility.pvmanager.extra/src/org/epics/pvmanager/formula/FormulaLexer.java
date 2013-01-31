// $ANTLR 3.4 org\\epics\\pvmanager\\formula\\Formula.g 2013-01-28 16:39:00

  package org.epics.pvmanager.formula;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FormulaLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int ESC_SEQ=4;
    public static final int EXPONENT=5;
    public static final int FLOAT=6;
    public static final int FULL_ID=7;
    public static final int HEX_DIGIT=8;
    public static final int ID=9;
    public static final int INT=10;
    public static final int OCTAL_ESC=11;
    public static final int QUOTED_ID=12;
    public static final int STRING=13;
    public static final int UNICODE_ESC=14;
    public static final int WS=15;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public FormulaLexer() {} 
    public FormulaLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public FormulaLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "org\\epics\\pvmanager\\formula\\Formula.g"; }

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:11:7: ( '%' )
            // org\\epics\\pvmanager\\formula\\Formula.g:11:9: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:12:7: ( '(' )
            // org\\epics\\pvmanager\\formula\\Formula.g:12:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:13:7: ( ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:13:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:14:7: ( '*' )
            // org\\epics\\pvmanager\\formula\\Formula.g:14:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:15:7: ( '**' )
            // org\\epics\\pvmanager\\formula\\Formula.g:15:9: '**'
            {
            match("**"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:16:7: ( '+' )
            // org\\epics\\pvmanager\\formula\\Formula.g:16:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:17:7: ( ',' )
            // org\\epics\\pvmanager\\formula\\Formula.g:17:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:18:7: ( '-' )
            // org\\epics\\pvmanager\\formula\\Formula.g:18:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:19:7: ( '/' )
            // org\\epics\\pvmanager\\formula\\Formula.g:19:9: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:20:7: ( '^' )
            // org\\epics\\pvmanager\\formula\\Formula.g:20:9: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "FULL_ID"
    public final void mFULL_ID() throws RecognitionException {
        try {
            int _type = FULL_ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:89:10: ( ( ( 'a' .. 'z' | 'A' .. 'Z' )* ':' '/' '/' ) ( '0' .. '9' )* ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' | '(' ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' | ',' | ' ' | '-' | STRING )* ')' )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:89:12: ( ( 'a' .. 'z' | 'A' .. 'Z' )* ':' '/' '/' ) ( '0' .. '9' )* ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' | '(' ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' | ',' | ' ' | '-' | STRING )* ')' )*
            {
            // org\\epics\\pvmanager\\formula\\Formula.g:89:12: ( ( 'a' .. 'z' | 'A' .. 'Z' )* ':' '/' '/' )
            // org\\epics\\pvmanager\\formula\\Formula.g:89:13: ( 'a' .. 'z' | 'A' .. 'Z' )* ':' '/' '/'
            {
            // org\\epics\\pvmanager\\formula\\Formula.g:89:13: ( 'a' .. 'z' | 'A' .. 'Z' )*
            loop1:
            do {
                int alt1=2;
                switch ( input.LA(1) ) {
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt1=1;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:
            	    {
            	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            match(':'); 

            match('/'); 

            match('/'); 

            }


            // org\\epics\\pvmanager\\formula\\Formula.g:89:48: ( '0' .. '9' )*
            loop2:
            do {
                int alt2=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt2=1;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // org\\epics\\pvmanager\\formula\\Formula.g:90:18: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' | '(' ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' | ',' | ' ' | '-' | STRING )* ')' )*
            loop4:
            do {
                int alt4=11;
                switch ( input.LA(1) ) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt4=1;
                    }
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                    {
                    alt4=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt4=3;
                    }
                    break;
                case '.':
                    {
                    alt4=4;
                    }
                    break;
                case '$':
                    {
                    alt4=5;
                    }
                    break;
                case '_':
                    {
                    alt4=6;
                    }
                    break;
                case ':':
                    {
                    alt4=7;
                    }
                    break;
                case '{':
                    {
                    alt4=8;
                    }
                    break;
                case '}':
                    {
                    alt4=9;
                    }
                    break;
                case '(':
                    {
                    alt4=10;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:20: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); 

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:29: 'A' .. 'Z'
            	    {
            	    matchRange('A','Z'); 

            	    }
            	    break;
            	case 3 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:38: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;
            	case 4 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:47: '.'
            	    {
            	    match('.'); 

            	    }
            	    break;
            	case 5 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:51: '$'
            	    {
            	    match('$'); 

            	    }
            	    break;
            	case 6 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:55: '_'
            	    {
            	    match('_'); 

            	    }
            	    break;
            	case 7 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:59: ':'
            	    {
            	    match(':'); 

            	    }
            	    break;
            	case 8 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:63: '{'
            	    {
            	    match('{'); 

            	    }
            	    break;
            	case 9 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:67: '}'
            	    {
            	    match('}'); 

            	    }
            	    break;
            	case 10 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:91:20: '(' ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' | ',' | ' ' | '-' | STRING )* ')'
            	    {
            	    match('('); 

            	    // org\\epics\\pvmanager\\formula\\Formula.g:91:24: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' | ',' | ' ' | '-' | STRING )*
            	    loop3:
            	    do {
            	        int alt3=14;
            	        switch ( input.LA(1) ) {
            	        case 'a':
            	        case 'b':
            	        case 'c':
            	        case 'd':
            	        case 'e':
            	        case 'f':
            	        case 'g':
            	        case 'h':
            	        case 'i':
            	        case 'j':
            	        case 'k':
            	        case 'l':
            	        case 'm':
            	        case 'n':
            	        case 'o':
            	        case 'p':
            	        case 'q':
            	        case 'r':
            	        case 's':
            	        case 't':
            	        case 'u':
            	        case 'v':
            	        case 'w':
            	        case 'x':
            	        case 'y':
            	        case 'z':
            	            {
            	            alt3=1;
            	            }
            	            break;
            	        case 'A':
            	        case 'B':
            	        case 'C':
            	        case 'D':
            	        case 'E':
            	        case 'F':
            	        case 'G':
            	        case 'H':
            	        case 'I':
            	        case 'J':
            	        case 'K':
            	        case 'L':
            	        case 'M':
            	        case 'N':
            	        case 'O':
            	        case 'P':
            	        case 'Q':
            	        case 'R':
            	        case 'S':
            	        case 'T':
            	        case 'U':
            	        case 'V':
            	        case 'W':
            	        case 'X':
            	        case 'Y':
            	        case 'Z':
            	            {
            	            alt3=2;
            	            }
            	            break;
            	        case '0':
            	        case '1':
            	        case '2':
            	        case '3':
            	        case '4':
            	        case '5':
            	        case '6':
            	        case '7':
            	        case '8':
            	        case '9':
            	            {
            	            alt3=3;
            	            }
            	            break;
            	        case '.':
            	            {
            	            alt3=4;
            	            }
            	            break;
            	        case '$':
            	            {
            	            alt3=5;
            	            }
            	            break;
            	        case '_':
            	            {
            	            alt3=6;
            	            }
            	            break;
            	        case ':':
            	            {
            	            alt3=7;
            	            }
            	            break;
            	        case '{':
            	            {
            	            alt3=8;
            	            }
            	            break;
            	        case '}':
            	            {
            	            alt3=9;
            	            }
            	            break;
            	        case ',':
            	            {
            	            alt3=10;
            	            }
            	            break;
            	        case ' ':
            	            {
            	            alt3=11;
            	            }
            	            break;
            	        case '-':
            	            {
            	            alt3=12;
            	            }
            	            break;
            	        case '\"':
            	            {
            	            alt3=13;
            	            }
            	            break;

            	        }

            	        switch (alt3) {
            	    	case 1 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:25: 'a' .. 'z'
            	    	    {
            	    	    matchRange('a','z'); 

            	    	    }
            	    	    break;
            	    	case 2 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:34: 'A' .. 'Z'
            	    	    {
            	    	    matchRange('A','Z'); 

            	    	    }
            	    	    break;
            	    	case 3 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:43: '0' .. '9'
            	    	    {
            	    	    matchRange('0','9'); 

            	    	    }
            	    	    break;
            	    	case 4 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:52: '.'
            	    	    {
            	    	    match('.'); 

            	    	    }
            	    	    break;
            	    	case 5 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:56: '$'
            	    	    {
            	    	    match('$'); 

            	    	    }
            	    	    break;
            	    	case 6 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:60: '_'
            	    	    {
            	    	    match('_'); 

            	    	    }
            	    	    break;
            	    	case 7 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:64: ':'
            	    	    {
            	    	    match(':'); 

            	    	    }
            	    	    break;
            	    	case 8 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:68: '{'
            	    	    {
            	    	    match('{'); 

            	    	    }
            	    	    break;
            	    	case 9 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:72: '}'
            	    	    {
            	    	    match('}'); 

            	    	    }
            	    	    break;
            	    	case 10 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:76: ','
            	    	    {
            	    	    match(','); 

            	    	    }
            	    	    break;
            	    	case 11 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:80: ' '
            	    	    {
            	    	    match(' '); 

            	    	    }
            	    	    break;
            	    	case 12 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:84: '-'
            	    	    {
            	    	    match('-'); 

            	    	    }
            	    	    break;
            	    	case 13 :
            	    	    // org\\epics\\pvmanager\\formula\\Formula.g:91:89: STRING
            	    	    {
            	    	    mSTRING(); 


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop3;
            	        }
            	    } while (true);


            	    match(')'); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FULL_ID"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:94:5: ( ( '0' .. '9' )+ )
            // org\\epics\\pvmanager\\formula\\Formula.g:94:7: ( '0' .. '9' )+
            {
            // org\\epics\\pvmanager\\formula\\Formula.g:94:7: ( '0' .. '9' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt5=1;
                    }
                    break;

                }

                switch (alt5) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:98:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
            int alt12=3;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:98:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // org\\epics\\pvmanager\\formula\\Formula.g:98:9: ( '0' .. '9' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        switch ( input.LA(1) ) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            {
                            alt6=1;
                            }
                            break;

                        }

                        switch (alt6) {
                    	case 1 :
                    	    // org\\epics\\pvmanager\\formula\\Formula.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    match('.'); 

                    // org\\epics\\pvmanager\\formula\\Formula.g:98:25: ( '0' .. '9' )*
                    loop7:
                    do {
                        int alt7=2;
                        switch ( input.LA(1) ) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            {
                            alt7=1;
                            }
                            break;

                        }

                        switch (alt7) {
                    	case 1 :
                    	    // org\\epics\\pvmanager\\formula\\Formula.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    // org\\epics\\pvmanager\\formula\\Formula.g:98:37: ( EXPONENT )?
                    int alt8=2;
                    switch ( input.LA(1) ) {
                        case 'E':
                        case 'e':
                            {
                            alt8=1;
                            }
                            break;
                    }

                    switch (alt8) {
                        case 1 :
                            // org\\epics\\pvmanager\\formula\\Formula.g:98:37: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:99:9: '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    match('.'); 

                    // org\\epics\\pvmanager\\formula\\Formula.g:99:13: ( '0' .. '9' )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        switch ( input.LA(1) ) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            {
                            alt9=1;
                            }
                            break;

                        }

                        switch (alt9) {
                    	case 1 :
                    	    // org\\epics\\pvmanager\\formula\\Formula.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);


                    // org\\epics\\pvmanager\\formula\\Formula.g:99:25: ( EXPONENT )?
                    int alt10=2;
                    switch ( input.LA(1) ) {
                        case 'E':
                        case 'e':
                            {
                            alt10=1;
                            }
                            break;
                    }

                    switch (alt10) {
                        case 1 :
                            // org\\epics\\pvmanager\\formula\\Formula.g:99:25: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:100:9: ( '0' .. '9' )+ EXPONENT
                    {
                    // org\\epics\\pvmanager\\formula\\Formula.g:100:9: ( '0' .. '9' )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        switch ( input.LA(1) ) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            {
                            alt11=1;
                            }
                            break;

                        }

                        switch (alt11) {
                    	case 1 :
                    	    // org\\epics\\pvmanager\\formula\\Formula.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    mEXPONENT(); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:103:5: ( ( '0' .. '9' )* ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:103:7: ( '0' .. '9' )* ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' )*
            {
            // org\\epics\\pvmanager\\formula\\Formula.g:103:7: ( '0' .. '9' )*
            loop13:
            do {
                int alt13=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt13=1;
                    }
                    break;

                }

                switch (alt13) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // org\\epics\\pvmanager\\formula\\Formula.g:103:43: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '$' | '_' | ':' | '{' | '}' )*
            loop14:
            do {
                int alt14=2;
                switch ( input.LA(1) ) {
                case '$':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ':':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case '{':
                case '}':
                    {
                    alt14=1;
                    }
                    break;

                }

                switch (alt14) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:
            	    {
            	    if ( input.LA(1)=='$'||input.LA(1)=='.'||(input.LA(1) >= '0' && input.LA(1) <= ':')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= '{')||input.LA(1)=='}' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:106:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // org\\epics\\pvmanager\\formula\\Formula.g:106:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:114:5: ( '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"' )
            // org\\epics\\pvmanager\\formula\\Formula.g:114:8: '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 

            // org\\epics\\pvmanager\\formula\\Formula.g:114:12: ( ESC_SEQ |~ ( '\\\\' | '\"' ) )*
            loop15:
            do {
                int alt15=3;
                int LA15_0 = input.LA(1);

                if ( (LA15_0=='\\') ) {
                    alt15=1;
                }
                else if ( ((LA15_0 >= '\u0000' && LA15_0 <= '!')||(LA15_0 >= '#' && LA15_0 <= '[')||(LA15_0 >= ']' && LA15_0 <= '\uFFFF')) ) {
                    alt15=2;
                }


                switch (alt15) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:114:14: ESC_SEQ
            	    {
            	    mESC_SEQ(); 


            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:114:24: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "QUOTED_ID"
    public final void mQUOTED_ID() throws RecognitionException {
        try {
            int _type = QUOTED_ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org\\epics\\pvmanager\\formula\\Formula.g:118:5: ( '\\'' ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )* '\\'' )
            // org\\epics\\pvmanager\\formula\\Formula.g:118:8: '\\'' ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )* '\\''
            {
            match('\''); 

            // org\\epics\\pvmanager\\formula\\Formula.g:118:13: ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )*
            loop16:
            do {
                int alt16=3;
                int LA16_0 = input.LA(1);

                if ( (LA16_0=='\\') ) {
                    alt16=1;
                }
                else if ( ((LA16_0 >= '\u0000' && LA16_0 <= '&')||(LA16_0 >= '(' && LA16_0 <= '[')||(LA16_0 >= ']' && LA16_0 <= '\uFFFF')) ) {
                    alt16=2;
                }


                switch (alt16) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:118:15: ESC_SEQ
            	    {
            	    mESC_SEQ(); 


            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:118:25: ~ ( '\\\\' | '\\'' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUOTED_ID"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:123:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // org\\epics\\pvmanager\\formula\\Formula.g:123:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // org\\epics\\pvmanager\\formula\\Formula.g:123:22: ( '+' | '-' )?
            int alt17=2;
            switch ( input.LA(1) ) {
                case '+':
                case '-':
                    {
                    alt17=1;
                    }
                    break;
            }

            switch (alt17) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // org\\epics\\pvmanager\\formula\\Formula.g:123:33: ( '0' .. '9' )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt18=1;
                    }
                    break;

                }

                switch (alt18) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:126:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // org\\epics\\pvmanager\\formula\\Formula.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:130:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt19=3;
            switch ( input.LA(1) ) {
            case '\\':
                {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt19=1;
                    }
                    break;
                case 'u':
                    {
                    alt19=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt19=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 1, input);

                    throw nvae;

                }

                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }

            switch (alt19) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:130:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 

                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:131:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 


                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:132:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:137:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt20=3;
            switch ( input.LA(1) ) {
            case '\\':
                {
                switch ( input.LA(2) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                    {
                    switch ( input.LA(3) ) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                        {
                        switch ( input.LA(4) ) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                            {
                            alt20=1;
                            }
                            break;
                        default:
                            alt20=2;
                        }

                        }
                        break;
                    default:
                        alt20=3;
                    }

                    }
                    break;
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    switch ( input.LA(3) ) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                        {
                        alt20=2;
                        }
                        break;
                    default:
                        alt20=3;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 1, input);

                    throw nvae;

                }

                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;

            }

            switch (alt20) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:137:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '3') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:138:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:139:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OCTAL_ESC"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:144:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // org\\epics\\pvmanager\\formula\\Formula.g:144:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 

            match('u'); 

            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNICODE_ESC"

    public void mTokens() throws RecognitionException {
        // org\\epics\\pvmanager\\formula\\Formula.g:1:8: ( T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | FULL_ID | INT | FLOAT | ID | WS | STRING | QUOTED_ID )
        int alt21=17;
        alt21 = dfa21.predict(input);
        switch (alt21) {
            case 1 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:10: T__16
                {
                mT__16(); 


                }
                break;
            case 2 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:16: T__17
                {
                mT__17(); 


                }
                break;
            case 3 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:22: T__18
                {
                mT__18(); 


                }
                break;
            case 4 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:28: T__19
                {
                mT__19(); 


                }
                break;
            case 5 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:34: T__20
                {
                mT__20(); 


                }
                break;
            case 6 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:40: T__21
                {
                mT__21(); 


                }
                break;
            case 7 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:46: T__22
                {
                mT__22(); 


                }
                break;
            case 8 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:52: T__23
                {
                mT__23(); 


                }
                break;
            case 9 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:58: T__24
                {
                mT__24(); 


                }
                break;
            case 10 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:64: T__25
                {
                mT__25(); 


                }
                break;
            case 11 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:70: FULL_ID
                {
                mFULL_ID(); 


                }
                break;
            case 12 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:78: INT
                {
                mINT(); 


                }
                break;
            case 13 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:82: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 14 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:88: ID
                {
                mID(); 


                }
                break;
            case 15 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:91: WS
                {
                mWS(); 


                }
                break;
            case 16 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:94: STRING
                {
                mSTRING(); 


                }
                break;
            case 17 :
                // org\\epics\\pvmanager\\formula\\Formula.g:1:101: QUOTED_ID
                {
                mQUOTED_ID(); 


                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA21 dfa21 = new DFA21(this);
    static final String DFA12_eotS =
        "\5\uffff";
    static final String DFA12_eofS =
        "\5\uffff";
    static final String DFA12_minS =
        "\2\56\3\uffff";
    static final String DFA12_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA12_specialS =
        "\5\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "97:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
        }
    }
    static final String DFA21_eotS =
        "\4\uffff\1\23\5\uffff\1\16\1\uffff\1\26\7\uffff\2\16\1\uffff\1\16"+
        "\1\15";
    static final String DFA21_eofS =
        "\31\uffff";
    static final String DFA21_minS =
        "\1\11\3\uffff\1\52\5\uffff\1\72\1\uffff\1\56\7\uffff\1\57\1\72\1"+
        "\uffff\1\53\1\44";
    static final String DFA21_maxS =
        "\1\172\3\uffff\1\52\5\uffff\1\172\1\uffff\1\172\7\uffff\1\57\1\172"+
        "\1\uffff\1\71\1\175";
    static final String DFA21_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\uffff\1\6\1\7\1\10\1\11\1\12\1\uffff\1\13"+
        "\1\uffff\1\15\1\16\1\17\1\20\1\21\1\5\1\4\2\uffff\1\14\2\uffff";
    static final String DFA21_specialS =
        "\31\uffff}>";
    static final String[] DFA21_transitionS = {
            "\2\17\2\uffff\1\17\22\uffff\1\17\1\uffff\1\20\2\uffff\1\1\1"+
            "\uffff\1\21\1\2\1\3\1\4\1\5\1\6\1\7\1\15\1\10\12\14\1\13\6\uffff"+
            "\32\12\3\uffff\1\11\1\16\1\uffff\32\12",
            "",
            "",
            "",
            "\1\22",
            "",
            "",
            "",
            "",
            "",
            "\1\24\6\uffff\32\25\6\uffff\32\25",
            "",
            "\1\15\1\uffff\12\14\7\uffff\4\16\1\27\25\16\4\uffff\1\16\1"+
            "\uffff\4\16\1\27\25\16",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\13",
            "\1\24\6\uffff\32\25\6\uffff\32\25",
            "",
            "\1\15\1\uffff\1\15\2\uffff\12\30",
            "\1\16\11\uffff\1\16\1\uffff\12\30\1\16\6\uffff\32\16\4\uffff"+
            "\1\16\1\uffff\33\16\1\uffff\1\16"
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | FULL_ID | INT | FLOAT | ID | WS | STRING | QUOTED_ID );";
        }
    }
 

}
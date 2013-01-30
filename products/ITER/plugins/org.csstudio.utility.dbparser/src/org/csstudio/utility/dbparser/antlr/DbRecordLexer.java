// $ANTLR 3.4 antlr/DbRecord.g 2013-01-28 12:45:10

package org.csstudio.utility.dbparser.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class DbRecordLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int ALIAS=4;
    public static final int COMMENT=5;
    public static final int ESC_SEQ=6;
    public static final int EXPONENT=7;
    public static final int FIELD=8;
    public static final int FLOAT=9;
    public static final int HEX_DIGIT=10;
    public static final int ID=11;
    public static final int INFO=12;
    public static final int INT=13;
    public static final int OCTAL_ESC=14;
    public static final int RECORD=15;
    public static final int RECORD_BODY=16;
    public static final int RECORD_INSTANCE=17;
    public static final int String=18;
    public static final int TYPE=19;
    public static final int UNICODE_ESC=20;
    public static final int VALUE=21;
    public static final int WHITESPACE=22;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public DbRecordLexer() {} 
    public DbRecordLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public DbRecordLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "antlr/DbRecord.g"; }

    // $ANTLR start "ALIAS"
    public final void mALIAS() throws RecognitionException {
        try {
            int _type = ALIAS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:11:7: ( 'alias' )
            // antlr/DbRecord.g:11:9: 'alias'
            {
            match("alias"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ALIAS"

    // $ANTLR start "FIELD"
    public final void mFIELD() throws RecognitionException {
        try {
            int _type = FIELD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:12:7: ( 'field' )
            // antlr/DbRecord.g:12:9: 'field'
            {
            match("field"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FIELD"

    // $ANTLR start "INFO"
    public final void mINFO() throws RecognitionException {
        try {
            int _type = INFO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:13:6: ( 'info' )
            // antlr/DbRecord.g:13:8: 'info'
            {
            match("info"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INFO"

    // $ANTLR start "RECORD"
    public final void mRECORD() throws RecognitionException {
        try {
            int _type = RECORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:14:8: ( 'record' )
            // antlr/DbRecord.g:14:10: 'record'
            {
            match("record"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RECORD"

    // $ANTLR start "RECORD_BODY"
    public final void mRECORD_BODY() throws RecognitionException {
        try {
            int _type = RECORD_BODY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:15:13: ( 'record_body' )
            // antlr/DbRecord.g:15:15: 'record_body'
            {
            match("record_body"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RECORD_BODY"

    // $ANTLR start "RECORD_INSTANCE"
    public final void mRECORD_INSTANCE() throws RecognitionException {
        try {
            int _type = RECORD_INSTANCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:16:17: ( 'record_instance' )
            // antlr/DbRecord.g:16:19: 'record_instance'
            {
            match("record_instance"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RECORD_INSTANCE"

    // $ANTLR start "TYPE"
    public final void mTYPE() throws RecognitionException {
        try {
            int _type = TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:17:6: ( 'type' )
            // antlr/DbRecord.g:17:8: 'type'
            {
            match("type"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "VALUE"
    public final void mVALUE() throws RecognitionException {
        try {
            int _type = VALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:18:7: ( 'value' )
            // antlr/DbRecord.g:18:9: 'value'
            {
            match("value"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VALUE"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:19:7: ( '(' )
            // antlr/DbRecord.g:19:9: '('
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
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:20:7: ( ')' )
            // antlr/DbRecord.g:20:9: ')'
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
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:21:7: ( ',' )
            // antlr/DbRecord.g:21:9: ','
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
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:22:7: ( '{' )
            // antlr/DbRecord.g:22:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:23:7: ( '}' )
            // antlr/DbRecord.g:23:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            int _type = OCTAL_ESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:52:3: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt1=3;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='\\') ) {
                int LA1_1 = input.LA(2);

                if ( ((LA1_1 >= '0' && LA1_1 <= '3')) ) {
                    int LA1_2 = input.LA(3);

                    if ( ((LA1_2 >= '0' && LA1_2 <= '7')) ) {
                        int LA1_4 = input.LA(4);

                        if ( ((LA1_4 >= '0' && LA1_4 <= '7')) ) {
                            alt1=1;
                        }
                        else {
                            alt1=2;
                        }
                    }
                    else {
                        alt1=3;
                    }
                }
                else if ( ((LA1_1 >= '4' && LA1_1 <= '7')) ) {
                    int LA1_3 = input.LA(3);

                    if ( ((LA1_3 >= '0' && LA1_3 <= '7')) ) {
                        alt1=2;
                    }
                    else {
                        alt1=3;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }
            switch (alt1) {
                case 1 :
                    // antlr/DbRecord.g:53:3: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
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
                    // antlr/DbRecord.g:54:5: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
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
                    // antlr/DbRecord.g:55:5: '\\\\' ( '0' .. '7' )
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
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OCTAL_ESC"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            int _type = HEX_DIGIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:59:3: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // antlr/DbRecord.g:
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            int _type = UNICODE_ESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:68:3: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // antlr/DbRecord.g:69:3: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 

            match('u'); 

            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNICODE_ESC"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:73:3: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
            // antlr/DbRecord.g:74:3: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            {
            // antlr/DbRecord.g:74:3: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= '\t' && LA2_0 <= '\n')||(LA2_0 >= '\f' && LA2_0 <= '\r')||LA2_0==' ') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // antlr/DbRecord.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
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
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);



               _channel = HIDDEN;
              

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:88:3: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // antlr/DbRecord.g:89:3: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // antlr/DbRecord.g:94:3: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='-'||(LA3_0 >= '0' && LA3_0 <= '9')||(LA3_0 >= 'A' && LA3_0 <= 'Z')||LA3_0=='_'||(LA3_0 >= 'a' && LA3_0 <= 'z')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // antlr/DbRecord.g:
            	    {
            	    if ( input.LA(1)=='-'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            	    break loop3;
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

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:104:3: ( ( '0' .. '9' )+ )
            // antlr/DbRecord.g:105:3: ( '0' .. '9' )+
            {
            // antlr/DbRecord.g:105:3: ( '0' .. '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // antlr/DbRecord.g:
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
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
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
            // antlr/DbRecord.g:109:3: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
            int alt11=3;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // antlr/DbRecord.g:110:3: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // antlr/DbRecord.g:110:3: ( '0' .. '9' )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // antlr/DbRecord.g:
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


                    match('.'); 

                    // antlr/DbRecord.g:110:19: ( '0' .. '9' )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // antlr/DbRecord.g:
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
                    	    break loop6;
                        }
                    } while (true);


                    // antlr/DbRecord.g:110:31: ( EXPONENT )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='E'||LA7_0=='e') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // antlr/DbRecord.g:110:31: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // antlr/DbRecord.g:111:5: '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    match('.'); 

                    // antlr/DbRecord.g:111:9: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0 >= '0' && LA8_0 <= '9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // antlr/DbRecord.g:
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
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);


                    // antlr/DbRecord.g:111:21: ( EXPONENT )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='E'||LA9_0=='e') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // antlr/DbRecord.g:111:21: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // antlr/DbRecord.g:112:5: ( '0' .. '9' )+ EXPONENT
                    {
                    // antlr/DbRecord.g:112:5: ( '0' .. '9' )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0 >= '0' && LA10_0 <= '9')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // antlr/DbRecord.g:
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
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
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

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:116:3: ( '#' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // antlr/DbRecord.g:117:3: '#' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match('#'); 

            // antlr/DbRecord.g:118:3: (~ ( '\\n' | '\\r' ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0 >= '\u0000' && LA12_0 <= '\t')||(LA12_0 >= '\u000B' && LA12_0 <= '\f')||(LA12_0 >= '\u000E' && LA12_0 <= '\uFFFF')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // antlr/DbRecord.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
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
            	    break loop12;
                }
            } while (true);


            // antlr/DbRecord.g:122:3: ( '\\r' )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='\r') ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // antlr/DbRecord.g:122:3: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 


                         _channel = HIDDEN;
                        

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "String"
    public final void mString() throws RecognitionException {
        try {
            int _type = String;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:129:3: ( '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"' )
            // antlr/DbRecord.g:130:3: '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 

            // antlr/DbRecord.g:131:3: ( ESC_SEQ |~ ( '\\\\' | '\"' ) )*
            loop14:
            do {
                int alt14=3;
                int LA14_0 = input.LA(1);

                if ( (LA14_0=='\\') ) {
                    alt14=1;
                }
                else if ( ((LA14_0 >= '\u0000' && LA14_0 <= '!')||(LA14_0 >= '#' && LA14_0 <= '[')||(LA14_0 >= ']' && LA14_0 <= '\uFFFF')) ) {
                    alt14=2;
                }


                switch (alt14) {
            	case 1 :
            	    // antlr/DbRecord.g:132:5: ESC_SEQ
            	    {
            	    mESC_SEQ(); 


            	    }
            	    break;
            	case 2 :
            	    // antlr/DbRecord.g:134:5: ~ ( '\\\\' | '\"' )
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
            	    break loop14;
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
    // $ANTLR end "String"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            int _type = ESC_SEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:143:3: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt15=3;
            int LA15_0 = input.LA(1);

            if ( (LA15_0=='\\') ) {
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
                    alt15=1;
                    }
                    break;
                case 'u':
                    {
                    alt15=2;
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
                    alt15=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // antlr/DbRecord.g:144:3: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
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
                    // antlr/DbRecord.g:155:5: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 


                    }
                    break;
                case 3 :
                    // antlr/DbRecord.g:156:5: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 


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
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            int _type = EXPONENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // antlr/DbRecord.g:162:3: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // antlr/DbRecord.g:163:3: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // antlr/DbRecord.g:167:3: ( '+' | '-' )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='+'||LA16_0=='-') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // antlr/DbRecord.g:
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


            // antlr/DbRecord.g:171:3: ( '0' .. '9' )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0 >= '0' && LA17_0 <= '9')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // antlr/DbRecord.g:
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
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXPONENT"

    public void mTokens() throws RecognitionException {
        // antlr/DbRecord.g:1:8: ( ALIAS | FIELD | INFO | RECORD | RECORD_BODY | RECORD_INSTANCE | TYPE | VALUE | T__23 | T__24 | T__25 | T__26 | T__27 | OCTAL_ESC | HEX_DIGIT | UNICODE_ESC | WHITESPACE | ID | INT | FLOAT | COMMENT | String | ESC_SEQ | EXPONENT )
        int alt18=24;
        alt18 = dfa18.predict(input);
        switch (alt18) {
            case 1 :
                // antlr/DbRecord.g:1:10: ALIAS
                {
                mALIAS(); 


                }
                break;
            case 2 :
                // antlr/DbRecord.g:1:16: FIELD
                {
                mFIELD(); 


                }
                break;
            case 3 :
                // antlr/DbRecord.g:1:22: INFO
                {
                mINFO(); 


                }
                break;
            case 4 :
                // antlr/DbRecord.g:1:27: RECORD
                {
                mRECORD(); 


                }
                break;
            case 5 :
                // antlr/DbRecord.g:1:34: RECORD_BODY
                {
                mRECORD_BODY(); 


                }
                break;
            case 6 :
                // antlr/DbRecord.g:1:46: RECORD_INSTANCE
                {
                mRECORD_INSTANCE(); 


                }
                break;
            case 7 :
                // antlr/DbRecord.g:1:62: TYPE
                {
                mTYPE(); 


                }
                break;
            case 8 :
                // antlr/DbRecord.g:1:67: VALUE
                {
                mVALUE(); 


                }
                break;
            case 9 :
                // antlr/DbRecord.g:1:73: T__23
                {
                mT__23(); 


                }
                break;
            case 10 :
                // antlr/DbRecord.g:1:79: T__24
                {
                mT__24(); 


                }
                break;
            case 11 :
                // antlr/DbRecord.g:1:85: T__25
                {
                mT__25(); 


                }
                break;
            case 12 :
                // antlr/DbRecord.g:1:91: T__26
                {
                mT__26(); 


                }
                break;
            case 13 :
                // antlr/DbRecord.g:1:97: T__27
                {
                mT__27(); 


                }
                break;
            case 14 :
                // antlr/DbRecord.g:1:103: OCTAL_ESC
                {
                mOCTAL_ESC(); 


                }
                break;
            case 15 :
                // antlr/DbRecord.g:1:113: HEX_DIGIT
                {
                mHEX_DIGIT(); 


                }
                break;
            case 16 :
                // antlr/DbRecord.g:1:123: UNICODE_ESC
                {
                mUNICODE_ESC(); 


                }
                break;
            case 17 :
                // antlr/DbRecord.g:1:135: WHITESPACE
                {
                mWHITESPACE(); 


                }
                break;
            case 18 :
                // antlr/DbRecord.g:1:146: ID
                {
                mID(); 


                }
                break;
            case 19 :
                // antlr/DbRecord.g:1:149: INT
                {
                mINT(); 


                }
                break;
            case 20 :
                // antlr/DbRecord.g:1:153: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 21 :
                // antlr/DbRecord.g:1:159: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 22 :
                // antlr/DbRecord.g:1:167: String
                {
                mString(); 


                }
                break;
            case 23 :
                // antlr/DbRecord.g:1:174: ESC_SEQ
                {
                mESC_SEQ(); 


                }
                break;
            case 24 :
                // antlr/DbRecord.g:1:182: EXPONENT
                {
                mEXPONENT(); 


                }
                break;

        }

    }


    protected DFA11 dfa11 = new DFA11(this);
    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA11_eotS =
        "\5\uffff";
    static final String DFA11_eofS =
        "\5\uffff";
    static final String DFA11_minS =
        "\2\56\3\uffff";
    static final String DFA11_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA11_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA11_specialS =
        "\5\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "108:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
        }
    }
    static final String DFA18_eotS =
        "\1\uffff\2\26\4\20\6\uffff\1\26\1\uffff\1\26\4\uffff\1\26\1\20\1"+
        "\uffff\5\20\2\53\2\uffff\2\20\1\uffff\1\56\6\20\1\53\4\uffff\2\20"+
        "\1\71\1\20\1\73\1\20\2\uffff\1\76\1\77\1\uffff\1\20\1\uffff\1\101"+
        "\3\uffff\1\104\2\uffff\1\20\2\uffff\6\20\1\116\1\20\1\uffff\3\20"+
        "\1\123\1\uffff";
    static final String DFA18_eofS =
        "\124\uffff";
    static final String DFA18_minS =
        "\1\11\2\55\1\156\1\145\1\171\1\141\5\uffff\1\42\1\53\1\uffff\1\56"+
        "\4\uffff\1\55\1\151\1\uffff\1\145\1\146\1\143\1\160\1\154\3\60\1"+
        "\uffff\2\60\1\uffff\1\56\1\141\1\154\2\157\1\145\1\165\1\60\2\uffff"+
        "\1\60\1\uffff\1\163\1\144\1\55\1\162\1\55\1\145\1\uffff\1\60\2\55"+
        "\1\uffff\1\144\1\uffff\1\55\1\60\2\uffff\1\55\2\uffff\1\142\2\uffff"+
        "\1\157\1\156\1\144\1\163\1\171\1\164\1\55\1\141\1\uffff\1\156\1"+
        "\143\1\145\1\55\1\uffff";
    static final String DFA18_maxS =
        "\1\175\2\172\1\156\1\145\1\171\1\141\5\uffff\1\165\1\172\1\uffff"+
        "\1\145\4\uffff\1\172\1\151\1\uffff\1\145\1\146\1\143\1\160\1\154"+
        "\2\67\1\146\1\uffff\2\71\1\uffff\1\145\1\141\1\154\2\157\1\145\1"+
        "\165\1\67\2\uffff\1\146\1\uffff\1\163\1\144\1\172\1\162\1\172\1"+
        "\145\1\uffff\1\146\2\172\1\uffff\1\144\1\uffff\1\172\1\146\2\uffff"+
        "\1\172\2\uffff\1\151\2\uffff\1\157\1\156\1\144\1\163\1\171\1\164"+
        "\1\172\1\141\1\uffff\1\156\1\143\1\145\1\172\1\uffff";
    static final String DFA18_acceptS =
        "\7\uffff\1\11\1\12\1\13\1\14\1\15\2\uffff\1\21\1\uffff\1\22\1\24"+
        "\1\25\1\26\2\uffff\1\17\10\uffff\1\27\2\uffff\1\30\10\uffff\2\16"+
        "\1\uffff\1\23\6\uffff\1\16\3\uffff\1\3\1\uffff\1\7\2\uffff\1\1\1"+
        "\2\1\uffff\1\10\1\20\1\uffff\1\4\1\20\10\uffff\1\5\4\uffff\1\6";
    static final String DFA18_specialS =
        "\124\uffff}>";
    static final String[] DFA18_transitionS = {
            "\2\16\1\uffff\2\16\22\uffff\1\16\1\uffff\1\23\1\22\4\uffff\1"+
            "\7\1\10\2\uffff\1\11\1\uffff\1\21\1\uffff\12\17\7\uffff\4\24"+
            "\1\15\1\24\24\20\1\uffff\1\14\2\uffff\1\20\1\uffff\1\1\3\24"+
            "\1\15\1\2\2\20\1\3\10\20\1\4\1\20\1\5\1\20\1\6\4\20\1\12\1\uffff"+
            "\1\13",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\13\20"+
            "\1\25\16\20",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\10\20"+
            "\1\27\21\20",
            "\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "",
            "",
            "",
            "",
            "",
            "\1\37\4\uffff\1\37\10\uffff\4\34\4\35\44\uffff\1\37\5\uffff"+
            "\1\37\3\uffff\1\37\7\uffff\1\37\3\uffff\1\37\1\uffff\1\37\1"+
            "\36",
            "\1\42\1\uffff\1\40\2\uffff\12\41\7\uffff\32\20\4\uffff\1\20"+
            "\1\uffff\32\20",
            "",
            "\1\21\1\uffff\12\43\13\uffff\1\21\37\uffff\1\21",
            "",
            "",
            "",
            "",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\44",
            "",
            "\1\45",
            "\1\46",
            "\1\47",
            "\1\50",
            "\1\51",
            "\10\52",
            "\10\54",
            "\12\55\7\uffff\6\55\32\uffff\6\55",
            "",
            "\12\41",
            "\12\41",
            "",
            "\1\21\1\uffff\12\43\13\uffff\1\21\37\uffff\1\21",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\10\65",
            "",
            "",
            "\12\66\7\uffff\6\66\32\uffff\6\66",
            "",
            "\1\67",
            "\1\70",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\72",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\74",
            "",
            "\12\75\7\uffff\6\75\32\uffff\6\75",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "\1\100",
            "",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\12\102\7\uffff\6\102\32\uffff\6\102",
            "",
            "",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\103\1\uffff\32"+
            "\20",
            "",
            "",
            "\1\106\6\uffff\1\107",
            "",
            "",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\117",
            "",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ALIAS | FIELD | INFO | RECORD | RECORD_BODY | RECORD_INSTANCE | TYPE | VALUE | T__23 | T__24 | T__25 | T__26 | T__27 | OCTAL_ESC | HEX_DIGIT | UNICODE_ESC | WHITESPACE | ID | INT | FLOAT | COMMENT | String | ESC_SEQ | EXPONENT );";
        }
    }
 

}
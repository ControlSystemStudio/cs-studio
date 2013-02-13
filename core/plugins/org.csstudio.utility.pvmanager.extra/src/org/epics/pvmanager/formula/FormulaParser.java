// $ANTLR 3.4 org\\epics\\pvmanager\\formula\\Formula.g 2013-02-08 14:06:03

  package org.epics.pvmanager.formula;
  import org.epics.pvmanager.expression.*;
  import static org.epics.pvmanager.ExpressionLanguage.*;
  import static org.epics.pvmanager.util.StringUtil.*;
  import static org.epics.pvmanager.vtype.ExpressionLanguage.*;
  import static org.epics.pvmanager.formula.ExpressionLanguage.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FormulaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ESC_SEQ", "EXPONENT", "FLOAT", "FULL_ID", "HEX_DIGIT", "ID", "INT", "OCTAL_ESC", "QUOTED_ID", "STRING", "UNICODE_ESC", "WS", "'%'", "'('", "')'", "'*'", "'**'", "'+'", "','", "'-'", "'/'", "'^'"
    };

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
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public FormulaParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public FormulaParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return FormulaParser.tokenNames; }
    public String getGrammarFileName() { return "org\\epics\\pvmanager\\formula\\Formula.g"; }


      @Override
    public void reportError(RecognitionException e) {
        throw new RuntimeException(e);
    }



    // $ANTLR start "singlePv"
    // org\\epics\\pvmanager\\formula\\Formula.g:27:1: singlePv returns [DesiredRateExpression<?> result] : pv EOF ;
    public final DesiredRateExpression<?> singlePv() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> pv1 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:28:5: ( pv EOF )
            // org\\epics\\pvmanager\\formula\\Formula.g:28:9: pv EOF
            {
            pushFollow(FOLLOW_pv_in_singlePv57);
            pv1=pv();

            state._fsp--;


            match(input,EOF,FOLLOW_EOF_in_singlePv59); 

            result = pv1;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "singlePv"



    // $ANTLR start "formula"
    // org\\epics\\pvmanager\\formula\\Formula.g:31:1: formula returns [DesiredRateExpression<?> result] : expression EOF ;
    public final DesiredRateExpression<?> formula() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> expression2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:32:5: ( expression EOF )
            // org\\epics\\pvmanager\\formula\\Formula.g:32:9: expression EOF
            {
            pushFollow(FOLLOW_expression_in_formula84);
            expression2=expression();

            state._fsp--;


            match(input,EOF,FOLLOW_EOF_in_formula86); 

            result = expression2;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "formula"



    // $ANTLR start "expression"
    // org\\epics\\pvmanager\\formula\\Formula.g:35:1: expression returns [DesiredRateExpression<?> result] : additiveExpression ;
    public final DesiredRateExpression<?> expression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> additiveExpression3 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:36:5: ( additiveExpression )
            // org\\epics\\pvmanager\\formula\\Formula.g:36:9: additiveExpression
            {
            pushFollow(FOLLOW_additiveExpression_in_expression111);
            additiveExpression3=additiveExpression();

            state._fsp--;


            result = additiveExpression3;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "expression"



    // $ANTLR start "additiveExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:39:1: additiveExpression returns [DesiredRateExpression<?> result] : op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* ;
    public final DesiredRateExpression<?> additiveExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:40:5: (op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:40:9: op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression138);
            op1=multiplicativeExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:41:9: ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            loop1:
            do {
                int alt1=3;
                switch ( input.LA(1) ) {
                case 21:
                    {
                    alt1=1;
                    }
                    break;
                case 23:
                    {
                    alt1=2;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:41:13: '+' op2= multiplicativeExpression
            	    {
            	    match(input,21,FOLLOW_21_in_additiveExpression154); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression158);
            	    op2=multiplicativeExpression();

            	    state._fsp--;


            	    result = addCast(result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:42:13: '-' op2= multiplicativeExpression
            	    {
            	    match(input,23,FOLLOW_23_in_additiveExpression174); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression178);
            	    op2=multiplicativeExpression();

            	    state._fsp--;


            	    result = subtractCast(result, op2);

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "additiveExpression"



    // $ANTLR start "multiplicativeExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:46:1: multiplicativeExpression returns [DesiredRateExpression<?> result] : op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )* ;
    public final DesiredRateExpression<?> multiplicativeExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:47:5: (op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:47:9: op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )*
            {
            pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression216);
            op1=exponentialExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:48:9: ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )*
            loop2:
            do {
                int alt2=4;
                switch ( input.LA(1) ) {
                case 19:
                    {
                    alt2=1;
                    }
                    break;
                case 24:
                    {
                    alt2=2;
                    }
                    break;
                case 16:
                    {
                    alt2=3;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:48:13: '*' op2= exponentialExpression
            	    {
            	    match(input,19,FOLLOW_19_in_multiplicativeExpression232); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression236);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = multiplyCast(result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:49:13: '/' op2= exponentialExpression
            	    {
            	    match(input,24,FOLLOW_24_in_multiplicativeExpression252); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression256);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = divideCast(result, op2);

            	    }
            	    break;
            	case 3 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:50:13: '%' op2= exponentialExpression
            	    {
            	    match(input,16,FOLLOW_16_in_multiplicativeExpression272); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression276);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = reminderCast(result, op2);

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "multiplicativeExpression"



    // $ANTLR start "exponentialExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:54:1: exponentialExpression returns [DesiredRateExpression<?> result] : op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )* ;
    public final DesiredRateExpression<?> exponentialExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:55:5: (op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:55:9: op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_exponentialExpression314);
            op1=unaryExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:56:9: ( '^' op2= unaryExpression | '**' op2= unaryExpression )*
            loop3:
            do {
                int alt3=3;
                switch ( input.LA(1) ) {
                case 25:
                    {
                    alt3=1;
                    }
                    break;
                case 20:
                    {
                    alt3=2;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:56:13: '^' op2= unaryExpression
            	    {
            	    match(input,25,FOLLOW_25_in_exponentialExpression330); 

            	    pushFollow(FOLLOW_unaryExpression_in_exponentialExpression334);
            	    op2=unaryExpression();

            	    state._fsp--;


            	    result = powCast(result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:57:13: '**' op2= unaryExpression
            	    {
            	    match(input,20,FOLLOW_20_in_exponentialExpression350); 

            	    pushFollow(FOLLOW_unaryExpression_in_exponentialExpression354);
            	    op2=unaryExpression();

            	    state._fsp--;


            	    result = powCast(result, op2);

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "exponentialExpression"



    // $ANTLR start "unaryExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:61:1: unaryExpression returns [DesiredRateExpression<?> result] : ( '-' op= unaryExpression |op= primary );
    public final DesiredRateExpression<?> unaryExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:62:5: ( '-' op= unaryExpression |op= primary )
            int alt4=2;
            switch ( input.LA(1) ) {
            case 23:
                {
                alt4=1;
                }
                break;
            case FLOAT:
            case FULL_ID:
            case ID:
            case INT:
            case QUOTED_ID:
            case STRING:
            case 17:
                {
                alt4=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }

            switch (alt4) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:62:9: '-' op= unaryExpression
                    {
                    match(input,23,FOLLOW_23_in_unaryExpression390); 

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression394);
                    op=unaryExpression();

                    state._fsp--;


                    result = negateCast(op);

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:63:9: op= primary
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpression408);
                    op=primary();

                    state._fsp--;


                    result = op;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "unaryExpression"



    // $ANTLR start "primary"
    // org\\epics\\pvmanager\\formula\\Formula.g:66:1: primary returns [DesiredRateExpression<?> result] : ( functionExpression | parExpression | pv | numericLiteral | stringLiteral );
    public final DesiredRateExpression<?> primary() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> functionExpression4 =null;

        DesiredRateExpression<?> parExpression5 =null;

        DesiredRateExpression<?> pv6 =null;

        DesiredRateExpression<?> numericLiteral7 =null;

        DesiredRateExpression<?> stringLiteral8 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:67:5: ( functionExpression | parExpression | pv | numericLiteral | stringLiteral )
            int alt5=5;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case 17:
                    {
                    alt5=1;
                    }
                    break;
                case EOF:
                case 16:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                    {
                    alt5=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    throw nvae;

                }

                }
                break;
            case 17:
                {
                alt5=2;
                }
                break;
            case FULL_ID:
            case QUOTED_ID:
                {
                alt5=3;
                }
                break;
            case FLOAT:
            case INT:
                {
                alt5=4;
                }
                break;
            case STRING:
                {
                alt5=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }

            switch (alt5) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:67:9: functionExpression
                    {
                    pushFollow(FOLLOW_functionExpression_in_primary433);
                    functionExpression4=functionExpression();

                    state._fsp--;


                    result = functionExpression4;

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:68:9: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary445);
                    parExpression5=parExpression();

                    state._fsp--;


                    result = parExpression5;

                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:69:9: pv
                    {
                    pushFollow(FOLLOW_pv_in_primary457);
                    pv6=pv();

                    state._fsp--;


                    result = pv6;

                    }
                    break;
                case 4 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:70:9: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_primary469);
                    numericLiteral7=numericLiteral();

                    state._fsp--;


                    result = numericLiteral7;

                    }
                    break;
                case 5 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:71:9: stringLiteral
                    {
                    pushFollow(FOLLOW_stringLiteral_in_primary481);
                    stringLiteral8=stringLiteral();

                    state._fsp--;


                    result = stringLiteral8;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "primary"



    // $ANTLR start "functionExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:74:1: functionExpression returns [DesiredRateExpression<?> result] : ID '(' op= expression ( ',' op2= expression )* ')' ;
    public final DesiredRateExpression<?> functionExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token ID9=null;
        DesiredRateExpression<?> op =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:75:5: ( ID '(' op= expression ( ',' op2= expression )* ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:75:9: ID '(' op= expression ( ',' op2= expression )* ')'
            {
            ID9=(Token)match(input,ID,FOLLOW_ID_in_functionExpression506); 

            match(input,17,FOLLOW_17_in_functionExpression508); 

            pushFollow(FOLLOW_expression_in_functionExpression512);
            op=expression();

            state._fsp--;


            String name = (ID9!=null?ID9.getText():null); DesiredRateExpressionList args = new DesiredRateExpressionListImpl().and(op);

            // org\\epics\\pvmanager\\formula\\Formula.g:76:9: ( ',' op2= expression )*
            loop6:
            do {
                int alt6=2;
                switch ( input.LA(1) ) {
                case 22:
                    {
                    alt6=1;
                    }
                    break;

                }

                switch (alt6) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:76:13: ',' op2= expression
            	    {
            	    match(input,22,FOLLOW_22_in_functionExpression528); 

            	    pushFollow(FOLLOW_expression_in_functionExpression532);
            	    op2=expression();

            	    state._fsp--;


            	    args.and(op2);

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            match(input,18,FOLLOW_18_in_functionExpression547); 

            result = function(name, args);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "functionExpression"



    // $ANTLR start "parExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:80:1: parExpression returns [DesiredRateExpression<?> result] : '(' expression ')' ;
    public final DesiredRateExpression<?> parExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> expression10 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:81:5: ( '(' expression ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:81:9: '(' expression ')'
            {
            match(input,17,FOLLOW_17_in_parExpression572); 

            pushFollow(FOLLOW_expression_in_parExpression574);
            expression10=expression();

            state._fsp--;


            match(input,18,FOLLOW_18_in_parExpression576); 

            result = expression10;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "parExpression"



    // $ANTLR start "pv"
    // org\\epics\\pvmanager\\formula\\Formula.g:84:1: pv returns [DesiredRateExpression<?> result] : ( ID | FULL_ID | QUOTED_ID );
    public final DesiredRateExpression<?> pv() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token ID11=null;
        Token FULL_ID12=null;
        Token QUOTED_ID13=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:85:5: ( ID | FULL_ID | QUOTED_ID )
            int alt7=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt7=1;
                }
                break;
            case FULL_ID:
                {
                alt7=2;
                }
                break;
            case QUOTED_ID:
                {
                alt7=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:85:9: ID
                    {
                    ID11=(Token)match(input,ID,FOLLOW_ID_in_pv601); 

                    result = cachedPv((ID11!=null?ID11.getText():null));

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:86:9: FULL_ID
                    {
                    FULL_ID12=(Token)match(input,FULL_ID,FOLLOW_FULL_ID_in_pv613); 

                    result = cachedPv((FULL_ID12!=null?FULL_ID12.getText():null));

                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:87:9: QUOTED_ID
                    {
                    QUOTED_ID13=(Token)match(input,QUOTED_ID,FOLLOW_QUOTED_ID_in_pv625); 

                    result = cachedPv(unquote((QUOTED_ID13!=null?QUOTED_ID13.getText():null)));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "pv"



    // $ANTLR start "numericLiteral"
    // org\\epics\\pvmanager\\formula\\Formula.g:90:1: numericLiteral returns [DesiredRateExpression<?> result] : ( INT | FLOAT );
    public final DesiredRateExpression<?> numericLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token INT14=null;
        Token FLOAT15=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:91:5: ( INT | FLOAT )
            int alt8=2;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt8=1;
                }
                break;
            case FLOAT:
                {
                alt8=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:91:9: INT
                    {
                    INT14=(Token)match(input,INT,FOLLOW_INT_in_numericLiteral650); 

                    result = vConst(Integer.parseInt((INT14!=null?INT14.getText():null)));

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:92:9: FLOAT
                    {
                    FLOAT15=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_numericLiteral662); 

                    result = vConst(Double.parseDouble((FLOAT15!=null?FLOAT15.getText():null)));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "numericLiteral"



    // $ANTLR start "stringLiteral"
    // org\\epics\\pvmanager\\formula\\Formula.g:95:1: stringLiteral returns [DesiredRateExpression<?> result] : STRING ;
    public final DesiredRateExpression<?> stringLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token STRING16=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:96:5: ( STRING )
            // org\\epics\\pvmanager\\formula\\Formula.g:96:7: STRING
            {
            STRING16=(Token)match(input,STRING,FOLLOW_STRING_in_stringLiteral685); 

            result = vConst(unquote((STRING16!=null?STRING16.getText():null)));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "stringLiteral"

    // Delegated rules


 

    public static final BitSet FOLLOW_pv_in_singlePv57 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_singlePv59 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_formula84 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_formula86 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_expression111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression138 = new BitSet(new long[]{0x0000000000A00002L});
    public static final BitSet FOLLOW_21_in_additiveExpression154 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression158 = new BitSet(new long[]{0x0000000000A00002L});
    public static final BitSet FOLLOW_23_in_additiveExpression174 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression178 = new BitSet(new long[]{0x0000000000A00002L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression216 = new BitSet(new long[]{0x0000000001090002L});
    public static final BitSet FOLLOW_19_in_multiplicativeExpression232 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression236 = new BitSet(new long[]{0x0000000001090002L});
    public static final BitSet FOLLOW_24_in_multiplicativeExpression252 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression256 = new BitSet(new long[]{0x0000000001090002L});
    public static final BitSet FOLLOW_16_in_multiplicativeExpression272 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression276 = new BitSet(new long[]{0x0000000001090002L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression314 = new BitSet(new long[]{0x0000000002100002L});
    public static final BitSet FOLLOW_25_in_exponentialExpression330 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression334 = new BitSet(new long[]{0x0000000002100002L});
    public static final BitSet FOLLOW_20_in_exponentialExpression350 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression354 = new BitSet(new long[]{0x0000000002100002L});
    public static final BitSet FOLLOW_23_in_unaryExpression390 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpression408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionExpression_in_primary433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pv_in_primary457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_primary469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringLiteral_in_primary481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_functionExpression506 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_functionExpression508 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_expression_in_functionExpression512 = new BitSet(new long[]{0x0000000000440000L});
    public static final BitSet FOLLOW_22_in_functionExpression528 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_expression_in_functionExpression532 = new BitSet(new long[]{0x0000000000440000L});
    public static final BitSet FOLLOW_18_in_functionExpression547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_parExpression572 = new BitSet(new long[]{0x00000000008236C0L});
    public static final BitSet FOLLOW_expression_in_parExpression574 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_parExpression576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pv601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FULL_ID_in_pv613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTED_ID_in_pv625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_numericLiteral650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_numericLiteral662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_stringLiteral685 = new BitSet(new long[]{0x0000000000000002L});

}
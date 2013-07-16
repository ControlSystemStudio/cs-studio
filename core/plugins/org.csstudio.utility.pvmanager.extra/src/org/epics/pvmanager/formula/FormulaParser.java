// $ANTLR 3.4 org\\epics\\pvmanager\\formula\\Formula.g 2013-07-10 15:08:29

  package org.epics.pvmanager.formula;
  import org.epics.pvmanager.expression.*;
  import static org.epics.pvmanager.ExpressionLanguage.*;
  import static org.epics.util.text.StringUtil.*;
  import static org.epics.pvmanager.vtype.ExpressionLanguage.*;
  import static org.epics.pvmanager.formula.ExpressionLanguage.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FormulaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ESC_SEQ", "EXPONENT", "FLOAT", "FUNCTION", "HEX_DIGIT", "INT", "OCTAL_ESC", "PV", "STRING", "UNICODE_ESC", "WS", "'!'", "'!='", "'%'", "'&&'", "'('", "')'", "'*'", "'**'", "'+'", "','", "'-'", "'/'", "':'", "'<'", "'='", "'=='", "'>'", "'?'", "'^'", "'||'"
    };

    public static final int EOF=-1;
    public static final int T__15=15;
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
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int ESC_SEQ=4;
    public static final int EXPONENT=5;
    public static final int FLOAT=6;
    public static final int FUNCTION=7;
    public static final int HEX_DIGIT=8;
    public static final int INT=9;
    public static final int OCTAL_ESC=10;
    public static final int PV=11;
    public static final int STRING=12;
    public static final int UNICODE_ESC=13;
    public static final int WS=14;

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
    // org\\epics\\pvmanager\\formula\\Formula.g:35:1: expression returns [DesiredRateExpression<?> result] : conditionalExpression ;
    public final DesiredRateExpression<?> expression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> conditionalExpression3 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:36:5: ( conditionalExpression )
            // org\\epics\\pvmanager\\formula\\Formula.g:36:9: conditionalExpression
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression111);
            conditionalExpression3=conditionalExpression();

            state._fsp--;


            result = conditionalExpression3;

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



    // $ANTLR start "conditionalExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:39:1: conditionalExpression returns [DesiredRateExpression<?> result] : op1= conditionalOrExpression ( '?' op2= expression ':' op3= conditionalExpression )? ;
    public final DesiredRateExpression<?> conditionalExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;

        DesiredRateExpression<?> op3 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:40:5: (op1= conditionalOrExpression ( '?' op2= expression ':' op3= conditionalExpression )? )
            // org\\epics\\pvmanager\\formula\\Formula.g:40:9: op1= conditionalOrExpression ( '?' op2= expression ':' op3= conditionalExpression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression138);
            op1=conditionalOrExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:41:9: ( '?' op2= expression ':' op3= conditionalExpression )?
            int alt1=2;
            switch ( input.LA(1) ) {
                case 32:
                    {
                    alt1=1;
                    }
                    break;
            }

            switch (alt1) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:41:13: '?' op2= expression ':' op3= conditionalExpression
                    {
                    match(input,32,FOLLOW_32_in_conditionalExpression154); 

                    pushFollow(FOLLOW_expression_in_conditionalExpression158);
                    op2=expression();

                    state._fsp--;


                    match(input,27,FOLLOW_27_in_conditionalExpression160); 

                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression164);
                    op3=conditionalExpression();

                    state._fsp--;


                    result = threeArgOp("?:", result, op2, op3);

                    }
                    break;

            }


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
    // $ANTLR end "conditionalExpression"



    // $ANTLR start "conditionalOrExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:45:1: conditionalOrExpression returns [DesiredRateExpression<?> result] : op1= conditionalAndExpression ( '||' op2= conditionalAndExpression )* ;
    public final DesiredRateExpression<?> conditionalOrExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:46:5: (op1= conditionalAndExpression ( '||' op2= conditionalAndExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:46:9: op1= conditionalAndExpression ( '||' op2= conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression202);
            op1=conditionalAndExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:47:9: ( '||' op2= conditionalAndExpression )*
            loop2:
            do {
                int alt2=2;
                switch ( input.LA(1) ) {
                case 34:
                    {
                    alt2=1;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:47:13: '||' op2= conditionalAndExpression
            	    {
            	    match(input,34,FOLLOW_34_in_conditionalOrExpression218); 

            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression222);
            	    op2=conditionalAndExpression();

            	    state._fsp--;


            	    result = twoArgOp("||", result, op2);

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
    // $ANTLR end "conditionalOrExpression"



    // $ANTLR start "conditionalAndExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:51:1: conditionalAndExpression returns [DesiredRateExpression<?> result] : op1= equalityExpression ( '&&' op2= equalityExpression )* ;
    public final DesiredRateExpression<?> conditionalAndExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:52:5: (op1= equalityExpression ( '&&' op2= equalityExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:52:9: op1= equalityExpression ( '&&' op2= equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_conditionalAndExpression260);
            op1=equalityExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:53:9: ( '&&' op2= equalityExpression )*
            loop3:
            do {
                int alt3=2;
                switch ( input.LA(1) ) {
                case 18:
                    {
                    alt3=1;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:53:13: '&&' op2= equalityExpression
            	    {
            	    match(input,18,FOLLOW_18_in_conditionalAndExpression276); 

            	    pushFollow(FOLLOW_equalityExpression_in_conditionalAndExpression280);
            	    op2=equalityExpression();

            	    state._fsp--;


            	    result = twoArgOp("&&", result, op2);

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
    // $ANTLR end "conditionalAndExpression"



    // $ANTLR start "equalityExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:57:1: equalityExpression returns [DesiredRateExpression<?> result] : op1= relationalExpression ( '==' op2= relationalExpression | '!=' op2= relationalExpression )* ;
    public final DesiredRateExpression<?> equalityExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:58:5: (op1= relationalExpression ( '==' op2= relationalExpression | '!=' op2= relationalExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:58:9: op1= relationalExpression ( '==' op2= relationalExpression | '!=' op2= relationalExpression )*
            {
            pushFollow(FOLLOW_relationalExpression_in_equalityExpression318);
            op1=relationalExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:59:9: ( '==' op2= relationalExpression | '!=' op2= relationalExpression )*
            loop4:
            do {
                int alt4=3;
                switch ( input.LA(1) ) {
                case 30:
                    {
                    alt4=1;
                    }
                    break;
                case 16:
                    {
                    alt4=2;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:59:13: '==' op2= relationalExpression
            	    {
            	    match(input,30,FOLLOW_30_in_equalityExpression334); 

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression338);
            	    op2=relationalExpression();

            	    state._fsp--;


            	    result = twoArgOp("==", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:60:13: '!=' op2= relationalExpression
            	    {
            	    match(input,16,FOLLOW_16_in_equalityExpression354); 

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression358);
            	    op2=relationalExpression();

            	    state._fsp--;


            	    result = twoArgOp("!=", result, op2);

            	    }
            	    break;

            	default :
            	    break loop4;
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
    // $ANTLR end "equalityExpression"



    // $ANTLR start "relationalExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:64:1: relationalExpression returns [DesiredRateExpression<?> result] : op1= additiveExpression ( '<' '=' op2= additiveExpression | '>' '=' op2= additiveExpression | '<' op2= additiveExpression | '>' op2= additiveExpression )* ;
    public final DesiredRateExpression<?> relationalExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:65:5: (op1= additiveExpression ( '<' '=' op2= additiveExpression | '>' '=' op2= additiveExpression | '<' op2= additiveExpression | '>' op2= additiveExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:65:9: op1= additiveExpression ( '<' '=' op2= additiveExpression | '>' '=' op2= additiveExpression | '<' op2= additiveExpression | '>' op2= additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression396);
            op1=additiveExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:66:9: ( '<' '=' op2= additiveExpression | '>' '=' op2= additiveExpression | '<' op2= additiveExpression | '>' op2= additiveExpression )*
            loop5:
            do {
                int alt5=5;
                switch ( input.LA(1) ) {
                case 28:
                    {
                    switch ( input.LA(2) ) {
                    case 29:
                        {
                        alt5=1;
                        }
                        break;
                    case FLOAT:
                    case FUNCTION:
                    case INT:
                    case PV:
                    case STRING:
                    case 15:
                    case 19:
                    case 25:
                        {
                        alt5=3;
                        }
                        break;

                    }

                    }
                    break;
                case 31:
                    {
                    switch ( input.LA(2) ) {
                    case 29:
                        {
                        alt5=2;
                        }
                        break;
                    case FLOAT:
                    case FUNCTION:
                    case INT:
                    case PV:
                    case STRING:
                    case 15:
                    case 19:
                    case 25:
                        {
                        alt5=4;
                        }
                        break;

                    }

                    }
                    break;

                }

                switch (alt5) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:66:13: '<' '=' op2= additiveExpression
            	    {
            	    match(input,28,FOLLOW_28_in_relationalExpression412); 

            	    match(input,29,FOLLOW_29_in_relationalExpression414); 

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression418);
            	    op2=additiveExpression();

            	    state._fsp--;


            	    result = twoArgOp("<=", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:67:13: '>' '=' op2= additiveExpression
            	    {
            	    match(input,31,FOLLOW_31_in_relationalExpression434); 

            	    match(input,29,FOLLOW_29_in_relationalExpression436); 

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression440);
            	    op2=additiveExpression();

            	    state._fsp--;


            	    result = twoArgOp(">=", result, op2);

            	    }
            	    break;
            	case 3 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:68:13: '<' op2= additiveExpression
            	    {
            	    match(input,28,FOLLOW_28_in_relationalExpression456); 

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression460);
            	    op2=additiveExpression();

            	    state._fsp--;


            	    result = twoArgOp("<", result, op2);

            	    }
            	    break;
            	case 4 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:69:13: '>' op2= additiveExpression
            	    {
            	    match(input,31,FOLLOW_31_in_relationalExpression476); 

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression480);
            	    op2=additiveExpression();

            	    state._fsp--;


            	    result = twoArgOp(">", result, op2);

            	    }
            	    break;

            	default :
            	    break loop5;
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
    // $ANTLR end "relationalExpression"



    // $ANTLR start "additiveExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:73:1: additiveExpression returns [DesiredRateExpression<?> result] : op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* ;
    public final DesiredRateExpression<?> additiveExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:74:5: (op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:74:9: op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression518);
            op1=multiplicativeExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:75:9: ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            loop6:
            do {
                int alt6=3;
                switch ( input.LA(1) ) {
                case 23:
                    {
                    alt6=1;
                    }
                    break;
                case 25:
                    {
                    alt6=2;
                    }
                    break;

                }

                switch (alt6) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:75:13: '+' op2= multiplicativeExpression
            	    {
            	    match(input,23,FOLLOW_23_in_additiveExpression534); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression538);
            	    op2=multiplicativeExpression();

            	    state._fsp--;


            	    result = twoArgOp("+", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:76:13: '-' op2= multiplicativeExpression
            	    {
            	    match(input,25,FOLLOW_25_in_additiveExpression554); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression558);
            	    op2=multiplicativeExpression();

            	    state._fsp--;


            	    result = twoArgOp("-", result, op2);

            	    }
            	    break;

            	default :
            	    break loop6;
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
    // org\\epics\\pvmanager\\formula\\Formula.g:80:1: multiplicativeExpression returns [DesiredRateExpression<?> result] : op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )* ;
    public final DesiredRateExpression<?> multiplicativeExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:81:5: (op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:81:9: op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )*
            {
            pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression596);
            op1=exponentialExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:82:9: ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )*
            loop7:
            do {
                int alt7=4;
                switch ( input.LA(1) ) {
                case 21:
                    {
                    alt7=1;
                    }
                    break;
                case 26:
                    {
                    alt7=2;
                    }
                    break;
                case 17:
                    {
                    alt7=3;
                    }
                    break;

                }

                switch (alt7) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:82:13: '*' op2= exponentialExpression
            	    {
            	    match(input,21,FOLLOW_21_in_multiplicativeExpression612); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression616);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = twoArgOp("*", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:83:13: '/' op2= exponentialExpression
            	    {
            	    match(input,26,FOLLOW_26_in_multiplicativeExpression632); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression636);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = twoArgOp("/", result, op2);

            	    }
            	    break;
            	case 3 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:84:13: '%' op2= exponentialExpression
            	    {
            	    match(input,17,FOLLOW_17_in_multiplicativeExpression652); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression656);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = twoArgOp("%", result, op2);

            	    }
            	    break;

            	default :
            	    break loop7;
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
    // org\\epics\\pvmanager\\formula\\Formula.g:88:1: exponentialExpression returns [DesiredRateExpression<?> result] : op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )* ;
    public final DesiredRateExpression<?> exponentialExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:89:5: (op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:89:9: op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_exponentialExpression694);
            op1=unaryExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:90:9: ( '^' op2= unaryExpression | '**' op2= unaryExpression )*
            loop8:
            do {
                int alt8=3;
                switch ( input.LA(1) ) {
                case 33:
                    {
                    alt8=1;
                    }
                    break;
                case 22:
                    {
                    alt8=2;
                    }
                    break;

                }

                switch (alt8) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:90:13: '^' op2= unaryExpression
            	    {
            	    match(input,33,FOLLOW_33_in_exponentialExpression710); 

            	    pushFollow(FOLLOW_unaryExpression_in_exponentialExpression714);
            	    op2=unaryExpression();

            	    state._fsp--;


            	    result = powCast(result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:91:13: '**' op2= unaryExpression
            	    {
            	    match(input,22,FOLLOW_22_in_exponentialExpression730); 

            	    pushFollow(FOLLOW_unaryExpression_in_exponentialExpression734);
            	    op2=unaryExpression();

            	    state._fsp--;


            	    result = powCast(result, op2);

            	    }
            	    break;

            	default :
            	    break loop8;
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
    // org\\epics\\pvmanager\\formula\\Formula.g:95:1: unaryExpression returns [DesiredRateExpression<?> result] : ( '-' op= unaryExpression |op= unaryExpressionNotPlusMinus );
    public final DesiredRateExpression<?> unaryExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:96:5: ( '-' op= unaryExpression |op= unaryExpressionNotPlusMinus )
            int alt9=2;
            switch ( input.LA(1) ) {
            case 25:
                {
                alt9=1;
                }
                break;
            case FLOAT:
            case FUNCTION:
            case INT:
            case PV:
            case STRING:
            case 15:
            case 19:
                {
                alt9=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:96:9: '-' op= unaryExpression
                    {
                    match(input,25,FOLLOW_25_in_unaryExpression770); 

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression774);
                    op=unaryExpression();

                    state._fsp--;


                    result = oneArgOp("-", op);

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:97:9: op= unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression788);
                    op=unaryExpressionNotPlusMinus();

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



    // $ANTLR start "unaryExpressionNotPlusMinus"
    // org\\epics\\pvmanager\\formula\\Formula.g:100:1: unaryExpressionNotPlusMinus returns [DesiredRateExpression<?> result] : ( '!' op= unaryExpression |op= primary );
    public final DesiredRateExpression<?> unaryExpressionNotPlusMinus() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:101:5: ( '!' op= unaryExpression |op= primary )
            int alt10=2;
            switch ( input.LA(1) ) {
            case 15:
                {
                alt10=1;
                }
                break;
            case FLOAT:
            case FUNCTION:
            case INT:
            case PV:
            case STRING:
            case 19:
                {
                alt10=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:101:9: '!' op= unaryExpression
                    {
                    match(input,15,FOLLOW_15_in_unaryExpressionNotPlusMinus813); 

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus817);
                    op=unaryExpression();

                    state._fsp--;


                    result = oneArgOp("!", op);

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:102:9: op= primary
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus831);
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
    // $ANTLR end "unaryExpressionNotPlusMinus"



    // $ANTLR start "primary"
    // org\\epics\\pvmanager\\formula\\Formula.g:105:1: primary returns [DesiredRateExpression<?> result] : ( functionExpression | parExpression | pv | numericLiteral | stringLiteral );
    public final DesiredRateExpression<?> primary() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> functionExpression4 =null;

        DesiredRateExpression<?> parExpression5 =null;

        DesiredRateExpression<?> pv6 =null;

        DesiredRateExpression<?> numericLiteral7 =null;

        DesiredRateExpression<?> stringLiteral8 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:106:5: ( functionExpression | parExpression | pv | numericLiteral | stringLiteral )
            int alt11=5;
            switch ( input.LA(1) ) {
            case FUNCTION:
                {
                alt11=1;
                }
                break;
            case 19:
                {
                alt11=2;
                }
                break;
            case PV:
                {
                alt11=3;
                }
                break;
            case FLOAT:
            case INT:
                {
                alt11=4;
                }
                break;
            case STRING:
                {
                alt11=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:106:9: functionExpression
                    {
                    pushFollow(FOLLOW_functionExpression_in_primary856);
                    functionExpression4=functionExpression();

                    state._fsp--;


                    result = functionExpression4;

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:107:9: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary868);
                    parExpression5=parExpression();

                    state._fsp--;


                    result = parExpression5;

                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:108:9: pv
                    {
                    pushFollow(FOLLOW_pv_in_primary880);
                    pv6=pv();

                    state._fsp--;


                    result = pv6;

                    }
                    break;
                case 4 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:109:9: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_primary892);
                    numericLiteral7=numericLiteral();

                    state._fsp--;


                    result = numericLiteral7;

                    }
                    break;
                case 5 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:110:9: stringLiteral
                    {
                    pushFollow(FOLLOW_stringLiteral_in_primary904);
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
    // org\\epics\\pvmanager\\formula\\Formula.g:113:1: functionExpression returns [DesiredRateExpression<?> result] : FUNCTION '(' op= expression ( ',' op2= expression )* ')' ;
    public final DesiredRateExpression<?> functionExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token FUNCTION9=null;
        DesiredRateExpression<?> op =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:114:5: ( FUNCTION '(' op= expression ( ',' op2= expression )* ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:114:9: FUNCTION '(' op= expression ( ',' op2= expression )* ')'
            {
            FUNCTION9=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_functionExpression929); 

            match(input,19,FOLLOW_19_in_functionExpression931); 

            pushFollow(FOLLOW_expression_in_functionExpression935);
            op=expression();

            state._fsp--;


            String name = (FUNCTION9!=null?FUNCTION9.getText():null); DesiredRateExpressionList args = new DesiredRateExpressionListImpl().and(op);

            // org\\epics\\pvmanager\\formula\\Formula.g:115:9: ( ',' op2= expression )*
            loop12:
            do {
                int alt12=2;
                switch ( input.LA(1) ) {
                case 24:
                    {
                    alt12=1;
                    }
                    break;

                }

                switch (alt12) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:115:13: ',' op2= expression
            	    {
            	    match(input,24,FOLLOW_24_in_functionExpression951); 

            	    pushFollow(FOLLOW_expression_in_functionExpression955);
            	    op2=expression();

            	    state._fsp--;


            	    args.and(op2);

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            match(input,20,FOLLOW_20_in_functionExpression970); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:119:1: parExpression returns [DesiredRateExpression<?> result] : '(' expression ')' ;
    public final DesiredRateExpression<?> parExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> expression10 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:120:5: ( '(' expression ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:120:9: '(' expression ')'
            {
            match(input,19,FOLLOW_19_in_parExpression995); 

            pushFollow(FOLLOW_expression_in_parExpression997);
            expression10=expression();

            state._fsp--;


            match(input,20,FOLLOW_20_in_parExpression999); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:123:1: pv returns [DesiredRateExpression<?> result] : PV ;
    public final DesiredRateExpression<?> pv() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token PV11=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:124:5: ( PV )
            // org\\epics\\pvmanager\\formula\\Formula.g:124:9: PV
            {
            PV11=(Token)match(input,PV,FOLLOW_PV_in_pv1024); 

            result = cachedPv(unquote((PV11!=null?PV11.getText():null)));

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
    // org\\epics\\pvmanager\\formula\\Formula.g:127:1: numericLiteral returns [DesiredRateExpression<?> result] : ( INT | FLOAT );
    public final DesiredRateExpression<?> numericLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token INT12=null;
        Token FLOAT13=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:128:5: ( INT | FLOAT )
            int alt13=2;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt13=1;
                }
                break;
            case FLOAT:
                {
                alt13=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:128:9: INT
                    {
                    INT12=(Token)match(input,INT,FOLLOW_INT_in_numericLiteral1049); 

                    result = vConst(Integer.parseInt((INT12!=null?INT12.getText():null)));

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:129:9: FLOAT
                    {
                    FLOAT13=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_numericLiteral1061); 

                    result = vConst(Double.parseDouble((FLOAT13!=null?FLOAT13.getText():null)));

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
    // org\\epics\\pvmanager\\formula\\Formula.g:132:1: stringLiteral returns [DesiredRateExpression<?> result] : STRING ;
    public final DesiredRateExpression<?> stringLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token STRING14=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:133:5: ( STRING )
            // org\\epics\\pvmanager\\formula\\Formula.g:133:7: STRING
            {
            STRING14=(Token)match(input,STRING,FOLLOW_STRING_in_stringLiteral1084); 

            result = vConst(unquote((STRING14!=null?STRING14.getText():null)));

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
    public static final BitSet FOLLOW_conditionalExpression_in_expression111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression138 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_32_in_conditionalExpression154 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression158 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_conditionalExpression160 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression202 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_conditionalOrExpression218 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression222 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_equalityExpression_in_conditionalAndExpression260 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_conditionalAndExpression276 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_equalityExpression_in_conditionalAndExpression280 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression318 = new BitSet(new long[]{0x0000000040010002L});
    public static final BitSet FOLLOW_30_in_equalityExpression334 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression338 = new BitSet(new long[]{0x0000000040010002L});
    public static final BitSet FOLLOW_16_in_equalityExpression354 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression358 = new BitSet(new long[]{0x0000000040010002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression396 = new BitSet(new long[]{0x0000000090000002L});
    public static final BitSet FOLLOW_28_in_relationalExpression412 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_relationalExpression414 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression418 = new BitSet(new long[]{0x0000000090000002L});
    public static final BitSet FOLLOW_31_in_relationalExpression434 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_relationalExpression436 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression440 = new BitSet(new long[]{0x0000000090000002L});
    public static final BitSet FOLLOW_28_in_relationalExpression456 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression460 = new BitSet(new long[]{0x0000000090000002L});
    public static final BitSet FOLLOW_31_in_relationalExpression476 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression480 = new BitSet(new long[]{0x0000000090000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression518 = new BitSet(new long[]{0x0000000002800002L});
    public static final BitSet FOLLOW_23_in_additiveExpression534 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression538 = new BitSet(new long[]{0x0000000002800002L});
    public static final BitSet FOLLOW_25_in_additiveExpression554 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression558 = new BitSet(new long[]{0x0000000002800002L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression596 = new BitSet(new long[]{0x0000000004220002L});
    public static final BitSet FOLLOW_21_in_multiplicativeExpression612 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression616 = new BitSet(new long[]{0x0000000004220002L});
    public static final BitSet FOLLOW_26_in_multiplicativeExpression632 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression636 = new BitSet(new long[]{0x0000000004220002L});
    public static final BitSet FOLLOW_17_in_multiplicativeExpression652 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression656 = new BitSet(new long[]{0x0000000004220002L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression694 = new BitSet(new long[]{0x0000000200400002L});
    public static final BitSet FOLLOW_33_in_exponentialExpression710 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression714 = new BitSet(new long[]{0x0000000200400002L});
    public static final BitSet FOLLOW_22_in_exponentialExpression730 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression734 = new BitSet(new long[]{0x0000000200400002L});
    public static final BitSet FOLLOW_25_in_unaryExpression770 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_unaryExpressionNotPlusMinus813 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionExpression_in_primary856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pv_in_primary880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_primary892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringLiteral_in_primary904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_functionExpression929 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_functionExpression931 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_expression_in_functionExpression935 = new BitSet(new long[]{0x0000000001100000L});
    public static final BitSet FOLLOW_24_in_functionExpression951 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_expression_in_functionExpression955 = new BitSet(new long[]{0x0000000001100000L});
    public static final BitSet FOLLOW_20_in_functionExpression970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_parExpression995 = new BitSet(new long[]{0x0000000002089AC0L});
    public static final BitSet FOLLOW_expression_in_parExpression997 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_parExpression999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PV_in_pv1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_numericLiteral1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_numericLiteral1061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_stringLiteral1084 = new BitSet(new long[]{0x0000000000000002L});

}
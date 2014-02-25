// $ANTLR 3.4 org\\epics\\pvmanager\\formula\\Formula.g 2014-01-27 15:02:06

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ESC_SEQ", "EXPONENT", "FLOAT", "FUNCTION", "HEX_DIGIT", "INT", "OCTAL_ESC", "PV", "STRING", "UNICODE_ESC", "WS", "'!'", "'!='", "'%'", "'&&'", "'&'", "'('", "')'", "'*'", "'**'", "'+'", "','", "'-'", "'/'", "':'", "'<'", "'='", "'=='", "'>'", "'?'", "'^'", "'|'", "'||'"
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
    public static final int T__35=35;
    public static final int T__36=36;
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
                case 33:
                    {
                    alt1=1;
                    }
                    break;
            }

            switch (alt1) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:41:13: '?' op2= expression ':' op3= conditionalExpression
                    {
                    match(input,33,FOLLOW_33_in_conditionalExpression154); 

                    pushFollow(FOLLOW_expression_in_conditionalExpression158);
                    op2=expression();

                    state._fsp--;


                    match(input,28,FOLLOW_28_in_conditionalExpression160); 

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
                case 36:
                    {
                    alt2=1;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:47:13: '||' op2= conditionalAndExpression
            	    {
            	    match(input,36,FOLLOW_36_in_conditionalOrExpression218); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:51:1: conditionalAndExpression returns [DesiredRateExpression<?> result] : op1= inclusiveOrExpression ( '&&' op2= inclusiveOrExpression )* ;
    public final DesiredRateExpression<?> conditionalAndExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:52:5: (op1= inclusiveOrExpression ( '&&' op2= inclusiveOrExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:52:9: op1= inclusiveOrExpression ( '&&' op2= inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression260);
            op1=inclusiveOrExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:53:9: ( '&&' op2= inclusiveOrExpression )*
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
            	    // org\\epics\\pvmanager\\formula\\Formula.g:53:13: '&&' op2= inclusiveOrExpression
            	    {
            	    match(input,18,FOLLOW_18_in_conditionalAndExpression276); 

            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression280);
            	    op2=inclusiveOrExpression();

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



    // $ANTLR start "inclusiveOrExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:57:1: inclusiveOrExpression returns [DesiredRateExpression<?> result] : op1= andExpression ( '|' op2= andExpression )* ;
    public final DesiredRateExpression<?> inclusiveOrExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:58:5: (op1= andExpression ( '|' op2= andExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:58:9: op1= andExpression ( '|' op2= andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_inclusiveOrExpression318);
            op1=andExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:59:9: ( '|' op2= andExpression )*
            loop4:
            do {
                int alt4=2;
                switch ( input.LA(1) ) {
                case 35:
                    {
                    alt4=1;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:59:13: '|' op2= andExpression
            	    {
            	    match(input,35,FOLLOW_35_in_inclusiveOrExpression334); 

            	    pushFollow(FOLLOW_andExpression_in_inclusiveOrExpression338);
            	    op2=andExpression();

            	    state._fsp--;


            	    result = twoArgOp("|", result, op2);

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
    // $ANTLR end "inclusiveOrExpression"



    // $ANTLR start "andExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:63:1: andExpression returns [DesiredRateExpression<?> result] : op1= equalityExpression ( '&' op2= equalityExpression )* ;
    public final DesiredRateExpression<?> andExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:64:5: (op1= equalityExpression ( '&' op2= equalityExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:64:9: op1= equalityExpression ( '&' op2= equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression376);
            op1=equalityExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:65:9: ( '&' op2= equalityExpression )*
            loop5:
            do {
                int alt5=2;
                switch ( input.LA(1) ) {
                case 19:
                    {
                    alt5=1;
                    }
                    break;

                }

                switch (alt5) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:65:13: '&' op2= equalityExpression
            	    {
            	    match(input,19,FOLLOW_19_in_andExpression392); 

            	    pushFollow(FOLLOW_equalityExpression_in_andExpression396);
            	    op2=equalityExpression();

            	    state._fsp--;


            	    result = twoArgOp("&", result, op2);

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
    // $ANTLR end "andExpression"



    // $ANTLR start "equalityExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:69:1: equalityExpression returns [DesiredRateExpression<?> result] : op1= relationalExpression ( '==' op2= relationalExpression | '!=' op2= relationalExpression )* ;
    public final DesiredRateExpression<?> equalityExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:70:5: (op1= relationalExpression ( '==' op2= relationalExpression | '!=' op2= relationalExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:70:9: op1= relationalExpression ( '==' op2= relationalExpression | '!=' op2= relationalExpression )*
            {
            pushFollow(FOLLOW_relationalExpression_in_equalityExpression434);
            op1=relationalExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:71:9: ( '==' op2= relationalExpression | '!=' op2= relationalExpression )*
            loop6:
            do {
                int alt6=3;
                switch ( input.LA(1) ) {
                case 31:
                    {
                    alt6=1;
                    }
                    break;
                case 16:
                    {
                    alt6=2;
                    }
                    break;

                }

                switch (alt6) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:71:13: '==' op2= relationalExpression
            	    {
            	    match(input,31,FOLLOW_31_in_equalityExpression450); 

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression454);
            	    op2=relationalExpression();

            	    state._fsp--;


            	    result = twoArgOp("==", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:72:13: '!=' op2= relationalExpression
            	    {
            	    match(input,16,FOLLOW_16_in_equalityExpression470); 

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression474);
            	    op2=relationalExpression();

            	    state._fsp--;


            	    result = twoArgOp("!=", result, op2);

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
    // $ANTLR end "equalityExpression"



    // $ANTLR start "relationalExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:76:1: relationalExpression returns [DesiredRateExpression<?> result] : op1= additiveExpression ( '<' '=' op2= additiveExpression | '>' '=' op2= additiveExpression | '<' op2= additiveExpression | '>' op2= additiveExpression )* ;
    public final DesiredRateExpression<?> relationalExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:77:5: (op1= additiveExpression ( '<' '=' op2= additiveExpression | '>' '=' op2= additiveExpression | '<' op2= additiveExpression | '>' op2= additiveExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:77:9: op1= additiveExpression ( '<' '=' op2= additiveExpression | '>' '=' op2= additiveExpression | '<' op2= additiveExpression | '>' op2= additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression512);
            op1=additiveExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:78:9: ( '<' '=' op2= additiveExpression | '>' '=' op2= additiveExpression | '<' op2= additiveExpression | '>' op2= additiveExpression )*
            loop7:
            do {
                int alt7=5;
                switch ( input.LA(1) ) {
                case 29:
                    {
                    switch ( input.LA(2) ) {
                    case 30:
                        {
                        alt7=1;
                        }
                        break;
                    case FLOAT:
                    case FUNCTION:
                    case INT:
                    case PV:
                    case STRING:
                    case 15:
                    case 20:
                    case 26:
                        {
                        alt7=3;
                        }
                        break;

                    }

                    }
                    break;
                case 32:
                    {
                    switch ( input.LA(2) ) {
                    case 30:
                        {
                        alt7=2;
                        }
                        break;
                    case FLOAT:
                    case FUNCTION:
                    case INT:
                    case PV:
                    case STRING:
                    case 15:
                    case 20:
                    case 26:
                        {
                        alt7=4;
                        }
                        break;

                    }

                    }
                    break;

                }

                switch (alt7) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:78:13: '<' '=' op2= additiveExpression
            	    {
            	    match(input,29,FOLLOW_29_in_relationalExpression528); 

            	    match(input,30,FOLLOW_30_in_relationalExpression530); 

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression534);
            	    op2=additiveExpression();

            	    state._fsp--;


            	    result = twoArgOp("<=", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:79:13: '>' '=' op2= additiveExpression
            	    {
            	    match(input,32,FOLLOW_32_in_relationalExpression550); 

            	    match(input,30,FOLLOW_30_in_relationalExpression552); 

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression556);
            	    op2=additiveExpression();

            	    state._fsp--;


            	    result = twoArgOp(">=", result, op2);

            	    }
            	    break;
            	case 3 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:80:13: '<' op2= additiveExpression
            	    {
            	    match(input,29,FOLLOW_29_in_relationalExpression572); 

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression576);
            	    op2=additiveExpression();

            	    state._fsp--;


            	    result = twoArgOp("<", result, op2);

            	    }
            	    break;
            	case 4 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:81:13: '>' op2= additiveExpression
            	    {
            	    match(input,32,FOLLOW_32_in_relationalExpression592); 

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression596);
            	    op2=additiveExpression();

            	    state._fsp--;


            	    result = twoArgOp(">", result, op2);

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
    // $ANTLR end "relationalExpression"



    // $ANTLR start "additiveExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:85:1: additiveExpression returns [DesiredRateExpression<?> result] : op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* ;
    public final DesiredRateExpression<?> additiveExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:86:5: (op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:86:9: op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression634);
            op1=multiplicativeExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:87:9: ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            loop8:
            do {
                int alt8=3;
                switch ( input.LA(1) ) {
                case 24:
                    {
                    alt8=1;
                    }
                    break;
                case 26:
                    {
                    alt8=2;
                    }
                    break;

                }

                switch (alt8) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:87:13: '+' op2= multiplicativeExpression
            	    {
            	    match(input,24,FOLLOW_24_in_additiveExpression650); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression654);
            	    op2=multiplicativeExpression();

            	    state._fsp--;


            	    result = twoArgOp("+", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:88:13: '-' op2= multiplicativeExpression
            	    {
            	    match(input,26,FOLLOW_26_in_additiveExpression670); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression674);
            	    op2=multiplicativeExpression();

            	    state._fsp--;


            	    result = twoArgOp("-", result, op2);

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
    // $ANTLR end "additiveExpression"



    // $ANTLR start "multiplicativeExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:92:1: multiplicativeExpression returns [DesiredRateExpression<?> result] : op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )* ;
    public final DesiredRateExpression<?> multiplicativeExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:93:5: (op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:93:9: op1= exponentialExpression ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )*
            {
            pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression712);
            op1=exponentialExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:94:9: ( '*' op2= exponentialExpression | '/' op2= exponentialExpression | '%' op2= exponentialExpression )*
            loop9:
            do {
                int alt9=4;
                switch ( input.LA(1) ) {
                case 22:
                    {
                    alt9=1;
                    }
                    break;
                case 27:
                    {
                    alt9=2;
                    }
                    break;
                case 17:
                    {
                    alt9=3;
                    }
                    break;

                }

                switch (alt9) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:94:13: '*' op2= exponentialExpression
            	    {
            	    match(input,22,FOLLOW_22_in_multiplicativeExpression728); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression732);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = twoArgOp("*", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:95:13: '/' op2= exponentialExpression
            	    {
            	    match(input,27,FOLLOW_27_in_multiplicativeExpression748); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression752);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = twoArgOp("/", result, op2);

            	    }
            	    break;
            	case 3 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:96:13: '%' op2= exponentialExpression
            	    {
            	    match(input,17,FOLLOW_17_in_multiplicativeExpression768); 

            	    pushFollow(FOLLOW_exponentialExpression_in_multiplicativeExpression772);
            	    op2=exponentialExpression();

            	    state._fsp--;


            	    result = twoArgOp("%", result, op2);

            	    }
            	    break;

            	default :
            	    break loop9;
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
    // org\\epics\\pvmanager\\formula\\Formula.g:100:1: exponentialExpression returns [DesiredRateExpression<?> result] : op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )* ;
    public final DesiredRateExpression<?> exponentialExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:101:5: (op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:101:9: op1= unaryExpression ( '^' op2= unaryExpression | '**' op2= unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_exponentialExpression810);
            op1=unaryExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:102:9: ( '^' op2= unaryExpression | '**' op2= unaryExpression )*
            loop10:
            do {
                int alt10=3;
                switch ( input.LA(1) ) {
                case 34:
                    {
                    alt10=1;
                    }
                    break;
                case 23:
                    {
                    alt10=2;
                    }
                    break;

                }

                switch (alt10) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:102:13: '^' op2= unaryExpression
            	    {
            	    match(input,34,FOLLOW_34_in_exponentialExpression826); 

            	    pushFollow(FOLLOW_unaryExpression_in_exponentialExpression830);
            	    op2=unaryExpression();

            	    state._fsp--;


            	    result = twoArgOp("^", result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:103:13: '**' op2= unaryExpression
            	    {
            	    match(input,23,FOLLOW_23_in_exponentialExpression846); 

            	    pushFollow(FOLLOW_unaryExpression_in_exponentialExpression850);
            	    op2=unaryExpression();

            	    state._fsp--;


            	    result = twoArgOp("^", result, op2);

            	    }
            	    break;

            	default :
            	    break loop10;
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
    // org\\epics\\pvmanager\\formula\\Formula.g:107:1: unaryExpression returns [DesiredRateExpression<?> result] : ( '-' op= unaryExpression |op= unaryExpressionNotPlusMinus );
    public final DesiredRateExpression<?> unaryExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:108:5: ( '-' op= unaryExpression |op= unaryExpressionNotPlusMinus )
            int alt11=2;
            switch ( input.LA(1) ) {
            case 26:
                {
                alt11=1;
                }
                break;
            case FLOAT:
            case FUNCTION:
            case INT:
            case PV:
            case STRING:
            case 15:
            case 20:
                {
                alt11=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:108:9: '-' op= unaryExpression
                    {
                    match(input,26,FOLLOW_26_in_unaryExpression886); 

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression890);
                    op=unaryExpression();

                    state._fsp--;


                    result = oneArgOp("-", op);

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:109:9: op= unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression904);
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
    // org\\epics\\pvmanager\\formula\\Formula.g:112:1: unaryExpressionNotPlusMinus returns [DesiredRateExpression<?> result] : ( '!' op= unaryExpression |op= primary );
    public final DesiredRateExpression<?> unaryExpressionNotPlusMinus() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:113:5: ( '!' op= unaryExpression |op= primary )
            int alt12=2;
            switch ( input.LA(1) ) {
            case 15:
                {
                alt12=1;
                }
                break;
            case FLOAT:
            case FUNCTION:
            case INT:
            case PV:
            case STRING:
            case 20:
                {
                alt12=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:113:9: '!' op= unaryExpression
                    {
                    match(input,15,FOLLOW_15_in_unaryExpressionNotPlusMinus929); 

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus933);
                    op=unaryExpression();

                    state._fsp--;


                    result = oneArgOp("!", op);

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:114:9: op= primary
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus947);
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
    // org\\epics\\pvmanager\\formula\\Formula.g:117:1: primary returns [DesiredRateExpression<?> result] : ( functionExpression | parExpression | pv | numericLiteral | stringLiteral );
    public final DesiredRateExpression<?> primary() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> functionExpression4 =null;

        DesiredRateExpression<?> parExpression5 =null;

        DesiredRateExpression<?> pv6 =null;

        DesiredRateExpression<?> numericLiteral7 =null;

        DesiredRateExpression<?> stringLiteral8 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:118:5: ( functionExpression | parExpression | pv | numericLiteral | stringLiteral )
            int alt13=5;
            switch ( input.LA(1) ) {
            case FUNCTION:
                {
                alt13=1;
                }
                break;
            case 20:
                {
                alt13=2;
                }
                break;
            case PV:
                {
                alt13=3;
                }
                break;
            case FLOAT:
            case INT:
                {
                alt13=4;
                }
                break;
            case STRING:
                {
                alt13=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:118:9: functionExpression
                    {
                    pushFollow(FOLLOW_functionExpression_in_primary972);
                    functionExpression4=functionExpression();

                    state._fsp--;


                    result = functionExpression4;

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:119:9: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary984);
                    parExpression5=parExpression();

                    state._fsp--;


                    result = parExpression5;

                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:120:9: pv
                    {
                    pushFollow(FOLLOW_pv_in_primary996);
                    pv6=pv();

                    state._fsp--;


                    result = pv6;

                    }
                    break;
                case 4 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:121:9: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_primary1008);
                    numericLiteral7=numericLiteral();

                    state._fsp--;


                    result = numericLiteral7;

                    }
                    break;
                case 5 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:122:9: stringLiteral
                    {
                    pushFollow(FOLLOW_stringLiteral_in_primary1020);
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
    // org\\epics\\pvmanager\\formula\\Formula.g:125:1: functionExpression returns [DesiredRateExpression<?> result] : FUNCTION '(' op= expression ( ',' op2= expression )* ')' ;
    public final DesiredRateExpression<?> functionExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token FUNCTION9=null;
        DesiredRateExpression<?> op =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:126:5: ( FUNCTION '(' op= expression ( ',' op2= expression )* ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:126:9: FUNCTION '(' op= expression ( ',' op2= expression )* ')'
            {
            FUNCTION9=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_functionExpression1045); 

            match(input,20,FOLLOW_20_in_functionExpression1047); 

            pushFollow(FOLLOW_expression_in_functionExpression1051);
            op=expression();

            state._fsp--;


            String name = (FUNCTION9!=null?FUNCTION9.getText():null); DesiredRateExpressionList args = new DesiredRateExpressionListImpl().and(op);

            // org\\epics\\pvmanager\\formula\\Formula.g:127:9: ( ',' op2= expression )*
            loop14:
            do {
                int alt14=2;
                switch ( input.LA(1) ) {
                case 25:
                    {
                    alt14=1;
                    }
                    break;

                }

                switch (alt14) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:127:13: ',' op2= expression
            	    {
            	    match(input,25,FOLLOW_25_in_functionExpression1067); 

            	    pushFollow(FOLLOW_expression_in_functionExpression1071);
            	    op2=expression();

            	    state._fsp--;


            	    args.and(op2);

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            match(input,21,FOLLOW_21_in_functionExpression1086); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:131:1: parExpression returns [DesiredRateExpression<?> result] : '(' expression ')' ;
    public final DesiredRateExpression<?> parExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> expression10 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:132:5: ( '(' expression ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:132:9: '(' expression ')'
            {
            match(input,20,FOLLOW_20_in_parExpression1111); 

            pushFollow(FOLLOW_expression_in_parExpression1113);
            expression10=expression();

            state._fsp--;


            match(input,21,FOLLOW_21_in_parExpression1115); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:135:1: pv returns [DesiredRateExpression<?> result] : PV ;
    public final DesiredRateExpression<?> pv() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token PV11=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:136:5: ( PV )
            // org\\epics\\pvmanager\\formula\\Formula.g:136:9: PV
            {
            PV11=(Token)match(input,PV,FOLLOW_PV_in_pv1140); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:139:1: numericLiteral returns [DesiredRateExpression<?> result] : ( INT | FLOAT );
    public final DesiredRateExpression<?> numericLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token INT12=null;
        Token FLOAT13=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:140:5: ( INT | FLOAT )
            int alt15=2;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt15=1;
                }
                break;
            case FLOAT:
                {
                alt15=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:140:9: INT
                    {
                    INT12=(Token)match(input,INT,FOLLOW_INT_in_numericLiteral1165); 

                    result = vConst(Integer.parseInt((INT12!=null?INT12.getText():null)));

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:141:9: FLOAT
                    {
                    FLOAT13=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_numericLiteral1177); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:144:1: stringLiteral returns [DesiredRateExpression<?> result] : STRING ;
    public final DesiredRateExpression<?> stringLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token STRING14=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:145:5: ( STRING )
            // org\\epics\\pvmanager\\formula\\Formula.g:145:7: STRING
            {
            STRING14=(Token)match(input,STRING,FOLLOW_STRING_in_stringLiteral1200); 

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
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression138 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_33_in_conditionalExpression154 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression158 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_conditionalExpression160 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression202 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_conditionalOrExpression218 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression222 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression260 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_conditionalAndExpression276 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression280 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_andExpression_in_inclusiveOrExpression318 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_35_in_inclusiveOrExpression334 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_andExpression_in_inclusiveOrExpression338 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression376 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_andExpression392 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression396 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression434 = new BitSet(new long[]{0x0000000080010002L});
    public static final BitSet FOLLOW_31_in_equalityExpression450 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression454 = new BitSet(new long[]{0x0000000080010002L});
    public static final BitSet FOLLOW_16_in_equalityExpression470 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression474 = new BitSet(new long[]{0x0000000080010002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression512 = new BitSet(new long[]{0x0000000120000002L});
    public static final BitSet FOLLOW_29_in_relationalExpression528 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_relationalExpression530 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression534 = new BitSet(new long[]{0x0000000120000002L});
    public static final BitSet FOLLOW_32_in_relationalExpression550 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_relationalExpression552 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression556 = new BitSet(new long[]{0x0000000120000002L});
    public static final BitSet FOLLOW_29_in_relationalExpression572 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression576 = new BitSet(new long[]{0x0000000120000002L});
    public static final BitSet FOLLOW_32_in_relationalExpression592 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression596 = new BitSet(new long[]{0x0000000120000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression634 = new BitSet(new long[]{0x0000000005000002L});
    public static final BitSet FOLLOW_24_in_additiveExpression650 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression654 = new BitSet(new long[]{0x0000000005000002L});
    public static final BitSet FOLLOW_26_in_additiveExpression670 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression674 = new BitSet(new long[]{0x0000000005000002L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression712 = new BitSet(new long[]{0x0000000008420002L});
    public static final BitSet FOLLOW_22_in_multiplicativeExpression728 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression732 = new BitSet(new long[]{0x0000000008420002L});
    public static final BitSet FOLLOW_27_in_multiplicativeExpression748 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression752 = new BitSet(new long[]{0x0000000008420002L});
    public static final BitSet FOLLOW_17_in_multiplicativeExpression768 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_exponentialExpression_in_multiplicativeExpression772 = new BitSet(new long[]{0x0000000008420002L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression810 = new BitSet(new long[]{0x0000000400800002L});
    public static final BitSet FOLLOW_34_in_exponentialExpression826 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression830 = new BitSet(new long[]{0x0000000400800002L});
    public static final BitSet FOLLOW_23_in_exponentialExpression846 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_unaryExpression_in_exponentialExpression850 = new BitSet(new long[]{0x0000000400800002L});
    public static final BitSet FOLLOW_26_in_unaryExpression886 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_unaryExpressionNotPlusMinus929 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionExpression_in_primary972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pv_in_primary996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_primary1008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringLiteral_in_primary1020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_functionExpression1045 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_functionExpression1047 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_expression_in_functionExpression1051 = new BitSet(new long[]{0x0000000002200000L});
    public static final BitSet FOLLOW_25_in_functionExpression1067 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_expression_in_functionExpression1071 = new BitSet(new long[]{0x0000000002200000L});
    public static final BitSet FOLLOW_21_in_functionExpression1086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_parExpression1111 = new BitSet(new long[]{0x0000000004109AC0L});
    public static final BitSet FOLLOW_expression_in_parExpression1113 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_parExpression1115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PV_in_pv1140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_numericLiteral1165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_numericLiteral1177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_stringLiteral1200 = new BitSet(new long[]{0x0000000000000002L});

}
// $ANTLR 3.4 org\\epics\\pvmanager\\formula\\Formula.g 2012-08-06 15:06:03

  package org.epics.pvmanager.formula;
  import org.epics.pvmanager.expression.*;
  import static org.epics.pvmanager.ExpressionLanguage.*;
  import static org.epics.pvmanager.data.ExpressionLanguage.*;
  import static org.epics.pvmanager.formula.ExpressionLanguage.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FormulaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ESC_SEQ", "EXPONENT", "FLOAT", "HEX_DIGIT", "ID", "INT", "OCTAL_ESC", "QUOTED_ID", "STRING", "UNICODE_ESC", "WS", "'%'", "'('", "')'", "'*'", "'+'", "'-'", "'/'"
    };

    public static final int EOF=-1;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int ESC_SEQ=4;
    public static final int EXPONENT=5;
    public static final int FLOAT=6;
    public static final int HEX_DIGIT=7;
    public static final int ID=8;
    public static final int INT=9;
    public static final int OCTAL_ESC=10;
    public static final int QUOTED_ID=11;
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



    // $ANTLR start "formula"
    // org\\epics\\pvmanager\\formula\\Formula.g:19:1: formula returns [DesiredRateExpression<?> result] : expression EOF ;
    public final DesiredRateExpression<?> formula() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> expression1 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:20:5: ( expression EOF )
            // org\\epics\\pvmanager\\formula\\Formula.g:20:9: expression EOF
            {
            pushFollow(FOLLOW_expression_in_formula51);
            expression1=expression();

            state._fsp--;


            match(input,EOF,FOLLOW_EOF_in_formula53); 

            result = expression1;

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
    // org\\epics\\pvmanager\\formula\\Formula.g:23:1: expression returns [DesiredRateExpression<?> result] : additiveExpression ;
    public final DesiredRateExpression<?> expression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> additiveExpression2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:24:5: ( additiveExpression )
            // org\\epics\\pvmanager\\formula\\Formula.g:24:9: additiveExpression
            {
            pushFollow(FOLLOW_additiveExpression_in_expression78);
            additiveExpression2=additiveExpression();

            state._fsp--;


            result = additiveExpression2;

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
    // org\\epics\\pvmanager\\formula\\Formula.g:27:1: additiveExpression returns [DesiredRateExpression<?> result] : op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* ;
    public final DesiredRateExpression<?> additiveExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:28:5: (op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:28:9: op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression105);
            op1=multiplicativeExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:29:9: ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            loop1:
            do {
                int alt1=3;
                switch ( input.LA(1) ) {
                case 19:
                    {
                    alt1=1;
                    }
                    break;
                case 20:
                    {
                    alt1=2;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:29:13: '+' op2= multiplicativeExpression
            	    {
            	    match(input,19,FOLLOW_19_in_additiveExpression121); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression125);
            	    op2=multiplicativeExpression();

            	    state._fsp--;


            	    result = addCast(result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:30:13: '-' op2= multiplicativeExpression
            	    {
            	    match(input,20,FOLLOW_20_in_additiveExpression141); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression145);
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
    // org\\epics\\pvmanager\\formula\\Formula.g:34:1: multiplicativeExpression returns [DesiredRateExpression<?> result] : op1= primary ( '*' op2= primary | '/' op2= primary | '%' op2= primary )* ;
    public final DesiredRateExpression<?> multiplicativeExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:35:5: (op1= primary ( '*' op2= primary | '/' op2= primary | '%' op2= primary )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:35:9: op1= primary ( '*' op2= primary | '/' op2= primary | '%' op2= primary )*
            {
            pushFollow(FOLLOW_primary_in_multiplicativeExpression183);
            op1=primary();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:36:9: ( '*' op2= primary | '/' op2= primary | '%' op2= primary )*
            loop2:
            do {
                int alt2=4;
                switch ( input.LA(1) ) {
                case 18:
                    {
                    alt2=1;
                    }
                    break;
                case 21:
                    {
                    alt2=2;
                    }
                    break;
                case 15:
                    {
                    alt2=3;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:36:13: '*' op2= primary
            	    {
            	    match(input,18,FOLLOW_18_in_multiplicativeExpression199); 

            	    pushFollow(FOLLOW_primary_in_multiplicativeExpression203);
            	    op2=primary();

            	    state._fsp--;


            	    result = multiplyCast(result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:37:13: '/' op2= primary
            	    {
            	    match(input,21,FOLLOW_21_in_multiplicativeExpression219); 

            	    pushFollow(FOLLOW_primary_in_multiplicativeExpression223);
            	    op2=primary();

            	    state._fsp--;


            	    result = divideCast(result, op2);

            	    }
            	    break;
            	case 3 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:38:13: '%' op2= primary
            	    {
            	    match(input,15,FOLLOW_15_in_multiplicativeExpression239); 

            	    pushFollow(FOLLOW_primary_in_multiplicativeExpression243);
            	    op2=primary();

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



    // $ANTLR start "primary"
    // org\\epics\\pvmanager\\formula\\Formula.g:42:1: primary returns [DesiredRateExpression<?> result] : ( parExpression | pv | numericLiteral | stringLiteral );
    public final DesiredRateExpression<?> primary() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> parExpression3 =null;

        DesiredRateExpression<?> pv4 =null;

        DesiredRateExpression<?> numericLiteral5 =null;

        DesiredRateExpression<?> stringLiteral6 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:43:5: ( parExpression | pv | numericLiteral | stringLiteral )
            int alt3=4;
            switch ( input.LA(1) ) {
            case 16:
                {
                alt3=1;
                }
                break;
            case ID:
            case QUOTED_ID:
                {
                alt3=2;
                }
                break;
            case FLOAT:
            case INT:
                {
                alt3=3;
                }
                break;
            case STRING:
                {
                alt3=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }

            switch (alt3) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:43:9: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary283);
                    parExpression3=parExpression();

                    state._fsp--;


                    result = parExpression3;

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:44:9: pv
                    {
                    pushFollow(FOLLOW_pv_in_primary295);
                    pv4=pv();

                    state._fsp--;


                    result = pv4;

                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:45:9: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_primary307);
                    numericLiteral5=numericLiteral();

                    state._fsp--;


                    result = numericLiteral5;

                    }
                    break;
                case 4 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:46:9: stringLiteral
                    {
                    pushFollow(FOLLOW_stringLiteral_in_primary319);
                    stringLiteral6=stringLiteral();

                    state._fsp--;


                    result = stringLiteral6;

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



    // $ANTLR start "parExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:49:1: parExpression returns [DesiredRateExpression<?> result] : '(' expression ')' ;
    public final DesiredRateExpression<?> parExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> expression7 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:50:5: ( '(' expression ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:50:9: '(' expression ')'
            {
            match(input,16,FOLLOW_16_in_parExpression344); 

            pushFollow(FOLLOW_expression_in_parExpression346);
            expression7=expression();

            state._fsp--;


            match(input,17,FOLLOW_17_in_parExpression348); 

            result = expression7;

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
    // org\\epics\\pvmanager\\formula\\Formula.g:53:1: pv returns [DesiredRateExpression<?> result] : ( ID | QUOTED_ID );
    public final DesiredRateExpression<?> pv() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token ID8=null;
        Token QUOTED_ID9=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:54:5: ( ID | QUOTED_ID )
            int alt4=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt4=1;
                }
                break;
            case QUOTED_ID:
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
                    // org\\epics\\pvmanager\\formula\\Formula.g:54:9: ID
                    {
                    ID8=(Token)match(input,ID,FOLLOW_ID_in_pv373); 

                    result = cachedPv((ID8!=null?ID8.getText():null));

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:55:9: QUOTED_ID
                    {
                    QUOTED_ID9=(Token)match(input,QUOTED_ID,FOLLOW_QUOTED_ID_in_pv385); 

                    result = cachedPv(((QUOTED_ID9!=null?QUOTED_ID9.getText():null)).substring(1,((QUOTED_ID9!=null?QUOTED_ID9.getText():null)).length() - 1));

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
    // org\\epics\\pvmanager\\formula\\Formula.g:58:1: numericLiteral returns [DesiredRateExpression<?> result] : ( INT | FLOAT );
    public final DesiredRateExpression<?> numericLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token INT10=null;
        Token FLOAT11=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:59:5: ( INT | FLOAT )
            int alt5=2;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt5=1;
                }
                break;
            case FLOAT:
                {
                alt5=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }

            switch (alt5) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:59:9: INT
                    {
                    INT10=(Token)match(input,INT,FOLLOW_INT_in_numericLiteral410); 

                    result = vConst(Integer.parseInt((INT10!=null?INT10.getText():null)));

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:60:9: FLOAT
                    {
                    FLOAT11=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_numericLiteral422); 

                    result = vConst(Double.parseDouble((FLOAT11!=null?FLOAT11.getText():null)));

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
    // org\\epics\\pvmanager\\formula\\Formula.g:63:1: stringLiteral returns [DesiredRateExpression<?> result] : STRING ;
    public final DesiredRateExpression<?> stringLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:64:5: ( STRING )
            // org\\epics\\pvmanager\\formula\\Formula.g:64:7: STRING
            {
            match(input,STRING,FOLLOW_STRING_in_stringLiteral445); 

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


 

    public static final BitSet FOLLOW_expression_in_formula51 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_formula53 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_expression78 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression105 = new BitSet(new long[]{0x0000000000180002L});
    public static final BitSet FOLLOW_19_in_additiveExpression121 = new BitSet(new long[]{0x0000000000011B40L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression125 = new BitSet(new long[]{0x0000000000180002L});
    public static final BitSet FOLLOW_20_in_additiveExpression141 = new BitSet(new long[]{0x0000000000011B40L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression145 = new BitSet(new long[]{0x0000000000180002L});
    public static final BitSet FOLLOW_primary_in_multiplicativeExpression183 = new BitSet(new long[]{0x0000000000248002L});
    public static final BitSet FOLLOW_18_in_multiplicativeExpression199 = new BitSet(new long[]{0x0000000000011B40L});
    public static final BitSet FOLLOW_primary_in_multiplicativeExpression203 = new BitSet(new long[]{0x0000000000248002L});
    public static final BitSet FOLLOW_21_in_multiplicativeExpression219 = new BitSet(new long[]{0x0000000000011B40L});
    public static final BitSet FOLLOW_primary_in_multiplicativeExpression223 = new BitSet(new long[]{0x0000000000248002L});
    public static final BitSet FOLLOW_15_in_multiplicativeExpression239 = new BitSet(new long[]{0x0000000000011B40L});
    public static final BitSet FOLLOW_primary_in_multiplicativeExpression243 = new BitSet(new long[]{0x0000000000248002L});
    public static final BitSet FOLLOW_parExpression_in_primary283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pv_in_primary295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_primary307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringLiteral_in_primary319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_parExpression344 = new BitSet(new long[]{0x0000000000011B40L});
    public static final BitSet FOLLOW_expression_in_parExpression346 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_parExpression348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pv373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTED_ID_in_pv385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_numericLiteral410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_numericLiteral422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_stringLiteral445 = new BitSet(new long[]{0x0000000000000002L});

}
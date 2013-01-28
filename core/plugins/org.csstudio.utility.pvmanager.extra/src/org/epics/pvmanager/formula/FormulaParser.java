// $ANTLR 3.4 org\\epics\\pvmanager\\formula\\Formula.g 2013-01-28 11:42:40

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ESC_SEQ", "EXPONENT", "FLOAT", "FULL_ID", "HEX_DIGIT", "ID", "INT", "OCTAL_ESC", "QUOTED_ID", "STRING", "UNICODE_ESC", "WS", "'%'", "'('", "')'", "'*'", "'+'", "','", "'-'", "'/'"
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



    // $ANTLR start "formula"
    // org\\epics\\pvmanager\\formula\\Formula.g:20:1: formula returns [DesiredRateExpression<?> result] : expression EOF ;
    public final DesiredRateExpression<?> formula() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> expression1 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:21:5: ( expression EOF )
            // org\\epics\\pvmanager\\formula\\Formula.g:21:9: expression EOF
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
    // org\\epics\\pvmanager\\formula\\Formula.g:24:1: expression returns [DesiredRateExpression<?> result] : additiveExpression ;
    public final DesiredRateExpression<?> expression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> additiveExpression2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:25:5: ( additiveExpression )
            // org\\epics\\pvmanager\\formula\\Formula.g:25:9: additiveExpression
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
    // org\\epics\\pvmanager\\formula\\Formula.g:28:1: additiveExpression returns [DesiredRateExpression<?> result] : op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* ;
    public final DesiredRateExpression<?> additiveExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:29:5: (op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:29:9: op1= multiplicativeExpression ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression105);
            op1=multiplicativeExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:30:9: ( '+' op2= multiplicativeExpression | '-' op2= multiplicativeExpression )*
            loop1:
            do {
                int alt1=3;
                switch ( input.LA(1) ) {
                case 20:
                    {
                    alt1=1;
                    }
                    break;
                case 22:
                    {
                    alt1=2;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:30:13: '+' op2= multiplicativeExpression
            	    {
            	    match(input,20,FOLLOW_20_in_additiveExpression121); 

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression125);
            	    op2=multiplicativeExpression();

            	    state._fsp--;


            	    result = addCast(result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:31:13: '-' op2= multiplicativeExpression
            	    {
            	    match(input,22,FOLLOW_22_in_additiveExpression141); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:35:1: multiplicativeExpression returns [DesiredRateExpression<?> result] : op1= unaryExpression ( '*' op2= unaryExpression | '/' op2= unaryExpression | '%' op2= unaryExpression )* ;
    public final DesiredRateExpression<?> multiplicativeExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op1 =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:36:5: (op1= unaryExpression ( '*' op2= unaryExpression | '/' op2= unaryExpression | '%' op2= unaryExpression )* )
            // org\\epics\\pvmanager\\formula\\Formula.g:36:9: op1= unaryExpression ( '*' op2= unaryExpression | '/' op2= unaryExpression | '%' op2= unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression183);
            op1=unaryExpression();

            state._fsp--;


            result = op1;

            // org\\epics\\pvmanager\\formula\\Formula.g:37:9: ( '*' op2= unaryExpression | '/' op2= unaryExpression | '%' op2= unaryExpression )*
            loop2:
            do {
                int alt2=4;
                switch ( input.LA(1) ) {
                case 19:
                    {
                    alt2=1;
                    }
                    break;
                case 23:
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
            	    // org\\epics\\pvmanager\\formula\\Formula.g:37:13: '*' op2= unaryExpression
            	    {
            	    match(input,19,FOLLOW_19_in_multiplicativeExpression199); 

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression203);
            	    op2=unaryExpression();

            	    state._fsp--;


            	    result = multiplyCast(result, op2);

            	    }
            	    break;
            	case 2 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:38:13: '/' op2= unaryExpression
            	    {
            	    match(input,23,FOLLOW_23_in_multiplicativeExpression219); 

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression223);
            	    op2=unaryExpression();

            	    state._fsp--;


            	    result = divideCast(result, op2);

            	    }
            	    break;
            	case 3 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:39:13: '%' op2= unaryExpression
            	    {
            	    match(input,16,FOLLOW_16_in_multiplicativeExpression239); 

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression243);
            	    op2=unaryExpression();

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



    // $ANTLR start "unaryExpression"
    // org\\epics\\pvmanager\\formula\\Formula.g:43:1: unaryExpression returns [DesiredRateExpression<?> result] : ( '-' op= unaryExpression |op= primary );
    public final DesiredRateExpression<?> unaryExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> op =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:44:5: ( '-' op= unaryExpression |op= primary )
            int alt3=2;
            switch ( input.LA(1) ) {
            case 22:
                {
                alt3=1;
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
                alt3=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }

            switch (alt3) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:44:9: '-' op= unaryExpression
                    {
                    match(input,22,FOLLOW_22_in_unaryExpression279); 

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression283);
                    op=unaryExpression();

                    state._fsp--;


                    result = negateCast(op);

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:45:9: op= primary
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpression297);
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
    // org\\epics\\pvmanager\\formula\\Formula.g:48:1: primary returns [DesiredRateExpression<?> result] : ( functionExpression | parExpression | pv | numericLiteral | stringLiteral );
    public final DesiredRateExpression<?> primary() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> functionExpression3 =null;

        DesiredRateExpression<?> parExpression4 =null;

        DesiredRateExpression<?> pv5 =null;

        DesiredRateExpression<?> numericLiteral6 =null;

        DesiredRateExpression<?> stringLiteral7 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:49:5: ( functionExpression | parExpression | pv | numericLiteral | stringLiteral )
            int alt4=5;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case 17:
                    {
                    alt4=1;
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
                    {
                    alt4=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 1, input);

                    throw nvae;

                }

                }
                break;
            case 17:
                {
                alt4=2;
                }
                break;
            case FULL_ID:
            case QUOTED_ID:
                {
                alt4=3;
                }
                break;
            case FLOAT:
            case INT:
                {
                alt4=4;
                }
                break;
            case STRING:
                {
                alt4=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }

            switch (alt4) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:49:9: functionExpression
                    {
                    pushFollow(FOLLOW_functionExpression_in_primary322);
                    functionExpression3=functionExpression();

                    state._fsp--;


                    result = functionExpression3;

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:50:9: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary334);
                    parExpression4=parExpression();

                    state._fsp--;


                    result = parExpression4;

                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:51:9: pv
                    {
                    pushFollow(FOLLOW_pv_in_primary346);
                    pv5=pv();

                    state._fsp--;


                    result = pv5;

                    }
                    break;
                case 4 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:52:9: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_primary358);
                    numericLiteral6=numericLiteral();

                    state._fsp--;


                    result = numericLiteral6;

                    }
                    break;
                case 5 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:53:9: stringLiteral
                    {
                    pushFollow(FOLLOW_stringLiteral_in_primary370);
                    stringLiteral7=stringLiteral();

                    state._fsp--;


                    result = stringLiteral7;

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
    // org\\epics\\pvmanager\\formula\\Formula.g:56:1: functionExpression returns [DesiredRateExpression<?> result] : ID '(' op= expression ( ',' op2= expression )* ')' ;
    public final DesiredRateExpression<?> functionExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token ID8=null;
        DesiredRateExpression<?> op =null;

        DesiredRateExpression<?> op2 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:57:5: ( ID '(' op= expression ( ',' op2= expression )* ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:57:9: ID '(' op= expression ( ',' op2= expression )* ')'
            {
            ID8=(Token)match(input,ID,FOLLOW_ID_in_functionExpression395); 

            match(input,17,FOLLOW_17_in_functionExpression397); 

            pushFollow(FOLLOW_expression_in_functionExpression401);
            op=expression();

            state._fsp--;


            String name = (ID8!=null?ID8.getText():null); DesiredRateExpressionList args = new DesiredRateExpressionListImpl().and(op);

            // org\\epics\\pvmanager\\formula\\Formula.g:58:9: ( ',' op2= expression )*
            loop5:
            do {
                int alt5=2;
                switch ( input.LA(1) ) {
                case 21:
                    {
                    alt5=1;
                    }
                    break;

                }

                switch (alt5) {
            	case 1 :
            	    // org\\epics\\pvmanager\\formula\\Formula.g:58:13: ',' op2= expression
            	    {
            	    match(input,21,FOLLOW_21_in_functionExpression417); 

            	    pushFollow(FOLLOW_expression_in_functionExpression421);
            	    op2=expression();

            	    state._fsp--;


            	    args.and(op2);

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            match(input,18,FOLLOW_18_in_functionExpression436); 

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
    // org\\epics\\pvmanager\\formula\\Formula.g:62:1: parExpression returns [DesiredRateExpression<?> result] : '(' expression ')' ;
    public final DesiredRateExpression<?> parExpression() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        DesiredRateExpression<?> expression9 =null;


        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:63:5: ( '(' expression ')' )
            // org\\epics\\pvmanager\\formula\\Formula.g:63:9: '(' expression ')'
            {
            match(input,17,FOLLOW_17_in_parExpression461); 

            pushFollow(FOLLOW_expression_in_parExpression463);
            expression9=expression();

            state._fsp--;


            match(input,18,FOLLOW_18_in_parExpression465); 

            result = expression9;

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
    // org\\epics\\pvmanager\\formula\\Formula.g:66:1: pv returns [DesiredRateExpression<?> result] : ( ID | FULL_ID | QUOTED_ID );
    public final DesiredRateExpression<?> pv() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token ID10=null;
        Token FULL_ID11=null;
        Token QUOTED_ID12=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:67:5: ( ID | FULL_ID | QUOTED_ID )
            int alt6=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt6=1;
                }
                break;
            case FULL_ID:
                {
                alt6=2;
                }
                break;
            case QUOTED_ID:
                {
                alt6=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }

            switch (alt6) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:67:9: ID
                    {
                    ID10=(Token)match(input,ID,FOLLOW_ID_in_pv490); 

                    result = cachedPv((ID10!=null?ID10.getText():null));

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:68:9: FULL_ID
                    {
                    FULL_ID11=(Token)match(input,FULL_ID,FOLLOW_FULL_ID_in_pv502); 

                    result = cachedPv((FULL_ID11!=null?FULL_ID11.getText():null));

                    }
                    break;
                case 3 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:69:9: QUOTED_ID
                    {
                    QUOTED_ID12=(Token)match(input,QUOTED_ID,FOLLOW_QUOTED_ID_in_pv514); 

                    result = cachedPv(unquote((QUOTED_ID12!=null?QUOTED_ID12.getText():null)));

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
    // org\\epics\\pvmanager\\formula\\Formula.g:72:1: numericLiteral returns [DesiredRateExpression<?> result] : ( INT | FLOAT );
    public final DesiredRateExpression<?> numericLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token INT13=null;
        Token FLOAT14=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:73:5: ( INT | FLOAT )
            int alt7=2;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt7=1;
                }
                break;
            case FLOAT:
                {
                alt7=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:73:9: INT
                    {
                    INT13=(Token)match(input,INT,FOLLOW_INT_in_numericLiteral539); 

                    result = vConst(Integer.parseInt((INT13!=null?INT13.getText():null)));

                    }
                    break;
                case 2 :
                    // org\\epics\\pvmanager\\formula\\Formula.g:74:9: FLOAT
                    {
                    FLOAT14=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_numericLiteral551); 

                    result = vConst(Double.parseDouble((FLOAT14!=null?FLOAT14.getText():null)));

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
    // org\\epics\\pvmanager\\formula\\Formula.g:77:1: stringLiteral returns [DesiredRateExpression<?> result] : STRING ;
    public final DesiredRateExpression<?> stringLiteral() throws RecognitionException {
        DesiredRateExpression<?> result = null;


        Token STRING15=null;

        try {
            // org\\epics\\pvmanager\\formula\\Formula.g:78:5: ( STRING )
            // org\\epics\\pvmanager\\formula\\Formula.g:78:7: STRING
            {
            STRING15=(Token)match(input,STRING,FOLLOW_STRING_in_stringLiteral574); 

            result = vConst(unquote((STRING15!=null?STRING15.getText():null)));

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
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression105 = new BitSet(new long[]{0x0000000000500002L});
    public static final BitSet FOLLOW_20_in_additiveExpression121 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression125 = new BitSet(new long[]{0x0000000000500002L});
    public static final BitSet FOLLOW_22_in_additiveExpression141 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression145 = new BitSet(new long[]{0x0000000000500002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression183 = new BitSet(new long[]{0x0000000000890002L});
    public static final BitSet FOLLOW_19_in_multiplicativeExpression199 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression203 = new BitSet(new long[]{0x0000000000890002L});
    public static final BitSet FOLLOW_23_in_multiplicativeExpression219 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression223 = new BitSet(new long[]{0x0000000000890002L});
    public static final BitSet FOLLOW_16_in_multiplicativeExpression239 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression243 = new BitSet(new long[]{0x0000000000890002L});
    public static final BitSet FOLLOW_22_in_unaryExpression279 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpression297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionExpression_in_primary322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pv_in_primary346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_primary358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringLiteral_in_primary370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_functionExpression395 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_functionExpression397 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_expression_in_functionExpression401 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_21_in_functionExpression417 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_expression_in_functionExpression421 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_18_in_functionExpression436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_parExpression461 = new BitSet(new long[]{0x00000000004236C0L});
    public static final BitSet FOLLOW_expression_in_parExpression463 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_parExpression465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pv490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FULL_ID_in_pv502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTED_ID_in_pv514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_numericLiteral539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_numericLiteral551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_stringLiteral574 = new BitSet(new long[]{0x0000000000000002L});

}
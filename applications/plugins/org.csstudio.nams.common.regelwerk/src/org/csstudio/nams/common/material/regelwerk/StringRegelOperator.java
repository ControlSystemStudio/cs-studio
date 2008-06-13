/**
 * 
 */
package org.csstudio.nams.common.material.regelwerk;

public enum StringRegelOperator {
	OPERATOR_TEXT_EQUAL, OPERATOR_TEXT_NOT_EQUAL,

	OPERATOR_NUMERIC_LT, OPERATOR_NUMERIC_LT_EQUAL, OPERATOR_NUMERIC_EQUAL, OPERATOR_NUMERIC_GT_EQUAL, OPERATOR_NUMERIC_GT, OPERATOR_NUMERIC_NOT_EQUAL,

	OPERATOR_TIME_BEFORE, OPERATOR_TIME_BEFORE_EQUAL, OPERATOR_TIME_EQUAL, OPERATOR_TIME_AFTER_EQUAL, OPERATOR_TIME_AFTER, OPERATOR_TIME_NOT_EQUAL;
	
	public static StringRegelOperator valueOf(short value) {
		switch (value){
		case 1: return OPERATOR_TEXT_EQUAL;
		case 2: return OPERATOR_TEXT_NOT_EQUAL;
		case 3: return OPERATOR_NUMERIC_LT;
		case 4: return OPERATOR_NUMERIC_LT_EQUAL ;
		case 5: return OPERATOR_NUMERIC_EQUAL ;
		case 6: return OPERATOR_NUMERIC_GT_EQUAL;
		case 7: return OPERATOR_NUMERIC_GT;
		case 8: return OPERATOR_NUMERIC_NOT_EQUAL ;

		case 9: return OPERATOR_TIME_BEFORE ;
		case 10: return OPERATOR_TIME_BEFORE_EQUAL ;
		case 11: return OPERATOR_TIME_EQUAL ;
		case 12: return OPERATOR_TIME_AFTER_EQUAL ;
		case 13: return OPERATOR_TIME_AFTER ;
		case 14: return OPERATOR_TIME_NOT_EQUAL ;
		default: throw new IllegalArgumentException();
		}
		}
	}
	
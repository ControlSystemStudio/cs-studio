
/**
 * 
 */

package org.csstudio.nams.common.material.regelwerk;

public enum StringRegelOperator {
	OPERATOR_TEXT_EQUAL, OPERATOR_TEXT_NOT_EQUAL,

	OPERATOR_NUMERIC_LT, OPERATOR_NUMERIC_LT_EQUAL, OPERATOR_NUMERIC_EQUAL, OPERATOR_NUMERIC_GT_EQUAL, OPERATOR_NUMERIC_GT, OPERATOR_NUMERIC_NOT_EQUAL,

	OPERATOR_TIME_BEFORE, OPERATOR_TIME_BEFORE_EQUAL, OPERATOR_TIME_EQUAL, OPERATOR_TIME_AFTER_EQUAL, OPERATOR_TIME_AFTER, OPERATOR_TIME_NOT_EQUAL;

	public static StringRegelOperator valueOf(final short value) {
		switch (value) {
		case 1:
			return OPERATOR_TEXT_EQUAL;
		case 2:
			return OPERATOR_TEXT_NOT_EQUAL;
		case 3:
			return OPERATOR_NUMERIC_LT;
		case 4:
			return OPERATOR_NUMERIC_LT_EQUAL;
		case 5:
			return OPERATOR_NUMERIC_EQUAL;
		case 6:
			return OPERATOR_NUMERIC_GT_EQUAL;
		case 7:
			return OPERATOR_NUMERIC_GT;
		case 8:
			return OPERATOR_NUMERIC_NOT_EQUAL;

		case 9:
			return OPERATOR_TIME_BEFORE;
		case 10:
			return OPERATOR_TIME_BEFORE_EQUAL;
		case 11:
			return OPERATOR_TIME_EQUAL;
		case 12:
			return OPERATOR_TIME_AFTER_EQUAL;
		case 13:
			return OPERATOR_TIME_AFTER;
		case 14:
			return OPERATOR_TIME_NOT_EQUAL;
		default: {
			throw new IllegalArgumentException("requested databse-id of "
					+ value + " is unknown for StringRegelOperator");
		}
		}
	}

	public short databaseValue() {
		switch (this) {
		case OPERATOR_TEXT_EQUAL:
			return 1;
		case OPERATOR_TEXT_NOT_EQUAL:
			return 2;
		case OPERATOR_NUMERIC_LT:
			return 3;
		case OPERATOR_NUMERIC_LT_EQUAL:
			return 4;
		case OPERATOR_NUMERIC_EQUAL:
			return 5;
		case OPERATOR_NUMERIC_GT_EQUAL:
			return 6;
		case OPERATOR_NUMERIC_GT:
			return 7;
		case OPERATOR_NUMERIC_NOT_EQUAL:
			return 8;

		case OPERATOR_TIME_BEFORE:
			return 9;
		case OPERATOR_TIME_BEFORE_EQUAL:
			return 10;
		case OPERATOR_TIME_EQUAL:
			return 11;
		case OPERATOR_TIME_AFTER_EQUAL:
			return 12;
		case OPERATOR_TIME_AFTER:
			return 13;
		case OPERATOR_TIME_NOT_EQUAL:
			return 14;
		default:
			throw new IllegalArgumentException(this
					+ " is an unsupported Enumconstant");
		}
	}
}

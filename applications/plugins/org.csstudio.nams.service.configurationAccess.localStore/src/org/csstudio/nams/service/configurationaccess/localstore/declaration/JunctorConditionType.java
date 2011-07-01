
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

/**
 * Der Operator von Juntion-Conditions.
 * 
 * Note: Namen der ENUM sind äquivalent zu den Einträgen in der DB!
 * 
 * XXX REname to Operator
 */
public enum JunctorConditionType {
	AND, OR;

	public static short asShort(final JunctorConditionType junctor) {
		switch (junctor) {
		case OR:
			return 0;
		case AND:
			return 1;
		default:
			throw new IllegalArgumentException("Unsupported Junctor");
		}
	}

	public static JunctorConditionType valueOf(final int value) {
		switch (value) {
		case 0:
			return OR;
		case 1:
			return AND;
		default:
			throw new IllegalArgumentException("Unsupported Junctor");
		}
	}
}

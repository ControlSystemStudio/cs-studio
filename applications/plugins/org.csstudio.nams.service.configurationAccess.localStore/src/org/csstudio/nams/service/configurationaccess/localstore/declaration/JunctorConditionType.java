package org.csstudio.nams.service.configurationaccess.localstore.declaration;

public enum JunctorConditionType {
	OR, AND, NOT;

	static JunctorConditionType valueOf(int value) {
		switch (value) {
		case 0:
			return OR;
		case 1:
			return AND;
		case 2:
			return NOT;
		default:
			throw new IllegalArgumentException("Unsupported Junctor");
		}
	}
	
	static short shortOf(JunctorConditionType junctor) {
		switch (junctor) {
		case OR:
			return 0;
		case AND:
			return 1;
		case NOT:
			return 2;
		default:
			throw new IllegalArgumentException("Unsupported Junctor");
		}
	}
}

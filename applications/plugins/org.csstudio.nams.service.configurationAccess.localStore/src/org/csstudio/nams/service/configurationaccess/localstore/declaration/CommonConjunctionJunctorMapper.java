package org.csstudio.nams.service.configurationaccess.localstore.declaration;

public enum CommonConjunctionJunctorMapper {
	OR, AND, NOT;

	public CommonConjunctionJunctorMapper valueOf(int value) {
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
}

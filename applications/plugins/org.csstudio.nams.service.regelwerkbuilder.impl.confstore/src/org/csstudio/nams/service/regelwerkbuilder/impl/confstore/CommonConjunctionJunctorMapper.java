
package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

public enum CommonConjunctionJunctorMapper {
	OR, AND, NOT;

	public CommonConjunctionJunctorMapper valueOf(final int value) {
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

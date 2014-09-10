/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.displayNone;
import static org.epics.vtype.ValueFactory.newVInt;

import java.util.Arrays;
import java.util.List;

import org.epics.pvmanager.util.NullUtils;
import org.epics.vtype.VEnum;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;

/**
 * @author tom.cobb@diamond.ac.uk
 *
 */
class EnumIndexOfFunction implements FormulaFunction {

	@Override
	public boolean isPure() {
		return true;
	}

	@Override
	public boolean isVarArgs() {
		return false;
	}

	@Override
	public String getName() {
		return "indexOf";
	}

	@Override
	public String getDescription() {
		return "Gets the index of a VEnum";
	}

	@Override
	public List<Class<?>> getArgumentTypes() {
		return Arrays.<Class<?>> asList(VEnum.class);
	}

	@Override
	public List<String> getArgumentNames() {
		return Arrays.asList("enum");
	}

	@Override
	public Class<?> getReturnType() {
		return VNumber.class;
	}

	@Override
	public Object calculate(List<Object> args) {
		if (NullUtils.containsNull(args)) {
			return null;
		}    	
		// args[0] is a VEnum
		VEnum value = (VEnum) args.get(0);    	
		return newVInt(value.getIndex(),
				ValueUtil.highestSeverityOf(args, false),
				ValueUtil.latestValidTimeOrNowOf(args),
				displayNone());    	
	}

}

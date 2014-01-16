package org.csstudio.opibuilder.script;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.epics.pvmanager.ReadFunction;

public class RuleReadFunction implements ReadFunction<Object> {

	private final List<ReadFunction<Boolean>> expressions;
	private final List<Object> values;
	private final Object defaultValue;
	private final List<ReadFunction<?>> pvArguments;
	private final List<Boolean> isPvTrigger;
	private List<Object> currentPvValues;
	private Object previousResult = null;
	
	public RuleReadFunction(List<String> expressions, List<Object> values,
			Object defaultValue, List<ReadFunction<?>> pvArguments, List<Boolean> isPvTrigger) {
		this.expressions = new ArrayList<>();
		// for each expression
		//    parse it
		//    create a ReadFunction that uses currentPvValues
		//    pvInt[0]>3
		//  new ReadFunction() {
		//      Object readValue() {
		//           return currentPvValue.get(0).getValue() > 3;
		//      }
		//   }
		this.values = values;
		this.defaultValue = defaultValue;
		this.pvArguments = pvArguments;
		this.isPvTrigger = isPvTrigger;
	}
	
	@Override
	public Object readValue() {
		// Create the new list of pv values
		List<Object> newValues = new ArrayList<>(pvArguments.size());
		for (ReadFunction<?> pvArgument: pvArguments) {
			Object value = pvArgument.readValue();
			newValues.add(value);
			
			// If even one pv does not have a value, do
			// not compute the rule
			if (value == null) {
				return previousResult;
			}
		}

		// Check that at least one trigger pv has changed
		boolean trigger = false;
		for (int i = 0; i < pvArguments.size(); i++) {
			if (isPvTrigger.get(i) && !Objects.equals(newValues.get(i), currentPvValues.get(i))) {
				trigger = true;
			}
		}
		
		if (!trigger) {
			return previousResult;
		}

		currentPvValues = newValues;

		// Result should be the value corresponding to the first
		// expression that is true
		Object result = null;
		for (int i = 0; i < expressions.size(); i++) {
			if (result == null && expressions.get(i).readValue()) {
				result = values.get(i);
			}
		}
		// If no expression was true, use the default
		if (result == null) {
			result = defaultValue;
		}
		
		previousResult = result;
		return result;
		
//		List<String> expressions
//		List<Object> values
//		Object defaultValue
//		List<String> pvs
//		List<Boolean> isPvTrigger
//		
//		expression -> parse -> function(List<Object> pvValues) -> Boolean
//		
//		Logic:
//	    * create new list of pvValue
//	    * if all pvtrigger are the same, return previous value
//	    * see what function returns true with the new list of pvvalue
//	    * return value of expression or default value
		// TODO Auto-generated method stub
	}


}

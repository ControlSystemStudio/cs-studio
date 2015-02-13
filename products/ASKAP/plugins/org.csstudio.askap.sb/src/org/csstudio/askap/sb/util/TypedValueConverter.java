package org.csstudio.askap.sb.util;

import java.util.Arrays;

import askap.interfaces.Direction;
import askap.interfaces.DoubleComplex;
import askap.interfaces.FloatComplex;
import askap.interfaces.TypedValue;
import askap.interfaces.TypedValueBool;
import askap.interfaces.TypedValueBoolSeq;
import askap.interfaces.TypedValueDirection;
import askap.interfaces.TypedValueDirectionSeq;
import askap.interfaces.TypedValueDouble;
import askap.interfaces.TypedValueDoubleComplex;
import askap.interfaces.TypedValueDoubleComplexSeq;
import askap.interfaces.TypedValueDoubleSeq;
import askap.interfaces.TypedValueFloat;
import askap.interfaces.TypedValueFloatComplex;
import askap.interfaces.TypedValueFloatComplexSeq;
import askap.interfaces.TypedValueFloatSeq;
import askap.interfaces.TypedValueInt;
import askap.interfaces.TypedValueIntSeq;
import askap.interfaces.TypedValueLong;
import askap.interfaces.TypedValueLongSeq;
import askap.interfaces.TypedValueString;
import askap.interfaces.TypedValueStringSeq;
import askap.interfaces.TypedValueType;

/**
 * This class is to be used to convert TypedValue to String
 * @author wu049
 *
 */

public class TypedValueConverter {
	
	static class DirectionWrapper {
		Direction direction = null;
		public DirectionWrapper(Direction direction) {
			this.direction = direction;
		}
		
		@Override
		public String toString() {
			String value = "";
			switch (direction.sys) {
				case J2000:
					value = "J2000 " + direction.coord1 + " " + direction.coord2;
					break;
				case AZEL:
					value = "AZEL " + direction.coord1 + " " + direction.coord2;
					break;
			}
			
			return value;
		}
	}
	
	
	static class ComplexFloatWrapper {
		float real;
		float imaginary;
		
		public ComplexFloatWrapper(FloatComplex floatComplex) {
			real = floatComplex.real;
			imaginary = floatComplex.imag;
		}
				
		@Override
		public String toString() {
			
			String value = "" + real;
			if (imaginary<0) {
				value += "" + imaginary + "i";
			} else {
				value += "+" + imaginary + "i";				
			}
			
			return value;
		}
	}
	
	static class ComplexDoubleWrapper {
		double real;
		double imaginary;
		
		public ComplexDoubleWrapper(DoubleComplex doubleComplex) {
			real = doubleComplex.real;
			imaginary = doubleComplex.imag;
		}
				
		@Override
		public String toString() {
			
			String value = "" + real;
			if (imaginary<0) {
				value += "" + imaginary + "i";
			} else {
				value += "+" + imaginary + "i";				
			}
			
			return value;
		}
	}


	public TypedValueConverter() {
	}
	
	
	public static String convert(TypedValue typedValue) {
		String value = "";
		switch (typedValue.type) {
			case TypeNull:
				value = "";
				break;
			case TypeFloat:
				float fvalue = ((TypedValueFloat) typedValue).value;
				value = "" + fvalue;
				break;
			case TypeFloatSeq:
				float fvalues[] = ((TypedValueFloatSeq) typedValue).value;
				value = Arrays.toString(fvalues);
				break;
			case TypeDouble:
				double dvalue = ((TypedValueDouble) typedValue).value;
				value = "" + dvalue;
				break;
			case TypeDoubleSeq:
				double dvals[] = ((TypedValueDoubleSeq) typedValue).value;
				value = Arrays.toString(dvals);
				break;
			case TypeInt:
				int ivalue = ((TypedValueInt) typedValue).value;
				value = "" + ivalue;
				break;
			case TypeIntSeq:
				int intvals[] = ((TypedValueIntSeq) typedValue).value;
				value = Arrays.toString(intvals);
				break;
			case TypeLong:
				long lvalue = ((TypedValueLong) typedValue).value;
				value = "" + lvalue;
				break;
			case TypeLongSeq:
				long lvalues[] = ((TypedValueLongSeq) typedValue).value;
				value = Arrays.toString(lvalues);
				break;
			case TypeString:
				value = ((TypedValueString) typedValue).value;
				break;
			case TypeStringSeq:
				String strvalues[] = ((TypedValueStringSeq) typedValue).value;
				value = Arrays.toString(strvalues);
				break;
			case TypeBool:
				boolean bvalue = ((TypedValueBool) typedValue).value;
				value = "" + bvalue;
				break;
			case TypeBoolSeq:
				boolean bvalues[] = ((TypedValueBoolSeq) typedValue).value;
				value = Arrays.toString(bvalues);
				break;
			case TypeFloatComplex:
				FloatComplex fcomplex = ((TypedValueFloatComplex) typedValue).value;
				ComplexFloatWrapper fwrapper = new ComplexFloatWrapper(fcomplex);
				value = fwrapper.toString();
				break;
			case TypeDoubleComplex:
				DoubleComplex dcomplex = ((TypedValueDoubleComplex) typedValue).value;
				ComplexDoubleWrapper dwrapper = new ComplexDoubleWrapper(dcomplex);
				value = dwrapper.toString();
				break;
			case TypeDoubleComplexSeq:
			case TypeFloatComplexSeq:
				value = getComplexSeqString(typedValue);
				break;
			case TypeDirection:
				Direction direction = ((TypedValueDirection) typedValue).value;			
				DirectionWrapper wrapper = new DirectionWrapper(direction);
				value = wrapper.toString();
				break;
			case TypeDirectionSeq:
				value = getDirectionSeqString(typedValue);
				break;
		}
		
		return value;

	}
	
	private static String getDirectionSeqString(TypedValue typedValue) {
		String value = "";
		if (typedValue.type.equals(TypedValueType.TypeDirectionSeq)) {
			Direction directions[] = ((TypedValueDirectionSeq) typedValue).value;
			value = "";
			
			DirectionWrapper wrappers[] = new DirectionWrapper[directions.length];
			for (int i=0; i<directions.length; i++) {
				wrappers[i] = new DirectionWrapper(directions[i]);
			}
			
			value = Arrays.toString(wrappers);
		}	
		return value;
	}

	private static String getComplexSeqString(TypedValue typedValue) {
		String value = "";
		Object wrappers[] = null;
		if (typedValue.type.equals(TypedValueType.TypeDoubleComplexSeq)) {
			DoubleComplex dComplext[] = ((TypedValueDoubleComplexSeq) typedValue).value;
			wrappers = new ComplexDoubleWrapper[dComplext.length];
			for (int i=0; i<dComplext.length; i++) {
				wrappers[i] = new ComplexDoubleWrapper(dComplext[i]);
			}			
		} else if (typedValue.type.equals(TypedValueType.TypeFloatComplexSeq)) {
			FloatComplex fComplext[] = ((TypedValueFloatComplexSeq) typedValue).value;
			wrappers = new ComplexFloatWrapper[fComplext.length];
			for (int i=0; i<fComplext.length; i++) {
				wrappers[i] = new ComplexFloatWrapper(fComplext[i]);
			}
		}

		value = Arrays.toString(wrappers);
		return value;
	}
	
}

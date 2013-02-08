package org.csstudio.opibuilder.datadefinition;
public enum DataType {
		BOOLEAN("boolean"), //$NON-NLS-1$
		CHAR("char"), //$NON-NLS-1$
		BYTE("byte"), //$NON-NLS-1$
		SHORT("short"), //$NON-NLS-1$
		INT("int"), //$NON-NLS-1$
		LONG("long"), //$NON-NLS-N$
		FLOAT("float"), //$NON-NLS-1$
		DOUBLE("double"), //$NON-NLS-1$
		STRING("String"), //$NON-NLS-1$
		ENUM("enum"), //$NON-NLS-1$
		UNKNOWN("unknown"), //$NON-NLS-1$
		BOOLEAN_ARRAY("boolean[]"), //$NON-NLS-1$
		CHAR_ARRAY("char[]"), //$NON-NLS-1$
		BYTE_ARRAY("byte[]"), //$NON-NLS-1$
		SHORT_ARRAY("short[]"), //$NON-NLS-1$
		INT_ARRAY("int[]"), //$NON-NLS-1$
		LONG_ARRAY("long[]"), //$NON-NLS-N$
		FLOAT_ARRAY("float[]"), //$NON-NLS-1$
		DOUBLE_ARRAY("double[]"), //$NON-NLS-1$
		STRING_ARRAY("String[]"), //$NON-NLS-1$
		ENUM_ARRAY("enum[]"),
		OBJECT_ARRAY("Object[]");//$NON-NLS-1$		
		private String description;
		private DataType(String description) {
			this.description = description;
		}
		
		@Override
		public String toString() {
			return description;
		}
		
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i =0 ;
			for(DataType f : values()){
				result[i++] = f.toString();
			}
			return result;
		}
	}
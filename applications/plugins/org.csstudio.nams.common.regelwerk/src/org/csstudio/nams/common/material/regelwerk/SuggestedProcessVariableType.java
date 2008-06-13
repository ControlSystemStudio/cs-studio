package org.csstudio.nams.common.material.regelwerk;

/**
 * Expected type of channel.
 */
public enum SuggestedProcessVariableType implements
		HoldsAnDatabaseId {
	
	STRING((short) 1, String.class, new Operator[] { Operator.EQUALS,
			Operator.UNEQUALS }, new SuggestedProcessVariableType.Parser<String>() {
		public String parse(String dbString) {
			return dbString;
		}

		public String toDbString(Object value) {
			return (String) value;
		}
	}),
	

	DOUBLE((short) 3, Double.class, Operator.values(),
			new SuggestedProcessVariableType.Parser<Double>() {
		public Double parse(String dbString) {
			return Double.valueOf(dbString);
		}

		public String toDbString(Object value) {
			Double d = (Double) value;
			return Double.toString(d.doubleValue());
		}
	}),

	LONG((short) 2, Long.class, Operator.values(),
			new SuggestedProcessVariableType.Parser<Long>() {
		public Long parse(String dbString) {
			return Long.valueOf(dbString);
		}

		public String toDbString(Object value) {
			Long l = (Long) value;
			return Long.toString(l.intValue());
		}
	});

	static private interface Parser<T> {
		public T parse(String stringValue);

		public String toDbString(Object value);
	}

	/**
	 * Returns the Operator for given database-id or {@code null} if id is
	 * unknown.
	 */
	public static SuggestedProcessVariableType findOperatorOfDBId(short id) {
		for (SuggestedProcessVariableType spvt : SuggestedProcessVariableType
				.values()) {
			if (spvt._dbid == id) {
				return spvt;
			}
		}

		return null;
	}

	/**
	 * The database-id.
	 */
	private short _dbid;
	private Class<?> _suggestedTypeClass;

	private SuggestedProcessVariableType.Parser<?> _parser;
	
	/**
	 * The comparison operators supported by this type.
	 */
	private Operator[] _supportedOperators;

	/**
	 * Creates a value-representation with given database-id.
	 * 
	 * @param id
	 *            The id in database:
	 */
	SuggestedProcessVariableType(short id, Class<?> suggestedTypeClass,
			Operator[] supportedOperators, SuggestedProcessVariableType.Parser<?> parser) {
		_dbid = id;
		_suggestedTypeClass = suggestedTypeClass;
		_supportedOperators = supportedOperators;
		_parser = parser;
	}
	
	/**
	 * Returns the comparison operators supported by this type.
	 * @return an array of the comparison operators supported by this type.
	 */
	public Operator[] getSupportedOperators() {
		// return a copy of the array, so the caller cannot modify our
		// internal one
		Operator[] ops = new Operator[_supportedOperators.length];
		System.arraycopy(_supportedOperators, 0, ops, 0, ops.length);
		return ops;
	}

	/**
	 * Returns the database-id of this SuggestedProcessVariableType-Value.
	 * 
	 * <strong>Pay attention:</strong> Please do never use this method
	 * outside the DAOs or UI combos!
	 */
	public short asDatabaseId() {
		return _dbid;
	}

	/**
	 * The Java-class of this type.
	 */
	public Class<?> getSuggestedTypeClass() {
		return _suggestedTypeClass;
	}

	/**
	 * Checks if given value is parsable depending on the type of this
	 * valus.
	 * 
	 * @param value
	 *            The value to be checked.
	 * @return {@code true} if the value is parsable, {@code false}
	 *         otherwise.
	 */
	public boolean isParsableValue(String value) {
		try {
			this.parseDatabaseValue(value);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public <T> T parseDatabaseValue(String dbString) {
		return (T) this._suggestedTypeClass.cast(this._parser
				.parse(dbString));
	}

	/**
	 * Formats the given value as DB-representation depending on the
	 * suggested type class.
	 * 
	 * @require this.getSuggestedTypeClass().isAssignableFrom(value.getClass())
	 */
	public String toDbString(Object value) {
		assert this.getSuggestedTypeClass().isAssignableFrom(
				value.getClass()) : "Precondition violated: this.getSuggestedTypeClass().isAssignableFrom(value.getClass())";

		return _parser.toDbString(this.getSuggestedTypeClass().cast(value));
	}
}
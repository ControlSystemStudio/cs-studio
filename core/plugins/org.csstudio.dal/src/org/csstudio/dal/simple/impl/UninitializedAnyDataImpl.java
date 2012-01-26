package org.csstudio.dal.simple.impl;

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.simple.Severity;

public class UninitializedAnyDataImpl<T> extends AbstractAnyDataImpl<T> {
	
	private Severity severity = new Severity() {

		public boolean hasValue() {
			return false;
		}

		public boolean isInvalid() {
			return true;
		}

		public boolean isMajor() {
			return false;
		}

		public boolean isMinor() {
			return false;
		}

		public boolean isOK() {
			return false;
		}

        public String descriptionToString() {
            return null;
        }

		public String getSeverityInfo() {
			return "UNKNOWN";
		}};

	public UninitializedAnyDataImpl(DynamicValueProperty<T> property) {
		super(property,Long.MIN_VALUE);
	}
	public UninitializedAnyDataImpl(DynamicValueProperty<T> property, long beamID) {
		super(property, beamID);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.simple.impl.AbstractAnyDataImpl#getSeverity()
	 */
	@Override
	public Severity getSeverity() {
		return severity;
	}
	/* (non-Javadoc)
	 * @see org.csstudio.dal.simple.impl.AbstractAnyDataImpl#isValid()
	 */
	@Override
	public boolean isValid() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.csstudio.dal.simple.impl.AbstractAnyDataImpl#getQuality()
	 */
	@Override
	public Quality getQuality() {
		return Quality.Invalid;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.simple.impl.AbstractAnyDataImpl#getStatus()
	 */
	@Override
	public String getStatus() {
		return "Uninitialized";
	}

	public Object[] anySeqValue() {
		return null;
	}

	public Object anyValue() {
		return null;
	}

	public double[] doubleSeqValue() {
		return null;
	}

	public double doubleValue() {
		return Double.NaN;
	}

	public long[] longSeqValue() {
		return null;
	}

	public long longValue() {
		return 0;
	}

	public Number[] numberSeqValue() {
		return null;
	}

	public Number numberValue() {
		return null;
	}

	public String[] stringSeqValue() {
		return null;
	}

	public String stringValue() {
		return null;
	}
	
	@Override
	protected T confirmValue(T value) { 
		return value;
	}

}

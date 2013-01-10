
/**
 * 
 */

package org.csstudio.nams.common.material.regelwerk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.service.logging.declaration.ILogger;

/**
 * @author Goesta Steen
 * 
 */
public class StringRegel implements VersandRegel {

	private static ILogger logger;

	public static void staticInject(final ILogger o) {
		StringRegel.logger = o;
	}

	private final StringRegelOperator operator;
	private final String compareString;

	private final MessageKeyEnum messageKey;

	private final SimpleDateFormat amsDateFormat; 
	
	public StringRegel(final StringRegelOperator operator,
			final MessageKeyEnum messageKey, final String compareString) {
		this.operator = operator;
		this.messageKey = messageKey;
		this.compareString = compareString;
		this.amsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht,
	 *      de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	@Override
    public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			final AlarmNachricht nachricht, final Pruefliste bisherigesErgebnis) {
		// nothing to do here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufTimeOuts(de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste,
	 *      de.c1wps.desy.ams.allgemeines.Millisekunden)
	 */
	@Override
    public Millisekunden pruefeNachrichtAufTimeOuts(
			final Pruefliste bisherigesErgebnis,
			final Millisekunden verstricheneZeitSeitErsterPruefung) {
		// nothing to do here
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht,
	 *      de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	@Override
    public Millisekunden pruefeNachrichtErstmalig(
			final AlarmNachricht nachricht, final Pruefliste ergebnisListe) {

		boolean istGueltig = false;

		// TODO hier muss noch der richtige Schlüssel gewählt werden
		final String value = nachricht.getValueFor(this.messageKey);

		try {
			switch (this.operator) {
			
			    // text compare
			    case OPERATOR_TEXT_EQUAL:
			        istGueltig = this.wildcardStringCompare(value,
					this.compareString);
			        break;
			    case OPERATOR_TEXT_NOT_EQUAL:
			        istGueltig = !this.wildcardStringCompare(value,
					this.compareString);
			        break;

	            // numeric compare
	            case OPERATOR_NUMERIC_LT:
	                if (!value.isEmpty()) {
	                    istGueltig = this.numericCompare(value, this.compareString) < 0;
	                } else {
	                    istGueltig = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_LT_EQUAL:
	                if (!value.isEmpty()) {
	                    istGueltig = this.numericCompare(value, this.compareString) <= 0;
	                } else {
	                    istGueltig = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_EQUAL:
	                if (!value.isEmpty()) {
	                    istGueltig = this.numericCompare(value, this.compareString) == 0;
	                } else {
	                    istGueltig = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_GT_EQUAL:
	                if (!value.isEmpty()) {
	                    istGueltig = this.numericCompare(value, this.compareString) >= 0;
	                } else {
	                    istGueltig = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_GT:
	                if (!value.isEmpty()) {
	                    istGueltig = this.numericCompare(value, this.compareString) > 0;
	                } else {
	                    istGueltig = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_NOT_EQUAL:
	                if (!value.isEmpty()) {
	                    istGueltig = this.numericCompare(value, this.compareString) != 0;
	                } else {
	                    istGueltig = false;
	                }
	                break;

    			// time compare
    			case OPERATOR_TIME_BEFORE:
    				istGueltig = this.timeCompare(value, this.compareString) < 0;
    				break;
    			case OPERATOR_TIME_BEFORE_EQUAL:
    				istGueltig = this.timeCompare(value, this.compareString) <= 0;
    				break;
    			case OPERATOR_TIME_EQUAL:
    				istGueltig = this.timeCompare(value, this.compareString) == 0;
    				break;
    			case OPERATOR_TIME_AFTER_EQUAL:
    				istGueltig = this.timeCompare(value, this.compareString) >= 0;
    				break;
    			case OPERATOR_TIME_AFTER:
    				istGueltig = this.timeCompare(value, this.compareString) > 0;
    				break;
    			case OPERATOR_TIME_NOT_EQUAL:
    				istGueltig = this.timeCompare(value, this.compareString) != 0;
    				break;
			}
		} catch (final Exception e) {
            if(StringRegel.logger != null) {
                StringRegel.logger.logErrorMessage(this,
                        "An error occured during parsing of : " + nachricht);
            }
			istGueltig = true;
		}

		if (istGueltig) {
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.ZUTREFFEND);
		} else {
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.NICHT_ZUTREFFEND);
		}
		return null;
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder("StringRegel: ");
		stringBuilder.append("messageKey: ");
		stringBuilder.append(this.messageKey);
		stringBuilder.append(" operator: ");
		stringBuilder.append(this.operator);
		stringBuilder.append(" compareString: ");
		stringBuilder.append(this.compareString);
		return stringBuilder.toString();
	}

	private int numericCompare(final String value, final String compare)
			throws NumberFormatException {
		final double dVal = Double.parseDouble(value);
		final double dCompVal = Double.parseDouble(compare);

		return Double.compare(dVal, dCompVal);
	}

	private int timeCompare(final String value, final String compare)
			throws ParseException {
//		final Date dateValue = DateFormat.getDateInstance(DateFormat.SHORT,
//				Locale.US).parse(value);
//		final Date dateCompValue = DateFormat.getDateInstance(DateFormat.SHORT,
//				Locale.US).parse(compare);

	    final Date dateValue = amsDateFormat.parse(value);
	    final Date dateCompValue = amsDateFormat.parse(compare);

		return dateValue.compareTo(dateCompValue);
	}

	private boolean wildcardStringCompare(final String value,
			final String wildcardString2) {
		try {
			return WildcardStringCompare.compare(value, wildcardString2);
		} catch (final Exception e) {
			// TODO handle Exception
			return true;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((compareString == null) ? 0 : compareString.hashCode());
		result = prime * result
				+ ((messageKey == null) ? 0 : messageKey.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StringRegel other = (StringRegel) obj;
		if (compareString == null) {
			if (other.compareString != null)
				return false;
		} else if (!compareString.equals(other.compareString))
			return false;
		if (messageKey == null) {
			if (other.messageKey != null)
				return false;
		} else if (!messageKey.equals(other.messageKey))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		return true;
	}
}

/**
 * 
 */
package org.csstudio.nams.common.material.regelwerk;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.service.logging.declaration.Logger;

/**
 * @author Goesta Steen
 * 
 */
public class StringRegel implements VersandRegel {

	private final StringRegelOperator operator;
	private final String compareString;
	private final MessageKeyEnum messageKey;
	private static Logger logger;

	public StringRegel(StringRegelOperator operator, MessageKeyEnum messageKey,
			String compareString) {
		this.operator = operator;
		this.messageKey = messageKey;
		this.compareString = compareString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht,
	 *      de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
		// nothing to do here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufTimeOuts(de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste,
	 *      de.c1wps.desy.ams.allgemeines.Millisekunden)
	 */
	public Millisekunden pruefeNachrichtAufTimeOuts(
			Pruefliste bisherigesErgebnis,
			Millisekunden verstricheneZeitSeitErsterPruefung, AlarmNachricht initialeNachricht) {
		// nothing to do here
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht,
	 *      de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	public Millisekunden pruefeNachrichtErstmalig(AlarmNachricht nachricht,
			Pruefliste ergebnisListe) {

		boolean istGueltig = false;
		
		// TODO hier muss noch der richtige Schlüssel gewählt werden
		String value = nachricht.getValueFor(messageKey);

		try {
			switch (operator) {
			// text compare
			case OPERATOR_TEXT_EQUAL:
				istGueltig = wildcardStringCompare(value, compareString);
				break;
			case OPERATOR_TEXT_NOT_EQUAL:
				istGueltig = !wildcardStringCompare(value, compareString);
				break;

			// numeric compare
			case OPERATOR_NUMERIC_LT:
				istGueltig = numericCompare(value, compareString) < 0;
				break;
			case OPERATOR_NUMERIC_LT_EQUAL:
				istGueltig = numericCompare(value, compareString) <= 0;
				break;
			case OPERATOR_NUMERIC_EQUAL:
				istGueltig = numericCompare(value, compareString) == 0;
				break;
			case OPERATOR_NUMERIC_GT_EQUAL:
				istGueltig = numericCompare(value, compareString) >= 0;
				break;
			case OPERATOR_NUMERIC_GT:
				istGueltig = numericCompare(value, compareString) > 0;
				break;
			case OPERATOR_NUMERIC_NOT_EQUAL:
				istGueltig = numericCompare(value, compareString) != 0;
				break;

			// time compare
			case OPERATOR_TIME_BEFORE:
				istGueltig = timeCompare(value, compareString) < 0;
				break;
			case OPERATOR_TIME_BEFORE_EQUAL:
				istGueltig = timeCompare(value, compareString) <= 0;
				break;
			case OPERATOR_TIME_EQUAL:
				istGueltig = timeCompare(value, compareString) == 0;
				break;
			case OPERATOR_TIME_AFTER_EQUAL:
				istGueltig = timeCompare(value, compareString) >= 0;
				break;
			case OPERATOR_TIME_AFTER:
				istGueltig = timeCompare(value, compareString) > 0;
				break;
			case OPERATOR_TIME_NOT_EQUAL:
				istGueltig = timeCompare(value, compareString) != 0;
				break;
			}
		} catch (Exception e) {
			logger.logErrorMessage(this, "An error occured during parsing of : "+ nachricht);
			istGueltig = true;
		}
		
		if (istGueltig) {
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, RegelErgebnis.ZUTREFFEND);
		} else {
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, RegelErgebnis.NICHT_ZUTREFFEND);
		}
		return null;
	}

	private int timeCompare(String value, String compareString)
			throws ParseException {
		Date dateValue = DateFormat
				.getDateInstance(DateFormat.SHORT, Locale.US).parse(value);
		Date dateCompValue = DateFormat.getDateInstance(DateFormat.SHORT,
				Locale.US).parse(compareString);

		return dateValue.compareTo(dateCompValue);
	}

	private int numericCompare(String value, String compareString)
			throws NumberFormatException {
		double dVal = Double.parseDouble(value);
		double dCompVal = Double.parseDouble(compareString);

		return Double.compare(dVal, dCompVal);
	}

	private boolean wildcardStringCompare(String value, String wildcardString2) {
		try {
			return WildcardStringCompare.compare(value, wildcardString2);
		} catch (Exception e) {
			// TODO handle Exception
			return true;
		}
	}

	public static void staticInject(Logger logger) {
		StringRegel.logger = logger;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("StringRegel: ");
		stringBuilder.append("messageKey: ");
		stringBuilder.append(messageKey);
		stringBuilder.append(" operator: ");
		stringBuilder.append(operator);
		stringBuilder.append(" compareString: ");
		stringBuilder.append(compareString);
		return stringBuilder.toString();
	}
}

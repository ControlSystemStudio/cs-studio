package org.csstudio.dct.export.internal;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dct.export.IExporter;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;

public class DbStatistics implements IExporter {

	private static final String NEWLINE = "\r\n";

	/**
	 * Number of all records in DB.
	 */
	private int _totalRecordNumber;

	/**
	 * Number of all records with IO in DB.
	 */
	private int _totalRecordNumberIO;

	/**
	 * Number of all records without IO in DB.
	 */
	private int _totalRecordNumberWithoutIO;

	/**
	 * Number of record types in DB.
	 */
	Map<String, Integer> _RecordTypeNumber;

	/**
	 * Number of record types with IO name in DB.
	 */
	Map<String, Integer> _RecordTypeNumberWithIO;

	/**
	 * Number of record types with IO name in DB.
	 */
	Map<String, Integer> _RecordTypeNumberWithoutIO;
	

	public String export(IProject project) {
		_totalRecordNumber = 0;
		_totalRecordNumberIO = 0;
		_totalRecordNumberWithoutIO = 0;
		_RecordTypeNumber = new HashMap<String, Integer>();
		_RecordTypeNumberWithIO = new HashMap<String, Integer>();
		_RecordTypeNumberWithoutIO = new HashMap<String, Integer>();
		for (IRecord r : project.getFinalRecords()) {
			countRecordTypes(r);
			countRecordTypesIoNames(r);
		}
		return createOutput();
	}

	private void countRecordTypesIoNames(IRecord r) {
		String type = r.getType();
		Map<String, String> finalFields = r.getFinalFields();
		String inp = finalFields.get("INP");
		if (inp != null) {
			_totalRecordNumberIO++;
			Integer typeNumber = _RecordTypeNumberWithIO.get(type);
			if (typeNumber != null) {
				typeNumber++;
				_RecordTypeNumberWithIO.put(type, typeNumber);
			} else {
				_RecordTypeNumberWithIO.put(type, 1);
			}
		} else {
			_totalRecordNumberWithoutIO++;
			Integer typeNumber = _RecordTypeNumberWithoutIO.get(type);
			if (typeNumber != null) {
				typeNumber++;
				_RecordTypeNumberWithoutIO.put(type, typeNumber);
			} else {
				_RecordTypeNumberWithoutIO.put(type, 1);
			}
		}
	}

	private void countRecordTypes(IRecord r) {
		_totalRecordNumber++;
		String type = r.getType();
		Integer typeNumber = _RecordTypeNumber.get(type);
		if (typeNumber != null) {
			typeNumber++;
			_RecordTypeNumber.put(type, typeNumber);
		} else {
			_RecordTypeNumber.put(type, 1);
		}
	}

	private String createOutput() {
		StringBuffer sb = new StringBuffer();
		sb.append("Total Number: ").append(_totalRecordNumber);
		sb.append(NEWLINE);

		for (String key : _RecordTypeNumber.keySet()) {
			Integer number = _RecordTypeNumber.get(key);
			sb.append("Number of type ").append(String.format("%8s", key)).append(": ").append(String.format("%4d", number));
			sb.append(NEWLINE);
		}
		sb.append(NEWLINE);

		sb.append("Total Number with IO name : ").append(_totalRecordNumberIO);
		sb.append(NEWLINE);
		
		for (String key : _RecordTypeNumberWithIO.keySet()) {
			Integer number = _RecordTypeNumberWithIO.get(key);
			sb.append("Number of type ").append(String.format("%8s", key)).append(": ").append(String.format("%4d", number));
			sb.append(NEWLINE);
		}
		
		sb.append(NEWLINE);
		sb.append("Total Number without IO name : " + _totalRecordNumberWithoutIO);
		sb.append(NEWLINE);
		
		for (String key : _RecordTypeNumberWithoutIO.keySet()) {
			Integer number = _RecordTypeNumberWithoutIO.get(key);
			sb.append("Number of type ").append(String.format("%8s", key)).append(": ").append(String.format("%4d", number));
			sb.append(NEWLINE);
		}
		return sb.toString();
	}
}

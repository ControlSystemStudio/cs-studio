package org.csstudio.dct.dbexport;

public class Record {
	private int id;
	private String ioName;
	private String epicsName;
	private String recordType;
	private String dctId;
	private String dctProjectId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIoName() {
		return ioName;
	}
	public void setIoName(String ioName) {
		this.ioName = ioName;
	}
	public String getEpicsName() {
		return epicsName;
	}
	public void setEpicsName(String epicsName) {
		this.epicsName = epicsName;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getDctId() {
		return dctId;
	}
	public void setDctId(String dctId) {
		this.dctId = dctId;
	}
	public String getDctProjectId() {
		return dctProjectId;
	}
	public void setDctProjectId(String dctProjectId) {
		this.dctProjectId = dctProjectId;
	}
}

package org.csstudio.archive.reader.appliance;


/**
 * Interface that defines constants which are used in archive reader appliance.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public interface ApplianceArchiveReaderConstants {
	/** The name of the archiver appliance reader as it appears in the preferences */
	String ARCHIVER_NAME = "EPICS Archiver Appliance";
	/** The description of the archiver appliance reader */
	String ARCHIVER_DESCRIPTION = "http://epicsarchiverap.sourceforge.net/";
	/** The version of the appliance reader */
	int VERSION = 1;
	/** The path from which the data is collected */
	String RETRIEVAL_PATH = "/data/getData.raw";
	/** The path to retrieve the list of archived PVs from */
	String SEARCH_PATH = "/bpl/searchForPVsRegex?regex=";
	/** Lower operating limit field */
	String LOPR = "LOPR";
	/** Minor alarm lower limit field */ 
	String LOW = "LOW";
	/** Major alarm lower limit field */
	String LOLO = "LOLO";
	/** Minor alarm upper limit field */
	String HIGH = "HIGH";
	/** Major alarm upper limit field */
	String HIHI = "HIHI";
	/** Upper operating limit field */
	String HOPR = "HOPR";
	/** Engineering units field */
	String EGU = "EGU";
	/** Precision field */
	String PREC = "PREC";
	/** Val field */
	String VAL = "val";
	
	/** Operator for every nth value in the archive */
	String OP_NTH = "nth_";
	/** Operator for counting number of samples in the archive */
	String OP_NCOUNT = "ncount";
	/** Operator for the minimum bin value */
	String OP_MIN = "min_";
	/** Operator for the maximum bin value */
	String OP_MAX = "max_";
	/** Operator for the number of points in the bin */
	String OP_COUNT = "count_";
	/** Operator for the standard deviation in the bin */
	String OP_STD = "std_";
	/** Operator for the mean bin value */
	String OP_MEAN = "mean_";
	
}
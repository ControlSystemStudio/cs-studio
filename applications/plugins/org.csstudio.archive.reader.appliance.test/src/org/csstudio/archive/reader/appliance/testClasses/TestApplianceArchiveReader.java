package org.csstudio.archive.reader.appliance.testClasses;

import org.csstudio.archive.reader.appliance.ApplianceArchiveReader;
import org.epics.archiverappliance.retrieval.client.DataRetrieval;

/**
 * Test {@code ApplianceArchiveReader} implementation.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class TestApplianceArchiveReader extends ApplianceArchiveReader{
	
	/**
	 * Constructor.
	 */
	public TestApplianceArchiveReader(boolean useStatistics) {
		super("aar://test",useStatistics);
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.appliance.ApplianceArchiveReader#createDataRetriveal(java.lang.String)
	 */
	@Override
	public DataRetrieval createDataRetriveal(String dataRetrievalURL) {
		return new TestDataRetrieval();
	}
}

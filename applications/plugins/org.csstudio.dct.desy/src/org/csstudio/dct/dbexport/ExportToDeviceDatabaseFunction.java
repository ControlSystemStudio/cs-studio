package org.csstudio.dct.dbexport;

import java.util.LinkedHashMap;
import java.util.Map;

import org.csstudio.dct.IRecordFunction;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Record function for archiving records.
 * 
 * FIXME: svw: Replace the dummy implementation with real stuff!
 * 
 * @author Sven Wende
 * 
 */
public final class ExportToDeviceDatabaseFunction implements IRecordFunction {
	private static final String ATTR_ARCHIVE = "archive";

	/**
	 *{@inheritDoc}
	 */
	public void run(IRecord record, Map<String, String> attributes) {
		if (Boolean.parseBoolean(attributes.get(ATTR_ARCHIVE))) {
			CentralLogger.getInstance().info(null, "Archiving Record [" + AliasResolutionUtil.getEpicsNameFromHierarchy(record) + "]");
		}

		Record dbRecord = new Record();
		dbRecord.setName(AliasResolutionUtil.getEpicsNameFromHierarchy(record));

		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		try {
			tx = session.beginTransaction();
			session.save(dbRecord);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (HibernateException e1) {
					CentralLogger.getInstance().error(this, "Error during transaction rollback.", e1);
				}
				CentralLogger.getInstance().error(this, e);
			}
		}
	}

	/**
	 *{@inheritDoc}
	 */
	public Map<String, String> getAttributes() {
		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put(ATTR_ARCHIVE, "true");
		return result;
	}

}

package org.csstudio.dct.dbexport;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.dct.IRecordFunction;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Record function that archives record information. Information ist stored in
 * DESY device database.
 * 
 * @author Sven Wende
 * 
 */
public final class ExportToDeviceDatabaseFunction implements IRecordFunction {
    
    private static final Logger LOG = LoggerFactory.getLogger(ExportToDeviceDatabaseFunction.class);
	private static final String ATTR_ARCHIVE = "archiveToDesyDeviceDb";

	private static final Pattern FIND_IONAME_FUNCTION = Pattern.compile("^>ioname\\((.*)\\)$");

	/**
	 *{@inheritDoc}
	 */
	public Map<String, String> getAttributes() {
		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put(ATTR_ARCHIVE, "true");
		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	public void run(IProject project, IProgressMonitor monitor) {
		monitor.beginTask("Archiving Record to DB", project.getFinalRecords().size());
		monitor.subTask("Connecting to database");

		// get hibernate session
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = null;

		// do archiving within a single transaction
		try {
			// .. begin transaction
			tx = session.beginTransaction();

			// .. delete existing database entries for this dct project
			monitor.subTask("deleting old entries for project");
			Query query = session.createQuery("DELETE FROM Record WHERE dctProjectId='" + project.getId().toString() + "'");
			query.executeUpdate();

			// .. archive all records
			for (IRecord r : project.getFinalRecords()) {
				monitor.subTask("Processing record " + AliasResolutionUtil.getEpicsNameFromHierarchy(r));

				if (!r.isAbstract()) {
					try {
						archiveIoNames(r, session);
					} catch (AliasResolutionException e) {
						LOG.error("", e);
					}
				}
				monitor.internalWorked(1);
			}

			// .. commit transaction
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (HibernateException e1) {
					LOG.error("Error during transaction rollback.", e1);
				}
				LOG.error("", e);
			}
		}

	}

	private void archiveIoNames(IRecord record, Session session) throws AliasResolutionException {
		String archiveProperty = record.getFinalProperties().get(ATTR_ARCHIVE);
		if (archiveProperty == null) {
			archiveProperty = getAttributes().get(ATTR_ARCHIVE);
		}

		assert archiveProperty != null;
		boolean archive = Boolean.valueOf(archiveProperty);

		// .. archive this record ?
		if (archive) {
			Map<String, String> fields = record.getFinalFields();

			// .. find field that uses the ioname() function
			for (String key : fields.keySet()) {
				String val = fields.get(key);

				Matcher matcher = FIND_IONAME_FUNCTION.matcher(val);

				if (matcher.find()) {
					// .. extract parameters of the ioname() function
					String[] params = matcher.group(1).split(",");
					for (int i = 0; i < params.length; i++) {
						params[i] = params[i].trim();
					}

					// .. create archive entry
					if (params.length > 0) {
						Record dbRecord = new Record();

						// .. io name
						dbRecord.setIoName(ResolutionUtil.resolve(params[0], record));

						// .. resolved epics name
						dbRecord.setEpicsName(ResolutionUtil.resolve(AliasResolutionUtil.getEpicsNameFromHierarchy(record), record));

						// .. dct id of the record
						dbRecord.setDctId(record.getId().toString());

						// .. dct id of the project
						dbRecord.setDctProjectId(record.getContainer().getProject().getId().toString());

						// .. record type
						dbRecord.setRecordType(record.getType());

						// .. save record to database
						session.save(dbRecord);
					}
				}
			}
		}
	}

}

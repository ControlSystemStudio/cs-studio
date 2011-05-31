package org.csstudio.utility.recordproperty.rdb.data;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.service.util.LdapNameUtils;
import org.csstudio.utility.recordproperty.Activator;
import org.csstudio.utility.recordproperty.Messages;
import org.csstudio.utility.recordproperty.RecordPropertyEntry;
import org.csstudio.utility.recordproperty.rdb.config.OracleSettings;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RecordPropertyGetRDB gets data (record fields) from RDB, DAL and RMI.
 *
 * @author Rok Povsic
 */
public class RecordPropertyGetRDB {

	ResultSet resultSet;
	ResultSet resultSetFieldNames;

	ArrayList<RecordPropertyEntry> data = new ArrayList<RecordPropertyEntry>();

	private String fieldName;
	private String valueRdb;
	private DBConnect connect;
	private DBConnect _connectForFieldNames;

	private String _fieldType;

	/**
	 * If DAL does not have any data, it prints this.
	 */
	private String _value = Messages.RecordPropertyView_NA;

	/**
	 * Record name, that you have to get data of
	 */
	private String _record;

	private String _nameIOC;
	private ChangelogEntry[] _entryRMI;
	private String _valueRMI;

	/**
	 * A string that is displayed when no access.
	 */
	private final String _na = Messages.RecordPropertyView_NA;

	/**
	 * Get if RDB is offline.
	 */
	private String _rtype;

	RecordPropertyEntry[] _stringArray;

	/**
	 * The logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RecordPropertyGetRDB.class);

	/**
	 * Gets all possible data that can be collected.
	 *
	 * @param rec
	 *            name of the record
	 * @return stringArray
	 */
	public RecordPropertyEntry[] getData(final String rec) {
		_record = rec;

		_record = validateRecord(_record);

		if (getRtypeFromDAL()) {
			// nothing is to be done here
		} else {
			if (!getRtypeFromRecordName(_record).equals("not-valid")) {
				// nothing is to be done here
			} else {
				// nothing is printed, everything is empty
			}
		}

		if (getDataFromRDB()) {
			getDataIfRDB();
		} else {
			if (getFieldNamesFromRDBsql()) {
				getFieldNamesFromRDB();
			} else {
				// nothing is printed, everything is grayed out
			}
		}

		return _stringArray;

	}

	/**
	 * Gets data if RDB is online.
	 *
	 * @return
	 */
	private void getDataIfRDB() {

		getDataFromRMI();

		try {
			// Goes through every row and gets data.
			while (resultSet.next()) {
				fieldName = resultSet.getString("FIELD_NAME");
				valueRdb = resultSet.getString("VALUE");

				while (resultSetFieldNames.next()) {
					if (fieldName.equals(resultSetFieldNames
							.getString("FIELD_NAME"))) {
						_fieldType = resultSetFieldNames.getString("FIELD_TYPE");

						final String badType = "15";

						if (!_fieldType.equals(badType)) {
							getDataFromDAL();
						} else {
							_value = _na;
						}
						break;
					}
				}

				// If IOC(RMI)(4th column) does not have any data, it prints
				// this.
				_valueRMI = _na;

				// Search if record.fieldName matches one in IOC(RMI) and sets
				// it.
				for (final ChangelogEntry element : _entryRMI) {
					if ((_record + "." + fieldName).equals(element
							.getPvName())) {
						_valueRMI = element.getValue();
						break;
					}
				}

				// Adds new line to table
				final RecordPropertyEntry entry = new RecordPropertyEntry(fieldName,
						valueRdb, _value, _valueRMI);
				data.add(entry);

				// Set value back to 'not found', to overwrite last value.
				_value = _na;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		_stringArray = data
				.toArray(new RecordPropertyEntry[data.size()]);

		connect.closeConnection();
	}

	/**
	 * Gets data if RDB is not online.
	 */
	private boolean getRtypeFromDAL() {
		final ProcessVariableAdressFactory _addressFactory = ProcessVariableAdressFactory
				.getInstance();

		final IProcessVariableConnectionService _connectionService = ProcessVariableConnectionServiceFactory
				.getDefault().createProcessVariableConnectionService();

		final IProcessVariableAddress pv = _addressFactory.createProcessVariableAdress("dal-epics://" + _record
				+ ".RTYP");

		try {
			_rtype = _connectionService.readValueSynchronously(pv, ValueType.STRING);
			System.out.println(_rtype);
			return true;
		} catch (final ConnectionException e) {
			return false;
		}
	}

	/**
	 * Gets data from RDB.
	 */
	private boolean getDataFromRDB() {
		connect = new DBConnect(new OracleSettings());

		connect.openConnection();

		try {
			resultSet = connect
					.executeQuery("With LVL0 as (Select tv.field_name||tv.prompt as checkValue "
							+ "from instance_values iv, type_val tv, instance_records tmp "
							+ "where (iv.instance_record_id = tmp.instance_record_id) and "
							+ "(tmp.instance_record = '"
							+ _record
							+ "') and "
							+ "(iv.type_id = tv.type_id) and (iv.field_index = tv.field_index) "
							+ "group by tv.field_name||tv.prompt), "
							+ "LVL1 as (Select tv.field_name||tv.prompt as checkValue "
							+ "from type_val tv, project_values pv, instance_records tmp "
							+ "where (pv.prototype_record_id = tmp.prototype_record_id) and "
							+ "(tmp.instance_record = '"
							+ _record
							+ "') and "
							+ "(tv.type_id = pv.type_id) and (tv.field_index = pv.field_index) "
							+ "group by tv.field_name||tv.prompt "
							+ "minus "
							+ "select checkValue from LVL0), "
							+ "LVL2 as (Select tv.field_name||tv.prompt as checkValue "
							+ "from type_val tv, instance_records tmp "
							+ "where (tv.type_id = tmp.type_id) and "
							+ "(tmp.instance_record = '"
							+ _record
							+ "') "
							+ "group by tv.field_name||tv.prompt "
							+ "minus "
							+ "select checkValue from LVL0 "
							+ "minus "
							+ "select checkValue from LVL1) "
							+

							"Select 0 lvl, tv.field_index, tv.field_name, tv.prompt, iv.value "
							+ "from instance_values iv, type_val tv, instance_records tmp "
							+ "where (iv.instance_record_id = tmp.instance_record_id) and "
							+ "(tmp.instance_record = '"
							+ _record
							+ "') and "
							+ "(iv.type_id = tv.type_id) and (iv.field_index = tv.field_index) "
							+ "union "
							+ "select 1 lvl, tv.field_index, tv.field_name, tv.prompt, pv.value "
							+ "from type_val tv, project_values pv, instance_records tmp "
							+ "where (pv.prototype_record_id = tmp.prototype_record_id) and "
							+ "(tmp.instance_record = '"
							+ _record
							+ "') and "
							+ "(tv.type_id = pv.type_id) and (tv.field_index = pv.field_index) and "
							+ "(tv.field_name||tv.prompt in (select checkValue from LVL1)) "
							+ "union "
							+ "select 2 lvl, tv.field_index, tv.field_name, tv.prompt, tv.default_value as value "
							+ "from type_val tv, instance_records tmp "
							+ "where (tv.type_id = tmp.type_id) and (tmp.instance_record = '"
							+ _record
							+ "') and "
							+ "(tv.field_name||tv.prompt in (select checkValue from LVL2)) "
							+ "order by field_index ");

			resultSetFieldNames = connect
					.executeQuery("select tv.field_name, tv.field_type from epics_version ev, "
							+ "rec_type rt, type_val tv where ev.epics_id = '4061' and "
							+ "ev.epics_id = rt.epics_id and rt.record_type = '"
							+ _rtype + "' and " + "rt.type_id = tv.type_id");

			// Check weather the result set is empty
			if (!resultSet.isBeforeFirst()) {
				return false;
			}
			return true;

		} catch (final SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Gets data from DAL.
	 */
	private void getDataFromDAL() {
		ProcessVariableAdressFactory _addressFactory;

		IProcessVariableConnectionService _connectionService;

		final ProcessVariableConnectionServiceFactory _connectionFactory = ProcessVariableConnectionServiceFactory
				.getDefault();

		_addressFactory = ProcessVariableAdressFactory.getInstance();

		_connectionService = _connectionFactory
				.createProcessVariableConnectionService();

		try {
			_value = _connectionService.readValueSynchronously(_addressFactory
					.createProcessVariableAdress("dal-epics://" + _record + "."
							+ fieldName), ValueType.STRING);
		} catch (final ConnectionException e) {
		    LOG.info("Field value not found: {}.{}", _record,  fieldName);
			e.printStackTrace();
		}
	}

	/**
	 * Gets data from RMI (IOC).
	 */
	private void getDataFromRMI() {
		Registry reg;

		try {
	        final ILdapService service = Activator.getDefault().getLdapService();
	        if (service == null) {
	            LOG.error("LDAP service unavailable."); //$NON-NLS-1$
	            return;
	        }
	        String pvName = _record;
	        if (pvName.contains(".") && ProcessVariableAdressFactory.getInstance().getDefaultControlSystem() == ControlSystemEnum.EPICS) {
	            pvName = pvName.substring(0, pvName.indexOf("."));
	        }
	        
	        final ILdapSearchResult result =
	            service.retrieveSearchResultSynchronously(LdapUtils.createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
	                                                      RECORD.getNodeTypeName() + FIELD_ASSIGNMENT + pvName,
	                                                      SearchControls.SUBTREE_SCOPE);
	        if (!result.getAnswerSet().isEmpty()) {
	            final SearchResult row = result.getAnswerSet().iterator().next();
	            LdapName ldapName;
	            try {
	                ldapName = service.parseSearchResult(row);
	                _nameIOC = LdapNameUtils.getValueOfRdnType(ldapName, IOC.getNodeTypeName());
	                if (_nameIOC == null) {
	                    LOG.error("No IOC was found for PV: " + _record); //$NON-NLS-1$
	                }
	            } catch (LdapServiceException e) {
	                LOG.error("Naming exception while parsing the search result for " + _record + " from LDAP.", e);
                }
	            _nameIOC = "";
	        }

			final IPreferencesService prefs = Platform.getPreferencesService();
			final String registryHost = prefs.getString(
					"org.csstudio.config.savevalue.ui", "RmiRegistryServer",
					null, null);
			LOG.info("Connecting to RMI registry."); //$NON-NLS-1$
			reg = LocateRegistry.getRegistry(registryHost);

			final ChangelogService cs = (ChangelogService) reg
					.lookup("SaveValue.changelog"); //$NON-NLS-1$
			_entryRMI = cs.readChangelog(_nameIOC);

		} catch (final RemoteException e) {
			LOG.error("Could not connect to RMI registry", e); //$NON-NLS-1$

		} catch (final NotBoundException e) {
			LOG.error("Changelog Service not bound in RMI registry", e); //$NON-NLS-1$

		} catch (final SaveValueServiceException e) {
			LOG.error("Server reported an error reading the changelog", e); //$NON-NLS-1$

		}
	}

	/**
	 * Gets field names from RDB (SQL).
	 */
	private boolean getFieldNamesFromRDBsql() {
		_connectForFieldNames = new DBConnect(new OracleSettings());

		_connectForFieldNames.openConnection();

		try {
			resultSetFieldNames = _connectForFieldNames
					.executeQuery("select tv.field_name, tv.field_type from epics_version ev, "
							+ "rec_type rt, type_val tv where ev.epics_id = '4061' and "
							+ "ev.epics_id = rt.epics_id and rt.record_type = '"
							+ _rtype + "' and " + "rt.type_id = tv.type_id");

			return true;

		} catch (final SQLException e) {

			return false;
		}
	}

	/**
	 * Gets field names from RDB. It is used only when normal RDB connect fails.
	 */
	private void getFieldNamesFromRDB() {

		getDataFromRMI();

		try {
			// Goes through every row and gets data.
			while (resultSetFieldNames.next()) {
				fieldName = resultSetFieldNames.getString("FIELD_NAME");
				_fieldType = resultSetFieldNames.getString("FIELD_TYPE");

				final String badType = "15";

				if (!_fieldType.equals(badType)) {
					getDataFromDAL();
				} else {
					_value = _na;
				}

				// If IOC(RMI)(4th column) does not have any data, it prints
				// this.
				_valueRMI = _na;

				// Search if record.fieldName matches one in IOC(RMI) and sets
				// it.
				for (final ChangelogEntry element : _entryRMI) {
					if ((_record + "." + fieldName).equals(element
							.getPvName())) {
						_valueRMI = element.getValue();
						break;
					}
				}

				// Adds new line to table
				final RecordPropertyEntry entry = new RecordPropertyEntry(fieldName,
						"", _value, _valueRMI);
				data.add(entry);

				// Set value back to 'not found', to overwrite last value.
				_value = _na;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		_stringArray = data
				.toArray(new RecordPropertyEntry[data.size()]);

		connect.closeConnection();
	}

	/**
	 * Deletes last part of record name, if it is less than 6 char and a dot,
	 * using Regular Expressions.
	 *
	 * Sample: record name: "recordname_type.xxxx" then returns
	 * "recordname_type"
	 *
	 * @param _record
	 *            record name
	 * @return fixed record name
	 */
	private String validateRecord(String _record) {

		final String REGEX = "(\\.[a-zA-Z1-9]{0,6})$";

		final Pattern p = Pattern.compile(REGEX);
		final Matcher m = p.matcher(_record);

		_record = m.replaceAll("");

		return _record;
	}

	/**
	 * Extracts record type out of a record name by deleting everything but
	 * record type, using Regular Expressions.
	 *
	 * Sample: record name: "record:.name_type" then returns: "type"
	 *
	 * @param _record
	 *            record name
	 * @return type of a record
	 */
	private String getRtypeFromRecordName(String _record) {

		if (_record.indexOf("_") > -1) {
			final String REGEX = "^([a-zA-Z1-9\\.:]+_){1,6}";

			final Pattern p = Pattern.compile(REGEX);
			final Matcher m = p.matcher(_record);

			_record = m.replaceAll("");
		} else {
			_record = "not-valid";
		}

		return _record;
	}
}

package org.csstudio.sds.service.pvvalidation.mkk;

import java.sql.Connection;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback.ValidationResult;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MkkDbExample {

    private static final Logger LOG = LoggerFactory.getLogger(MkkDbExample.class);
	
    private String _url = "jdbc:oracle:thin:@(DESCRIPTION = "
            + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521)) "
            + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521)) "
            + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521)) "
            + "(LOAD_BALANCE = yes) " + "(CONNECT_DATA = "
            + "(SERVER = DEDICATED) " + "(SERVICE_NAME = desy_db.desy.de) "
            + "(FAILOVER_MODE = " + "(TYPE = NONE) " + "(METHOD = BASIC) "
            + "(RETRIES = 180) " + "(DELAY = 5) " + ")" + ")" + ")";

    private String _user = "kryklogt";
    private String _password = "kryklogt";

	
	public ValidationResult checkPv(IProcessVariableAddress _pvAdress) {
		RDBUtil _rdbUtil;
		Connection connection;
		try {
			_rdbUtil = RDBUtil.connect(_url, _user, _password, true);
			connection = _rdbUtil.getConnection();
		} catch (final Exception e) {
			LOG.error("SQL Connection error ", e);
		}
		return ValidationResult.VALID;
	}

}

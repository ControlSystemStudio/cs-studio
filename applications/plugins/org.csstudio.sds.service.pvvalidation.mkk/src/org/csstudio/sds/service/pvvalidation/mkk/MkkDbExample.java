package org.csstudio.sds.service.pvvalidation.mkk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback;
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

    private String _user = "mkklog";
    private String _password = "mkklog";

    private String _pvStmt = "select count(*) from EPICS_REC where RECORD_NAME like ?";
	
	public void checkPv(List<IProcessVariableAddress> _pvAdresses,
				IProcessVariableAddressValidationCallback _callback) {
		RDBUtil _rdbUtil;
		Connection connection;
		ValidationResult result;
		try {
			_rdbUtil = RDBUtil.connect(_url, _user, _password, true);
			connection = _rdbUtil.getConnection();
			
	        final PreparedStatement checkPvName =
	                connection.prepareStatement(_pvStmt);
	        
	        for (IProcessVariableAddress pvAddress : _pvAdresses) {
	        	String rawName = pvAddress.getRawName();
	        	checkPvName.setString(1, rawName);
	        	ResultSet query = checkPvName.executeQuery();
	            if(query.next()) {
	            	int pvCount = Integer.parseInt(query.getString(1));
					if (pvCount == 0) {
	            		System.out.println(rawName + "no pv");
	            		result = ValidationResult.INVALID;
	            	} else {
	            		result = ValidationResult.VALID;
	            		System.out.println(rawName + pvCount);
	            	}
	            } else {
	            	System.out.println(rawName + "error");
	            	result = ValidationResult.INVALID;
	            }
	            _callback.onValidate(pvAddress, result, null);
	        }
		} catch (final Exception e) {
			LOG.error("SQL Connection error ", e);
		}
	}
}

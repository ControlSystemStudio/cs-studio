package org.csstudio.diag.pvfields.sns;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.diag.pvfields.DataProvider;
import org.csstudio.diag.pvfields.PVField;
import org.csstudio.diag.pvfields.PVInfo;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Data provider for SNS
 * 
 *  <p>Fetches channel information from RDB
 *  (Oracle, similar to older IRMIS)
 *  
 *  @author Kay Kasemir
 *  @author Dave Purcell - Original SQL code
 */
public class SNSDataProvider implements DataProvider
{
	final private static String PREFIX = "Irmis: ";
	
    @Override
    public PVInfo lookup(final String name) throws Exception
    {
        final String url = "jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=OFF)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=snsapp1a.sns.ornl.gov)(PORT=1610))(ADDRESS=(PROTOCOL=TCP)(HOST=snsapp1b.sns.ornl.gov)(PORT=1610))(CONNECT_DATA=(SERVICE_NAME=ics_prod_lba)))";
        final String user = "sns_reports";
        final String password = "sns";
        final RDBUtil rdb = RDBUtil.connect(url, user, password, false);
    
        final Map<String, String> properties = new HashMap<String, String>();
        final List<PVField> fields = new ArrayList<PVField>();

        try
        {
            final PreparedStatement statement = rdb.getConnection().prepareStatement(
                "SELECT fld_id, fld_val, rec_type_id, ioc_nm, file_nm, boot_dte" +
                " FROM epics.sgnl_fld_v" +
                " WHERE sgnl_id=?");
            statement.setString(1, name);
            final ResultSet result = statement.executeQuery();
            if (result.next())
            {
                properties.put(PREFIX + "Record Type", result.getString(3));
                properties.put(PREFIX + "IOC Name", result.getString(4));
                properties.put(PREFIX + "File Name", result.getString(5));
                properties.put(PREFIX + "Last Boot Time", result.getString(6));
    
                // Get first field
                fields.add(new PVField(name + "." + result.getString(1), result.getString(2)));
                // Get remaining fields
                while (result.next())
                    fields.add(new PVField(name + "." + result.getString(1), result.getString(2)));
            }
        }
        finally
        {
            rdb.close();
        }

        final PVInfo info = new PVInfo(properties, fields);
        Logger.getLogger(getClass().getName()).log(Level.FINE, "SNS Info for {0}: {1}", new Object[] { name, info });
		return info;
    }
}

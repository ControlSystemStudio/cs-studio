package org.csstudio.diag.pvfields;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.utility.rdb.RDBUtil;

public class SNSDataProvider implements DataProvider
{
    @Override
    public void run(final String name, final PVModelListener listener) throws Exception
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
                properties.put("Record Type", result.getString(3));
                properties.put("IOC Name", result.getString(4));
                properties.put("File Name", result.getString(5));
                properties.put("Boot Time", result.getString(6));
    
                // Get first field
                fields.add(new PVField(result.getString(1), result.getString(2)));
                // Get remaining fields
                while (result.next())
                    fields.add(new PVField(result.getString(1), result.getString(2)));
            }
        }
        finally
        {
            rdb.close();
        }

        listener.updateProperties(properties);
        listener.updateFields(fields.toArray(new PVField[fields.size()]));
    }
}

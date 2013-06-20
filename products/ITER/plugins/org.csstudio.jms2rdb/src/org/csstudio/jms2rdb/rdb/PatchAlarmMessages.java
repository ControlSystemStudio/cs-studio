package org.csstudio.jms2rdb.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.csstudio.platform.utility.rdb.RDBUtil;

/** Patch alarm messages: Attach a 'CONFIG' property unless there is one already.
 *
 *  One-time manual patch tool for SNS, left here in case something similar is needed later on.
 *
 *  This cannot run while another process like JMS2RDB is inserting
 *  messages since they'll collide with duplicate MSG_LOG.MESSAGE_CONTENT.ID
 *  values.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PatchAlarmMessages
{
    private static PreparedStatement next_prop_id_sel;
    private static PreparedStatement insert_prop;
    private static RDBUtil rdb;

    public static void main(String[] args) throws Exception
    {
        final String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.138)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.141)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=ics_prod_lba)))";
        final String user = "ics_msg_log_app";
        final String password = "SET ME";

        rdb = RDBUtil.connect(url, user, password, false);
        try
        {
            final Connection connection = rdb.getConnection();
            connection.setAutoCommit(false);

            next_prop_id_sel = connection.prepareStatement("SELECT MAX(ID) FROM MSG_LOG.MESSAGE_CONTENT");

            insert_prop = connection.prepareStatement("INSERT INTO MSG_LOG.MESSAGE_CONTENT(ID,MESSAGE_ID,MSG_PROPERTY_TYPE_ID,VALUE) VALUES(?,?,?,?)");

            // Get all 'alarm' messages
            final PreparedStatement msg_sel =
                connection.prepareStatement("SELECT ID FROM MSG_LOG.MESSAGE WHERE TYPE=? AND ID > 12872 AND ID < 50000");
            msg_sel.setString(1, "alarm");

            // Happen to know that 16 = 'CONFIG'
            final PreparedStatement prop_sel =
                connection.prepareStatement("SELECT VALUE FROM MSG_LOG.MESSAGE_CONTENT WHERE MESSAGE_ID=? AND MSG_PROPERTY_TYPE_ID=16");
            final ResultSet msg_res = msg_sel.executeQuery();
            while (msg_res.next())
            {
                // Does message have a 'CONFIG' entry?
                final int msg_id = msg_res.getInt(1);
                prop_sel.setInt(1, msg_id);
                final ResultSet prop_res = prop_sel.executeQuery();
                if (prop_res.next())
                    System.out.println(msg_id + ": " + prop_res.getString(1));
                else
                {
                    System.out.println(msg_id + ": No CONFIG");
                    addConfig(msg_id);
                }
                prop_res.close();
            }
            prop_sel.close();
            msg_sel.close();
        }
        finally
        {
            rdb.close();
        }
    }

    private static void addConfig(final int msg_id) throws Exception
    {
        final ResultSet result = next_prop_id_sel.executeQuery();
        if (! result.next())
            throw new Exception("No new ID");
        final int prop_id = result.getInt(1) + 1;
        result.close();

        insert_prop.setInt(1, prop_id);
        insert_prop.setInt(2, msg_id);
        insert_prop.setInt(3, 16);
        insert_prop.setString(4, "Annunciator");
        final int updates = insert_prop.executeUpdate();
        if (updates != 1)
            throw new Exception("Cannot add CONFIG");
        rdb.getConnection().commit();
    }
}

package org.csstudio.archive.rdb.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.logging.CentralLogger;

/** Enumeration Strings for a channel.
 *  <p>
 *  Presented as an array of strings for the enumerated values 0, 1, 2, ...
 *  The case where no enum strings are defined is represented by
 *  <code>null</code> EnumStrings.
 *  
 *  @author Kay Kasemir
 */
public class EnumMetaDataHelper
{
    private EnumMetaDataHelper()
    {
        // prevent instantiation
    }
    
    /** Set the enum strings for a channel.
     *  This either sets new strings or modifies the existing ones.
     *  @param archive Archive connection
     *  @param channel Channel for which to set the enums
     *  @param strings Enum strings. May be <code>null</code> to clear.
     *  @return EnumStrings that were set, or <code>null</code> when cleared.
     *  @throws Exception on error
     */
    public static void set(final RDBArchiveImpl archive,
            final ChannelConfig channel, final IEnumeratedMetaData meta) throws Exception
    {
        // See if already defined as needed.
        final IEnumeratedMetaData found = get(archive, channel);
        if (meta == null)
        {
            if (found != null)
            {   // Clear existing enum info
                delete(archive, channel);
                archive.getRDB().getConnection().commit();
            }
            return;
        }
        if (found != null && meta.equals(found))
            return;
        // Delete old entries, then insert current ones.
        delete(archive, channel);
        insert(archive, channel, meta);
        archive.getRDB().getConnection().commit();
    }

    /** Helper: Delete enum info in archive for channel. */
    private static void delete(final RDBArchiveImpl archive,
            final ChannelConfig channel) throws Exception
    {
        // Delete any existing entries
        final Connection connection = archive.getRDB().getConnection();
        final PreparedStatement del = connection.prepareStatement(
                archive.getSQL().enum_delete_by_channel);
        try
        {
            del.setInt(1, channel.getId());
            del.executeUpdate();
        }
        finally
        {
            del.close();
        }
    }

    /** Helper: Insert enum info into archive. */
    @SuppressWarnings("nls")
    private static void insert(final RDBArchiveImpl archive,
            final ChannelConfig channel, final IEnumeratedMetaData meta) throws Exception
    {
        final Connection connection = archive.getRDB().getConnection();
        // Define the new ones
        final PreparedStatement insert = connection.prepareStatement(
                archive.getSQL().enum_insert_channel_num_val);
        try
        {
            final String[] states = meta.getStates();
            for (int i=0; i<states.length; ++i)
            {
                insert.setInt(1, channel.getId());
                insert.setInt(2, i);
                // Oracle doesn't allow empty==null state strings.
                String state = states[i];
                if (state == null  ||  state.length() < 1)
                {   // Patch as "<#>"
                    state = "<" + i + ">";
                    CentralLogger.getInstance().getLogger(EnumMetaDataHelper.class)
                        .warn("Channel '" + channel.getName() +
                        "' has undefined state " + state);
                }
                insert.setString(3, state);
                insert.addBatch();
            }
            insert.executeBatch();
        }
        finally
        {
            insert.close();
        }
    }

    /** Locate enum strings for a channel.
     *  @param archive Archive to search
     *  @param channel Channel for which to get enum info
     *  @return {@link IEnumeratedMetaData} or null if nothing found
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public
    static IEnumeratedMetaData get(final RDBArchiveImpl archive,
            final ChannelConfig channel) throws Exception
    {
        final ArrayList<String> enums = new ArrayList<String>();
        final PreparedStatement sel = archive.getRDB().getConnection().prepareStatement(
            archive.getSQL().enum_sel_num_val_by_channel);
        try
        {
            sel.setInt(1, channel.getId());
            final ResultSet res = sel.executeQuery();
            while (res.next())
            {
                final int id = res.getInt(1);
                final String val = res.getString(2);
                // Expect vals for ids 0, 1, 2, ...
                if (id != enums.size())
                    throw new Exception("Enum IDs for channel "
                            + channel.getName() + " not in sequential order");
                enums.add(val);
            }
        }
        finally
        {   // RDB Cleanup
            sel.close();
        }
        // Anything found?
        if (enums.size() <= 0)
            return null; // Nothing found
        // Convert to plain array, then IEnumeratedMetaData
        final String states[] = new String[enums.size()];
        return ValueFactory.createEnumeratedMetaData(enums.toArray(states));
    }
}

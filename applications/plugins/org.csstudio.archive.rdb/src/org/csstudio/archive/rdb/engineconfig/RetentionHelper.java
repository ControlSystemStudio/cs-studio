package org.csstudio.archive.rdb.engineconfig;

import java.util.HashMap;

import org.csstudio.archive.rdb.Retention;
import org.csstudio.archive.rdb.internal.SQL;
import org.csstudio.utility.rdb.RDBUtil;
import org.csstudio.utility.rdb.StringID;
import org.csstudio.utility.rdb.StringIDHelper;

/** Helper for putting Retention info into RDB
 *  @author Kay Kasemir
 */
public class RetentionHelper
{
    private final StringIDHelper helper;
    
    private final HashMap<String, Retention> cache =
        new HashMap<String, Retention>();

    public RetentionHelper(final RDBUtil rdb, final SQL sql)
    {
        helper = new StringIDHelper(rdb,
            sql.retention_table, sql.retention_id_column, sql.retention_name_column);
    }
    
    /** Locate retention, either from cache or RDB
     *  @param description Description to locate
     *  @return Retention with that description
     *  @throws Exception on error
     */
    public Retention getRetention(final String description) throws Exception
    {
        Retention retention = cache.get(description);
        if (retention == null)
        {
            final StringID string_id = helper.add(description);
            retention = new Retention(string_id.getId(), string_id.getName());
            cache.put(description, retention);
        }
        return retention;
    }

    /** Must be called to release RDB resources */
    public void dispose()
    {
        helper.dispose();
    }
}

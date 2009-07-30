package org.csstudio.archive.rdb.engineconfig;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;

/** Export engine configuration as XML (to stdout)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLExport
{
    /** Create and run XML export
     *  @param rdb_url     RDB URL
     *  @param user        RDB user name
     *  @param password    RDB password
     *  @param engine_name Engine to export
     *  @throws Exception on error
     */
    public XMLExport(final String rdb_url, final String user, final String password,
            final String engine_name) throws Exception
    {
        RDBArchive archive = RDBArchive.connect(rdb_url, user, password);
        
        final SampleEngineConfig engine = archive.findEngine(engine_name);
        if (engine == null)
            throw new Exception("Unknown engine '" + engine_name + "'");
        dumpEngine(engine);
    }

    private void dumpEngine(final SampleEngineConfig engine) throws Exception
    {
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        System.out.println("<engineconfig>");
        final ChannelGroupConfig[] groups = engine.getGroups();
        for (ChannelGroupConfig group : groups)
            dumpGroup(group);
        System.out.println("</engineconfig>");
    }

    private void dumpGroup(final ChannelGroupConfig group) throws Exception
    {
        System.out.println("  <group>");
        System.out.println("  <name>" + group.getName() + "</group>");
        final ChannelConfig[] channels = group.getChannels();
        for (ChannelConfig channel : channels)
            dumpChannel(channel);
        System.out.println("  </group>");
    }

    private void dumpChannel(final ChannelConfig channel)
    {
        System.out.print("    <channel>");
        System.out.print("<name>" + channel.getName() + "</name>");
        System.out.print("<period>" + channel.getSamplePeriod() + "</period>");
        if (channel.getSampleMode().isMonitor())
            System.out.print("<monitor/>");
        else
            System.out.print("<scan/>");
        System.out.println("</channel>");
    }
}

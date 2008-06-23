package org.csstudio.archive.dump;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;


public class Dumper
{
    final static private String URL = "jdbc:oracle:thin:chan_arch1/sns@//snsdb1.sns.ornl.gov:1521/prod";
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            final ArgParser parser = new ArgParser();
            final StringOption url = new StringOption(parser, "-url", "URL", URL);
            final StringOption name = new StringOption(parser, "-channel", "Channel", "");
            parser.parse(args);
            
            if (name.get().length() <= 0)
            {
                System.out.println("Missing channel name");
                System.out.println(parser.getHelp());
                return;
            }
            
            final RDBArchive archive = RDBArchive.connect(url.get());
            final ChannelConfig channel = archive.getChannel(name.get());
            ITimestamp start = TimestampFactory.fromDouble(1.0);
            ITimestamp end = TimestampFactory.now();
            final SampleIterator samples = channel.getSamples(start, end);
            while (samples.hasNext())
            {
                System.out.println(samples.next());
            }
            
            archive.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

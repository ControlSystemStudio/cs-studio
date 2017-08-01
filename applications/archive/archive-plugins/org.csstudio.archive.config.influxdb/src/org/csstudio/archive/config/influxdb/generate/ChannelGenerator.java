package org.csstudio.archive.config.influxdb.generate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;
import org.csstudio.archive.config.xml.XMLArchiveConfig;
import org.csstudio.archive.config.xml.XMLChannelConfig;
import org.csstudio.archive.config.xml.XMLEngineConfig;
import org.csstudio.archive.config.xml.XMLGroupConfig;
import org.csstudio.archive.influxdb.Activator;
import org.csstudio.archive.influxdb.MetaTypes.StoreAs;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.writer.WriteChannel;
import org.csstudio.archive.writer.influxdb.InfluxDBArchiveWriter;
import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.ValueFactory;

public class ChannelGenerator {

    public static long FLUSH_COUNT = 10000;
    protected long flush_counter;

    final Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(2), 8.0, 9.0, 10.0, 0.0,
            10.0);

    final InfluxDBArchiveWriter writer;

    public static class ChanInfo {
        public final String name;
        protected final StoreAs storeas;

        public ChanInfo(final String name, final StoreAs storeas) {
            this.name = name;
            this.storeas = storeas;
        }

        @Override
        public String toString() {
            return name + ":" + storeas.toString();
        }
    }

    public static class TickSet implements Comparable<TickSet> {
        public final double period;
        public Instant next_tick;
        public final List<ChanInfo> chans;

        private final double[] dvals;
        private int cur_dval;

        double getDoubleValue() {
            return dvals[cur_dval];
        }

        double nextDoubleValue() {
            cur_dval++;
            if (cur_dval >= dvals.length)
                cur_dval = 0;

            return dvals[cur_dval];
        }

        public TickSet(final double period, final Instant start_ts) {
            this.period = period;
            next_tick = start_ts;

            chans = new ArrayList<ChanInfo>();
            dvals = new double[360];

            for (int deg = 0; deg < 360; deg++) {
                dvals[deg] = 100.0 * Math.sin(Math.toRadians(deg));
                cur_dval = 0;
            }
        }

        @Override
        public int compareTo(TickSet o) {
            return (next_tick.isBefore(o.next_tick)) ? -1 : ((next_tick.equals(o.next_tick)) ? 0 : 1);
        }

        public void addChanDouble(final String name) {
            chans.add(new ChanInfo(name, StoreAs.ARCHIVE_DOUBLE));
        }

        @Override
        public String toString() {
            return "Tickset[" + period + "], " + chans.size() + " Channels, Next=" + next_tick;
        }
    }

    protected TickSet[] ticks;

    public ChannelGenerator(XMLArchiveConfig config, InfluxDBArchiveWriter writer, Instant start_ts,
            boolean skipPVSample) throws Exception {

        this.writer = writer;
        this.flush_counter = 0;

        Map<Double, TickSet> ticksets = new HashMap<Double, TickSet>();

        for (EngineConfig engine : config.getEngines()) {
            XMLEngineConfig the_engine = (XMLEngineConfig) engine;
            for (GroupConfig group : the_engine.getGroupsArray()) {
                XMLGroupConfig the_group = (XMLGroupConfig) group;
                for (ChannelConfig chan : the_group.getChannelArray()) {
                    XMLChannelConfig the_chan = (XMLChannelConfig) chan;

                    Double period = the_chan.getSampleMode().getPeriod();
                    if (period == 0) {
                        Activator.getLogger().log(Level.WARNING,
                                "Got bad period (0) for PV " + the_chan.getName() + " set to default of 1");
                        period = 1.0;
                    }

                    if (!ticksets.containsKey(period)) {
                        ticksets.put(period, new TickSet(period, start_ts));
                    }
                    final TickSet tset = ticksets.get(period);

                    if (skipPVSample) {
                        tset.addChanDouble(the_chan.getName());
                    } else {
                        // TODO: determine channel type from PV access
                        tset.addChanDouble(the_chan.getName());
                    }
                }
            }
        }

        ticks = new TickSet[ticksets.size()];
        ticks = ticksets.values().toArray(ticks);
        Arrays.sort(ticks);
    }

    private void printState() {
        System.out.println("\n*****************************");
        for (TickSet ts : ticks) {
            System.out.println(ts);
            for (ChanInfo ci : ts.chans) {
                System.out.println("\t" + ci);
            }
        }
    }

    public Instant step() throws Exception {

        // printState();

        double val = ticks[0].nextDoubleValue();
        for (ChanInfo ci : ticks[0].chans) {
            WriteChannel channel = writer.getChannel(ci.name);
            writer.addSample(channel, new ArchiveVNumber(ticks[0].next_tick, AlarmSeverity.NONE, "OK", display, val));

            if (++flush_counter >= FLUSH_COUNT) {
                writer.flush();
                flush_counter = 0;
            }

        }

        Double millis = ticks[0].period * 1000;
        ticks[0].next_tick = ticks[0].next_tick.plusMillis(millis.longValue());
        Arrays.sort(ticks);

        return ticks[0].next_tick;
    }

}

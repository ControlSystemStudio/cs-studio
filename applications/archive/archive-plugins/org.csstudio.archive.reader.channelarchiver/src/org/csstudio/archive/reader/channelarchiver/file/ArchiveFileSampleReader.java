/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver.file;

import static org.csstudio.archive.reader.channelarchiver.file.ArchiveFileReader.logger;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;

import gov.aps.jca.dbr.Status;

/**
 * Obtains channel archiver samples from channel archiver
 * data files, and translates them to ArchiveVTypes.
 * <p>Note: Does not currently support data in multiple sub-archives.
 * @author Amanda Carpenter
 *
 */
public class ArchiveFileSampleReader implements ValueIterator
{
    private final Instant iteratorStop;

    /** Data headers from which samples will be read.
     *  Entries are removed as the 'buffer' and 'header' are set to them.
     */
    private final Queue<DataFileEntry> entries;

    private final ArchiveFileBuffer buffer = new ArchiveFileBuffer();

    private DataHeader header;

    /** sample that will be returned by nextSample(), or else <code>null</code> */
    private ArchiveVType next;

    private long samples_left;


    public ArchiveFileSampleReader(final Instant iteratorStart, final Instant iteratorStop,
                                   final List<DataFileEntry> entries) throws Exception
    {
        this.iteratorStop = iteratorStop;

        this.entries = new ArrayDeque<>(entries);

        if (this.entries.isEmpty())
        {
            next = null;
            samples_left = 0;
        }
        else
        {
            final DataFileEntry entry = this.entries.remove();
            buffer.setFile(entry.file);
            buffer.offset(entry.offset);
            header = DataHeader.readDataHeader(buffer, new CtrlInfoReader(0));
            samples_left = binarySearchSamples(iteratorStart);
        }
    }

    /** Searches for samples in the buffer, starting from its current offset, using the information in
     *  this.header.
     *  Finds sample with closest timestamp at-or-just-below 'time'.
     *  @return Samples left after 'next', with 'next' set to the first one, and buffer just after that sample,
     *          or -1 if no sample found.
     */
    private long binarySearchSamples(final Instant time) throws IOException
    {
        final int size = header.dbrType.getSize(header.dbrCount);
        final long initOffset = buffer.offset();
        long low = 0;
        long high = header.numSamples-1;
        while (low <= high)
        {
            long mid = (low + high) / 2;
            buffer.offset(initOffset + mid * size);
            final ArchiveVType sample = getSample(header.dbrType, header.dbrCount, header.info, buffer);
            final int compare = sample.getTimestamp().compareTo(time);
            if (compare > 0)
            {
                // 'mid' is after the start time, search lower half
                high = mid - 1;

                if (low > high)
                {   // No lower half, and mid is too large.
                    // If there is a sample before mid, use that.
                    if (mid > 0)
                    {
                        --mid;
                        buffer.offset(initOffset + mid * size);
                        next = getSample(header.dbrType, header.dbrCount, header.info, buffer);
                        return header.numSamples - mid - 1;
                    }
                    // Mid is after the requested start time, so use it
                    next = sample;
                    return header.numSamples - mid - 1;
                }
            }
            else if (compare < 0)
            {
                // 'mid' is before the start time, search upper half
                low = mid + 1;

                if (low > high)
                {   // There is no upper half, so 'mid' is it!
                    next = sample;
                    return header.numSamples - mid - 1;
                }
            }
            else
            {   // Perfect match
                next = sample;
                return header.numSamples - mid - 1;
            }
        }
        next = null;
        return -1;
    }

    private ArchiveVType nextSample() throws IOException
    {
        if (samples_left <= 0)
        {
            if (entries.isEmpty())
                return null;
            // Use next data block
            final DataFileEntry entry = this.entries.remove();
            buffer.setFile(entry.file);
            buffer.offset(entry.offset);
            header = DataHeader.readDataHeader(buffer, header.info);
            // Start on the first sample, no need to search for 'start' time
            samples_left = header.numSamples;
            // Is new data block empty?
            if (samples_left <= 0)
                return null;
        }
        ArchiveVType sample = getSample(header.dbrType, header.dbrCount, header.info, buffer);
        --samples_left;
        if (sample.getTimestamp().compareTo(iteratorStop) <= 0)
            return sample;
        else
            return null;
    }

    @Override
    public boolean hasNext()
    {
        return next != null;
    }

    @Override
    public VType next() throws IOException
    {
        final VType ret = next;
        try
        {
            next = nextSample();
        }
        catch (IOException ex)
        {
            next = null;
            throw ex;
        }
        return ret;
    }

    @Override
    public void close()
    {
        try
        {
            buffer.close();
        }
        catch (IOException ex)
        {
            logger.log(Level.WARNING, "Cannot close data file buffer", ex);
        }
    }

    /** Read sample at current 'buffer' offset */
    private static ArchiveVType getSample(DbrType dbrType, short dbrCount,
            CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
    {
        short statusCode = dataBuff.getShort();
        short severity = dataBuff.getShort();
        Instant timestamp = dataBuff.getEpicsTime();
        dataBuff.skip(dbrType.padding);
        AlarmSeverity sev = getSeverity(severity);
        String stat = getStatus(severity, statusCode);
        Display display = info.getDisplay(dataBuff);
        ArchiveVType sample = null;
        switch (dbrType)
        {
            case DBR_TIME_STRING:
                assert dbrCount == 1 : "String type DBR value must be scalar (count = 1).";
                dbrCount = 40; //read as a string of 40 chars
            case DBR_TIME_CHAR:
                byte valueBytes [] = new byte [dbrCount];
                dataBuff.get(valueBytes);
                dataBuff.skip(dbrType.getValuePad(dbrCount));
                String strValue = new String(valueBytes).split("\0", 2)[0];
                sample = new ArchiveVString(timestamp, sev, stat, strValue);
                break;
            case DBR_TIME_ENUM:
                assert dbrCount == 1 : "Enum type DBR value must be scalar (count = 1).";
                List<String> labels = info.getLabels(dataBuff);
                int index = dataBuff.getShort();
                sample = new ArchiveVEnum(timestamp, sev, stat, labels, index);
                break;
            case DBR_TIME_FLOAT:
                if (dbrCount == 1)
                {
                    float value = dataBuff.getFloat();
                    sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
                }
                else
                {
                    double value [] = new double [dbrCount];
                    for (int i = 0; i < dbrCount; ++i)
                        value[i] = dataBuff.getFloat();
                    dataBuff.skip(dbrType.getValuePad(dbrCount));
                    sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
                }
                break;
            case DBR_TIME_DOUBLE:
                if (dbrCount == 1)
                {
                    double value = dataBuff.getDouble();
                    sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
                }
                else
                {
                    double value [] = new double [dbrCount];
                    for (int i = 0; i < dbrCount; ++i)
                        value[i] = dataBuff.getDouble();
                    dataBuff.skip(dbrType.getValuePad(dbrCount));
                    sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
                }
                break;
            case DBR_TIME_SHORT: //==DBR_TIME_INT
                if (dbrCount == 1)
                {
                    short value = dataBuff.getShort();
                    sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
                    dataBuff.skip(dbrType.getValuePad(dbrCount));
                }
                else
                {
                    int value [] = new int [dbrCount];
                    for (int i = 0; i < dbrCount; ++i)
                        value[i] = dataBuff.getShort();
                    dataBuff.skip(dbrType.getValuePad(dbrCount));
                    sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
                }
                break;
            case DBR_TIME_LONG:
                if (dbrCount == 1)
                {
                    int value = dataBuff.getInt();
                    sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
                }
                else
                {
                    int value [] = new int [dbrCount];
                    for (int i = 0; i < dbrCount; ++i)
                        value[i] = dataBuff.getInt();
                    dataBuff.skip(dbrType.getValuePad(dbrCount));
                    sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
                }
                break;
        } //end switch(type)
        return sample;
    }

    //Dbr types (defines how data is arranged in file)
    enum DbrType
    {
        DBR_TIME_STRING(14, 0, 40),
        DBR_TIME_SHORT(15, 2, 2), //==DBR_TIME_INT
        DBR_TIME_FLOAT(16, 0, 4),
        DBR_TIME_ENUM(17, 2, 2),
        DBR_TIME_CHAR(18, 3, 1),
        DBR_TIME_LONG(19, 0, 4),
        DBR_TIME_DOUBLE(20, 4, 8);

        public final int typeCode;
        public final int padding;
        public final int valueSize;
        private DbrType(int type, int padding, int valueSize)
        {
            this.typeCode = type;
            this.padding = padding;
            this.valueSize = valueSize;
        }

        public static DbrType forValue(int typeCode)
        {
            return DbrType.values()[typeCode-14];
        }

        //DBR types, when stored in files, are padded for alignment;
        //that is, so that their size is a multiple of 8. This includes
        //both the struct as defined (which only includes the first value)
        //and the "true" data structure (which includes an array of values
        //of arbitrary size).
        public int getValuePad(int count)
        {
            int remainder = getUnpaddedSize(count) % 8;
            return remainder != 0 ? 8 - remainder : 0;
        }

        public int getSize(int count)
        {
            int size = getUnpaddedSize(count);
            if (size % 8 != 0)
                size += 8 - size%8;
            return size;
        }

        private int getUnpaddedSize(int count)
        {
            return 12 + padding + valueSize * count;
        }
    }

    private static AlarmSeverity getSeverity(final short severity)
    {
        if ((severity & 0x0F00) != 0) //special archiver values
            return AlarmSeverity.UNDEFINED;
        AlarmSeverity severities [] = AlarmSeverity.values();
        if (severity < severities.length && severity >= 0)
            return severities[severity];
        return AlarmSeverity.NONE;
    }

    private static String getStatus(final short severity, final short status)
    {
        if (severity == 0x0f80)
            return "Est_Repeat " + status;
        if (severity == 0x0f10)
            return "Repeat " + status;
        if (severity == 0x0f40)
            return "Disconnected";
        if (severity == 0x0f20)
            return "Archive_Off";
        if (severity == 0x0f08)
            return "Archive_Disabled";
        if (severity == 0x0f02)
            return "Change Sampling Period";

        try
        {
            final Status stat = Status.forValue(status);
            // stat.toString()?
            return stat.getName();
        }
        catch (Exception ex)
        {
            return  "<" + status + ">";
        }
    }
}
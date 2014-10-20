package org.csstudio.scan.server.pvaccess;

/**
 * Created with IntelliJ IDEA.
 * User: berryman
 * Date: 5/20/13
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.epics.pvaccess.PVFactory;
import org.epics.pvaccess.client.Lockable;
import org.epics.pvdata.factory.StandardFieldFactory;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Scalar;
import org.epics.pvdata.pv.Structure;

public class PVTopStructure implements Lockable
{
    public interface PVTopStructureListener {
        public void topStructureChanged(BitSet changedBitSet);
    }

    private static final PVDataCreate pvDataCreate = PVFactory.getPVDataCreate();

    private final Lock lock = new ReentrantLock();
    private final PVStructure pvStructure;
    private final ArrayList<PVTopStructureListener> listeners = new ArrayList<PVTopStructureListener>();

    public PVTopStructure(PVStructure pvStructure)
    {
        this.pvStructure = pvStructure;
    }

    public PVTopStructure(Field valueType)
    {
        if (valueType instanceof Scalar)
        {
            // TODO access via PVFactory?
            Field field =
                    StandardFieldFactory.getStandardField().
                            scalar(
                                    ((Scalar)valueType).getScalarType(),
                                    "value,timeStamp,alarm,display,control,valueAlarm");

            pvStructure = pvDataCreate.createPVStructure((Structure)field);
        }
        else if (valueType instanceof Structure)
        {
            pvStructure = pvDataCreate.createPVStructure((Structure)valueType);
        }
        else
        {
            pvStructure = null;
        }
    }

    public PVStructure getPVStructure()
    {
        return pvStructure;
    }

    public void process()
    {
        // default is noop
    }

    // TODO async
    public PVStructure request(PVStructure pvArgument)
    {
        throw new UnsupportedOperationException("not implemented");
    }


    public void lock()
    {
        lock.lock();
    }

    public void unlock()
    {
        lock.unlock();
    }

    public void registerListener(PVTopStructureListener listener)
    {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(PVTopStructureListener listener)
    {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void notifyListeners(BitSet changedBitSet)
    {
        synchronized (listeners) {
            for (PVTopStructureListener listener : listeners)
            {
                try {
                    listener.topStructureChanged(changedBitSet);
                }
                catch (Throwable th) {
                    Writer writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    th.printStackTrace(printWriter);
                    pvStructure.message("Unexpected exception caught: " + writer, MessageType.fatalError);
                }
            }
        }
    }

}
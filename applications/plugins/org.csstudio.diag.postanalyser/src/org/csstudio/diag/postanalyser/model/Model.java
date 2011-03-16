package org.csstudio.diag.postanalyser.model;

import java.util.ArrayList;
import java.util.logging.Level;

import org.csstudio.diag.postanalyser.Activator;

/** Data Model for the analyzer
 *  @author Kay Kasemir
 */
public class Model
{
    /** The supported algoithms */
    final private Algorithm algorithms[] = new Algorithm[]
    {
            new RawAlgorithm(),
            new LineFitAlgorithm(),
            new ExpFitAlgorithm(),
            new GaussFitAlgorithm(),
            new CorrelationAlgorithm(),
            new FFTAlgorithm()
    };

    /** Channels */
    final private ArrayList<Channel> channels = new ArrayList<Channel>();

    /** Listeners */
    final private ArrayList<ModelListener> listeners =
        new ArrayList<ModelListener>();

    /** Add given listener */
    public void addListener(final ModelListener listener)
    {
        listeners.add(listener);
    }

    /** Remove given listener */
    public void removeListener(final ModelListener listener)
    {
        listeners.remove(listener);
    }

    /** @return Number of algorithms */
    public int getAlgorithmCount()
    {
        return algorithms.length;
    }

    /** @return Algorithm with index 0..<code>getAlgorithmCount()-1</code>. */
    public Algorithm getAlgorithm(int index)
    {
        return algorithms[index];
    }

    /** @return Number of channels */
    public int getChannelCount()
    {
        return channels.size();
    }

    /** @return Channel with index 0..<code>getChannelCount()-1</code>. */
    public Channel getChannel(int index)
    {
        return channels.get(index);
    }

    /** @return Channel of given name
     * @throws Exception when channel not found
     */
    @SuppressWarnings("nls")
    public Channel getChannel(final String name) throws Exception
    {
        if (name.length() <= 0)
            throw new IllegalArgumentException("No channel name");
        for (Channel channel : channels)
            if (channel.getName().equals(name))
                return channel;
        throw new IllegalArgumentException("Unknown channel '" + name + "'");
    }

    /** Add a channel to the model */
    public void addChannel(final Channel channel)
    {
        // Avoid duplicates
        for (int i=0; i<channels.size(); ++i)
        {
            if (channels.get(i).getName().equals(channel.getName()))
            {
                channels.remove(i);
                break;
            }
        }
        // Add
        channels.add(channel);
        // Notify listeners
        try
        {
            for (ModelListener listener : listeners)
                listener.newChannels();
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.SEVERE, "Error adding channel", ex); //$NON-NLS-1$
        }
    }

    /** Reduce the samples of given channel to start ... end on the 'x' axis */
    public void cropChannel(final String name,
            final double start, final double end)
    {
        for (Channel channel : channels)
            if (channel.getName().equals(name))
                channel.crop(start, end);

    }

    /** Adjust all Y values of given channel to given base line */
    public void baseline(final String name, final double baseline)
    {
        for (Channel channel : channels)
            if (channel.getName().equals(name))
                channel.baseline(baseline);
    }
}

package org.csstudio.saverestore.ui.util;

/**
 * <code>RunnableWithID</code> is an extension of the Runnable which provides an additional method to extract an ID. The
 * ID identifies the runnable and helps trapping same requests before they are executed.
 *
 * @see DismissableBlockingQueue
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public interface RunnableWithID extends Runnable {

    /**
     * Returns the identifier of this runnable. Runnables that have the same ID are considered identical or
     * interchangeable. The ID is primarily used by the queue implementation to identify the same requests.
     *
     * @return the identifier of the runnable
     */
    default int getID() {
        return 0;
    }
}

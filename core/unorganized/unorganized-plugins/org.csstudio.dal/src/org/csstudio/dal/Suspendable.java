package org.csstudio.dal;

/**
 * Interface for monitors that support suspend/resume operations.
 *
 * @author Blaz Hostnik, Cosylab (blaz.hostnikATcosylab.com)
 */
public interface Suspendable {
    /**
     * Suspends the monitor to stop receiving events flow
     * from the data access event source.
     *
     * @throws UnsupportedOperationException if suspend operation is not supported.
     */
    public void suspend();

    /**
     * Resumes the monitor to start again receiving events flow
     * from the data access event source. <br>
     * <b>Note:</b> if suspend was called several times, resume
     * needs to be called same number of times to start receiving
     * events again.
     *
     * @throws UnsupportedOperationException if suspend operation is not supported.
     */
    public void resume();

    /**
     * Returns suspended state.
     * @return Returns <code>true</code>, if monitor events flow is currently
     * stopped, else <code>false</code>.
     *
     * @throws UnsupportedOperationException if suspend operation is not supported.
     */
    public boolean isSuspended();
}

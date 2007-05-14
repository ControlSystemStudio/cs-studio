package org.csstudio.platform.data;

/** The enumerated version of {@link IMetaData}.
 *  <p>
 *  Handles the mapping from enumeration integers to state strings.
 *  
 *  @see IMetaData
 *  @see IEnumeratedValue
 *  @author Kay Kasemir 
 */
public interface IEnumeratedMetaData extends IMetaData
{
    /** Obtain the state strings.
     *  <p>
     *  The array element <code>i</code> represends state number <code>i</code>.
     *  @return The state strings.
     */
    public String[] getStates();

    /** Convenience routine for getting a state string.
     *  <p>
     *  Also allows getting undefined states. Applications that want to
     *  specifically handle undefined states should use
     *  <code>getStates()</code> instead.
     *  @param state The state to get.
     *  @return A state string.
     */
    public String getState(int state);
}
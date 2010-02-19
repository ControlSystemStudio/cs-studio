package org.csstudio.opibuilder;

/** Placeholder for 'TO DO' items
 *  @author Kay Kasemir
 */
public interface TODO
{
    /** From John Hammonds <JPHammonds@anl.gov>:
     * 
     * TODO Order of Macros
     * - In the widget for adding user Macros to a widget, there are
buttons that arrange the order of the macros in the list.  After leaving
the entry form and coming back the selected order is not preserved. It
would be nice to have this order preserved, especially if the list of
entries is a somewhat ordered list (N1, N2, ...)  which have similar
values to populate a repetitive display.

      TODO Support character waveforms
    - Other EPICS display programs (e.g.) MEDM have the ability to
translate a waveform of Characters into a string.  This is useful for
entry of strings longer than the stringin/out can hold.  It would be
nice if the Text Input/Update widgets could also handle this.

utility.pv.epics handles BYTE waveforms as long waveforms.
Make ValueUtil.getString() version that turns those into string
instead of "element1, element2, element3, ..."?

     TODO Linking container macros
    - User macros that reference parent macros do not seem to work
correctly in Linking containers.  For instance in the above example,
where the user feeds macros N1, N2, ...,NX to a display1 which in turn
shows X copies of a display2 via the linking container.  Here I would
feed a macro N to display 2 such that N=N1 for the first instance and
N=N2 for the second and so on.
     */
}

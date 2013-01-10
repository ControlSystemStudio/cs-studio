/*  * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,  * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY. * * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.  * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED  * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND  * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE  * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR  * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE  * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR  * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.  * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,  * OR MODIFICATIONS. * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,  * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS  * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY  * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM */package org.csstudio.platform.ui.internal.vafada.swtcalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/** *  */public class SWTMonthChooser extends Composite {        /**     *      */    private Combo comboBox;
    /**     *      */    private Locale locale;
    /**     *      *      * @param parent      */    public SWTMonthChooser(Composite parent) {
        super(parent, SWT.NONE);

        locale = Locale.getDefault();
        setLayout(new FillLayout());
        comboBox = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);

        initNames();

        setMonth(Calendar.getInstance().get(Calendar.MONTH));
        setFont(parent.getFont());
    }

    /**     *      */    private void initNames() {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        String[] monthNames = dateFormatSymbols.getMonths();

        int month = comboBox.getSelectionIndex();
        if (comboBox.getItemCount() > 0) {
            comboBox.removeAll();
        }

        for (int i = 0; i < monthNames.length; i++) {
            String name = monthNames[i];
            if (name.length() > 0) {
                comboBox.add(name);
            }
        }

        if (month < 0) {
            month = 0;
        } else if (month >= comboBox.getItemCount()) {
            month = comboBox.getItemCount() - 1;
        }

        comboBox.select(month);
    }

    /**     *      *      * @param listener      */    public void addSelectionListener(SelectionListener listener) {
        comboBox.addSelectionListener(listener);
    }

    /**     *      *      * @param listener      */    public void removeSelectionListener(SelectionListener listener) {
        comboBox.removeSelectionListener(listener);
    }

    /**     *      *      * @param newMonth      */    public void setMonth(int newMonth) {
        comboBox.select(newMonth);
    }

    /**     *      *      * @return      */    public int getMonth() {
        return comboBox.getSelectionIndex();
    }

    /**     *      *      * @param locale      */    public void setLocale(Locale locale) {
        this.locale = locale;
        initNames();
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
     */
    /**     *      *      * @param font      */    public void setFont(Font font) {
        super.setFont(font);
        comboBox.setFont(getFont());
    }
}

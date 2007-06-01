/*
 *  SWTMonthChooser.java  - A month chooser component for SWT
 *  Author: Mark Bryan Yu
 *  Modified by: Sergey Prigogin
 *  swtcalendar.sourceforge.net
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in the
 *  Software without restriction, including without limitation the rights to use, copy,
 *  modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the
 *  following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL SIMON TATHAM BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.vafada.swtcalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class SWTMonthChooser extends Composite {    private Combo comboBox;
    private Locale locale;
    public SWTMonthChooser(Composite parent) {
        super(parent, SWT.NONE);

        locale = Locale.getDefault();
        setLayout(new FillLayout());
        comboBox = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);

        initNames();

        setMonth(Calendar.getInstance().get(Calendar.MONTH));
        setFont(parent.getFont());
    }

    private void initNames() {
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

    public void addSelectionListener(SelectionListener listener) {
        comboBox.addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        comboBox.removeSelectionListener(listener);
    }

    public void setMonth(int newMonth) {
        comboBox.select(newMonth);
    }

    public int getMonth() {
        return comboBox.getSelectionIndex();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        initNames();
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        comboBox.setFont(getFont());
    }
}

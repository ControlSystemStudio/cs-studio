/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.vafada.swtcalendar.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("nls")
public class EmbeddedDemo {
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display, SWT.CLOSE);
        RowLayout rowLayout = new RowLayout();
        rowLayout.type = SWT.VERTICAL;
        shell.setLayout(rowLayout);
        final Label l = new Label(shell, SWT.NONE);
        RowData data = new RowData(200, SWT.DEFAULT);
        l.setLayoutData(data);

        Composite localePanel = new Composite(shell, SWT.NONE);
        localePanel.setLayout(new RowLayout());
        Label localeLabel = new Label(localePanel, SWT.NONE);
        localeLabel.setText("Locale:");
        final Combo localeCombo = new Combo(localePanel, SWT.DROP_DOWN | SWT.READ_ONLY);


        Locale[] temp = Calendar.getAvailableLocales();
        int count = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].getCountry().length() > 0) {
                count++;
            }
        }

        final Locale[] locales = new Locale[count];

        count = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].getCountry().length() > 0) {
                locales[count] = temp[i];
                localeCombo.add(locales[count].getDisplayName());
                count++;
            }
        }


        for (int i = 0; i < locales.length; i++) {
            if (locales[i].equals(Locale.getDefault())) {
                localeCombo.select(i);
                break;
            }
        }


        final SWTCalendar c = new SWTCalendar(shell, SWT.NONE | SWTCalendar.RED_SUNDAY);

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        l.setText(df.format(c.getCalendar().getTime()));
        c.addSWTCalendarListener(new SWTCalendarListener() {
            public void dateChanged(SWTCalendarEvent calendarEvent) {
                Locale _locale = locales[localeCombo.getSelectionIndex()];
                DateFormat df2 = DateFormat.getDateInstance(DateFormat.LONG, _locale);
                l.setText(df2.format(calendarEvent.getCalendar().getTime()));
            }
        });

        localeCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                Locale _locale = locales[localeCombo.getSelectionIndex()];
                DateFormat df2 = DateFormat.getDateInstance(DateFormat.LONG, _locale);
                l.setText(df2.format(c.getCalendar().getTime()));
                c.setLocale(_locale);
            }

            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });

        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();


    }

}

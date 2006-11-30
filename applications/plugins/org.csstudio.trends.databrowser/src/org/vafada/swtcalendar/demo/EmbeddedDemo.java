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

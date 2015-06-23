/**
 *
 */
package org.csstudio.logbook.ui;

import java.io.IOException;

import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookBuilder;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 *
 * A View to create log entries.
 *
 * @author shroffk
 *
 */
public class CreateLogEntryView extends ViewPart {
    private LogEntryWidget logEntryWidget;

    private final IPreferencesService service = Platform
        .getPreferencesService();

    public CreateLogEntryView() {
    }

    /** View ID defined in plugin.xml */
    public static final String ID = "org.csstudio.logbook.ui.CreateLogEntry"; //$NON-NLS-1$

    @Override
    public void createPartControl(Composite parent) {
    parent.setLayout(new FormLayout());
    logEntryWidget = new LogEntryWidget(parent, SWT.NONE, false, true);
    FormData fd_logEntryWidget = new FormData();
    fd_logEntryWidget.top = new FormAttachment(0, 1);
    fd_logEntryWidget.left = new FormAttachment(0, 1);
    fd_logEntryWidget.bottom = new FormAttachment(100, -1);
    fd_logEntryWidget.right = new FormAttachment(100, -1);
    logEntryWidget.setLayoutData(fd_logEntryWidget);
    try {
        String defaultLogbook = service.getString("org.csstudio.logbook.ui", "Default.logbook", "", null);
        String defaultLevel = service.getString("org.csstudio.logbook.ui", "Default.level", "", null);
        logEntryWidget.setLogEntry(LogEntryBuilder.withText("")
                                  .setLevel(defaultLevel)
                                  .addLogbook(LogbookBuilder.logbook(defaultLogbook))
                                  .build());
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    @Override
    public void setFocus() {

    }

}

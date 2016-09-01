/**
 *
 */
package org.csstudio.logbook.ui;

import static org.csstudio.logbook.util.LogEntrySearchUtil.SEARCH_KEYWORD_END;
import static org.csstudio.logbook.util.LogEntrySearchUtil.SEARCH_KEYWORD_LOGBOOKS;
import static org.csstudio.logbook.util.LogEntrySearchUtil.SEARCH_KEYWORD_START;
import static org.csstudio.logbook.util.LogEntrySearchUtil.SEARCH_KEYWORD_TAGS;
import static org.csstudio.logbook.util.LogEntrySearchUtil.SEARCH_KEYWORD_TEXT;
import static org.csstudio.logbook.util.LogEntrySearchUtil.parseSearchMap;
import static org.csstudio.logbook.util.LogEntrySearchUtil.parseSearchString;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.csstudio.apputil.ui.time.Messages;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.ui.util.DelayedNotificator;
import org.csstudio.ui.util.widgets.MultipleSelectionCombo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Joiner;

/**
 *
 * A dialog box listing logbook, tags, start/end times and other search conditions for log entries
 *
 * @author shroffk
 *
 */
public class LogEntrySearchDialog extends Dialog {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    // GUI components
    private Text searchString;
    private MultipleSelectionCombo<String> logbookCombo;
    private MultipleSelectionCombo<String> tagCombo;
    private Text text;
    private Text textFrom;
    private Text textTo;

    // Model
    List<String> logbooks = Collections.emptyList();
    List<String> tags = Collections.emptyList();
    Map<String, String> searchParameters = new LinkedHashMap<String, String>();

    public LogEntrySearchDialog(Shell parentShell, List<String> logbooks,
            List<String> tags, Map<String, String> initialSearchParameters) {
        super(parentShell);
        this.logbooks = logbooks;
        this.tags = tags;
        setBlockOnOpen(false);
        setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
    }

    @Override
    public Control createDialogArea(Composite parent) {
        getShell().setText("Log Entry Search");
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(3, false));

        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("searchParameters")) {
                    initialize();
                }

            }
        });

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setLayoutData(
                new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText("Search:");

        searchString = new Text(container, SWT.BORDER);
        searchString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        searchString.addModifyListener(new ModifyListener() {

            private DelayedNotificator notificator = new DelayedNotificator(250, TimeUnit.MILLISECONDS);

            @Override
            public void modifyText(ModifyEvent e) {
                notificator.delayedExec(searchString, new Runnable() {

                    @Override
                    public void run() {
                        setSearchParameters(parseSearchString(searchString.getText()));
                    }
                });
            }
        });
        new Label(container, SWT.NONE);

        Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label lblText = new Label(container, SWT.NONE);
        lblText.setLayoutData(
                new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblText.setText("Text:");

        text = new Text(container, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {

            private DelayedNotificator notificator = new DelayedNotificator(250,
                    TimeUnit.MILLISECONDS);

            @Override
            public void modifyText(ModifyEvent e) {
                notificator.delayedExec(text, new Runnable() {

                    @Override
                    public void run() {
                        searchParameters.put(SEARCH_KEYWORD_TEXT, text.getText().trim());
                        updateSearch();
                    }
                });
            }
        });

        text.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label lblLogbooks = new Label(container, SWT.NONE);
        lblLogbooks.setLayoutData(
                new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblLogbooks.setText("Logbooks:");

        logbookCombo = new MultipleSelectionCombo<String>(container, SWT.NONE);
        logbookCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        logbookCombo.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                searchParameters.put(SEARCH_KEYWORD_LOGBOOKS, Joiner.on(",").join(logbookCombo.getSelection()));
                updateSearch();
            }
        });

        Label lblTags = new Label(container, SWT.NONE);
        lblTags.setLayoutData(
                new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblTags.setText("Tags:");

        tagCombo = new MultipleSelectionCombo<String>(container, SWT.NONE);
        tagCombo.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        tagCombo.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                searchParameters.put(SEARCH_KEYWORD_TAGS, Joiner.on(",").join(tagCombo.getSelection()));
                updateSearch();
            }
        });

        Label lblFrom = new Label(container, SWT.NONE);
        lblFrom.setLayoutData(
                new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblFrom.setText("From:");
        textFrom = new Text(container, SWT.BORDER);
        textFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textFrom.setToolTipText(Messages.StartEnd_StartTime_TT);

        Button btnNewButton = new Button(container, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final StartEndDialog dlg = new StartEndDialog(getShell(),
                        textFrom.getText(),
                        textTo.getText());
                if (dlg.open() != Window.OK)
                    return;
                textFrom.setText(dlg.getStartSpecification());
                searchParameters.put(SEARCH_KEYWORD_START, dlg.getStartSpecification());
                textTo.setText(dlg.getEndSpecification());
                searchParameters.put(SEARCH_KEYWORD_END, dlg.getEndSpecification());
                updateSearch();
            }
        });
        btnNewButton.setText("...");

        Label lblTo = new Label(container, SWT.NONE);
        lblTo.setLayoutData(
                new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblTo.setText("To:");
        textTo = new Text(container, SWT.BORDER);
        textTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textTo.setToolTipText(Messages.StartEnd_EndTime_TT);

        Button btnNewButton_1 = new Button(container, SWT.NONE);
        btnNewButton_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final StartEndDialog dlg = new StartEndDialog(getShell(),
                        textFrom.getText(),
                        textTo.getText());
                if (dlg.open() != Window.OK)
                    return;
                textFrom.setText(dlg.getStartSpecification());
                searchParameters.put(SEARCH_KEYWORD_START, dlg.getStartSpecification());
                textTo.setText(dlg.getEndSpecification());
                searchParameters.put(SEARCH_KEYWORD_END, dlg.getEndSpecification());
                updateSearch();
            }
        });
        btnNewButton_1.setText("...");
        initialize();
        return container;

    }

    private void initialize() {
        logbookCombo.setItems(logbooks);
        tagCombo.setItems(tags);
        for (String keyword : searchParameters.keySet()) {
            if (SEARCH_KEYWORD_TEXT.equals(keyword)) {
                text.setText(searchParameters.get(keyword));
            }
            if (SEARCH_KEYWORD_LOGBOOKS.equals(keyword)) {
                logbookCombo.setSelection(searchParameters.get(keyword));
            }
            if (SEARCH_KEYWORD_TAGS.equals(keyword)) {
                tagCombo.setSelection(searchParameters.get(keyword));
            }
            if (SEARCH_KEYWORD_START.equals(keyword)) {
                textFrom.setText(searchParameters.get(keyword));
            }
            if (SEARCH_KEYWORD_END.equals(keyword)) {
                textTo.setText(searchParameters.get(keyword));
            }
        }
    }

    protected void updateSearch() {
        searchString.setText(parseSearchMap(searchParameters));
    }

    /**
     * @return the searchParameters
     */
    @SuppressWarnings("unused")
    private synchronized Map<String, String> getSearchParameters() {
        return searchParameters;
    }

    /**
     * @param searchParameters
     *            the searchParameters to set
     */
    private synchronized void setSearchParameters(
            Map<String, String> searchParameters) {
        Map<String, String> OldValue = this.searchParameters;
        this.searchParameters = searchParameters;
        changeSupport.firePropertyChange("searchParameters", OldValue,
                this.searchParameters);
    }

    public String getSearchString() {
        return parseSearchMap(searchParameters);
    }

}

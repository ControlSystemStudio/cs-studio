/**
 *
 */
package org.csstudio.sds.ui.internal.preferences;

import java.util.List;

import org.csstudio.sds.cursorservice.AbstractCursor;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.cursorservice.CursorSettings;
import org.csstudio.sds.cursorservice.CursorState;
import org.csstudio.sds.cursorservice.ICursorService;
import org.csstudio.sds.cursorservice.RuleDescriptor;
import org.csstudio.sds.ui.cursors.internal.CursorHelper;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for the mouse cursor preferences.
 *
 * @author Joerg Rathlev
 */
public final class MouseCursorPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * The <code>CursorService</code>.
     */
    private final ICursorService _service;

    /**
     * The combo for selecting the cursor selection rule.
     */
    private Combo _ruleCombo;

    /**
     * The cursor selection rules.
     */
    private final RuleDescriptor[] _rules;

    /**
     * The available cursors.
     */
    private final AbstractCursor[] _cursors;

    /**
     * References to the cursor states of the currently selected rule.
     */
    private final CursorState[] _cursorStates;

    /**
     * The names of the cursors.
     */
    private final String[] _cursorNames;

    /**
     * The labels for the cursor graphics combos.
     */
    private CLabel[] _cursorLabels;

    /**
     * The combos for selecting the cursor graphics.
     */
    private Combo[] _cursorCombos;

    /**
     * The cursor settings. This preference page uses a local copy of the preferences which is
     * written back to the <code>CursorService</code> when the settings are applied.
     */
    private final CursorSettings _settings;

    /**
     * Label which explains the cursor selection.
     */
    private Label _explainLabel;

    /**
     * Updates the available cursor selection combos when the selected cursor selection rule
     * changes.
     */
    private class RuleSelectionListener extends SelectionAdapter {

        /**
         * Updates the available cursor selection combos based on the selected cursor selection
         * rule.
         *
         * @param e
         *            the selection event.
         */
        @Override
        public void widgetSelected(final SelectionEvent e) {
            if (e.widget == _ruleCombo) {
                updateCursorCombos();
            }
        }
    }

    /**
     * Listener for cursor selection events which updates the label for previewing the cursor.
     */
    private final class CursorSelectionListener extends SelectionAdapter {

        /**
         * The index of the label and combo.
         */
        private final int _index;

        /**
         * Creates a new cursor selection listener.
         *
         * @param index
         *            the index of the label and combo for which this listener is a listener.
         */
        private CursorSelectionListener(final int index) {
            _index = index;
        }

        /**
         * Applies the selected cursor to the label.
         *
         * @param e
         *            the selection event.
         */
        @Override
        public void widgetSelected(final SelectionEvent e) {
            if (e.widget instanceof Combo) {
                final Combo combo = (Combo) e.widget;
                final int i = combo.getSelectionIndex();
                final AbstractCursor cursor = _cursors[i];
                _settings.setCursor(selectedRule(), _cursorStates[_index], cursor);
                CursorHelper.applyCursor(_cursorLabels[_index], cursor.getIdentifier());
            }
        }
    }

    /**
     * Creates the preference page.
     */
    public MouseCursorPreferencePage() {
        setDescription("Set up the mouse cursors that are used in running displays.");

        _service = CursorService.getInstance();

        final List<RuleDescriptor> rules = _service.availableRules();
        _rules = rules.toArray(new RuleDescriptor[rules.size()]);

        final List<AbstractCursor> cursors = _service.availableCursors();
        _cursors = cursors.toArray(new AbstractCursor[cursors.size()]);
        _cursorNames = new String[_cursors.length];
        for (int i = 0; i < _cursors.length; i++) {
            _cursorNames[i] = _cursors[i].getTitle();
        }
        _cursorStates = new CursorState[ICursorService.MAX_CURSOR_STATES];
        _settings = _service.getPreferences();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(final Composite parent) {
        final Composite contents = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        contents.setLayout(layout);

        createRuleSelectionCombo(contents);

        // horizontal separator
        final Label label = new Label(contents, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false, 2, 1));

        _explainLabel = new Label(contents, SWT.WRAP);
        final GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false, 2, 1);
        // Using a small widthHint to prevent the label from sizing up the
        // preference page. The label automatically expands to the full width
        // of the columns regardless of this hint, but the hint prevents the
        // label from being used to calculate the columns' width.
        gd.widthHint = 200;
        _explainLabel.setLayoutData(gd);
        _explainLabel.setText("The cursor selection rule applies cursors to "
                + "widgets in the states listed below. Select the cursor graphic to be used "
                + "for each state. To preview a cursor, hover your mouse over " + "the label.");

        createCursorSelectionCombos(contents);

        _ruleCombo.addSelectionListener(new RuleSelectionListener());
        updateCursorCombos();

        return contents;
    }

    /**
     * Creates the combo boxes for selecting the cursor graphics.
     *
     * @param parent
     *            the parent control.
     */
    private void createCursorSelectionCombos(final Composite parent) {
        _cursorLabels = new CLabel[ICursorService.MAX_CURSOR_STATES];
        _cursorCombos = new Combo[ICursorService.MAX_CURSOR_STATES];
        for (int i = 0; i < ICursorService.MAX_CURSOR_STATES; i++) {
            // For the cursor labels, a CLabel is used because it automatically
            // shortens the text if it is too long and displays the full text
            // in a tooltip if the text was shortened.
            _cursorLabels[i] = new CLabel(parent, SWT.NONE);
            _cursorLabels[i].setVisible(false);
            final GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
            gd.widthHint = 150;
            _cursorLabels[i].setLayoutData(gd);
            _cursorCombos[i] = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
            _cursorCombos[i].setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
            _cursorCombos[i].setVisible(false);
            _cursorCombos[i].setItems(_cursorNames);
            _cursorCombos[i].select(0);
            _cursorCombos[i].addSelectionListener(new CursorSelectionListener(i));
        }
    }

    /**
     * Creates the combo box for choosing the cursor selection rule.
     *
     * @param parent
     *            the parent control.
     */
    private void createRuleSelectionCombo(final Composite parent) {
        final Label label = new Label(parent, SWT.NONE);
        label.setText("Cursor selection rule:");
        _ruleCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        _ruleCombo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        for (final RuleDescriptor rule : _rules) {
            _ruleCombo.add(rule.getDescription());
        }

        // get the currently chosen rule and select it
        final RuleDescriptor currentRule = _service.getPreferredRule();
        for (int i = 0, n = _rules.length; i < n; i++) {
            if (_rules[i].equals(currentRule)) {
                _ruleCombo.select(i);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performOk() {
        final RuleDescriptor configuredRule = _rules[_ruleCombo.getSelectionIndex()];
        _service.setPreferredRule(configuredRule);
        _service.setPreferences(_settings);
        return true;
    }

    /**
     * Returns the currently selected rule.
     *
     * @return the currently selected rule.
     */
    private RuleDescriptor selectedRule() {
        final int index = _ruleCombo.getSelectionIndex();
        return _rules[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IWorkbench workbench) {
        // nothing to do
    }

    /**
     * Updates the visibilty and the selected cursors based on the currently selected cursor
     * selection rule.
     */
    private void updateCursorCombos() {
        final RuleDescriptor rule = selectedRule();
        final List<CursorState> cursorStates = rule.cursorStates();
        _explainLabel.setEnabled(cursorStates.size() > 0);
        for (int i = 0; i < ICursorService.MAX_CURSOR_STATES; i++) {
            if (i < cursorStates.size()) {
                _cursorStates[i] = cursorStates.get(i);
                _cursorLabels[i].setText(_cursorStates[i].getName() + ":");
                _cursorLabels[i].setVisible(true);
                _cursorCombos[i].setVisible(true);
                AbstractCursor cursor = _settings.getCursor(rule, _cursorStates[i]);
                if (cursor == null) {
                    cursor = ICursorService.SYSTEM_DEFAULT_CURSOR;
                }
                for (int j = 0; j < ICursorService.MAX_CURSOR_STATES; j++) {
                    if (cursor.equals(_cursors[j])) {
                        _cursorCombos[i].select(j);
                        final Listener[] listeners = _cursorCombos[i].getListeners(SWT.Selection);
                        // FIXME HR: Geht das nicht eleganter? Das hab ich gemacht damit die
                        // Courser nach der Initialisierung angezeigt werden.
                        // Das .select löst nicht den Selection Listener aus.
                        if (listeners != null && listeners.length > 0) {
                            final Listener listener = listeners[0];
                            listener.handleEvent(new Event());
                            final TypedListener tl = (TypedListener) listener;
                            final CursorSelectionListener csl = (CursorSelectionListener) tl
                                    .getEventListener();
                            final Event e = new Event();
                            e.widget = _cursorCombos[i];
                            final SelectionEvent e2 = new SelectionEvent(e);
                            e2.widget = _cursorCombos[i];
                            csl.widgetSelected(e2);
                        }
                        break;
                    }
                }
            } else {
                _cursorStates[i] = null; // prevent stale references
                _cursorLabels[i].setVisible(false);
                _cursorCombos[i].setVisible(false);
            }
        }
    }

}

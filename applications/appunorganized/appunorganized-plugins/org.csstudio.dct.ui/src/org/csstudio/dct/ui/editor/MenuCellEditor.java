package org.csstudio.dct.ui.editor;

import org.csstudio.dct.metamodel.IChoice;
import org.csstudio.dct.metamodel.IMenuDefinition;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Cell editor implementation that uses a combo box.
 *
 * The editor displays a set of {@link IOption}´s. When one of the options is
 * chosen, the identifier of that option (see {@link IOption#getIdentifier()})
 * is returned as selected value for this cell editor.
 *
 * @author Sven Wende
 *
 */
public final class MenuCellEditor extends CellEditor {
    private AbstractListViewer _viewer;
    private CCombo _combobox;
    private IChoice _selection;
    private IMenuDefinition _menuDefinition;

    /**
     * Constructor.
     *
     * @param parent
     *            the parent composite
     * @param menuDefinition
     *            menu definition
     */
    public MenuCellEditor(Composite parent, IMenuDefinition menuDefinition) {
        super(parent, SWT.READ_ONLY);
        assert menuDefinition != null;
        _menuDefinition = menuDefinition;
        _viewer.setInput(_menuDefinition.getChoices());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();
        getControl().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                ((CCombo) getControl()).setListVisible(true);
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createControl(Composite parent) {
        _combobox = new CCombo(parent, getStyle());
        _combobox.setEditable(false);
        _combobox.setVisibleItemCount(20);
        _viewer = new ComboViewer(_combobox);

        _viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((IChoice) element).getDescription();
            }
        });
        _viewer.setContentProvider(new ArrayContentProvider());
        _combobox.setFont(parent.getFont());

        _combobox.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201
            @Override
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });

        _combobox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }

            @Override
            public void widgetSelected(SelectionEvent event) {
                Object sel = _viewer.getSelection();

                if (sel instanceof IStructuredSelection) {
                    _selection = (IChoice) ((IStructuredSelection) sel).getFirstElement();
                }

            }
        });

        _combobox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                applyEditorValueAndDeactivate();
            }
        });

        _combobox.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        _combobox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                MenuCellEditor.this.focusLost();
            }
        });

        _viewer.getControl().forceFocus();

        return _combobox;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValue() {
        String result = "";

        if (_selection != null) {
            result = _selection.getDescription();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetFocus() {
        _viewer.getControl().setFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue(Object value) {
        for(IChoice choice : _menuDefinition.getChoices()) {
            if(choice.getDescription().equals(value)) {
                _selection = choice;
                _viewer.setSelection(new StructuredSelection(_selection));
            }
        }
    }

    /**
     * Applies the currently selected value and deactivates the cell editor.
     */
    void applyEditorValueAndDeactivate() {
        Object newValue = doGetValue();
        markDirty();
        boolean isValid = isCorrect(newValue);
        setValueValid(isValid);

        fireApplyEditorValue();
        deactivate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') { // Escape character
            fireCancelEditor();
        } else if (keyEvent.character == '\t') { // tab key
            applyEditorValueAndDeactivate();
        }
    }
}

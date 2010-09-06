package org.csstudio.config.ioconfig.config.view.helper;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * TODO: (hrickens) Das ist Svens implemaentation eines Combo Cell Editors.
 * Soll vom Look & Feel besser sein als die SWT Implementierung.
 * Um den Verwenden zu können muss aber noch mehr angepasst werden.
 * {@see IChoice} and {@see IMenuDefinition} im DCT!
 */
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
//	private AbstractListViewer _viewer;
//	private CCombo _combobox;
//	private IChoice _selection;
//
//	/**
//	 * Constructor.
//	 *
//	 * @param parent
//	 *            the parent composite
//	 * @param menuDefinition
//	 *            menu definition
//	 */
//	public MenuCellEditor(Composite parent, IMenuDefinition menuDefinition) {
//		super(parent, SWT.READ_ONLY);
//		assert menuDefinition != null;
//		_viewer.setInput(menuDefinition.getChoices());
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
	public void activate() {
//		super.activate();
//		getControl().getDisplay().asyncExec(new Runnable() {
//
//			public void run() {
//				((CCombo) getControl()).setListVisible(true);
//			}
//
//		});
	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
	protected Control createControl(final Composite parent) {
//		_combobox = new CCombo(parent, getStyle());
//		_combobox.setEditable(false);
//		_combobox.setVisibleItemCount(20);
//		_viewer = new ComboViewer(_combobox);
//
//		_viewer.setLabelProvider(new LabelProvider() {
//			@Override
//			public String getText(Object element) {
//				return ((IChoice) element).getDescription();
//			}
//		});
//		_viewer.setContentProvider(new ArrayContentProvider());
//		_combobox.setFont(parent.getFont());
//
//		_combobox.addKeyListener(new KeyAdapter() {
//			// hook key pressed - see PR 14201
//			public void keyPressed(KeyEvent e) {
//				keyReleaseOccured(e);
//			}
//		});
//
//		_combobox.addSelectionListener(new SelectionAdapter() {
//			public void widgetDefaultSelected(SelectionEvent event) {
//				applyEditorValueAndDeactivate();
//			}
//
//			public void widgetSelected(SelectionEvent event) {
//				Object o = _viewer.getSelection();
//
//				if (o instanceof IChoice) {
//					_selection = (IChoice) o;
//				}
//
//			}
//		});
//
//		_combobox.addKeyListener(new KeyAdapter() {
//			// hook key pressed - see PR 14201
//			public void keyPressed(KeyEvent e) {
//				keyReleaseOccured(e);
//			}
//		});
//
//		_combobox.addSelectionListener(new SelectionAdapter() {
//			public void widgetDefaultSelected(SelectionEvent event) {
//				applyEditorValueAndDeactivate();
//			}
//
//			public void widgetSelected(SelectionEvent event) {
//				ISelection sel = _viewer.getSelection();
//
//				if (sel instanceof IStructuredSelection) {
//					_selection = (IChoice) ((IStructuredSelection) sel).getFirstElement();
//					applyEditorValueAndDeactivate();
//				}
//			}
//		});
//
//		_combobox.addTraverseListener(new TraverseListener() {
//			public void keyTraversed(TraverseEvent e) {
//				if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
//					e.doit = false;
//				}
//			}
//		});
//
//		_combobox.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				MenuCellEditor.this.focusLost();
//			}
//		});
//
//		_viewer.getControl().forceFocus();
//
//		return _combobox;
//
	    return null;
	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
	protected Object doGetValue() {
//		String result = "";
//
//		if (_selection != null) {
//			result = _selection.getDescription();
//		}
//
//		return result;
	    return null;
	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
	protected void doSetFocus() {
//		_viewer.getControl().setFocus();
	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
	protected void doSetValue(final Object value) {
//		_viewer.setSelection(new StructuredSelection(value));
	}
//
//	/**
//	 * Applies the currently selected value and deactivates the cell editor.
//	 */
//	void applyEditorValueAndDeactivate() {
//		Object newValue = doGetValue();
//		markDirty();
//		boolean isValid = isCorrect(newValue);
//		setValueValid(isValid);
//
//		fireApplyEditorValue();
//		deactivate();
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	protected void focusLost() {
//		if (isActivated()) {
//			applyEditorValueAndDeactivate();
//		}
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	protected void keyReleaseOccured(KeyEvent keyEvent) {
//		if (keyEvent.character == '\u001b') { // Escape character
//			fireCancelEditor();
//		} else if (keyEvent.character == '\t') { // tab key
//			applyEditorValueAndDeactivate();
//		}
//	}
}

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
package org.csstudio.opibuilder.visualparts;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolBar;

/**
 * A table cell editor for values of type PointList.
 *  
 * @author Kai Meyer (original author), Xihui Chen (since import from SDS 2009/9) 
 */
public final class PointListCellEditor extends AbstractDialogCellEditor {

	/**
	 * The current PointList value.
	 */
	private List<Point> _pointList;
	/**
	 * A copy of the current PointList value.
	 */
	private List<Point> _orgPointList;

	/**
	 * Creates a new string cell editor parented under the given control. The
	 * cell editor value is a PointList.
	 * 
	 * @param parent
	 *            The parent table.
	 */
	public PointListCellEditor(final Composite parent) {
		super(parent, "Points");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void openDialog(final Shell parentShell, final String dialogTitle) {
		PointListInputDialog dialog = new PointListInputDialog(parentShell,dialogTitle,"Add or remove Points");
		if (dialog.open()==Window.CANCEL) {
			_pointList = _orgPointList;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldFireChanges() {
		if (_pointList.size()==_orgPointList.size()) {
			for (int i=0;i<_pointList.size();i++) {
				Point p1 = _pointList.get(i);
				Point p2 = _orgPointList.get(i);
				if (p1.x!=p2.x || p1.y!=p2.y) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValue() {
		return this.listToPointList(_pointList);
	}
	
	/**
	 * Creates a new PointList with the Points of the given List.
	 * @param list
	 * 			A {@link List} which contains the Points
	 * @return PointList
	 * 			The new {@link PointList}
	 */
	private PointList listToPointList(final List<Point> list) {
		PointList result = new PointList();
		for (Point p : list) {
			result.addPoint(p);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetValue(final Object value) {
		Assert.isTrue(value instanceof PointList);
		PointList list = (PointList) value;
		_orgPointList = new LinkedList<Point>();
		_pointList = new LinkedList<Point>();
		for (int i=0;i<list.size();i++) {
			_pointList.add(list.getPoint(i));
			_orgPointList.add(list.getPoint(i));
		}
	}
	
	/**
	 * This class represents a Dialog to add, edit and remove Points of a PointList.
	 * 
	 * @author Kai Meyer
	 */
	private final class PointListInputDialog extends Dialog {
		/**
	     * The title of the dialog.
	     */
	    private String _title;
	    /**
	     * The message to display, or <code>null</code> if none.
	     */
	    private String _message;
	    /**
	     * The List-Widget.
	     */
	    private ListViewer _viewer;
	    /**
	     * Adds new entries to the List.
	     */
	    private Action _addAction;
	    /**
	     * Edits the selected entry.
	     */
	    private Action _editAction;
	    /**
	     * Removes the selected entries from the List.
	     */
	    private Action _removeAction;
	    /**
	     * Edits the selected entry.
	     */
	    private Action _upAction;
	    /**
	     * Removes the selected entries from the List.
	     */
	    private Action _downAction;

	    /**
	     * Creates an input dialog with OK and Cancel buttons. Note that the dialog
	     * will have no visual representation (no widgets) until it is told to open.
	     * <p>
	     * Note that the <code>open</code> method blocks for input dialogs.
	     * </p>
	     * 
	     * @param parentShell
	     *            the parent shell, or <code>null</code> to create a top-level
	     *            shell
	     * @param dialogTitle
	     *            the dialog title, or <code>null</code> if none
	     * @param dialogMessage
	     *            the dialog message, or <code>null</code> if none
	     
	     */
		public PointListInputDialog(final Shell parentShell, final String dialogTitle,
	            final String dialogMessage) {
			super(parentShell);
			_title = dialogTitle;
	        _message = dialogMessage;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void configureShell(final Shell shell) {
	        super.configureShell(shell);
	        if (_title != null) {
				shell.setText(_title);
			}
	    }
		
		/**
	     * {@inheritDoc}
	     */
		@Override
	    protected Control createDialogArea(final Composite parent) {
	        Composite composite = (Composite) super.createDialogArea(parent);
	        composite.setLayout(new GridLayout(1, false));
	        if (_message != null) {
	            Label label = new Label(composite, SWT.WRAP);
	            label.setText(_message);
	            GridData data = new GridData(GridData.GRAB_HORIZONTAL
	                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
	                    | GridData.VERTICAL_ALIGN_CENTER);
	            data.horizontalSpan = 2;
	            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
	            label.setLayoutData(data);
	        }
	        
	        Composite toolBarComposite = new Composite(composite,SWT.BORDER);
	        GridLayout gridLayout = new GridLayout(1,false);
	        gridLayout.marginLeft = 0;
	        gridLayout.marginRight = 0;
	        gridLayout.marginBottom = 0;
	        gridLayout.marginTop = 0;
	        gridLayout.marginHeight = 0;
	        gridLayout.marginWidth = 0;
			toolBarComposite.setLayout(gridLayout);
	        GridData grid = new GridData(SWT.FILL,SWT.FILL,true,true);
	        toolBarComposite.setLayoutData(grid);
	        
	        ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
	        ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
	        GridData gid = new GridData();
	        gid.horizontalAlignment = GridData.FILL;
	        gid.verticalAlignment = GridData.BEGINNING;
	        toolBar.setLayoutData(gid);
	        
	        this.createActions(toolbarManager); 
	        _viewer = this.createListViewer(toolBarComposite);        
	        this.hookPopupMenu(_viewer);
	        this.hookDoubleClick(_viewer);

	        return composite;
	    }
		
		/**
		 * Creates Actions and adds them to the given {@link ToolBarManager}.
		 * @param manager
		 * 			The ToolBarManager, which should contain the actions
		 */
		private void createActions(final ToolBarManager manager) {
			_addAction = new Action() {
				@Override
				public void run() {
					openPointDialog(true);
				}
			};
			_addAction.setText("Add "+_title);
			_addAction.setToolTipText("Adds a new "+_title+" to the list");
			_addAction.setImageDescriptor(CustomMediaFactory.getInstance()
					.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
					"icons/add.gif"));
			manager.add(_addAction);
			_editAction = new Action() {
				@Override
				public void run() {
	        		openPointDialog(false);
	        		refreshActions();
				}
			};
			_editAction.setText("Edit "+_title);
			_editAction.setToolTipText("Edits the selected "+_title);
			_editAction.setImageDescriptor(CustomMediaFactory.getInstance()
					.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
					"icons/edit.gif"));
			_editAction.setEnabled(false);
			manager.add(_editAction);
			_removeAction = new Action() {
				@Override
				public void run() {
					removePoint();
	        		refreshActions();
				}
			};
			_removeAction.setText("Remove "+_title);
			_removeAction.setToolTipText("Removes the selected "+_title+" from the list");
			_removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
					.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
					"icons/delete.gif"));
			_removeAction.setEnabled(false);
			manager.add(_removeAction);
			manager.add(new Separator());
			_upAction = new Action() {
				@Override
				public void run() {
					movePoint(true);
				}
			};
			_upAction.setText("Move up");
			_upAction.setToolTipText("Increases the index of the selected Point");
			_upAction.setImageDescriptor(CustomMediaFactory.getInstance()
					.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
					"icons/search_prev.gif"));
			_upAction.setEnabled(false);
			manager.add(_upAction);
			_downAction = new Action() {
				@Override
				public void run() {
					movePoint(false);
				}
			};
			_downAction.setText("Move down");
			_downAction.setToolTipText("Decreases the index of the selected Point");
			_downAction.setImageDescriptor(CustomMediaFactory.getInstance()
					.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
					"icons/search_next.gif"));
			_downAction.setEnabled(false);
			manager.add(_downAction);
			manager.update(true);
		}
		
		/**
		 * Creates the viewer for the List.
		 * @param parent 
		 * 				The parent composite for the viewer
		 * @return ListViewer
		 * 				The ListViewer
		 */
		private ListViewer createListViewer(final Composite parent) {
			final ListViewer viewer = new ListViewer(parent);
	        viewer.setContentProvider(new ArrayContentProvider());
	        viewer.setLabelProvider(new LabelProvider() {
	        	@Override
	        	public String getText(final Object element) {
	        		if (element instanceof Point) {
	        			Point p = (Point) element;
	    				return p.toString();//p.x+","+p.y;
	        		}
	        		return element.toString();
	        	}
	        });
	        viewer.setInput(_pointList.toArray(new Point[_pointList.size()]));
	        GridData gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
	        gridData.verticalSpan = 6;
	        gridData.heightHint = 150;
	        viewer.getList().setLayoutData(gridData);
	        viewer.getList().addSelectionListener(new SelectionAdapter() {
				/**
	        	 * {@inheritDoc}
	        	 */
				@Override
				public void widgetSelected(final SelectionEvent e) {
					refreshActions();
				}
			});
	        viewer.getList().setFocus();
			return viewer;
		}
		
		/**
		 * Adds a Popup menu to the given ListViewer.
		 * @param viewer
		 * 			The ListViewer
		 */
		private void hookPopupMenu(final ListViewer viewer) {
			MenuManager popupMenu = new MenuManager();
			popupMenu.add(_addAction);
			popupMenu.add(_editAction);
			popupMenu.add(new Separator());
			popupMenu.add(_removeAction);
			popupMenu.add(new Separator());
			popupMenu.add(_upAction);
			popupMenu.add(_downAction);
			Menu menu = popupMenu.createContextMenu(viewer.getList());
			viewer.getList().setMenu(menu);
		}
		
		/**
		 * Adds doubleclick support to the given ListViewer.
		 * @param viewer
		 * 			The Listviewer
		 */
		private void hookDoubleClick(final ListViewer viewer) {
			viewer.getControl().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(final MouseEvent e) {
					if (_viewer.getList().getSelectionCount()==1) {
						_editAction.run();
					} else {
						_addAction.run();
					}
				}
			});
		}

		/**
		 * Opens a Dialog for adding a new Point.
		 * @param isNew
		 * 				True, if a new Point should be created, false otherwise
		 */
		private void openPointDialog(final boolean isNew) {
			int index = _viewer.getList().getItemCount();
			int[] selectedIndices = _viewer.getList().getSelectionIndices();
			if (selectedIndices.length>0) {
				index = selectedIndices[0]; 
			}
			PointDialog dialog = new PointDialog(this.getParentShell(),"Point", null, index, isNew);
			if (dialog.open()==Window.OK) {
				this.setInput();
			}
			_viewer.getList().setSelection(index);
			this.refreshActions();
		}
		
		/**
		 * Removes the current selected Points from the List.
		 */
		private void removePoint() {
			if (_viewer.getList().getSelectionIndices().length>0) {
				int[] selectedIndices = _viewer.getList().getSelectionIndices();
				Arrays.sort(selectedIndices);
				int i=0;
				for (int s : selectedIndices) {
					_pointList.remove(s - i);
					i++;
				}
				this.setInput();
			}
			refreshActions();
		}
		
		/**
		 * Enables or disables the RemoveButton.
		 */
		private void refreshActions() {
			if (_viewer.getList().getItemCount()>2) {
				_removeAction.setEnabled(_viewer.getList().getSelectionIndices().length>0);
			} else {
				_removeAction.setEnabled(false);
			}
			_editAction.setEnabled(_viewer.getList().getSelectionIndices().length==1);
			_upAction.setEnabled(_viewer.getList().getSelectionIndices().length==1 && _viewer.getList().getSelectionIndex()>0);
			_downAction.setEnabled(_viewer.getList().getSelectionIndices().length==1 && _viewer.getList().getSelectionIndex()<_viewer.getList().getItemCount()-1);
		}
		
		/**
		 * Moves the current selected Point one step up or down, depending on the given boolean. 
		 * @param up 
		 * 			True, if the Point should be moved up, false otherwise
		 */
		private void movePoint(final boolean up) {
			int newIndex = _viewer.getList().getSelectionIndex();
			Point point = _pointList.get(newIndex);
			_pointList.remove(newIndex);
			if (up) {
				newIndex = newIndex-1;
			} else {
				newIndex = newIndex+1;
			}
			if (newIndex<0) {
				newIndex = 0;
			}
			if (newIndex>_pointList.size()) {
				newIndex=_pointList.size();
			}
			_pointList.add(newIndex, point);
			this.setInput();
			_viewer.getList().setSelection(newIndex);
			this.refreshActions();
		}
		
		/**
		 * Sets the input on the viewer and refreshes it.
		 */
		private void setInput() {
			_viewer.setInput(_pointList.toArray(new Point[_pointList.size()]));
			_viewer.refresh();
		}
		
	}
	
	/**
	 * This class represents a Dialog for editing a Point.
	 * @author Kai Meyer
	 */
	private final class PointDialog extends Dialog {
		/**
	     * The title of the dialog.
	     */
	    private String _title;
	    /**
	     * The message to display, or <code>null</code> if none.
	     */
	    private String _message;
	    /**
	     * The input value; the empty string by default.
	     */
	    private int _index = -1;
	    /**
	     * The Spinner for the x-value of the Point.
	     */
	    private Spinner _xSpinner;
	    /**
	     * The Spinner for the y-value of the Point.
	     */
	    private Spinner _ySpinner;
	    /**
	     * A boolean, which indicates if the Point is new.
	     */
	    private boolean _isNew;
	    
		/**
	     * Creates an input dialog with OK and Cancel buttons. Note that the dialog
	     * will have no visual representation (no widgets) until it is told to open.
	     * <p>
	     * Note that the <code>open</code> method blocks for input dialogs.
	     * </p>
	     * 
	     * @param parentShell
	     *            the parent shell, or <code>null</code> to create a top-level
	     *            shell
	     * @param dialogTitle
	     *            the dialog title, or <code>null</code> if none
	     * @param dialogMessage
	     *            the dialog message, or <code>null</code> if none
	     * @param initialValue
	     *            the initial input value, or <code>null</code> if none
	     * @param isNew
	     *            true id the Point is new, false otherwise
	     */
		public PointDialog(final Shell parentShell, final String dialogTitle,
	            final String dialogMessage, final int initialValue, final boolean isNew) {
			super(parentShell);
			_title = dialogTitle;
	        _message = dialogMessage;
	        _isNew = isNew;
	        if (initialValue>=0) {
				_index = initialValue;
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void configureShell(final Shell shell) {
	        super.configureShell(shell);
	        if (_title != null) {
				shell.setText(_title);
			}
	    }
		
		/**
	     * {@inheritDoc}
	     */
		@Override
	    protected Control createDialogArea(final Composite parent) {
	        Composite composite = (Composite) super.createDialogArea(parent);
	        composite.setLayout(new GridLayout(2, false));
	        if (_message != null) {
	            Label label = new Label(composite, SWT.WRAP);
	            label.setText(_message);
	            GridData data = new GridData(GridData.GRAB_HORIZONTAL
	                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
	                    | GridData.VERTICAL_ALIGN_CENTER);
	            data.horizontalSpan = 2;
	            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
	            label.setLayoutData(data);
	        }
	        Label label = new Label(composite, SWT.NONE);
	        label.setText("x:");
	        _xSpinner = new Spinner(composite, SWT.BORDER);
	        _xSpinner.setMaximum(10000);
	        _xSpinner.setMinimum(-10000);
	        if (_index<0 || _index >=_pointList.size()) {
	        	_xSpinner.setSelection(0);
	        } else {
	        	_xSpinner.setSelection(_pointList.get(_index).x);
	        }
	        label = new Label(composite, SWT.NONE);
	        label.setText("y:");
	        _ySpinner = new Spinner(composite, SWT.BORDER);
	        _ySpinner.setMaximum(10000);
	        _ySpinner.setMinimum(-10000);
	        if (_index<0 || _index >=_pointList.size()) {
	        	_ySpinner.setSelection(0);
	        } else {
	        	_ySpinner.setSelection(_pointList.get(_index).y);
	        }
	        return composite;
	    }
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void okPressed() {
			this.getButton(IDialogConstants.OK_ID).setFocus();
			if (_index>=0) {
				if (!_isNew) {
					_pointList.remove(_index);
				}
				_pointList.add(_index, new Point(_xSpinner.getSelection(), _ySpinner.getSelection()));
			}
			//_pointList.add(new Point(_xSpinner.getSelection(), _ySpinner.getSelection()));			
			super.okPressed();
		}
	}
	
}

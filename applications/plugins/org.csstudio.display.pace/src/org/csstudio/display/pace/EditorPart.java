/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace;

import java.util.HashMap;
import java.util.logging.Level;

import org.csstudio.display.pace.gui.GUI;
import org.csstudio.display.pace.model.Cell;
import org.csstudio.display.pace.model.Instance;
import org.csstudio.display.pace.model.Model;
import org.csstudio.display.pace.model.ModelListener;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.ui.LogEntryBuilderDialog;
import org.csstudio.logbook.ui.LogEntryBuilderEnum;
import org.csstudio.logbook.ui.LogEntryBuilderListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/** Eclipse EditorPart for the PACE Model and GUI
 * 
 *  
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
public class EditorPart extends org.eclipse.ui.part.EditorPart
    implements ModelListener, LogEntryBuilderListener
{
    private Model model;
    private boolean is_dirty = false;
    
    private String changes = "";
    // Display ELog entry dialog
    private Shell shell = null;
    private String userName = "";

    /** Initialize Model from editor input */
    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
        setSite(site);
        // Get file behind input
        final IFile file = (IFile) input.getAdapter(IFile.class);
        if (file != null)
            setInput(input);
        else
            throw new PartInitException("Cannot handle " + input.getName()); //$NON-NLS-1$
        // Create model from file
        try
        {
            model = new Model(file.getContents());
        }
        catch (Exception ex)
        {
            throw new PartInitException(ex.getMessage());
        }
        // Set window title and message
        setPartName(file.getName());
        updateContentDescription();
    }

    /** Create GUI using the model as input. */
    @Override
    public void createPartControl(final Composite parent)
    {
        new GUI(parent, model, getSite());
        model.addListener(this);
        try
        {
            model.start();
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "Cannot start model", ex); //$NON-NLS-1$
        }
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                model.removeListener(EditorPart.this);
                model.stop();
            }
        });
    }

    @Override
    public void setFocus()
    {
        // no need to set focus
    }

    /** @return <code>true</code> if Model contains user changes */
    @Override
	public boolean isDirty() {
		return is_dirty;
	}

    /** Create the 'body', the main text of the ELog entry which
     *  lists all the changes.
     *  @return ELog text
     */
    private String createElogText()
    {
        final StringBuilder body = new StringBuilder(Messages.SaveIntro);

        // While changes to most cells are meant to be logged,
        // some cells' "main" PV might actually be the "comment" meta-PV
        // of another cell.
        // In that case, the comment should be logged with the "main" cell,
        // and the individual logging of the comment cell should be suppressed.

        // Map of 'main' cells to 'comment' cells
        final HashMap<Cell, Cell> comment_cell_map = new HashMap<Cell, Cell>();

        // Locate secondary comment cells
        for (int i=0; i<model.getInstanceCount(); ++i)
        {
            final Instance instance = model.getInstance(i);
            for (int c=0; c<model.getColumnCount(); ++c)
            {
                final Cell main_cell = instance.getCell(c);
                if (!main_cell.isEdited()  ||  !main_cell.hasMetaInformation())
                    continue;
                // Look for possible 'comment' cell for main_cell
                final String comment_pv_name = main_cell.getCommentPVName();
                boolean found_comment = false;
                for (int j=0; j<model.getInstanceCount() && !found_comment; ++j)
                {
                    final Instance search_instance = model.getInstance(j);
                    for (int d=0; d<model.getColumnCount(); ++d)
                    {
                        final Cell search_cell = search_instance.getCell(d);
                        if (search_cell.getName().equals(comment_pv_name))
                        {
                            comment_cell_map.put(main_cell, search_cell);
                            found_comment = true;
                            break;
                        }
                    }
                }
            }
        }

        // Loop over all cells to log changes
        for (int i=0; i<model.getInstanceCount(); ++i)
        {
            final Instance instance = model.getInstance(i);
            // Check every cell in each instance (row) to see if they have been
            // edited.  If they have add them to the elog message.
            for (int c=0; c<model.getColumnCount(); ++c)
            {
                final Cell cell = instance.getCell(c);
                if (!cell.isEdited())
                    continue;
                // Skip comment cells which will be logged when handling
                // the associated "main" cell
                if (comment_cell_map.containsValue(cell))
                   continue;
                body.append(NLS.bind(Messages.SavePVInfoFmt,
                                     new Object[]
                                     {
                                        cell.getName(),
                                        cell.getCurrentValue(),
                                        cell.getUserValue()
                                     }));
                // If the cell has comments, find the comment pv and log it's changed
                // value with the limit change log report.
                final Cell comment_cell = comment_cell_map.get(cell);
                if (comment_cell != null)
                    body.append(NLS.bind(Messages.SaveCommentInfoFmt,
                                         comment_cell.getValue()));
            }
        }
        return body.toString();
    }

	/**
	 * "Save" means create elog entry about changes, then write user values to
	 * PVs.
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(IProgressMonitor)
	 */
    @Override
	public void doSave(final IProgressMonitor monitor) {
        this.changes = createElogText();
        // Display ELog entry dialog
        this.shell = getSite().getShell();
      
        // "Normal" case with ELog support
		try {
			final String title = NLS.bind(Messages.ELogTitleFmt,
					model.getTitle());
			StringBuilder textContent = new StringBuilder();
			textContent.append(title).append("\n").append(changes);

			final LogEntryBuilderDialog logEntryBuilderDialog = new LogEntryBuilderDialog(
					shell, LogEntryBuilder.withText(textContent.toString()));

			logEntryBuilderDialog.addListener(this);
			logEntryBuilderDialog.setBlockOnOpen(true);
			logEntryBuilderDialog.open();
		} catch (Exception ex) {
			MessageDialog.openError(shell, Messages.SaveError,
					NLS.bind(Messages.SaveErrorFmt, ex.getMessage()));
		}
    }

    /** "SaveAs isn't allowed and should not get invoked,
     *  but in case it is, we handle it like 'doSave'
     */
    @Override
	public void doSaveAs() {
		doSave(new NullProgressMonitor());
	}

    /** @return <code>false</code> to prohibit 'save as' */
    @Override
	public boolean isSaveAsAllowed() {
		return false;
	}

    /** Update the editor's "dirty" state when model changes
     *  @see ModelListener
     */
    @Override
	public void cellUpdate(final Cell cell) {
		if (is_dirty == model.isEdited())
			return;
		is_dirty = model.isEdited();

		updateContentDescription();
		firePropertyChange(PROP_DIRTY);
	}

    /** Update the 'content description', i.e. a line just below
     *  the editor's title to show the model's title and some
     *  hint about the 'dirty' state.
     */
    private void updateContentDescription()
    {
        final String info;
        if (is_dirty)
            info = NLS.bind(Messages.FileChangedFmt, model.getTitle());
        else
            info = NLS.bind(Messages.FileUnchangedFmt, model.getTitle());
        setContentDescription(info);
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void initializeSaveAction(String userName) {
    	if (userName == null || userName.isEmpty()) {
    		userName = System.getenv("user.name");
    	}
		this.userName = userName;
	}

    /**
     * {@inheritDoc}
     * @throws Exception 
     */
	@Override
	public void saveProcessStatus(LogEntryBuilderEnum state) throws Exception {
		switch (state) {
		case START_SAVE:
			saveUserValues();
			break;
		case STOP_SAVE:
			finalizeSave();
			break;
		case CANCEL_SAVE:
			reverOginalValues();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Rever oginal values.
	 */
	private void reverOginalValues() {
		try {
			model.revertOriginalValues();
		} catch (Exception save_ex) {
			MessageDialog.openError(shell, Messages.SaveError,
                  NLS.bind(Messages.SaveErrorFmt, save_ex.getMessage()));
		}
	}
	
	/**
	 * Finalize save.
	 */
	private void finalizeSave() {
		model.clearUserValues();
	}
	
	/**
	 * Save user values.
	 * @throws Exception 
	 */
	private void saveUserValues() throws Exception {
		// The whole elog-and-pv-update should be handled
		// as a transaction that either succeeds or fails
		// as a whole.

		// Check if we can connect to the logbook (user, password)
		try { // Change PVs.
			model.saveUserValues(this.userName);
		} catch (Exception ex) { // At least some saves failed, to revert
			try {
				model.revertOriginalValues();
			} catch (Exception ignore) {
				// Since saving didn't work, restoral will also fail.
				// Hopefully those initial PVs that did get updated will
				// also be restored...
			}
			// Update error to be more specific, displayed by ELog dialog
			throw new Exception(NLS.bind(Messages.PVWriteErrorFmt,
					ex.getMessage()));
		}
	}


}

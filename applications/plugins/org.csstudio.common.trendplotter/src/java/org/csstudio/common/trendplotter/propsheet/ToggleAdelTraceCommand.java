/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.common.trendplotter.propsheet;

import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/**
 * Undo-able command to show the RecordField#ADEL range. 
 * 
 * @author bknerr
 * @since 29.06.2011
 */
public class ToggleAdelTraceCommand implements IUndoableCommand {
    
    final private Shell _shell;
    private final PVItem _item;
    
    /**
     * Constructor.
     */
    public ToggleAdelTraceCommand(final Shell shell,
                                  final OperationsManager operations_manager,
                                  final PVItem item) {
        _shell = shell;
        _item = item;
        try {
            _item.toggleShowAdel();
            operations_manager.addCommand(this);
        } catch (Exception e) {
            MessageDialog.openError(_shell,
                                    Messages.Error,
                                    NLS.bind(Messages.AddItemErrorFmt, item.getName(), e.getMessage()));
        }

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        try {
            _item.toggleShowAdel();
        } catch (Exception e) {
            MessageDialog.openError(_shell,
                                    Messages.Error,
                                    NLS.bind(Messages.AddItemErrorFmt, _item.getName(), e.getMessage()));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void redo() {
        try {
            _item.toggleShowAdel();
        } catch (Exception e) {
            MessageDialog.openError(_shell,
                                    Messages.Error,
                                    NLS.bind(Messages.AddItemErrorFmt, _item.getName(), e.getMessage()));
        }
    }
    
    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.ADELVisibilityCommand;
    }
}

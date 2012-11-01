/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.ModelItem;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * TODO (jhatje) : 
 * 
 * @author jhatje
 * @since 09.05.2012
 */
public class BulkEditTraceAction extends Action {
    private final Shell _shell;
    private final PVItem[] _pvs;
    private final OperationsManager _operations_manager;
    private final Model _model;
    
    public BulkEditTraceAction(Shell shell,
                          PVItem[] pvs,
                          OperationsManager operations_manager,
                          Model model) {
        _shell = shell;
        _pvs = pvs;
        _operations_manager = operations_manager;
        _model = model;
        setText("Bulk Edit");
    }
    
    @Override
    public void run() {
        BulkEditTraceDataModel bulkModel = new BulkEditTraceDataModel();
        bulkModel.createDataFromSelection(_pvs);
        BulkEditTraceDialog dialog = new BulkEditTraceDialog(_shell, _operations_manager, bulkModel, _model);
        if (dialog.open() != Window.OK)
            return;
        setData(bulkModel);
    }
    
    private void setData(BulkEditTraceDataModel bulkModel) {
        if (!bulkModel.getTraceDisplayName().equals("") && bulkModel.isTraceDisplayNameChanged()) {
            for (PVItem pvItem : _pvs) {
                new ChangeDisplayNameCommand(_operations_manager, pvItem, bulkModel.getTraceDisplayName());
            }
        }
        if (bulkModel.getScanPeriod() != -1 && bulkModel.isScanPeriodChanged()) {
            for (PVItem pvItem : _pvs) {
                new ChangeSamplePeriodCommand(_shell, _operations_manager, pvItem, bulkModel.getScanPeriod());
            }
        }
        if (bulkModel.getLiveCapacity() != -1 && bulkModel.isLiveCapacityChanged()) {
            for (PVItem pvItem : _pvs) {
                new ChangeLiveCapacityCommand(_shell, _operations_manager, pvItem, bulkModel.getLiveCapacity());
            }
        }
        if (bulkModel.getLineWidth() != -1 && bulkModel.isLineWidthChanged()) {
            for (PVItem pvItem : _pvs) {
                new ChangeLineWidthCommand(_operations_manager, (ModelItem) pvItem, bulkModel.getLineWidth());
            }
        }
        if (bulkModel.getAxis() != null && bulkModel.isAxisChanged()) {
            for (PVItem pvItem : _pvs) {
                new ChangeAxisCommand(_operations_manager, (ModelItem) pvItem, bulkModel.getAxis());
            }
        }
        if (bulkModel.getTraceType() != null && bulkModel.isTraceTypeChanged()) {
            for (PVItem pvItem : _pvs) {
                new ChangeTraceTypeCommand(_operations_manager, (ModelItem) pvItem, bulkModel.getTraceType());
            }
        }
        if (bulkModel.getRequestType() != null && bulkModel.isRequestTypeChanged()) {
            for (PVItem pvItem : _pvs) {
                new ChangeRequestTypeCommand(_operations_manager, pvItem, bulkModel.getRequestType());
            }
        }
        if (bulkModel.getVisible() != null && bulkModel.isVisibleChanged()) {
            for (PVItem pvItem : _pvs) {
                new ChangeVisibilityCommand(_operations_manager, pvItem, bulkModel.getVisible());
            }
        }
    }
    
}

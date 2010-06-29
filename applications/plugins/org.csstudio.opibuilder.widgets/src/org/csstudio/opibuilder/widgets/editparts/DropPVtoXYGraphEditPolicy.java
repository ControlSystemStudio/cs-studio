package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.dnd.DropPVRequest;
import org.csstudio.opibuilder.widgets.model.XYGraphModel;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

/**The editpolicy for dropping pv onto a XYGraph widget. It will add the dropped PVs to XYGraph as traces.
 * @author Xihui Chen
 *
 */
public class DropPVtoXYGraphEditPolicy extends AbstractEditPolicy {

	public final static String DROP_PV_ROLE = "DropPVEditPolicy"; //$NON-NLS-1$
	
	@Override
	public Command getCommand(Request request) {
		if(request.getType() == DropPVRequest.REQ_DROP_PV){
			DropPVRequest dropPVRequest =(DropPVRequest)request; 
			if(dropPVRequest.getTargetWidget() != null && 
					dropPVRequest.getTargetWidget() instanceof XYGraphEditPart){
				CompoundCommand command = new CompoundCommand("Add Traces");
				XYGraphModel xyGraphModel = (XYGraphModel) dropPVRequest.getTargetWidget().getWidgetModel();
				int existTraces = xyGraphModel.getTracesAmount();
				if(existTraces >= XYGraphModel.MAX_TRACES_AMOUNT)
					return null;
				command.add(new SetWidgetPropertyCommand(xyGraphModel,
						XYGraphModel.PROP_TRACE_COUNT, dropPVRequest.getPvNames().length + existTraces));
				int i=existTraces;
				for(String pvName : dropPVRequest.getPvNames()){
					command.add(new SetWidgetPropertyCommand(xyGraphModel, XYGraphModel.makeTracePropID(
									XYGraphModel.TraceProperty.YPV.propIDPre, i), pvName));
					command.add(new SetWidgetPropertyCommand(xyGraphModel, XYGraphModel.makeTracePropID(
							XYGraphModel.TraceProperty.NAME.propIDPre, i), pvName));
					if(++i >= XYGraphModel.MAX_TRACES_AMOUNT)
						break;
				}
				return command;
			}
					
		}
		return super.getCommand(request);
	}
	
	@Override
	public EditPart getTargetEditPart(Request request) {
		if(request.getType() == DropPVRequest.REQ_DROP_PV)
			return getHost();
		return super.getTargetEditPart(request);
	}
	
	
}

/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.opibuilder.actions;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action class that copies all widgets that are currently stored on the
 * Clipboard to the current Editor's display model.
 * 
 * @author Sven Wende(original author), Xihui Chen (since import from SDS 2009/9) 
 * 
 */
public final class PasteWidgetsAction extends WorkbenchPartAction {


	/**
	 * Stores the mouse pointer location.
	 */
	private org.eclipse.swt.graphics.Point _cursorLocation = null;

	/**
	 * Constructor.
	 * 
	 * @param workbenchPart
	 *            a workbench part
	 */
	public PasteWidgetsAction(OPIEditor workbenchPart) {
		super(workbenchPart);		
		setText("Paste");
		setActionDefinitionId("org.eclipse.ui.edit.paste"); //$NON-NLS-1$
		setId(ActionFactory.PASTE.getId());
		ISharedImages sharedImages = 
			workbenchPart.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages
        .getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		return getWidgetsFromClipboard() != null;
	}

	/**
	 * Creates the paste command.
	 * 
	 * @return Command The paste command
	 */
	public Command createPasteCommand() {
		
			AbstractContainerModel targetModel = getTargetContainerModel();

			List<AbstractWidgetModel> widgets = getWidgetsFromClipboard();

			if (widgets != null) {
				if (_cursorLocation==null) {
					this.fetchCurrentCursorLocation();
				}
				Control cursorControl = Display.getCurrent().getCursorControl();
				
				Point pastePoint;
				if(isCursorAboveTargetContainer(cursorControl, targetModel)){
					pastePoint = getCursorRelativePositionToTargetContainer(
							getCursorLocationOnDisplay(cursorControl), targetModel);
					//move the cursor location so that the user could know he has pasted how many widgets.
					Display.getCurrent().setCursorLocation(
							_cursorLocation.x + 10, 
							_cursorLocation.y + 10);
				}else {
					Random rand = new Random();
					pastePoint = new Point(rand.nextInt(20), rand.nextInt(20));
				}
				
				List<Point> intrinsicLocations = getWidgetsIntrinsicRelativePositions(widgets);
				
				CompoundCommand cmd = new CompoundCommand("Paste "
						+ widgets.size() + " Widget"
						+ (widgets.size() > 0 ? "s" : ""));
				int i=0;
				for (AbstractWidgetModel widgetModel : widgets) {
				
					// create command
					cmd.add(new WidgetCreateCommand(widgetModel, targetModel,
							new Rectangle(
									intrinsicLocations.get(i).translate(pastePoint), 
									widgetModel.getSize()), 
							(i== 0? false : true)));
					i++;
				
				}
				_cursorLocation = null;
				return cmd;
			}
		

		return null;

	}

	/**
	 * Detects if the given control (where the mouse is currently above) is from
	 * the {@link DisplayEditor}.
	 * 
	 * @param cursorControl
	 *            The control where the mouse is above
	 * @return true if the control is from the {@link DisplayEditor}, false
	 *         otherwise
	 */
	private boolean isCursorAboveTargetContainer(final Control cursorControl, 
			final AbstractContainerModel targetContainer) {
		Control parent = cursorControl;
		while (parent != null) {
			if (parent.equals(getOPIEditor().getParentComposite())) {	
				if(targetContainer instanceof DisplayModel)
					 return true;
				Rectangle targetAbsoluteBound = new Rectangle(
						getAbsolutePosition(targetContainer), targetContainer.getSize());
				return 
					targetAbsoluteBound.contains(getCursorLocationOnDisplay(cursorControl));
					
			}
			parent = parent.getParent();
		}
		return false;
	}

	/**
	 * Returns a list with widget models that are currently stored on the
	 * clipboard.
	 * 
	 * @return a list with widget models or an empty list
	 */
	@SuppressWarnings("unchecked")
	private List<AbstractWidgetModel> getWidgetsFromClipboard() {	
		Object result = getOPIEditor().getClipboard()
				.getContents(OPIWidgetsTransfer.getInstance());
		if(result != null && result instanceof List<?>){
			List<AbstractWidgetModel> widgets = (List<AbstractWidgetModel>)result;
			return widgets;
		}
		return null;
	}


	/**
	 * Returns the currently open OPI editor.
	 * 
	 * @return the currently open OPI editor
	 */
	private OPIEditor getOPIEditor() {

			return (OPIEditor) getWorkbenchPart();

	}

	/**
	 * Determines and stores the current mouse pointer location. The widgets
	 * from the clipboard will be pasted at the mouse pointer location, if the
	 * mouse pointer is within the editor. If this action is added to a context
	 * menu, this method should be called when the menu displays, so that the
	 * paste location is the location at which the user opened the context
	 * menu.
	 */
	public void fetchCurrentCursorLocation() {
		_cursorLocation = Display.getCurrent().getCursorLocation();
	}

	/**
	 * Performs the delete action on the selected objects.
	 */
	@Override
	public void run() {
		execute(createPasteCommand());		
	}
	
	public AbstractContainerModel getTargetContainerModel(){
		ISelection selection = ((GraphicalViewer)getOPIEditor().getAdapter(GraphicalViewer.class)).getSelection();
		if(selection != null && selection instanceof IStructuredSelection
				&& ((IStructuredSelection)selection).size() == 1){
			Object obj = ((IStructuredSelection)selection).getFirstElement();
			if(obj != null && obj instanceof EditPart){
				if(((EditPart)obj).getModel() instanceof AbstractContainerModel
						&& ((AbstractContainerModel) ((EditPart)obj).getModel()).isChildrenOperationAllowable()){
					return (AbstractContainerModel) ((EditPart)obj).getModel();
				}
			}
		}
		return getOPIEditor().getDisplayModel();
	}
	
	/**
	 * @param widgetModel
	 * @return the absolute position of a widget relative to display.
	 */
	private Point getAbsolutePosition(AbstractWidgetModel widgetModel){
		if(widgetModel instanceof DisplayModel)
			return new Point(0, 0);
		
		Point result = widgetModel.getLocation();
		AbstractContainerModel parent = widgetModel.getParent();
		while(!(parent instanceof DisplayModel)){
			result.translate(parent.getLocation());
			parent = parent.getParent();
		}
		return result;
		
	}
	
	private Point getCursorLocationOnDisplay(Control cursorControl){
		org.eclipse.swt.graphics.Point swtPoint = cursorControl.toControl(_cursorLocation);
		return new Point(swtPoint.x, swtPoint.y);
	}
	
	private Point getCursorRelativePositionToTargetContainer(
			Point cursorLocationOnDisplay, AbstractContainerModel targetContainer){
		Point targetAbsolutePosition = getAbsolutePosition(targetContainer);
		return cursorLocationOnDisplay.translate(-targetAbsolutePosition.x, -targetAbsolutePosition.y);
	}
	
	private List<Point> getWidgetsIntrinsicRelativePositions(List<AbstractWidgetModel> widgets){

		PointList pointList = new PointList(widgets.size());
		for (AbstractWidgetModel widgetModel : widgets) {			
			pointList.addPoint(widgetModel.getLocation());
		}
		
		Point upperLeftCorner = pointList.getBounds().getLocation();
		
		List<Point> result = new ArrayList<Point>(widgets.size());
		for(int i=0; i<widgets.size(); i++){
			result.add(pointList.getPoint(i).translate(-upperLeftCorner.x, -upperLeftCorner.y));
		}
		
		return result;
	}

	/**
	 * Refresh the action's enable state hence the action bars.
	 */
	public void refreshEnable() {
		refresh();
	}	

}

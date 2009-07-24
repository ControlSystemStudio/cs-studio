package org.csstudio.opibuilder.editparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class DisplayEditpart extends AbstractContainerEditpart {

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();

		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new RootComponentEditPolicy());
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
	}

	@Override
	protected IFigure createFigure() {
		
		Figure f = new FreeformLayer();
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());
		
		return f;
	}
	

}

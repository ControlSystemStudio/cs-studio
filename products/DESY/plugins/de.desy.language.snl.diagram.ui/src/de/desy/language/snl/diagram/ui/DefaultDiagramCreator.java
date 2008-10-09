package de.desy.language.snl.diagram.ui;

import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.diagram.model.SNLDiagram;
import de.desy.language.snl.diagram.model.StateModel;
import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.model.WhenModel;

public class DefaultDiagramCreator {
	
	public static SNLDiagram createDefaultDiagram() {
		SNLDiagram diagram = new SNLDiagram();
		StateModel state1 = new StateModel();
		state1.setName("State1");
		state1.setLocation(new Point(50,50));
		WhenModel when = new WhenModel("v>0", "State2");
		state1.setWhens(when.toString());
		diagram.addChild(state1);
		StateModel state2 = new StateModel();
		state2.setName("State2");
		state2.setLocation(new Point(150,50));
		when = new WhenModel("v>5", "State3");
		state2.setWhens(when.toString());
		diagram.addChild(state2);
		StateModel state3 = new StateModel();
		state3.setName("State3");
		state3.setLocation(new Point(250,50));
		when = new WhenModel("v>10", "State1");
		state3.setWhens(when.toString());
		diagram.addChild(state3);
		StateModel state4 = new StateModel();
		state4.setName("State3");
		state4.setLocation(new Point(350,50));
		when = new WhenModel("v>10", "State4");
		state4.setWhens(when.toString());
		diagram.addChild(state4);
		new WhenConnection(state1,state2);
		new WhenConnection(state2,state3);
		new WhenConnection(state3,state4);
		new WhenConnection(state3,state1);
		new WhenConnection(state4,state1);
		new WhenConnection(state4,state2);
		return diagram;
	}

}

package de.desy.language.snl.diagram.ui.command;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

public class AbstractBendPointCommand extends Command {
	
	private int _index;
	private Point _location;
	private Connection _connection;
	private Bendpoint _bendPoint;
	private Dimension _d1;
	private Dimension _d2;
	
	public Dimension getFirstRelativeDimension() {
		return _d1;
	}
	
	public Dimension getSecaondRelativeDimension() {
		return _d2;
	}
	
	public void setRelativeDimensions(Dimension firstDimension, Dimension secondDimension) {
		_d1 = firstDimension;
		_d2 = secondDimension;
	}

	public void setBendPoint(Bendpoint bendPoint) {
		_bendPoint = bendPoint;
	}

	public Bendpoint getBendPoint() {
		return _bendPoint;
	}

	public void setLocation(Point location) {
		_location = location;
	}

	public Point getLocation() {
		return _location;
	}

	public void setIndex(int index) {
		_index = index;
	}

	public int getIndex() {
		return _index;
	}
	
	@Override
	public void redo() {
		execute();
	}

	public void setConnection(Connection connection) {
		_connection = connection;
	}

	public Connection getConnection() {
		return _connection;
	}

}

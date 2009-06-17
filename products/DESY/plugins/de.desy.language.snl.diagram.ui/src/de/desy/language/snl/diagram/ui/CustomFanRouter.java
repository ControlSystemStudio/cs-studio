package de.desy.language.snl.diagram.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

public class CustomFanRouter extends AutomaticRouter {
	
	private class MapKey {
		private final ConnectionAnchor _source;
		private final ConnectionAnchor _target;

		public MapKey(ConnectionAnchor source, ConnectionAnchor target) {
			assert source != null : "source != null";
			assert target != null : "target != null";
			
			_source = source;
			_target = target;
		}

		public ConnectionAnchor getTarget() {
			return _target;
		}

		public ConnectionAnchor getSource() {
			return _source;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((_source == null) ? 0 : _source.hashCode());
			result = prime * result
					+ ((_target == null) ? 0 : _target.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MapKey other = (MapKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (_source == null) {
				if (other._source != null)
					return false;
			} else if (!_source.equals(other._source))
				return false;
			if (_target == null) {
				if (other._target != null)
					return false;
			} else if (!_target.equals(other._target))
				return false;
			return true;
		}

		private CustomFanRouter getOuterType() {
			return CustomFanRouter.this;
		}
		
	}

	private int _separation = 10;
	private final Map<MapKey, List<Connection>> _connectionMap;
	
	public CustomFanRouter() {
		_connectionMap = new HashMap<MapKey, List<Connection>>();
	}
	
	@Override
	public void route(Connection conn) {
		ConnectionAnchor sourceAnchor = conn.getSourceAnchor();
		ConnectionAnchor targetAnchor = conn.getTargetAnchor();
		
		MapKey key = new MapKey(sourceAnchor, targetAnchor);
		MapKey reverseKey = new MapKey(targetAnchor, sourceAnchor);
		
		if (_connectionMap.containsKey(key) || _connectionMap.containsKey(reverseKey)) {
			List<Connection> connections = _connectionMap.get(key);
			if (!connections.contains(conn)) {
				connections.add(conn);
			}
			int index = connections.indexOf(conn);
			handleCollision(conn, index);
		} else {
			List<Connection> list = new LinkedList<Connection>();
			list.add(conn);
			List<Connection> reverseList = new LinkedList<Connection>();
			reverseList.add(conn);
			_connectionMap.put(key, list);
			_connectionMap.put(reverseKey, reverseList);
		}
		
		super.route(conn);
	}
	
	@Override
	protected void handleCollision(PointList list, int index) {
		//nothing to do
	}

	protected void handleCollision(Connection conn, int index) {
		PointList list = conn.getPoints();
		Point firstPoint = list.getFirstPoint();
		Point lastPoint = list.getLastPoint();

		if (index != 0) {
			list.removeAllPoints();
			list.addAll(calculateNewPoints(firstPoint, lastPoint, index));
		}
	}

	private PointList calculateNewPoints(Point firstPoint, Point lastPoint,
			int index) {
		// orientationUp is based on the visual representation
		boolean orientationRightUp = firstPoint.x <= lastPoint.x;

		double realAngle = calculateNormalizedAngle(firstPoint, lastPoint);
		double firstAngle = realAngle - ((Math.PI / 30) * index);
		double lastAngle = realAngle + (Math.PI / 2) + ((Math.PI / 30) * index);

		double hypotenuse = calculateHypotenuse(_separation, index);

		double firstOppositeLeg = Math.cos(firstAngle) * hypotenuse;
		double firstAdjacentLeg = Math.sin(firstAngle) * hypotenuse;

		double lastOppositeLeg = Math.cos(lastAngle) * hypotenuse;
		double lastAdjacentLeg = Math.sin(lastAngle) * hypotenuse;

		double newFirstX = 0;
		double newFirstY = 0;

		double newLastX = 0;
		double newLastY = 0;

		if (orientationRightUp) {
			newFirstX = firstPoint.x + firstAdjacentLeg;
			newFirstY = firstPoint.y - firstOppositeLeg;
			
			newLastX = lastPoint.x - lastAdjacentLeg;
			newLastY = lastPoint.y + lastOppositeLeg;
		} else {
			newFirstX = firstPoint.x - firstAdjacentLeg;
			newFirstY = firstPoint.y + firstOppositeLeg;
			
			newLastX = lastPoint.x + lastAdjacentLeg;
			newLastY = lastPoint.y - lastOppositeLeg;
		}

		Point firstBendPoint = new Point(newFirstX, newFirstY);
		Point secondBendPoint = new Point(newLastX, newLastY);

		PointList result = new PointList();
		result.addPoint(firstPoint);
		result.addPoint(firstBendPoint);
		result.addPoint(secondBendPoint);
		result.addPoint(lastPoint);

		return result;
	}

	private double calculateNormalizedAngle(Point firstPoint, Point lastPoint) {
		double angle = 0.0;
		double deltaX = lastPoint.x - firstPoint.x;
		double deltaY = lastPoint.y - firstPoint.y;
		double gradient = deltaY / deltaX;

		angle = Math.atan(gradient); // 0 - 2Pi
		angle = angle + Math.PI / 4;

		if (angle >= 2 * Math.PI) {
			angle = angle - 2 * Math.PI;
		}

		return angle;
	}

	private double calculateHypotenuse(int distance, int index) {
		double result = Math.sqrt(2 * (distance * distance)) * index;
		return result;
	}

	public void setSeparation(int separation) {
		_separation = separation;
	}

	public int getSeparation() {
		return _separation;
	}

}

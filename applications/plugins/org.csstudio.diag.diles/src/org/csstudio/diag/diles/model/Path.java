package org.csstudio.diag.diles.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class Path extends AbstractChartElement {
	protected Activity source, target;
	protected String sourceName, targetName;

	protected String sourceId, targetId;

	protected boolean status = false;
	protected double doubleStatus = 0.0d;

	protected List<WireBendpoint> bendpoints = new ArrayList<WireBendpoint>();

	public void attachSource() {
		if (getSource() == null
				|| getSource().getSourceConnections().contains(this))
			return;
		getSource().addSourceConnection(this);
	}

	public void attachTarget() {
		if (getTarget() == null
				|| getTarget().getTargetConnections().contains(this))
			return;
		getTarget().addTargetConnection(this);
	}

	public void detachSource() {
		if (getSource() == null)
			return;
		getSource().removeSourceConnection(this);
	}

	public void detachTarget() {
		if (getTarget() == null)
			return;
		getTarget().removeTargetConnection(this);
	}

	public void disconnect() {
		source.removeSourceConnection(this);
		target.removeTargetConnection(this);
	}

	public List<WireBendpoint> getBendpoints() {
		return bendpoints;
	}

	public double getDoubleStatus() {
		return doubleStatus;
	}

	public Activity getSource() {
		return source;
	}

	public String getSourceId() {
		return sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public boolean getStatus() {
		return status;
	}

	public Activity getTarget() {
		return target;
	}

	public String getTargetId() {
		return targetId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void insertBendpoint(int index, WireBendpoint point) {
		getBendpoints().add(index, point);
		firePropertyChange("bendpoint", null, null);//$NON-NLS-1$
	}

	public void reconnect() {
		source.addSourceConnection(this);
		target.addTargetConnection(this);
	}

	public void removeBendpoint(int index) {
		getBendpoints().remove(index);
		firePropertyChange("bendpoint", null, null);//$NON-NLS-1$
	}

	public void setBendpoint(int index, WireBendpoint point) {
		getBendpoints().set(index, point);
		firePropertyChange("bendpoint", null, null);//$NON-NLS-1$
	}

	public void setBendpoints(Vector points) {
		bendpoints = points;
		firePropertyChange("bendpoint", null, null);//$NON-NLS-1$
	}

	public void setDoubleStatus(double doubleStatus) {
		// System.out.println(doubleStatus);
		this.doubleStatus = doubleStatus;
	}

	public void setSource(Activity e) {
		source = e;
	}

	public void setSourceId(String id) {
		sourceId = id;
	}

	public void setSourceName(String s) {
		sourceName = s;
	}

	public void setStatus(boolean value) {

		if (getSource().getColumn() == getTarget().getColumn()) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell();
			MessageDialog.openError(shell, "Warning",
					"Output and input should not be in the same column.");
		}

		if (value == this.status)
			return;
		this.status = value;
		if (target != null) {
			// target.update();
		}
		firePropertyChange(AbstractChartElement.ACTIVITY_STATUS, null, null);
	}

	public void setTarget(Activity e) {
		target = e;
	}

	public void setTargetId(String id) {
		targetId = id;
	}

	public void setTargetName(String s) {
		targetName = s;
	}

}
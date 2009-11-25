package de.desy.language.snl.parser.nodes;

import de.desy.language.snl.codeElements.PredefinedTypes;

/**
 * The outline node of an SNL variable.
 * 
 * @author C1 WPS / KM, MZ
 */
public class VariableNode extends AbstractSNLNode {

	private final String _variableName;
	private final String _variableType;
	private AssignStatementNode _assignNode;
	private MonitorStatementNode _monitorNode;
	private final boolean _isArray;

	public VariableNode(final String variableName, final String variableType,
			final int statementsStartOffsetInSource,
			final int statementsEndOffsetInSource, boolean isArray) {
		_variableName = variableName;
		_variableType = variableType;
		_assignNode = null;
		_monitorNode = null;
		_isArray = isArray;
		
		this.setStatementOffsets(statementsStartOffsetInSource,
				statementsEndOffsetInSource);
	}

	@Override
	public String humanReadableRepresentation() {
		final StringBuffer result = new StringBuffer(this.getNodeTypeName());
		result.append(": ");
		result.append(this.getSourceIdentifier());
		result.append(" (Type: ");
		result.append(this._variableType);
		if (this.isAssigned()) {
			result.append(", assigned to \"");
			result.append(this.getAssignedChannelName());
			result.append("\"");
		}
		if (this.isMonitored()) {
			result.append(", monitored");
		}
		result.append(")");
		return result.toString();
	}

	@Override
	public String getNodeTypeName() {
		return "Variable";
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._variableName;
	}

	/**
	 * Change this method to return an element of {@link PredefinedTypes}.
	 */
	public String getTypeName() {
		return this._variableType;
	}

	public boolean isAssigned() {
		return this._assignNode != null;
	}

	public void setAssignedChannel(final AssignStatementNode assignNode) {
		this._assignNode = assignNode;
		this.addChild(assignNode);
	}

	public String getAssignedChannelName() {
		return this._assignNode.getContent();
	}

	public boolean isMonitored() {
		return this._monitorNode != null;
	}
	
	public boolean isArray() {
		return _isArray;
	}

	public void setMonitored(final MonitorStatementNode monitorNode) {
		this._monitorNode = monitorNode;
		this.addChild(this._monitorNode);
	}

}

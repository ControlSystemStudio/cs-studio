package de.desy.language.snl.parser.nodes;

/**
 * The outline node of an SNL variable.
 * 
 * @author C1 WPS / KM, MZ
 */
public class AllVariablesNode extends AbstractSNLNode {

	public AllVariablesNode() {
	}

	@Override
	public String humanReadableRepresentation() {
		return "Variables";
	}

	@Override
	public String getNodeTypeName() {
		return "Variables";
	}

	@Override
	protected String doGetSourceIdentifier() {
		return "Variables";
	}

}

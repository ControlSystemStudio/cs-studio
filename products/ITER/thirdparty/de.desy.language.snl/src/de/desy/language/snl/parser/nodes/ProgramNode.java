package de.desy.language.snl.parser.nodes;

import de.desy.language.libraries.utils.contract.Contract;

/**
 * The root node of an SNL program outline.
 * 
 * @author C1 WPS / KM, MZ
 */
public class ProgramNode extends AbstractSNLNode {

	private String _programName;

	public ProgramNode(final String programName, final int startOffset,
			final int endOffset) {
		Contract.requireNotNull("programName", programName);

		this.setStatementOffsets(startOffset, endOffset);

		this._programName = programName;
	}

	@Override
	public String humanReadableRepresentation() {
		return this.getNodeTypeName()
				+ " "
				+ (this._programName != null ? this._programName
						: "(name not avail)");
	}

	/**
	 * Gives the program name, may null.
	 */
	public String getProgramName() {
		return this._programName;
	}

	public void setProgramName(final String programName) {
		Contract.requireNotNull("programName", programName);

		this._programName = programName;
	}

	@Override
	public String getNodeTypeName() {
		return "Program";
	}

	@Override
	protected String doGetSourceIdentifier() {
		return this._programName;
	}

}

package org.csstudio.sds.ui.logic;

import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * Editor for SDS script rules.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public class ScriptEditor extends AbstractTextEditor {
	/**
	 * Constructor.
	 */
	public ScriptEditor() {
		setDocumentProvider(new ScriptDocumentProvider());
	}

}

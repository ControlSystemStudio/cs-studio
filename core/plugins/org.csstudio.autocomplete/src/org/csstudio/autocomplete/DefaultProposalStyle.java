/**
 * 
 */
package org.csstudio.autocomplete;

import org.eclipse.swt.SWT;

/**
 * An Default instance of ProposalStyle with the UI parameters preconfigured.
 * Use this class to ensure that all the autocomplete proposals have the same
 * highlight look and feel
 * 
 * @author shroffk
 * 
 */
public class DefaultProposalStyle extends ProposalStyle {

    public DefaultProposalStyle(int from, int to) {
	super(from, to, SWT.BOLD, SWT.COLOR_BLUE);
    }

}

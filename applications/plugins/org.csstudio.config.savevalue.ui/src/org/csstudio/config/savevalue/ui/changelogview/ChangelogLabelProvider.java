/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.config.savevalue.ui.changelogview;

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for the changelog table.
 * 
 * @author Joerg Rathlev
 */
class ChangelogLabelProvider extends LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnText(final Object element, final int columnIndex) {
		if (element instanceof ChangelogEntry) {
			ChangelogEntry entry = (ChangelogEntry) element;
			switch (columnIndex) {
			case ChangelogViewPart.COL_PV:
				return entry.getPvName();
			case ChangelogViewPart.COL_VALUE:
				return entry.getValue();
			case ChangelogViewPart.COL_USER:
				return entry.getUsername();
			case ChangelogViewPart.COL_HOST:
				return entry.getHostname();
			case ChangelogViewPart.COL_MODIFIED:
				return entry.getLastModified();
			default:
				return null;
			}
		}
		return null;
	}
}

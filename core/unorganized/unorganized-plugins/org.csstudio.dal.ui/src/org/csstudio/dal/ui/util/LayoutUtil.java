/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.dal.ui.util;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * This class provides som useful static convinience methods for standard SWT
 * layout tasks.
 * 
 * @deprecated Use {@link GridDataFactory} instead - its much more convenient.
 * 
 * @author Sven Wende
 */
public final class LayoutUtil {
	/**
	 * Private constructor to prevent instantiation.
	 */
	private LayoutUtil() {
	}

	/**
	 * Creates a GridLayout.
	 * 
	 * @param columns
	 *            number of columns
	 * @param margin
	 *            margin width
	 * @param verticalSpacing
	 *            vertical spacing
	 * @param horizontalSpacing
	 *            horizontal spacing
	 * @return a GridLayout
	 */
	public static GridLayout createGridLayout(final int columns,
			final int margin, final int verticalSpacing,
			final int horizontalSpacing) {
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginHeight = margin;
		layout.marginWidth = margin;
		layout.verticalSpacing = verticalSpacing;
		layout.horizontalSpacing = horizontalSpacing;

		return layout;
	}

	/**
	 * Creates a GridLayout.
	 * 
	 * @param columns
	 *            number of columns
	 * @param leftMargin
	 *            left margin width
	 * @param rightMargin
	 *            right margin width
	 * @param topMargin
	 *            top margin width
	 * @param bottomMargin
	 *            bottom margin width
	 * @param verticalSpacing
	 *            vertical spacing
	 * @param horizontalSpacing
	 *            horizontal spacing
	 * @return a GridLayout
	 */
	public static GridLayout createGridLayout(final int columns,
			final int leftMargin, final int rightMargin, final int topMargin,
			final int bottomMargin, final int verticalSpacing,
			final int horizontalSpacing) {
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		layout.marginTop = topMargin;
		layout.marginBottom = bottomMargin;
		layout.marginLeft = leftMargin;
		layout.marginRight = rightMargin;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = verticalSpacing;
		layout.horizontalSpacing = horizontalSpacing;

		return layout;
	}

	/**
	 * Creates a GridData, which will fill the current cell horizontally and
	 * vertically.
	 * 
	 * @return a GridData
	 */
	public static GridData createGridData() {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;

		return gd;
	}

	/**
	 * Creates a GridData, which will fill the current cell horizontally and
	 * grab the specified width and height.
	 * 
	 * @param width
	 *            width hint
	 * @param height
	 *            height hint
	 * 
	 * @return a GridData
	 */
	public static GridData createGridData(final int width, final int height) {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.minimumHeight = height;
		gd.minimumWidth = width;
		gd.widthHint = width;
		gd.heightHint = height;

		return gd;
	}

	/**
	 * Creates a GridData, which will fill the current cell horizontally and
	 * grab the specified width.
	 * 
	 * @param width
	 *            width hint
	 * @return a GridData
	 */
	public static GridData createGridData(final int width) {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.minimumWidth = width;
		gd.widthHint = width;

		return gd;
	}

	/**
	 * Creates a GridData, which will fill the current cell horizontally and
	 * fully grab the available space.
	 * 
	 * @return a GridData
	 */
	public static GridData createGridDataForFillingCell() {
		GridData gd = new GridData();
		gd.verticalAlignment = 1;
		gd.horizontalAlignment = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;

		return gd;
	}

	/**
	 * Creates a GridData, which will grab the specified space.
	 * 
	 * @param width
	 *            preferred width
	 * @param height
	 *            preferred height
	 * @return a GridData
	 */
	public static GridData createGridDataForFillingCell(final int width,
			final int height) {
		GridData gd = createGridDataForFillingCell();
		gd.minimumHeight = height;
		gd.minimumWidth = width;
		gd.widthHint = width;
		gd.heightHint = height;

		return gd;
	}

	/**
	 * Creates a GridData, which will grab the available horizontal space.
	 * 
	 * @return a GridData
	 */
	public static GridData createGridDataForHorizontalFillingCell() {
		GridData gd = createGridDataForFillingCell();

		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = SWT.TOP;

		return gd;
	}

	/**
	 * Creates a GridData, which will grab the available vertical space.
	 * 
	 * @return a GridData
	 */
	public static GridData createGridDataForVerticalFillingCell() {
		GridData gd = createGridDataForFillingCell();

		gd.grabExcessHorizontalSpace = false;
		gd.horizontalAlignment = SWT.TOP;

		return gd;
	}

	/**
	 * Creates a GridData, which will grab the available horizontal space and
	 * use the specified heigth.
	 * 
	 * @param height
	 *            the preferred height
	 * 
	 * @return a GridData
	 */
	public static GridData createGridDataForHorizontalFillingCell(
			final int height) {
		GridData gd = createGridDataForFillingCell();

		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = SWT.TOP;

		gd.minimumHeight = height;
		gd.heightHint = height;

		return gd;
	}

	/**
	 * Creates a GridData, which will grab the available vertical space and use
	 * the specified width.
	 * 
	 * @param width
	 *            the preferred width
	 * 
	 * @return a GridData
	 */
	public static GridData createGridDataForVerticalFillingCell(final int width) {
		GridData gd = createGridDataForFillingCell();

		gd.grabExcessHorizontalSpace = false;
		gd.horizontalAlignment = SWT.TOP;

		gd.minimumWidth = width;
		gd.widthHint = width;

		return gd;
	}

	/**
	 * Creates a simple group with a FillLayout.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            a group description
	 * @return a Group
	 */
	public static Group createGroupWithFillLayout(final Composite parent,
			final String text) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(text);
		FillLayout layout = new FillLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		group.setLayout(layout);
		return group;
	}

}

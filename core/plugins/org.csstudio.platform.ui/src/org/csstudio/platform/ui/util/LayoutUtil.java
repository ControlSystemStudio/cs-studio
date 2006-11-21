package org.csstudio.platform.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * This class provides several helper methods for SWT layout tasks.
 * 
 * @author swende
 * 
 */
public class LayoutUtil {
	private LayoutUtil() {
	}

	public static GridLayout createGridLayout(final int columns, final int margin,
			final int verticalSpacing, final int horizontalSpacing) {
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

	public static GridLayout createGridLayout(final int columns, final int leftMargin,
			final int rightMargin, final int topMargin, final int bottomMargin,
			final int verticalSpacing, final int horizontalSpacing) {
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

	public static GridData createGridData() {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;

		return gd;
	}

	public static GridData createGridData(final int height, final int width) {
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

	public static GridData createGridDataForFillingCell(final int height, final int width) {
		GridData gd = createGridDataForFillingCell();
		gd.minimumHeight = height;
		gd.minimumWidth = width;
		gd.widthHint = width;
		gd.heightHint = height;

		return gd;
	}

	public static GridData createGridDataForHorizontalFillingCell() {
		GridData gd = createGridDataForFillingCell();

		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = SWT.TOP;

		return gd;
	}

	public static GridData createGridDataForVerticalFillingCell() {
		GridData gd = createGridDataForFillingCell();

		gd.grabExcessHorizontalSpace = false;
		gd.horizontalAlignment = SWT.TOP;

		return gd;
	}

	public static GridData createGridDataForHorizontalFillingCell(final int height) {
		GridData gd = createGridDataForFillingCell();

		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = SWT.TOP;

		gd.minimumHeight = height;
		gd.heightHint = height;

		return gd;
	}

	public static GridData createGridDataForVerticalFillingCell(final int width) {
		GridData gd = createGridDataForFillingCell();

		gd.grabExcessHorizontalSpace = false;
		gd.horizontalAlignment = SWT.TOP;

		gd.minimumWidth = width;
		gd.widthHint = width;

		return gd;
	}

	public static Group createGroupWithFillLayout(final Composite parent, final String text) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(text);
		FillLayout layout = new FillLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		group.setLayout(layout);
		group.setLayoutData(createGridDataForFillingCell());
		return group;
	}

	

}
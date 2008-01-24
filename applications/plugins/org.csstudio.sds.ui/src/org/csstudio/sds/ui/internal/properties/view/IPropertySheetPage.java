package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.part.IPage;

/**
 * Interface for a property sheet page that appears in a property sheet view.
 * This interface defines the minimum requirement for pages within the property
 * sheet view, namely they must be pages (implement <code>IPage</code>) be
 * prepared to contribute actions to the property sheet view's tool bar and
 * status line, and listen for selection changes in the active part.
 * <p>
 * Clients may implement this interface from scratch if the property sheet
 * viewer's default page is unsuitable for displaying a part's properties.
 * </p>
 * 
 * @see PropertySheetPage
 * 
 * @author Sven Wende
 */
public interface IPropertySheetPage extends IPage, ISelectionListener {
}

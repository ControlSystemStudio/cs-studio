package org.csstudio.alarm.table.dataModel;

import org.csstudio.platform.model.AbstractControlSystemItemFactory;

/**
 * Implementation of {@link AbstractControlSystemItemFactory} for text
 * containers.
 * 
 * @author Sven Wende
 * 
 */
public final class TextContainerFactory extends
		AbstractControlSystemItemFactory<TextContainer> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String createStringRepresentationFromItem(final TextContainer item) {
		return item.getTitle() + ":" + item.getTitle(); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TextContainer createItemFromStringRepresentation(final String string) {
		assert string != null;

		String[] pieces = string.split(":"); //$NON-NLS-1$

		String title = pieces[0];
		String text = pieces[1];

		TextContainer textContainer = new TextContainer(title, text);

		return textContainer;

	}

}

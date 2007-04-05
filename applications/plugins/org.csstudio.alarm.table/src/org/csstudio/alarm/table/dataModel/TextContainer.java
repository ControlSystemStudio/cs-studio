package org.csstudio.alarm.table.dataModel;

import org.csstudio.platform.model.AbstractControlSystemItem;

/**
 * An example for a custom control system item. A text container simply consists of a
 * title and text.
 * 
 * @author Sven Wende
 * 
 */
public final class TextContainer extends AbstractControlSystemItem {
	/**
	 * Type ID for text containers.
	 */
	public static final String TYPE_ID = "css:sandbox:textContainer"; //$NON-NLS-1$

	/**
	 * The title.
	 */
	private String _title;

	/**
	 * The text.
	 */
	private String _text;

	/**
	 * Constructs a TextContainer.
	 * 
	 * @param title
	 *            the initial title
	 * @param text
	 *            the initial text
	 */
	public TextContainer(final String title, final String text) {
		super(title);
		_title = title;
		_text = text;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTypeId() {
		return TYPE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return _title;
	}

	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		return _text;
	}

	/**
	 * Sets the text.
	 * 
	 * @param text
	 *            the text
	 */
	public void setText(final String text) {
		_text = text;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the title
	 */
	public void setTitle(final String title) {
		_title = title;
	}
}

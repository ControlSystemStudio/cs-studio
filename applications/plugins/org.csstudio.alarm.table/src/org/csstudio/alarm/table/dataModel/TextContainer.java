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

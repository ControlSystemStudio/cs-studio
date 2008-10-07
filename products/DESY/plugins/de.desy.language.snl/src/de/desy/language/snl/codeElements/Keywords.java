/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
package de.desy.language.snl.codeElements;

import de.desy.language.editor.core.ILanguageElements;

/**
 * A enumeration of all SNL keywords.
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.3
 */
public enum Keywords implements ILanguageElements {
	ASSIGN("assign"), //$NON-NLS-1$
	BREAK("break"), //$NON-NLS-1$
	ELSE("else"), //$NON-NLS-1$
	ENTRY("entry"), //$NON-NLS-1$
	EXIT("exit"), //$NON-NLS-1$
	FOR("for"), //$NON-NLS-1$
	IF("if"), //$NON-NLS-1$
	MONITOR("monitor"), //$NON-NLS-1$
	OPTION("option"), //$NON-NLS-1$
	PROGRAM("program"), 
	STATE("state"), 
	STATE_SET("ss"), //$NON-NLS-1$
	SYNC("sync"), 
	TO("to"), 
	UNSIGNED("unsigned"), 
	WHEN("when"), 
	WHILE("while"),
	DEFINE("define"); //$NON-NLS-1$

	/**
	 * The name in the source.
	 */
	private String _elementName;

	/**
	 * Initializer of the enum-values.
	 * 
	 * @param elementName
	 *            The name in the source.
	 */
	private Keywords(final String elementName) {
		this._elementName = elementName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getElementName() {
		return this._elementName;
	}

}

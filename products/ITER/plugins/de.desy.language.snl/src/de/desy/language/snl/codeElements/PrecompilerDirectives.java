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
 * Preprocessor directives in SNL.
 * 
 * TODO Recognize this directives in highlighting and parsing!
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public enum PrecompilerDirectives implements ILanguageElements {

	POUND_DEFINE("#define"); //$NON-NLS-1$

	/**
	 * The name in the source.
	 */
	private String _inCodeName;

	/**
	 * Initializer of the enum-values.
	 * 
	 * @param inCodeName
	 *            The name in the source.
	 */
	PrecompilerDirectives(final String inCodeName) {
		this._inCodeName = inCodeName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getElementName() {
		return this._inCodeName;
	}
}

/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

/** Base class for Annunciator implementations
 *  @author Kay Kasemir
 */
abstract public class BaseAnnunciator implements Annunciator
{
    protected Translation translations[] = null;

    /** Define translations */
    @Override
    public void setTranslations(final Translation[] translations)
    {
        this.translations = translations;
    }

    /** Apply all translations to input
     *  @param something Text to speak
     *  @return Text after applying all translations
     */
    protected String applyTranslations(String something)
    {
        if (translations == null)
            return something;
        for (Translation translation : translations)
            something = translation.apply(something);
        return something;
    }
}

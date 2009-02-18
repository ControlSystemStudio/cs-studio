/*
 * Copyright (c) 2003 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package com.cosylab.vdct;

/**
 *
 * @author ilist
 */
public interface ConsoleInterface
{
	/**
	 * DOCUMENT ME!
	 */
	public void flush();

	/**
	 * DOCUMENT ME!
	 *
	 * @param text DOCUMENT ME!
	 */
	public void print(String text);

	/**
	 * DOCUMENT ME!
	 */
	public void println();

	/**
	 * DOCUMENT ME!
	 *
	 * @param text DOCUMENT ME!
	 */
	public void println(String text);

	/**
	 * DOCUMENT ME!
	 *
	 * @param thr DOCUMENT ME!
	 */
	public void println(Throwable thr);

	/**
	 * DOCUMENT ME!
	 *
	 * @param string DOCUMENT ME!
	 */
	public void silent(String string);
}

/* __oOo__ */

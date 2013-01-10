package org.csstudio.archive.reader.kblog;

import org.csstudio.archive.reader.ValueIterator;

/**
 * Value iterator interface with isClosed() method. 
 * 
 * @author Takashi Nakamoto
 */
public interface KBLogValueIterator extends ValueIterator {
	public boolean isClosed();
}

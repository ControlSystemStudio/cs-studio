package org.csstudio.archive.crawl;

import java.util.Iterator;

import org.csstudio.platform.data.IValue;

/** Type simplification:
 *  <code>new Iterator&lt;Value&gt;[N]</code> results in compiler error,
 *  but <code>new ValueIterator[N]</code> is fine,
 *  even though they're really expressing the same thing.
 *  @author Kay Kasemir
 */
public interface ValueIterator extends Iterator<IValue>
{
    // empty since it's just a typedef
}

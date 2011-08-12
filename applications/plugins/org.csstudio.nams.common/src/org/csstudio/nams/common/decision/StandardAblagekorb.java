
/* 
 * Copyright (c) 2008 C1 WPS mbH, 
 * HAMBURG, GERMANY.
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
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */

package org.csstudio.nams.common.decision;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import org.csstudio.nams.common.wam.Material;

@Material
public class StandardAblagekorb<T extends Ablagefaehig> implements
		Eingangskorb<T>, Ausgangskorb<T>, Zwischenablagekorb<T> {
	private final LinkedBlockingQueue<T> inhalt;

	public StandardAblagekorb() {
		this.inhalt = new LinkedBlockingQueue<T>();
	}

	/**
	 * Legt eine neues Dokument in den Korb.
	 * 
	 * @param dokument
	 *            Das neue Dokuement,
	 * @throws InterruptedException
	 */
	@Override
    public void ablegen(final T dokument) throws InterruptedException {
		this.inhalt.put(dokument);
	}

	/**
	 * Entnimmt den ältesten Eingang aus diesem Korb. Achtung:
	 * <ol>
	 * <li>Der entnommene Eingang ist anschließend nicht mehr enthalten!</li>
	 * <ol>
	 * <li>Dieses Operation blockiert bis ein dokument verfügbar ist!</li>
	 * </ol>
	 * 
	 * @throws InterruptedException
	 *             Falls der Thread beim warten auf ein Element unterbrochen
	 *             wird.
	 */
	@Override
    public T entnehmeAeltestenEingang() throws InterruptedException {
		return this.inhalt.take();
	}

	public boolean istEnthalten(final T element) {
		return this.inhalt.contains(element);
	}

	@Override
    public Iterator<T> iterator() {
		return this.inhalt.iterator();
	}
}

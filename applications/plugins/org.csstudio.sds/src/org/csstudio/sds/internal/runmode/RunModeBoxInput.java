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
package org.csstudio.sds.internal.runmode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

/**
 * Collects the necessary information for a single run mode window (shell or
 * view).
 *
 * @author Sven Wende
 *
 */
public class RunModeBoxInput implements Serializable {

    private static final long serialVersionUID = 6449572208586410269L;

    private transient IPath _filePath;

    private transient Map<String, String> _aliases;

    private RunModeType _type;

    private RunModeBoxInput _predecessorBox;

    private long _timestamp;

    /**
     * Constructor.
     *
     * @param filePath
     *            the path to the display
     * @param aliases
     *            the aliases
     * @param type
     *            the run mode type
     */
    public RunModeBoxInput(IPath filePath, Map<String, String> aliases,
            RunModeType type) {
        assert filePath != null;
        assert aliases != null;
        assert type != null;
        _filePath = filePath;
        _aliases = aliases;
        _type = type;
        _timestamp = System.currentTimeMillis();
    }

    /**
     * Gets the predecessor box´s input.
     *
     * @return the predecessor box´s input
     */
    public RunModeBoxInput getPredecessorBox() {
        return _predecessorBox;
    }

    /**
     * Sets the predecessor box´s input.
     *
     * @param input
     *            the input of the predecessor box
     */
    public void setPredecessorBox(RunModeBoxInput input) {
        _predecessorBox = input;
    }

    public IPath getFilePath() {
        return _filePath;
    }

    public Map<String, String> getAliases() {
        return new HashMap<String, String>(_aliases);
    }

    public RunModeType getType() {
        return _type;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        String fullpath = calculateFullPath();
        return fullpath.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof RunModeBoxInput) {
            RunModeBoxInput input = (RunModeBoxInput) obj;

            result = calculateFullPath().equals(input.calculateFullPath());
        }

        return result;
    }

    public String calculateFullPath() {
        StringBuffer sb = new StringBuffer();
        sb.append(_filePath.toPortableString());

        if (_aliases != null && !_aliases.keySet().isEmpty()) {
            sb.append("?");

            Iterator<String> it = _aliases.keySet().iterator();

            while (it.hasNext()) {
                String key = it.next();
                String val = _aliases.get(key);
                sb.append(key);
                sb.append("=");
                sb.append(val);
                sb.append(it.hasNext() ? "," : "");
            }
        }

        return sb.toString();
    }
}

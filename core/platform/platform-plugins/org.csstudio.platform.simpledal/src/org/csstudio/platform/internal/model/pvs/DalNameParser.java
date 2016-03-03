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
 package org.csstudio.platform.internal.model.pvs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;

/**
 * Name parser, which can be parameterized using a {@link ControlSystemEnum}.
 *
 * The parser will detect so called "characteristics", which are appended to the
 * normal process variable String using square bracket, e.g.
 * <b>prefix://anyproperty[characteristic]</b>.
 *
 *
 * @author Sven Wende
 *
 */
public class DalNameParser extends AbstractProcessVariableNameParser {

    /**
     * The control system.
     */
    private ControlSystemEnum _controlSystem;

    /**
     * Constructor.
     *
     * @param controlSystem
     *            the control system
     */
    public DalNameParser(final ControlSystemEnum controlSystem) {
        _controlSystem = controlSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IProcessVariableAddress doParse(final String nameWithoutPrefix,
            final String rawName) {
        ProcessVariableAdress result = null;
        // compile a regex pattern and parse the String
        // the used regular expression checks for the following uri components:
        // 1) line start
        // 2) 1-n arbitrary chars, except [ and ] (mandatory)
        // 3) [ followed by 1-n arbitrary chars, except [ and ], followed by ]
        // (optional)
        // 4) line end
        Pattern p = Pattern.compile("^([^,\\[\\]]+)(\\[([^\\[\\]]+)\\])?(, ([a-z;A-Z]+))?$");

        Matcher m = p.matcher(nameWithoutPrefix);

        if (m.find()) {
            String property = m.group(1);
            String characteristic = m.group(3);
            String typeHint = m.group(5);

            result = new ProcessVariableAdress(rawName, _controlSystem, null,
                    property, characteristic);

            if(typeHint!=null) {
                ValueType valueType = ValueType.createFromPortable(typeHint);
                result.setValueTypeHint(valueType);
            }

        }

        return result;
    }

}

package com.cosylab.vdct.vdb;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.regex.Pattern;
import com.cosylab.vdct.inspector.InspectableProperty;

/**
 * Insert the type's description here.
 * @author Matej Sekoranja
 */
public class MacroDescriptionProperty implements InspectableProperty {
        private static final String defaultDescription = "";
        private static final String name = "Description";
        private static final String helpString = "Port description";

        private VDBMacro macro = null;

        /**
         * Constructor
         */
        public MacroDescriptionProperty(VDBMacro macro)
        {
            this.macro=macro;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#allowsOtherValues()
         */
        public boolean allowsOtherValues()
        {
            return false;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getEditPattern()
         */
        public Pattern getEditPattern()
        {
            return null;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getHelp()
         */
        public String getHelp()
        {
            return helpString;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getInitValue()
         */
        public String getInitValue()
        {
            return null;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getName()
         */
        public String getName()
        {
            return name;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getSelectableValues()
         */
        public String[] getSelectableValues()
        {
            return null;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getToolTipText()
         */
        public String getToolTipText()
        {
            return null;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getValue()
         */
        public String getValue()
        {
            String val = macro.getDescription();
            if (val==null)
                return defaultDescription;
            else
                return val;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getVisibility()
         */
        public int getVisibility()
        {
            return InspectableProperty.UNDEFINED_VISIBILITY;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#isEditable()
         */
        public boolean isEditable()
        {
            return true;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#isSepatator()
         */
        public boolean isSepatator()
        {
            return false;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#isValid()
         */
        public boolean isValid()
        {
            return true;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#popupEvent(Component, int, int)
         */
        public void popupEvent(java.awt.Component component, int x, int y)
        {
            macro.popupEvent(component, x, y);
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#setValue(String)
         */
        public void setValue(String value)
        {
            macro.setDescription(value);
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#toString(String)
         */
        public String toString()
        {
            return name;
        }
}

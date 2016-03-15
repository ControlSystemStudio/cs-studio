/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.opibuilder.properties;

/**
 * Categories of widget properties.
 *
 * @author Sven Wende (similar class as in SDS),
 * @author Xihui Chen
 */
public interface WidgetPropertyCategory {

    /**
     * Image category.
     */
    public final static WidgetPropertyCategory Image = new WidgetPropertyCategory(){
        @Override
        public String toString() {
            return "Image";
        }
    };

    /**
     * Behavior category.
     */
    public final static WidgetPropertyCategory Behavior = new WidgetPropertyCategory(){
        @Override
        public String toString() {
            return "Behavior";
        }
    };

    /**
     * Display category.
     */
    public final static WidgetPropertyCategory Display = new WidgetPropertyCategory(){
        @Override
        public String toString() {
            return "Display";
        }
    };

    /**
     * Position category.
     */
    public final static WidgetPropertyCategory Position = new WidgetPropertyCategory(){
        @Override
        public String toString() {
            return "Position";
        }
    };


    /**
     * Misc category.
     */
    public final static WidgetPropertyCategory Misc = new WidgetPropertyCategory(){
        @Override
        public String toString() {
            return "Misc";
        }
    };

    /**
     * Border category.
     */
    public final static WidgetPropertyCategory Border = new WidgetPropertyCategory(){
        @Override
        public String toString() {
            return "Border";
        }
    };

    /**
     * Misc category.
     */
    public final static WidgetPropertyCategory Basic = new WidgetPropertyCategory(){
        @Override
        public String toString() {
            return "Basic";
        }
    };
}

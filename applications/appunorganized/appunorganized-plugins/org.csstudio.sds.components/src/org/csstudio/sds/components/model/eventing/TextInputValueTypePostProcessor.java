/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.model.eventing;

import static org.csstudio.sds.components.model.TextInputModel.PROP_INPUT_TEXT;
import static org.csstudio.sds.model.AbstractTextTypeWidgetModel.PROP_PRECISION;
import static org.csstudio.sds.model.AbstractTextTypeWidgetModel.PROP_TEXT_TYPE;

import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.model.TextTypeEnum;
import org.csstudio.sds.model.commands.HidePropertyCommand;
import org.csstudio.sds.model.commands.ShowPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 06.05.2010
 */
public class TextInputValueTypePostProcessor extends AbstractWidgetPropertyPostProcessor<TextInputModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command doCreateCommand(final TextInputModel tiWidget) {
        assert tiWidget != null : "widget != null";
        return new TextTypeStuffCommand(tiWidget);
    }

    private static final class TextTypeStuffCommand extends Command {
        private final TextInputModel widget;
        private CompoundCommand chain;

        private TextTypeStuffCommand(final TextInputModel widget) {
            this.widget = widget;
        }

        @Override
        public void execute() {
            if (chain == null) {
                chain = new CompoundCommand();

                // .. determine the selected border style
                int optionIndex = widget.getArrayOptionProperty(TextInputModel.PROP_TEXT_TYPE);

                if (TextTypeEnum.DOUBLE.getIndex() == optionIndex ) {
                    chain.add(new ShowPropertyCommand(widget, PROP_INPUT_TEXT, PROP_TEXT_TYPE));
                    chain.add(new ShowPropertyCommand(widget, PROP_PRECISION, PROP_TEXT_TYPE));
                } else if (TextTypeEnum.ALIAS.getIndex() == optionIndex ) {
                    chain.add(new HidePropertyCommand(widget, PROP_INPUT_TEXT, PROP_TEXT_TYPE));
                    chain.add(new HidePropertyCommand(widget, PROP_PRECISION, PROP_TEXT_TYPE));
                } else {
                    chain.add(new ShowPropertyCommand(widget, PROP_INPUT_TEXT, PROP_TEXT_TYPE));
                    chain.add(new HidePropertyCommand(widget, PROP_PRECISION, PROP_TEXT_TYPE));
                }
            }

            chain.execute();
        }

        @Override
        public void undo() {
            chain.undo();
        }
    }
}

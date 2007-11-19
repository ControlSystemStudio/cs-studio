/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.ams.messageminder;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 31.10.2007
 */
public class MessageKey {

    /**
     * A list with the fields they are use as key.
     */
    private String[] _keyWords;

    /**
     * 
     * @param keyWords A list with the fields they are use as key.
     */
    public MessageKey(final String[] keyWords) {
        _keyWords = keyWords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        int sign = 1;
        for (String keyWord : _keyWords) {
            hashCode+=(sign*keyWord.hashCode());
            sign*=-1;
        }
        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof MessageKey) {
            MessageKey mKey = (MessageKey) arg0;
            // Don't the same length they can't the same Key.
            if(mKey._keyWords.length!=_keyWords.length){
                return false;
            }
            for (int i = 0; i < _keyWords.length; i++) {
                if(!mKey._keyWords[i].equals(_keyWords[i])){
                    return false;
                }
            }
            return true;
        }
        return false;    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String outPut = "key words: ";
        for (String key : _keyWords) {
            outPut=outPut.concat(key+",");
        }
        return outPut.substring(0,outPut.length()-1);
    }
    
    
    

}

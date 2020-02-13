/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.platform.libs.jms;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 15.06.2007
 */
public class ActiveMQURL {
    
    
    /**
     * The Complett URL.
     */ 
    private String _url=null;
    /**
     * The Prefix of the URL.
     * For instance failover
     */
    private String _pre=null;
    
    /**
     * The Host Name.
     */
    private String _host=null;
    
    /**
     * Max Reconnect Attempts.
     * If not 0, then this is the maximum number of reconnect attempts before an error is sent back to the client.
     */
    private String _maxReconnectAttempts=null;
    
    /**
     * Initial Reconnect Delay.
     * How long to wait before the first reconnect attempt (in ms).
     */
    private String _initialReconnectDelay=null;
    
    /**
     * Max Reconnect Delay.
     * The maximum amount of time we ever wait between reconnect attempts (in ms).
     */
    private String _maxReconnectDelay=null;
    
    /**
     * Use Exponential BackOff.
     * Should an exponential backoff be used btween reconnect attempts.
     */
    private String _useExponentialBackOff=null;
    
    /**
     * Back Off Multiplier.
     * The exponent used in the exponential backoff attempts. 
     */
    private String _backOffMultiplier=null;
    
    /**
     * randomize.
     * use a random algorithm to choose the the URI to use for reconnect from the list provided;
     */
    private String _randomize=null;
    /**
     *  @param url The ActiveMQ URL. 
     */
    public ActiveMQURL(final String url) {
        _url=url;
        int ind;
        int endInd;
        if(_url.startsWith("failover")||_url.startsWith("fanout")){
            ind  =_url.indexOf(':');
            _pre = _url.substring(0,ind);
            ind++;
            if(_url.startsWith("(",ind)){
                ind++;
                endInd  = _url.indexOf(')',ind);
            }else{
                endInd = _url.indexOf('?',ind);
                if(endInd<0){
                    endInd=url.length();
                }
            }
        }else{
        	ind =0;
        	endInd = _url.indexOf('?');
        	if(endInd<0){
        		endInd=_url.length();
        	}
        }
                
        _host = _url.substring(ind,endInd);
        ind=endInd;
        while(endInd<_url.length()){
            if(_url.startsWith(")",ind)){
                ind++;
                endInd++;
            }
            if(_url.startsWith("?",ind)||_url.startsWith("&",ind)){
                ind++;
                endInd = _url.indexOf('&',ind);
                if(endInd<0){
                    endInd=url.length();
                } 
                String tmp= _url.substring(ind,endInd);
                if(tmp.startsWith("maxReconnectAttempts")){
                    _maxReconnectAttempts = new String(tmp);
                }else if(tmp.startsWith("backOffMultiplier")){
                    _backOffMultiplier = new String(tmp);
                }else if(tmp.startsWith("initialReconnectDelay")){
                    _initialReconnectDelay = new String(tmp);
                }else if(tmp.startsWith("maxReconnectDelay")){
                    _maxReconnectDelay = new String(tmp);
                }else if(tmp.startsWith("randomize")){
                    _randomize = new String(tmp);
                }else if(tmp.startsWith("useExponentialBackOff")){
                    _useExponentialBackOff = new String(tmp);
                }
            }
            
            ind = _url.indexOf('&',endInd);
        }
    }
    /**
     * @return backOffMultiplier
     */
    public final String getBackOffMultiplier() {
        return _backOffMultiplier;
    }
    /**
     * 
     * @param backOffMultiplier The exponent used in the exponential backoff attempts
     */
    public final void setBackOffMultiplier(final String backOffMultiplier) {
        _backOffMultiplier = backOffMultiplier;
    }
    /**
     * @return Host.
     */
    public final String getHost() {
        return _host;
    }
    /**
     * 
     * @param host The Host.
     */
    public final void setHost(final String host) {
        _host = host;
    }
    /**
     * 
     * @return initial Reconnect Delay
     */
    public final String getInitialReconnectDelay() {
        return _initialReconnectDelay;
    }
    /**
     * @param reconnectDelay maxReconnectDelay;
     */
    public final void setInitialReconnectDelay(final String reconnectDelay) {
        _initialReconnectDelay = reconnectDelay;
    }
    /**
     * @return max Reconnect Attempts
     */
    public final String getMaxReconnectAttempts() {
        return _maxReconnectAttempts;
    }
    /**
     * 
     * @param maxReconnectAttempts The maximum amount of time we ever wait between reconnect attempts (in ms).
     */
    public final void setMaxReconnectAttempts(final String maxReconnectAttempts) {
    	/** Soll unentlich weiter versucht werden eine Verbindung auf zu bauen muss derEintarg entfernt werden.
    	 * In der Doku steht zwar das die zuweisung mit dem Wert 0 das gleich bewirkt doch das funktioniert nicht. 
    	 */
//    	int count = new Integer(maxReconnectAttempts.substring(maxReconnectAttempts.indexOf('=')+1,maxReconnectAttempts.length()));
//    	if(count>0){
    		_maxReconnectAttempts = maxReconnectAttempts;
//    	}else{
//    		_maxReconnectAttempts=null;
//    	}
    		
    }
    /**
     * @return max Reconnect Delay.
     */
    public final String getMaxReconnectDelay() {
        return _maxReconnectDelay;
    }
    /**
     * 
     * @param reconnectDelay The maximum amount of time we ever wait between reconnect attempts (in ms)
     */
    public final void setmaxReconnectDelay(final String reconnectDelay) {
        _maxReconnectDelay = reconnectDelay;
    }
    /**
     * @return The Prefeix of the URL
     */
    public final String getPrefix() {
        return _pre;
    }
    /**
     * 
     * @param pre The Prefix of High Level Protocol URIs (ActiveMQ: Failover, Fanout,...)
     */
    public final void setPrefix(final String pre) {
        _pre = pre;
    }
    /**
     * @return the Randomize True or False as String.
     */
    public final String getRandomize() {
        return _randomize;
    }
    /**
     * @param randomize use a random algorithm to choose the the URI to use for reconnect from the list provided
     */
    public final void setRandomize(final String randomize) {
        _randomize = randomize;
    }
    /**
     * @return The compliet ActiveMQ URL.
     */
    public final String getURL() {
        if(_host==null){
            return null;
        }

        _url ="";
        if(_pre!=null){
            _url = _pre+":("+_host+")";    
        }else{
            _url=_host;
            return _url;
        }
        char c = '?'; 
        if(_maxReconnectAttempts!=null){
            _url=_url.concat(c+_maxReconnectAttempts);
            c='&';
        }
        if(_backOffMultiplier!=null){
            _url=_url.concat(c+_backOffMultiplier);
            c='&';
        }
        if(_initialReconnectDelay!=null){
            _url=_url.concat(c+_initialReconnectDelay);
            c='&';
        }
        if(_maxReconnectDelay!=null){
            _url=_url.concat(c+_maxReconnectDelay);
            c='&';
        }
        if(_randomize!=null){
            _url=_url.concat(c+_randomize);
            c='&';
        }
        if(_useExponentialBackOff!=null){
            _url=_url.concat(c+_useExponentialBackOff);
        }
        return _url;
    }
    /**
     * @param url ActiveMQ URL
     */
    public final void setURL(final String url) {
        _url = url;
    }
    /**
     * @return use Exponential Back Off true or False as String
     */
    public final String getUseExponentialBackOff() {
        return _useExponentialBackOff;
    }
    /**
     * 
     * @param useExponentialBackOff Should an exponential backoff be used btween reconnect attempts (String 'true' or 'false')
     */
    public final void setUseExponentialBackOff(final String useExponentialBackOff) {
        _useExponentialBackOff = useExponentialBackOff;
    }
    /**
     * @return Get the URL as String. 
     */
    @Override
    public final String toString (){
        return getURL();
    }
    
    

}

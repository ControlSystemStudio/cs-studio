
package org.csstudio.alarm.jms2ora;

public class XmppInfo {
    
    private String xmppServer;
    private String xmppUser;
    private String xmppPassword;
    
    public XmppInfo(String server, String user, String password) {
        this.xmppServer = server;
        this.xmppUser = user;
        this.xmppPassword = password;
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("XmppInfo {" + xmppServer + ",");
        buffer.append(xmppUser + ",");
        buffer.append(xmppPassword + "}");
        
        return buffer.toString();
    }
    
    public String getXmppServer() {
        return xmppServer;
    }
    
    public void setXmppServer(String server) {
        this.xmppServer = server;
    }
    
    public String getXmppUser() {
        return xmppUser;
    }
    
    public void setXmppUser(String user) {
        this.xmppUser = user;
    }
    
    public String getXmppPassword() {
        return xmppPassword;
    }
    
    public void setXmppPassword(String password) {
        this.xmppPassword = password;
    }
}

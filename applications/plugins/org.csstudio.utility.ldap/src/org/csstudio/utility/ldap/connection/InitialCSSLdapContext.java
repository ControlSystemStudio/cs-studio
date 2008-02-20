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
package org.csstudio.utility.ldap.connection;

import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;

import org.csstudio.platform.logging.CentralLogger;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 15.02.2008
 */
public class InitialCSSLdapContext extends InitialLdapContext {

    private static final long DEFAULT_TIME = 60;
    private static final long MAX_TIME = 900000;
    private long _time = DEFAULT_TIME;
    private Hashtable<?, ?> _environment;
    
    /**
     * @param arg0
     * @throws NamingException 
     * @throws NamingException
     */
    public InitialCSSLdapContext(Hashtable<?, ?> arg0,Control[] connCtls) throws NamingException {
        super(arg0,connCtls);
        InitialLdapContext test = new InitialLdapContext();
//        getConnectControls()[0]
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#bind(javax.naming.Name, java.lang.Object, javax.naming.directory.Attributes)
     */
    @Override
    public void bind(Name name, Object obj, Attributes attrs)
            throws NamingException {
                        try{
                            super.bind(name, obj, attrs);
                            _time=DEFAULT_TIME;    
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                
                                if(_time<MAX_TIME){_time=_time*10;}
                                super.bind(name, obj, attrs);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#bind(java.lang.String, java.lang.Object, javax.naming.directory.Attributes)
     */
    @Override
    public void bind(String name, Object obj, Attributes attrs)
            throws NamingException {
                        try{
                            super.bind(name, obj, attrs);
                            _time=DEFAULT_TIME;    
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                super.bind(name, obj, attrs);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#createSubcontext(javax.naming.Name, javax.naming.directory.Attributes)
     */
    @Override
    public DirContext createSubcontext(Name name, Attributes attrs)
            throws NamingException {
                        try{
                            DirContext dc = super.createSubcontext(name, attrs);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return createSubcontext(name, attrs);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#createSubcontext(java.lang.String, javax.naming.directory.Attributes)
     */
    @Override
    public DirContext createSubcontext(String name, Attributes attrs)
            throws NamingException {
                        try{
                            DirContext dc = super.createSubcontext(name, attrs);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return createSubcontext(name, attrs);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#getAttributes(javax.naming.Name, java.lang.String[])
     */
    @Override
    public Attributes getAttributes(Name name, String[] attrIds)
            throws NamingException {
                        try{
                            Attributes dc = super.getAttributes(name, attrIds);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                
                                if(_time<MAX_TIME){_time=_time*10;}
                                return getAttributes(name, attrIds);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#getAttributes(javax.naming.Name)
     */
    @Override
    public Attributes getAttributes(Name name) throws NamingException {
                        try{
                            Attributes dc = super.getAttributes(name);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return getAttributes(name);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#getAttributes(java.lang.String, java.lang.String[])
     */
    @Override
    public Attributes getAttributes(String name, String[] attrIds)
            throws NamingException {
                        try{
                            Attributes dc = super.getAttributes(name, attrIds);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                for (Control control : getConnectControls()) {
                                    System.out.println("test control: "+control.isCritical());
                                }
                                reconnect(null);

                                if(_time<MAX_TIME){_time=_time*10;}
                                return getAttributes(name, attrIds);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#getAttributes(java.lang.String)
     */
    @Override
    public Attributes getAttributes(String name) throws NamingException {
                        try{
                            Attributes dc = super.getAttributes(name);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                for (Control control : getConnectControls()) {
                                    System.out.println("test control: "+control.isCritical());
                                }
                                reconnect(null);

                                if(_time<MAX_TIME){_time=_time*10;}
                                return getAttributes(name);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#getSchema(javax.naming.Name)
     */
    @Override
    public DirContext getSchema(Name name) throws NamingException {
                        try{
                            DirContext dc = super.getSchema(name);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return getSchema(name);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#getSchema(java.lang.String)
     */
    @Override
    public DirContext getSchema(String name) throws NamingException {
                        try{
                            DirContext dc = super.getSchema(name);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return getSchema(name);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#getSchemaClassDefinition(javax.naming.Name)
     */
    @Override
    public DirContext getSchemaClassDefinition(Name name)
            throws NamingException {
                        try{
                            DirContext dc = super.getSchemaClassDefinition(name);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return getSchemaClassDefinition(name);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#getSchemaClassDefinition(java.lang.String)
     */
    @Override
    public DirContext getSchemaClassDefinition(String name)
            throws NamingException {
                        try{
                            DirContext dc = super.getSchemaClassDefinition(name);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return getSchemaClassDefinition(name);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#modifyAttributes(javax.naming.Name, int, javax.naming.directory.Attributes)
     */
    @Override
    public void modifyAttributes(Name name, int mod_op, Attributes attrs)
            throws NamingException {
                        try{
                            super.modifyAttributes(name, mod_op, attrs);
                            _time=DEFAULT_TIME;    
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                super.modifyAttributes(name, mod_op, attrs);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#modifyAttributes(javax.naming.Name, javax.naming.directory.ModificationItem[])
     */
    @Override
    public void modifyAttributes(Name name, ModificationItem[] mods)
            throws NamingException {
                        try{
                            super.modifyAttributes(name, mods);
                            _time=DEFAULT_TIME;    
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                super.modifyAttributes(name, mods);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#modifyAttributes(java.lang.String, int, javax.naming.directory.Attributes)
     */
    @Override
    public void modifyAttributes(String name, int mod_op, Attributes attrs)
            throws NamingException {
                        try{
                            super.modifyAttributes(name, mod_op, attrs);
                            _time=DEFAULT_TIME;    
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                super.modifyAttributes(name, mod_op, attrs);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#modifyAttributes(java.lang.String, javax.naming.directory.ModificationItem[])
     */
    @Override
    public void modifyAttributes(String name, ModificationItem[] mods)
            throws NamingException {
                        try{
                            super.modifyAttributes(name, mods);
                            _time=DEFAULT_TIME;    
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                super.modifyAttributes(name, mods);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#rebind(javax.naming.Name, java.lang.Object, javax.naming.directory.Attributes)
     */
    @Override
    public void rebind(Name name, Object obj, Attributes attrs)
            throws NamingException {
                        try{
                            super.rebind(name, obj, attrs);
                            _time=DEFAULT_TIME;    
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                super.rebind(name, obj, attrs);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#rebind(java.lang.String, java.lang.Object, javax.naming.directory.Attributes)
     */
    @Override
    public void rebind(String name, Object obj, Attributes attrs)
            throws NamingException {
                        try{
                            super.rebind(name, obj, attrs);
                            _time=DEFAULT_TIME;    
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                super.rebind(name, obj, attrs);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#search(javax.naming.Name, javax.naming.directory.Attributes, java.lang.String[])
     */
    @Override
    public NamingEnumeration<SearchResult> search(Name name,
            Attributes matchingAttributes, String[] attributesToReturn)
            throws NamingException {
                        try{
                            NamingEnumeration<SearchResult> dc = super.search(name, matchingAttributes, attributesToReturn);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return search(name, matchingAttributes, attributesToReturn);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        System.out.println("return null");return null;
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#search(javax.naming.Name, javax.naming.directory.Attributes)
     */
    @Override
    public NamingEnumeration<SearchResult> search(Name name,
            Attributes matchingAttributes) throws NamingException {
                        try{
                            NamingEnumeration<SearchResult> dc = super.search(name, matchingAttributes);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return search(name, matchingAttributes);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#search(javax.naming.Name, java.lang.String, java.lang.Object[], javax.naming.directory.SearchControls)
     */
    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filterExpr,
            Object[] filterArgs, SearchControls cons) throws NamingException {
                        try{
                            NamingEnumeration<SearchResult> dc = super.search(name, filterExpr, filterArgs, cons);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return search(name, filterExpr, filterArgs, cons);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#search(javax.naming.Name, java.lang.String, javax.naming.directory.SearchControls)
     */
    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filter,
            SearchControls cons) throws NamingException {
                        try{
                            NamingEnumeration<SearchResult> dc = super.search(name, filter, cons);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return search(name, filter, cons);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#search(java.lang.String, javax.naming.directory.Attributes, java.lang.String[])
     */
    @Override
    public NamingEnumeration<SearchResult> search(String name,
            Attributes matchingAttributes, String[] attributesToReturn)
            throws NamingException {
                        try{
                            NamingEnumeration<SearchResult> dc = super.search(name, matchingAttributes, attributesToReturn);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return search(name, matchingAttributes, attributesToReturn);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#search(java.lang.String, javax.naming.directory.Attributes)
     */
    @Override
    public NamingEnumeration<SearchResult> search(String name,
            Attributes matchingAttributes) throws NamingException {
                        try{
                            NamingEnumeration<SearchResult> dc = super.search(name, matchingAttributes);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return search(name, matchingAttributes);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#search(java.lang.String, java.lang.String, java.lang.Object[], javax.naming.directory.SearchControls)
     */
    @Override
    public NamingEnumeration<SearchResult> search(String name,
            String filterExpr, Object[] filterArgs, SearchControls cons)
            throws NamingException {
                        try{
                            NamingEnumeration<SearchResult> dc = super.search(name, filterExpr, filterArgs, cons);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
                                if(_time<MAX_TIME){_time=_time*10;}
                                return search(name, filterExpr, filterArgs, cons);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }

    /* (non-Javadoc)
     * @see javax.naming.directory.InitialDirContext#search(java.lang.String, java.lang.String, javax.naming.directory.SearchControls)
     */
    @Override
    public NamingEnumeration<SearchResult> search(String name, String filter,
            SearchControls cons) throws NamingException {
                        try{
                            NamingEnumeration<SearchResult> dc = super.search(name, filter, cons);
                            _time=DEFAULT_TIME;    
                        	return dc;
                        }catch(CommunicationException ce){
                        	System.out.println("Test CommunicationExeption ("+_time+")");
                        	try {
                               //Thread.sleep(_time); init(_environment);
//                        	    for (Control control : getConnectControls()) {
//                                    System.out.println("test control: "+control.isCritical());
//                                }
                        	    System.out.println("befor reconnect");
                        	    try{
                        	        reconnect(null);
                        	    }catch (Exception e){
                        	        CentralLogger.getInstance().debug(this, e);
                        	    }
                        	    System.out.println("after reconnect");
                                if(_time<MAX_TIME){_time=_time*10;}
                                return search(name, filter, cons);
                            } catch (Exception e) {//catch (InterruptedException e) {System.out.println("Test IntrExc");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                		
                        System.out.println("return null");return null;
        
    }
        
}

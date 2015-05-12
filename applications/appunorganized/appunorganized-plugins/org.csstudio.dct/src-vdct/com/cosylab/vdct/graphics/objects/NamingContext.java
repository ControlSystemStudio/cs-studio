package com.cosylab.vdct.graphics.objects;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.Settings;
import com.cosylab.vdct.util.StringUtils;
import com.cosylab.vdct.vdb.LinkProperties;
import com.cosylab.vdct.vdb.VDBPort;
import com.cosylab.vdct.vdb.VDBTemplate;
import com.cosylab.vdct.vdb.VDBTemplateInstance;

/**
 * @author ilist
 */
public class NamingContext {

    NamingContext parent;
    VDBTemplateInstance templateInstance;
    VDBTemplate template;
    Map map = new Hashtable();
    Map macroMap = new Hashtable();
    Map portMap = new Hashtable();
    String addedPrefix;
    String removedPrefix;
    boolean export;

    /**
     * Used by createNamingContextFor
     * @param parent
     * @param templateInstance
     */
    private NamingContext(NamingContext parent, VDBTemplateInstance templateInstance, String addedPrefix, String removedPrefix, boolean export) {
        this.parent=parent;
        this.templateInstance=templateInstance;
        this.template=templateInstance.getTemplate();
        this.addedPrefix=addedPrefix;
        this.export=export;
    }

    /**
     * This constructor should be used for the topmost template, which doesn't have
     * template instance
     * @param parent
     * @param template
     */
    public NamingContext(NamingContext parent, VDBTemplate template, String addedPrefix, String removedPrefix, boolean export) {
        this.parent=parent;
        this.template=template;
        this.addedPrefix=addedPrefix;
        this.removedPrefix=removedPrefix;
        this.export=export;
    }

    /**
     * Returns parent, which can be null.
     * @return
     */
    public NamingContext getParent() {
        return parent;
    }

    /**
     * Returns template instance, which can be null
     */
    public VDBTemplateInstance getTemplateInstance() {
        return templateInstance;
    }

    /**
     * Returns template
     * @return
     */
    public VDBTemplate getTemplate() {
        return template;
    }

    /**
     * Returns full map
     * @return
     */
    public Map getMap() {
        return map;
    }

    /**
     * Returns only mapping of macros
     * @return
     */
    public Map getMacroMap() {
        return macroMap;
    }

    /**
     * Recursively resolves macros/ports
     * @param name
     * @param value
     * @return
     */
    public String resolveMacro(String name, String value) {
        //DebugSystem.out.println("resolve macro "+name+"="+value);

        // only macros/ports on previous level can have influence
        if (getParent()!=null) {
            /* try with a record first
             * if there are some macros in the target, we suppose the same macros
              * are in the record name, therefore they are substituted later
             */
            value = getParent().resolveLink(value);
            value = getParent().matchAndReplace(value);
        }

        return value;
    }

    Map namingContextCache=new Hashtable();

    /**
     * Creates or returns NamingContext for specific template
     * @param instance
     * @return
     */
    public NamingContext createNamingContextFor(VDBTemplateInstance instance) {
        //DebugSystem.out.println("create record namer for " +instance);

        NamingContext rn=(NamingContext)namingContextCache.get(instance);
        if (rn==null) {
            //handle added and removed prefixes
            String addedPrefix=this.addedPrefix;
            String removedPrefix=this.removedPrefix;
            if (Settings.getInstance().getHierarhicalNames() && export) {
                addedPrefix=addedPrefix+instance.getName()+Constants.HIERARCHY_SEPARATOR;
            }

            rn=new NamingContext(this, instance, addedPrefix, removedPrefix, export);
            namingContextCache.put(instance, rn);
        }
        return rn;
    }

    /**
     * Recursively resolves macros/ports
     * @param port
     * @return
     */
    public String resolvePort(VDBPort port) {
        //DebugSystem.out.println("resolve port "+port);

        String target = port.getTarget();

        /* try with a record first
         * if there are some macros in the target, we suppose the same macros
         * are in the record name, therefore they are substituted later
         */

        LinkProperties lp = new LinkProperties(port);
        Object rec = template.getGroup().findObject(lp.getRecord(), true);
        if (rec !=null && rec instanceof Record) {
            /*lp.setRecord(getResolvedName(((Record)rec).getRecordData()));
            target = lp.getFull();*/
            target=getResolvedName(target);
        }

        target = matchAndReplace(target);

        return target;
    }

    /**
     * Renames the record apropriately.
     * When exporting to Capfast hierarchy.
     * When exporting single Group.
     * @param data
     * @return
     */
/*    public String getResolvedName(VDBRecordData data) {
        System.out.println("get res name "+data);

        return data.getName();
    }*/

    /**
     * Used in write VDCT data
     * Should handle exporting single Group
     * @param name
     * @return
     */
    public String getResolvedName(String name) {
        if (removedPrefix!=null)
            name = StringUtils.removeBegining(name, removedPrefix);
        if (addedPrefix!=null)
            name = addedPrefix + name;
        return name;
    }

    /**
     * Adds a port to this naming context
     * @param string
     * @param value
     */
    public void addPort(String name, String value) {
        //DebugSystem.out.println("add port "+name+"="+value);
        portMap.put(name, value);
        map.put(name, value);
    }

    /**
     * Adds a macro to this naming context
     * @param name
     * @param value
     */
    public void addMacro(String name, String value) {
        //DebugSystem.out.println("add macro "+name+"="+value);
        macroMap.put(name, value);
        map.put(name,value);
    }

    /**
     * Subtracts record name from link, looks for apropriate record and renames
     * the record in apropriate way. It contructs back the link.
     * @param target
     * @return
     */
    public String resolveLink(String target) {
        //DebugSystem.out.println("resolve link "+target);
        //TODO

        String record=LinkProperties.getRecordFromString(target);
        Object rec=null;

        if (record!=null && template !=null) rec=template.getGroup().findObject(record, true);
        if (rec !=null && rec instanceof Record) {
            target=getResolvedName(target);
        }

        return target;
    }

    public final static Object cycleFlag=new Object();

    /**
     * Searches for the macro in the specific template.
     * Possibly also for global macro definitions.
     * @param name
     * @return
     */
    public String findAndResolveMacro(String name) {
        //DebugSystem.out.println("find and resolve macro "+name);

        String value = "$("+name+")";

        if (macroMap.containsKey(name)) {
            if (macroMap.get(name) == cycleFlag) {
                Console.getInstance().println("Warning: cyclic reference made by macro '"+value+"'.");

                addMacro(name, value);
            }
            return (String)macroMap.get(name);
        }

        macroMap.put(name, cycleFlag); // flag visited

        if (templateInstance!=null) {
            String val = (String)templateInstance.getProperties().get(name);
            if (val!=null) value = resolveMacro(name, val);
            else {
                if (Settings.getInstance().getGlobalMacros() && getParent()!=null)
                            value = getParent().findAndResolveMacro(name);
            }
        }



        addMacro(name, value);

        return value;
    }

    /**
     * Searches for port in the specific template.
     * Notice that port has to be resolved in inner template, but added in outer one.
     * @param temp
     * @param name
     * @return
     */
    public String findAndResolvePort(String temp, String name) {
        //DebugSystem.out.println("find and resolve port: "+temp+"."+name);

        String portname=temp+"."+name;
        String target="$("+portname+")";

        if (portMap.containsKey(portname)) {
            if (portMap.get(portname) == cycleFlag) {
                Console.getInstance().println("Warning: cyclic reference made by port '"+portname+"'.");
                addPort(portname, target);
            }
            return (String)portMap.get(portname);
        }

        portMap.put(portname, cycleFlag); // flag visited



        Object obj=template.getGroup().findObject(temp, true);
        if (obj!=null && obj instanceof Template) {
            Template t=(Template)obj;
            NamingContext newNamer = createNamingContextFor(t.getTemplateData());

            VDBPort port=(VDBPort)t.getTemplateData().getTemplate().getPorts().get(name);
            if (port!=null) target=newNamer.resolvePort(port);
        }

        addPort(portname, target);

        return target;
    }

    /**
     * Finds $() and $(.) in strings and replaces them.
     * TODO currently it doesn't handle something like $($()) very well.
     * I don't think this is a simple problem, which could be done with regex.
     * Hierarchy prevents it.
     * @param value
     * @return
     */
    public String matchAndReplace(String value) {
        if (value==null || value.indexOf('$')<0) return value;

        //DebugSystem.out.println("matchandreplace "+value);

        // by definition from ebnfg - \0 is bad character, but occurs in groups
        //Pattern macrop = Pattern.compile("$\\(([a-zA-Z0-9_:-]+)\\)");
        //Pattern portp = Pattern.compile("$\\(([a-zA-Z0-9_:-]+)\\.([a-zA-Z0-9_:-]+)\\)");
        Pattern macrop = Pattern.compile("\\$\\(([^\\.\\$\\)]+)\\)");
        //Pattern portp = Pattern.compile("\\$\\(([^\\.\\$]+)\\.([^\\.\\$]+)\\)");
        Pattern portp = Pattern.compile("\\$\\(([^\\.\\)]+)\\.([^\\.\\)]+)\\)");

        Matcher port = portp.matcher(value);
        StringBuffer result = new StringBuffer();
        while (port.find()) {
            String portt=port.group(1), portn=port.group(2);
            port.appendReplacement(result, findAndResolvePort(portt, portn).replaceAll("\\$","\\\\\\$"));
        }
        port.appendTail(result);

        Matcher macro = macrop.matcher(result.toString());
        result=new StringBuffer();
        while (macro.find()) {
            String macron=macro.group(1);
            macro.appendReplacement(result, findAndResolveMacro(macron).replaceAll("\\$","\\\\\\$"));
        }
        macro.appendTail(result);

        return result.toString();
    }

}

This is the xmlrpc-2.0.1 source from the Apache web site with
the following changes:

org.apache.xmlrpc.DefaultTypeFactory:

    public Object createDouble(String cdata)
    {
        // KUK: Parse 'nan' into Double.NaN
        String trim = cdata.trim ();
        if (trim.equalsIgnoreCase("nan"))
            return Double.NaN;
        return new Double(cdata.trim ());
    }
    
Built like this:

ant
cp target/xmlrpc-2.1-dev.jar ../xmlrpc-2.0.1.jar
    

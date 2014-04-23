/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
// $ANTLR 3.4 cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g 2013-10-04 12:02:59
package org.csstudio.utility.dbdparser.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class DbdFileParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "BREAKTABLE", "CHOICE", "COMMENT", "C_declaration", "DEVICE", "DIGIT", "DRIVER", "ENG", "ESC_SEQ", "EXPONENT", "FIELD", "FLOAT", "FUNCTION", "HEX_DIGIT", "INCLUDE", "KEY", "MENU", "NAME", "OCTAL_ESC", "PATH", "RAW", "RECORDTYPE", "REGISTRAR", "RULE", "STRING", "String", "TYPE", "UNICODE_ESC", "UnquotedString", "VALUE", "VARIABLE", "WHITESPACE", "'('", "')'", "','", "'addpath'", "'breaktable'", "'choice'", "'device'", "'driver'", "'field'", "'function'", "'include'", "'menu'", "'path'", "'recordtype'", "'registrar'", "'variable'", "'{'", "'}'"
    };

    public static final int EOF=-1;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__50=50;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int BREAKTABLE=4;
    public static final int CHOICE=5;
    public static final int COMMENT=6;
    public static final int C_declaration=7;
    public static final int DEVICE=8;
    public static final int DIGIT=9;
    public static final int DRIVER=10;
    public static final int ENG=11;
    public static final int ESC_SEQ=12;
    public static final int EXPONENT=13;
    public static final int FIELD=14;
    public static final int FLOAT=15;
    public static final int FUNCTION=16;
    public static final int HEX_DIGIT=17;
    public static final int INCLUDE=18;
    public static final int KEY=19;
    public static final int MENU=20;
    public static final int NAME=21;
    public static final int OCTAL_ESC=22;
    public static final int PATH=23;
    public static final int RAW=24;
    public static final int RECORDTYPE=25;
    public static final int REGISTRAR=26;
    public static final int RULE=27;
    public static final int STRING=28;
    public static final int String=29;
    public static final int TYPE=30;
    public static final int UNICODE_ESC=31;
    public static final int UnquotedString=32;
    public static final int VALUE=33;
    public static final int VARIABLE=34;
    public static final int WHITESPACE=35;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public DbdFileParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public DbdFileParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return DbdFileParser.tokenNames; }
    public String getGrammarFileName() { return "cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g"; }


    public static class top_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "top"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:40:1: top : program ;
    public final DbdFileParser.top_return top() throws RecognitionException {
        DbdFileParser.top_return retval = new DbdFileParser.top_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.program_return program1 =null;



        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:40:5: ( program )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:40:7: program
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_program_in_top234);
            program1=program();

            state._fsp--;

            adaptor.addChild(root_0, program1.getTree());

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "top"


    public static class program_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:1: program : ( pathdef | include | menu | recordtype | device | driver | registrar | variable | function | breaktable )* ;
    public final DbdFileParser.program_return program() throws RecognitionException {
        DbdFileParser.program_return retval = new DbdFileParser.program_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.pathdef_return pathdef2 =null;

        DbdFileParser.include_return include3 =null;

        DbdFileParser.menu_return menu4 =null;

        DbdFileParser.recordtype_return recordtype5 =null;

        DbdFileParser.device_return device6 =null;

        DbdFileParser.driver_return driver7 =null;

        DbdFileParser.registrar_return registrar8 =null;

        DbdFileParser.variable_return variable9 =null;

        DbdFileParser.function_return function10 =null;

        DbdFileParser.breaktable_return breaktable11 =null;



        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:9: ( ( pathdef | include | menu | recordtype | device | driver | registrar | variable | function | breaktable )* )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:11: ( pathdef | include | menu | recordtype | device | driver | registrar | variable | function | breaktable )*
            {
            root_0 = (Object)adaptor.nil();


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:11: ( pathdef | include | menu | recordtype | device | driver | registrar | variable | function | breaktable )*
            loop1:
            do {
                int alt1=11;
                switch ( input.LA(1) ) {
                case 39:
                case 48:
                    {
                    alt1=1;
                    }
                    break;
                case 46:
                    {
                    alt1=2;
                    }
                    break;
                case 47:
                    {
                    alt1=3;
                    }
                    break;
                case 49:
                    {
                    alt1=4;
                    }
                    break;
                case 42:
                    {
                    alt1=5;
                    }
                    break;
                case 43:
                    {
                    alt1=6;
                    }
                    break;
                case 50:
                    {
                    alt1=7;
                    }
                    break;
                case 51:
                    {
                    alt1=8;
                    }
                    break;
                case 45:
                    {
                    alt1=9;
                    }
                    break;
                case 40:
                    {
                    alt1=10;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:13: pathdef
            	    {
            	    pushFollow(FOLLOW_pathdef_in_program245);
            	    pathdef2=pathdef();

            	    state._fsp--;

            	    adaptor.addChild(root_0, pathdef2.getTree());

            	    }
            	    break;
            	case 2 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:23: include
            	    {
            	    pushFollow(FOLLOW_include_in_program249);
            	    include3=include();

            	    state._fsp--;

            	    adaptor.addChild(root_0, include3.getTree());

            	    }
            	    break;
            	case 3 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:33: menu
            	    {
            	    pushFollow(FOLLOW_menu_in_program253);
            	    menu4=menu();

            	    state._fsp--;

            	    adaptor.addChild(root_0, menu4.getTree());

            	    }
            	    break;
            	case 4 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:40: recordtype
            	    {
            	    pushFollow(FOLLOW_recordtype_in_program257);
            	    recordtype5=recordtype();

            	    state._fsp--;

            	    adaptor.addChild(root_0, recordtype5.getTree());

            	    }
            	    break;
            	case 5 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:53: device
            	    {
            	    pushFollow(FOLLOW_device_in_program261);
            	    device6=device();

            	    state._fsp--;

            	    adaptor.addChild(root_0, device6.getTree());

            	    }
            	    break;
            	case 6 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:62: driver
            	    {
            	    pushFollow(FOLLOW_driver_in_program265);
            	    driver7=driver();

            	    state._fsp--;

            	    adaptor.addChild(root_0, driver7.getTree());

            	    }
            	    break;
            	case 7 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:71: registrar
            	    {
            	    pushFollow(FOLLOW_registrar_in_program269);
            	    registrar8=registrar();

            	    state._fsp--;

            	    adaptor.addChild(root_0, registrar8.getTree());

            	    }
            	    break;
            	case 8 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:83: variable
            	    {
            	    pushFollow(FOLLOW_variable_in_program273);
            	    variable9=variable();

            	    state._fsp--;

            	    adaptor.addChild(root_0, variable9.getTree());

            	    }
            	    break;
            	case 9 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:94: function
            	    {
            	    pushFollow(FOLLOW_function_in_program277);
            	    function10=function();

            	    state._fsp--;

            	    adaptor.addChild(root_0, function10.getTree());

            	    }
            	    break;
            	case 10 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:105: breaktable
            	    {
            	    pushFollow(FOLLOW_breaktable_in_program281);
            	    breaktable11=breaktable();

            	    state._fsp--;

            	    adaptor.addChild(root_0, breaktable11.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "program"


    public static class pathdef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "pathdef"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:1: pathdef : ( 'path' | 'addpath' ) path -> ^( PATH path ) ;
    public final DbdFileParser.pathdef_return pathdef() throws RecognitionException {
        DbdFileParser.pathdef_return retval = new DbdFileParser.pathdef_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal12=null;
        Token string_literal13=null;
        DbdFileParser.path_return path14 =null;


        Object string_literal12_tree=null;
        Object string_literal13_tree=null;
        RewriteRuleTokenStream stream_48=new RewriteRuleTokenStream(adaptor,"token 48");
        RewriteRuleTokenStream stream_39=new RewriteRuleTokenStream(adaptor,"token 39");
        RewriteRuleSubtreeStream stream_path=new RewriteRuleSubtreeStream(adaptor,"rule path");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:9: ( ( 'path' | 'addpath' ) path -> ^( PATH path ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:11: ( 'path' | 'addpath' ) path
            {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:11: ( 'path' | 'addpath' )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==48) ) {
                alt2=1;
            }
            else if ( (LA2_0==39) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }
            switch (alt2) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:13: 'path'
                    {
                    string_literal12=(Token)match(input,48,FOLLOW_48_in_pathdef295);  
                    stream_48.add(string_literal12);


                    }
                    break;
                case 2 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:22: 'addpath'
                    {
                    string_literal13=(Token)match(input,39,FOLLOW_39_in_pathdef299);  
                    stream_39.add(string_literal13);


                    }
                    break;

            }


            pushFollow(FOLLOW_path_in_pathdef303);
            path14=path();

            state._fsp--;

            stream_path.add(path14.getTree());

            // AST REWRITE
            // elements: path
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 44:39: -> ^( PATH path )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:42: ^( PATH path )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(PATH, "PATH")
                , root_1);

                adaptor.addChild(root_1, stream_path.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "pathdef"


    public static class include_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "include"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:46:1: include : 'include' filename -> ^( INCLUDE filename ) ;
    public final DbdFileParser.include_return include() throws RecognitionException {
        DbdFileParser.include_return retval = new DbdFileParser.include_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal15=null;
        DbdFileParser.filename_return filename16 =null;


        Object string_literal15_tree=null;
        RewriteRuleTokenStream stream_46=new RewriteRuleTokenStream(adaptor,"token 46");
        RewriteRuleSubtreeStream stream_filename=new RewriteRuleSubtreeStream(adaptor,"rule filename");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:46:9: ( 'include' filename -> ^( INCLUDE filename ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:46:11: 'include' filename
            {
            string_literal15=(Token)match(input,46,FOLLOW_46_in_include320);  
            stream_46.add(string_literal15);


            pushFollow(FOLLOW_filename_in_include322);
            filename16=filename();

            state._fsp--;

            stream_filename.add(filename16.getTree());

            // AST REWRITE
            // elements: filename
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 46:30: -> ^( INCLUDE filename )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:46:33: ^( INCLUDE filename )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(INCLUDE, "INCLUDE")
                , root_1);

                adaptor.addChild(root_1, stream_filename.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "include"


    public static class path_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "path"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:48:1: path : String ;
    public final DbdFileParser.path_return path() throws RecognitionException {
        DbdFileParser.path_return retval = new DbdFileParser.path_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token String17=null;

        Object String17_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:48:6: ( String )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:48:8: String
            {
            root_0 = (Object)adaptor.nil();


            String17=(Token)match(input,String,FOLLOW_String_in_path339); 
            String17_tree = 
            (Object)adaptor.create(String17)
            ;
            adaptor.addChild(root_0, String17_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "path"


    public static class filename_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "filename"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:50:1: filename : String ;
    public final DbdFileParser.filename_return filename() throws RecognitionException {
        DbdFileParser.filename_return retval = new DbdFileParser.filename_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token String18=null;

        Object String18_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:50:10: ( String )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:50:12: String
            {
            root_0 = (Object)adaptor.nil();


            String18=(Token)match(input,String,FOLLOW_String_in_filename348); 
            String18_tree = 
            (Object)adaptor.create(String18)
            ;
            adaptor.addChild(root_0, String18_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "filename"


    public static class menu_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "menu"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:52:1: menu : menu_head menu_block -> ^( MENU ^( NAME menu_head ) menu_block ) ;
    public final DbdFileParser.menu_return menu() throws RecognitionException {
        DbdFileParser.menu_return retval = new DbdFileParser.menu_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.menu_head_return menu_head19 =null;

        DbdFileParser.menu_block_return menu_block20 =null;


        RewriteRuleSubtreeStream stream_menu_head=new RewriteRuleSubtreeStream(adaptor,"rule menu_head");
        RewriteRuleSubtreeStream stream_menu_block=new RewriteRuleSubtreeStream(adaptor,"rule menu_block");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:52:6: ( menu_head menu_block -> ^( MENU ^( NAME menu_head ) menu_block ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:52:8: menu_head menu_block
            {
            pushFollow(FOLLOW_menu_head_in_menu357);
            menu_head19=menu_head();

            state._fsp--;

            stream_menu_head.add(menu_head19.getTree());

            pushFollow(FOLLOW_menu_block_in_menu359);
            menu_block20=menu_block();

            state._fsp--;

            stream_menu_block.add(menu_block20.getTree());

            // AST REWRITE
            // elements: menu_block, menu_head
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 52:29: -> ^( MENU ^( NAME menu_head ) menu_block )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:52:32: ^( MENU ^( NAME menu_head ) menu_block )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(MENU, "MENU")
                , root_1);

                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:52:39: ^( NAME menu_head )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(NAME, "NAME")
                , root_2);

                adaptor.addChild(root_2, stream_menu_head.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_1, stream_menu_block.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "menu"


    public static class menu_head_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "menu_head"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:54:1: menu_head : 'menu' '(' name ')' -> name ;
    public final DbdFileParser.menu_head_return menu_head() throws RecognitionException {
        DbdFileParser.menu_head_return retval = new DbdFileParser.menu_head_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal21=null;
        Token char_literal22=null;
        Token char_literal24=null;
        DbdFileParser.name_return name23 =null;


        Object string_literal21_tree=null;
        Object char_literal22_tree=null;
        Object char_literal24_tree=null;
        RewriteRuleTokenStream stream_47=new RewriteRuleTokenStream(adaptor,"token 47");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:54:11: ( 'menu' '(' name ')' -> name )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:54:13: 'menu' '(' name ')'
            {
            string_literal21=(Token)match(input,47,FOLLOW_47_in_menu_head382);  
            stream_47.add(string_literal21);


            char_literal22=(Token)match(input,36,FOLLOW_36_in_menu_head384);  
            stream_36.add(char_literal22);


            pushFollow(FOLLOW_name_in_menu_head386);
            name23=name();

            state._fsp--;

            stream_name.add(name23.getTree());

            char_literal24=(Token)match(input,37,FOLLOW_37_in_menu_head388);  
            stream_37.add(char_literal24);


            // AST REWRITE
            // elements: name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 54:33: -> name
            {
                adaptor.addChild(root_0, stream_name.nextTree());

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "menu_head"


    public static class menu_block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "menu_block"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:56:1: menu_block : '{' ( menu_body )* '}' -> ( menu_body )* ;
    public final DbdFileParser.menu_block_return menu_block() throws RecognitionException {
        DbdFileParser.menu_block_return retval = new DbdFileParser.menu_block_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal25=null;
        Token char_literal27=null;
        DbdFileParser.menu_body_return menu_body26 =null;


        Object char_literal25_tree=null;
        Object char_literal27_tree=null;
        RewriteRuleTokenStream stream_52=new RewriteRuleTokenStream(adaptor,"token 52");
        RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
        RewriteRuleSubtreeStream stream_menu_body=new RewriteRuleSubtreeStream(adaptor,"rule menu_body");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:56:12: ( '{' ( menu_body )* '}' -> ( menu_body )* )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:56:14: '{' ( menu_body )* '}'
            {
            char_literal25=(Token)match(input,52,FOLLOW_52_in_menu_block401);  
            stream_52.add(char_literal25);


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:56:18: ( menu_body )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==41||LA3_0==46) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:56:18: menu_body
            	    {
            	    pushFollow(FOLLOW_menu_body_in_menu_block403);
            	    menu_body26=menu_body();

            	    state._fsp--;

            	    stream_menu_body.add(menu_body26.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            char_literal27=(Token)match(input,53,FOLLOW_53_in_menu_block406);  
            stream_53.add(char_literal27);


            // AST REWRITE
            // elements: menu_body
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 56:33: -> ( menu_body )*
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:56:36: ( menu_body )*
                while ( stream_menu_body.hasNext() ) {
                    adaptor.addChild(root_0, stream_menu_body.nextTree());

                }
                stream_menu_body.reset();

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "menu_block"


    public static class menu_body_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "menu_body"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:58:1: menu_body : ( choice | include );
    public final DbdFileParser.menu_body_return menu_body() throws RecognitionException {
        DbdFileParser.menu_body_return retval = new DbdFileParser.menu_body_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.choice_return choice28 =null;

        DbdFileParser.include_return include29 =null;



        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:58:11: ( choice | include )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==41) ) {
                alt4=1;
            }
            else if ( (LA4_0==46) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:58:13: choice
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_choice_in_menu_body420);
                    choice28=choice();

                    state._fsp--;

                    adaptor.addChild(root_0, choice28.getTree());

                    }
                    break;
                case 2 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:58:22: include
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_include_in_menu_body424);
                    include29=include();

                    state._fsp--;

                    adaptor.addChild(root_0, include29.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "menu_body"


    public static class choice_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "choice"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:60:1: choice : 'choice' '(' key_value ')' -> ^( CHOICE key_value ) ;
    public final DbdFileParser.choice_return choice() throws RecognitionException {
        DbdFileParser.choice_return retval = new DbdFileParser.choice_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal30=null;
        Token char_literal31=null;
        Token char_literal33=null;
        DbdFileParser.key_value_return key_value32 =null;


        Object string_literal30_tree=null;
        Object char_literal31_tree=null;
        Object char_literal33_tree=null;
        RewriteRuleTokenStream stream_41=new RewriteRuleTokenStream(adaptor,"token 41");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_key_value=new RewriteRuleSubtreeStream(adaptor,"rule key_value");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:60:8: ( 'choice' '(' key_value ')' -> ^( CHOICE key_value ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:60:10: 'choice' '(' key_value ')'
            {
            string_literal30=(Token)match(input,41,FOLLOW_41_in_choice433);  
            stream_41.add(string_literal30);


            char_literal31=(Token)match(input,36,FOLLOW_36_in_choice435);  
            stream_36.add(char_literal31);


            pushFollow(FOLLOW_key_value_in_choice437);
            key_value32=key_value();

            state._fsp--;

            stream_key_value.add(key_value32.getTree());

            char_literal33=(Token)match(input,37,FOLLOW_37_in_choice439);  
            stream_37.add(char_literal33);


            // AST REWRITE
            // elements: key_value
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 60:37: -> ^( CHOICE key_value )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:60:40: ^( CHOICE key_value )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(CHOICE, "CHOICE")
                , root_1);

                adaptor.addChild(root_1, stream_key_value.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "choice"


    public static class recordtype_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recordtype"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:62:1: recordtype : recordtype_head recordtype_block -> ^( RECORDTYPE recordtype_head recordtype_block ) ;
    public final DbdFileParser.recordtype_return recordtype() throws RecognitionException {
        DbdFileParser.recordtype_return retval = new DbdFileParser.recordtype_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.recordtype_head_return recordtype_head34 =null;

        DbdFileParser.recordtype_block_return recordtype_block35 =null;


        RewriteRuleSubtreeStream stream_recordtype_head=new RewriteRuleSubtreeStream(adaptor,"rule recordtype_head");
        RewriteRuleSubtreeStream stream_recordtype_block=new RewriteRuleSubtreeStream(adaptor,"rule recordtype_block");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:62:12: ( recordtype_head recordtype_block -> ^( RECORDTYPE recordtype_head recordtype_block ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:62:14: recordtype_head recordtype_block
            {
            pushFollow(FOLLOW_recordtype_head_in_recordtype456);
            recordtype_head34=recordtype_head();

            state._fsp--;

            stream_recordtype_head.add(recordtype_head34.getTree());

            pushFollow(FOLLOW_recordtype_block_in_recordtype458);
            recordtype_block35=recordtype_block();

            state._fsp--;

            stream_recordtype_block.add(recordtype_block35.getTree());

            // AST REWRITE
            // elements: recordtype_head, recordtype_block
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 62:47: -> ^( RECORDTYPE recordtype_head recordtype_block )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:62:50: ^( RECORDTYPE recordtype_head recordtype_block )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(RECORDTYPE, "RECORDTYPE")
                , root_1);

                adaptor.addChild(root_1, stream_recordtype_head.nextTree());

                adaptor.addChild(root_1, stream_recordtype_block.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recordtype"


    public static class recordtype_head_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recordtype_head"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:64:1: recordtype_head : 'recordtype' '(' name ')' -> ^( NAME name ) ;
    public final DbdFileParser.recordtype_head_return recordtype_head() throws RecognitionException {
        DbdFileParser.recordtype_head_return retval = new DbdFileParser.recordtype_head_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal36=null;
        Token char_literal37=null;
        Token char_literal39=null;
        DbdFileParser.name_return name38 =null;


        Object string_literal36_tree=null;
        Object char_literal37_tree=null;
        Object char_literal39_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:64:17: ( 'recordtype' '(' name ')' -> ^( NAME name ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:64:19: 'recordtype' '(' name ')'
            {
            string_literal36=(Token)match(input,49,FOLLOW_49_in_recordtype_head477);  
            stream_49.add(string_literal36);


            char_literal37=(Token)match(input,36,FOLLOW_36_in_recordtype_head479);  
            stream_36.add(char_literal37);


            pushFollow(FOLLOW_name_in_recordtype_head481);
            name38=name();

            state._fsp--;

            stream_name.add(name38.getTree());

            char_literal39=(Token)match(input,37,FOLLOW_37_in_recordtype_head483);  
            stream_37.add(char_literal39);


            // AST REWRITE
            // elements: name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 64:45: -> ^( NAME name )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:64:48: ^( NAME name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(NAME, "NAME")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recordtype_head"


    public static class recordtype_block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recordtype_block"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:66:1: recordtype_block : '{' ( recordtype_body )* '}' -> ( recordtype_body )* ;
    public final DbdFileParser.recordtype_block_return recordtype_block() throws RecognitionException {
        DbdFileParser.recordtype_block_return retval = new DbdFileParser.recordtype_block_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal40=null;
        Token char_literal42=null;
        DbdFileParser.recordtype_body_return recordtype_body41 =null;


        Object char_literal40_tree=null;
        Object char_literal42_tree=null;
        RewriteRuleTokenStream stream_52=new RewriteRuleTokenStream(adaptor,"token 52");
        RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
        RewriteRuleSubtreeStream stream_recordtype_body=new RewriteRuleSubtreeStream(adaptor,"rule recordtype_body");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:66:18: ( '{' ( recordtype_body )* '}' -> ( recordtype_body )* )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:66:20: '{' ( recordtype_body )* '}'
            {
            char_literal40=(Token)match(input,52,FOLLOW_52_in_recordtype_block500);  
            stream_52.add(char_literal40);


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:66:24: ( recordtype_body )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==44||LA5_0==46) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:66:24: recordtype_body
            	    {
            	    pushFollow(FOLLOW_recordtype_body_in_recordtype_block502);
            	    recordtype_body41=recordtype_body();

            	    state._fsp--;

            	    stream_recordtype_body.add(recordtype_body41.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            char_literal42=(Token)match(input,53,FOLLOW_53_in_recordtype_block505);  
            stream_53.add(char_literal42);


            // AST REWRITE
            // elements: recordtype_body
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 66:45: -> ( recordtype_body )*
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:66:48: ( recordtype_body )*
                while ( stream_recordtype_body.hasNext() ) {
                    adaptor.addChild(root_0, stream_recordtype_body.nextTree());

                }
                stream_recordtype_body.reset();

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recordtype_block"


    public static class recordtype_body_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recordtype_body"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:68:1: recordtype_body : ( field | include );
    public final DbdFileParser.recordtype_body_return recordtype_body() throws RecognitionException {
        DbdFileParser.recordtype_body_return retval = new DbdFileParser.recordtype_body_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.field_return field43 =null;

        DbdFileParser.include_return include44 =null;



        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:68:17: ( field | include )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==44) ) {
                alt6=1;
            }
            else if ( (LA6_0==46) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }
            switch (alt6) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:68:19: field
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_field_in_recordtype_body519);
                    field43=field();

                    state._fsp--;

                    adaptor.addChild(root_0, field43.getTree());

                    }
                    break;
                case 2 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:68:27: include
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_include_in_recordtype_body523);
                    include44=include();

                    state._fsp--;

                    adaptor.addChild(root_0, include44.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recordtype_body"


    public static class field_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "field"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:70:1: field : field_head field_block -> ^( FIELD field_head field_block ) ;
    public final DbdFileParser.field_return field() throws RecognitionException {
        DbdFileParser.field_return retval = new DbdFileParser.field_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.field_head_return field_head45 =null;

        DbdFileParser.field_block_return field_block46 =null;


        RewriteRuleSubtreeStream stream_field_head=new RewriteRuleSubtreeStream(adaptor,"rule field_head");
        RewriteRuleSubtreeStream stream_field_block=new RewriteRuleSubtreeStream(adaptor,"rule field_block");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:70:7: ( field_head field_block -> ^( FIELD field_head field_block ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:70:9: field_head field_block
            {
            pushFollow(FOLLOW_field_head_in_field532);
            field_head45=field_head();

            state._fsp--;

            stream_field_head.add(field_head45.getTree());

            pushFollow(FOLLOW_field_block_in_field534);
            field_block46=field_block();

            state._fsp--;

            stream_field_block.add(field_block46.getTree());

            // AST REWRITE
            // elements: field_block, field_head
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 70:32: -> ^( FIELD field_head field_block )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:70:35: ^( FIELD field_head field_block )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(FIELD, "FIELD")
                , root_1);

                adaptor.addChild(root_1, stream_field_head.nextTree());

                adaptor.addChild(root_1, stream_field_block.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "field"


    public static class field_head_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "field_head"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:72:1: field_head : 'field' '(' name ',' field_type ')' -> ^( NAME name ) ^( TYPE field_type ) ;
    public final DbdFileParser.field_head_return field_head() throws RecognitionException {
        DbdFileParser.field_head_return retval = new DbdFileParser.field_head_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal47=null;
        Token char_literal48=null;
        Token char_literal50=null;
        Token char_literal52=null;
        DbdFileParser.name_return name49 =null;

        DbdFileParser.field_type_return field_type51 =null;


        Object string_literal47_tree=null;
        Object char_literal48_tree=null;
        Object char_literal50_tree=null;
        Object char_literal52_tree=null;
        RewriteRuleTokenStream stream_44=new RewriteRuleTokenStream(adaptor,"token 44");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleTokenStream stream_38=new RewriteRuleTokenStream(adaptor,"token 38");
        RewriteRuleSubtreeStream stream_field_type=new RewriteRuleSubtreeStream(adaptor,"rule field_type");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:72:12: ( 'field' '(' name ',' field_type ')' -> ^( NAME name ) ^( TYPE field_type ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:72:14: 'field' '(' name ',' field_type ')'
            {
            string_literal47=(Token)match(input,44,FOLLOW_44_in_field_head553);  
            stream_44.add(string_literal47);


            char_literal48=(Token)match(input,36,FOLLOW_36_in_field_head555);  
            stream_36.add(char_literal48);


            pushFollow(FOLLOW_name_in_field_head557);
            name49=name();

            state._fsp--;

            stream_name.add(name49.getTree());

            char_literal50=(Token)match(input,38,FOLLOW_38_in_field_head559);  
            stream_38.add(char_literal50);


            pushFollow(FOLLOW_field_type_in_field_head561);
            field_type51=field_type();

            state._fsp--;

            stream_field_type.add(field_type51.getTree());

            char_literal52=(Token)match(input,37,FOLLOW_37_in_field_head563);  
            stream_37.add(char_literal52);


            // AST REWRITE
            // elements: name, field_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 72:50: -> ^( NAME name ) ^( TYPE field_type )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:72:53: ^( NAME name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(NAME, "NAME")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:72:66: ^( TYPE field_type )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(TYPE, "TYPE")
                , root_1);

                adaptor.addChild(root_1, stream_field_type.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "field_head"


    public static class field_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "field_type"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:74:1: field_type : UnquotedString ;
    public final DbdFileParser.field_type_return field_type() throws RecognitionException {
        DbdFileParser.field_type_return retval = new DbdFileParser.field_type_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token UnquotedString53=null;

        Object UnquotedString53_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:74:12: ( UnquotedString )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:74:14: UnquotedString
            {
            root_0 = (Object)adaptor.nil();


            UnquotedString53=(Token)match(input,UnquotedString,FOLLOW_UnquotedString_in_field_type586); 
            UnquotedString53_tree = 
            (Object)adaptor.create(UnquotedString53)
            ;
            adaptor.addChild(root_0, UnquotedString53_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "field_type"


    public static class field_block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "field_block"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:76:1: field_block : '{' ( rule )* '}' -> ( rule )* ;
    public final DbdFileParser.field_block_return field_block() throws RecognitionException {
        DbdFileParser.field_block_return retval = new DbdFileParser.field_block_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal54=null;
        Token char_literal56=null;
        DbdFileParser.rule_return rule55 =null;


        Object char_literal54_tree=null;
        Object char_literal56_tree=null;
        RewriteRuleTokenStream stream_52=new RewriteRuleTokenStream(adaptor,"token 52");
        RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
        RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:76:13: ( '{' ( rule )* '}' -> ( rule )* )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:76:15: '{' ( rule )* '}'
            {
            char_literal54=(Token)match(input,52,FOLLOW_52_in_field_block595);  
            stream_52.add(char_literal54);


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:76:19: ( rule )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==UnquotedString||LA7_0==47) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:76:19: rule
            	    {
            	    pushFollow(FOLLOW_rule_in_field_block597);
            	    rule55=rule();

            	    state._fsp--;

            	    stream_rule.add(rule55.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            char_literal56=(Token)match(input,53,FOLLOW_53_in_field_block600);  
            stream_53.add(char_literal56);


            // AST REWRITE
            // elements: rule
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 76:29: -> ( rule )*
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:76:32: ( rule )*
                while ( stream_rule.hasNext() ) {
                    adaptor.addChild(root_0, stream_rule.nextTree());

                }
                stream_rule.reset();

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "field_block"


    public static class rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:78:1: rule : ( menu_head -> ^( RULE ^( NAME STRING[\"menu\"] ) ^( VALUE menu_head ) ) | rule_head -> ^( RULE rule_head ) );
    public final DbdFileParser.rule_return rule() throws RecognitionException {
        DbdFileParser.rule_return retval = new DbdFileParser.rule_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.menu_head_return menu_head57 =null;

        DbdFileParser.rule_head_return rule_head58 =null;


        RewriteRuleSubtreeStream stream_menu_head=new RewriteRuleSubtreeStream(adaptor,"rule menu_head");
        RewriteRuleSubtreeStream stream_rule_head=new RewriteRuleSubtreeStream(adaptor,"rule rule_head");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:78:6: ( menu_head -> ^( RULE ^( NAME STRING[\"menu\"] ) ^( VALUE menu_head ) ) | rule_head -> ^( RULE rule_head ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==47) ) {
                alt8=1;
            }
            else if ( (LA8_0==UnquotedString) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }
            switch (alt8) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:78:8: menu_head
                    {
                    pushFollow(FOLLOW_menu_head_in_rule614);
                    menu_head57=menu_head();

                    state._fsp--;

                    stream_menu_head.add(menu_head57.getTree());

                    // AST REWRITE
                    // elements: menu_head
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 78:18: -> ^( RULE ^( NAME STRING[\"menu\"] ) ^( VALUE menu_head ) )
                    {
                        // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:78:21: ^( RULE ^( NAME STRING[\"menu\"] ) ^( VALUE menu_head ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RULE, "RULE")
                        , root_1);

                        // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:78:28: ^( NAME STRING[\"menu\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(NAME, "NAME")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(STRING, "menu")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:78:51: ^( VALUE menu_head )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(VALUE, "VALUE")
                        , root_2);

                        adaptor.addChild(root_2, stream_menu_head.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:78:73: rule_head
                    {
                    pushFollow(FOLLOW_rule_head_in_rule637);
                    rule_head58=rule_head();

                    state._fsp--;

                    stream_rule_head.add(rule_head58.getTree());

                    // AST REWRITE
                    // elements: rule_head
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 78:83: -> ^( RULE rule_head )
                    {
                        // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:78:86: ^( RULE rule_head )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RULE, "RULE")
                        , root_1);

                        adaptor.addChild(root_1, stream_rule_head.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule"


    public static class rule_head_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule_head"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:80:1: rule_head : name '(' rule_value ')' -> ^( NAME name ) ^( VALUE rule_value ) ;
    public final DbdFileParser.rule_head_return rule_head() throws RecognitionException {
        DbdFileParser.rule_head_return retval = new DbdFileParser.rule_head_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal60=null;
        Token char_literal62=null;
        DbdFileParser.name_return name59 =null;

        DbdFileParser.rule_value_return rule_value61 =null;


        Object char_literal60_tree=null;
        Object char_literal62_tree=null;
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_rule_value=new RewriteRuleSubtreeStream(adaptor,"rule rule_value");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:80:11: ( name '(' rule_value ')' -> ^( NAME name ) ^( VALUE rule_value ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:80:13: name '(' rule_value ')'
            {
            pushFollow(FOLLOW_name_in_rule_head654);
            name59=name();

            state._fsp--;

            stream_name.add(name59.getTree());

            char_literal60=(Token)match(input,36,FOLLOW_36_in_rule_head656);  
            stream_36.add(char_literal60);


            pushFollow(FOLLOW_rule_value_in_rule_head658);
            rule_value61=rule_value();

            state._fsp--;

            stream_rule_value.add(rule_value61.getTree());

            char_literal62=(Token)match(input,37,FOLLOW_37_in_rule_head660);  
            stream_37.add(char_literal62);


            // AST REWRITE
            // elements: rule_value, name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 80:37: -> ^( NAME name ) ^( VALUE rule_value )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:80:40: ^( NAME name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(NAME, "NAME")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:80:53: ^( VALUE rule_value )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(VALUE, "VALUE")
                , root_1);

                adaptor.addChild(root_1, stream_rule_value.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule_head"


    public static class rule_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule_value"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:82:1: rule_value : ( String | UnquotedString | FLOAT );
    public final DbdFileParser.rule_value_return rule_value() throws RecognitionException {
        DbdFileParser.rule_value_return retval = new DbdFileParser.rule_value_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token set63=null;

        Object set63_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:82:12: ( String | UnquotedString | FLOAT )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
            {
            root_0 = (Object)adaptor.nil();


            set63=(Token)input.LT(1);

            if ( input.LA(1)==FLOAT||input.LA(1)==String||input.LA(1)==UnquotedString ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(set63)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule_value"


    public static class device_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "device"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:84:1: device : 'device' '(' record_type ',' link_type ',' dsetname ',' choice_string ')' -> ^( DEVICE record_type link_type dsetname choice_string ) ;
    public final DbdFileParser.device_return device() throws RecognitionException {
        DbdFileParser.device_return retval = new DbdFileParser.device_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal64=null;
        Token char_literal65=null;
        Token char_literal67=null;
        Token char_literal69=null;
        Token char_literal71=null;
        Token char_literal73=null;
        DbdFileParser.record_type_return record_type66 =null;

        DbdFileParser.link_type_return link_type68 =null;

        DbdFileParser.dsetname_return dsetname70 =null;

        DbdFileParser.choice_string_return choice_string72 =null;


        Object string_literal64_tree=null;
        Object char_literal65_tree=null;
        Object char_literal67_tree=null;
        Object char_literal69_tree=null;
        Object char_literal71_tree=null;
        Object char_literal73_tree=null;
        RewriteRuleTokenStream stream_42=new RewriteRuleTokenStream(adaptor,"token 42");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleTokenStream stream_38=new RewriteRuleTokenStream(adaptor,"token 38");
        RewriteRuleSubtreeStream stream_record_type=new RewriteRuleSubtreeStream(adaptor,"rule record_type");
        RewriteRuleSubtreeStream stream_choice_string=new RewriteRuleSubtreeStream(adaptor,"rule choice_string");
        RewriteRuleSubtreeStream stream_dsetname=new RewriteRuleSubtreeStream(adaptor,"rule dsetname");
        RewriteRuleSubtreeStream stream_link_type=new RewriteRuleSubtreeStream(adaptor,"rule link_type");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:84:8: ( 'device' '(' record_type ',' link_type ',' dsetname ',' choice_string ')' -> ^( DEVICE record_type link_type dsetname choice_string ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:84:10: 'device' '(' record_type ',' link_type ',' dsetname ',' choice_string ')'
            {
            string_literal64=(Token)match(input,42,FOLLOW_42_in_device700);  
            stream_42.add(string_literal64);


            char_literal65=(Token)match(input,36,FOLLOW_36_in_device702);  
            stream_36.add(char_literal65);


            pushFollow(FOLLOW_record_type_in_device704);
            record_type66=record_type();

            state._fsp--;

            stream_record_type.add(record_type66.getTree());

            char_literal67=(Token)match(input,38,FOLLOW_38_in_device706);  
            stream_38.add(char_literal67);


            pushFollow(FOLLOW_link_type_in_device708);
            link_type68=link_type();

            state._fsp--;

            stream_link_type.add(link_type68.getTree());

            char_literal69=(Token)match(input,38,FOLLOW_38_in_device710);  
            stream_38.add(char_literal69);


            pushFollow(FOLLOW_dsetname_in_device712);
            dsetname70=dsetname();

            state._fsp--;

            stream_dsetname.add(dsetname70.getTree());

            char_literal71=(Token)match(input,38,FOLLOW_38_in_device714);  
            stream_38.add(char_literal71);


            pushFollow(FOLLOW_choice_string_in_device716);
            choice_string72=choice_string();

            state._fsp--;

            stream_choice_string.add(choice_string72.getTree());

            char_literal73=(Token)match(input,37,FOLLOW_37_in_device718);  
            stream_37.add(char_literal73);


            // AST REWRITE
            // elements: dsetname, link_type, record_type, choice_string
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 84:84: -> ^( DEVICE record_type link_type dsetname choice_string )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:84:87: ^( DEVICE record_type link_type dsetname choice_string )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(DEVICE, "DEVICE")
                , root_1);

                adaptor.addChild(root_1, stream_record_type.nextTree());

                adaptor.addChild(root_1, stream_link_type.nextTree());

                adaptor.addChild(root_1, stream_dsetname.nextTree());

                adaptor.addChild(root_1, stream_choice_string.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "device"


    public static class record_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "record_type"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:86:1: record_type : UnquotedString ;
    public final DbdFileParser.record_type_return record_type() throws RecognitionException {
        DbdFileParser.record_type_return retval = new DbdFileParser.record_type_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token UnquotedString74=null;

        Object UnquotedString74_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:86:13: ( UnquotedString )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:86:15: UnquotedString
            {
            root_0 = (Object)adaptor.nil();


            UnquotedString74=(Token)match(input,UnquotedString,FOLLOW_UnquotedString_in_record_type741); 
            UnquotedString74_tree = 
            (Object)adaptor.create(UnquotedString74)
            ;
            adaptor.addChild(root_0, UnquotedString74_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "record_type"


    public static class link_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "link_type"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:88:1: link_type : UnquotedString ;
    public final DbdFileParser.link_type_return link_type() throws RecognitionException {
        DbdFileParser.link_type_return retval = new DbdFileParser.link_type_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token UnquotedString75=null;

        Object UnquotedString75_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:88:11: ( UnquotedString )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:88:13: UnquotedString
            {
            root_0 = (Object)adaptor.nil();


            UnquotedString75=(Token)match(input,UnquotedString,FOLLOW_UnquotedString_in_link_type750); 
            UnquotedString75_tree = 
            (Object)adaptor.create(UnquotedString75)
            ;
            adaptor.addChild(root_0, UnquotedString75_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "link_type"


    public static class dsetname_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "dsetname"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:90:1: dsetname : UnquotedString ;
    public final DbdFileParser.dsetname_return dsetname() throws RecognitionException {
        DbdFileParser.dsetname_return retval = new DbdFileParser.dsetname_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token UnquotedString76=null;

        Object UnquotedString76_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:90:10: ( UnquotedString )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:90:12: UnquotedString
            {
            root_0 = (Object)adaptor.nil();


            UnquotedString76=(Token)match(input,UnquotedString,FOLLOW_UnquotedString_in_dsetname759); 
            UnquotedString76_tree = 
            (Object)adaptor.create(UnquotedString76)
            ;
            adaptor.addChild(root_0, UnquotedString76_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "dsetname"


    public static class choice_string_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "choice_string"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:92:1: choice_string : String ;
    public final DbdFileParser.choice_string_return choice_string() throws RecognitionException {
        DbdFileParser.choice_string_return retval = new DbdFileParser.choice_string_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token String77=null;

        Object String77_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:92:15: ( String )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:92:17: String
            {
            root_0 = (Object)adaptor.nil();


            String77=(Token)match(input,String,FOLLOW_String_in_choice_string768); 
            String77_tree = 
            (Object)adaptor.create(String77)
            ;
            adaptor.addChild(root_0, String77_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "choice_string"


    public static class driver_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "driver"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:94:1: driver : 'driver' '(' name ')' -> ^( DRIVER name ) ;
    public final DbdFileParser.driver_return driver() throws RecognitionException {
        DbdFileParser.driver_return retval = new DbdFileParser.driver_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal78=null;
        Token char_literal79=null;
        Token char_literal81=null;
        DbdFileParser.name_return name80 =null;


        Object string_literal78_tree=null;
        Object char_literal79_tree=null;
        Object char_literal81_tree=null;
        RewriteRuleTokenStream stream_43=new RewriteRuleTokenStream(adaptor,"token 43");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:94:8: ( 'driver' '(' name ')' -> ^( DRIVER name ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:94:10: 'driver' '(' name ')'
            {
            string_literal78=(Token)match(input,43,FOLLOW_43_in_driver777);  
            stream_43.add(string_literal78);


            char_literal79=(Token)match(input,36,FOLLOW_36_in_driver779);  
            stream_36.add(char_literal79);


            pushFollow(FOLLOW_name_in_driver781);
            name80=name();

            state._fsp--;

            stream_name.add(name80.getTree());

            char_literal81=(Token)match(input,37,FOLLOW_37_in_driver783);  
            stream_37.add(char_literal81);


            // AST REWRITE
            // elements: name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 94:32: -> ^( DRIVER name )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:94:35: ^( DRIVER name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(DRIVER, "DRIVER")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "driver"


    public static class registrar_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "registrar"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:96:1: registrar : 'registrar' '(' name ')' -> ^( REGISTRAR name ) ;
    public final DbdFileParser.registrar_return registrar() throws RecognitionException {
        DbdFileParser.registrar_return retval = new DbdFileParser.registrar_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal82=null;
        Token char_literal83=null;
        Token char_literal85=null;
        DbdFileParser.name_return name84 =null;


        Object string_literal82_tree=null;
        Object char_literal83_tree=null;
        Object char_literal85_tree=null;
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:96:11: ( 'registrar' '(' name ')' -> ^( REGISTRAR name ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:96:13: 'registrar' '(' name ')'
            {
            string_literal82=(Token)match(input,50,FOLLOW_50_in_registrar800);  
            stream_50.add(string_literal82);


            char_literal83=(Token)match(input,36,FOLLOW_36_in_registrar802);  
            stream_36.add(char_literal83);


            pushFollow(FOLLOW_name_in_registrar804);
            name84=name();

            state._fsp--;

            stream_name.add(name84.getTree());

            char_literal85=(Token)match(input,37,FOLLOW_37_in_registrar806);  
            stream_37.add(char_literal85);


            // AST REWRITE
            // elements: name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 96:38: -> ^( REGISTRAR name )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:96:41: ^( REGISTRAR name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(REGISTRAR, "REGISTRAR")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "registrar"


    public static class variable_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variable"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:98:1: variable : 'variable' var_body -> var_body ;
    public final DbdFileParser.variable_return variable() throws RecognitionException {
        DbdFileParser.variable_return retval = new DbdFileParser.variable_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal86=null;
        DbdFileParser.var_body_return var_body87 =null;


        Object string_literal86_tree=null;
        RewriteRuleTokenStream stream_51=new RewriteRuleTokenStream(adaptor,"token 51");
        RewriteRuleSubtreeStream stream_var_body=new RewriteRuleSubtreeStream(adaptor,"rule var_body");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:98:10: ( 'variable' var_body -> var_body )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:98:12: 'variable' var_body
            {
            string_literal86=(Token)match(input,51,FOLLOW_51_in_variable823);  
            stream_51.add(string_literal86);


            pushFollow(FOLLOW_var_body_in_variable825);
            var_body87=var_body();

            state._fsp--;

            stream_var_body.add(var_body87.getTree());

            // AST REWRITE
            // elements: var_body
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 98:32: -> var_body
            {
                adaptor.addChild(root_0, stream_var_body.nextTree());

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "variable"


    public static class var_body_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_body"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:100:1: var_body : ( var_body_no_type | var_body_with_type );
    public final DbdFileParser.var_body_return var_body() throws RecognitionException {
        DbdFileParser.var_body_return retval = new DbdFileParser.var_body_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.var_body_no_type_return var_body_no_type88 =null;

        DbdFileParser.var_body_with_type_return var_body_with_type89 =null;



        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:100:10: ( var_body_no_type | var_body_with_type )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==36) ) {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==UnquotedString) ) {
                    int LA9_2 = input.LA(3);

                    if ( (LA9_2==37) ) {
                        alt9=1;
                    }
                    else if ( (LA9_2==38) ) {
                        alt9=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 2, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }
            switch (alt9) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:100:12: var_body_no_type
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_var_body_no_type_in_var_body838);
                    var_body_no_type88=var_body_no_type();

                    state._fsp--;

                    adaptor.addChild(root_0, var_body_no_type88.getTree());

                    }
                    break;
                case 2 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:100:31: var_body_with_type
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_var_body_with_type_in_var_body842);
                    var_body_with_type89=var_body_with_type();

                    state._fsp--;

                    adaptor.addChild(root_0, var_body_with_type89.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_body"


    public static class var_body_no_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_body_no_type"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:102:1: var_body_no_type : '(' name ')' -> ^( VARIABLE name ) ;
    public final DbdFileParser.var_body_no_type_return var_body_no_type() throws RecognitionException {
        DbdFileParser.var_body_no_type_return retval = new DbdFileParser.var_body_no_type_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal90=null;
        Token char_literal92=null;
        DbdFileParser.name_return name91 =null;


        Object char_literal90_tree=null;
        Object char_literal92_tree=null;
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:102:18: ( '(' name ')' -> ^( VARIABLE name ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:102:20: '(' name ')'
            {
            char_literal90=(Token)match(input,36,FOLLOW_36_in_var_body_no_type851);  
            stream_36.add(char_literal90);


            pushFollow(FOLLOW_name_in_var_body_no_type853);
            name91=name();

            state._fsp--;

            stream_name.add(name91.getTree());

            char_literal92=(Token)match(input,37,FOLLOW_37_in_var_body_no_type855);  
            stream_37.add(char_literal92);


            // AST REWRITE
            // elements: name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 102:33: -> ^( VARIABLE name )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:102:36: ^( VARIABLE name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(VARIABLE, "VARIABLE")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_body_no_type"


    public static class var_body_with_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_body_with_type"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:104:1: var_body_with_type : '(' name ',' variable_type ')' -> ^( VARIABLE name variable_type ) ;
    public final DbdFileParser.var_body_with_type_return var_body_with_type() throws RecognitionException {
        DbdFileParser.var_body_with_type_return retval = new DbdFileParser.var_body_with_type_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal93=null;
        Token char_literal95=null;
        Token char_literal97=null;
        DbdFileParser.name_return name94 =null;

        DbdFileParser.variable_type_return variable_type96 =null;


        Object char_literal93_tree=null;
        Object char_literal95_tree=null;
        Object char_literal97_tree=null;
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleTokenStream stream_38=new RewriteRuleTokenStream(adaptor,"token 38");
        RewriteRuleSubtreeStream stream_variable_type=new RewriteRuleSubtreeStream(adaptor,"rule variable_type");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:104:20: ( '(' name ',' variable_type ')' -> ^( VARIABLE name variable_type ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:104:22: '(' name ',' variable_type ')'
            {
            char_literal93=(Token)match(input,36,FOLLOW_36_in_var_body_with_type872);  
            stream_36.add(char_literal93);


            pushFollow(FOLLOW_name_in_var_body_with_type874);
            name94=name();

            state._fsp--;

            stream_name.add(name94.getTree());

            char_literal95=(Token)match(input,38,FOLLOW_38_in_var_body_with_type876);  
            stream_38.add(char_literal95);


            pushFollow(FOLLOW_variable_type_in_var_body_with_type878);
            variable_type96=variable_type();

            state._fsp--;

            stream_variable_type.add(variable_type96.getTree());

            char_literal97=(Token)match(input,37,FOLLOW_37_in_var_body_with_type880);  
            stream_37.add(char_literal97);


            // AST REWRITE
            // elements: name, variable_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 104:53: -> ^( VARIABLE name variable_type )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:104:56: ^( VARIABLE name variable_type )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(VARIABLE, "VARIABLE")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_1, stream_variable_type.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_body_with_type"


    public static class variable_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variable_type"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:106:1: variable_type : UnquotedString ;
    public final DbdFileParser.variable_type_return variable_type() throws RecognitionException {
        DbdFileParser.variable_type_return retval = new DbdFileParser.variable_type_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token UnquotedString98=null;

        Object UnquotedString98_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:106:15: ( UnquotedString )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:106:17: UnquotedString
            {
            root_0 = (Object)adaptor.nil();


            UnquotedString98=(Token)match(input,UnquotedString,FOLLOW_UnquotedString_in_variable_type899); 
            UnquotedString98_tree = 
            (Object)adaptor.create(UnquotedString98)
            ;
            adaptor.addChild(root_0, UnquotedString98_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "variable_type"


    public static class function_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:108:1: function : 'function' '(' name ')' -> ^( FUNCTION name ) ;
    public final DbdFileParser.function_return function() throws RecognitionException {
        DbdFileParser.function_return retval = new DbdFileParser.function_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal99=null;
        Token char_literal100=null;
        Token char_literal102=null;
        DbdFileParser.name_return name101 =null;


        Object string_literal99_tree=null;
        Object char_literal100_tree=null;
        Object char_literal102_tree=null;
        RewriteRuleTokenStream stream_45=new RewriteRuleTokenStream(adaptor,"token 45");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:108:10: ( 'function' '(' name ')' -> ^( FUNCTION name ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:108:12: 'function' '(' name ')'
            {
            string_literal99=(Token)match(input,45,FOLLOW_45_in_function908);  
            stream_45.add(string_literal99);


            char_literal100=(Token)match(input,36,FOLLOW_36_in_function910);  
            stream_36.add(char_literal100);


            pushFollow(FOLLOW_name_in_function912);
            name101=name();

            state._fsp--;

            stream_name.add(name101.getTree());

            char_literal102=(Token)match(input,37,FOLLOW_37_in_function914);  
            stream_37.add(char_literal102);


            // AST REWRITE
            // elements: name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 108:36: -> ^( FUNCTION name )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:108:39: ^( FUNCTION name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(FUNCTION, "FUNCTION")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class breaktable_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "breaktable"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:110:1: breaktable : breaktable_head breaktable_block -> ^( BREAKTABLE breaktable_head breaktable_block ) ;
    public final DbdFileParser.breaktable_return breaktable() throws RecognitionException {
        DbdFileParser.breaktable_return retval = new DbdFileParser.breaktable_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.breaktable_head_return breaktable_head103 =null;

        DbdFileParser.breaktable_block_return breaktable_block104 =null;


        RewriteRuleSubtreeStream stream_breaktable_head=new RewriteRuleSubtreeStream(adaptor,"rule breaktable_head");
        RewriteRuleSubtreeStream stream_breaktable_block=new RewriteRuleSubtreeStream(adaptor,"rule breaktable_block");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:110:12: ( breaktable_head breaktable_block -> ^( BREAKTABLE breaktable_head breaktable_block ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:110:14: breaktable_head breaktable_block
            {
            pushFollow(FOLLOW_breaktable_head_in_breaktable931);
            breaktable_head103=breaktable_head();

            state._fsp--;

            stream_breaktable_head.add(breaktable_head103.getTree());

            pushFollow(FOLLOW_breaktable_block_in_breaktable933);
            breaktable_block104=breaktable_block();

            state._fsp--;

            stream_breaktable_block.add(breaktable_block104.getTree());

            // AST REWRITE
            // elements: breaktable_block, breaktable_head
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 110:47: -> ^( BREAKTABLE breaktable_head breaktable_block )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:110:50: ^( BREAKTABLE breaktable_head breaktable_block )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(BREAKTABLE, "BREAKTABLE")
                , root_1);

                adaptor.addChild(root_1, stream_breaktable_head.nextTree());

                adaptor.addChild(root_1, stream_breaktable_block.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "breaktable"


    public static class breaktable_head_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "breaktable_head"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:112:1: breaktable_head : 'breaktable' '(' name ')' -> ^( NAME name ) ;
    public final DbdFileParser.breaktable_head_return breaktable_head() throws RecognitionException {
        DbdFileParser.breaktable_head_return retval = new DbdFileParser.breaktable_head_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token string_literal105=null;
        Token char_literal106=null;
        Token char_literal108=null;
        DbdFileParser.name_return name107 =null;


        Object string_literal105_tree=null;
        Object char_literal106_tree=null;
        Object char_literal108_tree=null;
        RewriteRuleTokenStream stream_40=new RewriteRuleTokenStream(adaptor,"token 40");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:112:17: ( 'breaktable' '(' name ')' -> ^( NAME name ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:112:19: 'breaktable' '(' name ')'
            {
            string_literal105=(Token)match(input,40,FOLLOW_40_in_breaktable_head952);  
            stream_40.add(string_literal105);


            char_literal106=(Token)match(input,36,FOLLOW_36_in_breaktable_head954);  
            stream_36.add(char_literal106);


            pushFollow(FOLLOW_name_in_breaktable_head956);
            name107=name();

            state._fsp--;

            stream_name.add(name107.getTree());

            char_literal108=(Token)match(input,37,FOLLOW_37_in_breaktable_head958);  
            stream_37.add(char_literal108);


            // AST REWRITE
            // elements: name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 112:45: -> ^( NAME name )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:112:48: ^( NAME name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(NAME, "NAME")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "breaktable_head"


    public static class breaktable_block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "breaktable_block"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:114:1: breaktable_block : '{' ( breaktable_body )* '}' -> ( breaktable_body )* ;
    public final DbdFileParser.breaktable_block_return breaktable_block() throws RecognitionException {
        DbdFileParser.breaktable_block_return retval = new DbdFileParser.breaktable_block_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal109=null;
        Token char_literal111=null;
        DbdFileParser.breaktable_body_return breaktable_body110 =null;


        Object char_literal109_tree=null;
        Object char_literal111_tree=null;
        RewriteRuleTokenStream stream_52=new RewriteRuleTokenStream(adaptor,"token 52");
        RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
        RewriteRuleSubtreeStream stream_breaktable_body=new RewriteRuleSubtreeStream(adaptor,"rule breaktable_body");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:114:18: ( '{' ( breaktable_body )* '}' -> ( breaktable_body )* )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:114:20: '{' ( breaktable_body )* '}'
            {
            char_literal109=(Token)match(input,52,FOLLOW_52_in_breaktable_block975);  
            stream_52.add(char_literal109);


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:114:24: ( breaktable_body )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==FLOAT) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:114:24: breaktable_body
            	    {
            	    pushFollow(FOLLOW_breaktable_body_in_breaktable_block977);
            	    breaktable_body110=breaktable_body();

            	    state._fsp--;

            	    stream_breaktable_body.add(breaktable_body110.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            char_literal111=(Token)match(input,53,FOLLOW_53_in_breaktable_block980);  
            stream_53.add(char_literal111);


            // AST REWRITE
            // elements: breaktable_body
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 114:45: -> ( breaktable_body )*
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:114:48: ( breaktable_body )*
                while ( stream_breaktable_body.hasNext() ) {
                    adaptor.addChild(root_0, stream_breaktable_body.nextTree());

                }
                stream_breaktable_body.reset();

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "breaktable_block"


    public static class breaktable_body_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "breaktable_body"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:116:1: breaktable_body : raw_value eng_value -> ^( VALUE ^( RAW raw_value ) ^( ENG eng_value ) ) ;
    public final DbdFileParser.breaktable_body_return breaktable_body() throws RecognitionException {
        DbdFileParser.breaktable_body_return retval = new DbdFileParser.breaktable_body_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        DbdFileParser.raw_value_return raw_value112 =null;

        DbdFileParser.eng_value_return eng_value113 =null;


        RewriteRuleSubtreeStream stream_eng_value=new RewriteRuleSubtreeStream(adaptor,"rule eng_value");
        RewriteRuleSubtreeStream stream_raw_value=new RewriteRuleSubtreeStream(adaptor,"rule raw_value");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:116:17: ( raw_value eng_value -> ^( VALUE ^( RAW raw_value ) ^( ENG eng_value ) ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:116:19: raw_value eng_value
            {
            pushFollow(FOLLOW_raw_value_in_breaktable_body994);
            raw_value112=raw_value();

            state._fsp--;

            stream_raw_value.add(raw_value112.getTree());

            pushFollow(FOLLOW_eng_value_in_breaktable_body996);
            eng_value113=eng_value();

            state._fsp--;

            stream_eng_value.add(eng_value113.getTree());

            // AST REWRITE
            // elements: raw_value, eng_value
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 116:39: -> ^( VALUE ^( RAW raw_value ) ^( ENG eng_value ) )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:116:42: ^( VALUE ^( RAW raw_value ) ^( ENG eng_value ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(VALUE, "VALUE")
                , root_1);

                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:116:50: ^( RAW raw_value )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(RAW, "RAW")
                , root_2);

                adaptor.addChild(root_2, stream_raw_value.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:116:67: ^( ENG eng_value )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(ENG, "ENG")
                , root_2);

                adaptor.addChild(root_2, stream_eng_value.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "breaktable_body"


    public static class raw_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "raw_value"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:118:1: raw_value : FLOAT ;
    public final DbdFileParser.raw_value_return raw_value() throws RecognitionException {
        DbdFileParser.raw_value_return retval = new DbdFileParser.raw_value_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token FLOAT114=null;

        Object FLOAT114_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:118:11: ( FLOAT )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:118:13: FLOAT
            {
            root_0 = (Object)adaptor.nil();


            FLOAT114=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_raw_value1023); 
            FLOAT114_tree = 
            (Object)adaptor.create(FLOAT114)
            ;
            adaptor.addChild(root_0, FLOAT114_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "raw_value"


    public static class eng_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "eng_value"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:120:1: eng_value : FLOAT ;
    public final DbdFileParser.eng_value_return eng_value() throws RecognitionException {
        DbdFileParser.eng_value_return retval = new DbdFileParser.eng_value_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token FLOAT115=null;

        Object FLOAT115_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:120:11: ( FLOAT )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:120:13: FLOAT
            {
            root_0 = (Object)adaptor.nil();


            FLOAT115=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_eng_value1032); 
            FLOAT115_tree = 
            (Object)adaptor.create(FLOAT115)
            ;
            adaptor.addChild(root_0, FLOAT115_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "eng_value"


    public static class key_value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "key_value"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:122:1: key_value : name ',' value -> ^( KEY name ) ^( VALUE value ) ;
    public final DbdFileParser.key_value_return key_value() throws RecognitionException {
        DbdFileParser.key_value_return retval = new DbdFileParser.key_value_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal117=null;
        DbdFileParser.name_return name116 =null;

        DbdFileParser.value_return value118 =null;


        Object char_literal117_tree=null;
        RewriteRuleTokenStream stream_38=new RewriteRuleTokenStream(adaptor,"token 38");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:122:11: ( name ',' value -> ^( KEY name ) ^( VALUE value ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:122:13: name ',' value
            {
            pushFollow(FOLLOW_name_in_key_value1041);
            name116=name();

            state._fsp--;

            stream_name.add(name116.getTree());

            char_literal117=(Token)match(input,38,FOLLOW_38_in_key_value1043);  
            stream_38.add(char_literal117);


            pushFollow(FOLLOW_value_in_key_value1045);
            value118=value();

            state._fsp--;

            stream_value.add(value118.getTree());

            // AST REWRITE
            // elements: value, name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 122:28: -> ^( KEY name ) ^( VALUE value )
            {
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:122:31: ^( KEY name )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(KEY, "KEY")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextTree());

                adaptor.addChild(root_0, root_1);
                }

                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:122:43: ^( VALUE value )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(VALUE, "VALUE")
                , root_1);

                adaptor.addChild(root_1, stream_value.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "key_value"


    public static class name_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "name"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:124:1: name : UnquotedString ;
    public final DbdFileParser.name_return name() throws RecognitionException {
        DbdFileParser.name_return retval = new DbdFileParser.name_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token UnquotedString119=null;

        Object UnquotedString119_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:124:6: ( UnquotedString )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:124:8: UnquotedString
            {
            root_0 = (Object)adaptor.nil();


            UnquotedString119=(Token)match(input,UnquotedString,FOLLOW_UnquotedString_in_name1068); 
            UnquotedString119_tree = 
            (Object)adaptor.create(UnquotedString119)
            ;
            adaptor.addChild(root_0, UnquotedString119_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "name"


    public static class value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "value"
    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:126:1: value : String ;
    public final DbdFileParser.value_return value() throws RecognitionException {
        DbdFileParser.value_return retval = new DbdFileParser.value_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token String120=null;

        Object String120_tree=null;

        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:126:7: ( String )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:126:9: String
            {
            root_0 = (Object)adaptor.nil();


            String120=(Token)match(input,String,FOLLOW_String_in_value1077); 
            String120_tree = 
            (Object)adaptor.create(String120)
            ;
            adaptor.addChild(root_0, String120_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "value"

    // Delegated rules


 

    public static final BitSet FOLLOW_program_in_top234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathdef_in_program245 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_include_in_program249 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_menu_in_program253 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_recordtype_in_program257 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_device_in_program261 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_driver_in_program265 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_registrar_in_program269 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_variable_in_program273 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_function_in_program277 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_breaktable_in_program281 = new BitSet(new long[]{0x000FED8000000002L});
    public static final BitSet FOLLOW_48_in_pathdef295 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_39_in_pathdef299 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_path_in_pathdef303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_include320 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_filename_in_include322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_String_in_path339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_String_in_filename348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_menu_head_in_menu357 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_menu_block_in_menu359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_menu_head382 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_menu_head384 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_menu_head386 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_menu_head388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_menu_block401 = new BitSet(new long[]{0x0020420000000000L});
    public static final BitSet FOLLOW_menu_body_in_menu_block403 = new BitSet(new long[]{0x0020420000000000L});
    public static final BitSet FOLLOW_53_in_menu_block406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_choice_in_menu_body420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_include_in_menu_body424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_choice433 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_choice435 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_key_value_in_choice437 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_choice439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_recordtype_head_in_recordtype456 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_recordtype_block_in_recordtype458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_recordtype_head477 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_recordtype_head479 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_recordtype_head481 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_recordtype_head483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_recordtype_block500 = new BitSet(new long[]{0x0020500000000000L});
    public static final BitSet FOLLOW_recordtype_body_in_recordtype_block502 = new BitSet(new long[]{0x0020500000000000L});
    public static final BitSet FOLLOW_53_in_recordtype_block505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_in_recordtype_body519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_include_in_recordtype_body523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_head_in_field532 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_field_block_in_field534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_field_head553 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_field_head555 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_field_head557 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_field_head559 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_field_type_in_field_head561 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_field_head563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UnquotedString_in_field_type586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_field_block595 = new BitSet(new long[]{0x0020800100000000L});
    public static final BitSet FOLLOW_rule_in_field_block597 = new BitSet(new long[]{0x0020800100000000L});
    public static final BitSet FOLLOW_53_in_field_block600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_menu_head_in_rule614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_head_in_rule637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_rule_head654 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_rule_head656 = new BitSet(new long[]{0x0000000120008000L});
    public static final BitSet FOLLOW_rule_value_in_rule_head658 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_rule_head660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_device700 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_device702 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_record_type_in_device704 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_device706 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_link_type_in_device708 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_device710 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_dsetname_in_device712 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_device714 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_choice_string_in_device716 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_device718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UnquotedString_in_record_type741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UnquotedString_in_link_type750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UnquotedString_in_dsetname759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_String_in_choice_string768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_driver777 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_driver779 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_driver781 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_driver783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_registrar800 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_registrar802 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_registrar804 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_registrar806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_variable823 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_var_body_in_variable825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_body_no_type_in_var_body838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_body_with_type_in_var_body842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_var_body_no_type851 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_var_body_no_type853 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_var_body_no_type855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_var_body_with_type872 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_var_body_with_type874 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_var_body_with_type876 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_variable_type_in_var_body_with_type878 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_var_body_with_type880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UnquotedString_in_variable_type899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_function908 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_function910 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_function912 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_function914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_breaktable_head_in_breaktable931 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_breaktable_block_in_breaktable933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_breaktable_head952 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_breaktable_head954 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_name_in_breaktable_head956 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_breaktable_head958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_breaktable_block975 = new BitSet(new long[]{0x0020000000008000L});
    public static final BitSet FOLLOW_breaktable_body_in_breaktable_block977 = new BitSet(new long[]{0x0020000000008000L});
    public static final BitSet FOLLOW_53_in_breaktable_block980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_raw_value_in_breaktable_body994 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_eng_value_in_breaktable_body996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_raw_value1023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_eng_value1032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_name_in_key_value1041 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_key_value1043 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_value_in_key_value1045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UnquotedString_in_name1068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_String_in_value1077 = new BitSet(new long[]{0x0000000000000002L});

}
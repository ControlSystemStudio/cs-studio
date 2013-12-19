/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
// $ANTLR 3.4 cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g 2013-10-04 12:03:00
package org.csstudio.utility.dbdparser.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class DbdFileLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public DbdFileLexer() {} 
    public DbdFileLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public DbdFileLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g"; }

    // $ANTLR start "BREAKTABLE"
    public final void mBREAKTABLE() throws RecognitionException {
        try {
            int _type = BREAKTABLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:11:12: ( '$$_breaktable' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:11:14: '$$_breaktable'
            {
            match("$$_breaktable"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BREAKTABLE"

    // $ANTLR start "CHOICE"
    public final void mCHOICE() throws RecognitionException {
        try {
            int _type = CHOICE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:12:8: ( '$$_choice' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:12:10: '$$_choice'
            {
            match("$$_choice"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CHOICE"

    // $ANTLR start "DEVICE"
    public final void mDEVICE() throws RecognitionException {
        try {
            int _type = DEVICE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:13:8: ( '$$_device' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:13:10: '$$_device'
            {
            match("$$_device"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DEVICE"

    // $ANTLR start "DRIVER"
    public final void mDRIVER() throws RecognitionException {
        try {
            int _type = DRIVER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:14:8: ( '$$_driver' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:14:10: '$$_driver'
            {
            match("$$_driver"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DRIVER"

    // $ANTLR start "ENG"
    public final void mENG() throws RecognitionException {
        try {
            int _type = ENG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:15:5: ( '$$_eng' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:15:7: '$$_eng'
            {
            match("$$_eng"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ENG"

    // $ANTLR start "FIELD"
    public final void mFIELD() throws RecognitionException {
        try {
            int _type = FIELD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:16:7: ( '$$_field' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:16:9: '$$_field'
            {
            match("$$_field"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FIELD"

    // $ANTLR start "FUNCTION"
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:17:10: ( '$$_function' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:17:12: '$$_function'
            {
            match("$$_function"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FUNCTION"

    // $ANTLR start "INCLUDE"
    public final void mINCLUDE() throws RecognitionException {
        try {
            int _type = INCLUDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:18:9: ( '$$_include' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:18:11: '$$_include'
            {
            match("$$_include"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INCLUDE"

    // $ANTLR start "KEY"
    public final void mKEY() throws RecognitionException {
        try {
            int _type = KEY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:19:5: ( '$$_key' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:19:7: '$$_key'
            {
            match("$$_key"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KEY"

    // $ANTLR start "MENU"
    public final void mMENU() throws RecognitionException {
        try {
            int _type = MENU;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:20:6: ( '$$_menu' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:20:8: '$$_menu'
            {
            match("$$_menu"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MENU"

    // $ANTLR start "NAME"
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:21:6: ( '$$_name' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:21:8: '$$_name'
            {
            match("$$_name"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAME"

    // $ANTLR start "PATH"
    public final void mPATH() throws RecognitionException {
        try {
            int _type = PATH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:22:6: ( '$$_path' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:22:8: '$$_path'
            {
            match("$$_path"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PATH"

    // $ANTLR start "RAW"
    public final void mRAW() throws RecognitionException {
        try {
            int _type = RAW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:23:5: ( '$$_raw' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:23:7: '$$_raw'
            {
            match("$$_raw"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RAW"

    // $ANTLR start "RECORDTYPE"
    public final void mRECORDTYPE() throws RecognitionException {
        try {
            int _type = RECORDTYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:24:12: ( '$$_recordtype' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:24:14: '$$_recordtype'
            {
            match("$$_recordtype"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RECORDTYPE"

    // $ANTLR start "REGISTRAR"
    public final void mREGISTRAR() throws RecognitionException {
        try {
            int _type = REGISTRAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:25:11: ( '$$_registrar' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:25:13: '$$_registrar'
            {
            match("$$_registrar"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "REGISTRAR"

    // $ANTLR start "RULE"
    public final void mRULE() throws RecognitionException {
        try {
            int _type = RULE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:26:6: ( '$$_rule' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:26:8: '$$_rule'
            {
            match("$$_rule"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RULE"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:27:8: ( '$$_string' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:27:10: '$$_string'
            {
            match("$$_string"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "TYPE"
    public final void mTYPE() throws RecognitionException {
        try {
            int _type = TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:28:6: ( '$$_type' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:28:8: '$$_type'
            {
            match("$$_type"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "VALUE"
    public final void mVALUE() throws RecognitionException {
        try {
            int _type = VALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:29:7: ( '$$_value' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:29:9: '$$_value'
            {
            match("$$_value"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VALUE"

    // $ANTLR start "VARIABLE"
    public final void mVARIABLE() throws RecognitionException {
        try {
            int _type = VARIABLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:30:10: ( '$$_variable' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:30:12: '$$_variable'
            {
            match("$$_variable"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VARIABLE"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:31:7: ( '(' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:31:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:32:7: ( ')' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:32:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:33:7: ( ',' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:33:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:34:7: ( 'addpath' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:34:9: 'addpath'
            {
            match("addpath"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:35:7: ( 'breaktable' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:35:9: 'breaktable'
            {
            match("breaktable"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:36:7: ( 'choice' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:36:9: 'choice'
            {
            match("choice"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:37:7: ( 'device' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:37:9: 'device'
            {
            match("device"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:38:7: ( 'driver' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:38:9: 'driver'
            {
            match("driver"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:39:7: ( 'field' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:39:9: 'field'
            {
            match("field"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:40:7: ( 'function' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:40:9: 'function'
            {
            match("function"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:41:7: ( 'include' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:41:9: 'include'
            {
            match("include"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__46"

    // $ANTLR start "T__47"
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:7: ( 'menu' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:42:9: 'menu'
            {
            match("menu"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__47"

    // $ANTLR start "T__48"
    public final void mT__48() throws RecognitionException {
        try {
            int _type = T__48;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:43:7: ( 'path' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:43:9: 'path'
            {
            match("path"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__48"

    // $ANTLR start "T__49"
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:7: ( 'recordtype' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:44:9: 'recordtype'
            {
            match("recordtype"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__49"

    // $ANTLR start "T__50"
    public final void mT__50() throws RecognitionException {
        try {
            int _type = T__50;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:45:7: ( 'registrar' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:45:9: 'registrar'
            {
            match("registrar"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__50"

    // $ANTLR start "T__51"
    public final void mT__51() throws RecognitionException {
        try {
            int _type = T__51;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:46:7: ( 'variable' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:46:9: 'variable'
            {
            match("variable"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__51"

    // $ANTLR start "T__52"
    public final void mT__52() throws RecognitionException {
        try {
            int _type = T__52;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:47:7: ( '{' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:47:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__52"

    // $ANTLR start "T__53"
    public final void mT__53() throws RecognitionException {
        try {
            int _type = T__53;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:48:7: ( '}' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:48:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__53"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            int _type = OCTAL_ESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:129:3: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt1=3;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='\\') ) {
                int LA1_1 = input.LA(2);

                if ( ((LA1_1 >= '0' && LA1_1 <= '3')) ) {
                    int LA1_2 = input.LA(3);

                    if ( ((LA1_2 >= '0' && LA1_2 <= '7')) ) {
                        int LA1_4 = input.LA(4);

                        if ( ((LA1_4 >= '0' && LA1_4 <= '7')) ) {
                            alt1=1;
                        }
                        else {
                            alt1=2;
                        }
                    }
                    else {
                        alt1=3;
                    }
                }
                else if ( ((LA1_1 >= '4' && LA1_1 <= '7')) ) {
                    int LA1_3 = input.LA(3);

                    if ( ((LA1_3 >= '0' && LA1_3 <= '7')) ) {
                        alt1=2;
                    }
                    else {
                        alt1=3;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }
            switch (alt1) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:130:3: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '3') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:131:5: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 3 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:132:5: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OCTAL_ESC"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:136:3: ( '0' .. '9' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:141:3: ( ( DIGIT | 'a' .. 'f' | 'A' .. 'F' ) )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            int _type = UNICODE_ESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:150:3: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:151:3: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 

            match('u'); 

            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNICODE_ESC"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            int _type = ESC_SEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:155:3: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt2=1;
                    }
                    break;
                case 'u':
                    {
                    alt2=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt2=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }
            switch (alt2) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:156:3: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 

                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:167:5: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 


                    }
                    break;
                case 3 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:168:5: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:172:3: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:173:3: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:177:3: ( '+' | '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='+'||LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:181:3: ( '0' .. '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:185:3: ( ( DIGIT )+ '.' ( DIGIT )* ( EXPONENT )? | '.' ( DIGIT )+ ( EXPONENT )? | ( DIGIT )+ EXPONENT )
            int alt11=3;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:186:3: ( DIGIT )+ '.' ( DIGIT )* ( EXPONENT )?
                    {
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:186:3: ( DIGIT )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    match('.'); 

                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:186:14: ( DIGIT )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:186:21: ( EXPONENT )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='E'||LA7_0=='e') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:186:21: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:187:5: '.' ( DIGIT )+ ( EXPONENT )?
                    {
                    match('.'); 

                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:187:9: ( DIGIT )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0 >= '0' && LA8_0 <= '9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);


                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:187:16: ( EXPONENT )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='E'||LA9_0=='e') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:187:16: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:188:5: ( DIGIT )+ EXPONENT
                    {
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:188:5: ( DIGIT )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0 >= '0' && LA10_0 <= '9')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);


                    mEXPONENT(); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "String"
    public final void mString() throws RecognitionException {
        try {
            int _type = String;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:192:3: ( '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:193:3: '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 

            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:194:3: ( ESC_SEQ |~ ( '\\\\' | '\"' ) )*
            loop12:
            do {
                int alt12=3;
                int LA12_0 = input.LA(1);

                if ( (LA12_0=='\\') ) {
                    alt12=1;
                }
                else if ( ((LA12_0 >= '\u0000' && LA12_0 <= '!')||(LA12_0 >= '#' && LA12_0 <= '[')||(LA12_0 >= ']' && LA12_0 <= '\uFFFF')) ) {
                    alt12=2;
                }


                switch (alt12) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:195:5: ESC_SEQ
            	    {
            	    mESC_SEQ(); 


            	    }
            	    break;
            	case 2 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:197:5: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "String"

    // $ANTLR start "UnquotedString"
    public final void mUnquotedString() throws RecognitionException {
        try {
            int _type = UnquotedString;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:206:3: ( ( DIGIT | ( 'A' .. 'Z' ) | ( 'a' .. 'z' ) | '_' | '-' | ':' | '.' | '[' | ']' | '<' | '>' | ';' )+ )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:207:3: ( DIGIT | ( 'A' .. 'Z' ) | ( 'a' .. 'z' ) | '_' | '-' | ':' | '.' | '[' | ']' | '<' | '>' | ';' )+
            {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:207:3: ( DIGIT | ( 'A' .. 'Z' ) | ( 'a' .. 'z' ) | '_' | '-' | ':' | '.' | '[' | ']' | '<' | '>' | ';' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0 >= '-' && LA13_0 <= '.')||(LA13_0 >= '0' && LA13_0 <= '<')||LA13_0=='>'||(LA13_0 >= 'A' && LA13_0 <= '[')||LA13_0==']'||LA13_0=='_'||(LA13_0 >= 'a' && LA13_0 <= 'z')) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
            	    {
            	    if ( (input.LA(1) >= '-' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '<')||input.LA(1)=='>'||(input.LA(1) >= 'A' && input.LA(1) <= '[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UnquotedString"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:235:3: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:236:3: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            {
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:236:3: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0 >= '\t' && LA14_0 <= '\n')||(LA14_0 >= '\f' && LA14_0 <= '\r')||LA14_0==' ') ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
            } while (true);


             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:247:3: ( '#' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:248:3: '#' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match('#'); 

            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:249:3: (~ ( '\\n' | '\\r' ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0 >= '\u0000' && LA15_0 <= '\t')||(LA15_0 >= '\u000B' && LA15_0 <= '\f')||(LA15_0 >= '\u000E' && LA15_0 <= '\uFFFF')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:253:3: ( '\\r' )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='\r') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:253:3: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "C_declaration"
    public final void mC_declaration() throws RecognitionException {
        try {
            int _type = C_declaration;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:258:3: ( '%' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:259:3: '%' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match('%'); 

            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:260:3: (~ ( '\\n' | '\\r' ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0 >= '\u0000' && LA17_0 <= '\t')||(LA17_0 >= '\u000B' && LA17_0 <= '\f')||(LA17_0 >= '\u000E' && LA17_0 <= '\uFFFF')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:264:3: ( '\\r' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0=='\r') ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:264:3: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "C_declaration"

    public void mTokens() throws RecognitionException {
        // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:8: ( BREAKTABLE | CHOICE | DEVICE | DRIVER | ENG | FIELD | FUNCTION | INCLUDE | KEY | MENU | NAME | PATH | RAW | RECORDTYPE | REGISTRAR | RULE | STRING | TYPE | VALUE | VARIABLE | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | OCTAL_ESC | UNICODE_ESC | ESC_SEQ | FLOAT | String | UnquotedString | WHITESPACE | COMMENT | C_declaration )
        int alt19=47;
        alt19 = dfa19.predict(input);
        switch (alt19) {
            case 1 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:10: BREAKTABLE
                {
                mBREAKTABLE(); 


                }
                break;
            case 2 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:21: CHOICE
                {
                mCHOICE(); 


                }
                break;
            case 3 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:28: DEVICE
                {
                mDEVICE(); 


                }
                break;
            case 4 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:35: DRIVER
                {
                mDRIVER(); 


                }
                break;
            case 5 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:42: ENG
                {
                mENG(); 


                }
                break;
            case 6 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:46: FIELD
                {
                mFIELD(); 


                }
                break;
            case 7 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:52: FUNCTION
                {
                mFUNCTION(); 


                }
                break;
            case 8 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:61: INCLUDE
                {
                mINCLUDE(); 


                }
                break;
            case 9 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:69: KEY
                {
                mKEY(); 


                }
                break;
            case 10 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:73: MENU
                {
                mMENU(); 


                }
                break;
            case 11 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:78: NAME
                {
                mNAME(); 


                }
                break;
            case 12 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:83: PATH
                {
                mPATH(); 


                }
                break;
            case 13 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:88: RAW
                {
                mRAW(); 


                }
                break;
            case 14 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:92: RECORDTYPE
                {
                mRECORDTYPE(); 


                }
                break;
            case 15 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:103: REGISTRAR
                {
                mREGISTRAR(); 


                }
                break;
            case 16 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:113: RULE
                {
                mRULE(); 


                }
                break;
            case 17 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:118: STRING
                {
                mSTRING(); 


                }
                break;
            case 18 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:125: TYPE
                {
                mTYPE(); 


                }
                break;
            case 19 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:130: VALUE
                {
                mVALUE(); 


                }
                break;
            case 20 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:136: VARIABLE
                {
                mVARIABLE(); 


                }
                break;
            case 21 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:145: T__36
                {
                mT__36(); 


                }
                break;
            case 22 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:151: T__37
                {
                mT__37(); 


                }
                break;
            case 23 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:157: T__38
                {
                mT__38(); 


                }
                break;
            case 24 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:163: T__39
                {
                mT__39(); 


                }
                break;
            case 25 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:169: T__40
                {
                mT__40(); 


                }
                break;
            case 26 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:175: T__41
                {
                mT__41(); 


                }
                break;
            case 27 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:181: T__42
                {
                mT__42(); 


                }
                break;
            case 28 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:187: T__43
                {
                mT__43(); 


                }
                break;
            case 29 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:193: T__44
                {
                mT__44(); 


                }
                break;
            case 30 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:199: T__45
                {
                mT__45(); 


                }
                break;
            case 31 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:205: T__46
                {
                mT__46(); 


                }
                break;
            case 32 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:211: T__47
                {
                mT__47(); 


                }
                break;
            case 33 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:217: T__48
                {
                mT__48(); 


                }
                break;
            case 34 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:223: T__49
                {
                mT__49(); 


                }
                break;
            case 35 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:229: T__50
                {
                mT__50(); 


                }
                break;
            case 36 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:235: T__51
                {
                mT__51(); 


                }
                break;
            case 37 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:241: T__52
                {
                mT__52(); 


                }
                break;
            case 38 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:247: T__53
                {
                mT__53(); 


                }
                break;
            case 39 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:253: OCTAL_ESC
                {
                mOCTAL_ESC(); 


                }
                break;
            case 40 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:263: UNICODE_ESC
                {
                mUNICODE_ESC(); 


                }
                break;
            case 41 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:275: ESC_SEQ
                {
                mESC_SEQ(); 


                }
                break;
            case 42 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:283: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 43 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:289: String
                {
                mString(); 


                }
                break;
            case 44 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:296: UnquotedString
                {
                mUnquotedString(); 


                }
                break;
            case 45 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:311: WHITESPACE
                {
                mWHITESPACE(); 


                }
                break;
            case 46 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:322: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 47 :
                // cs-studio/products/ITER/plugins/org.csstudio.utility.dbdparser.test/src/org/csstudio/utility/dbdparser/antlr/DbdFile.g:1:330: C_declaration
                {
                mC_declaration(); 


                }
                break;

        }

    }


    protected DFA11 dfa11 = new DFA11(this);
    protected DFA19 dfa19 = new DFA19(this);
    static final String DFA11_eotS =
        "\5\uffff";
    static final String DFA11_eofS =
        "\5\uffff";
    static final String DFA11_minS =
        "\2\56\3\uffff";
    static final String DFA11_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA11_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA11_specialS =
        "\5\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "184:1: FLOAT : ( ( DIGIT )+ '.' ( DIGIT )* ( EXPONENT )? | '.' ( DIGIT )+ ( EXPONENT )? | ( DIGIT )+ EXPONENT );";
        }
    }
    static final String DFA19_eotS =
        "\5\uffff\12\25\3\uffff\2\25\6\uffff\14\25\2\74\2\uffff\1\101\1\25"+
        "\1\101\1\uffff\15\25\1\74\3\uffff\1\101\1\25\1\uffff\1\25\1\101"+
        "\1\25\16\uffff\10\25\1\166\1\167\3\25\2\uffff\1\25\1\101\1\25\1"+
        "\101\10\uffff\5\25\1\u0085\2\25\2\uffff\3\25\5\uffff\2\25\1\u008e"+
        "\1\u008f\1\u0090\1\uffff\5\25\1\uffff\1\u0097\1\25\3\uffff\1\25"+
        "\1\u009a\3\25\2\uffff\1\25\1\u009f\1\uffff\2\25\1\u00a2\1\25\1\uffff"+
        "\1\25\1\u00a5\1\uffff\1\u00a6\1\u00a7\3\uffff";
    static final String DFA19_eofS =
        "\u00a8\uffff";
    static final String DFA19_minS =
        "\1\11\1\44\3\uffff\1\144\1\162\1\150\1\145\1\151\1\156\1\145\1\141"+
        "\1\145\1\141\2\uffff\1\42\1\56\1\60\5\uffff\1\137\1\144\1\145\1"+
        "\157\1\166\1\151\1\145\1\156\1\143\1\156\1\164\1\143\1\162\3\60"+
        "\1\uffff\1\55\1\53\1\55\1\142\1\160\1\141\2\151\1\166\1\154\1\143"+
        "\1\154\1\165\1\150\1\157\2\151\1\60\2\uffff\1\60\1\55\1\53\1\uffff"+
        "\1\60\1\55\1\53\2\uffff\1\145\1\uffff\1\151\5\uffff\1\141\2\uffff"+
        "\2\141\1\153\2\143\1\145\1\144\1\164\1\165\2\55\1\162\1\163\1\141"+
        "\1\uffff\2\60\1\55\1\60\1\55\5\uffff\1\143\1\uffff\1\154\2\164\2"+
        "\145\1\162\1\55\1\151\1\144\2\uffff\1\144\1\164\1\142\1\60\4\uffff"+
        "\1\150\1\141\3\55\1\uffff\1\157\1\145\1\164\1\162\1\154\1\uffff"+
        "\1\55\1\142\3\uffff\1\156\1\55\1\171\1\141\1\145\2\uffff\1\154\1"+
        "\55\1\uffff\1\160\1\162\1\55\1\145\1\uffff\1\145\1\55\1\uffff\2"+
        "\55\3\uffff";
    static final String DFA19_maxS =
        "\1\175\1\44\3\uffff\1\144\1\162\1\150\1\162\1\165\1\156\1\145\1"+
        "\141\1\145\1\141\2\uffff\1\165\1\145\1\71\5\uffff\1\137\1\144\1"+
        "\145\1\157\1\166\1\151\1\145\1\156\1\143\1\156\1\164\1\147\1\162"+
        "\2\67\1\146\1\uffff\1\172\1\71\1\172\1\166\1\160\1\141\2\151\1\166"+
        "\1\154\1\143\1\154\1\165\1\150\1\157\2\151\1\67\2\uffff\1\146\1"+
        "\172\1\71\1\uffff\1\71\1\172\1\71\2\uffff\1\162\1\uffff\1\165\5"+
        "\uffff\1\165\2\uffff\2\141\1\153\2\143\1\145\1\144\1\164\1\165\2"+
        "\172\1\162\1\163\1\141\1\uffff\1\146\1\71\1\172\1\71\1\172\5\uffff"+
        "\1\147\1\uffff\1\162\2\164\2\145\1\162\1\172\1\151\1\144\2\uffff"+
        "\1\144\1\164\1\142\1\146\4\uffff\1\150\1\141\3\172\1\uffff\1\157"+
        "\1\145\1\164\1\162\1\154\1\uffff\1\172\1\142\3\uffff\1\156\1\172"+
        "\1\171\1\141\1\145\2\uffff\1\154\1\172\1\uffff\1\160\1\162\1\172"+
        "\1\145\1\uffff\1\145\1\172\1\uffff\2\172\3\uffff";
    static final String DFA19_acceptS =
        "\2\uffff\1\25\1\26\1\27\12\uffff\1\45\1\46\3\uffff\1\53\1\54\1\55"+
        "\1\56\1\57\20\uffff\1\51\22\uffff\2\47\3\uffff\1\52\3\uffff\1\1"+
        "\1\2\1\uffff\1\5\1\uffff\1\10\1\11\1\12\1\13\1\14\1\uffff\1\21\1"+
        "\22\16\uffff\1\47\5\uffff\1\3\1\4\1\6\1\7\1\15\1\uffff\1\20\11\uffff"+
        "\1\40\1\41\4\uffff\1\16\1\17\1\23\1\24\5\uffff\1\35\5\uffff\1\50"+
        "\2\uffff\1\32\1\33\1\34\5\uffff\1\50\1\30\2\uffff\1\37\4\uffff\1"+
        "\36\2\uffff\1\44\2\uffff\1\43\1\31\1\42";
    static final String DFA19_specialS =
        "\u00a8\uffff}>";
    static final String[] DFA19_transitionS = {
            "\2\26\1\uffff\2\26\22\uffff\1\26\1\uffff\1\24\1\27\1\1\1\30"+
            "\2\uffff\1\2\1\3\2\uffff\1\4\1\25\1\23\1\uffff\12\22\3\25\1"+
            "\uffff\1\25\2\uffff\33\25\1\21\1\25\1\uffff\1\25\1\uffff\1\5"+
            "\1\6\1\7\1\10\1\25\1\11\2\25\1\12\3\25\1\13\2\25\1\14\1\25\1"+
            "\15\3\25\1\16\4\25\1\17\1\uffff\1\20",
            "\1\31",
            "",
            "",
            "",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35\14\uffff\1\36",
            "\1\37\13\uffff\1\40",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44",
            "\1\45",
            "",
            "",
            "\1\51\4\uffff\1\51\10\uffff\4\46\4\47\44\uffff\1\51\5\uffff"+
            "\1\51\3\uffff\1\51\7\uffff\1\51\3\uffff\1\51\1\uffff\1\51\1"+
            "\50",
            "\1\52\1\uffff\12\22\13\uffff\1\53\37\uffff\1\53",
            "\12\54",
            "",
            "",
            "",
            "",
            "",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70\3\uffff\1\71",
            "\1\72",
            "\10\73",
            "\10\75",
            "\12\76\7\uffff\6\76\32\uffff\6\76",
            "",
            "\2\25\1\uffff\12\77\3\25\1\uffff\1\25\2\uffff\4\25\1\100\26"+
            "\25\1\uffff\1\25\1\uffff\1\25\1\uffff\4\25\1\100\25\25",
            "\1\101\1\uffff\1\102\2\uffff\12\103",
            "\2\25\1\uffff\12\54\3\25\1\uffff\1\25\2\uffff\4\25\1\104\26"+
            "\25\1\uffff\1\25\1\uffff\1\25\1\uffff\4\25\1\104\25\25",
            "\1\105\1\106\1\107\1\110\1\111\2\uffff\1\112\1\uffff\1\113"+
            "\1\uffff\1\114\1\115\1\uffff\1\116\1\uffff\1\117\1\120\1\121"+
            "\1\uffff\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\10\140",
            "",
            "",
            "\12\141\7\uffff\6\141\32\uffff\6\141",
            "\2\25\1\uffff\12\77\3\25\1\uffff\1\25\2\uffff\4\25\1\100\26"+
            "\25\1\uffff\1\25\1\uffff\1\25\1\uffff\4\25\1\100\25\25",
            "\1\101\1\uffff\1\142\2\uffff\12\143",
            "",
            "\12\103",
            "\2\25\1\uffff\12\103\3\25\1\uffff\1\25\2\uffff\33\25\1\uffff"+
            "\1\25\1\uffff\1\25\1\uffff\32\25",
            "\1\101\1\uffff\1\144\2\uffff\12\145",
            "",
            "",
            "\1\146\14\uffff\1\147",
            "",
            "\1\150\13\uffff\1\151",
            "",
            "",
            "",
            "",
            "",
            "\1\152\3\uffff\1\153\17\uffff\1\154",
            "",
            "",
            "\1\155",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\1\170",
            "\1\171",
            "\1\172",
            "",
            "\12\173\7\uffff\6\173\32\uffff\6\173",
            "\12\143",
            "\2\25\1\uffff\12\143\3\25\1\uffff\1\25\2\uffff\33\25\1\uffff"+
            "\1\25\1\uffff\1\25\1\uffff\32\25",
            "\12\145",
            "\2\25\1\uffff\12\145\3\25\1\uffff\1\25\2\uffff\33\25\1\uffff"+
            "\1\25\1\uffff\1\25\1\uffff\32\25",
            "",
            "",
            "",
            "",
            "",
            "\1\174\3\uffff\1\175",
            "",
            "\1\176\5\uffff\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\1\u0083",
            "\1\u0084",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\1\u0086",
            "\1\u0087",
            "",
            "",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\12\u008b\7\uffff\6\u008b\32\uffff\6\u008b",
            "",
            "",
            "",
            "",
            "\1\u008c",
            "\1\u008d",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\u0095",
            "",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\1\u0098",
            "",
            "",
            "",
            "\1\u0099",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\1\u009b",
            "\1\u009c",
            "\1\u009d",
            "",
            "",
            "\1\u009e",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "",
            "\1\u00a0",
            "\1\u00a1",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\1\u00a3",
            "",
            "\1\u00a4",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "\2\25\1\uffff\15\25\1\uffff\1\25\2\uffff\33\25\1\uffff\1\25"+
            "\1\uffff\1\25\1\uffff\32\25",
            "",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( BREAKTABLE | CHOICE | DEVICE | DRIVER | ENG | FIELD | FUNCTION | INCLUDE | KEY | MENU | NAME | PATH | RAW | RECORDTYPE | REGISTRAR | RULE | STRING | TYPE | VALUE | VARIABLE | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | OCTAL_ESC | UNICODE_ESC | ESC_SEQ | FLOAT | String | UnquotedString | WHITESPACE | COMMENT | C_declaration );";
        }
    }
 

}
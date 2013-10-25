// EPICS Database Definition R3.15 http://www.aps.anl.gov/epics/base/R3-15/0-docs/AppDevGuide/node7.html
grammar DbdFile;

options {
  language = Java;
  output = AST;
}

tokens {
  PATH = '$$_path';
  INCLUDE = '$$_include';
  MENU = '$$_menu';
  CHOICE = '$$_choice';
  RECORDTYPE = '$$_recordtype';
  FIELD = '$$_field';
  RULE = '$$_rule';
  DEVICE = '$$_device';
  DRIVER = '$$_driver';
  REGISTRAR = '$$_registrar';
  VARIABLE = '$$_variable';
  FUNCTION = '$$_function';
  BREAKTABLE = '$$_breaktable';
  RAW = '$$_raw';
  ENG = '$$_eng';
  NAME = '$$_name';
  TYPE = '$$_type';
  KEY = '$$_key';
  VALUE = '$$_value';
  STRING = '$$_string';
}

@header {
package org.csstudio.utility.dbdparser.test.antlr;
}

@lexer::header {
package org.csstudio.utility.dbdparser.test.antlr;
}

top : program ;

program : ( pathdef | include | menu | recordtype | device | driver | registrar | variable | function | breaktable )* ;

pathdef : ( 'path' | 'addpath' ) path -> ^(PATH path) ;

include : 'include' filename -> ^(INCLUDE filename) ;

path : String ;

filename : String ;

menu : menu_head menu_block -> ^(MENU ^(NAME menu_head) menu_block) ;

menu_head : 'menu' '(' name ')' -> name ;

menu_block : '{' menu_body* '}' -> menu_body* ;

menu_body : choice | include ;

choice : 'choice' '(' key_value ')' -> ^(CHOICE key_value) ;

recordtype : recordtype_head recordtype_block -> ^(RECORDTYPE recordtype_head recordtype_block) ;

recordtype_head : 'recordtype' '(' name ')' -> ^(NAME name) ;

recordtype_block : '{' recordtype_body* '}' -> recordtype_body* ;

recordtype_body : field | include ;

field : field_head field_block -> ^(FIELD field_head field_block) ;

field_head : 'field' '(' name ',' field_type ')' -> ^(NAME name) ^(TYPE field_type) ;

field_type : UnquotedString ;

field_block : '{' rule* '}' -> rule* ;

rule : menu_head -> ^(RULE ^(NAME STRING["menu"]) ^(VALUE menu_head)) | rule_head -> ^(RULE rule_head) ;

rule_head : name '(' rule_value ')' -> ^(NAME name) ^(VALUE rule_value) ;

rule_value : String | UnquotedString | FLOAT ;

device : 'device' '(' record_type ',' link_type ',' dsetname ',' choice_string ')' -> ^(DEVICE record_type link_type dsetname choice_string) ;

record_type : UnquotedString ;

link_type : UnquotedString ;

dsetname : UnquotedString ;

choice_string : String ;

driver : 'driver' '(' name ')' -> ^(DRIVER name) ;

registrar : 'registrar' '(' name ')' -> ^(REGISTRAR name) ;

variable : 'variable' var_body -> var_body ;

var_body : var_body_no_type | var_body_with_type ;

var_body_no_type : '(' name ')' -> ^(VARIABLE name) ;

var_body_with_type : '(' name ',' variable_type ')' -> ^(VARIABLE name variable_type) ;

variable_type : UnquotedString ;

function : 'function' '(' name ')' -> ^(FUNCTION name) ;

breaktable : breaktable_head breaktable_block -> ^(BREAKTABLE breaktable_head breaktable_block) ;

breaktable_head : 'breaktable' '(' name ')' -> ^(NAME name) ;

breaktable_block : '{' breaktable_body* '}' -> breaktable_body* ;

breaktable_body : raw_value eng_value -> ^(VALUE ^(RAW raw_value) ^(ENG eng_value)) ;

raw_value : FLOAT ;

eng_value : FLOAT ;

key_value : name ',' value -> ^(KEY name) ^(VALUE value) ;

name : UnquotedString ;

value : String ;

OCTAL_ESC
  :
  '\\' ('0'..'3') ('0'..'7') ('0'..'7')
  | '\\' ('0'..'7') ('0'..'7')
  | '\\' ('0'..'7')
  ;

fragment DIGIT
  :
  '0'..'9'
  ;

fragment HEX_DIGIT
  :
  (
    DIGIT
    | 'a'..'f'
    | 'A'..'F'
  )
  ;

UNICODE_ESC
  :
  '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
  ;

ESC_SEQ
  :
  '\\'
  (
    'b'
    | 't'
    | 'n'
    | 'f'
    | 'r'
    | '\"'
    | '\''
    | '\\'
  )
  | UNICODE_ESC
  | OCTAL_ESC
  ;

fragment EXPONENT
  :
  (
    'e'
    | 'E'
  )
  (
    '+'
    | '-'
  )?
  ('0'..'9')+
  ;

FLOAT
  :
  DIGIT+ '.' DIGIT* EXPONENT?
  | '.' DIGIT+ EXPONENT?
  | DIGIT+ EXPONENT
  ;

String
  :
  '"'
  (
    ESC_SEQ
    |
    ~(
      '\\'
      | '"'
    )
  )*
  '"'
  ;

UnquotedString
  :
  (   
    DIGIT
    |
    ('A'..'Z')
    |
    ('a'..'z')
    |
    '_'
    |
    '-'
    |
    ':'
    |
    '.'
    |
    '['
    |
    ']'
    |
    '<'
    |
    '>'
    |
    ';'
  )+
  ;

WHITESPACE
  :
  (
    '\t'
    | ' '
    | '\r'
    | '\n'
    | '\u000C'
  )+
  { $channel = HIDDEN; }
  ;

COMMENT
  :
  '#'
  ~(
    '\n'
    | '\r'
  )*
  '\r'? '\n'
  { $channel = HIDDEN; }
  ;

C_declaration
  :
  '%'
  ~(
    '\n'
    | '\r'
  )*
  '\r'? '\n' 
  { $channel = HIDDEN; }
  ;

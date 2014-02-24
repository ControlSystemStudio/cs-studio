header
{
//   $Id: sql-gen.g 10001 2006-06-08 21:08:04Z steve.ebersole@jboss.com $
package org.hibernate.hql.antlr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

}
/**
 * SQL Generator Tree Parser, providing SQL rendering of SQL ASTs produced by the previous phase, HqlSqlWalker.  All
 * syntax decoration such as extra spaces, lack of spaces, extra parens, etc. should be added by this class.
 * <br>
 * This grammar processes the HQL/SQL AST and produces an SQL string.  The intent is to move dialect-specific
 * code into a sub-class that will override some of the methods, just like the other two grammars in this system.
 * @author Joshua Davis (joshua@hibernate.org)
 */
class SqlGeneratorBase extends TreeParser;

options {
	// Note: importVocab and exportVocab cause ANTLR to share the token type numbers between the
	// two grammars.  This means that the token type constants from the source tree are the same
	// as those in the target tree.  If this is not the case, tree translation can result in
	// token types from the *source* tree being present in the target tree.
	importVocab=HqlSql;         // import definitions from "HqlSql"
	exportVocab=Sql;            // Call the resulting definitions "Sql"
	buildAST=false;             // Don't build an AST.
}

{
	private static Logger log = LoggerFactory.getLogger(SqlGeneratorBase.class);

   /** the buffer resulting SQL statement is written to */
	private StringBuffer buf = new StringBuffer();

	protected void out(String s) {
		buf.append(s);
	}

	/**
	 * Returns the last character written to the output, or -1 if there isn't one.
	 */
	protected int getLastChar() {
		int len = buf.length();
		if ( len == 0 )
			return -1;
		else
			return buf.charAt( len - 1 );
	}

	/**
	 * Add a aspace if the previous token was not a space or a parenthesis.
	 */
	protected void optionalSpace() {
		// Implemented in the sub-class.
	}

	protected void out(AST n) {
		out(n.getText());
	}

	protected void separator(AST n, String sep) {
		if (n.getNextSibling() != null)
			out(sep);
	}

	protected boolean hasText(AST a) {
		String t = a.getText();
		return t != null && t.length() > 0;
	}

	protected void fromFragmentSeparator(AST a) {
		// moved this impl into the subclass...
	}

	protected void nestedFromFragment(AST d,AST parent) {
		// moved this impl into the subclass...
	}

	protected StringBuffer getStringBuffer() {
		return buf;
	}

	protected void nyi(AST n) {
		throw new UnsupportedOperationException("Unsupported node: " + n);
	}

	protected void beginFunctionTemplate(AST m,AST i) {
		// if template is null we just write the function out as it appears in the hql statement
		out(i);
		out("(");
	}

	protected void endFunctionTemplate(AST m) {
	      out(")");
	}

	protected void commaBetweenParameters(String comma) {
		out(comma);
	}
}

statement
	: selectStatement
	| updateStatement
	| deleteStatement
	| insertStatement
	;

selectStatement
	: #(SELECT { out("select "); }
		selectClause
		from
		( #(WHERE { out(" where "); } whereExpr ) )?
		( #(GROUP { out(" group by "); } groupExprs ( #(HAVING { out(" having "); } booleanExpr[false]) )? ) )?
		( #(ORDER { out(" order by "); } orderExprs ) )?
	)
	;

// Note: eats the FROM token node, as it is not valid in an update statement.
// It's outlived its usefulness after analysis phase :)
// TODO : needed to use conditionList directly here and deleteStatement, as whereExprs no longer works for this stuff
updateStatement
	: #(UPDATE { out("update "); }
		#( FROM fromTable )
		setClause
		(whereClause)?
	)
	;

deleteStatement
	// Note: not space needed at end of "delete" because the from rule included one before the "from" it outputs
	: #(DELETE { out("delete"); }
		from
		(whereClause)?
	)
	;

insertStatement
	: #(INSERT { out( "insert " ); }
		i:INTO { out( i ); out( " " ); }
		selectStatement
	)
	;

setClause
	// Simply re-use comparisionExpr, because it already correctly defines the EQ rule the
	// way it is needed here; not the most aptly named, but ah
	: #( SET { out(" set "); } comparisonExpr[false] ( { out(", "); } comparisonExpr[false] )* )
	;

whereClause
	: #(WHERE { out(" where "); } whereClauseExpr )
	;

whereClauseExpr
	: (SQL_TOKEN) => conditionList
	| booleanExpr[ false ]
	;

orderExprs
	// TODO: remove goofy space before the comma when we don't have to regression test anymore.
	: ( expr ) (dir:orderDirection { out(" "); out(dir); })? ( {out(", "); } orderExprs)?
	;

groupExprs
	// TODO: remove goofy space before the comma when we don't have to regression test anymore.
	: expr ( {out(" , "); } groupExprs)?
	;

orderDirection
	: ASCENDING
	| DESCENDING
	;

whereExpr
	// Expect the filter subtree, followed by the theta join subtree, followed by the HQL condition subtree.
	// Might need parens around the HQL condition if there is more than one subtree.
	// Put 'and' between each subtree.
	: filters
		( { out(" and "); } thetaJoins )?
		( { out(" and "); } booleanExpr [ true ] )?
	| thetaJoins
		( { out(" and "); } booleanExpr [ true ] )? 
	| booleanExpr[false]
	;

filters
	: #(FILTERS conditionList )
	;

thetaJoins
	: #(THETA_JOINS conditionList )
	;

conditionList
	: sqlToken ( { out(" and "); } conditionList )?
	;

selectClause
	: #(SELECT_CLAUSE (distinctOrAll)? ( selectColumn )+ )
	;

selectColumn
	: p:selectExpr (sc:SELECT_COLUMNS { out(sc); } )? { separator( (sc != null) ? sc : p,", "); }
	;

selectExpr
	: e:selectAtom { out(e); }
	| mcr:mapComponentReference { out(mcr); }
	| count
	| #(CONSTRUCTOR (DOT | IDENT) ( selectColumn )+ )
	| methodCall
	| aggregate
	| c:constant { out(c); }
	| arithmeticExpr
	| param:PARAM { out(param); }
	| sn:SQL_NODE { out(sn); }
	| { out("("); } selectStatement { out(")"); }
	;

count
	: #(COUNT { out("count("); }  ( distinctOrAll ) ? countExpr { out(")"); } )
	;

distinctOrAll
	: DISTINCT { out("distinct "); }
	| ALL { out("all "); }
	;

countExpr
	// Syntacitic predicate resolves star all by itself, avoiding a conflict with STAR in expr.
	: ROW_STAR { out("*"); }
	| simpleExpr
	;

selectAtom
	: DOT
	| SQL_TOKEN
	| ALIAS_REF
	| SELECT_EXPR
	;

mapComponentReference
    : KEY
    | VALUE
    | ENTRY
    ;

// The from-clause piece is all goofed up.  Currently, nodes of type FROM_FRAGMENT
// and JOIN_FRAGMENT can occur at any level in the FromClause sub-tree. We really
// should come back and clean this up at some point; which I think will require
// a post-HqlSqlWalker phase to "re-align" the FromElements in a more sensible
// manner.
from
	: #(f:FROM { out(" from "); }
		(fromTable)* )
	;

fromTable
	// Write the table node (from fragment) and all the join fragments associated with it.
	: #( a:FROM_FRAGMENT  { out(a); } (tableJoin [ a ])* { fromFragmentSeparator(a); } )
	| #( b:JOIN_FRAGMENT  { out(b); } (tableJoin [ b ])* { fromFragmentSeparator(b); } )
	;

tableJoin [ AST parent ]
	: #( c:JOIN_FRAGMENT { out(" "); out(c); } (tableJoin [ c ] )* )
	| #( d:FROM_FRAGMENT { nestedFromFragment(d,parent); } (tableJoin [ d ] )* )
	;

booleanOp[ boolean parens ]
	: #(AND booleanExpr[true] { out(" and "); } booleanExpr[true])
	| #(OR { if (parens) out("("); } booleanExpr[false] { out(" or "); } booleanExpr[false] { if (parens) out(")"); })
	| #(NOT { out(" not ("); } booleanExpr[false] { out(")"); } )
	;

booleanExpr[ boolean parens ]
	: booleanOp [ parens ]
	| comparisonExpr [ parens ]
	| st:SQL_TOKEN { out(st); } // solely for the purpose of mapping-defined where-fragments
	;
	
comparisonExpr[ boolean parens ]
	: binaryComparisonExpression
	| { if (parens) out("("); } exoticComparisonExpression { if (parens) out(")"); }
	;
	
binaryComparisonExpression
	: #(EQ expr { out("="); } expr)
	| #(NE expr { out("<>"); } expr)
	| #(GT expr { out(">"); } expr)
	| #(GE expr { out(">="); } expr)
	| #(LT expr { out("<"); } expr)
	| #(LE expr { out("<="); } expr)
	;
	
exoticComparisonExpression
	: #(LIKE expr { out(" like "); } expr likeEscape )
	| #(NOT_LIKE expr { out(" not like "); } expr likeEscape)
	| #(BETWEEN expr { out(" between "); } expr { out(" and "); } expr)
	| #(NOT_BETWEEN expr { out(" not between "); } expr { out(" and "); } expr)
	| #(IN expr { out(" in"); } inList )
	| #(NOT_IN expr { out(" not in "); } inList )
	| #(EXISTS { optionalSpace(); out("exists "); } quantified )
	| #(IS_NULL expr) { out(" is null"); }
	| #(IS_NOT_NULL expr) { out(" is not null"); }
	;

likeEscape
	: ( #(ESCAPE { out(" escape "); } expr) )?
	;

inList
	: #(IN_LIST { out(" "); } ( parenSelect | simpleExprList ) )
	;
	
simpleExprList
	: { out("("); } (e:simpleExpr { separator(e," , "); } )* { out(")"); }
	;

// A simple expression, or a sub-select with parens around it.
expr
	: simpleExpr
	| #( VECTOR_EXPR { out("("); } (e:expr { separator(e," , "); } )*  { out(")"); } )
	| parenSelect
	| #(ANY { out("any "); } quantified )
	| #(ALL { out("all "); } quantified )
	| #(SOME { out("some "); } quantified )
	;
	
quantified
	: { out("("); } ( sqlToken | selectStatement ) { out(")"); } 
	;
	
parenSelect
	: { out("("); } selectStatement { out(")"); }
	;
	
simpleExpr
	: c:constant { out(c); }
	| NULL { out("null"); }
	| addrExpr
	| sqlToken
	| aggregate
	| methodCall
	| count
	| parameter
	| arithmeticExpr
	;
	
constant
	: NUM_DOUBLE
	| NUM_FLOAT
	| NUM_INT
	| NUM_LONG
	| NUM_BIG_INTEGER
	| NUM_BIG_DECIMAL
	| QUOTED_STRING
	| CONSTANT
	| JAVA_CONSTANT
	| TRUE
	| FALSE
	| IDENT
	;
	
arithmeticExpr
	: additiveExpr
	| multiplicativeExpr
//	| #(CONCAT { out("("); } expr ( { out("||"); } expr )+ { out(")"); } )
	| #(UNARY_MINUS { out("-"); } expr)
	| caseExpr
	;

additiveExpr
	: #(PLUS expr { out("+"); } expr)
	| #(MINUS expr { out("-"); } nestedExprAfterMinusDiv)
	;

multiplicativeExpr
	: #(STAR nestedExpr { out("*"); } nestedExpr)
	| #(DIV nestedExpr { out("/"); } nestedExprAfterMinusDiv)
	| #(MOD nestedExpr { out(" % "); } nestedExprAfterMinusDiv)
	;

nestedExpr
	// Generate parens around nested additive expressions, use a syntactic predicate to avoid conflicts with 'expr'.
	: (additiveExpr) => { out("("); } additiveExpr { out(")"); }
	| expr
	;
	
nestedExprAfterMinusDiv
	// Generate parens around nested arithmetic expressions, use a syntactic predicate to avoid conflicts with 'expr'.
	: (arithmeticExpr) => { out("("); } arithmeticExpr { out(")"); }
	| expr
	;

caseExpr
	: #(CASE { out("case"); } 
		( #(WHEN { out( " when "); } booleanExpr[false] { out(" then "); } expr) )+ 
		( #(ELSE { out(" else "); } expr) )?
		{ out(" end"); } )
	| #(CASE2 { out("case "); } expr
		( #(WHEN { out( " when "); } expr { out(" then "); } expr) )+ 
		( #(ELSE { out(" else "); } expr) )?
		{ out(" end"); } )
	;

aggregate
    : #(
        a:AGGREGATE { beginFunctionTemplate( a, a ); }
     	expr
        { endFunctionTemplate( a ); }
    )
    ;

methodCall
	: #(m:METHOD_CALL i:METHOD_NAME { beginFunctionTemplate(m,i); }
	 ( #(EXPR_LIST (arguments)? ) )?
	 { endFunctionTemplate(m); } )
	;

arguments
	: expr ( { commaBetweenParameters(", "); } expr )*
	;

parameter
	: n:NAMED_PARAM { out(n); }
	| p:PARAM { out(p); }
	;

addrExpr
	: #(r:DOT . .) { out(r); }
	| i:ALIAS_REF { out(i); }
	| j:INDEX_OP { out(j); }
	;

sqlToken
	: t:SQL_TOKEN { out(t); }
	;


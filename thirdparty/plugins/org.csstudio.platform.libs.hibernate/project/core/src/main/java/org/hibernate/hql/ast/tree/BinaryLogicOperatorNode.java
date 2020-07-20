/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.hql.ast.tree;

import org.hibernate.type.OneToOneType;
import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.TypeMismatchException;
import org.hibernate.HibernateException;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.util.StringHelper;
import org.hibernate.hql.antlr.HqlSqlTokenTypes;
import org.hibernate.engine.SessionFactoryImplementor;
import antlr.SemanticException;
import antlr.collections.AST;

/**
 * Contract for nodes representing binary operators.
 *
 * @author Steve Ebersole
 */
public class BinaryLogicOperatorNode extends HqlSqlWalkerNode implements BinaryOperatorNode {
	/**
	 * Performs the operator node initialization by seeking out any parameter
	 * nodes and setting their expected type, if possible.
	 */
	public void initialize() throws SemanticException {
		Node lhs = getLeftHandOperand();
		if ( lhs == null ) {
			throw new SemanticException( "left-hand operand of a binary operator was null" );
		}
		Node rhs = getRightHandOperand();
		if ( rhs == null ) {
			throw new SemanticException( "right-hand operand of a binary operator was null" );
		}

		Type lhsType = extractDataType( lhs );
		Type rhsType = extractDataType( rhs );

		if ( lhsType == null ) {
			lhsType = rhsType;
		}
		if ( rhsType == null ) {
			rhsType = lhsType;
		}

		if ( ExpectedTypeAwareNode.class.isAssignableFrom( lhs.getClass() ) ) {
			( ( ExpectedTypeAwareNode ) lhs ).setExpectedType( rhsType );
		}
		if ( ExpectedTypeAwareNode.class.isAssignableFrom( rhs.getClass() ) ) {
			( ( ExpectedTypeAwareNode ) rhs ).setExpectedType( lhsType );
		}

		mutateRowValueConstructorSyntaxesIfNecessary( lhsType, rhsType );
	}

	protected final void mutateRowValueConstructorSyntaxesIfNecessary(Type lhsType, Type rhsType) {
		// TODO : this really needs to be delayed until after we definitively know all node types
		// where this is currently a problem is parameters for which where we cannot unequivocally
		// resolve an expected type
		SessionFactoryImplementor sessionFactory = getSessionFactoryHelper().getFactory();
		if ( lhsType != null && rhsType != null ) {
			int lhsColumnSpan = getColumnSpan( lhsType, sessionFactory );
			if ( lhsColumnSpan != getColumnSpan( rhsType, sessionFactory ) ) {
				throw new TypeMismatchException(
						"left and right hand sides of a binary logic operator were incompatibile [" +
						lhsType.getName() + " : "+ rhsType.getName() + "]"
				);
			}
			if ( lhsColumnSpan > 1 ) {
				// for dialects which are known to not support ANSI-SQL row-value-constructor syntax,
				// we should mutate the tree.
				if ( !sessionFactory.getDialect().supportsRowValueConstructorSyntax() ) {
					mutateRowValueConstructorSyntax( lhsColumnSpan );
				}
			}
		}
	}

	private int getColumnSpan(Type type, SessionFactoryImplementor sfi) {
		int columnSpan = type.getColumnSpan( sfi );
		if ( columnSpan == 0 && type instanceof OneToOneType ) {
			columnSpan = ( ( OneToOneType ) type ).getIdentifierOrUniqueKeyType( sfi ).getColumnSpan( sfi );
		}
		return columnSpan;
	}

	/**
	 * Mutate the subtree relating to a row-value-constructor to instead use
	 * a series of ANDed predicates.  This allows multi-column type comparisons
	 * and explicit row-value-constructor syntax even on databases which do
	 * not support row-value-constructor.
	 * <p/>
	 * For example, here we'd mutate "... where (col1, col2) = ('val1', 'val2) ..." to
	 * "... where col1 = 'val1' and col2 = 'val2' ..."
	 *
	 * @param valueElements The number of elements in the row value constructor list.
	 */
	private void mutateRowValueConstructorSyntax(int valueElements) {
		// mutation depends on the types of nodes involved...
		int comparisonType = getType();
		String comparisonText = getText();
		setType( HqlSqlTokenTypes.AND );
		setText( "AND" );
		String[] lhsElementTexts = extractMutationTexts( getLeftHandOperand(), valueElements );
		String[] rhsElementTexts = extractMutationTexts( getRightHandOperand(), valueElements );

		ParameterSpecification lhsEmbeddedCompositeParameterSpecification =
				getLeftHandOperand() == null || ( !ParameterNode.class.isInstance( getLeftHandOperand() ) )
						? null
						: ( ( ParameterNode ) getLeftHandOperand() ).getHqlParameterSpecification();

		ParameterSpecification rhsEmbeddedCompositeParameterSpecification =
				getRightHandOperand() == null || ( !ParameterNode.class.isInstance( getRightHandOperand() ) )
						? null
						: ( ( ParameterNode ) getRightHandOperand() ).getHqlParameterSpecification();

		translate( valueElements, comparisonType, comparisonText,
                lhsElementTexts, rhsElementTexts,
                lhsEmbeddedCompositeParameterSpecification,
                rhsEmbeddedCompositeParameterSpecification, this );
	}
	/**
	 * 
	 */
    protected void translate( int valueElements, int comparisonType,
            String comparisonText, String[] lhsElementTexts,
            String[] rhsElementTexts,
            ParameterSpecification lhsEmbeddedCompositeParameterSpecification,
            ParameterSpecification rhsEmbeddedCompositeParameterSpecification,
            AST container ) {
        for ( int i = valueElements - 1; i > 0; i-- ) {
			if ( i == 1 ) {
				AST op1 = getASTFactory().create( comparisonType, comparisonText );
				AST lhs1 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[0] );
				AST rhs1 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[0] );
				op1.setFirstChild( lhs1 );
				lhs1.setNextSibling( rhs1 );
				container.setFirstChild( op1 );
				AST op2 = getASTFactory().create( comparisonType, comparisonText );
				AST lhs2 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[1] );
				AST rhs2 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[1] );
				op2.setFirstChild( lhs2 );
				lhs2.setNextSibling( rhs2 );
				op1.setNextSibling( op2 );

				// "pass along" our initial embedded parameter node(s) to the first generated
				// sql fragment so that it can be handled later for parameter binding...
				SqlFragment fragment = ( SqlFragment ) lhs1;
				if ( lhsEmbeddedCompositeParameterSpecification != null ) {
					fragment.addEmbeddedParameter( lhsEmbeddedCompositeParameterSpecification );
				}
				if ( rhsEmbeddedCompositeParameterSpecification != null ) {
					fragment.addEmbeddedParameter( rhsEmbeddedCompositeParameterSpecification );
				}
			}
			else {
				AST op = getASTFactory().create( comparisonType, comparisonText );
				AST lhs = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[i] );
				AST rhs = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[i] );
				op.setFirstChild( lhs );
				lhs.setNextSibling( rhs );
				AST newContainer = getASTFactory().create( HqlSqlTokenTypes.AND, "AND" );
				container.setFirstChild( newContainer );
				newContainer.setNextSibling( op );
				container = newContainer;
			}
		}
    }

	protected static String[] extractMutationTexts(Node operand, int count) {
		if ( operand instanceof ParameterNode ) {
			String[] rtn = new String[count];
			for ( int i = 0; i < count; i++ ) {
				rtn[i] = "?";
			}
			return rtn;
		}
		else if ( operand.getType() == HqlSqlTokenTypes.VECTOR_EXPR ) {
			String[] rtn = new String[ operand.getNumberOfChildren() ];
			int x = 0;
			AST node = operand.getFirstChild();
			while ( node != null ) {
				rtn[ x++ ] = node.getText();
				node = node.getNextSibling();
			}
			return rtn;
		}
		else if ( operand instanceof SqlNode ) {
			String nodeText = operand.getText();
			if ( nodeText.startsWith( "(" ) ) {
				nodeText = nodeText.substring( 1 );
			}
			if ( nodeText.endsWith( ")" ) ) {
				nodeText = nodeText.substring( 0, nodeText.length() - 1 );
			}
			String[] splits = StringHelper.split( ", ", nodeText );
			if ( count != splits.length ) {
				throw new HibernateException( "SqlNode's text did not reference expected number of columns" );
			}
			return splits;
		}
		else {
			throw new HibernateException( "dont know how to extract row value elements from node : " + operand );
		}
	}

	protected Type extractDataType(Node operand) {
		Type type = null;
		if ( operand instanceof SqlNode ) {
			type = ( ( SqlNode ) operand ).getDataType();
		}
		if ( type == null && operand instanceof ExpectedTypeAwareNode ) {
			type = ( ( ExpectedTypeAwareNode ) operand ).getExpectedType();
		}
		return type;
	}

	public Type getDataType() {
		// logic operators by definition resolve to booleans
		return Hibernate.BOOLEAN;
	}

	/**
	 * Retrieves the left-hand operand of the operator.
	 *
	 * @return The left-hand operand
	 */
	public Node getLeftHandOperand() {
		return ( Node ) getFirstChild();
	}

	/**
	 * Retrieves the right-hand operand of the operator.
	 *
	 * @return The right-hand operand
	 */
	public Node getRightHandOperand() {
		return ( Node ) getFirstChild().getNextSibling();
	}
}

//$Id: Z.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.xml.hbm;

/**
 * @author Emmanuel Bernard
 */
public interface Z extends java.io.Serializable {
  public Integer getZId();

  public void setZId(Integer zId);

  public B getB();

  public void setB(B b);
}

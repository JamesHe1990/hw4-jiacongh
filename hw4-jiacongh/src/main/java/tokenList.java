

/* First created by JCasGen Fri Oct 25 15:41:57 EDT 2013 */

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringList;


/** 
 * Updated by JCasGen Fri Oct 25 15:41:57 EDT 2013
 * XML source: C:/Users/James_He/git/hw4-jiacongh/hw4-jiacongh/src/main/resources/descriptors/retrievalsystem/Document.xml
 * @generated */
public class tokenList extends StringList {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(tokenList.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected tokenList() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public tokenList(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public tokenList(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
}

    
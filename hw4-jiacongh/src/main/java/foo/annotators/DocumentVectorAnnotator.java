package foo.annotators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.NonEmptyStringList;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

//import edu.cmu.deiis.types.Token;
import foo.typesystems.Token;
import foo.typesystems.Document;
import foo.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {
  public ArrayList<String> stopWordList; 

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}
	/**
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		constructStopWordList();
		//System.out.println("docText:  "+docText);
		//TO DO: construct a vector of tokens and update the tokenList in CAS
		String[]tok = docText.split(" ");
		//String[] tok = filtStopWord(tokenList);
		ArrayList <Token> al = new ArrayList<Token>();
		ArrayList <String> temp = new ArrayList<String>();
		int freq[]=new int[tok.length];
		int j=0;
		for(int i = 0;i<tok.length;i++){
		  String str = tok[i].toLowerCase();
		  if(!temp.contains(str)){
		    temp.add(str);
		    freq[j]=1;
		    j++;
		  }
		  else{
		    int k=temp.indexOf(str);		    
		    freq[k]++;		    
		  }
		}
		for (int i=0;i<j;i++) {
		  String str = temp.get(i);
		  Token t = new Token(jcas);
		  t.setText(str);
		  t.setFrequency(freq[i]);
		  //System.out.println("token:"+str+"\tfrequency:"+freq[i]);
		  t.addToIndexes();
      jcas.addFsToIndexes(t);
      al.add(t);
		}
		FSList tokenAL = Utils.fromCollectionToFSList(jcas, al);
    doc.setTokenList(tokenAL);
	}
	
	
	
	public String[] filtStopWord(String[] str) throws FileNotFoundException{
	  BufferedReader reader = new BufferedReader(new FileReader("stopwords.txt")); 
	  
	  return str;
	}
	
	public void constructStopWordList(){
    try {
      BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/stopwords.txt"));
      String str;
      try {
        while((str=br.readLine())!=null){
         // stopWordList.add(str);
          }
      } catch (IOException e) {
      }
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }     
  }
	

}

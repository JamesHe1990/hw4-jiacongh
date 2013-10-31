package foo.casconsumers;

import java.awt.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyStringList;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import foo.typesystems.Document;
import foo.typesystems.Token;
import foo.utils.Utils;


public class RetrievalEvaluator extends CasConsumer_ImplBase {

	/** query id number **/
 
  public ArrayList<String> stopWordList;
  public HashMap<Integer,String>qst ;
  public HashMap<Integer,ArrayList<String>>asw;
  /** query and text relevant values **/
	public ArrayList<Integer> relList;
  public ArrayList<Integer> qIdList;
		
	public void initialize() throws ResourceInitializationException {

		qIdList = new ArrayList<Integer>();

		relList = new ArrayList<Integer>();
		
		stopWordList = new ArrayList<String>();
		qst= new HashMap<Integer,String>();
		asw= new HashMap<Integer,ArrayList<String>>();

	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}
		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
		if (it.hasNext()) {
			Document doc = (Document) it.next();
			//Make sure that your previous annotators have populated this in CAS
			qIdList.add(doc.getQueryID());
			relList.add(doc.getRelevanceValue());	
			
			if(doc.getRelevanceValue()==99){
			  int x = doc.getQueryID();
			  qst.put(x, doc.getText());
			}
			else{
			  int x = doc.getQueryID();
			  ArrayList <String> al = asw.get(x);
			  if(al==null){
			    al = new ArrayList<String>();
			  }
			  al.add(doc.getRelevanceValue()+" "+doc.getText());
			  asw.put(x, al);
			}
			//Do something useful here
		}
	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {
	  
		super.collectionProcessComplete(arg0);
		constructStopWordList();
		java.util.Iterator<Integer> iterator = asw.keySet().iterator();
		java.util.Iterator<Integer> iterator1 = qst.keySet().iterator();
		ArrayList<Integer> mrrResult=new ArrayList<Integer>();
		while (iterator.hasNext()&&iterator1.hasNext()){
		  int xx = iterator.next();
		  ArrayList<String> aswSentence =asw.get(xx);
		  String qstSentence = qst.get(iterator1.next());
		  String []qstToken = qstSentence.toLowerCase().split(" ");
		  double temp=0.0;
		  double pos = 0;
		  ArrayList<Double>result=new ArrayList<Double>();
		  for (int i=0;i<aswSentence.size();i++) {
		    String document = aswSentence.get(i);
        String[] aswToken = document.toLowerCase().split(" ");
        aswToken[0]=null;
        double x = compute_Sim(qstToken,aswToken,4);
        result.add(x);
        if(temp<x){
          pos=i;
          temp=x;
        }
      }
		 // int rank[]=new int[result.size()];
		 // int rev[]=new int[result.size()];
		  int flag=0;
		  for(int i=0;i<result.size();i++){
		    String sentence = aswSentence.get(i);
        int rev = Integer.parseInt(sentence.split(" ")[0]);
        if(rev==1) flag=i;
        System.out.println("rel="+rev+"\tqid="+xx+"\tSentence:"+sentence.substring(2)+"\tScore="+result.get(i));
		  }		  
		  int rank=1;
		  for(int i=0;i<result.size();i++){	
		    if (result.get(i)>result.get(flag))
		      rank++;		    
		  }   
		  mrrResult.add(rank);
		  
/*		  for(int i=0;i<result.size();i++){
		    String sentence = aswSentence.get(i);
		    int rev = Integer.parseInt(sentence.split(" ")[0]);
		    if(rev==1){
		      		    }
		  }
*/
		}	
		double metric_mrr = compute_mrr(mrrResult);
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}

	private double compute_mrr(ArrayList<Integer> result) {
		double metric_mrr=0.0;
		double count = 0;
		for (Integer in : result) {
      if(in==1)
        count++;
      else{
        double x = (1.0/(double)in);
        count+=x;
      }
    }
		metric_mrr=count/result.size();

		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		
		return metric_mrr;
	}
	
	public double compute_Sim(String[]str1,String[]str2,int num){//num means to use which similarity
	  double sim = 0.0;
	  ArrayList<String> dictionary= new ArrayList<String>();
	  for (String str : str1) {
      if (str==null||dictionary.contains(str)) continue;
      dictionary.add(str);
    }
	  for (String str : str2) {
      if (str==null||dictionary.contains(str)) continue;
      dictionary.add(str);
    }
	  int str1Freq[] = cal_freq(dictionary,str1);
	  int str2Freq[] = cal_freq(dictionary,str2);
	  switch(num){
	    case 1:
	      sim=CalculateByCos(str1Freq,str2Freq);
	      //System.out.println("cos");
	      break;
	    case 2:
	      sim=CalculateByDice(str1Freq,str2Freq);
	      //System.out.println("dice");
	      break;
	    case 3:
	      sim=CalculateByJac(str1Freq,str2Freq);
        //System.out.println("Jac");	  
        break;
	    case 4:
	      sim=(CalculateByCos(str1Freq,str2Freq)+CalculateByDice(str1Freq,str2Freq)+CalculateByJac(str1Freq,str2Freq))/3;
	  }
	  //sim = CalculateByCos(str1Freq,str2Freq);
	  return sim;
	}

	public int[] cal_freq(ArrayList<String> dict, String str[] ){
	  int length = dict.size();
	  int freq[]=new int[length];
	  for (int i=0;i<length;i++) {
	    String str1 = dict.get(i);
	    int count=0;
      for (String str2 : str) {
        if (str1.equals(str2)) count++;
      }
      freq[i]=count;
    }
	  return freq;
	}
	
	
	 public double CalculateByDice(int[]c1,int[]c2){
	    double a=0;//d1*d2
	    double b=0;//d1平方
	    double c=0;//d2平方
	    for(int i=0;i<c1.length;i++){
	      a+=c1[i]*c2[i];
	      b+=c1[i]*c1[i];
	      c+=c2[i]*c2[i];
	    }
	    double x=(double)a*2/(double)(b+c);
//	    System.out.println("a="+a+"  b="+b+"  c="+c+"  d="+d+"  x="+x  );
	    return x;
	  }
	  public double CalculateByJac(int[]c1,int[]c2){
	    double a=0;//d1*d2
	    double b=0;//d1平方
	    double c=0;//d2平方
	    for(int i=0;i<c1.length;i++){
	      a+=c1[i]*c2[i];
	      b+=c1[i]*c1[i];
	      c+=c2[i]*c2[i];
	    }
	    double x=(double)a/(double)(b+c-a);
//	    System.out.println("a="+a+"  b="+b+"  c="+c+"  d="+d+"  x="+x  );
	    return x;
	  }
	  public double CalculateByCos(int[]c1,int[]c2){
	    double a=0;//d1*d2
	    double b=0;//d1平方
	    double c=0;//d2平方
	    for(int i=0;i<c1.length;i++){
	      a+=c1[i]*c2[i];
	      b+=c1[i]*c1[i];
	      c+=c2[i]*c2[i];
	    }
	    double x=a/ Math.sqrt(b*c);
	    return x;
	  }
	  public void constructStopWordList(){
	    try {
        BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/stopwords.txt"));
        String str;
        try {
          while((str=br.readLine())!=null){
            stopWordList.add(str);
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

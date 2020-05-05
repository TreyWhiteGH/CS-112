package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		HashMap<String,Occurrence> wordMap = new HashMap<>(1000, 2.0f);  
        Scanner sc = new Scanner(new File(docFile));
        while (sc.hasNext()) 
        {
            String word = sc.next();
            word = getKeyword(word);
            if (word != null) 
            {                                   
                Occurrence occ = wordMap.get(word);
                if (occ == null) 
                {                           
                    occ = new Occurrence(docFile, 1);
                    wordMap.put(word, occ);                                   
                }
                else 
                {                                            
                	occ.frequency++; 
                }
            }
        }
        return wordMap;
	}
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for (Map.Entry<String, Occurrence> entry : kws.entrySet()) 
		{
			
            String keyWord = entry.getKey();
            Occurrence occ = entry.getValue();
            ArrayList<Occurrence> occList = keywordsIndex.get(keyWord);
            if (occList == null) 
            {   
            	occList = new ArrayList<>();           
                occList.add(occ);                  
                keywordsIndex.put(keyWord, occList);   
                        
            }
            else 
            {                                 
                 occList.add(occ);                      
                insertLastOccurrence(occList);  
            }
        }
	}
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) 
	{
		word = word.toLowerCase();
        while (true) 
        {
            if (word.length() == 0) { 
                break;
            }
            char endChar = word.charAt(word.length()-1);
            boolean stop=false;
            switch (endChar)
            {
            	case ',':
            	case '.':
            	case ':':
            	case '?':
            	case ';':
            	case '!':
            		word = word.substring(0, word.length()-1);
            		break;
            	default:
            		stop=true;
            		break;	
            }
            if (stop)
            {
            	break;
            }
        }
        for (int i=0; i < word.length(); i++) 
        {
            char oneChar = word.charAt(i);
            if (!Character.isLetter(oneChar)) 
            {
                word = null;
                break;
            }
        }
        if (word!=null)
        {
        	if ((word.length() == 0) || (noiseWords.contains(word)))
        			{
        				word=null;
        			}
        }
        return word;
	}
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) 
	{
		ArrayList<Integer> midList = new ArrayList<>();
		int lastIndex=occs.size()-1;
        int freq= occs.get(lastIndex).frequency;     
        int bottom = 0;
        int top = occs.size() - 2; 
        int mid;
        int index;
        //boolean stop=false;
        while (true) 
        {                                             
            mid = (top + bottom) / 2;
            midList.add(mid);
            Occurrence occMid = occs.get(mid);
            if (occMid.frequency == freq) 
            {     
                index = mid;
                //stop=true;
                break;
            }
            else if (occMid.frequency < freq) 
            {  
                top = mid - 1;                          
                if (bottom > top) 
                {
                    index = mid;
                    //stop=true;
                    break;
                }
            }
            else 
            {                                                  
                bottom = mid + 1;                          
                if (bottom > top) 
                {
                    index = mid + 1;
                   // stop=true;
                    break;
                }
            }
        }
        if (index != occs.size() - 1) 
        {
            Occurrence temp = occs.get(occs.size()-1);              
            occs.remove(occs.size()-1);                            
            occs.add(index, temp);                          
        }
        return midList;
	}
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {     
		ArrayList<String> ret=new ArrayList<String>();
		ArrayList<Occurrence> temp1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> temp2 = new ArrayList<Occurrence>();
	       if (keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)) 
	       {
	    	   for (int i = 0; i < keywordsIndex.get(kw1).size() && i < 5; i++)
	            {
	                temp1.add(keywordsIndex.get(kw1).get(i));
	            }
	    	   for (int i = 0; i < keywordsIndex.get(kw2).size() && i < 5; i++)
	            {
	                temp2.add(keywordsIndex.get(kw2).get(i));
	            }
	    	   return simmer(temp1,temp2);
	    	   
	       }
	       else if (keywordsIndex.containsKey(kw1) || keywordsIndex.containsKey(kw2)) 
	       {
	    	   if (keywordsIndex.containsKey(kw1))
	    	   {
	    		   for (int i = 0; i < keywordsIndex.get(kw1).size() && i < 5; i++)
		            {
		                ret.add(keywordsIndex.get(kw1).get(i).document);
		            }
	    	   }
	    	   else if(keywordsIndex.containsKey(kw2)) 
	    	   {
	    		   for (int i = 0; i < keywordsIndex.get(kw2).size() && i < 5; i++)
		            {
		                ret.add(keywordsIndex.get(kw2).get(i).document);
		            }
	    	   }
	       }
	       else
	       {
	    	   return null;
	       }
	       return ret; 
	    }
	private ArrayList<String> simmer(ArrayList<Occurrence> temp1, ArrayList<Occurrence> temp2)
	{
		ArrayList<String> ret=new ArrayList<String>();
		ArrayList<Occurrence> retOcc=new ArrayList<Occurrence>();
		ArrayList<String> retStr=new ArrayList<String>();
		for (int i=0;i<temp1.size();i++)
		{
			retOcc.add(temp1.get(i));
		}
		for (int i=0;i<temp2.size();i++)
		{
			retOcc.add(temp2.get(i));

		}
		for (int i = 0; i < retOcc.size(); i++) {
            int pos = i;
            for (int j = i; j < retOcc.size(); j++) {
                if (retOcc.get(j).frequency > retOcc.get(pos).frequency)
                    pos = j;
            }
            Occurrence max = retOcc.get(pos);
            retOcc.set(pos, retOcc.get(i));
            retOcc.set(i, max);
        }
		for (int i=0;i<retOcc.size();i++)
		{
			if (!retStr.contains(retOcc.get(i).document))
					{
						retStr.add(retOcc.get(i).document);
					}
		}
		for (int i=0;i<retStr.size() && i<5;i++)
		{
			ret.add(retStr.get(i));
		}
		return ret;
	}
    } 
/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 *  Searches an index for results of a query.
 */
public class Searcher {

    /** The index to be searched by this Searcher. */
    Index index;

    /** The k-gram index to be searched by this Searcher */
    KGramIndex kgIndex;
    
    /** Constructor */
    public Searcher( Index index, KGramIndex kgIndex ) {
        this.index = index;
        this.kgIndex = kgIndex;
    }

    /**
     *  Searches the index for postings matching the query.
     *  @return A postings list representing the result of the query.
     */
    public PostingsList ListIntersect(PostingsList list1, PostingsList list2) {
    	System.out.println("***Inside Intersect Algorithm***");
    	PostingsList answer = new PostingsList();
    	ArrayList<PostingsEntry> pe1 = list1.getEntries();
    	ArrayList<PostingsEntry> pe2 = list2.getEntries();
    	int i = 0;
    	int j = 0;
    	
   	
    	while(i < pe1.size() && j < pe2.size()) {
    		if(pe1.get(i).docID == pe2.get(j).docID) {
    			PostingsEntry e = new PostingsEntry(pe1.get(i).docID);
    			answer.add(e);
    			i++;
    			j++;
    		}
    		else if(pe1.get(i).docID < pe2.get(j).docID) {
    			i++;
    		}
    		else {
    			j++;
    		}
    	}
    	System.out.println("answer:" + answer);
    	return answer;
    }
    
    public PostingsList positionalIntersection(PostingsList pl1, PostingsList pl2) {
    	System.out.println("***Inside Positional Intersection***");
    	PostingsList answer = new PostingsList();         
        ListIterator<PostingsEntry> itr1 = pl1.getEntries().listIterator();
        ListIterator<PostingsEntry> itr2 = pl2.getEntries().listIterator();
        PostingsEntry itr1Value = null;
        PostingsEntry itr2Value = null;
        
        if (itr1.hasNext()) {
            itr1Value = itr1.next();
        } else {
            return answer;
        }

        if (itr2.hasNext()) {
            itr2Value = itr2.next();
        } else {
            return answer;
        }

        while (true) {
            if(itr1Value.docID == itr2Value.docID)
            {
                ArrayList<Integer> pp1 = itr1Value.offsets;
                ArrayList<Integer> pp2 = itr2Value.offsets;
                ListIterator<Integer> itrOffset1 = pp1.listIterator();
                ListIterator<Integer> itrOffset2 = pp2.listIterator();
                Integer itrOffset1Value = null;
                Integer itrOffset2Value = null;
                            
                if(itrOffset1.hasNext()) {
                    itrOffset1Value = itrOffset1.next();
                } else {
                    break;
                }
                if(itrOffset2.hasNext()) {
                    itrOffset2Value = itrOffset2.next();          
                } else {
                    break;
                }

                while (true) {
                    if((itrOffset2Value - itrOffset1Value) == 1) {
                        //offsetList.add(itrOffset2Value);
                        PostingsEntry temp = answer.searchEntry(itr1Value.docID);
                        if (temp  == null) {
                            answer.addWithOffset(itr1Value.docID, itrOffset2Value);
                        } else {
                            temp.offsets.add(itrOffset2Value);
                        }

                        if (itrOffset1.hasNext()) {
                            itrOffset1Value = itrOffset1.next();
                        } else {
                            break;
                        }

                        if (itrOffset2.hasNext()) {
                            itrOffset2Value = itrOffset2.next();
                        } else {
                            break;
                        }
                        
                    } else if(itrOffset2Value > (itrOffset1Value) ) {
                        if (itrOffset1.hasNext()) {
                            itrOffset1Value = itrOffset1.next();
                        } else {
                            break;
                        }                        
                    }else  {
                        if (itrOffset2.hasNext()) {
                            itrOffset2Value = itrOffset2.next();
                        } else {
                            break;
                        }
                    }
                }
                
                if(itr1.hasNext()) {
                    itr1Value = itr1.next();
                } else {
                    break;
                }  

                if(itr2.hasNext())  {
                    itr2Value = itr2.next();
                } else {
                    break;
                }
            } else if(itr1Value.docID < itr2Value.docID) {
                if(itr1.hasNext()) {
                    itr1Value = itr1.next();
                } else {
                    break;
                }                
            } else {
                if(itr2.hasNext())  {
                    itr2Value = itr2.next();
                } else {
                    break;
                }
            }
         }
    
        return answer;
    }
    
    public PostingsList search( Query query, QueryType queryType, RankingType rankingType ) { 
    	
    	System.out.println("***Inside Search***");
    	System.out.println("QueryType:" + queryType);
    	
    	if((query.size() > 1) && (queryType == QueryType.INTERSECTION_QUERY)) {
    		System.out.println("Inside INTERSECTION_QUERY");
    		String[] queryTerms = new String[query.queryterm.size()];
    		PostingsList[] list = new PostingsList[query.queryterm.size()];
    		
    		for(int i = 0; i < query.queryterm.size(); i++) {
    			list[i] = new PostingsList();
    			queryTerms[i] = query.queryterm.get(i).term;
    			list[i] = index.getPostings(queryTerms[i]);
    			System.out.println("list:" + list[i]);
    		}
        	PostingsList p = new PostingsList();
    		p = list[0];
    		for(int i = 0; i < list.length; i++) {
    			p = ListIntersect(p,list[i]);
    		}
    		return p;
    	}
    	else if(query.queryterm.size() > 1 && (queryType==QueryType.PHRASE_QUERY)) {
    		String[] tokens = new String[query.queryterm.size()];
    		PostingsList[] arr_pl = new PostingsList[query.queryterm.size()];
    		
    		for(int i=0; i<query.queryterm.size(); i++) {
    			tokens[i] = query.queryterm.get(i).term;
    			arr_pl[i] = index.getPostings(tokens[i]);
    		}
    		
    		PostingsList post_list = arr_pl[0];
    		for(int i=1;i<arr_pl.length;i++) {
    			post_list = positionalIntersection(post_list, arr_pl[i]);
    		}
    		return post_list;
     	}
        else
        {
            System.out.println("Searching for: " + query.queryterm.get(0).term);
            String token = query.queryterm.get(0).term;
            PostingsList pl = index.getPostings(token);
            System.out.println("pl:" + pl);
            return pl;
        }   	
    } 
}
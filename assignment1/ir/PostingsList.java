/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
import java.util.HashMap;

public class PostingsList {
    
    /** The postings list */
    private ArrayList<PostingsEntry> list = new ArrayList<PostingsEntry>();
    private HashMap<Integer, PostingsEntry> map = new HashMap<Integer,PostingsEntry>();
    
    public ArrayList<PostingsEntry> getEntries() {
    	return list;
    }


    public HashMap<Integer, PostingsEntry> getMap() {
		return map;
	}

	/** Number of postings in this list. */
    public int size() {
    return list.size();
    }

    /** Returns the ith posting. */
    public PostingsEntry get( int i ) {
    	if(i <= list.size())
    		return list.get(i);
    	else
    		return null;
    }

    // 
    //  YOUR CODE HERE
    //
    
    public void addWithOffset(Integer docID, Integer offset) {
    	PostingsEntry pe = new PostingsEntry(docID);
    	pe.addtoOffset(offset);
    	list.add(pe);
    	map.put(docID,pe);    	
    }
    public void add(PostingsEntry entry) {
    	list.add(entry);
    	map.put(entry.docID, entry);
    }
    public PostingsEntry searchEntry(int docID) {
    	if(map.containsKey(docID)) {
    		return map.get(docID);
    	}
    	else 
    		return null;
    }
    public void addEntry(PostingsEntry entry) {
    	if(list.isEmpty()) {
    		list.add(entry);
    	}
    	else {
    		
    		PostingsEntry beg = list.get(0);
    		PostingsEntry end = list.get(list.size() - 1);
    		
    		if(entry.docID <= beg.docID) {
    			list.add(0, entry);
    			return;
    		}
    		if(entry.docID >= end.docID) {
    			list.add(entry);
    			return;
    		}
    		for (int i = 0; i < list.size(); i++) {
    			PostingsEntry curr = list.get(i);
    			if(entry.docID < curr.docID) {
    				list.add(i, entry);
    				return;
    			}
    		}
    	}
    }
}


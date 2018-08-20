package com.sanguine.webpos.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sanguine.webpos.bean.clsPOSSalesFlashColumns;



public class clsPOSSortMapOnValue
{
    
    public <K extends Comparable, V extends Comparable> Map<K, V> funSortMapOnValues(Map<K, V> map)
    {
	List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
	
	Collections.sort(entries, new Comparator<Map.Entry<K, V>>()
	{
	    
	    @Override
	    public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
	    {
		return o1.getValue().compareTo(o2.getValue());
	    }
	});
	
	// LinkedHashMap will keep the keys in the order they are inserted
	// which is currently sorted on natural ordering
	Map<K, V> sortedMap = new LinkedHashMap<K, V>();
	
	for (Map.Entry<K, V> entry : entries)
	{
	    sortedMap.put(entry.getKey(), entry.getValue());
	}
	
	return sortedMap;
    }
    
    
    public static Comparator<clsPOSSalesFlashColumns> COMPARATOR = new Comparator<clsPOSSalesFlashColumns>()
    	    {
    	        // This is where the sorting happens.
    	        public int compare(clsPOSSalesFlashColumns o1, clsPOSSalesFlashColumns o2)
    	        {
    	         return (int) (o2.getSeqNo() - o1.getSeqNo());
    	        }
    	    };
    
    
    
    
}

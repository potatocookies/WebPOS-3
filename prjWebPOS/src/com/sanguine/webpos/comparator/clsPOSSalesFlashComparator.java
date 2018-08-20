package com.sanguine.webpos.comparator;

import java.util.Comparator;

import com.sanguine.webpos.bean.clsPOSSalesFlashReportsBean;

public class clsPOSSalesFlashComparator 
{
	  public static Comparator<clsPOSSalesFlashReportsBean> COMPARATOR = new Comparator<clsPOSSalesFlashReportsBean>()
	    {
	        // This is where the sorting happens.
	        public int compare(clsPOSSalesFlashReportsBean o1, clsPOSSalesFlashReportsBean o2)
	        {
	         return (int) (o2.getSeqNo() - o1.getSeqNo());
	        }
	    };
}

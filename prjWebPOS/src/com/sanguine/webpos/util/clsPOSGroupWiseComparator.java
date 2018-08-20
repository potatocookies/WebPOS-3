package com.sanguine.webpos.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSGroupWaiseSalesBean;

public class clsPOSGroupWiseComparator  implements Comparator<clsPOSGroupWaiseSalesBean> {

	 private List<Comparator<clsPOSGroupWaiseSalesBean>> listComparators;

	    @SafeVarargs
	    public clsPOSGroupWiseComparator(Comparator<clsPOSGroupWaiseSalesBean>... comparators)
	    {
	        this.listComparators = Arrays.asList(comparators);
	    }

	    @Override
	    public int compare(clsPOSGroupWaiseSalesBean o1, clsPOSGroupWaiseSalesBean o2)
	    {
	        for (Comparator<clsPOSGroupWaiseSalesBean> comparator : listComparators)
	        {
	            int result = comparator.compare(o1, o2);
	            if (result != 0)
	            {
	                return result;
	            }
	        }
	        return 0;
	    }
}

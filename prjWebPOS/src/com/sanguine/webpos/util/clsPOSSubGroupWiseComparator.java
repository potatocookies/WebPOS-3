
package com.sanguine.webpos.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSSubGroupWaiseSalesBean;

public class clsPOSSubGroupWiseComparator  implements Comparator<clsPOSSubGroupWaiseSalesBean> {

	 private List<Comparator<clsPOSSubGroupWaiseSalesBean>> listComparators;

	    @SafeVarargs
	    public clsPOSSubGroupWiseComparator(Comparator<clsPOSSubGroupWaiseSalesBean>... comparators)
	    {
	        this.listComparators = Arrays.asList(comparators);
	    }

	    @Override
	    public int compare(clsPOSSubGroupWaiseSalesBean o1, clsPOSSubGroupWaiseSalesBean o2)
	    {
	        for (Comparator<clsPOSSubGroupWaiseSalesBean> comparator : listComparators)
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




package com.sanguine.webpos.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import com.sanguine.webpos.bean.clsPOSVoidBillReportBean;


public class clsPOSVoidBillReportBeanComparator  implements Comparator<clsPOSVoidBillReportBean> {

	 private List<Comparator<clsPOSVoidBillReportBean>> listComparators;

	    @SafeVarargs
	    public clsPOSVoidBillReportBeanComparator(Comparator<clsPOSVoidBillReportBean>... comparators)
	    {
	        this.listComparators = Arrays.asList(comparators);
	    }

	    @Override
	    public int compare(clsPOSVoidBillReportBean o1, clsPOSVoidBillReportBean o2)
	    {
	        for (Comparator<clsPOSVoidBillReportBean> comparator : listComparators)
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

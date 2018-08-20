
package com.sanguine.webpos.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSRevenueHeadWiseSalesReportBean;


public class clsPOSRevenueHeadWiseComparator  implements Comparator<clsPOSRevenueHeadWiseSalesReportBean> {

	 private List<Comparator<clsPOSRevenueHeadWiseSalesReportBean>> listComparators;

	    @SafeVarargs
	    public clsPOSRevenueHeadWiseComparator(Comparator<clsPOSRevenueHeadWiseSalesReportBean>... comparators)
	    {
	        this.listComparators = Arrays.asList(comparators);
	    }

	    @Override
	    public int compare(clsPOSRevenueHeadWiseSalesReportBean o1, clsPOSRevenueHeadWiseSalesReportBean o2)
	    {
	        for (Comparator<clsPOSRevenueHeadWiseSalesReportBean> comparator : listComparators)
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

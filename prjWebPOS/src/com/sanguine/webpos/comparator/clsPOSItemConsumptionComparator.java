/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSItemWiseConsumption;

/**
 *
 * @author ajjim
 */

public class clsPOSItemConsumptionComparator implements Comparator<clsPOSItemWiseConsumption>
{

    private List<Comparator<clsPOSItemWiseConsumption>> listComparators;

    @SafeVarargs
    public clsPOSItemConsumptionComparator(Comparator<clsPOSItemWiseConsumption>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2)
    {
        for (Comparator<clsPOSItemWiseConsumption> comparator : listComparators)
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
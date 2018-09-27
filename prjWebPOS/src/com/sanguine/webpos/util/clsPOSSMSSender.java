package com.sanguine.webpos.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ajjim
 */
public class clsPOSSMSSender extends Thread
{
	
	
	

	
	
	
	
    private ArrayList<String> mobileNumberList = new ArrayList<String>();
    private String mainSms="";
    //private clsUtility objUtility=new clsUtility();
    
    public clsPOSSMSSender(ArrayList<String> mobileNumberList,String mainSMS)
    {
        this.mobileNumberList=mobileNumberList;
        this.mainSms=mainSMS;
    }

    @Override
    public void run()
    {       
       // boolean isSend = objUtility.funSendBulkSMS(mobileNumberList, mainSms);
       // System.out.println("SMS Sender->"+isSend);
    }
    
    
    
}
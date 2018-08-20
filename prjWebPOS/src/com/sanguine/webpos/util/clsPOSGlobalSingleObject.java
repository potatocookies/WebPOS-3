package com.sanguine.webpos.util;



public class clsPOSGlobalSingleObject {
   private static clsPOSPasswordEncryptDecreat objPasswordEncryptDecreat=null;
   
   private clsPOSGlobalSingleObject(){}
   
   /**
     * @return the objPasswordEncryptDecreat
     */
    public static clsPOSPasswordEncryptDecreat getObjPasswordEncryptDecreat() 
    {
        if (objPasswordEncryptDecreat == null) 
        {
            objPasswordEncryptDecreat = new clsPOSPasswordEncryptDecreat();
        }
        return objPasswordEncryptDecreat;
    }
}
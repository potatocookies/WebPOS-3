/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.bean;


/**
 *
 * @author ajjim
 * 
 * 
 */
public class clsPOSSMSPackDtl
{
    private String strUserId;//
    private String strPassword;//
    private String strSenderId;//
    private String strSMSPack;//NOSMSPACK,Transactional,Propmotional

    public clsPOSSMSPackDtl(String userId, String password,String senderId, String smsPack)
    {
        this.strUserId=userId;
        this.strPassword=password;
        this.strSenderId = senderId;
        this.strSMSPack = smsPack;
    }

    public String getStrSenderId()
    {
        return strSenderId;
    }

    public void setStrSenderId(String strSenderId)
    {
        this.strSenderId = strSenderId;
    }

    public String getStrSMSPack()
    {
        return strSMSPack;
    }

    public void setStrSMSPack(String strSMSPack)
    {
        this.strSMSPack = strSMSPack;
    }

    public String getStrUserId()
    {
        return strUserId;
    }

    public void setStrUserId(String strUserId)
    {
        this.strUserId = strUserId;
    }

    public String getStrPassword()
    {
        return strPassword;
    }

    public void setStrPassword(String strPassword)
    {
        this.strPassword = strPassword;
    }
    
    
}

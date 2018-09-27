<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
 	var fieldName,textValue2="",selectedRowIndex=0,delTableNo="",delbillNo="",delItemcode="", delAmount="",bDate="",delModItemcode="",count=0;
 	var arrVoidedItemDtlList=new Array();

 	$(function() 
 	{
 	   funFillGrid();
 	});
 	
    $(document).ready(function ()
    {
       $("#btnShowSimple").click(function (e)
       {
          ShowDialog(false);
          e.preventDefault();
       });

       $("#btnShowModal").click(function (e)
       {
          ShowDialog(true);
          e.preventDefault();
       });

       $("#btnClose").click(function (e)
       {
          HideDialog();
          e.preventDefault();
       });

       $("#btnSubmit").click(function (e)
       {
          var brand = $("#brands input:radio:checked").val();
          $("#output").html("<b>Your favorite mobile brand: </b>" + brand);
          HideDialog();
          e.preventDefault();
       });

    });

    function ShowDialog(modal)
    {
       $("#overlay").show();
       $("#dialog").fadeIn(300);

       if (modal)
       {
          $("#overlay").unbind("click");
       }
       else
       {
          $("#overlay").click(function (e)
          {
             HideDialog();
          });
       }
    }

    function HideDialog()
    {
       $("#overlay").hide();
       $("#dialog").fadeOut(300);
    }

		
	/**
	* Success Message After Saving Record
	**/
	$(document).ready(function()
	{
		var message='';
		<%if (session.getAttribute("success") != null) 
		{
			if(session.getAttribute("successMessage") != null)
			{%>
				message='<%=session.getAttribute("successMessage").toString()%>';
			    <%
			    session.removeAttribute("successMessage");
			}
			boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
			session.removeAttribute("success");
			if (test) 
			{
				%>alert("Data Saved \n\n"+message);<%
			}
		}%>
	});
	
	
	
	function funMoveSelectedRow(count)
	{
		if(count==1)
			{
				if (selectedRowIndex == 0)
				{
					//do nothing
				}
				else
				{
				  var table = document.getElementById("tblData");
				  var description=table.rows[selectedRowIndex].cells[0].innerHTML;
				  var qty=table.rows[selectedRowIndex].cells[1].innerHTML; 
				  var amt=table.rows[selectedRowIndex].cells[2].innerHTML; 
				  var itemCode=table.rows[selectedRowIndex].cells[3].innerHTML;
				  var description1=table.rows[selectedRowIndex-1].cells[0].innerHTML;
				  var qty1=table.rows[selectedRowIndex-1].cells[1].innerHTML; 
				  var amt1=table.rows[selectedRowIndex-1].cells[2].innerHTML; 
				  var itemCode1=table.rows[selectedRowIndex-1].cells[3].innerHTML;
				  var kot=table.rows[selectedRowIndex].cells[4].innerHTML;
				  var kot1=table.rows[selectedRowIndex-1].cells[4].innerHTML;
				  
				  funMoveRowUp(description,qty,amt,itemCode,kot,selectedRowIndex,description1,qty1,amt1,itemCode1,kot1);
				}
				
			}
			else
			{
				var table = document.getElementById("tblData");
				var rowCount = table.rows.length;
				if(rowCount>0)
				{
					  var table = document.getElementById("tblData");
					  var description=table.rows[selectedRowIndex].cells[0].innerHTML;
					  var qty=table.rows[selectedRowIndex].cells[1].innerHTML; 
					  var amt=table.rows[selectedRowIndex].cells[2].innerHTML; 
					  var itemCode=table.rows[selectedRowIndex].cells[3].innerHTML;
					  var description1=table.rows[selectedRowIndex-1].cells[0].innerHTML;
					  var qty1=table.rows[selectedRowIndex-1].cells[1].innerHTML; 
					  var amt1=table.rows[selectedRowIndex-1].cells[2].innerHTML; 
					  var itemCode1=table.rows[selectedRowIndex-1].cells[3].innerHTML;
					  var kot=table.rows[selectedRowIndex].cells[4].innerHTML;
					  var kot1=table.rows[selectedRowIndex-1].cells[4].innerHTML;
					
					  funMoveRowDown(description,qty,amt,itemCode,kot,selectedRowIndex,description1,qty1,amt1,itemCode1,kot1);
				}
				
			}
	}
	
	
	function funGetSelectedRowIndex(obj)
	{
		 var index = obj.parentNode.parentNode.rowIndex;
		 var table = document.getElementById("tblData");
		 if((selectedRowIndex>0) && (index!=selectedRowIndex))
		 {
			 if(selectedRowIndex%2==0)
			 {
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#A3D0F7';
				 selectedRowIndex=index;
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#ffd966';
				 row.hilite = true;
			 }
			 else
			 {
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#C0E4FF';
				 selectedRowIndex=index;
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#ffd966';
				 row.hilite = true;
	         }
			
		 }
		 else
		 {
			 selectedRowIndex=index;
			 row = table.rows[selectedRowIndex];
			 row.style.backgroundColor='#ffd966';
			 row.hilite = true;
		 }
		 

		   
	}
	
	
	function funMoveRowDown(description,qty,amt,itemCode,kot,rowCount,description1,qty1,amt1,itemCode1,kot1)
	{
		var table = document.getElementById("tblData");
	    table.deleteRow(rowCount);
	    var row = table.insertRow(rowCount+1);
	    row = table.rows[rowCount+1];
		
		var nameArr = description.split('value=');
		var name=nameArr[1].split('onclick=');
		var Description=name[0].substring(1, (name[0].length-2));
		var qtyArr = qty.split('value=');
		var Qty=qtyArr[1].split('onclick=');
		var quantity=Qty[0].substring(1, (Qty[0].length-2));
		var amtArr = amt.split('value=');
		var amtStock=amtArr[1].split('onclick=');
		var Amount=amtStock[0].substring(1, (amtStock[0].length-2));
		var itemCodeArr = itemCode.split('value=');
		var code=itemCodeArr[1].split('onclick=');
		var ItemCode=code[0].substring(1, (code[0].length-2));
		
		var kotArr = kot.split('value=');
		var kotcode=kotArr[1].split('onclick=');
		var kotNo=kotcode[0].substring(1, (kotcode[0].length-2));
		if(document.all("cbSGSel."+rowCount).checked==true)
		{
			
		}
		
		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+Description+"\" value='"+Description+"'  onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+quantity+"\" value='"+quantity+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+Amount+"\" value='"+Amount+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+ItemCode+"\" value='"+ItemCode+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+kotNo+"\" value='"+kotNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		
		
		
		
	
		  row = table.rows[rowCount+1];
		  row.style.backgroundColor='#ffd966';
		  selectedRowIndex=rowCount+1;
		  
		
		var nextNameArr = description1.split('value=');
		var nextName=nextNameArr[1].split('onclick=');
		var nextDescription=nextName[0].substring(1, (nextName[0].length-2));
		var nextQtyArr = qty1.split('value=');
		var nextQty=nextQtyArr[1].split('onclick=');
		var nextquantity=nextQty[0].substring(1, (nextQty[0].length-2));
		var nextAmtArr = amt1.split('value=');
		var nextAmtStock=nextAmtArr[1].split('onclick=');
		var nextAmount=nextAmtStock[0].substring(1, (nextAmtStock[0].length-2));
		var nextItemCodeArr = itemCode1.split('value=');
		var nextCode=nextItemCodeArr[1].split('onclick=');
		var nextItemCode=nextCode[0].substring(1, (nextCode[0].length-2));
		
		var nextkotArr = kot1.split('value=');
		var nextkot=nextkotArr[1].split('onclick=');
		var nextkotNo=nextkot[0].substring(1, (nextkot[0].length-2));
		var row1 = table.insertRow(rowCount);
		
		if(document.all("cbSGSel."+rowCount).checked==true)
		{
			
		}
		row1.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+nextDescription+"\" value='"+nextDescription+"'  onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextquantity+"\" value='"+nextquantity+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextAmount+"\" value='"+nextAmount+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextItemCode+"\" value='"+nextItemCode+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextkotNo+"\" value='"+nextkotNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";	    
		table.deleteRow(rowCount+1);
		  
		  
	}
	
    function funMoveRowUp(description,qty,amt,itemCode,kot,rowCount,description1,qty1,amt1,itemCode1,kot1)
	{
		var table = document.getElementById("tblData");
	    table.deleteRow(rowCount);
	    var row = table.insertRow(rowCount-1);
	    row = table.rows[rowCount-1];
		
		var nameArr = description.split('value=');
		var name=nameArr[1].split('onclick=');
		var Description=name[0].substring(1, (name[0].length-2));
		var qtyArr = qty.split('value=');
		var Qty=qtyArr[1].split('onclick=');
		var quantity=Qty[0].substring(1, (Qty[0].length-2));
		var amtArr = amt.split('value=');
		var amtStock=amtArr[1].split('onclick=');
		var itemCodeArr = itemCode.split('value=');
		var code=itemCodeArr[1].split('onclick=');
		var ItemCode=code[0].substring(1, (code[0].length-2));
		var kotArr = kot.split('value=');
		var kotcode=kotArr[1].split('onclick=');
		var kotNo=kotcode[0].substring(1, (kotcode[0].length-2));
		

		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+Description+"\" value='"+Description+"'  onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+quantity+"\" value='"+quantity+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+Amount+"\" value='"+Amount+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+ItemCode+"\" value='"+ItemCode+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+kotNo+"\" value='"+kotNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(5).innerHTML= "<input id=\"cbSGSel."+(rowCount)+"\" type=\"checkbox\" checked=\""+checkBox+"\" name=\"BillGroupthemes\" value='"+rowData[3]+"' class=\"SGCheckBoxClass\" />";
			   
	    row = table.rows[rowCount-1];
		row.style.backgroundColor='#ffd966';
		selectedRowIndex=rowCount-1;
		    
		var nextNameArr = description1.split('value=');
		var nextName=nextNameArr[1].split('onclick=');
		var nextDescription=nextName[0].substring(1, (nextName[0].length-2));
		var nextQtyArr = qty1.split('value=');
		var nextQty=nextQtyArr[1].split('onclick=');
		var nextquantity=nextQty[0].substring(1, (nextQty[0].length-2));
		var nextAmtArr = amt1.split('value=');
		var nextAmtStock=nextAmtArr[1].split('onclick=');
		var nextAmount=nextAmtStock[0].substring(1, (nextAmtStock[0].length-2));
		var nextItemCodeArr = itemCode1.split('value=');
		var nextCode=nextItemCodeArr[1].split('onclick=');
		var nextItemCode=nextCode[0].substring(1, (nextCode[0].length-2)); 
		var nextkotArr = kot1.split('value=');
		var nextkot=nextkotArr[1].split('onclick=');
		var nextkotNo=nextkot[0].substring(1, (nextkot[0].length-2));
		var row1 = table.insertRow(rowCount+1);
		 
		row1.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+nextDescription+"\" value='"+nextDescription+"'  onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextquantity+"\" value='"+nextquantity+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextAmount+"\" value='"+nextAmount+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextItemCode+"\" value='"+nextItemCode+"' onclick=\"funGetSelectedRowIndex(this)\"/>";		    
		row1.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextkotNo+"\" value='"+nextkotNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(5).innerHTML= "<input id=\"cbSGSel."+(rowCount)+"\" type=\"checkbox\" checked=\""+checkBox+"\" name=\"BillGroupthemes\" value='"+rowData[3]+"' class=\"SGCheckBoxClass\" />";
		table.deleteRow(rowCount);
	}


	function funFillGrid()
	{
	    var searchUrl="";
	    var tableName="";
	   
	    
	    searchUrl=getContextPath()+"/funGetBillList.html?";
		$.ajax({
		        type: "GET",
		        url: searchUrl,
		        async:false,
		        data:{
		        	tableName:tableName,
		       
				},
			    success: function(response)
			    {
			    	$.each(response, function(i,item)
					{
			    		funAddFullRow(response);
					});
			    },
			    error: function(jqXHR, exception) {
		            if (jqXHR.status === 0) {
		                alert('Not connect.n Verify Network.');
		            } else if (jqXHR.status == 404) {
		                alert('Requested page not found. [404]');
		            } else if (jqXHR.status == 500) {
		                alert('Internal Server Error [500].');
		            } else if (exception === 'parsererror') {
		                alert('Requested JSON parse failed.');
		            } else if (exception === 'timeout') {
		                alert('Time out error.');
		            } else if (exception === 'abort') {
		                alert('Ajax request aborted.');
		            } else {
		                alert('Uncaught Error.n' + jqXHR.responseText);
		            }
		        }
		      });
	    
	    
	   
	}
	
	function funAddFullRow(data){
		$('#tblDataFillGrid tbody').empty();
		var table = document.getElementById("tblDataFillGrid");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"KOT NO.\" value=Bill No >";
		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"TB Name\" value=BillDate >";
		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Waiter Name\"  value=Amount >";
		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Take Away\" value=TableName >";
		
		rowCount++;
	    for(var i=0;i<data.length;i++){
	    	row = table.insertRow(rowCount);
	    	var rowData=data[i];
	    	
	    	for(var j=0;j<rowData.length;j++){
	    		
	    		if(j==2)
    	    	{
    	    	row.insertCell(j).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"7%\" id=\""+rowData[j]+"\" style=\"text-align: right;\" value='"+rowData[j]+"' onclick=\"funGetSelectedRowData(this)\"/>";
    	    	}
	    	    else
	    	    {
	    	    	row.insertCell(j).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowData[j]+"\" value='"+rowData[j]+"' onclick=\"funGetSelectedRowData(this)\"/>";    
	    	    }	
	    	}
	    	rowCount++;
	    }		
	}
	
	
	
	 var deletedIndex;
     function funGetSelectedRowData(obj)
     {
    	deletedIndex="";
    	var index = obj.parentNode.parentNode.rowIndex;
    	var tableName = document.getElementById("tblDataFillGrid");
       	var dataBilNo= tableName.rows[index].cells[0].innerHTML; 
        var btnBackground=dataBilNo.split('value=');
        var billData=btnBackground[1].split("onclick");
        var bill=billData[0].substring(1, (billData[0].length-2));
    	
        var dataTableName= tableName.rows[index].cells[3].innerHTML; 
        var btnBackgroundTableName=dataTableName.split('value=');
        var tabledata=btnBackgroundTableName[1].split("onclick");
        delTableNo=tabledata[0].substring(1, (tabledata[0].length-2));
     delbillNo=bill
        $("#lblBillNo").text(bill);
        searchUrl=getContextPath()+"/fillBillDtlData.html?";
		$.ajax({
		        type: "GET",
		        url: searchUrl,
		        async:false,
		        data:"billNo="+bill,
			    success: function(response)
			    {
					
			    	$.each(response, function(i,item)
					{
	
			    		funAddFullRowBillDtl(response.listFillGrid,bill,response.totalAmount,response.taxAmt,response.userCreated,response.subTotalAmt);
					});
			    },
			    error: function(jqXHR, exception) {
		            if (jqXHR.status === 0) {
		                alert('Not connect.n Verify Network.');
		            } else if (jqXHR.status == 404) {
		                alert('Requested page not found. [404]');
		            } else if (jqXHR.status == 500) {
		                alert('Internal Server Error [500].');
		            } else if (exception === 'parsererror') {
		                alert('Requested JSON parse failed.');
		            } else if (exception === 'timeout') {
		                alert('Time out error.');
		            } else if (exception === 'abort') {
		                alert('Ajax request aborted.');
		            } else {
		                alert('Uncaught Error.n' + jqXHR.responseText);
		            }
		        }
		      });
	    
	 }
     
     function funAddFullRowBillDtl(data,bill,totalAmount,taxAmt,userCreated,subTotalAmt){
 		$('#tblData tbody').empty()
 		var table = document.getElementById("tblData");
 		var rowCount = table.rows.length;
 		var row = table.insertRow(rowCount);
 		for(var i=0;i<data.length;i++)
 		{
 	    	row = table.insertRow(rowCount);
 	    	var rowData=data[i];
 	    	row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"35%\" id=\"txtItemName."+ (rowCount) +"\" style=\"text-align: left\" value='"+rowData[0]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
 	    	row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"5%\" id=\"txtQty."+ (rowCount) +"\" style=\"text-align: right\" value='"+rowData[1]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
 	    	row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"7%\" id=\"txtAmount."+ (rowCount) +"\" style=\"text-align: right\" value='"+rowData[2]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
 	    	row.insertCell(3).innerHTML= "<input type=\"hidden\" class=\"Box \" size=\"0%\" id=\"txtItemCode."+ (rowCount) +"\" value='"+rowData[3]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
 	    	row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"11%\" id=\"txtKOT."+ (rowCount) +"\"style=\"text-align: left\" value='"+rowData[4]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
 	    	row.insertCell(5).innerHTML= "<input type=\"button\" class=\"deletebutton\" size=\"5%\" style=\"text-align: center;width:100%;font-size: 8px\" value = \"Del\" onClick=\"Javacsript:funDeleteRow(this)\"/>";
            rowCount++;
 	    }
	  	$("#lblBillNo").text(bill);
	  	$("#lblUserCreated").text(userCreated);
    	$("#lblTax").text(taxAmt.toFixed(2));
    	$("#lblSubTotlal").text(subTotalAmt.toFixed(2));
    	$("#lblTotal").text(totalAmount.toFixed(2));
 	}
     


    function funVoidItems()
	 {
	    var taxAmt=$("#lblTax").text();
	    var remarks = prompt("Enter Remarks", "");
    	var reasonCode=$("#cmbDocType").val();
      

    	searchUrl=getContextPath()+"/voidItem.html?";
		$.ajax({
			
			type: "POST",
	        url: searchUrl,
	        dataType: "text",
	        async:true,
	        data:"delbillNo="+delbillNo+"&delTableNo="+delTableNo+"&remarks="+remarks+"&reasonCode="+reasonCode+"&taxAmt="+taxAmt+"&voidedItemList="+arrVoidedItemDtlList,
	        success: function(response)
	        {
					if(response)
					{
				       alert("Void Bill SucessFully");
				       arrVoidedItemDtlList=new Array();
				       location.reload(true);
					}
				},
			    error: function(jqXHR, exception) {
		            if (jqXHR.status === 0) {
		                alert('Not connect.n Verify Network.');
		            } else if (jqXHR.status == 404) {
		                alert('Requested page not found. [404]');
		            } else if (jqXHR.status == 500) {
		                alert('Internal Server Error [500].');
		            } else if (exception === 'parsererror') {
		                alert('Requested JSON parse failed.');
		            } else if (exception === 'timeout') {
		                alert('Time out error.');
		            } else if (exception === 'abort') {
		                alert('Ajax request aborted.');
		            } else {
		                alert('Uncaught Error.n' + jqXHR.responseText);
		            }
		        }
		      });
	
	 }
 	
    
 	//Function to Delete Selected Row From Grid
	function funDeleteRow(obj)
	{
 			
 		var table = document.getElementById("tblData");
 		
 		var originalQty=obj.parentNode.parentNode.children[1].firstChild.defaultValue;
		 var amount = obj.parentNode.parentNode.children[2].firstChild.defaultValue;
		 var rate = amount/originalQty;
		 var qty="",newamount="",person="";
		    if(originalQty>1)
		    {
		    person = prompt("Please enter quantity to void:", "");
		    }
		    else
		    {
	    	person = 1;
		    }	
		    if (person != null || person != "") 
		    {
		    	qty = parseFloat(originalQty) - parseFloat(person) ;
		    }
		    else
		    {
		    	qty = parseFloat(originalQty) - parseFloat(obj.parentNode.parentNode.children[1].firstChild.defaultValue);
		    }
		    var amt = qty * rate;
		    newamount = rate*person;
		    if(qty!="" || qty > 0)
		    {
		    	obj.parentNode.parentNode.children[1].firstChild.defaultValue=qty;
		    	obj.parentNode.parentNode.children[2].firstChild.defaultValue=amt;
				var voidedItemDtl=obj.parentNode.parentNode.children[3].firstChild.defaultValue+"#"+obj.parentNode.parentNode.children[0].firstChild.defaultValue+"#"+obj.parentNode.parentNode.children[3].firstChild.defaultValue+"#"+qty+"#"+amt+"#"+person+"#"+newamount;
			    arrVoidedItemDtlList[count]=voidedItemDtl;
			    count++;	
		    }
		    else if(originalQty==1)
		    {
		    	obj.parentNode.parentNode.children[1].firstChild.defaultValue=qty;
		    	obj.parentNode.parentNode.children[2].firstChild.defaultValue=amt;
				var voidedItemDtl=obj.parentNode.parentNode.children[3].firstChild.defaultValue+"#"+obj.parentNode.parentNode.children[0].firstChild.defaultValue+"#"+obj.parentNode.parentNode.children[3].firstChild.defaultValue+"#"+originalQty+"#"+amount+"#"+person+"#"+newamount;
			    arrVoidedItemDtlList[count]=voidedItemDtl;
			    count++;	
		    	 table.deleteRow(deletedIndex);
		    }	
		  	
	}
 	
 	function funFullVoidBill()
 	{
 	   var billNo=$("#lblBillNo");
 	   var remarks = prompt("Enter Remarks", "");
   	   var reasonCode=$("#cmbDocType").val();
   	
   	  searchUrl=getContextPath()+"/fullVoidBillButtonClick.html?";
	   $.ajax({
	        type: "GET",
	        url: searchUrl,
	        async:false,
	        data:"billNo="+delbillNo+"&remarks="+remarks+
	        "&reasonCode="+reasonCode,
	        
		    success: function(response)
		    {
		    	funNextFillGrid();
		    	alert("Void Bill SucessFully");
				},
		    error: function(jqXHR, exception) {
	            if (jqXHR.status === 0) {
	                alert('Not connect.n Verify Network.');
	            } else if (jqXHR.status == 404) {
	                alert('Requested page not found. [404]');
	            } else if (jqXHR.status == 500) {
	                alert('Internal Server Error [500].');
	            } else if (exception === 'parsererror') {
	                alert('Requested JSON parse failed.');
	            } else if (exception === 'timeout') {
	                alert('Time out error.');
	            } else if (exception === 'abort') {
	                alert('Ajax request aborted.');
	            } else {
	                alert('Uncaught Error.n' + jqXHR.responseText);
	            }
	        }
	      });
    
	

 	}
     

 	function funNextFillGrid()
 	{
 		
 		$("#lblBillNo").text("");
	  	$("#lblUserCreated").text("");
    	$("#lblTax").text("0");
    	$("#lblSubTotlal").text("0");
    	$("#lblTotal").text("0");
    	$('#tblData tbody').empty();
 		funFillGrid();
 	}
 	
 	
   
</script>



</head>
<body>
       
     <div id="formHeading">
		<label>POS Void Bill </label>
			</div>


	<s:form name="Void Bill" method="POST" action="" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;" >
	   
	   <div class="title">
	
			<div style=" width: 50%; float:left; border-collapse: separate; ">
			
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
				    <div class="element-input col-lg-6" style="width: 20%;"> 
	    				<label class="title">Bill No.</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 40%;">
	    				<label id="lblBillNo" />
	    			</div>
	    		 	<div class="element-input col-lg-6" style="width: 15%;margin-left: -30px;"> 
	    				<label class="title">User</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 40%;">
	    				<label id="lblUserCreated" >
	    			</div>
	    		</div>
	    			
	    		<div class="row" style="background-color: #fff;margin-bottom: 10px;">
		    	 	<div class="element-input col-lg-6" style="width: 26%;margin-left: -15px;"> 
		   				<label class="title">Reason</label>
		   			</div>
		  		 	<div class="element-input col-lg-6" style="width: 75%;">
		    				<s:select path="strReson" items="${listReson}" id="cmbDocType" style="margin-bottom: 10px;"></s:select>
		 		    </div>
		 		    
			   </div>
		    		
		       <div style="border: 1px solid #ccc; display: block; height: 400px; margin: auto; overflow-x: scroll; overflow-y: scroll;">
			 		
			 		<table style="width: 100%;overflow: scroll;background: #2FABE9;color: white;">
						<thead>
						  	<tr >
								<td style="border-right: 1px solid black;width:50%">Description</td>
								<td style="border-right: 1px solid black;width:12%">Qty</td>
								<td style="border-right: 1px solid black;width:10%">Amount</td>
<!-- 								<td style="border-right: 1px solid black;width:0%">Modifier</td> -->
								<td style="border-right: 1px solid black;width:20%">KOT</td>
								<td style="width:3%">Del</td>
						   </tr>
						</thead>
					</table>
					
					<table id="tblData"	style="width: 100%">
							<tbody></tbody>
							
					  </table>
					     
    		   </div>
    		    
    		    <br/>
    		    
    		   <div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
	    			<div class="element-input col-lg-6" style="width: 20%;"> 
	    				<label class="title">Tax</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 35%;">
	    				<label id="lblTax" style="text-transform: uppercase; width:100px; height:25px;" />
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 20%;"> 
	    				<label class="title">SubTotal</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 35%;">
	    				<label  id="lblSubTotlal" style="text-transform: uppercase; width:100px; height:25px;" />
	    			</div>
    		   </div>
    		  
    		  <br/>
    		  
    		   <div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
	    	  		<div class="element-input col-lg-6" style="width: 25%;"> 
    		   			<input id="btnDown" type="button" value="Down" onclick="funMoveSelectedRow(0);"></input>
	    	  		</div>
	    	  		<div class="element-input col-lg-6" style="width: 25%;"> 
    		   			<input id="btnUp" type="button" value="Up" onclick="funMoveSelectedRow(1);" ></input>
	    	  		</div>
	    	  		<div class="element-input col-lg-6" style="width: 20%; margin-left: 20px;"> 
    		   			<label class="title">Total</label>
	    	  		</div>
	    	  		<div class="element-input col-lg-6" style="width: 25%;"> 
    		   			<label id="lblTotal" style="text-transform: uppercase; width:100px; height:25px;" />
	    	  		</div>
	    	  		
	    	  	</div>
	    	  	<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">	
	    	  		<div class="element-input col-lg-6" style="width: 25%;"> 
    		   			<input id="btnDelete" type="button" value="Save" onclick="funVoidItems();"></input>
	    	  		</div>
	    	  		<div class="element-input col-lg-6" style="width: 25%;"> 
    		   			<input id="btnDone" type="button" value="Full Void" onclick="funFullVoidBill();">
	    	  		</div>
	    	  </div>
	    	  
	       </div>
	       
	       <div style=" width: 50%; float:right; border-collapse: separate; ">
	     
	       		<div class="element-input col-lg-6"> 
				    		<input type="text" id="txtSearch" style="margin-top: 5px;height: 28px;width: 218px;margin-left: 60px;margin-bottom: 10px;" ng-model="searchKeyword"></input>
				</div>
				
				<div style="border: 1px solid #ccc; display: block; height: 430px; margin: auto; overflow-x: scroll; overflow-y: scroll; width: 90%;">
					<table id="tblDataFillGrid" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll">
						<tr>

					    </tr>
					</table>
				</div>
	       		
	       </div>
	       
	  </div>

	</s:form> 

    
<br /><br />       


 
</body>
</html>
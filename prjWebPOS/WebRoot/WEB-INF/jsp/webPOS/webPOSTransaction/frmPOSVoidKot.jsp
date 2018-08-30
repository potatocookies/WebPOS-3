<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
 	var fieldName,textValue2="",selectedRowIndex=0,delTableNo="",delKotNo="",delItemcode="", delAmount="",delQuatity="";
 
 	
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
			
				  funMoveRowUp(description,qty,amt,itemCode,selectedRowIndex,description1,qty1,amt1,itemCode1);
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
					  funMoveRowDown(description,qty,amt,itemCode,selectedRowIndex,description1,qty1,amt1,itemCode1);
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
	
	
	function funMoveRowDown(description,qty,amt,itemCode,rowCount,description1,qty1,amt1,itemCode1)
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
		
		
		
		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+Description+"\" value='"+Description+"'  onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+quantity+"\" value='"+quantity+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+Amount+"\" value='"+Amount+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+ItemCode+"\" value='"+ItemCode+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		
		
		
		
	
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
		
		var row1 = table.insertRow(rowCount);
		
		row1.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+nextDescription+"\" value='"+nextDescription+"'  onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextquantity+"\" value='"+nextquantity+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextAmount+"\" value='"+nextAmount+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextItemCode+"\" value='"+nextItemCode+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
				    
		table.deleteRow(rowCount+1);
		  
		  
	}
	
    function funMoveRowUp(description,qty,amt,itemCode,rowCount,description1,qty1,amt1,itemCode1)
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
		var Amount=amtStock[0].substring(1, (amtStock[0].length-2));
		var itemCodeArr = itemCode.split('value=');
		var code=itemCodeArr[1].split('onclick=');
		var ItemCode=code[0].substring(1, (code[0].length-2));
		
		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+Description+"\" value='"+Description+"'  onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+quantity+"\" value='"+quantity+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+Amount+"\" value='"+Amount+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+ItemCode+"\" value='"+ItemCode+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		
			   
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
		var row1 = table.insertRow(rowCount+1);
		 
		row1.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+nextDescription+"\" value='"+nextDescription+"'  onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextquantity+"\" value='"+nextquantity+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextAmount+"\" value='"+nextAmount+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row1.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+nextItemCode+"\" value='"+nextItemCode+"' onclick=\"funGetSelectedRowIndex(this)\"/>";		    
		table.deleteRow(rowCount);
	}

	
	function funFillGrid()
	{
	    var searchUrl="";
	    
	    var tableName=$('#cmbType').val();
		searchUrl=getContextPath()+"/fillRGridData.html";
		$.ajax({
		        type: "GET",
		        data:{
		        	tableName:tableName,
		       
				},
		        url: searchUrl,
		        
		        dataType : "json",
		       
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
		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"KOT NO.\" value=KOT NO. >";
		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"TB Name\" value=TB Name >";
		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"Waiter Name\" value=Waiter Name >";
		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"Take Away\" value=Take Away >";
		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"User Created\" value=User Created >";
		row.insertCell(5).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"Amount\" value=Amount >";
		rowCount++;
	    for(var i=0;i<data.length;i++){
	    	row = table.insertRow(rowCount);
	    	var rowData=data[i];
	    	
	    	for(var j=0;j<rowData.length;j++){
	    		
	    		row.insertCell(j).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowData[j]+"\" value='"+rowData[j]+"' onclick=\"funGetSelectedRowData(this)\"/>";
// 	    		 row.insertCell(j).innerHTML= "<input type=\"hidden\" readonly=\"readonly\" class=\"cell\" value='"+rowData[j]+"' />";
	    		 
	    	}
	    	rowCount++;
	    }
		
		
		
	}
	
     function funGetSelectedRowData(obj)
     {
    	
    	var index = obj.parentNode.parentNode.rowIndex;
    	var tableName = document.getElementById("tblDataFillGrid");
       	var dataKotNo= tableName.rows[index].cells[0].innerHTML; 
        var btnBackground=dataKotNo.split('value=');
        var kotData=btnBackground[1].split("onclick");
        var kot=kotData[0].substring(1, (kotData[0].length-2));
        
        var dataTableNo=tableName.rows[index].cells[1].innerHTML; 
        var btnBackground1=dataTableNo.split('value=');
        var tableData=btnBackground1[1].split("onclick");
        var tableNo=tableData[0].substring(1, (tableData[0].length-2));
        var cmbTableName=$('#cmbType').val();
        $("#textTableNo").val(tableNo);
        
        delTableNo=$('#textTableNo').val();
        delKotNo=kot;
        
        searchUrl=getContextPath()+"/fillItemDataTable.html?";
		$.ajax({
		        type: "GET",
		        url: searchUrl,
		        async:false,
		        data:"kot="+kot+"&tableNo="+tableNo+"&cmbTableName="+cmbTableName,
		        
// 		        data:{ kot:kot,
// 		        	tableName:tableName,
					
					
// 				},
			    success: function(response)
			    {
					
			    
			    		funAddItemTableData(response.listFillItemGrid,response.totalAmount,response.taxAmt,response.KotNo,response.subTotalAmt);
					
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
     
     function funAddItemTableData(itemDataList,totalAmount,taxAmt,KotNo,subTotalAmt){
    		$('#tblData tbody').empty()

    		var table = document.getElementById("tblData");
    		var rowCount = table.rows.length;

    		var row = table.insertRow(rowCount);
    		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"50%\" id=\"Description\" value=Description >";
    		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"Quantity\" value=Qty >";
    		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"Amount\" value= Amount>";
    		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"background-color: #85cdffe6; height: 21px;\" size=\"15%\" id=\"Item Code\" value=Item Code >";
//     		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"\" value=Select >";
    		
    		rowCount++;
    	  
    	    	
    	    	for(var j=0;j<itemDataList.length;j++){
    	    	  row = table.insertRow(rowCount);
    	    	  var rowItemData=itemDataList[j];
    	    	 
    	    		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+rowItemData[0]+"\" value='"+rowItemData[0]+"' onclick=\"funGetSelectedRowIndex(this)\"/ >";
    	    		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowItemData[1]+"\" value='"+rowItemData[1]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    	    		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowItemData[2]+"\" value='"+rowItemData[2]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    	    		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowItemData[3]+"\" value='"+rowItemData[3]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    	    		rowCount++;
    	    		
    	    		$("#lblUserCreated").text(rowItemData[4]);
    	    		$("#lblDateTime").text(rowItemData[5]);

    	    	}
    	    	if(rowCount<=2)
    	    	{

    	    		 document.getElementById("btnUp").style.display='none';
    	    		 document.getElementById("btnDown").style.display='none';
    	    		 document.getElementById("btnDelete").style.display='none';
    	    		
    	    	}
    	    	else
    	    	{
    	    		document.getElementById("btnUp").style.display='block';
   	    			 document.getElementById("btnDown").style.display='block';
   	    		 	document.getElementById("btnDelete").style.display='block';
    	   		}
    	    	$("#lblKotNo").text(KotNo);
    	    	
    	    	$("#lblTax").text(taxAmt.toFixed(2));
    	    	$("#lblSubTotlal").text(subTotalAmt.toFixed(2));
    	    	$("#lblTotal").text(totalAmount.toFixed(2));
    	 
     }

 	function funDeleteRow()
	{
	    var taxAmt=$("#lblTax").text();
	
	    var table = document.getElementById("tblData");
	    var delItemName="";
	    var count=$('#tblData tr').length-1;
	    var delRowNo="";
	    var i=selectedRowIndex;
         var a="";
			delRowNo=delRowNo+"del"+i;

        	var tableName = document.getElementById("tblData");
	       	var itemcode= tableName.rows[i].cells[3].innerHTML; 
	       	
	        var btnBackground=itemcode.split('value=');
	        var iCode=btnBackground[1].split("onclick");
	        delItemcode=delItemcode+"aa"+iCode[0].substring(1, (iCode[0].length-2));
	        
  	var itemName= tableName.rows[i].cells[0].innerHTML; 
	       	
	        var btnBackgrounditemName=itemName.split('value=');
	        var iName=btnBackgrounditemName[1].split("onclick");
	        delItemName=delItemName+"//aa"+iName[0].substring(1, (iName[0].length-2));
	        
	        
   	var amount= tableName.rows[i].cells[2].innerHTML; 
	       	
	        var btnBackgroundamount=amount.split('value=');
	        var amountnext=btnBackgroundamount[1].split("onclick");
	        delAmount=delAmount+"aa"+amountnext[0].substring(1, (amountnext[0].length-2));
	        
   	var qty= tableName.rows[i].cells[1].innerHTML; 
	       	
	        var btnBackgroundqty=qty.split('value=');
	        var quantity=btnBackgroundqty[1].split("onclick");
	        delQuatity=delQuatity+"aa"+quantity[0].substring(1, (quantity[0].length-2));

	   var delreow= delRowNo.split("del");
	  
	   for(var i=1;i<delreow.length;i++) {
		   
		   
		   table.deleteRow(delreow[i]);
	   }
	   var remarks = prompt("Enter Remarks", "");
    	var reasonCode=$("#cmbDocType").val();
      
    	searchUrl=getContextPath()+"/doneButtonClick.html?";
		$.ajax({
		        type: "GET",
		        url: searchUrl,
		        async:false,
		        data:"delItemcode="+delItemcode+"&delKotNo="+delKotNo+"&delTableNo="+delTableNo+"&remarks="+remarks+
		        "&reasonCode="+reasonCode+"&delQuatity="+delQuatity+"&delAmount="+delAmount+"&delItemName="+delItemName+"&taxAmt="+taxAmt,
		        
			    success: function(response)
			    {
					if(response)
						{
					alert("Void Kot SucessFully");
						}},
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
 	
 	function funFullVoidKot()
 	{
 		
 	   var remarks = prompt("Enter Remarks", "");
   	var reasonCode=$("#cmbDocType").val();
   	
   	searchUrl=getContextPath()+"/fullVoidKotButtonClick.html?";
	$.ajax({
	        type: "GET",
	        url: searchUrl,
	        async:false,
	        data:"delKotNo="+delKotNo+"&remarks="+remarks+
	        "&reasonCode="+reasonCode,
	        
		    success: function(response)
		    {
		    	$('#tblData tbody').empty();
		    	funFillGrid();	   
		    	
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
 		$('#tblData tbody').empty()

 		var table = document.getElementById("tblData");
 		var rowCount = table.rows.length;

 		var row = table.insertRow(rowCount);
 		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\"Description\" value=Description >";
 		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Quantity\" value=Qty >";
 		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Amount\" value= Amount>";
 		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Item Code\" value=Item Code >";
 		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"\" value=Select >";
 	    
 		funFillGrid();
 	}
   
</script>



</head>
<body>
       
     <div id="formHeading">
		<label>POS Void KOT </label>
			</div>

	<s:form name="Void KOT" method="POST" action="" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;">
	  
	  <div class="title" style="margin-left: 10%;">
				
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">KOT No.</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								 <label id="lblKotNo" />
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">User Created</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<label id="lblUserCreated" />
							</div>
					 </div>
					 
					  <div style="width: 49%;margin-right: 5px;float: left;">
					 
						 <div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
								<div class="element-input col-lg-6" style="width: 30%;"> 
			    					<label class="title">Date & Time</label>
			    				</div>
			    				<div class="element-input col-lg-6" style="width: 75%;"> 
									 <label id="lblDateTime"  />
									 <s:input type="hidden" id="textTableNo" name="textTableNo" value="textTableNo"  path="strTables"/>
								</div>
						</div>	
							
						<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
								<div class="element-input col-lg-6" style="width: 30%;"> 
			    					<label class="title">Reason</label>
			    				</div>
			    				<div class="element-input col-lg-6" style="width: 75%;"> 
									<s:select path="strReson" items="${listReson}" id="cmbDocType" ></s:select>
								</div>
						 </div>
					 
					 	<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
		 
					 		<div style="border: 1px solid #ccc; display: block; height: 200px; overflow-x: scroll; overflow-y: scroll; width: 100%;">
		
									<table id="tblData" style="width: 100%;" >
											<tbody style="border-top:none;">
					    					</tbody>
								    </table>
						
						     </div>
							
					    </div>
					    
					    <div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
					    		<div class="element-input col-lg-6" style="width: 70%;text-align: right;"> 
		    						<label class="title">Sub Total</label>
			    				</div>
			    				<div class="element-input col-lg-6" style="width: 30%;text-align: right;"> 
									 <label id="lblSubTotlal"/>
								</div>
						 </div>
					    
					    <div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
								<div class="element-input col-lg-6" style="width: 70%;text-align: right;"> 
		    						<label class="title">Tax</label>
			    				</div>
			    				<div class="element-input col-lg-6" style="width: 30%;text-align: right;"> 
									 <label id="lblTax" />
								</div>
						 </div>
					    
					    <div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
								<div class="element-input col-lg-6" style="width: 70%;text-align: right;"> 
		    						<label class="title">Total</label>
			    				</div>
			    				<div class="element-input col-lg-6" style="width: 30%;text-align: right;"> 
									 <label id="lblTotal" />
								</div>
					    </div>
					    
					    <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%;">
								<p align="center">
										<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnUp" type="button" value="UP" onclick="funMoveSelectedRow(1);"></input></div>
										<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnDown" type="button" value="DOWN" onclick="funMoveSelectedRow(0);"></input></div>
								</p>
						 </div>	
						 <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%;">
								<p align="center">
										<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnDelete" type="button" value="DELETE" onclick="funDeleteRow();"></input></div>
										<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnDone" type="button" value="FULLVOIDKOT" onclick="funFullVoidKot();"></input></div>
								</p>
						</div>
					 	
					 </div>
					 
					 <div style="width: 49%;margin-right: 5px;float: right;">
					 
					 	   <div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;height: 23px;">
					 	   </div>
					 	
					 	   <div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
					    		<div class="element-input col-lg-6" style="width: 35%;"> 
					 				<s:select id="cmbType" path="strTables" items="${tableData}"  onchange="funFillGrid()" />
					 			</div>
					 	   </div>
					 	
						 	<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
			 
						 		<div style="border: 1px solid #ccc; display: block; height: 200px; overflow-x: scroll; overflow-y: scroll; width: 100%;">
			
										<table id="tblDataFillGrid" style="width: 100%;" >
					    					<tbody style="border-top:none;">
					    					</tbody>
									    </table>
							
							     </div>
								
						     </div>
						     
					  </div>	 
	</div>

	</s:form> 
    
<br /><br />       
 
</body>
</html>
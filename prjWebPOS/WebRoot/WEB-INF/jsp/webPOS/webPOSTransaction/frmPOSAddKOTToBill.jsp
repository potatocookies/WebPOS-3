<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>

<style type="text/css">
.ui-tooltip 
{   
   white-space: pre-wrap;      
}
</style>
<script type="text/javascript">
	
	var fieldName,prevBillRow,prevBillCell,nxtBillRow,nxtBillCell;
	
	
//Load bill and kot data

  function funLoadData()
  {
	  funLoadKOT();
	  funLoadBill();
  }
	
	
//Delete a All record from a grid
	function funRemoveHeaderTableRows(tableName)
	{
		var table = document.getElementById(tableName);
		var rowCount = table.rows.length;
		while(rowCount>0)
		{
			table.deleteRow(0);
			rowCount--;
		}
	}
	
	
	
	
	function funLoadKOT()
	{
	  var tableName=$("#cmbTable").val();	
	   var searchurl=getContextPath()+"/LoadADDKOTTableData.html?TableName="+tableName;
		$.ajax({
			type : "GET",
			url : searchurl,
			dataType : "json",
			success : function(response){ 
				funRemoveHeaderTableRows("tblKOT");
				var table=document.getElementById("tblKOT");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);
				var row=table.insertRow();
				var k=0,first="",second="",third="",fourth="";
				var list=response.listKOTDtl,cnt=0,rowPosition=0;
				$.each(response.listKOTDtl,function(i,item)
				{
					cnt++;
					if(cnt<=4)
					  {
						var x=row.insertCell(0);
						x.innerHTML="<input type=\"button\"  class=\"transForm_button\"  value='"+item.strKOTNo+"' onclick=\"funGetSelectedRowIndex(this,"+"tblKOT"+")\" >";
						
					  }
					else
					 {
						row=table.insertRow();
						rowPosition++;
						cnt=1;
						var x=row.insertCell(0);
						x.innerHTML="<input type=\"button\"  class=\"transForm_button\"  value='"+item.strKOTNo+"' onclick=\"funGetSelectedRowIndex(this,"+"tblKOT"+"')\" >";
					 }
				 });			
				
			},
			error : function(e){
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
	
	
	
	
	function funLoadBill()
	{
	  var searchurl=getContextPath()+"/LoadUnsettleBill.html";
		$.ajax({
			type : "GET",
			url : searchurl,
			dataType : "json",
			success : function(response){ 
				funRemoveHeaderTableRows("tblBill");
				var table=document.getElementById("tblBill");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);
				var row=table.insertRow();
				var list=response.listBillDtl,cnt=0;
				$.each(response.listBillDtl,function(i,item)
				{
					cnt++;
					if(cnt<=4)
					  {
						var x=row.insertCell(0);
						x.innerHTML="<input type=\"button\"  class=\"transForm_button\"  value='"+item.strBillNo+"' onclick=\"funGetSelectedRowIndex(this,"+"tblBill"+")\" >";
					  }
					else
					 {
						row=table.insertRow();
						rowPosition++;
						cnt=1;
						var x=row.insertCell(0);
						x.innerHTML="<input type=\"button\"  class=\"transForm_button\"  value='"+item.strBillNo+"' onclick=\"funGetSelectedRowIndex(this,"+"tblBill"+")\" >";
					 }
				 });			
				
			},
			error : function(e){
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

	

	
	
	function funGetSelectedRowIndex(obj,tableName)
	{
		 
		 var index = obj.parentNode.parentNode.rowIndex;
		 var cellIndex=obj.parentNode.cellIndex;
		 var tblname=tableName.rows[index].cells[cellIndex].innerHTML;
		 var btnClassName=tblname.split('class="');
		 var btnBackground=btnClassName[1].split('value=');
		 var btnType=btnBackground[0].substring(0, (btnBackground[0].length-2));
		 var data=btnBackground[1].split('onclick=');
		 var code=data[0].substring(1, (data[0].length-2));
		 var selectedTableName=data[1].split('this,');
         var table=selectedTableName[1].substring(0, (selectedTableName[1].length-3));
		 var row = tableName.rows[index]
		 var rowCount = tableName.rows.length;
		 if(table=="tblKOT")
		 {  
			 row.deleteCell(cellIndex);
			 if(btnType!="transForm_button")
			 {
				 row.insertCell(cellIndex).innerHTML= "<input type=\"button\" class=\"transForm_button\"  value='"+code+"' onclick=\"funGetSelectedRowIndex(this,"+table+")\"/>";
			 }
			 else
			 {
				 row.insertCell(cellIndex).innerHTML= "<input  name=\"listKOTDtl["+0+"].strKOTNo\"  readonly=\"readonly\" class=\"transForm_bluebutton\" id=\"btnKot."+0+"\" value='"+code+"' onclick=\"funGetSelectedRowIndex(this,"+table+")\"/>";
			 }
		  }
		 else
		  {
			 if(prevBillRow==null)
		 	  {
				 prevBillRow=index;
			  }
			 else
			  {
				 prevBillRow=nxtBillRow;
			  }	 
			 if(prevBillCell==null)
		 	  {
				 prevBillCell=cellIndex;
			  }
			 else
			 {
				 prevBillCell=nxtBillCell;
			 }
			 if(prevBillRow==index && prevBillCell==cellIndex)
			  {
				 if(btnType!="transForm_button")
				 {   
				 }
				 else
				 {
					 row.deleteCell(cellIndex);
					 row.insertCell(cellIndex).innerHTML= "<input  name=\"listBillDtl["+0+"].strBillNo\"  readonly=\"readonly\" class=\"transForm_graybutton\" id=\"btnBill."+0+"\" value='"+code+"' onclick=\"funGetSelectedRowIndex(this,"+table+")\"/>";
					 nxtBillRow=index;
					 nxtBillCell=cellIndex;
				 }
			  }
			 else
			  {
				 var secondtblname=tableName.rows[prevBillRow].cells[prevBillCell].innerHTML;
				 var secondbtnClassName=secondtblname.split('class="');
				 var secondbtnBackground=secondbtnClassName[1].split('value=');
				 var data1=secondbtnBackground[1].split('onclick=');
				 var code1=data1[0].substring(1, (data1[0].length-2));
				 var row1 = tableName.rows[prevBillRow];
				 row1.deleteCell(prevBillCell);
				 row1.insertCell(prevBillCell).innerHTML= "<input type=\"button\" class=\"transForm_button\"  value='"+code+"' onclick=\"funGetSelectedRowIndex(this,"+table+")\"/>";
				 
				 if(btnType!="transForm_button")
					 
				 {
					
				 }
				 else
				 {
					 row.deleteCell(cellIndex);
					 row.insertCell(cellIndex).innerHTML= "<input  name=\"listBillDtl["+0+"].strBillNo\"  readonly=\"readonly\" class=\"transForm_graybutton\" id=\"btnBill."+0+"\" value='"+code+"' onclick=\"funGetSelectedRowIndex(this,"+table+")\"/>";
					 nxtBillRow=index;
					 nxtBillCell=cellIndex;
				 }
			  }	 
     }

  }
	
	function funHelp(transactionName)
	{
		fieldName = transactionName;
		window.showModalDialog("searchform.html?formname=" + transactionName + "&searchText=", "","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
	//Combo Box Change then set value
	function funOnChange() 
	{
		funLoadKOT();
		
	}
	
	
</script>


</head>
<body onload="funLoadData()">

	<div id="formHeading">
		<label>Add KOT To Bill</label>
	</div>

	<br />
	<br />
<%-- 	<s:form name="Add KOT To Bill" method="POST" action="saveAddKOTToBill.html?saddr=${urlHits}" > --%>

	<s:form name="Add KOT To Bill" method="POST" action="saveAddKOTToBill.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:1200px;min-width:150px;margin-top:2%;">
	
		<div class="title" >
			<div style=" width: 550px; height: 450px;float:left; overflow-x: scroll; border-collapse: separate;overflow-y: scroll;">
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display:block;">
					<div class="element-input col-lg-6" style="width: 30%;"> 
	    				<label class="title" >OPEN KOT</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 35%;"> 
	    				<label class="title" >Table Name:</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 35%;">
						<s:select id="cmbTable" items="${tableList}" onchange="funOnChange();" path="strTableName">
						</s:select>
					</div>
				</div>
				
				<table id="tblKOT" class="transFormTable">
				</table>
			</div>
		</div>
		
		<div class="title">
			<div style=" width: 600px; height: 450px;float:left; margin-left: 20px;overflow-x: scroll; border-collapse: separate; overflow-y: scroll;">
				<div class="row" style="background-color: #fff;margin-bottom: 20px;display:block;">
					<div class="element-input col-lg-6" style="width: 100%;margin-left: 15px;"> 
	    				<label class="title" >Bill No</label>
	    			</div>
	    		</div>
	    		
	    		<table id="tblBill" class="transFormTable">
				</table>
			</div>
		</div>
		
		<br/><br/><br/><br/>
		
		<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 240px;">
     		  <p align="center">
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit" onclick="return funValidateFields();"/></div>
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()"></div>
     		  </p>
   		</div>
		

	</s:form>
</body>
</html>

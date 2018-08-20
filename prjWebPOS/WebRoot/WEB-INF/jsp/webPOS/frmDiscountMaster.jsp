<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Counter Master</title>
<style>
.ui-autocomplete {
    max-height: 200px;
    overflow-y: auto;
    /* prevent horizontal scrollbar */
    overflow-x: hidden;
    /* add padding to account for vertical scrollbar */
    padding-right: 20px;
}
/* IE 6 doesn't support max-height
 * we use height instead, but this forces the menu to always be this tall
 */
* html .ui-autocomplete {
    height: 200px;
}

</style>
<script type="text/javascript">
var selectedRowIndex=0; 
var fieldName="";
	$(document).ready(function() {
	
		var POSDate="${gPOSDate}"
			var startDate="${gPOSDate}";
		  	var Date = startDate.split(" ");
			var arr = Date[0].split("-");
			Dat=arr[2]+"-"+arr[1]+"-"+arr[0];	
			$("#txtFromDate").datepicker({ dateFormat: 'dd-mm-yy'  });
			$("#txtFromDate" ).datepicker('setDate', Dat);
			
			$("#txtToDate").datepicker({ dateFormat: 'dd-mm-yy'  });
			$("#txtToDate" ).datepicker('setDate', Dat);
		 $("#txtDiscountOnCode").val("All");
		 $("#txtDiscOnValue").val("All");
		  $("form").submit(function(event){
			  if ($("#txtDiscountName").val()=="")
		        {
		            alert("Please Enter Discount Name");
		            return false;
		        }
			  var table = document.getElementById("tblDiscDtl");
			  var rowCount = table.rows.length;
			  if (rowCount <= 0)
		        {
		            alert("Please Enter Discount Detail");
		            return false;
		        }
			  var fromDate = $("#txtFromDate").val();
			  var toDate = $("#txtToDate").val();
			  var frmDate= fromDate.split('-');
				 Dat=frmDate[0]+frmDate[1]+frmDate[2];	
				 fDate = Dat;

				    var tDate= toDate.split('-');
					 Dat=tDate[0]+tDate[1]+tDate[2];	
					t1Date =  Dat;

		    	var dateDiff=t1Date-fDate;
				
			    	var dateDiff=t1Date-fDate;
					
			  if (dateDiff < 0)
		        {
		           alert("Invalid date");
		            return false;
		        }
			  
			});
		  
		  $("#cmbDiscountOn").change(function () {
			  funFillDiscountOnTypeWise();
		    });
		  
		 
		});
	
	
	function funFillDiscountOnTypeWise()
	{
		if($("#cmbDiscountOn").val()=="All")
		{
			$("#txtDiscountOnCode").val("All");
			$("#txtDiscOnValue").val("All");
		}
		else if($("#cmbDiscountOn").val()=="Item")
		{
			$("#txtDiscountOnCode").val(funHelp('POSMenuItemMaster'));
		}
		else if($("#cmbDiscountOn").val()=="Group")
		{
			$("#txtDiscountOnCode").val(funHelp('POSGroupMaster'));
		}
		else if($("#cmbDiscountOn").val()=="SubGroup")
		{
			$("#txtDiscountOnCode").val(funHelp('POSSubGroupMaster'));
		}
	}
	
</script>
<script type="text/javascript">
var field;

	/**
	* Reset The Group Name TextField
	**/
	
	$(function() 
	{		

		$("[type='reset']").click(function(){
		location.reload(true);
	});
	});	
	
	function funHelp(transactionName) {
		fieldName=transactionName;
		window.open("searchform.html?formname=" + transactionName
				+ "&searchText=", "",
				"dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	}
	
	/**
	* Get and Set data from help file and load data Based on Selection Passing Value(Group Code)
	**/
	
	function funSetData(code){
		var discOn = "";
		switch(fieldName){

			case 'POSDiscountMaster' : 
				funSetDiscMasterDtl(code);
				break;
			case 'POSMenuItemMaster':
				discOn = $("#cmbDiscountOn").val();
			funSetDiscOnChangeDtl(code,discOn);
				break;
			case 'POSGroupMaster':
				discOn = $("#cmbDiscountOn").val();
				funSetDiscOnChangeDtl(code,discOn);
					break;
			case 'POSSubGroupMaster':
				discOn = $("#cmbDiscountOn").val();
				funSetDiscOnChangeDtl(code,discOn);
					break;		
		}
	}
	function funSetDiscOnChangeDtl(code,discOn)
	{
		$("#txtDiscountOnCode").val(code);
		
		var searchurl=getContextPath()+"/loadPOSDiscountDiscountOnChangeData.html?code="+code+"&discOn="+discOn;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strDiscountCode=='Invalid Code')
			        	{
			        		alert("Invalid Code");
			        		$("#txtDiscountOnCode").val(''); 
			        	}
			        	else
			        	{
				        	$("#txtDiscountOnCode").val(response.strDiscountCode);
				        	$("#txtDiscOnValue").val(response.strDiscountName);
				        	$("#txtDiscOnValue").focus();
				        	
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

	function funSetDiscMasterDtl(code)
	{
		$("#txtDiscountCode").val(code);
		var searchurl=getContextPath()+"/loadPOSDiscountMasterData.html?discCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strDiscountCode=='Invalid Code')
			        	{
			        		alert("Invalid Discount Code");
			        		$("#txtDiscountCode").val(''); 
			        	}
			        	else
			        	{
				        	$("#txtDiscountCode").val(response.strDiscountCode);
				        	$("#txtDiscountName").val(response.strDiscountName);
				        	$("#txtDiscountName").focus();
				        	$("#cmbPOSName").val(response.strPosCode);
				        	$("#cmbDiscountOn").val(response.strDiscountOn);
				        	
				        	$.each(response.listDiscountDtl, function(i,item)
									{			
							    		funAddRow(item.discountReasonCode,item.strDiscoutnName,item.discountOnType,item.discountOnValue);
							    	});
				        	
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

	/**
	 * Success Message After Saving Record
	 **/
	$(document)
			.ready(
					function() {
						var message = '';
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
		
	function btnAdd_onclick() 
	{
		 	
		
		var discOn = $("#cmbDiscountOn").val();
		var discTye = $("#cmbDiscTye").val();
		var discountOnCode = $("#txtDiscountOnCode").val();
		var discValue = $("#txtDiscountValue").val();
		var discountOnName = $("#txtDiscOnValue").val();
		if ($("#txtDiscountValue").length >= 6) {
			alert("Value length must be less than 7");

			return false;
		}
		if ($("#txtDiscountOnCode").val() == "") {
			alert("Please Select" + discOn);

			return false;
		}
		if (discValue<= 0 || discValue>99) {
			alert("Invalid Discount Value");
		}

		else {
			funAddRow(discountOnCode,discountOnName,discTye,discValue);
			var table = document.getElementById("tblDiscDtl");
			var rowCount = table.rows.length;
			if(rowCount>0)
			{
				$("#txtDiscountOnCode").val("");
				$("#txtDiscOnValue").val("");
				$("#txtDiscountValue").val(0);
				$("#cmbDiscountOn").val($("#cmbDiscountOn").val());
				$("#cmbDiscountOn").prop( "disabled", true );   
			}
		
			
		}
		

	}

	function funAddRow(discountOnCode, discountOnName, discTye, discValue) {
		var table = document.getElementById("tblDiscDtl");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		
		var discOn = $("#cmbDiscountOn").val();
		var isExists = false;
        for (var i = 0; i < rowCount; i++)
        {
        	var tblDiscOnCode=table.rows[i].cells[0].children[0].value;
            if (discountOnCode==tblDiscOnCode)
            {
                isExists = true;
                break;
            }
        }
        if (isExists)
        {
           alert("Duplicate Discount.");
            return;
        }

        var isValid = true;
        for (var i = 0; i < rowCount; i++)
        {
        	var tblDiscOnCode=table.rows[i].cells[0].children[0].value;
            if (tblDiscOnCode=="All")
            {
                isValid = false;
                break;
            }
        }
        if (!isValid)
        {
            alert("Invalid Discount Details.");
            return;
        }

        if (rowCount > 0 && discOn=="All")
        {
           alert("Invalid Discount Details.");
            return;
        }

		
		

		row.insertCell(0).innerHTML = "<input type=\"hidden\" class=\"Box\" name=\"listDiscountDtl["
				+ (rowCount)
				+ "].discountReasonCode\" size=\"0%\"  id=\"txtDiscOnCode."
				+ (rowCount)
				+ "\" value='"
				+ discountOnCode
				+ "' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(1).innerHTML = "<input class=\"Box\" name=\"listDiscountDtl["
				+ (rowCount)
				+ "].strDiscoutnName\" size=\"35%\"  id=\"txtDiscName."
				+ (rowCount)
				+ "\" value='"
				+ discountOnName
				+ "' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(2).innerHTML = "<input class=\"Box\" name=\"listDiscountDtl["
				+ (rowCount)
				+ "].discountOnType\" size=\"30%\"  id=\"txtDiscOnType."
				+ (rowCount)
				+ "\" value='"
				+ discTye
				+ "' onclick=\"funGetSelectedRowIndex(this)\"/>";
		row.insertCell(3).innerHTML = "<input class=\"Box\" name=\"listDiscountDtl["
				+ (rowCount)
				+ "].discountOnValue\" size=\"16%\"  id=\"txtDiscOnValue."
				+ (rowCount)
				+ "\" value='"
				+ discValue
				+ "' onclick=\"funGetSelectedRowIndex(this)\"/>";
				
		
	}
	function funGetSelectedRowIndex(obj) {
		var index = obj.parentNode.parentNode.rowIndex;
		var table = document.getElementById("tblDiscDtl");
		if ((selectedRowIndex > 0) && (index != selectedRowIndex)) {
			if (selectedRowIndex % 2 == 0) {
				row = table.rows[selectedRowIndex];
				row.style.backgroundColor = '#A3D0F7';
				selectedRowIndex = index;
				row = table.rows[selectedRowIndex];
				row.style.backgroundColor = '#ffd966';
				row.hilite = true;
			} else {
				row = table.rows[selectedRowIndex];
				row.style.backgroundColor = '#C0E4FF';
				selectedRowIndex = index;
				row = table.rows[selectedRowIndex];
				row.style.backgroundColor = '#ffd966';
				row.hilite = true;
			}

		} else {
			selectedRowIndex = index;
			row = table.rows[selectedRowIndex];
			row.style.backgroundColor = '#ffd966';
			row.hilite = true;
		}

	}

	function btnRemove_onclick() {

		var table = document.getElementById("tblDiscDtl");
		table.deleteRow(selectedRowIndex);

	}
</script>		
</head>
<body>

	<div id="formHeading">
	<label>Discouont Master</label>
	</div>

<br/>
<br/>

	<s:form name="DiscountMaster" method="POST" action="savePOSDiscountMaster.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

		<div class="title" style="margin-left: 190px;">
		
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >Discount Code</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtDiscountCode" path="strDiscountCode" ondblclick="funHelp('POSDiscountMaster')"/>
					</div>
					<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtDiscountName" path="strDiscountName" />
					</div>
				</div>
					
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;">
						<label class="title">POS</label>
					</div>
	    			<div class="element-input col-lg-6" style="width: 50%;">
						<s:select id="cmbPOSName" path="strPosCode" items="${posList}" />
					</div> 	
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >From Date</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtFromDate" required="required" path="dteFromDate" pattern="\d{1,2}-\d{1,2}-\d{4}"/>
					</div>
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >To Date</label>
	    			</div>
					<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtToDate" required="required" path="dteToDate" pattern="\d{1,2}-\d{1,2}-\d{4}" />
					</div>
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >Discount On</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
						<s:select id="cmbDiscountOn" path="strDiscountOn" >
						    <option value="All">All</option>
						    <option value="Item">Item</option>
						    <option value="Group">Group</option>
						    <option value="SubGroup">SubGroup</option>
						</s:select>
					</div>
					<div class="element-input col-lg-6" style="width: 15%;">
						<s:input id="txtDiscountOnCode" required="required" path="strDiscountOnCode" readonly="true" onclick="funFillDiscountOnTypeWise()"/>
					</div>
					<div class="element-input col-lg-6" style="width: 15%;margin-left: 64px;">
					<s:input id="txtDiscOnValue" required="required" path="strDiscOnValue" readonly="true"/>
					</div>
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title">Discount Type</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
						<s:select id="cmbDiscTye" name="cmbDiscTye" path="strDiscountType" >
							<option value="Percentage">Percentage</option>
							<option value="Amount">Amount</option>
						</s:select>

					</div>
					<div class="element-input col-lg-6" style="width: 25%;">
						<s:input id="txtDiscountValue" required="required" path="dblDiscountValue" style="text-align: right;" />
					</div>
					<div class="element-input col-lg-6" style="width: 15%;margin-right: 5px;">
						<input id="btnAdd" type="button" value="Add" onclick="return btnAdd_onclick();" style="width: 100px;">
					</div>
					<div class="element-input col-lg-6" style="width: 15%;">
						<input id="btnRemove" type="button" value="Remove" onclick="return btnRemove_onclick(); ">
					</div>
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
				
					<div style="border: 1px solid #ccc; display: block; height: 150px; overflow-x: hidden; overflow-y: scroll; width: 80%;">
					 	
								<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;">	
				    					<thead>
				        					<tr> 
				            					<th style="width:40%">Name</th>
												<th style="width:35%">Discount Type</th>
												<th style="width:20%">Value</th>
				        					</tr>	
				    					</thead>
				    				</table>
				    		
				    				<table id="tblDiscDtl" style="width: 100%;">
				    					<tbody>				
									
										</tbody>
				    				</table>
				    				
				    			</div>
				   	 </div>
				   	 	 
			   	 </div>
			   	 
			   	 <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 25%;">
				   	    <p align="center">
								<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit"  /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" /></div>
					    </p>
				 </div>
				
		</div>
	

	</s:form>
</body>
</html>

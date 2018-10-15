<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<style>

#tab_container {
	width: 100%;
	margin: 1;
	
	height: 500px;
	overflow: auto;
	float: right;
	
}
#tblReservation td {

    -o-text-overflow: ellipsis;   /* Opera */
    text-overflow:    ellipsis;   /* IE, Safari (WebKit) */
    overflow:hidden;              /* don't show excess chars */
    white-space:nowrap;           /* force single line */
    width: 300px;                 /* fixed width */
}
</style>
<script type="text/javascript">
	$(document).ready(function() 
		{
		 
		  $('input#txtTableNo').mlKeyboard({layout: 'en_US'});
		  $('input#txtTableName').mlKeyboard({layout: 'en_US'});
		  $('input#txtPaxNo').mlKeyboard({layout: 'en_US'});
		  
			$(".tab_content").hide();
			$(".tab_content:first").show();
	
			$("ul.tabs li").click(function() {
				$("ul.tabs li").removeClass("active");
				$(this).addClass("active");
				$(".tab_content").hide();
	
				var activeTab = $(this).attr("data-state");
				$("#" + activeTab).fadeIn();
			});
			
			$("#tab").click(function() {
				var fromDate = $("#txtdteFrom").val();
				var toDate = $("#txtdteTo").val();
	
				if (fromDate.trim() == '' && fromDate.trim().length == 0) {
					alert("Please Enter From Date");
					return false;
				}
				if (toDate.trim() == '' && toDate.trim().length == 0) {
					alert("Please Enter To Date");
					return false;
				}
		
				if(CalculateDateDiff(fromDate,toDate))
				{
					if(timeDifference())
					{
					fDate=fromDate;
					tDate=toDate;
					funLoadReservations();
					}
				}
		});
		
		$("form").submit(function(event){
			 
			  if($("#txtContactNo").val().trim()=="")
				{
					alert("Please Enter Contact No.");
					return false;
				}
			  if ($("#txtCustName").val().trim()=="") {
					alert("Please Enter Customer Name.");
					return false;
				}
			  if(($("#cmbresHH").val()=="HH") || ($("#cmbresMM").val()=="MM"))
			    {
					alert("Please Select Valid Time.");
			   		return false;
				}
			});
		
		  
		 
		  $("#btnExecute").click(function(event) {
			  var fromDate = $("#txtdteFrom").val();
				var toDate = $("#txtdteTo").val();

				if (fromDate.trim() == '' && fromDate.trim().length == 0) {
					alert("Please Enter From Date");
					return false;
				}
				if (toDate.trim() == '' && toDate.trim().length == 0) {
					alert("Please Enter To Date");
					return false;
				}
				if(($("#cmbHH").val()=="HH") || ($("#cmbMM").val()=="MM"))
			    {
					alert("Invalid From Time");
			   		
			       	return false;
				}
				 if(($("#cmbToHH").val()=="HH") || ($("#cmbToMM").val()=="MM"))
			    {
					alert("Invalid To Time");
			   		
			       	return false;
				}
			
					if(CalculateDateDiff(fromDate,toDate)){
						if(timeDifference())
							{
								fDate=fromDate;
								tDate=toDate;
								funLoadReservations();
							}
					}
				
			}); 
	});
	
	 
</script>
<script type="text/javascript">
var posDate;
var selectedRowIndex=0;
var areaCode, areaName;
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
	
	var fieldName;

	function onLoadData()
	{
		$("#cmbToHH").val("11");
		$("#cmbHH").val("12");
		$("#cmbToMM").val("00");
		$("#cmbMM").val("00");
		$("#cmbAMPM").val("AM");
		$("#cmbToAMPM").val("PM");
		funSetDate();
	}

	function funSetData(code){

		switch(fieldName){

			case 'POSTableReservation' : 
				funSetReservationDtl(code);
				break;
				
			case 'POSCustomerMaster':
				funSetDataCustomer(code);
				break;
				
			case 'POSCustomerAreaMaster':
				funSetDataBuilding(code);					
			break;
			
			case 'POSTableReserveMaster' : 
				funSetTableNo(code);
				break;
		}
	}

	/**
	* Get and Set data from help file and load data Based on Selection Passing Value(Customer Code)
	**/
	function funSetDataCustomer(code)
	{
	
		var searchurl=getContextPath()+"/loadPOSCustomerMasterData.html?POSCustomerCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	
				        	
				        	$("#txtContactNo").val(response.intlongMobileNo);
				        
				        	$("#txtCustName").val(response.strCustomerName);
				        	$("#txtCustCode").val(response.strCustomerCode);
				        	$("#txtBldgCode").val(response.strBuldingCode);
				        	$("#txtBldgName").val(response.strBuildingName);
				        	
				        	$("#cmbCity").val(response.strCity);
				        
			        	
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
	function funSetDataBuilding(code)
	{
		$("#txtBldgCode").val(code);
	
		var searchurl=getContextPath()+"/loadPOSCustomerAreaMasterData.html?POSCustomerAreaCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strCustomerAreaCode=='Invalid Code')
			        	{
			        		alert("Invalid Building  Code");
			        		$("#txtBuldingCode").val('');
			        	}
			        	else
			        	{
				        
				        	
				        	$("#txtBldgCode").val(response.strCustomerAreaCode);
				        	$("#txtBldgName").val(response.strCustomerAreaName);
				        	
				        
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

	function funSetTableNo(code){

			
			var searchurl=getContextPath()+"/loadPOSTableMasterData.html?tableCode="+code;		
			 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	
				        	$("#txtTableName").val(response.strTableName);
			        	$("#txtTableNo").val(code);
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

	function funSetReservationDtl(code){

			$("#txtReservationCode").val(code);
			var searchurl=getContextPath()+"/loadPOSTableReservationData.html?resCode="+code;		
			 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strTableNo=='Invalid Code')
			        	{
			        		alert("Invalid Reservation Code");
			        		
			        	}
			        	else
			        	{
				        	$("#txtContactNo").val(response.strContactNo);
				        	$("#txtCustCode").val(response.strCustCode);
				        	$("#txtCustName").val(response.strCustName);
							$("#cmbSmokingYN").val(response.strSmokingYN);
				        	$("#txtBldgCode").val(response.strBldgCode);
				        	$("#txtBldgName").val(response.strBldgName);
				        	$("#cmbCity").val(response.strCity);
				        	$("#txtTableNo").val(response.strTableNo);
							$("#txtTableName").val(response.strTableName);
				        	$("#txtPax").val(response.intPax); 
				        	$("#txtdteDate").val(response.dteDate);
				        	$("#cmbresHH").val(response.strHH);
				        	$("#cmbresMM").val(response.strMM);
				        	$("#cmbresAMPM").val(response.strAMPM);
				        	$("#cmbPOSName").val(response.strPOS);
				        	$("#txtInfo").val(response.strInfo);
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


	function funSetDate()
	{
		var POSDate="${gPOSDate}";
	  	var Date = POSDate.split(" ");
		var arr = Date[0].split("-");
		Dat=arr[2]+"-"+arr[1]+"-"+arr[0];
	
		$("#txtdteFrom").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtdteFrom" ).datepicker('setDate', Dat);
		$("#txtdteTo").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtdteTo" ).datepicker('setDate', Dat);
		
		$("#txtdteDate").datepicker({ dateFormat: 'dd-mm-yy' });
	    $("#txtdteDate").datepicker('setDate', Dat);
	    var fromDate = $("#txtdteFrom").val();
		var toDate = $("#txtdteTo").val();
	    fDate=fromDate;
		tDate=toDate;
		funLoadReservations();
		 
	}
	function funHelp(transactionName)
	{
		fieldName=transactionName;
		  window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	}
	function CalculateDateDiff(fromDate,toDate) {

		var frmDate= fromDate.split('-');
	    var fDate = new Date(frmDate[0],frmDate[1],frmDate[2]);
	    
	    var tDate= toDate.split('-');
	    var t1Date = new Date(tDate[0],tDate[1],tDate[2]);

    	var dateDiff=t1Date-fDate;
  		 if (dateDiff >= 0 ) 
  		 {
         	return true;
         }else{
        	 alert("Please Check From Date And To Date");
        	 return false
         }
	}
	function timeDifference()
	{
		var HH = parseInt($("#cmbHH").val());
		var MM=parseInt($("#cmbMM").val());
	    var AMPM = $("#cmbAMPM").val();
	    var fTime=HH + ":"+ MM + ":" + "00";
	   if(AMPM=="PM")
		   HH=HH+12;

	    var toHH = parseInt($("#cmbToHH").val());
		var toMM=parseInt($("#cmbToMM").val());
	    var toAMPM = $("#cmbToAMPM").val();
	    var toTime=toHH + ":"+ toMM + ":" + "00";
	    if(toAMPM=="PM")
			   toHH=toHH+12;
	  if(toHH<HH)
	  { 
		  alert("from Time must be less than To Time..!! ");
			return false;
	  }
	  else if(toHH==HH)
	 {
		  if(toMM<=MM)
		  { 
			  alert("from Time must be less than To Time..!! ");
		    	 return false;
		  }
		  else 
			  return true;
	}
	  else
		  return true;
	}
	
	function funLoadReservations()
	{
		var HH = ($("#cmbHH").val());
		var MM=($("#cmbMM").val());
		 var AMPM = $("#cmbAMPM").val();
	   if(HH=="12" && AMPM=="AM")
		   HH="00";
	   if(AMPM=="PM")
	   {   HH= parseInt(HH);
		 HH +=12;
	   }
	    var fTime=HH + ":"+ MM + ":" + "00";
	    
	    var toHH =$("#cmbToHH").val();
		var toMM=($("#cmbToMM").val());
		 var toAMPM = $("#cmbToAMPM").val();
		 if(toAMPM=="PM")
		 {  toHH=parseInt(toHH);
				   toHH+=12;
		 }
	    var toTime=toHH + ":"+ toMM + ":" + "00";
		funRemoveTableRows();
	var gurl = getContextPath() + "/loadTableReservationDtl.html";
	$.ajax({
		type : "GET",
		data:{ fromDate:fDate,
				toDate:tDate,
				fromTime:fTime,
				toTime:toTime,
			},
		url : gurl,
		dataType : "json",
		success : function(response) {
			
				$.each(response,function(i,item){
	            	
					funFillReservationDetail(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9]);
            	});
	        	
			
		},
		error : function(jqXHR, exception) {
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
	function funCancelReservation(tblNo,reservCode)
	{
		
	var gurl = getContextPath() + "/funCancelTableReservation.html";
	$.ajax({
		type : "GET",
		data:{ reservationNo:reservCode,
			tableNo:tblNo,
				
			},
		url : gurl,
		dataType : "text",
		success : function(response) 
		{
			funLoadReservations();
				
		},
		error : function(jqXHR, exception) {
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
	function funFillReservationDetail(item1,item2,item3,item4,item5,item6,item7,item8,item9,item10)
	{
		var table = document.getElementById("tblReservation");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);

	      row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box \" size=\"12%\" id=\"txtItemName."+(rowCount)+"\" value='"+item1+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"16%\" id=\"txtDate."+(rowCount)+"\" value='"+item2+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item3+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+item4+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"7%\" id=\"txtVariance."+(rowCount)+"\" value='"+item5+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(5).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"12%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item6+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"12%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+item7+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"21%\" id=\"txtVariance."+(rowCount)+"\" value='"+item8+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(8).innerHTML= "<input type=\"hidden\" class=\"Box \" size=\"0%\" id=\"txtItemName."+(rowCount)+"\" value='"+item9+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(9).innerHTML= "<input type=\"hidden\" class=\"Box \" size=\"0%\" id=\"txtItemName."+(rowCount)+"\" value='"+item10+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	}
	function btnRemove_onclick() 
	{
		var table = document.getElementById("tblReservation");
		
		  var tableNo=table.rows[selectedRowIndex].cells[8].innerHTML;
		  var resCode=table.rows[selectedRowIndex].cells[9].innerHTML; 
		
		  var codeArr = tableNo.split('value=');
			var code=codeArr[1].split('onclick=');
			var tblNo=code[0].substring(1, (code[0].length-2));
			var nameArr = resCode.split('value=');
			var name=nameArr[1].split('onclick=');
			var reservCode=name[0].substring(1, (name[0].length-2));
			
		funCancelReservation(tblNo,reservCode);
	
	}

	
 function funResetFields()
 {
	
 	$("#txtdteFrom").val(posDate);
 	$("#txtdteTo").val(posDate);
	$("#cmbHH").val("HH");
	$("#cmbMM").val("MM");
   $("#cmbAMPM").val("AM");
  
   $("#cmbToHH").val("HH");
	$("#cmbToMM").val("MM");
   $("#cmbToAMPM").val("AM");
 	funRemoveTableRows();
 }

	

	 function funRemoveTableRows()
		{
			var table = document.getElementById("tblReservation");
			var rowCount = table.rows.length;
			while(rowCount>0)
			{
				table.deleteRow(0);
				rowCount--;
			}
		}
	//Check Duplicate Product in grid

	
	function funGetSelectedRowIndex(obj)
	{
		 var index = obj.parentNode.parentNode.rowIndex;
		 var table = document.getElementById("tblReservation");
			var rowCount = table.rows.length;
		 if(selectedRowIndex%2==0)
			 {
			 if(rowCount==selectedRowIndex)
				 {
				 selectedRowIndex=index;
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#ffd966';
				 row.hilite = true;
				 }
			 else{
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#A3D0F7';
				 selectedRowIndex=index;
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#ffd966';
				 row.hilite = true;
			 }
			 }
			 else
			 {
				 if(rowCount==selectedRowIndex)
				 {
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
			
		 
		
		 
		
	}
	
	
</script>

</head>
<body onload="onLoadData()">

	<div id="formHeading">
	<label>Delivery Boy Master</label>
	</div>

	<s:form name="saveTableReservation" method="POST" action="saveTableReservation.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
	
	   <div style="margin-left: 5%;">
		<div id="tab_container" style="overflow: hidden;">
				<ul class="tabs">
						<li class="active" data-state="tab1" style="width: 20%; padding-left: 5%; height: 25px; border-radius: 4px;">Table Reservation</li>
						<li data-state="tab2" style="width: 20%; padding-left: 5%; height: 25px; border-radius: 4px;">Reservations</li>
				</ul>
				
			<br /> <br />
			
<!-- 	Start of Tab1 -->
					
		    <div id="tab1" class="tab_content">
			
				<div class="title">
				   <div>
						<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Reservation Code</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input type="text" id="txtReservationCode" readonly="true" path="strReservationCode" ondblclick="funHelp('POSTableReservation')" />
							</div>
							<div class="element-input col-lg-6" style="width: 17%; margin-left: 2%"> 
		    					<label class="title">Contact No</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input type="text" id="txtContactNo" path="strContactNo" ondblclick="funHelp('POSCustomerMaster')" style="width: 100%;" />
							</div>
					 	</div>
				 	</div>
				 	<div>
					 	<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Customer Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input type="text" id="txtCustName" path="strCustName" />
								<s:input type="hidden" id="txtCustCode" path="strCustCode" />
							</div>
							<div class="element-input col-lg-6" style="width: 17%;margin-left: 2%""> 
		    					<label class="title">Smoking</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbSmokingYN" path="strSmokingYN">
										<option value="Y">Yes</option>
									 <option value="N">No</option>
					 			</s:select>
							</div>
					 	</div>
				 	</div>
				 	<div>
					 	<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Area/City</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input type="text" id="txtBldgCode" path="strBldgCode" readonly="true" ondblclick="funHelp('POSCustomerAreaMaster')" />
							</div>
		    				<div class="element-input col-lg-6" style="width: 19%;margin-left: 2%"> 
		    					<s:input type="text" id="txtBldgName" path="strBldgName" style=" width: 100%;"/>
							</div>
		    				<div class="element-input col-lg-6" style="width: 20%; margin-left: -17px;"> 
								<s:select id="cmbCity" path="strCity" >
										<option value="Pune">Pune</option><option value="Agalgaon">Agalgaon</option> <option value="Agartala">Agartala</option>
					 					<option value="Agra">Agra</option> <option value="Ahmedabad">Ahmedabad</option><option value="Ahmednagar">Ahmednagar</option>
					 					<option value="Ajmer">Ajmer</option> <option value="Akluj">Akluj</option> <option value="Akola">Akola</option><option value="Akot">Akot</option>
					 					<option value="Allahabad">Allahabad</option><option value="Allepey">Allepey</option> <option value="Amalner">Amalner</option> <option value="Ambernath">Ambernath</option>
					 					<option value="Amravati">Amravati</option> <option value="Amritsar">Amritsar</option><option value="Anand">Anand</option>
					 					<option value="Arvi">Arvi</option> <option value="Asansol">Asansol</option> <option value="Ashta">Ashta</option>
					 					<option value="Aurangabad">Aurangabad</option> <option value="Aziwal">Aziwal</option><option value="Baddi">Baddi</option><option value="Bangalore">Bangalore</option>
									 	<option value="Bansarola">Bansarola</option> <option value="Baramati">Baramati</option> <option value="Bareilly">Bareilly</option><option value="Baroda">Baroda</option>
									 	<option value="Barshi">Barshi</option> <option value="Beed">Beed</option> <option value="Belgum">Belgum</option><option value="Bellary">Bellary</option> <option value="Bhandara">Bhandara</option><option value="Bhilai">Bhilai</option>
									 	<option value="Bhivandi">Bhivandi</option><option value="Bhopal">Bhopal</option> <option value="Bhubaneshwar">Bhubaneshwar</option> <option value="Bhusawal">Bhusawal</option><option value="Bikaner">Bikaner</option> <option value="Bokaro">Bokaro</option>
									 	<option value="Bombay">Bombay</option> <option value="Buldhana">Buldhana</option>	<option value="Burhanpur">Burhanpur</option> <option value="Chandigarh">Chandigarh</option><option value="Chattisgad">Chattisgad</option> <option value="Chennai(Madras)">Chennai(Madras)</option>
									 	<option value="Cochin">Cochin</option><option value="Coimbature">Coimbature</option> <option value="Dehradun">Dehradun</option><option value="Delhi">Delhi</option>  <option value="Dhanbad">Dhanbad</option> <option value="Dhule">Dhule</option>
									 	<option value="Faridabad">Faridabad</option> <option value="Goa">Goa</option> <option value="Gujrat">Gujrat</option><option value="Gurgaon">Gurgaon</option>  <option value="Guwahati">Guwahati</option><option value="Gwalior">Gwalior</option>
									 	<option value="Hyderabad">Hyderabad</option>  <option value="Ichalkaranji">Ichalkaranji</option>  <option value="Indapur">Indapur</option>  <option value="Indore">Indore</option><option value="Jabalpur">Jabalpur</option> <option value="Jaipur">Jaipur</option>
										<option value="Jalandhar">Jalandhar</option><option value="Jalgaon">Jalgaon</option>  <option value="Jalna">Jalna</option><option value="Jamshedpur">Jamshedpur</option> <option value="Kalamnuri">Kalamnuri</option> <option value="Kanpur">Kanpur</option>
										<option value="Karad">Karad</option> <option value="Kochi(Cochin)">Kochi(Cochin)</option><option value="Kolhapur">Kolhapur</option><option value="Kolkata(Calcutta)">Kolkata(Calcutta)</option><option value="Kozhikode(Calicut)">Kozhikode(Calicut)</option>   <option value="Latur">Latur</option>
				 						<option value="Lucknow">Lucknow</option>    <option value="Ludhiana">Ludhiana</option><option value="Mumbai">Mumbai</option> <option value="Madurai">Madurai</option><option value="Mangalvedha">Mangalvedha</option>  <option value="Manmad">Manmad</option>
										<option value="Meerut">Meerut</option> <option value="Mumbai(Bombay)">Mumbai(Bombay)</option>
										<option value="Mysore">Mysore</option>   <option value="Nagpur">Nagpur</option><option value="Nanded">Nanded</option>
										<option value="Nandurbar">Nandurbar</option> <option value="Nashik">Nashik</option><option value="Orisa">Orisa</option><option value="Osmanabad">Osmanabad</option>
				 						<option value="Pachora">Pachora</option><option value="Pandharpur">Pandharpur</option><option value="Parbhani">Parbhani</option><option value="Patna">Patna</option>
										<option value="Pratapgad">Pratapgad</option><option value="Raipur">Raipur</option> <option value="Rajasthan">Rajasthan</option><option value="Rajkot">Rajkot</option>
										<option value="Ranchi">Ranchi</option><option value="Ratnagiri">Ratnagiri</option><option value="Salem">Salem</option>
					  					<option value="Sangamner">Sangamner</option><option value="Sangli">Sangli</option> <option value="Satara">Satara</option>
										<option value="Sawantwadi">Sawantwadi</option><option value="Secunderabad">Secunderabad</option> <option value="Shirdi">Shirdi</option>
									 	<option value="Sindhudurga">Sindhudurga</option><option value="Solapur">Solapur</option> <option value="Srinagar">Srinagar</option>
									 	<option value="Surat">Surat</option><option value="Tiruchirapalli">Tiruchirapalli</option><option value="Vadodara(Baroda)">Vadodara(Baroda)</option>
										<option value="Varanasi(Benares)">Varanasi(Benares)</option><option value="Vijayawada">Vijayawada</option><option value="Visakhapatnam">Visakhapatnam</option><option value="Yawatmal">Yawatmal</option>
					 					<option value="Other">Other</option>
								 </s:select>
							</div>
						</div>
					</div>
					<div>
						<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input id="txtdteDate" name="txtdteDate" path="dteDate" />
							</div>
							<div class="element-input col-lg-6" style="width: 7%;margin-left: 2%""> 
		    					<label class="title">Time</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 10%;"> 
								<s:select id="cmbresHH" name="cmbHH" path="strHH" >
										<option value="HH">HH</option>
										<option value="01">01</option>
										<option value="02">02</option>
										<option value="03">03</option>
										<option value="04">04</option>
										<option value="05">05</option>
										<option value="06">06</option>
										<option value="07">07</option> 
										<option value="08">08</option>
										<option value="09">09</option>
										<option value="10">10</option>
										<option value="11">11</option>
										<option value="12">12</option>
								</s:select>
						 	</div>
			    			<div class="element-input col-lg-6" style="width: 10%;"> 
					 			<s:select id="cmbresMM" name="cmbresMM" path="strMM">
										<option value="MM">MM</option><option value="00">00</option><option value="01">01</option>
										<option value="02">02</option><option value="03">03</option><option value="04">04</option>
										<option value="05">05</option><option value="06">06</option><option value="07">07</option> 
										<option value="08">08</option><option value="09">09</option><option value="10">10</option>
										<option value="11">11</option><option value="12">12</option><option value="13">13</option>
										<option value="14">14</option><option value="15">15</option><option value="16">16</option>
										<option value="17">17</option><option value="18">18</option><option value="19">19</option>
										<option value="20">20</option><option value="21">21</option><option value="22">22</option>
										<option value="23">23</option><option value="24">24</option><option value="25">25</option>
										<option value="26">26</option><option value="27">27</option><option value="28">28</option>
										<option value="29">29</option><option value="30">30</option><option value="31">31</option>
										<option value="32">32</option><option value="33">33</option><option value="34">34</option>
										<option value="35">35</option><option value="36">36</option><option value="37">37</option>
										<option value="38">38</option><option value="39">39</option><option value="41">41</option>
										<option value="42">42</option><option value="43">43</option><option value="44">44</option>
										<option value="45">45</option><option value="46">46</option><option value="47">47</option>
										<option value="48">48</option><option value="49">49</option><option value="50">50</option>
										<option value="51">51</option><option value="52">52</option><option value="53">53</option>
										<option value="54">54</option><option value="55">55</option><option value="56">56</option>
										<option value="57">57</option><option value="58">58</option><option value="59">59</option>
								</s:select>
					 		</div>
					 		
			    			<div class="element-input col-lg-6" style="width: 10%;"> 
						 			<s:select id="cmbresAMPM" name="cmbresAMPM" path="strAMPM">
											<option value="AM">AM</option>
								    		<option value="PM">PM</option>
							 		</s:select>
							</div>
					 	</div>
					</div>
					<div>
						<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Table Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input type="text" id="txtTableName" readonly="true" path="strTableName" ondblclick="funHelp('POSTableReserveMaster')" />
								<s:input type="hidden" id="txtTableNo" path="strTableNo" />
							</div>
							<div class="element-input col-lg-6" style="width: 7%;margin-left: 2%""> 
		    					<label class="title">Pax</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input type="text" id="txtPax" path="intPax" style="width: 100%;"/>
							</div>
					 	</div>
				 	</div>
				 	
				 	<div>
					 	<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					 		<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
						 	<div class="element-input col-lg-6" style="width: 22.5%;"> 
									<s:select id="cmbPOSName" name="cmbPOSName" path="strPOS" items="${posList}" />
							</div>
					 	</div>
				 	</div>
				 	<div>
					 	<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
					 		<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Special Information</label>
		    				</div>
						 	<div class="element-input col-lg-6" style="width: 20%;"> 
									<s:input type="text" id="txtInfo" path="strInfo" />
							</div>
					 	</div>
				 	</div>
				 	<div>
					 	<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%; ">
				     		  	<p align="center">
				            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit" /></div>
				           			<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()"/></div>
				     	  		</p>
		   			    </div>
		           </div>
				</div>
				
			</div>
			
			
<!-- 	End Of Tab1			 -->

<!-- 	Start of Tab2 		 -->

					
		  <div id="tab2" class="tab_content">
			
			
				<div class="title">
				      <div>
						<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:input id="txtdteFrom" name="txtdteFrom" path=""  style="width: 100%;"/>
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:input id="txtdteTo" name="txtdteTo" path="" style="width: 100%;"/>
							</div>
					 	 </div>
					 </div>	
				 	
				 	 <div>
						<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">From Time</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 10%;"> 
								<s:select id="cmbHH" name="cmbHH" path="" >
										<option value="HH">HH</option>
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="6">6</option>
										<option value="7">7</option> 
										<option value="8">8</option>
										<option value="9">9</option>
										<option value="10">10</option>
										<option value="11">11</option>
										<option value="12">12</option>
					 			</s:select>
					 		</div>
		    				<div class="element-input col-lg-6" style="width: 10%;"> 
					 			<s:select id="cmbMM" name="cmbMM" path="">
										<option value="MM">MM</option><option value="00">00</option><option value="01">01</option>
										<option value="02">02</option><option value="03">03</option><option value="04">04</option>
										<option value="05">05</option><option value="06">06</option><option value="07">07</option> 
										<option value="08">08</option><option value="09">09</option><option value="10">10</option>
										<option value="11">11</option><option value="12">12</option><option value="13">13</option>
										<option value="14">14</option><option value="15">15</option><option value="16">16</option>
										<option value="17">17</option><option value="18">18</option><option value="19">19</option>
										<option value="20">20</option><option value="21">21</option><option value="22">22</option>
										<option value="23">23</option><option value="24">24</option><option value="25">25</option>
										<option value="26">26</option><option value="27">27</option><option value="28">28</option>
										<option value="29">29</option><option value="30">30</option><option value="31">31</option>
										<option value="32">32</option><option value="33">33</option><option value="34">34</option>
										<option value="35">35</option><option value="36">36</option><option value="37">37</option>
										<option value="38">38</option><option value="39">39</option><option value="41">41</option>
										<option value="42">42</option><option value="43">43</option><option value="44">44</option>
										<option value="45">45</option><option value="46">46</option><option value="47">47</option>
										<option value="48">48</option><option value="49">49</option><option value="50">50</option>
										<option value="51">51</option><option value="52">52</option><option value="53">53</option>
										<option value="54">54</option><option value="55">55</option><option value="56">56</option>
										<option value="57">57</option><option value="58">58</option><option value="59">59</option>
								</s:select>
							</div>
		    				<div class="element-input col-lg-6" style="width: 10%;">
		    					<s:select id="cmbAMPM" name="cmbAMPM" path="">
										<option value="AM">AM</option>
									 	<option value="PM">PM</option>
								</s:select>
		    				</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">To Time</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 10%;"> 
								<s:select id="cmbToHH" name="cmbToHH" path="">
										<option value="HH">HH</option>
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="6">6</option>
										<option value="7">7</option> 
										<option value="8">8</option>
										<option value="9">9</option>
										<option value="10">10</option>
										<option value="11">11</option>
										<option value="12">12</option>
								</s:select>
						  	</div>
			    			<div class="element-input col-lg-6" style="width: 10%;"> 
									<s:select id="cmbToMM" name="cmbToMM" path="">
											<option value="MM">MM</option><option value="00">00</option><option value="01">10</option>
											<option value="02">02</option><option value="03">03</option><option value="04">04</option>
											<option value="05">05</option><option value="06">06</option><option value="07">07</option> 
											<option value="08">08</option><option value="09">09</option><option value="10">10</option>
											<option value="11">11</option><option value="12">12</option><option value="13">13</option>
											<option value="14">14</option><option value="15">15</option><option value="16">16</option>
											<option value="17">17</option><option value="18">18</option><option value="19">19</option>
											<option value="20">20</option><option value="21">21</option><option value="22">22</option>
											<option value="23">23</option><option value="24">24</option><option value="25">25</option>
											<option value="26">26</option><option value="27">27</option><option value="28">28</option>
											<option value="29">29</option><option value="30">30</option><option value="31">31</option>
											<option value="32">32</option><option value="33">33</option><option value="34">34</option>
											<option value="35">35</option><option value="36">36</option><option value="37">37</option>
											<option value="38">38</option><option value="39">39</option><option value="41">41</option>
											<option value="42">42</option><option value="43">43</option><option value="44">44</option>
											<option value="45">45</option><option value="46">46</option><option value="47">47</option>
											<option value="48">48</option><option value="49">49</option><option value="50">50</option>
											<option value="51">51</option><option value="52">52</option><option value="53">53</option>
											<option value="54">54</option><option value="55">55</option><option value="56">56</option>
											<option value="57">57</option><option value="58">58</option><option value="59">59</option>
									</s:select>
							</div>
			    			<div class="element-input col-lg-6" style="width: 10%;"> 
								 <s:select id="cmbToAMPM" name="cmbToAMPM" path="" >
										<option value="AM">AM</option>
						 				<option value="PM">PM</option>
						 		</s:select>
							</div>
					
					</div>
				   </div>	
					<div>
						<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
								
			    			<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;">
								  	<p align="center">
											<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnExecute" type="button" value="Execute" ></input></div>
											<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnRemove" type="button" value="Cancel" onclick="return btnRemove_onclick();"></input></div>
											<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="Reset" onclick="funResetFields()"/></div>
									</p>
				  			</div>
				  				
			    		</div>
		    		</div>
		    		<div>
			    		<div style="background-color: #fff;margin-bottom: 10px;">
			    			
			    			<table style="width: 85%;  border: 1px solid #ccc;" >
								 <thead style="background-color: #85cdffe6;">
										<tr>
											<th style="width:12%; border: #ccc 1px solid;">Contact No</th>
											<th style="width:15% ; border: #ccc 1px solid;">Customer Name</th>
											<th style="width:10% ; border: #ccc 1px solid;">Smoking</th>
											<th style="width:10%; border: #ccc 1px solid;">Table</th>
											<th style="width:8%; border: #ccc 1px solid;">PAX</th>
											<th style="width:12%; border: #ccc 1px solid;">Date</th>
											<th style="width:12%; border: #ccc 1px solid;">Time</th>
											<th style="width:21%; border: #ccc 1px solid;">Special Info</th>
										</tr>
								</thead>
							</table>
							
							<div style=" border: 1px solid #ccc;height: 150px;overflow-y: scroll; width: 85%;">
					
								<table id="tblReservation" style="width: 100%; ">
										<tbody>
										</tbody>
								</table>
					
							</div>
			    			
			    		</div>
		    		</div>

			</div>	
				 
		</div>

	</div>
</div>
</s:form>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>POSWise Item Incentive</title>
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
 	<%-- var fieldName;
 	
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
		
		  $("form").submit(function(event){
			  if($("#txtZoneName").val().trim()=="")
				{
					alert("Please Enter Zone Name");
					return false;
				}
			  if($("#txtZoneName").val().length > 30)
				{
					alert("Zone Name length must be less than 30");
					return false;
				}
			 
			  else{
				  flg=funCallFormAction();
				  return flg;
			  }
			});
	});
	  	
  	 --%>
  	 function funExecute()
  	 {
  		if(funDeleteTableAllRows()){
				funFetchColNames();
			}
  	 }
  	 
	 function funDeleteTableAllRows()
	 {
	 	$('#tblListData tbody').empty();
	 	
	 	var table = document.getElementById("tblListData");
	 	var rowCount1 = table.rows.length;
	 	if(rowCount1==0){
	 		return true;
	 	}else{
	 		return false;
	 	}
	 }
	 
  	 function funFetchColNames() {
 	 	
 	 	var POSCode=$('#cmbPOSName').val();
 	 	
 	 	var gurl = getContextPath()+"/loadPOSWiseItemIncentiveData.html";
 	 	
 	 	
 	 	$.ajax({
 	 		type : "POST",
 	 		data:{ 
 	 			POSCode:POSCode,
 	 				
 	 			},
 	 		url : gurl,
 	 		dataType : "json",
 	 		success : function(response) {
 	 		 	if (response== 0) 
 	 			{
 	 				alert("Data Not Found");
 	 			} 
 	 			else 
 	 			{ 
 	 					$.each(response.list,function(i,item){
 		            	funFillTableCol(item[0],item[1],item[2],item[3],item[4]);
 	             		});
 	 			}
 	 		}
 	 			
 	 });
 	 }
  	 
  	function funFillTableCol(item0,item1,item2,item3,item4)
	{
		var table = document.getElementById("tblListData");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		var comboitem="";
		if(item2=="Amt" || item2=="amt")
			{
			comboitem="per ";
			}
		else
			{
			comboitem="amt ";
			}
			

	      /*row.insertCell(0).innerHTML= "<input   class=\"Box \" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strBillNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; */
	      row.insertCell(0).innerHTML= "<input    readonly=\"readonly\" class=\"Box \" size=\"28%\" name=\"listItemIncentive["+(rowCount)+"].strItemCode\" id=\"txtDate."+(rowCount)+"\" value='"+item0+"' />"; 
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"33%\" name=\"listItemIncentive["+(rowCount)+"].strItemName\" id=\"txtDate."+(rowCount)+"\" value='"+item1+"' />";
	      row.insertCell(2).innerHTML= "<input readonly=\"readonly\" class=\"Box \" size=\"10%\"><select  readonly=\"readonly\" class=\"Box \" style=\"width: 60%;margin-left: 15px;\" name=\"listItemIncentive["+(rowCount)+"].strIncentiveType\"  id=\"txtCompStk\" value='"+item2+"'onclick=\"creatBrandOptions(this)\"> <OPTION>"+item2 +"</OPTION><OPTION>"+comboitem+"</OPTION></SELECT>";
	      row.insertCell(3).innerHTML= "<input style=\"text-align: right;\" size=\"28%\" class=\"Box \" name=\"listItemIncentive["+(rowCount)+"].strIncentiveValue\" id=\"txtPhyStk."+(rowCount)+"\" value='"+item3+"' />";
	      row.insertCell(4).innerHTML= "<input   type=\"hidden\" size=\"0\" class=\"Box \" size=\"0%\" name=\"listItemIncentive["+(rowCount)+"].strPOSCode\" id=\"txtPhyStk."+(rowCount)+"\" value='"+item4+"' />";
	   
	}
/*   	 <select name="combo" id="combo"></select>  type=\"hidden\"  */  
		  /* function val(e)
		  {-1!==$.inArray(e.keyCode,[46,8,9,27,13,110,190])||/65|67|86|88/.test(e.keyCode)&&(!0===e.ctrlKey||!0===e.metaKey)||35<=e.keyCode&&40>=e.keyCode||(e.shiftKey||48>e.keyCode||57<e.keyCode)&&(96>e.keyCode||105<e.keyCode)&&e.preventDefault()
			  }
	 */
	 var count=0;
	 function creatBrandOptions(item){
		   
		        // $('<option value="abc">'+a+'</option>').appendTo('#txtCompStk');
    var select = document.getElementById('txtCompStk');
   // if(count=0)
    	
		if(item==amt)
			{
		    select.options[select.options.length] = new Option('per', 'per');
			}
		else if(item==per)
			{
			  select.options[select.options.length] = new Option('amt', 'amt');
			} 
    	
	}
	 function selectComboBox()
	 {
		
		 innerHTML="<select id=\"cmbPOSName\"   items=\"${posList}\" >"
	 }

	 function funResetFields()
	 {
			$('#tblListData tbody').empty();	 
	 }
</script>
</head>
<body>

	<div id="formHeading">
	<label>POSWise Item Incentive</label>
	</div>
<br/>
<br/>

	<s:form name="POSWiseItemIncentive" method="POST" action="savePOSWiseItemIncentive.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;" >
		
		<div class="title" style="margin-left: 100px;">
		
			<div class="row" style="background-color: #fff;display: -webkit-box;">
				<div class="element-input col-lg-6" style=" width: 15%;"> 
    				<label class="title">POS Name</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom:  10px;width: 30%;"> 
					<s:select id="cmbPOSName" path="strPOSName" items="${posList}" >
					</s:select>
				</div>
				
				<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 50%;">
			 			<div class="submit col-lg-4 col-sm-4 col-xs-4">
							<input type="button" value="Execute" id="execute" onclick="funExecute()"/>
						</div>
						<div class="submit col-lg-4 col-sm-4 col-xs-4">
							<input type="submit" value="Save"  />
						</div>
						<div class="submit col-lg-4 col-sm-4 col-xs-4">
							<input type="reset" value="Reset" onclick="funResetFields()"/>
						</div>
				</div>
				
			 </div>
			 
			<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
			 
					 	<div style="border: 1px solid #ccc; display: block; height: 300px; overflow-x: hidden; overflow-y: scroll; width: 80%;">
					 	
								<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;">
			    					<thead>
			        					<tr> 
			            					<th style="width:10%">Item Code</th>
											<th style="width:12%">Item Name</th>
											<th style="width:5%">Incentive Type</th>
											<th style="width:10%">Incentive Value</th>
			        					</tr>	
			    					</thead>
			    				</table>
			    		
			    				<table id="tblListData" style="width: 100%;">
			    					<tbody style="border-top: none;">				
								
									</tbody>
			    				</table>
			    				
			
			   	 		 </div>	
			</div>
			 
			 
		</div>
		
<!-- 		<table class="masterTable"> -->
<!-- 			<tr> -->
<!-- 				<td width="140px">POS Name</td> -->
<%-- 				<td colspan="3"><s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" cssClass="BoxW124px" items="${posList}" > --%>
					
<%-- 				 </s:select></td> --%>
<!-- 				   <td> -->
<!-- 				  	 <input type="button" value="Execute" tabindex="3" class="form_button"  id="execute" onclick="funExecute()"/> -->
<!-- 				   </td> -->
<!-- 				 <td> -->
<!-- 				 <input type="submit" value="Save" tabindex="3" class="form_button" /> -->
<!-- 				 </td> -->
<!-- 				  <td> -->
<!-- 				 <input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/> -->
<!-- 				 </td> -->
				
<!-- 			</tr> -->
			
			
<!-- 		</table> -->

<!-- <table id="tblListHeader" class="transTablex" -->
<!-- 					style="width: 80%;  text-align: center !important; "> -->
			
<!-- 			<th style="border: 1px white solid;width:10%">Item Code<label></label></th> -->
<!-- 			<th style="border: 1px white solid;width:10%">Item Name<label></label></th> -->
<!-- 			<th style="border: 1px white solid;width:10%">Incentive Type<label></label></th> -->
<!-- 			<th style="border: 1px white solid;width:11%">Incentive Value<label></label></th> -->
		
			
			
<!-- 				</table> -->
				
				
				
				
<!-- 				<table id="tblListData" class="transTablex" -->
<!-- 					style="width: 80%; background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 400px; text-align: center !important;"> -->
				
				
<%-- 											<col style="width:10%"><!--  COl1   --> --%>
<%-- 											<col style="width:10%"><!--  COl2   --> --%>
<%-- 											<col style="width:10%"><!--  COl3   --> --%>
<%-- 											<col style="width:10%"><!--  COl4   --> --%>
<%-- 											<col style="width:1%"><!--  COl4   --> --%>
											
											
<!-- 				</table> -->
	</s:form>
</body>
</html>

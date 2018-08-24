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
 		            	funFillTableCol(item[0],item[1],item[2],item[3],item[4],item[5],item[6]);
 	             		});
 	 			}
 	 		}
 	 			
 	 });
 	 }
  	 
  	function funFillTableCol(item0,item1,item2,item3,item4,item5,item6)
	{
		var table = document.getElementById("tblListData");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		var comboitem="";
		if(item5=="Amt" || item5=="amt")
			{
			comboitem="per ";
			}
		else
			{
			comboitem="amt ";
			}
		   row.insertCell(0).innerHTML= "<input    readonly=\"readonly\" class=\"Box \" size=\"15%\" name=\"listItemIncentive["+(rowCount)+"].strItemCode\" id=\"txtItemCode."+(rowCount)+"\" value='"+item0+"' />"; 
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"30%\" name=\"listItemIncentive["+(rowCount)+"].strItemName\" id=\"txtItemName."+(rowCount)+"\" value='"+item1+"' />";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"20%\" name=\"listItemIncentive["+(rowCount)+"].strGroupName\" id=\"txtGroupName."+(rowCount)+"\" value='"+item2+"' />";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"20%\" name=\"listItemIncentive["+(rowCount)+"].strSubGroupName\" id=\"txtSubGroupName."+(rowCount)+"\" value='"+item3+"' />";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"20%\" name=\"listItemIncentive["+(rowCount)+"].strPOSName\" id=\"txtPOSName."+(rowCount)+"\" value='"+item4+"' />";
	      row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box \" size=\"10%\"><select  readonly=\"readonly\" class=\"Box \" style=\"width: 60%;margin-left: 15px;\" name=\"listItemIncentive["+(rowCount)+"].strIncentiveType\"  id=\"txtCompStk\" value='"+item5+"'onclick=\"creatBrandOptions(this)\"> <OPTION>"+item5 +"</OPTION><OPTION>"+comboitem+"</OPTION></SELECT>";
	      row.insertCell(6).innerHTML= "<input style=\"text-align: right;\" size=\"10%\" class=\"Box \" name=\"listItemIncentive["+(rowCount)+"].strIncentiveValue\" id=\"txtPhyStk."+(rowCount)+"\" value='"+item6+"' />";
	      
	   
	}
	 var count=0;
	 function creatBrandOptions(item){
		   

    var select = document.getElementById('txtCompStk');

    	
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

	<s:form name="POSWiseItemIncentive" method="POST" action="savePOSWiseItemIncentive.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;" >
		
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
			 
					 	<div style="border: 1px solid #ccc; display: block; height: 600px; overflow-x: scroll; overflow-y: scroll; width: 100%;">
					 	
								<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;">
			    					<thead>
			        					<tr> 
			            					<th style="width:11.4%">Code</th>
											<th style="width:20.4%">Name</th>
											<th style="width:14.9%">Group</th>
											<th style="width:14.6%">Sub Group</th>
											<th style="width:15.2%">POS Name</th>
											<th style="width:11.1%">Incentive Type</th>
											<th style="width:6%">Incentive Value</th>
			        					</tr>	
			    					</thead>
			    				</table>
			    		
			    				<table id="tblListData" style="width: 100%; overflow-x: scroll;">
			    					<tbody style="border-top: none;">				
								
									</tbody>
			    				</table>
			   	 		 </div>	
			</div>
		</div>
	</s:form>
</body>
</html>

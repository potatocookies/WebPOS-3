<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>



<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AREA MASTER</title>
		
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

 $(document).ready(function () {
// 		  $('input#txtAreaCode').mlKeyboard({layout: 'en_US'});
// 		  $('input#txtAreaName').mlKeyboard({layout: 'en_US'});
		  
		  $("form").submit(function(event){

			});
		}); 




	/**
	* Reset  Form
	**/
	function funResetFields()
	{
	$("#txtFilePath").val("");
		
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

		function funExportExcelData()
		{
			var searchurl=getContextPath()+"/funExportData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "text",
				        async: false,
				        success: function(response)
				        {
				        	alert(response);
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
		function funImportExcelData()
		{
			var masterName = $("#cmbMasterName").val();
			var industryType = $("#cmbIndustryType").val();
					var file = new FormData();    
					file.append("file", $('#myFile').get(0).files[0]);
				    searchUrl=getContextPath()+"/importExcelData.html?masterName="+masterName+"&industryType="+industryType;	
			        $.ajax({
			            
		                url : searchUrl,
			            type: "POST",
		                data: file,
		                mimeType: "multipart/form-data",
		                contentType: false,
		                cache: false,
		                processData: false,
		                dataType: "text",
			            success : function(response) 
			            {
			            	alert(response);
			            },
			            error: function(jqXHR, exception)
						{
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
		
		function setfilename(val)
		{
		  var fileName = val.substr(val.lastIndexOf("\\")+1, val.length);
		 document.getElementById("txtFilePath").value = fileName;
		}
</script>


</head>

<body >
	<div id="formHeading">
		<label>Import Masters</label>
	</div>

	<s:form name="ImpostMasters" method="POST" action=""  class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;" > 
	
		<br />
		<br />
		
	 <div class="title" style="margin-left: 25%;">
	 
		
		<div class="row" style="background-color: #fff;margin-bottom:  10px;display: -webkit-box;">	
			<div class="element-input col-lg-6">
					<label class="title">Select Master</label>
			</div>
			<div class="element-input col-lg-6"  style="width: 30%;">
					<s:select id="cmbMasterName" path="strAreaCode" items="${listOfMasters}"  style="width: 100%;"/>
			</div>
			
		</div>
		<div class="row" style="background-color: #fff;margin-bottom:  10px;display: -webkit-box;">	
			<div class="element-input col-lg-6">
					<label class="title">Industry Type</label>
			</div>
			<div class="element-input col-lg-6"  style="width: 30%;">
					<s:select id="cmbIndustryType" path="strAreaName" items="${listOfIndustryType}"  style="width: 100%;"/>
			</div>
			
		</div>
		
		<div class="row" style="background-color: #fff;margin-bottom:  10px;display: -webkit-box;">
			<div class="element-input col-lg-6"> 
    			<label class="title">Select File</label>
    		</div>
    		<div class="element-input col-lg-6"  style="width: 30%;"> 
				<s:input class="large" type="text" id="txtFilePath" path="strPOSName" style="width: 100%;"/>
			</div>
			<div class="element-input col-lg-6"  style="width: 30%;"> 
				 <input type="file" id="myFile"  Width="5%"  onchange="setfilename(this.value);">
			</div>
			
		</div>
		
		
		 <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%;">
    			<p align="center">
           		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="IMPORT" onclick="funImportExcelData();"/></div>
         		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="EXPORT" onclick="funExportExcelData();"/></div>
         		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="CLOSE" onclick="funPOSHome()"/></div>
           		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="RESET" onclick="funResetFields()"></div>
    			</p>
  		</div>
   			
		</div>
		
	</div>

	</s:form>

</body>
</html>

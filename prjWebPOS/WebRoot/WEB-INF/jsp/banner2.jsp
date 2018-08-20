<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<style type="text/css">
  #tblNotify tr:hover{
	background-color: #72BEFC;
	
}
</style>
<script type="text/javascript">

$(document).ready(function(){
	//window.location.href=getContextPath()+"/loadPendingRequisition.html";
   		$("#MainDiv").hide();
   			funGetNotification();
   		
   	    $("#notification").click(function(){
   	        $("#MainDiv").fadeToggle();
   	    });
   	    
   	});
</script> 

</head>
<body>
	<table id="page_top_banner">
		<thead style="">
			<tr>
				 <th style="width: 50%; text-align: left;font-weight: bold;font-size: 11px;text-transform: uppercase;padding-top: 5px;padding-bottom: 5px; FONT-FAMILY: trebuchet ms, Helvetica, sans-serif;">${companyName} &nbsp;-&nbsp; ${financialYear} &nbsp;-&nbsp; ${propertyName} &nbsp;-&nbsp; ${locationName}</th>
				 <th style="width: 34%;"></th>
				 <th id="notification" style="width: 4%;font-weight: bold;font-size: 11px; padding-left: 23px;padding-bottom: 8px;">
					<div style=" background-color: #A33519; margin-left: 18px;margin-top: -5px; position: absolute;text-align: center;width: 15px;">
					<label id="lblNotifyCount"></label>
					</div>
					<img  src="../${pageContext.request.contextPath}/resources/images/Notification.png" title="Notification" height="20px" width="20px">
					
				</th> 
				<th style="width: 4%;padding-bottom: 6px;padding-left: 15px;"> 
				<img  src="../${pageContext.request.contextPath}/resources/images/help.png" onclick="funGetFormName()" title="HELP" height="20px" width="20px"> &nbsp;&nbsp;
				</th>
				
				<th style="width: 4%;padding-bottom: 8px;"><a href="frmHome.html"  style="text-decoration:underline ;color: white;"><img  src="../${pageContext.request.contextPath}/resources/images/home.png" title="HOME" height="20px" width="20px"></a>
				</th>
				<th style="width: 4%;padding-bottom: 8px;"><a href="frmChangeModuleSelection.html" style="text-decoration:underline ;color: white; padding-bottom: 16px;"><img  src="../${pageContext.request.contextPath}/resources/images/ModuleSelection.png" title="Change Module" height="20px" width="20px"></a>
				</th >
				<th style="width: 4%;padding-bottom: 8px;"><a href="logout.html" style="text-decoration:underline;color: white; padding-bottom: 16px;"><img  src="../${pageContext.request.contextPath}/resources/images/logout.png" title="LOGOUT" height="20px" width="20px" ></a>
				</th> 
			</tr>
		</thead>
	</table>
	 <div id="MainDiv"
		style="background-color: #FFFFFF; 
		border: 1px solid #ccc; height: 238px; margin: auto;
		 overflow-x: hidden; overflow-y: scroll; width: 30%;
		 position: absolute; z-index: 1; right: 3.5%;">

		<table id="tblNotify"
			style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll;"
			class="transTablex">
			<tbody id="tbodyNotifyid">
			<tr><td colspan="4">Notifications</td></tr>
			<c:forEach items="${Notifcation}" var="draw1" varStatus="status1">
			<tr>
				<td>${draw1.strReqCode}</td>
				<td>${draw1.dtReqDate}</td>
				<td>${draw1.Locationby}</td>
				<td>${draw1.LocationOn}</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
	</div> 
</body>
</html>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title><tiles:insertAttribute name="title" ignore="true"></tiles:insertAttribute>
</title>

</head>

<body >

<div style="max-height: 100%;" >
    <tiles:insertAttribute name="header"></tiles:insertAttribute>
</div>
<hr style="border: 1px solid #ccc; ">


<div class="row containerPOS">
<!--     <div class="content"> -->
     
        <div class="contentsPOS">
           <tiles:insertAttribute name="body"></tiles:insertAttribute>
        </div>
<!--     </div> -->
    
    
    <div id="loginfooter">
<!-- 			<!-- id="loginfooter"> --> 
			<tiles:insertAttribute name="footer"></tiles:insertAttribute>
<!-- 			<!-- end .footer --> 
<!-- 		</div> -->
</div>

	

</body>
</html>





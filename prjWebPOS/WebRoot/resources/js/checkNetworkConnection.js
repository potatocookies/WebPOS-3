/**
 * 
 */



function funCheckNetworkConnection()
{
	var searchUrl=getContextPath()+"/checkNetworkConnection.html";
	$.ajax({
        type: "GET",
        url: searchUrl,
        dataType: "text",
        
        success: function(response)
        {
       
        },
        error: function(jqXHR, exception) {
                alert('Network Issue');
   
        }
	});
	 $(document).ajaxStart(function(){
	 		$("#wait").css("display","none");
			  });
}
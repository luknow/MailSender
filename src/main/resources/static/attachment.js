$(document).ready(function () {

	$("#attachmentErrorAlert").hide();	
			  
	//User has pressed "Dodaj zalacznik" button
    $("#btnSubmit").click(function (event) {
    
   		 $("#attachmentErrorAlert").hide();
    
    	//Blocking automatic sending data via form - sending data via Ajax instead
		event.preventDefault();
		
		//Checking if attachment size exceeds allowed size	
		if(document.getElementById('upload').files[0].size > 10485760){
			$("#attachmentErrorAlert").text("Przekroczono dopuszczalną wielkość załącznika");
			$("#attachmentErrorAlert").show();
			document.getElementById('upload').value = null;
			return;
		}
		
		//Getting attachments form
		var form = $('#fileUploadForm')[0];
		
		//Getting file attached to form
		var data = new FormData(form);
		
		//Getting filename
		var filename = document.getElementById('upload').files[0].name;
		
		//Clearing the form
		document.getElementById('upload').value = null;
		
		//Shorten filename - better formatting in view
		if(filename.length > 50){
			filename = shorterFilename(filename);
		}
		
		//Sending request to server to upload attachment
        $.ajax({
			type: "POST",
			enctype: 'multipart/form-data',
			url: "/addAttachment",
			data: data,
			processData: false,
			contentType: false,
			cache: false,
			timeout: 600000,
			success: function (data) {
				//There has not been any problem on the server side with file
				//Adding information that the attachment has been attached correctly
				var attachmentsList = document.getElementById('attachmentsList');
				attachmentsList.insertAdjacentHTML('beforeend', '<div class="panel panel-default text-center"><div class="panel-body"><span class="fa-stack fa-2x"><i class="fa fa-square fa-stack-2x"></i><i class="fa fa-paperclip fa-stack-1x fa-inverse"></i></span></div><div class="panel-footer">'+filename+'</div>');
			},
			error: function (e) {
				//There has been  some problem on the server side with file
				//Adding information to the view
				$("#attachmentErrorAlert").text(e.responseText);
				$("#attachmentErrorAlert").show().delay(5000).hide();
			}
		});

	});

	//Sending request to server to get filenames
	//If the page has been refreshed e.g. when the form has been filled incorrectly 
	//you need to complete the information in the view about attached files
	$.ajax({
		type: "POST",
        enctype: 'multipart/form-data',
        url: "/getAttachments",
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
        	//There has not been any problem on the server side, attachments list is not empty
        	//Adding information about attached files
			if(data.length > 0){
				for(var i=0;i<data.length;i++){
					var attachmentsList = document.getElementById('attachmentsList');
						if(data[i].length > 50){
							data[i] = shorterFilename(data[i]);
						}
					attachmentsList.insertAdjacentHTML('beforeend', '<div class="panel panel-default text-center"><div class="panel-body"><span class="fa-stack fa-2x"><i class="fa fa-square fa-stack-2x"></i><i class="fa fa-paperclip fa-stack-1x fa-inverse"></i></span></div><div class="panel-footer">'+data[i]+'</div>');
				}
			}
        },
        error: function (e) {
			//There has been  some problem on the server side with file
			//Adding information to the view
            $("#attachmentErrorAlert").text(e.responseText);
			$("#attachmentErrorAlert").show().delay(5000).hide();
        }
    });
	 
	 //Function that shortens the filename to fit in the view
	 //Filename has to be at most 50 characters long 
	 function shorterFilename(filename){
		filenameExtension = filename.split('.').pop();
		filename = filename.substring(0, (50-filenameExtension.length-6));
		filename = filename+"(...)."+filenameExtension;
		return filename;
	 }
});
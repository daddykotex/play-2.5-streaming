$(document).ready(function() {
	var form = $("#uploadForm");
	var fileInput = $("#file");

	form.submit(function(e){
        e.preventDefault();

        var file = fileInput[0].files[0];
		var url = form.attr("data-url");

        $.ajax({
            url: url,
            type: 'POST',
            xhr: function() {  // Custom XMLHttpRequest
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener("progress", function(evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = Math.floor(evt.loaded / evt.total * 100);
            			console.log('in progress: ' + percentComplete);
                    }
               }, false);
               return xhr;
            },
            data: file,

            cache: false,
            contentType: file.type,
            processData: false
        }).done(function(r) {
            alert(r);
        })
        .fail(function() {
            alert('failed');
        });
	});
});
window.onload = function() {
	var inp_password = document.querySelector('input[name=password]');
	var inp_confirmPassword = document.querySelector('input[name=confirmPassword]');
	
	document.querySelector('#confirmPassword').onchange = function() {
		if (inp_password.value != inp_confirmPassword.value) {
			document.querySelector('#message').innerHTML = "Пароли не совпадают";
		} else {
			document.querySelector('#message').innerHTML = "";
		}
	}
	var token = $("meta[name='_csrf']").attr("content");

	$('#login').change(function() {
		var str = document.querySelector('input[name=login]').value;
		var jsonData = { Login: str };
		$.ajax({
			type: "POST",
			url: "../api/user/isexists",
			headers: { "X-CSRF-TOKEN": token },
			data: JSON.stringify(jsonData),
			contentType: 'application/json',
			dataType: 'json',
			success: function(html) {
					document.querySelector('#messageLogin').innerHTML = html.message;				
			},
			error: function(err){
				$('#messageLogin').html("");
			}
		})
	});

	//	$.getJSON('../user', function(data) {
	//		$.each(data, function(key, val) {
	//			console.log(val.login);
	//		})
	//	})

}


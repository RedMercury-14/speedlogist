var token = $("meta[name='_csrf']").attr("content");
document.querySelector('input[id=login2]').addEventListener('change', (event)=>{
	var str = document.querySelector('input[id=login2]').value;
	var jsonData = { Login: str };
	$.ajax({
		type: "POST",
		url: "/speedlogist/api/user/isexists",
		headers: { "X-CSRF-TOKEN": token },
		data: JSON.stringify(jsonData),
		contentType: 'application/json',
		dataType: 'json',
		success: function(html) {
			document.querySelector('#messageLogin').innerHTML = html.message;
		},
		error: function(err) {
			$('#messageLogin').html("Логин доступен");
		}
	})
})
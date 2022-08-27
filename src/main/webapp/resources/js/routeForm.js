	var token = $("meta[name='_csrf']").attr("content");

//	$('#login').change(function() {
//		var str = document.querySelector('input[name=login]').value;
//		var jsonData = { Login: str };
//		$.ajax({
//			type: "POST",
//			url: "../api/user/isexists",
//			headers: { "X-CSRF-TOKEN": token },
//			data: JSON.stringify(jsonData),
//			contentType: 'application/json',
//			dataType: 'json',
//			success: function(html) {
//					document.querySelector('#messageLogin').innerHTML = html.message;				
//			},
//			error: function(err){
//				$('#messageLogin').html("");
//			}
//		})
//	});
	
		
		
		$('#routeDirection').change(function(){
			document.querySelector('#message').innerHTML = '';
			var target = document.querySelector('#routeDirection').value;
			$.getJSON('../../../api/simpleroute', function(data) {
			$.each(data, function(key, val) {
				if(val.routeDirection == target){
					document.querySelector('#message').innerHTML = 'Маршрут с данным названием уже соществует!';
				};
				
			})
		})
		})

$.getJSON('../../api/info/message/routes/from_me', function(data) {
	$.each(data, function(key, val) {
		var rowItem = document.querySelectorAll('tr');
		for (i = 1; i < rowItem.length; i++) {
			var rowItemI = rowItem[i];
			var target = rowItemI.querySelector('.none').innerHTML;
			if(target == val.idRoute){
				rowItemI.classList.add("activRow");
				rowItemI.querySelector('#offer').innerHTML = val.text;
			}
		}
	})
});
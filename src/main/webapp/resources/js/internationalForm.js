var numPoint = 1;
var token = $("meta[name='_csrf']").attr("content");
var adress;
var weight;
var pall;
let arr = new Array();

pointForm(numPoint);
function pointForm(numPoint) {
	let row = document.createElement('div');
	row.innerHTML = `<div id="${numPoint}" name = "${numPoint}">
			<label>Точка № ${numPoint}</label>
			<br>
			<div class="form-group">
			<label>Адрес:</label>
				<input name="adress" class="form-control" required="true"/>							
			</div>
				<br>
			<div class="form-group">
				<label>Вес:</label>
				<input name="weight" class="form-control" required="true"/>
			</div>
				<br>
			<div class="form-group">
				<label>Количество паллет:</label>
				<input name="pall" class="form-control" required="true"/>
			</div><br></div>`;
	document.querySelector('#content').appendChild(row);
}
document.querySelector("#button").addEventListener("mousedown", () => {
	var test = document.getElementById(numPoint);
	adress = test.querySelector('input[name=adress]').value;
	weight = test.querySelector('input[name=weight]').value;
	pall = test.querySelector('input[name=pall]').value;
	if (adress == '' || weight == '' || pall == "") {
		document.querySelector('#message').innerHTML = "Не заполнены все обязательные поля";
	} else {
		document.querySelector('#message').innerHTML = "";
		var json = { order: numPoint, address: adress, weight: weight, pall: pall };
		arr[numPoint - 1] = json;
		console.log(arr);
		numPoint++;
		pointForm(numPoint);
	}

});
document.querySelector("#next").addEventListener("mousedown", () => {
	var test = document.getElementById(numPoint);
	adress = test.querySelector('input[name=adress]').value;
	weight = test.querySelector('input[name=weight]').value;
	pall = test.querySelector('input[name=pall]').value;
	if (adress == '' || weight == '' || pall == "") {
		document.querySelector('#message').innerHTML = "Не заполнены все обязательные поля";
	} else {
		document.querySelector('#message').innerHTML = "";
		var json = { order: numPoint, address: adress, weight: weight, pall: pall };
		arr[numPoint - 1] = json;
		$.ajax({
			type: "POST",
			url: "../../../api/route/addpoints",
			headers: { "X-CSRF-TOKEN": token },
			data: JSON.stringify(arr),
			contentType: 'application/json',
			dataType: 'json',
			success: function(html) {
				console.log(html.message)
				var url = './addRoute'
				window.location.href = url;
			},
			error: function(err) {
				console.log("bad")
				console.log(err.message);
			}
		})
	}

});

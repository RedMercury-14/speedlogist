var numPoint = 1;
var token = $("meta[name='_csrf']").attr("content");
var adress;
var weight;
var pall;
var cargo;
var position;
var volume;
let arr = new Array();


pointFormLoad(numPoint);
function pointFormLoad(numPoint) {
	let row = document.createElement('div');
	row.innerHTML = `<div id="${numPoint}" name = "${numPoint}">
			<label id="position"><h2>Загрузка (точка № ${numPoint})</h2></label>
			<br>
			<div class="form-group">
			<label>Адрес:</label>
				<input name="adress" class="form-control" required="true"/>							
			</div>
				<br>
			<div class="form-group">
				<label>Вес, кг:</label>
				<input type="number" name="weight" class="form-control" required="true"/>
			</div>
				<br>
			<div class="form-group">
				<label>Количество паллет:</label>
				<input type="number" name="pall" class="form-control" required="true"/>
			</div>
				<br>
			<div class="form-group">
				<label>Объем, м<sup><small>3</small></sup>:</label>
				<input type="number" name="volume" class="form-control" />
			</div>
				<br>			
			<div class="form-group">
				<label>Наиминование груза:</label>
				<input name="cargo" class="form-control" required="true"/>
			</div>
				<br>
			</div>`;
	document.querySelector('#content').appendChild(row);
}
function pointFormUnload(numPoint) {
	let row = document.createElement('div');
	row.innerHTML = `<div id="${numPoint}" name = "${numPoint}">
			<label id="position"><h2>Выгрузка (точка № ${numPoint})</h2></label>
			<br>
			<div class="form-group">
			<label>Адрес:</label>
				<input name="adress" class="form-control" value="Логистический центр 24; Хатежинский сельсовет, 1
Минский район, РБ" required="true"/>							
			</div>
				<br>
			<div class="form-group">
				<label>Вес, кг:</label>
				<input type="number" name="weight" class="form-control" required="true"/>
			</div>
				<br>
			<div class="form-group">
				<label>Количество паллет:</label>
				<input type="number" name="pall" class="form-control" required="true"/>
			</div>
				<br>
			<div class="form-group">
				<label>Объем, м<sup><small>3</small></sup>:</label>
				<input type="number" name="volume" class="form-control"/>
			</div>
				<br>			
			<div class="form-group">
				<label>Наиминование груза:</label>
				<input name="cargo" class="form-control" required="true"/>
			</div>
				<br>
			</div>`;
	document.querySelector('#content').appendChild(row);
}
//кнопка добавления точки выгрузки
document.querySelector("#button").addEventListener("mousedown", () => {
	var test = document.getElementById(numPoint);
	adress = test.querySelector('input[name=adress]').value;
	weight = test.querySelector('input[name=weight]').value;
	pall = test.querySelector('input[name=pall]').value;
	cargo = test.querySelector('input[name=cargo]').value;
	position = test.querySelector('h2').innerHTML;
	volume = test.querySelector('input[name=volume]').value;
	if (adress == '' || weight == '' || pall == "" || cargo == "" ) {
		document.querySelector('#message').innerHTML = "Не заполнены все обязательные поля";
	} else {
		document.querySelector('#message').innerHTML = "";
		var json = { order: numPoint, address: adress, weight: weight, pall: pall, cargo: cargo, position: position, volume: volume };
		arr[numPoint - 1] = json;
		numPoint++;
		pointFormUnload(numPoint);
	}

});
//кнопка добавления точки загрузки
document.querySelector("#button2").addEventListener("mousedown", () => {
	var test = document.getElementById(numPoint);
	adress = test.querySelector('input[name=adress]').value;
	weight = test.querySelector('input[name=weight]').value;
	pall = test.querySelector('input[name=pall]').value;
	cargo = test.querySelector('input[name=cargo]').value;
	position = test.querySelector('h2').innerHTML;
	volume = test.querySelector('input[name=volume]').value;
	if (adress == '' || weight == '' || pall == "" || cargo == "" ) {
		document.querySelector('#message').innerHTML = "Не заполнены все обязательные поля";
	} else {
		document.querySelector('#message').innerHTML = "";
		var json = { order: numPoint, address: adress, weight: weight, pall: pall, cargo: cargo, position: position, volume: volume };
		arr[numPoint - 1] = json;
		numPoint++;
		pointFormLoad(numPoint);
	}

});
document.querySelector("#next").addEventListener("mousedown", () => {
	var test = document.getElementById(numPoint);
	adress = test.querySelector('input[name=adress]').value;
	weight = test.querySelector('input[name=weight]').value;
	pall = test.querySelector('input[name=pall]').value;
	cargo = test.querySelector('input[name=cargo]').value;
	position = test.querySelector('h2').innerHTML;
	volume = test.querySelector('input[name=volume]').value;
	if (adress == '' || weight == '' || pall == "" || cargo == "" ) {
		document.querySelector('#message').innerHTML = "Не заполнены все обязательные поля";
	} else {
		document.querySelector('#message').innerHTML = "";
		var json = { order: numPoint, address: adress, weight: weight, pall: pall, cargo: cargo, position: position, volume: volume };
		arr[numPoint - 1] = json;
		console.log(arr);
		$.ajax({
			type: "POST",
			url: "../../../api/route/addpoints",
			headers: { "X-CSRF-TOKEN": token },
			data: JSON.stringify(arr),
			contentType: 'application/json',
			dataType: 'json',
			success: function(html) {
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



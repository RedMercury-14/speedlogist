import { addPointsUrl } from './globalConstants/urls.js';

var numPoint = 1;
var token = $("meta[name='_csrf']").attr("content");
//var flag = document.querySelector('#flag').value;
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
			<label>Страна:</label>		
				<div class="autocomplete">		
    				<input id="myInput" type="text" name="myCountry" class="form-control" required="true"/>
				</div>										
			</div>
			<div class="form-group">
			<label>Город:</label>
				<input name="city" class="form-control" required="true"/>							
			</div>			
			<div class="form-group">
			<label>Адрес:</label>
				<input name="adress" class="form-control" required="true"/>							
			</div>
			<div class="form-group">
				<label>Вес, кг:</label>
				<input type="number" name="weight" class="form-control" required="true"/>
			</div>
			<div class="form-group">
				<label>Количество паллет:</label>
				<input type="number" name="pall" class="form-control" required="true"/>
			</div>
			<div class="form-group">
				<label>Объем, м<sup><small>3</small></sup>:</label>
				<input type="text" name="volume" class="form-control" />
			</div>			
			<div class="form-group">
				<label>Наиминование груза:</label>
				<input name="cargo" class="form-control" required="true"/>
			</div>
			</div>`;
	document.querySelector('#content').appendChild(row);
}

function pointFormUnload(numPoint) {
	var weight = document.querySelector('input[name=weight]').value;
	var pall = document.querySelector('input[name=pall]').value;
	var volume = document.querySelector('input[name=volume]').value;
	var cargo = document.querySelector('input[name=cargo]').value
	let row = document.createElement('div');
	row.innerHTML = `<div id="${numPoint}" name = "${numPoint}">
			<label id="position"><h2>Выгрузка (точка № ${numPoint})</h2></label>
			<br>
			<div class="form-group">
			<label>Страна:</label>		
				<div class="autocomplete">		
    				<input id="myInput" type="text" name="myCountry" class="form-control" required="true" value="BY Беларусь"/>
				</div>										
			</div>
			<div class="form-group">
			<label>Город:</label>
				<input name="city" class="form-control" list="cities" required="true" />	
				<datalist id="cities">
					<option value="Минск">
				    <option value="Кулики">
				    <option value="Таборы">
				    <option value="Прилесье">
				  </datalist>						
			</div>
			<div class="form-group">
			<label>Адрес:</label>
				<input name="adress" class="form-control" list="adresses" required="true"/>	
				  <datalist id="adresses">
				    <option value="Логистический центр 24; Хатежинский сельсовет, 1;Минский район">Логистический центр 24; Хатежинский сельсовет, 1;Минский район">
				    <option value="Минская обл., Минский р-н, Луговослободской с/с, М4, 18-й км, ТЛК «Прилесье»">
				    <option value="Минская обл., Червенский р-он д. Кулики">
				  </datalist>						
			</div>
			<div class="form-group">
				<label>Вес, кг:</label>
				<input type="number" name="weight" class="form-control" required="true" value = "${weight}"/>
			</div>
			<div class="form-group">
				<label>Количество паллет:</label>
				<input type="number" name="pall" class="form-control" required="true" value = "${pall}"/>
			</div>
			<div class="form-group">
				<label>Объем, м<sup><small>3</small></sup>:</label>
				<input type="text" name="volume" class="form-control" value = "${volume}"/>
			</div>			
			<div class="form-group">
				<label>Наиминование груза:</label>
				<input name="cargo" class="form-control" required="true" value = "${cargo}"/>
			</div>
			</div>`;
	document.querySelector('#content').appendChild(row);
}
//кнопка добавления точки выгрузки
document.querySelector("#button").addEventListener("mousedown", () => {
	var test = document.getElementById(numPoint);
	adress = test.querySelector('input[name=myCountry]').value +"; "+ test.querySelector('input[name=city]').value +"; "+ test.querySelector('input[name=adress]').value;
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
		autocomplete(document.getElementById(numPoint).querySelector("#myInput"), countries);
	}

});
//кнопка добавления точки загрузки
document.querySelector("#button2").addEventListener("mousedown", () => {
	var test = document.getElementById(numPoint);
	adress = test.querySelector('input[name=myCountry]').value +"; "+ test.querySelector('input[name=city]').value +"; "+ test.querySelector('input[name=adress]').value;
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
		autocomplete(document.getElementById(numPoint).querySelector("#myInput"), countries);
	}

});
//кнопка перехода к созданию маршрута
document.querySelector("#next").addEventListener("mousedown", () => {
	var test = document.getElementById(numPoint);
	adress = test.querySelector('input[name=myCountry]').value +"; "+ test.querySelector('input[name=city]').value +"; "+ test.querySelector('input[name=adress]').value;
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
			url: addPointsUrl,
			headers: { "X-CSRF-TOKEN": token },
			data: JSON.stringify(arr),
			contentType: 'application/json',
			dataType: 'json',
			success: function(html) {
				var first = document.getElementById(1).querySelector('input[name=city]').value;
				var last = document.getElementById(numPoint).querySelector('input[name=city]').value;
				var url = `./addRoute?routeDirection=${first +"-"+last}`;
				window.location.href = url;
			},
			error: function(err) {
				console.log("bad")
				console.log(err.message);
			}
		})
	}
});


function autocomplete(inp, arr) {
  /*функция автозаполнения принимает два аргумента,
    элемент текстового поля и массив возможных значений автозаполнения:*/
  var currentFocus;
  /*выполните функцию, когда кто-то пишет в текстовом поле:*/
  inp.addEventListener("input", function(e) {
      var a, b, i, val = this.value;
      /*закройте все уже открытые списки значений автозаполнения*/
      closeAllLists();
      if (!val) { return false;}
      currentFocus = -1;
      /*создайте элемент DIV, который будет содержать элементы (значения):*/
      a = document.createElement("DIV");
      a.setAttribute("id", this.id + "autocomplete-list");
      a.setAttribute("class", "autocomplete-items");
      /*добавьте элемент DIV в качестве дочернего элемента контейнера автозаполнения:*/
      this.parentNode.appendChild(a);
      /*для каждого элемента массива...*/
      for (i = 0; i < arr.length; i++) {
        /*проверьте, начинается ли элемент с тех же букв, что и значение текстового поля:*/
        if (arr[i].substr(3, val.length).toUpperCase() == val.toUpperCase()) {
          /*создайте элемент DIV для каждого соответствующего элемента:*/
          b = document.createElement("DIV");
          /*сделайте соответствующие буквы жирными:*/
          b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
          b.innerHTML += arr[i].substr(val.length);
          /*вставьте поле ввода, которое будет содержать значение текущего элемента массива:*/
          b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
          /*выполните функцию, когда кто-то нажимает на значение элемента (DIV элемент):*/
          b.addEventListener("click", function(e) {
              /*вставьте значение для текстового поля автозаполнения:*/
              inp.value = this.getElementsByTagName("input")[0].value;
              /*закройте список значений автозаполнения,
              (или любые другие открытые списки значений автозаполнения:*/
              closeAllLists();
          });
          a.appendChild(b);
        }
      }
  });
  /*выполнение функции нажатие клавиши на клавиатуре:*/
  inp.addEventListener("keydown", function(e) {
      var x = document.getElementById(this.id + "autocomplete-list");
      if (x) x = x.getElementsByTagName("div");
      if (e.keyCode == 40) {
        /*Если нажата клавиша со стрелкой вниз,
          увеличьте текущую переменную фокуса:*/
        currentFocus++;
        /*и сделать текущий элемент более заметным:*/
        addActive(x);
      } else if (e.keyCode == 38) { //вверх
        /*Если нажата клавиша со стрелкой вверх,
          уменьшите текущую переменную фокуса:*/
        currentFocus--;
        /*и сделать текущий элемент более заметным:*/
        addActive(x);
      } else if (e.keyCode == 13) {
        /*Если нажата клавиша ENTER, не допускайте отправки формы,*/
        e.preventDefault();
        if (currentFocus > -1) {
          /*и имитировать щелчок по "активному" пункту:*/
          if (x) x[currentFocus].click();
        }
      }
  });
  function addActive(x) {
    /*функция для классификации элемента как " активного":*/
    if (!x) return false;
    /*начните с удаления "активного" класса на всех элементах:*/
    removeActive(x);
    if (currentFocus >= x.length) currentFocus = 0;
    if (currentFocus < 0) currentFocus = (x.length - 1);
    /*добавить класс "autocomplete-active":*/
    x[currentFocus].classList.add("autocomplete-active");
  }
  function removeActive(x) {
    /*функция для удаления класса "active" из всех элементов автозаполнения:*/
    for (var i = 0; i < x.length; i++) {
      x[i].classList.remove("autocomplete-active");
    }
  }
  function closeAllLists(elmnt) {
    /*закройте все списки автозаполнения в документе,
    за исключением того, что было передано в качестве аргумента:*/
    var x = document.getElementsByClassName("autocomplete-items");
    for (var i = 0; i < x.length; i++) {
      if (elmnt != x[i] && elmnt != inp) {
        x[i].parentNode.removeChild(x[i]);
      }
    }
  }
  /*выполните функцию, когда кто-то щелкает в документе:*/
  document.addEventListener("click", function (e) {
      closeAllLists(e.target);
  });
}
/*Массив, содержащий все названия стран в мире:*/
import { countries } from './global.js';
/*инициируйте функцию автозаполнения на элементе "мой вход" и передайте по массиву стран как можно больше значений автозаполнения:*/
autocomplete(document.getElementById("myInput"), countries);



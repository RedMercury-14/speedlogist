export function autocomplete(inp, arr) {
	/*функция автозаполнения принимает два аргумента,
	  элемент текстового поля и массив возможных значений автозаполнения:*/
	var currentFocus;
	/*выполните функцию, когда кто-то пишет в текстовом поле:*/
	inp.addEventListener("input", function (e) {
		var a,
			b,
			i,
			val = this.value;
		/*закройте все уже открытые списки значений автозаполнения*/
		closeAllLists();
		if (!val) {
			return false;
		}
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
				b.addEventListener("click", function (e) {
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
	inp.addEventListener("keydown", function (e) {
		var x = document.getElementById(this.id + "autocomplete-list");
		if (x) x = x.getElementsByTagName("div");
		if (e.keyCode == 40) {
			/*Если нажата клавиша со стрелкой вниз,
			увеличьте текущую переменную фокуса:*/
			currentFocus++;
			/*и сделать текущий элемент более заметным:*/
			addActive(x);
		} else if (e.keyCode == 38) {
			//вверх
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
		if (currentFocus < 0) currentFocus = x.length - 1;
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

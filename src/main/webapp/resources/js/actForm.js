var inputs = document.querySelectorAll('input[type=text]');
var textareas = document.querySelectorAll('textarea');
var target = true;
var ii = '/main/carrier/transportation/routecontrole/getformact';


// Подождём, пока документ будет полностью загружен
//document.addEventListener('DOMContentLoaded', () => {
//    const selectElement = document.getElementById('documentType');
//    const hiddenBlock = document.getElementById('hiddenBlock');
//    const numOfIP = document.getElementById('numOfIP');
//    const dateOfIP = document.getElementById('dateOfIP');
//
//    selectElement.addEventListener('change', (event) => {
//        const selectedValue = event.target.value;
//
//        if (selectedValue === 'свидетельства') {
//            hiddenBlock.style.display = 'inline'; // Показываем блок
//            numOfIP.required = true; // Делаем поле обязательным
//            dateOfIP.required = true; // Делаем поле обязательным
//        } else {
//            hiddenBlock.style.display = 'none'; // Скрываем блок
//            numOfIP.required = false; // Убираем обязательность
//            dateOfIP.required = false; // Убираем обязательность
//        }
//    });
//});

document.addEventListener("DOMContentLoaded", () => {
    const documentTypeSelect = document.getElementById("documentType");
    const hiddenBlock1 = document.getElementById("hiddenBlock1");
    const hiddenBlock2 = document.getElementById("hiddenBlock2");
    const hiddenBlock1Doc = document.getElementById("hiddenBlock1-doc");
    const hiddenBlock2Doc = document.getElementById("hiddenBlock2-doc");

    documentTypeSelect.addEventListener("change", () => {
        const selectedValue = documentTypeSelect.value;

        if (selectedValue === "устава") {
            // Показываем блоки для "Юр. лицо"
            hiddenBlock1.style.display = "inline";
            hiddenBlock1Doc.style.display = "inline";

            // Скрываем блоки для "ИП"
            hiddenBlock2.style.display = "none";
            hiddenBlock2Doc.style.display = "none";
        } else if (selectedValue === "свидетельства") {
            // Показываем блоки для "ИП"
            hiddenBlock2.style.display = "inline";
            hiddenBlock2Doc.style.display = "inline";

            // Скрываем блоки для "Юр. лицо"
            hiddenBlock1.style.display = "none";
            hiddenBlock1Doc.style.display = "none";
        }
    });
});





document.querySelector('#get').addEventListener('mousedown', () => {
	for (let i = 0; i < inputs.length; i++) {
		var input = inputs[i];
		if(input.value == ''){
			target = false;
			break;
		}
	}
		for (let i = 0; i < textareas.length; i++) {
		var textarea = textareas[i];
		if(textarea.value == ''){
			target = false;
			break;
		}
		
	}
	target = true;
})

const cmrInput = document.querySelector('#cmr');
cmrInput.addEventListener('input', (e) => {
	if (e.data === ',' || e.target.value.includes(',')) {
		e.target.value = e.target.value.replace(',', ';')
	}
})

var numContract = document.querySelector('#numContractFromServer').value;
document.querySelector('input[name=numContract]').value = numContract.split(' от ')[0];
document.querySelector('input[name=dateContract]').value = numContract.split(' от ')[1];

var inputs = document.querySelectorAll('input[type=text]');
var textareas = document.querySelectorAll('textarea');
var target = true;
var ii = '/main/carrier/transportation/routecontrole/getformact';
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

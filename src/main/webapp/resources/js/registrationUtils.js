import { checkLoginUrl, checkNumYnpUrl, registrationUserUrl, sendContractUrl, sendFileAgreeUrl } from "./globalConstants/urls.js"

let error = false

let current_fs, next_fs, previous_fs
let opacity

export function isUIError() {
	return error
}
export function clearUIError() {
	error = false
}
export function setUIError() {
	error = true
}

export function next(element) {
	current_fs = $(element).parent()
	next_fs = $(element).parent().next()

	$("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active")

	next_fs.show();

	current_fs.animate({opacity: 0}, {
		step: function(now) {
			opacity = 1 - now

			current_fs.css({
				'display': 'none',
				'position': 'relative'
			});
			next_fs.css({'opacity': opacity})
		}, 
		duration: 600
	})
}
export function prev(element) {
	error = false

	current_fs = $(element).parent()
	previous_fs = $(element).parent().prev()
	
	$("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active")
	
	previous_fs.show()

	current_fs.animate({opacity: 0}, {
		step: function(now) {
			opacity = 1 - now;

			current_fs.css({
				'display': 'none',
				'position': 'relative'
			});
			previous_fs.css({'opacity': opacity})
		}, 
		duration: 600
	});
}

// функция проверки совпадения паролей
export function checkPasswordMatching(pass1, pass2, messageElement) {
	if (pass1 !== pass2) {
		messageElement.innerHTML = "Пароли не совпадают";
	} else {
		messageElement.innerHTML = "";
	}
}

// функция проверки логина пользователя
export function checkLogin(login, token, messageElement) {
	const jsonData = { Login: login }
	$.ajax({
		type: "POST",
		url: checkLoginUrl,
		headers: { "X-CSRF-TOKEN": token },
		data: JSON.stringify(jsonData),
		contentType: 'application/json',
		dataType: 'json',
		success: function(html) {
			messageElement.innerHTML = html.message
			error = true
		},
		error: function(err){
			messageElement.innerHTML = ''
			error = false
		}
	})
}

// функция проверки номера УНП
export function checkNumYNP(numYNP, token, messageElement) {
	const jsonData = { Login: numYNP }
	$.ajax({
		type: "POST",
		url: checkNumYnpUrl,
		headers: { "X-CSRF-TOKEN": token },
		data: JSON.stringify(jsonData),
		contentType: 'application/json',
		dataType: 'json',
		success: function(html) {
			messageElement.innerHTML = html.message
			error = true
		},
		error: function(err){
			messageElement.innerHTML = ''
			error = false
		}
	})
}

export function addPropertiSizeToltip(propertySizeInput, tooltipInput) {
	const propertySize = propertySizeInput.value
	tooltipInput.val(propertySize)
}

// валидация и переход на следующую страницу формы регистрации
export function validateStep(condition, form, targetButton) {
	if (condition) {
		form.removeClass('was-validated')
		next(targetButton)
	} else {
		form.addClass('was-validated')
	}
}

// регистрация пользователя
export function registration(e, isInternational, token) {
	e.preventDefault()

	if (isUIError()) return

	const formData = new FormData(e.target)
	const body = getBodyToSending(formData, isInternational)

	$.ajax({
		type: "POST",
		url: registrationUserUrl,
		headers: { "X-CSRF-TOKEN": token },
		data: JSON.stringify(body),
		contentType: "application/json",
		dataType: "json",
		success: function (html) {
			console.log("success")
			sendFileAgree(formData, token, body.numYNP)
			sendContract(formData, token, body.companyName)
		},
		error: function (err) {
			console.log(err)
			onErrorRegistration()
			alert("Ошибка регистрации, попробуйте снова")
		},
	})
}

//реализация отправки изображения на сервер
function sendFileAgree(formData, token, numYNP) {
	const file = formData.get("agreePersonalData")

	$.ajax({
		type: "POST",
		url: sendFileAgreeUrl,
		headers: {
			"x-file-name": encodeURI(file.name),
			"x-file-size": file.size,
			"X-CSRF-TOKEN": token,
			ynp: numYNP,
		},
		cache: false,
		contentType: false,
		processData: false,
		enctype: "multipart/form-data",
		data: formData,
		success: function (msg) {
			console.log(msg)
		},
		error: function (err) {
			console.log(err)
			alert("Ошибка отправки фото согласия, обратитесь к администратору")
		},
	})
}

//реализация отправки договора на сервер
function sendContract(formData, token, companyName) {
	const file = formData.get("contract")

	$.ajax({
		type: "POST",
		url: sendContractUrl,
		headers: {
			"x-file-name": encodeURI(file.name),
			"x-file-size": file.size,
			"X-CSRF-TOKEN": token,
			companyName: encodeURI(companyName),
		},
		cache: false,
		contentType: false,
		processData: false,
		enctype: "multipart/form-data",
		data: formData,
		success: function (msg) {
			console.log(msg)
			onSuccessRegistration()
		},
		error: function (err) {
			console.log(err)
			onErrorRegistration()
			alert("Ошибка отправки договора, обратитесь к администратору")
		},
	});
}

// заполнение финальной страницы формы при успешной регистрации
function onSuccessRegistration() {
	$('#finishTitle').removeClass('none')
	$('#finishInfo').removeClass('none')
	$('#spinner').addClass('none')
	$('#successImage').removeClass('none')
	redirectToSigninPage()
}
// заполнение финальной страницы формы при ошибке регистрации
function onErrorRegistration() {
	$('#errorTitle').removeClass('none')
	('#errorInfo').removeClass('none')
	$('#spinner').addClass('none')
	$('#errorImage').removeClass('none')
}

//редирект на страницу входа
function redirectToSigninPage() {
	setTimeout(() => {
		window.location ='../main/signin'
	}, 3000)
}

// получение данных для отправки на сервер
function getBodyToSending(formData, isInternational) {
	const data = Object.fromEntries(formData)

	const nameData = data.name.split(' ')
	const surname = nameData[0] ? nameData[0] : null
	const name =  nameData[1] ? nameData[1] : null
	const patronymic =  nameData[2] ? nameData[2] : null
	const registrationCertificate = getRegistrationCertificate(data)
	const affiliatedCompanies = data.affiliatedCompanies ? data.affiliatedCompanies : null

	if (isInternational) {
		const TIR = data.check_tir ? true : false
		const directionOfTransportation = getDirectionOfTransportation(data)
		const characteristicsOfTruks = getCharacteristicsOfTrucks(data)

		return {
			check: data.check,
			login: data.login.trim(),
			password: data.password,
			name,
			surname,
			patronymic,
			tel: data.tel,
			mail: data.mail,
			propertySize: data.propertySize,
			companyName: data.propertySize + ' ' + data.companyName,
			countryOfRegistration: data.countryOfRegistration,
			director: data.director,
			numYNP: Number(data.numYNP),
			affiliatedCompanies,
			requisites: data.requisites,
			registrationCertificate,
			TIR,
			directionOfTransportation,
			numberOfTruks: Number(data.numberOfTruks),
			characteristicsOfTruks
		}
	} else {
		return {
			check: data.check,
			login: data.login.trim(),
			password: data.password,
			name,
			surname,
			patronymic,
			tel: data.tel,
			mail: data.mail,
			propertySize: data.propertySize,
			companyName: data.propertySize + ' ' + data.companyName,
			countryOfRegistration: data.countryOfRegistration,
			director: data.director,
			numYNP: Number(data.numYNP),
			affiliatedCompanies,
			requisites: data.requisites,
			registrationCertificate,
		}
	}


}

// получение данных свидетельства о регистрации
function getRegistrationCertificate(data) {
	return data.registrationCertificate_ser
		? 'Серия: '
			+ data.registrationCertificate_ser
			+ ', Номер: '
			+ data.registrationCertificate_num
			+ ' от '
			+ data.registrationCertificate_date
		: 'Номер: '
			+ data.registrationCertificate_num
			+ ' от '
			+ data.registrationCertificate_date
}
// получение данных характеристик подвижного состава
function getCharacteristicsOfTrucks(data) {
	let characteristicsOfTruks = ''
	const characteristicsOfTruksData = [
		data.check_tent ? data.check_tent : '',
		data.check_ref ? data.check_ref : '',
		data.check_term ? data.check_term : '',
		data.check_20ft ? data.check_20ft : '',
		data.check_40ft ? data.check_40ft : '',
		data.check_45ft ? data.check_45ft : '',
	]
	characteristicsOfTruksData.forEach(str => {
		if (str) {
			characteristicsOfTruks
				= characteristicsOfTruks + `${str}; `
		}
	})
	return characteristicsOfTruks
}
// получение данных направлений перевозок
function getDirectionOfTransportation(data) {
	let directionOfTransportation = ''
	const directionOfTransportationData = [
		data.check_ru ? data.check_ru : '',
		data.check_by ? data.check_by : '',
		data.check_eu ? data.check_eu : '',
		data.check_sng ? data.check_sng : '',
		data.check_ge ? data.check_ge : '',
		data.check_tr ? data.check_tr : '',
		data.check_kz ? data.check_kz : '',
		data.directionOfTransportation_other,
	]
	directionOfTransportationData.forEach(str => {
		if (str) {
			directionOfTransportation
				= directionOfTransportation + `${str}; `
		}
	})
	return directionOfTransportation
}

export function prohibitionOfSpecialCharactersInput(e, messageLoginElem) {
	const input = e.target
	const reg = /[^a-zA-Z0-9@!%.]/g
	if (input.value.match(reg)) {
		input.value = input.value.replaceAll(reg, '')
		messageLoginElem.innerText = 'Киррилица и специмволы, кроме @, !, %, ., запрещены для ввода'
	} else {
		messageLoginElem.innerText = ''
	}
}
import {
	addPropertiSizeToltip,
	checkLogin,
	checkNumYNP,
	checkPasswordMatching,
	isUIError,
	next,
	prev,
	registration,
	validateStep,
} from "./registrationUtils.js"

const token = $("meta[name='_csrf']").attr("content")
const mailRegex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-]))/

$(document).ready(function() {
	// проверка совпадения паролей
	const inp_password = document.querySelector("#password")
	const messagePassElem = document.querySelector("#message")
	$("#confirmPassword").change((e) => checkPasswordMatching(inp_password.value, e.target.value, messagePassElem))

	// проверка логина и номера УНП
	const messageLoginElem = document.querySelector("#messageLogin")
	const messageYnpElem = document.querySelector("#messageYNP")
	$("#login").change((e) => checkLogin(e.target.value, token, messageLoginElem))
	$("#numYNP").change((e) => checkNumYNP(e.target.value, token, messageYnpElem))

	// форма и подзаголовок формы
	const form = $('#regform')
	const formInfo = $('#form-info')

	// инпуты шага 1
	const loginInput = $('#login')
	const passwordInput = $('#password')
	const nameInput = $('#name')
	const telInput = $('#tel')
	const mailInput = $('#mail')
	const filedataInput = document.getElementsByName("agreePersonalData")

	// инпуты шага 2
	const contractInput = $('#contract')
	const companyNameInput = $('#companyName')
	const propertySizeInput = $('#propertySize')
	const propertySizeTooltip = $('#propertySizeTooltip')
	const countryOfRegistrationInput = $('#countryOfRegistration')
	const directorInput = $('#director')
	const numYNPInput = $('#numYNP')
	const requisitesInput = $('#requisites')
	const registrationCertificate_serInput = $('#registrationCertificate_ser')
	const registrationCertificate_numInput = $('#registrationCertificate_num')
	const registrationCertificate_dateInput = $('#registrationCertificate_date')
	const registrationCertificate_fileInput = $('#registrationCertificate_file')

	// добавление формы собственности в поле перед названием компании
	propertySizeInput.on('change', (e) => addPropertiSizeToltip(e.target, propertySizeTooltip))

	// подсказка для инпута наименования компании
	companyNameInput.on('focusin', (e) => $('#companyNamePopover').addClass('show'))
	companyNameInput.on('focusout', (e) => $('#companyNamePopover').removeClass('show'))

	// валидация шага 1
	const isValidStep1Data = () =>
		!isUIError() &&
		loginInput.val() &&
		passwordInput.val() &&
		nameInput.val() &&
		telInput.val() &&
		mailRegex.test(mailInput.val()) &&
		filedataInput[0].files[0]

	// валидация шага 2
	const isValidStep2Data = () =>
		!isUIError() &&
		contractInput[0].files[0] &&
		companyNameInput.val() &&
		propertySizeInput.val() &&
		countryOfRegistrationInput.val() &&
		directorInput.val() &&
		numYNPInput.val() &&
		requisitesInput.val() &&
		registrationCertificate_numInput.val() &&
		registrationCertificate_dateInput.val()
	// && registrationCertificate_fileInput.val()

	// добавление листнеров на кнопки вперед и назад
	$(".previous").click((e) => prev(e.target))
	$("#step1Btn").click((e) => validateStep(isValidStep1Data(), form, e.target))
	$("#step2Btn").click((e) => validateFinalStep(e))

	// регистрация и отправка формы регистрации на сервер
	form.on("submit", (e) => registration(e, false, token))

	// валидация 2 шага и переход на финальную страницу регистрации
	function validateFinalStep(e) {
		if (isValidStep2Data()) {
			form.removeClass('was-validated')
			next(e.target)
			formInfo.text(' ')
		} else {
			form.addClass('was-validated')
		}
	}
})

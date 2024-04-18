import { snackbar } from "./snackbar/snackbar.js"

export const errorStatusText = {
	400:', недопустимый запрос',
	401:', отказано в доступе',
	403:', запрещено',
	404:', проверьте инернет соединение или адрес',
	405:', метод запрещен',
	406:', недопустимый тип MIME',
	412:', необходимое условие не выполнено',
	414:', адрес слишком длинный',
	500: ', проверьте данные',
	'': '',
}

export const ajaxUtils = {
	get({ url, successCallback, errorCallback }) {
		$.ajax({
			type: "GET",
			url : url,
			success : (res) => {
				successCallback && successCallback(res)
			},
			error: (err) => {
				console.log(err)
				const errorStatus = err.status ? err.status : ''
				snackbar.show(`Ошибка${errorStatusText[errorStatus]}!`)
				errorCallback && errorCallback()
			}
		})
	},

	postJSONdata({ url, token, data, successCallback, errorCallback }) {
		$.ajax({
			type: "POST",
			url: url,
			headers: { "X-CSRF-TOKEN": token },
			data: JSON.stringify(data),
			contentType: 'application/json',
			dataType: 'json',
			success: function(res) {
				successCallback && successCallback(res)
			},
			error: function(err) {
				console.log(err)
				const errorStatus = err.status ? err.status : ''
				snackbar.show(`Ошибка${errorStatusText[errorStatus]}!`)
				errorCallback && errorCallback()
			}
		})
	},

	postMultipartFformData({ url, token, data, successCallback, errorCallback }) {
		$.ajax({
			type: "POST",
			url: url,
			headers: { "X-CSRF-TOKEN": token },
			data: data,
			cache: false,
			contentType: false,
			processData: false,
			enctype: 'multipart/form-data',
			success: function(res) {
				successCallback && successCallback(res)
			},
			error: function(err){
				console.log(err)
				const errorStatus = err.status ? err.status : ''
				snackbar.show(`Ошибка${errorStatusText[errorStatus]}!`)
				errorCallback && errorCallback()
			}
		})
	},

	postFileUponRegistration({ url, token, file, checkHeader, data, successCallback, errorCallback }) {
		$.ajax({
			type: "POST",
			url: url,
			headers: {
				"x-file-name": encodeURI(file.name),
				"x-file-size":  file.size,
				"X-CSRF-TOKEN": token,
				...checkHeader
			},
			cache: false,
			contentType: false,
			processData: false,
			enctype: 'multipart/form-data',
			data: data,
			success: function(res){
				successCallback && successCallback(res)
			},
			error: function(err){
				console.log(err)
				const errorStatus = err.status ? err.status : ''
				snackbar.show(`Ошибка${errorStatusText[errorStatus]}!`)
				errorCallback && errorCallback()
			}
		})
	},
}
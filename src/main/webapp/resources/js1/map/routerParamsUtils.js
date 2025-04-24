// -------------------------------------------------------------------------------//
// ------------- функции для управления параметрами маршрутизатора ---------------//
// -------------------------------------------------------------------------------//

import { ajaxUtils } from "../ajaxUtils.js"
import { snackbar } from "../snackbar/snackbar.js"
import { getData } from "../utils.js"

// сохранение параметров на сервер
export function saveRouterParams(paramsForm, url, token) {
	const parameters = getRouterParams(paramsForm)
	console.log("routerParams:", parameters)

	ajaxUtils.postJSONdata({
		url: url,
		token: token,
		data: parameters,
		successCallback: (res) => {
			console.log('setRouterParams', res)
			snackbar.show('Настройки маршрутизатора сохранены')
		}
	})
}

// загрузка параметров с сервера
export function loadRouterParams(paramsForm, url) {
	try {
		setRouterParams(paramsForm, url)
		snackbar.show('Настройки маршрутизатора загружены')
	} catch (error) {
		console.error(error)
		const errorStatus = err.status ? err.status : ''
		snackbar.show(`Ошибка ${errorStatus}!`)
	}
}

// получение параметров из формы
export function getRouterParams(paramsForm) {
	return {
		roadClassPRIMARY: paramsForm.roadClassPRIMARY.value ? paramsForm.roadClassPRIMARY.value : null,
		roadClassSECONDARY: paramsForm.roadClassSECONDARY.value ? paramsForm.roadClassSECONDARY.value : null,
		roadClassTERTIARY: paramsForm.roadClassTERTIARY.value ? paramsForm.roadClassTERTIARY.value : null,
		roadClassRESIDENTIAL: paramsForm.roadClassRESIDENTIAL.value ? paramsForm.roadClassRESIDENTIAL.value : null,
		roadClassUNCLASSIFIED: paramsForm.roadClassUNCLASSIFIED.value ? paramsForm.roadClassUNCLASSIFIED.value : null,
		roadEnvironmentFERRY: paramsForm.roadEnvironmentFERRY.value ? paramsForm.roadEnvironmentFERRY.value : null,
		maxAxleLoad: paramsForm.maxAxleLoad.value ? paramsForm.maxAxleLoad.value : null,
		maxAxleLoadCoeff: paramsForm.maxAxleLoadCoeff.value ? paramsForm.maxAxleLoadCoeff.value : null,
		surfaceMISSING: paramsForm.surfaceMISSING.value ? paramsForm.surfaceMISSING.value : null,
		surfaceGRAVEL: paramsForm.surfaceGRAVEL.value ? paramsForm.surfaceGRAVEL.value : null,
		surfaceCOMPACTED: paramsForm.surfaceCOMPACTED.value ? paramsForm.surfaceCOMPACTED.value : null,
		surfaceASPHALT: paramsForm.surfaceASPHALT.value ? paramsForm.surfaceASPHALT.value : null,
		roadClassMOTORWAYTOLL: paramsForm.roadClassMOTORWAYTOLL.value ? paramsForm.roadClassMOTORWAYTOLL.value : null,
		distanceInfluence: paramsForm.distanceInfluence.value ? paramsForm.distanceInfluence.value : null,
	}
}

// усановка параметров в форму после загрузки с сервера
export async function setRouterParams(paramsForm, url) {
	const routerParams = await getData(url)

	paramsForm.roadClassPRIMARY.value = routerParams.roadClassPRIMARY ? routerParams.roadClassPRIMARY : ''
	paramsForm.roadClassSECONDARY.value = routerParams.roadClassSECONDARY ? routerParams.roadClassSECONDARY : ''
	paramsForm.roadClassTERTIARY.value = routerParams.roadClassTERTIARY ? routerParams.roadClassTERTIARY : ''
	paramsForm.roadClassRESIDENTIAL.value = routerParams.roadClassRESIDENTIAL ? routerParams.roadClassRESIDENTIAL : ''
	paramsForm.roadClassUNCLASSIFIED.value = routerParams.roadClassUNCLASSIFIED ? routerParams.roadClassUNCLASSIFIED : ''
	paramsForm.roadEnvironmentFERRY.value = routerParams.roadEnvironmentFERRY ? routerParams.roadEnvironmentFERRY : ''
	paramsForm.maxAxleLoad.value = routerParams.maxAxleLoad ? routerParams.maxAxleLoad : ''
	paramsForm.maxAxleLoadCoeff.value = routerParams.maxAxleLoadCoeff ? routerParams.maxAxleLoadCoeff : ''
	paramsForm.surfaceASPHALT.value = routerParams.surfaceASPHALT ? routerParams.surfaceASPHALT : ''
	paramsForm.surfaceCOMPACTED.value = routerParams.surfaceCOMPACTED ? routerParams.surfaceCOMPACTED : ''
	paramsForm.surfaceGRAVEL.value = routerParams.surfaceGRAVEL ? routerParams.surfaceGRAVEL : ''
	paramsForm.surfaceMISSING.value = routerParams.surfaceMISSING ? routerParams.surfaceMISSING : ''
	paramsForm.roadClassMOTORWAYTOLL.value = routerParams.roadClassMOTORWAYTOLL ? routerParams.roadClassMOTORWAYTOLL : ''
	paramsForm.distanceInfluence.value = routerParams.distanceInfluence ? routerParams.distanceInfluence : ''
}
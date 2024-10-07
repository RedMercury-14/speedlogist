import { store } from "./store.js"

export const fakeServer = (request) => {
    // в 30% случаев выдавать ошибку
    if (Math.random() < 0.3) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                resolve({ status: '100', data: request })
            }, 500)
        })
    }

    return new Promise((resolve, reject) => {
        setTimeout(() => {
            resolve({ status: '200', data: request })
        }, 500)
    })

}

// сохранение данных в локал сторадж
export function saveToLocalStorage(trucks) {
	localStorage.setItem('trucksData', JSON.stringify(trucks))
}
export function getFromLocalStorage() {
	const trucksData = localStorage.getItem('trucksData')
	if (!trucksData) return null
	return JSON.parse(trucksData)
}

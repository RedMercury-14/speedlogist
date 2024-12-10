contextMenu();
onNumberMessage();
var route;
var routeDirection;
let startPrice
var routeStatus;
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js';
import { ws } from './global.js';
import { wsHead } from './global.js';
import { EUR } from './global.js';
import { USD } from './global.js';
import { RUB } from './global.js';
import { KZT } from './global.js';
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
var role = document.querySelector('input[id=role]').value;

const exportButton = document.querySelector('#export')
exportButton.addEventListener('click', () => downloadExcelReport())

// скачивание отчёта excel
async function downloadExcelReport() {
	bootstrap5overlay.showOverlay()
	const dateStart = document.querySelector("input[name=dateStart]").value
	const dateFinish = document.querySelector("input[name=dateFinish]").value
	const url = `../../api/manager/getReport/${dateStart}&${dateFinish}`

	const res = await fetch(url)
	const data = await res.json()
	
	if (data.status === 200) {
		const path = data.body
		const pathInArray = path.includes('\\') ? path.split('\\') : path.split('/')
		const index = pathInArray.indexOf('speedlogist')
		const slisedPathInArray = pathInArray.slice(index)
		const linkHref = '/' + slisedPathInArray.join('/')
		const fileName = pathInArray[pathInArray.length - 1]

		const link = document.createElement('a');
		document.body.appendChild(link)
		link.download = fileName
		link.href = linkHref
		link.click()
		document.body.removeChild(link)
	}

	bootstrap5overlay.hideOverlay()
}


if (localStorage.getItem('lastClickRoute') != null) {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		if (routeItemI.querySelector('#idRoute').innerHTML == localStorage.getItem('lastClickRoute')) {
			routeItemI.style.border = "2px solid black";
		}
	}
	localStorage.setItem('lastClickRoute', null);
}



//подсветка строк
function onNumberMessage() {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		let coll = routeItemI.querySelector('.coll');
		const status = routeItemI.querySelector('#status').innerHTML
			.replace(/\s/g, '')
			.split('.')[0]

		if (status == 'Тендерзавершен') {
			routeItemI.classList.add("finishRow");
		} else if (status === 'Ожиданиеподтверждения') {
			routeItemI.classList.add("attentionRow");
		} else if (status === 'Тендеротменен') {
			routeItemI.classList.add("cancelRow");
		} else if (status === 'Маршрутзавершен') {
			routeItemI.classList.add("endRow");
		} else if (status === 'Контрольцены') {
			routeItemI.classList.add("oncePersonRoute");
		}
		if (status === 'Маршрутнабирже') {
			getNumMessege(routeItemI.querySelector('#idRoute').innerHTML, coll, routeItemI)
		}
	}
}

function onMessage(msg) {
	if (msg.idRoute != null) {
		var routeItem = document.querySelectorAll('tr');
		for (let i = 1; i < routeItem.length; i++) {
			var routeItemI = routeItem[i];
			if (routeItemI.querySelector("#idRoute").innerHTML == msg.idRoute) {
				let targetRouteItemI = routeItemI;
				let coll = routeItemI.querySelector('.coll');
				setTimeout(() => getNumMessege(msg.idRoute, coll, targetRouteItemI), 500);
				//routeItemI.classList.add("activRow");
			}
		}
	}
};

function getNumMessege(idRoute, coll, routeItemI) {
	fetch(`../../api/info/message/numroute/${idRoute}`).then(function(response) {
		response.text().then(function(text) {
			if(coll) coll.innerText = "(" + text + ")";
			if (text >= 1) {
				routeItemI.classList.remove("noneRow");
				routeItemI.classList.add("activRow");
			} else if (text == '0') {
				routeItemI.classList.add("noneRow");
			}
		});
	});
}

function sendMessage(message) {
	wsHead.send(JSON.stringify(message));
}

function contextMenu() {
	(function() {
		var routeItem = document.querySelectorAll('tr');
		for (let i = 0; i < routeItem.length; i++) {
			var routeItemI = routeItem[i];
			contextMenuListner(routeItemI);
			showOfferByCarrier(routeItemI)
		}
		function showOfferByCarrier(routeItemI) {
			if (routeItemI.querySelector('#showOfferByCArrier') != null) {
				routeItemI.querySelector('#showOfferByCArrier').addEventListener('click', () => {
					localStorage.setItem('lastClickRoute', routeItemI.querySelector('#idRoute').innerHTML);
				})
			}

		}
		let mouseX;
		let mouseY;
		const menu = document.querySelector(".right-click-menu");
		function contextMenuListner(el) {
			el.addEventListener("contextmenu", event => {
				console.log("!CONTEXT!")
				var routeItem = document.querySelectorAll('tr');
				for (let i = 1; i < routeItem.length; i++) {
					var routeItemI = routeItem[i];
					routeItemI.style.border = "1px";
				}
				event.preventDefault();
				menu.classList.add("active");
				mouseX = event.clientX || event.touches[0].clientX;
				mouseY = event.clientY || event.touches[0].clientY;
				let menuHeight = menu.getBoundingClientRect().height;
				let menuWidth = menu.getBoundingClientRect().width;
				let width = window.innerWidth;
				let height = window.innerHeight;
				//ближний правый угол
				if (width - mouseX <= 200) {
					menu.style.left = width - menuWidth + "px";
					menu.style.top = mouseY + "px";
					//правый низ
					if (height - mouseY <= 200) {
						menu.style.top = mouseY - menuHeight + "px";
					}
				}
				// левая сторона
				else {
					menu.style.left = mouseX + "px";
					menu.style.top = mouseY + "px";
					//левый низ
					if (height - mouseY <= 200) {
						menu.style.top = mouseY - menuHeight + "px";
					}
				}

				route = el.querySelector('#idRoute').innerHTML;
				routeDirection = el.querySelector('#routeDirection').innerHTML;
				routeStatus = el.querySelector('#status').innerHTML.replace(/\s/g, '');
				startPrice = el.querySelector('#cost').innerHTML;
				if (routeStatus == 'Ожиданиеподтверждения.') {
					menu.querySelector('#l1').style.color = "red";
					menu.querySelector('#l5').style.color = "red";
					menu.querySelector('#l2').classList.remove('none');
					menu.querySelector('#l4').classList.remove('none');
					menu.querySelector('#l5').classList.remove('none');
					// menu.querySelector('#l6').classList.remove('none');
					menu.querySelector('#l7').classList.remove('none');
				} else if (routeStatus == 'Тендеротменен.') {
					menu.querySelector('#l1').style.color = "black";
					menu.querySelector('#l5').style.color = "black";
					menu.querySelector('#l2').classList.add('none');
					menu.querySelector('#l4').classList.add('none');
					menu.querySelector('#l5').classList.add('none');
					// menu.querySelector('#l6').classList.add('none');
					menu.querySelector('#l7').classList.add('none');
				} else {
					menu.querySelector('#l2').classList.remove('none');
					menu.querySelector('#l4').classList.remove('none');
					menu.querySelector('#l5').classList.remove('none');
					// menu.querySelector('#l6').classList.remove('none');
					menu.querySelector('#l7').classList.remove('none');
					menu.querySelector('#l1').style.color = "black";
					menu.querySelector('#l5').style.color = "black";
				}
				el.style.border = "2px solid black";
				localStorage.setItem('lastClickRoute', route);
			}, false);
		}
		document.addEventListener("click", event => {
			if (event.button !== 2) {
				menu.classList.remove("active");				
			}
			var routeItem = document.querySelectorAll('tr');
				for (let i = 1; i < routeItem.length; i++) {
					var routeItemI = routeItem[i];
					routeItemI.style.border = "1px";
				}

		}, false);

		menu.addEventListener("click", event => {
			event.stopPropagation();
		}, false);

		document.addEventListener("scroll", event => {
			if (event.button !== 2) {
				menu.classList.remove("active");
			}
		}, false);

		menu.addEventListener("scroll", event => {
			event.stopPropagation();
		}, false);


		document.querySelector("#l1").addEventListener("click", () => {
			var url = `./international/tenderOffer?idRoute=${route}`;
			localStorage.setItem("mouseX", mouseX);
			localStorage.setItem("mouseY", mouseY);
			window.location.href = url;
		}, false);
		document.querySelector("#l2").addEventListener("click", () => {
			var url = `../../api/logistics/routeUpdate/${route}&1`
			fetch(url)
				.then(res => {
					if (!res.ok) {
						alert('Ошибка при отправке тендера')
						return
					}
					window.location.reload()
				})
			sendMessage({
				fromUser: "logist",
				toUser: "international",
				text: 'Маршрут ' + routeDirection + ' доступен для торгов.',
				url: `/speedlogist/main/carrier/tender/tenderpage?routeId=${route}`,
				idRoute: route,
				status: "1"
			})
		}, false);
		document.querySelector("#l3").addEventListener("click", () => {
			var url = `../logistics/international/routeShow?idRoute=${route}`;
			localStorage.setItem("mouseX", mouseX);
			localStorage.setItem("mouseY", mouseY);
			window.location.href = url;
		}, false);
		document.querySelector("#l4").addEventListener("click", () => {
			var url = `/speedlogist/main/logistics/international/editRoute?idRoute=${route}`;
				localStorage.setItem("mouseX", mouseX);
				localStorage.setItem("mouseY", mouseY);
				window.location.href = url;
//			if (startPrice != '' && role == '[ROLE_MANAGER]') {
//				alert("В доступе отказано");
//
//			} else {
//				var url = `/speedlogist/main/logistics/international/editRoute?idRoute=${route}`;
//				localStorage.setItem("mouseX", mouseX);
//				localStorage.setItem("mouseY", mouseY);
//				window.location.href = url;
//			}

		}, false);
		document.querySelector("#l5").addEventListener("click", () => {
			fetch(`/speedlogist/api/memory/message/routes/${route}`).then(function(response) {
				response.json().then(function(text) {
					var flag = false;
					text.forEach(function(element) {
						if (element.text == 'На_выгрузке') {
							flag = true;
						}
					})
					if (flag) {
						var url = `/speedlogist/main/logistics/international/routeEnd?idRoute=${route}`;
						window.location.href = url;
					} else {
						alert('Маршрут не может быть завершен, т.к. авто не прибыло на место разгрузки.')
					}
				});
			});
		}, false);
		// document.querySelector("#l6").addEventListener("click", () => {
		// 	var url = `/speedlogist/main/logistics/international/addRoute?idRoute=${route}`;
		// 	localStorage.setItem("mouseX", mouseX);
		// 	localStorage.setItem("mouseY", mouseY);
		// 	window.location.href = url;
		// }, false);
		document.querySelector("#l7").addEventListener("click", () => {
			var url = `../../api/logistics/routeUpdate/${route}&5`
			fetch(url)
				.then(res => {
					if (!res.ok) {
						alert('Ошибка при отмене маршрута')
						return
					}
					window.location.reload()
				})
		}, false);
	})();
}


//модальное окно водителя
var modal = document.querySelectorAll('#myModal');
var btn = document.querySelectorAll("#myBtn");
for (let i = 0; i < btn.length; i++) {
	var modalI = modal[i];
	var btnI = btn[i];
	var span = modalI.querySelector(".closer");
	modalTarget(modalI, btnI, span)
}
function modalTarget(modalI, btnI, span) {
	btnI.addEventListener("click", () => {
		modalI.style.display = "block";
	})
	span.onclick = function() {
		modalI.style.display = "none";
	}
	window.onclick = function(event) {
		if (event.target == modalI) {
			modalI.style.display = "none";
		}
	}
}

//модальное окно главное
var modal2 = document.querySelectorAll('#mainModal');
var btn2 = document.querySelectorAll("#myBtn2");
for (let i = 0; i < btn2.length; i++) {
	var modalI = modal2[i];
	var btnI = btn2[i];
	var span = modalI.querySelector(".closer");
	modalTarget2(modalI, btnI, span)
}
function modalTarget2(modalI, btnI, span) {
	btnI.addEventListener("click", () => {
		modalI.style.display = "block";
	})
	span.onclick = function() {
		modalI.style.display = "none";
	}
	window.onclick = function(event) {
		if (event.target == modalI) {
			modalI.style.display = "none";
		}
	}
}

//управление фильтрами

document.querySelector('#statusSort').addEventListener('change', (event) => {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		routeItemI.classList.remove('status-none');
	}
	localStorage.setItem('statusSort', event.target.value);
	
	switch (event.target.value) {
		case '1':
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') != 'Ожиданиеподтверждения.') {
					routeItemI.classList.add("status-none");
				}
			}
			break

		case '2':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '').split('.')[0] != 'Маршрутнабирже') {
					routeItemI.classList.add("status-none");
				}
			}
			break
		case '3':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') != 'Тендерзавершен.Перевозчикпринят.') {
					routeItemI.classList.add("status-none");
				}
			}
			break
		case '4':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') != 'Маршрутзавершен.') {
					routeItemI.classList.add("status-none");
				}
			}
			break
		case '5':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '').split('.')[0] != 'Контрольцены') {
					routeItemI.classList.add("status-none");
				}
			}
			break

		default:
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				routeItemI.classList.remove('status-none');
			}
			break
	}
});

document.querySelector('#statusWay').addEventListener('change', (event) => {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		routeItemI.classList.remove('status-none');
	}
	localStorage.setItem('statusWay', event.target.value);
	
	switch (event.target.value) {
		case '1':
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#way').innerHTML.replace(/\s/g, '') != 'Имп') {
					routeItemI.classList.add("status-none");
				}
			}
			break

		case '2':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#way').innerHTML.replace(/\s/g, '').split('.')[0] != 'Экс') {
					routeItemI.classList.add("status-none");
				}
			}
			break

		case '3':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#way').innerHTML != 'РБ') {
					routeItemI.classList.add("status-none");
				}
			}
			break
			
		default:
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				routeItemI.classList.remove('status-none');
			}
			break
	}
});


if (localStorage.getItem('statusSort') != null) {
	document.querySelector('#statusSort').value = localStorage.getItem('statusSort');
}
setTimeout(() => statusSort(localStorage.getItem('statusSort'), 100));


function statusSort(target) {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		routeItemI.classList.remove('status-none');
	}
	switch (target) {
		case '1':
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') != 'Ожиданиеподтверждения.') {
					routeItemI.classList.add("status-none");
				}
			}
			break

		case '2':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '').split('.')[0] != 'Маршрутнабирже') {
					routeItemI.classList.add("status-none");
				}
			}
			break
		case '3':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') != 'Тендерзавершен.Перевозчикпринят.') {
					routeItemI.classList.add("status-none");
				}
			}
			break
		case '4':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') != 'Маршрутзавершен.') {
					routeItemI.classList.add("status-none");
				}
			}
			break
		case '5':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '').split('.')[0] != 'Контрольцены') {
					routeItemI.classList.add("status-none");
				}
			}
			break

		default:
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				routeItemI.classList.remove('status-none');
			}
			break
	}
}
delta();
//расчёт экономии
function delta() {
	localStorage.setItem('economyStorageTotlClosetBYN', 0);
	var economyTotl = 0;
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		var econ = routeItemI.querySelector('#economy');
		var finishCost = routeItemI.querySelector('#finishCost');
		var cost = routeItemI.querySelector('#cost');
		if (cost.innerHTML.split(' ')[0] != '' && finishCost.innerHTML.split(' ')[0] != '') {
			if (routeItemI.querySelector('#finishCost').innerHTML.split(' ')[1] == 'BYN') {
				econ.innerHTML = cost.innerHTML.split(' ')[0] - finishCost.innerHTML.split(' ')[0] + ' BYN';
			}
			var num = cost.innerHTML.split(' ')[0] - finishCost.innerHTML.split(' ')[0];
			if (parseInt(num) < 0) {
				econ.style.color = "red"
			}
			if (routeItemI.querySelector('#cost').innerHTML.replace(/\s/g, '').split('-')[1] == undefined) {
				economyTotl = economyTotl + num;
			} else if (routeItemI.querySelector('#finishCost').innerHTML.split(' ')[1] == 'BYN' && routeItemI.querySelector('#cost').innerHTML.replace(/\s/g, '').split('-')[1] != undefined) {
				localStorage.setItem('economyStorageTotlClosetBYN', parseInt(localStorage.getItem('economyStorageTotlClosetBYN')) + num);
			} else if (routeItemI.querySelector('#finishCost').innerHTML.split(' ')[1] == 'USD') {
				var routeItemTarget = routeItemI;
				parceCostTD(USD, routeItemTarget);
			} else if (routeItemI.querySelector('#finishCost').innerHTML.split(' ')[1] == 'EUR') {
				var routeItemTarget = routeItemI;
				parceCostTD(EUR, routeItemTarget);
			} else if (routeItemI.querySelector('#finishCost').innerHTML.split(' ')[1] == 'RUB') {
				var routeItemTarget = routeItemI;
				parceCostTD(RUB, routeItemTarget);
			} else if (routeItemI.querySelector('#finishCost').innerHTML.split(' ')[1] == 'KZT') {
				var routeItemTarget = routeItemI;
				parceCostTD(KZT, routeItemTarget);
			}

		} else {
			econ.innerHTML = " - "
		}

	}
	document.querySelector('#getEconomy').addEventListener('mousedown', () => {
		document.querySelector('#totalEconomy').innerHTML = "Экономия за выбранный период (открытые тендеры): " + economyTotl + " BYN";
		document.querySelector('#economyTotlClosetBYN').innerHTML = "Экономия за выбранный период (закрытые тендеры) BYN: " + localStorage.getItem('economyStorageTotlClosetBYN') + " BYN";
		var resultEconomy = parseInt(localStorage.getItem('economyStorageTotlClosetBYN')) + economyTotl;
		document.querySelector('#economyResultBYN').innerHTML = "Общая экономия BYN: " + resultEconomy + " BYN";
	})


	function parceCostTD(cur, routeItemTarget) {
		fetch(`https://www.nbrb.by/api/exrates/rates/${cur}`).then((response) => {
			response.json().then((text) => {
				var econ = routeItemTarget.querySelector('#economy');
				var finishCost = routeItemTarget.querySelector('#finishCost');
				var cost = routeItemTarget.querySelector('#cost');
				var parceCost = Math.round(text.Cur_OfficialRate * finishCost.innerHTML.split(' ')[0] / text.Cur_Scale);
				var parceNum = cost.innerHTML.split(' ')[0] - parceCost;
				econ.innerHTML = parceNum + ' BYN'
				if (parseInt(parceNum) < 0) {
					econ.style.color = "red"
				}
				localStorage.setItem('economyStorageTotlClosetBYN', parseInt(localStorage.getItem('economyStorageTotlClosetBYN')) + parceNum);
			});
		});
	}
}
// фильтр статуса тендера
document.querySelector('#typeTenderSort').addEventListener('change', (event) => {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		routeItemI.classList.remove('type-tender-none');
	}
	localStorage.setItem('typeTenderSort', event.target.value);
	switch (event.target.value) {
		case '1':
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#cost').innerHTML.replace(/\s/g, '').split('-')[1] == undefined) {
					routeItemI.classList.add("type-tender-none");
				}
			}
			break

		case '2':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#cost').innerHTML.replace(/\s/g, '').split('-')[1] != undefined) {
					routeItemI.classList.add("type-tender-none");
				}
			}
			break
		default:
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				routeItemI.classList.remove('type-tender-none');
			}
			break
	}
});
if (localStorage.getItem('typeTenderSort') != null) {
	document.querySelector('#typeTenderSort').value = localStorage.getItem('typeTenderSort');
}
setTimeout(() => typeTenderSort(localStorage.getItem('typeTenderSort'), 100));
function typeTenderSort(target) {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		routeItemI.classList.remove('type-tender-none');
	}
	switch (target) {
		case '1':
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#cost').innerHTML.replace(/\s/g, '').split('-')[1] == undefined) {
					routeItemI.classList.add("type-tender-none");
				}
			}
			break

		case '2':  // if (x === 'value2')
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				if (routeItemI.querySelector('#cost').innerHTML.replace(/\s/g, '').split('-')[1] != undefined) {
					routeItemI.classList.add("type-tender-none");
				}
			}
			break
		default:
			for (let i = 1; i < routeItem.length; i++) {
				var routeItemI = routeItem[i];
				routeItemI.classList.remove('type-tender-none');
			}
			break
	}
}
var checkboxes = document.querySelectorAll("input[type=checkbox]");
for (let i = 0; i < checkboxes.length; i++) {
	var checkboxesI = checkboxes[i];
	if (localStorage.getItem(checkboxesI.dataset.columnClass) != null) {
		$(`#${checkboxesI.dataset.columnClass}`).prop('checked', false);
		toggleColumn(checkboxesI.dataset.columnClass);
	}
}

const controls = document.getElementById('controls');
controls.addEventListener('change', e => {
	toggleColumn(e.target.dataset.columnClass);
	if (localStorage.getItem(e.target.dataset.columnClass) == null) {
		localStorage.setItem(e.target.dataset.columnClass, e.target.dataset.columnClass);
	} else {
		localStorage.removeItem(e.target.dataset.columnClass)
	}
});

function toggleColumn(columnClass) {
	const cells = document.querySelectorAll(`.${columnClass}`);
	cells.forEach(cell => {
		cell.classList.toggle('hidden');
	});
}

//экранирование
function escape(string) {
    var htmlEscapes = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    };

    return string.replace(/[&<>"']/g, function(match) {
        return htmlEscapes[match];
    });
};

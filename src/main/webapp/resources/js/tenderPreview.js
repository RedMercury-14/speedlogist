import { getTenderPreviewBaseUrl } from "./globalConstants/urls.js";
import { dateHelper, getData } from "./utils.js";

const PAGE_NAME = 'tenderPreview'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

document.addEventListener("DOMContentLoaded", async () => {
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 0, 21)
	const tenders = await getTendersData(dateStart, dateEnd)
	console.log("🚀 ~ document.addEventListener ~ tenders:", tenders)

	const currentUrl = window.location.href
	if (!currentUrl.includes("tender-preview")) {
		tenders.length = 6
	}

	const cardsContainer = document.getElementById("cardsContainer")
	cardsContainer && tenders
		.sort((a, b) => new Date(b.tenderId) - new Date(a.tenderId))
		.forEach(tender => cardsContainer.appendChild(createCard(tender)))
})

// получение данных
async function getTendersData(dateStart, dateEnd) {
	const url = `${getTenderPreviewBaseUrl}${dateStart}&${dateEnd}`
	const res = await getData(url)
	if (!res) return []
	return res.tenderPreviewDtos ? res.tenderPreviewDtos : []
}

function createCard(tender) {
	const date = getLoadDate(tender.dateLoadActual)

	const truckType = tender.truckType || "—";
	const loadType = tender.loadType || "—";
	const temperature = truckType === "Рефрижератор" ? `Темп.: ${tender.temperature}` : ""
	const cargo = `${tender.cargo}`
	const weight = getWeight(tender.weight)
	const count = `${weight} т, ${tender.pallets} палл`
	const { load, unload } = getAdresses(tender.routeDirection)

	const card = document.createElement("div");
	card.className = "col-12 mb-3";
	// card.innerHTML = `
	// 	<div class="card shadow-sm">
	// 		<div class="card-body row">
	// 			<div class="px-2 mb-2 mb-md-0 col-md-1">
	// 				<p class="mb-0 text-muted small font-weight-bold">Загрузка: ${date}</p>
	// 			</div>
	// 			<div class="px-2 mb-2 mb-md-0 col-md-2 col-6">
	// 				<p class="mb-0 text-muted small font-weight-bold">${truckType}</p>
	// 				<p class="mb-0 text-muted small">Тип загрузки: ${loadType}</p>
	// 				<p class="mb-0 text-muted small">${temperature}</p>
	// 			</div>
	// 			<div class="px-2 mb-2 mb-md-0 col-md-1 col-6">
	// 				<p class="mb-0 text-muted small font-weight-bold">${cargo}</p>
	// 				<p class="mb-0 text-muted small">${count}</p>
	// 			</div>
	// 			<div class="px-2 mb-2 mb-md-0 col-md-4">
	// 				<p class="mb-0 text-muted small">${load}</p>
	// 			</div>
	// 			<div class="px-2 mb-2 mb-md-0 col-md-4">
	// 				<p class="mb-0 text-muted small">${unload}</p>
	// 			</div>
	// 		</div>
	// 	</div>
	// `
	card.innerHTML = `
		<div class="card shadow-sm position-relative">
			<div class="position-absolute" style="top:0; left:0; width:5px; height:100%; background-color:#b4e602;"></div>
			<div class="card-body row no-gutters align-items-start pl-2">
				<div class="px-2 mb-2 mb-md-0 col-md-2">
					<p class="text-muted mb-1">
						<i class="fas fa-calendar-alt mr-1 text-secondary">
						</i><strong>Загрузка</strong>
					</p>
					<p class="mb-0 text-muted small">${date}</p>
				</div>
				<div class="px-2 mb-2 mb-md-0 col-md-2 col-7">
					<p class="text-muted mb-1"><i class="fas fa-truck mr-1 text-secondary"></i><strong>${truckType}</strong></p>
					<p class="mb-0 text-muted small">Тип загрузки: ${loadType}</p>
					<p class="mb-0 text-muted small">${temperature}</p>
				</div>
				<div class="px-2 mb-2 mb-md-0 col-md-2 col-5">
					<p class="text-muted mb-1"><i class="fas fa-boxes mr-1 text-secondary"></i><strong>${cargo}</strong></p>
					<p class="mb-0 text-muted small">${count}</p>
				</div>
				<div class="px-2 mb-2 mb-md-0 col-md-3">
					<p class="text-muted mb-1"><i class="fas fa-map-marker-alt mr-1 text-secondary"></i><strong>Откуда</strong></p>
					<p class="mb-0 text-muted small">${load}</p>
				</div>
				<div class="px-2 mb-2 mb-md-0 col-md-3">
					<p class="text-muted mb-1"><i class="fas fa-location-arrow mr-1 text-secondary"></i><strong>Куда</strong></p>
					<p class="mb-0 text-muted small">${unload}</p>
				</div>
			</div>
		</div>
	`

	return card
}

function getLoadDate(dateArr) {
	if (!dateArr) return "—"
	if (dateArr.length === 0) return "—"

	const [y,m,d] = dateArr
	const date = new Date(y,m-1,d)
	return dateHelper.getFormatDate(date)
}

function getAdresses(routeDirection) {
	if (!routeDirection) return { load: "—", unload: "—" }

	const parts = routeDirection.split(" - ")
	const load = parts[0]?.trim() || "—"
	const unload = parts[parts.length - 1]?.trim() || "—"

	return { load, unload }
}

function getWeight(weight) {
	if (!weight) return ""
	const tonnage = Number(weight) / 1000
	const roundedTonnage = Math.round(tonnage * 10) / 10
	return roundedTonnage.toString()
}

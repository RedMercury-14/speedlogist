
import {
    getAllAcceptanceQualityFoodCardUrl,
} from "./globalConstants/urls.js";

const BASE_URL = 'http://localhost:8080/speedlogist_war'; // или путь для продакшн сервера

export async function openModal(card) {
    const modal = document.getElementById("acceptanceQualityMainCardsModal");

    console.log(card);

    // Показать модалку
    modal.style.display = 'block';

    // Начальный контент модалки
    let cardsContent = `
        <div class="modal-content">
            <span id="close-modal-btn" class="close">&times;</span>
            <h2>Список созданных карточек</h2>
            <p><strong>Фирма:</strong> ${card.firmNameAccept || "Не указано"}</p>
            <p><strong>Номер машины:</strong> ${card.carNumber || "Не указано"}</p>
            <p><strong>Информация:</strong> ${card.cardInfo || "Не указано"}</p>
            <div id="cards-container">Загрузка данных...</div>
        </div>
    `;

    // Вставляем базовое содержимое
    modal.innerHTML = cardsContent;

    const closeBtn = document.getElementById("close-modal-btn");
    closeBtn.addEventListener("click", () => {
        modal.style.display = 'none';
    });

    try {
        // Загружаем данные с сервера
        // const response = await fetch(`http://10.10.1.22:14000/quality/getAllAcceptanceQualityFoodCard?idAcceptanceFoodQuality=${card.idAcceptanceFoodQuality}`);
        // if (!response.ok) throw new Error("Ошибка загрузки данных");

        // const mockCards = await response.json();

        const mockCards = await getAcceptanceQualityCards(card.idAcceptanceFoodQuality);

        console.log("мок карды", mockCards)

        // Генерация карточек
        const cardsContainer = document.getElementById("cards-container");
        cardsContainer.innerHTML = mockCards.map(mockCard => `
            <div class="card-box">
                <h3>${mockCard.productName}</h3>
                <p><strong>Фирма:</strong> ${mockCard.firmNameAccept || "Не указано"}</p>
                <p><strong>Номер машины:</strong> ${mockCard.carNumber || "Не указано"}</p>
                <p><strong>Выборка:</strong> ${mockCard.sampleSize} из ${mockCard.cargoWeightCard}</p>
                <p><strong>Брак:</strong> ${mockCard.totalDefectWeight} кг / ${mockCard.totalDefectPercentage}% / ${mockCard.totalDefectPercentageWithPC || "-"}%</p>
                <div class="actions">
                    <button onclick="viewCard(${mockCard.idAcceptanceQualityFoodCard})">Просмотр</button>
                    <button onclick="editCard(${mockCard.idAcceptanceQualityFoodCard})">Редактировать</button>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error("Ошибка при загрузке данных: ", error);
        document.getElementById("cards-container").innerHTML = "Ошибка загрузки данных.";
    }
}

async function getAcceptanceQualityCards(idAcceptanceQuality) {

    let url = getAllAcceptanceQualityFoodCardUrl;


    try {
        // Выполняем GET-запрос с помощью AJAX
        const data = await new Promise((resolve, reject) => {
            $.ajax({
                type: "GET",
                url: `${BASE_URL}${url}?idAcceptanceFoodQuality=${idAcceptanceQuality}`,
                success: (res) => {
                    console.log(`Получены данные для статуса ${status}:`, res);
                    resolve(res); // Преобразуем результат в промис
                },
                error: (err) => {
                    const errorStatus = err.status ? err.status : '';
                    snackbar.show(`Ошибка ${errorStatusText[errorStatus]}!`);
                    console.error(`Ошибка при загрузке данных для статуса ${status}:`, err);
                    reject(err); // Пробрасываем ошибку через промис
                }
            });
        });

        return data; // Возвращаем данные из успешного запроса
    } catch (error) {
        console.error('Ошибка при выполнении GET-запроса:', error);
        return null; // Возвращаем null в случае ошибки
    }
}


function viewCard(id) {
    alert(`Просмотр карточки ${id}`);
}

function editCard(id) {
    alert(`Редактирование карточки ${id}`);
}







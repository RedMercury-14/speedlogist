import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { changeGridTableMarginTop, dateHelper, debounce, disableButton, enableButton, getData, hideLoadingSpinner, isAdmin, isOderSupport, showLoadingSpinner } from './utils.js'

const testData = [
	 {
	  "productCode": 717455,
	  "productName": "Вафли \"ЧЕРНОМОРСКИЕ ТОП\"(флоу-пак.) 87г",
	  "barcode": "^4810067087007",
	  "firstPrice": 0.52,
	  "priceWithoutVAT": 0.52,
	  "quantityOrdered": 501552,
	  "currentStock": 4.941799802,
	  "orderTurnover": 20.113688240333897
	 },
	 {
	  "productCode": 885082,
	  "productName": "Ваф. батончик \"SMILE TIME\"(Black) 21г",
	  "barcode": "^4810067094098",
	  "firstPrice": 0.2,
	  "priceWithoutVAT": 0.2,
	  "quantityOrdered": 86400,
	  "currentStock": 0,
	  "orderTurnover": 38.58144579600915
	 },
	 {
	  "productCode": 649177,
	  "productName": "Вафли \"ЧЕРНОМОРСКИЕ\"(флоу-пак.) 216г",
	  "barcode": "^4810067081890",
	  "firstPrice": 1.27,
	  "priceWithoutVAT": 1.27,
	  "quantityOrdered": 79380,
	  "currentStock": 11.64951793,
	  "orderTurnover": 13.12915258294009
	 },
	 {
	  "productCode": 586959,
	  "productName": "Батончик-мюсли \"ЗЛАКИ С КЛЮКВОЙ\" 35гр",
	  "barcode": "^4810067080855",
	  "firstPrice": 0.43,
	  "priceWithoutVAT": 0.43,
	  "quantityOrdered": 63360,
	  "currentStock": 7.959629153,
	  "orderTurnover": 16.86413988018741
	 },
	 {
	  "productCode": 1544074,
	  "productName": "Вафли \"СЛИВОЧНЫЕ\"(флоу-пак) 72г",
	  "barcode": "^4810067103011",
	  "firstPrice": 0.5,
	  "priceWithoutVAT": 0.5,
	  "quantityOrdered": 54432,
	  "currentStock": 6.440820599,
	  "orderTurnover": 19.039141243843467
	 },
	 {
	  "productCode": 697693,
	  "productName": "Вафли \"СПАРТАК ТОП\"(лимон,умельч) 72г",
	  "barcode": "^4810067084501",
	  "firstPrice": 0.47,
	  "priceWithoutVAT": 0.47,
	  "quantityOrdered": 51192,
	  "currentStock": 6.636437078,
	  "orderTurnover": 19.722076332352437
	 },
	 {
	  "productCode": 45591,
	  "productName": "Печенье \"МАРИЯ\" (Спартак) 140 г",
	  "barcode": "^4810067002994",
	  "firstPrice": 0.66,
	  "priceWithoutVAT": 0.66,
	  "quantityOrdered": 50400,
	  "currentStock": 9.043147538,
	  "orderTurnover": 16.042753816690976
	 },
	 {
	  "productCode": 47434,
	  "productName": "Вафли \"НА ФРУКТОЗЕ\" 100г",
	  "barcode": "^4810067003106",
	  "firstPrice": 0.86,
	  "priceWithoutVAT": 0.86,
	  "quantityOrdered": 31500,
	  "currentStock": 22.07193535,
	  "orderTurnover": 14.802971462963194
	 },
	 {
	  "productCode": 575036,
	  "productName": "Батончик-мюсли \"ЗЛАКИ С ВИШНЕЙ\" 35 г",
	  "barcode": "^4810067080817",
	  "firstPrice": 0.43,
	  "priceWithoutVAT": 0.43,
	  "quantityOrdered": 28800,
	  "currentStock": 8.671325568,
	  "orderTurnover": 15.08056620482218
	 },
	 {
	  "productCode": 9848,
	  "productName": "Печенье \"ВЕСЕННЯЯ РАПСОДИЯ\"  300 г",
	  "barcode": "^4810067000549",
	  "firstPrice": 1.96,
	  "priceWithoutVAT": 1.96,
	  "quantityOrdered": 22848,
	  "currentStock": 9.628894662,
	  "orderTurnover": 14.4670865549427
	 },
	 {
	  "productCode": 520831,
	  "productName": "Печенье \"ДИАБЕТИЧЕСКОЕ\" (сорбит) 100г",
	  "barcode": "^4810067079064",
	  "firstPrice": 0.59,
	  "priceWithoutVAT": 0.59,
	  "quantityOrdered": 19980,
	  "currentStock": 8.119570137,
	  "orderTurnover": 17.967550264296058
	 },
	 {
	  "productCode": 242655,
	  "productName": "Карамель \"КРАБОВЫЕ ШЕЙКИ\" РБ 150г",
	  "barcode": "^4810067068389",
	  "firstPrice": 0.95,
	  "priceWithoutVAT": 0.95,
	  "quantityOrdered": 18000,
	  "currentStock": 7.931135851,
	  "orderTurnover": 14.781574375336703
	 },
	 {
	  "productCode": 575037,
	  "productName": "Батончик-мюсли \"ЗЛАКИ С ПЕРСИКОМ\" 35 г",
	  "barcode": "^4810067080893",
	  "firstPrice": 0.43,
	  "priceWithoutVAT": 0.43,
	  "quantityOrdered": 13080,
	  "currentStock": 6.487121167,
	  "orderTurnover": 17.16252930180737
	 },
	 {
	  "productCode": 885084,
	  "productName": "Ваф.батончик \"SMILE TIME\"(ваниль) 21г",
	  "barcode": "^4810067094081",
	  "firstPrice": 0.2,
	  "priceWithoutVAT": 0.2,
	  "quantityOrdered": 12160,
	  "currentStock": 6.193889531,
	  "orderTurnover": 18.905044351736215
	 },
	 {
	  "productCode": 242656,
	  "productName": "Карамель \"РАЧКИ\" (с арахисом) РБ 150г",
	  "barcode": "^4810067068341",
	  "firstPrice": 0.95,
	  "priceWithoutVAT": 0.95,
	  "quantityOrdered": 12096,
	  "currentStock": 8.655170153,
	  "orderTurnover": 17.62507376565471
	 },
	 {
	  "productCode": 607020,
	  "productName": "Печенье \"ПОСТНОЕ\" (флоу-пак),220г",
	  "barcode": "^4810067082767",
	  "firstPrice": 1.01,
	  "priceWithoutVAT": 1.01,
	  "quantityOrdered": 7200,
	  "currentStock": 8.770352259,
	  "orderTurnover": 18.92881782591322
	 },
	 {
	  "productCode": 785990,
	  "productName": "Печенье\"ПОСТНОЕ\" (кунж\/лён) 220г",
	  "barcode": "^4810067091929",
	  "firstPrice": 1.05,
	  "priceWithoutVAT": 1.05,
	  "quantityOrdered": 7056,
	  "currentStock": 7.114009144,
	  "orderTurnover": 14.833465873763565
	 },
	 {
	  "productCode": 847126,
	  "productName": "Шоколад \"СПАРТАК\"(белый\/минд\/кокос) 90г",
	  "barcode": "^4810067092933",
	  "firstPrice": 2.34,
	  "priceWithoutVAT": 2.34,
	  "quantityOrdered": 4914,
	  "currentStock": 1.858680662,
	  "orderTurnover": 23.299889727084167
	 },
	 {
	  "productCode": 223996,
	  "productName": "Карамель \"КЛУБНИКА СО СЛИВК.\" (мет.)150г",
	  "barcode": "^4810067033158",
	  "firstPrice": 0.86,
	  "priceWithoutVAT": 0.86,
	  "quantityOrdered": 4248,
	  "currentStock": 4.034868342,
	  "orderTurnover": 19.83810268236506
	 },
	 {
	  "productCode": 68786,
	  "productName": "Конфеты \"ШОКОЛАДНЫЕ БУТЫЛОЧКИ\" 168г",
	  "barcode": "^4810067005162",
	  "firstPrice": 4.82,
	  "priceWithoutVAT": 4.82,
	  "quantityOrdered": 4140,
	  "currentStock": 11.86163233,
	  "orderTurnover": 14.935266981425613
	 },
	 {
	  "productCode": 607047,
	  "productName": "Вафли \"ЧЕРНОМОРСКИЕ\"(флоу-пак) 72г",
	  "barcode": "^4810067080756",
	  "firstPrice": 0.52,
	  "priceWithoutVAT": 0.52,
	  "quantityOrdered": 3942,
	  "currentStock": 16.56805144,
	  "orderTurnover": 13.666302317107032
	 },
	 {
	  "productCode": 1336273,
	  "productName": "Вафельный батончик \"MILX\" 35г",
	  "barcode": "^4810067099888",
	  "firstPrice": 0.51,
	  "priceWithoutVAT": 0.51,
	  "quantityOrdered": 3600,
	  "currentStock": 54.84376617,
	  "orderTurnover": 17.6535728015115
	 },
	 {
	  "productCode": 617914,
	  "productName": "Вафли \"СПАРТАК ТОП\"(халвичные,фл\/п) 72г",
	  "barcode": "^4810067080763",
	  "firstPrice": 0.5,
	  "priceWithoutVAT": 0.5,
	  "quantityOrdered": 3132,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 721454,
	  "productName": "Вафли \"СПАРТАК ТОП\"(на сорбите) 72г",
	  "barcode": "^4810067086338",
	  "firstPrice": 0.58,
	  "priceWithoutVAT": 0.58,
	  "quantityOrdered": 2322,
	  "currentStock": 17.74158323,
	  "orderTurnover": 15.569144468451032
	 },
	 {
	  "productCode": 495546,
	  "productName": "Конфеты\"СПАРТАК\" нуга гл.(кар. арах)210г",
	  "barcode": "^4810067078111",
	  "firstPrice": 2.57,
	  "priceWithoutVAT": 2.57,
	  "quantityOrdered": 2272,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 571573,
	  "productName": "Шоколад \"СПАРТАК\"(с цел. фундуком) 90 г",
	  "barcode": "^4810067080701",
	  "firstPrice": 2.25,
	  "priceWithoutVAT": 2.25,
	  "quantityOrdered": 2232,
	  "currentStock": 16.02536452,
	  "orderTurnover": 9.242535815966075
	 },
	 {
	  "productCode": 1473468,
	  "productName": "Печенье \"ВЕСЕННЯЯ РАПСОДИЯ\" (арахис)300г",
	  "barcode": "^4810067079897",
	  "firstPrice": 2.16,
	  "priceWithoutVAT": 2.16,
	  "quantityOrdered": 1472,
	  "currentStock": 7.944500669,
	  "orderTurnover": 16.997536314846908
	 },
	 {
	  "productCode": 664125,
	  "productName": "Шоколад \"СПАРТАК\"(горький с фундук) 90г",
	  "barcode": "^4810067085935",
	  "firstPrice": 2.34,
	  "priceWithoutVAT": 2.34,
	  "quantityOrdered": 1314,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 617911,
	  "productName": "Вафли \"АРТЕК\"(флоу-пак) 72г",
	  "barcode": "^4810067083917",
	  "firstPrice": 0.47,
	  "priceWithoutVAT": 0.47,
	  "quantityOrdered": 1242,
	  "currentStock": 7.544849941,
	  "orderTurnover": 17.353154865031016
	 },
	 {
	  "productCode": 74055,
	  "productName": "Печенье \"КРОКЕТ\" Спартак 170 г",
	  "barcode": "^4810067002666",
	  "firstPrice": 0.82,
	  "priceWithoutVAT": 0.82,
	  "quantityOrdered": 1230,
	  "currentStock": 0,
	  "orderTurnover": 27.075755509928793
	 },
	 {
	  "productCode": 1146307,
	  "productName": "Шоколад\"АЛЕНКА\"(молочный с начинкой) 45г",
	  "barcode": "^4810067099376",
	  "firstPrice": 0.76,
	  "priceWithoutVAT": 0.76,
	  "quantityOrdered": 1196,
	  "currentStock": 23.47429874,
	  "orderTurnover": 0.8879237576692883
	 },
	 {
	  "productCode": 690180,
	  "productName": "Печенье \"МОСК.ХЛЕБЦЫ \"(с изюмом) 200г",
	  "barcode": "^4810067084860",
	  "firstPrice": 2.4,
	  "priceWithoutVAT": 2.4,
	  "quantityOrdered": 1020,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 471131,
	  "productName": "Шок. \"IMPRESSO\"(темный и белый) 200г",
	  "barcode": "^4810067076766",
	  "firstPrice": 3.98,
	  "priceWithoutVAT": 3.98,
	  "quantityOrdered": 630,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 23129,
	  "productName": "Печенье \"К ЧАЮ\" Спартак 100 г",
	  "barcode": "^4810067001263",
	  "firstPrice": 0.49,
	  "priceWithoutVAT": 0.49,
	  "quantityOrdered": 555,
	  "currentStock": 9.572305046,
	  "orderTurnover": 20.512082240857215
	 },
	 {
	  "productCode": 785992,
	  "productName": "Печенье \"ПОСТНОЕ\" (семеч\/лён) 220г",
	  "barcode": "^4810067091936",
	  "firstPrice": 1.03,
	  "priceWithoutVAT": 1.03,
	  "quantityOrdered": 360,
	  "currentStock": 9.982543861,
	  "orderTurnover": 14.911683775861809
	 },
	 {
	  "productCode": 1207990,
	  "productName": "Печенье \"ЧУДО-УТРО\"(имбирное,ж\/б) 500г",
	  "barcode": "^4810067097167",
	  "firstPrice": 7.21,
	  "priceWithoutVAT": 7.21,
	  "quantityOrdered": 300,
	  "currentStock": 1.1199888,
	  "orderTurnover": 20.999790006299897
	 },
	 {
	  "productCode": 505574,
	  "productName": "Печенье \"ИМПРЕССО\" (с какао) 190г",
	  "barcode": "^4810067078227",
	  "firstPrice": 1.85,
	  "priceWithoutVAT": 1.85,
	  "quantityOrdered": 256,
	  "currentStock": 0,
	  "orderTurnover": 33.18641431164117
	 },
	 {
	  "productCode": 2070104,
	  "productName": "Конфеты \"ГРИЛЬЯЖ В ШОКОЛАДЕ\"(декор) 290г",
	  "barcode": "^4810067107156",
	  "firstPrice": 7.07,
	  "priceWithoutVAT": 7.07,
	  "quantityOrdered": 256,
	  "currentStock": 0,
	  "orderTurnover": 21.140385370777018
	 },
	 {
	  "productCode": 1631002,
	  "productName": "Шоколад \"MILX\"(с мол.нач) 180г",
	  "barcode": "^4810067101895",
	  "firstPrice": 3.18,
	  "priceWithoutVAT": 3.18,
	  "quantityOrdered": 192,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 505572,
	  "productName": "Печенье \"ИМПРЕССО\" (дробл.арах) 190г",
	  "barcode": "^4810067078210",
	  "firstPrice": 1.85,
	  "priceWithoutVAT": 1.85,
	  "quantityOrdered": 160,
	  "currentStock": 2.382978724,
	  "orderTurnover": 23.82978723505659
	 },
	 {
	  "productCode": 1199661,
	  "productName": "Конфеты  \"SPARTAK\" EXCLUSIVE 306г",
	  "barcode": "^4810067099123",
	  "firstPrice": 9.44,
	  "priceWithoutVAT": 9.44,
	  "quantityOrdered": 140,
	  "currentStock": 4.195950907,
	  "orderTurnover": 29.371656351620686
	 },
	 {
	  "productCode": 2052384,
	  "productName": "Шоколад \"СПАРТАК\"(горьк,б\/с,коллаг) 95г",
	  "barcode": "^4810067106227",
	  "firstPrice": 2.75,
	  "priceWithoutVAT": 2.75,
	  "quantityOrdered": 100,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 1797507,
	  "productName": "Конфеты \"СПАРТАК\" (пина колада) 144г",
	  "barcode": "^4810067104070",
	  "firstPrice": 1.81,
	  "priceWithoutVAT": 1.81,
	  "quantityOrdered": 72,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 1849129,
	  "productName": "Шоколад мол. \"СПАРТАК\"(врывной микс) 95г",
	  "barcode": "^4810067104308",
	  "firstPrice": 2.53,
	  "priceWithoutVAT": 2.53,
	  "quantityOrdered": 40,
	  "currentStock": 17.5,
	  "orderTurnover": 34.999999995625
	 },
	 {
	  "productCode": 1991000,
	  "productName": "Шоколад\"СПАРТАК\"(горьк,б\/с,клуб\/фунд)95г",
	  "barcode": "^4810067105862",
	  "firstPrice": 2.75,
	  "priceWithoutVAT": 2.75,
	  "quantityOrdered": 20,
	  "currentStock": 9999,
	  "orderTurnover": "#DIV\/0!"
	 },
	 {
	  "productCode": 2073588,
	  "productName": "Шоколад \"СПАРТАК\"(горьк,б\/с,кл\/фунд) 95г",
	  "barcode": "^4810067107408",
	  "firstPrice": 2.75,
	  "priceWithoutVAT": 2.75,
	  "quantityOrdered": 20,
	  "currentStock": 9999,
	  "orderTurnover": "#DIV\/0!"
	 },
	 {
	  "productCode": 2073585,
	  "productName": "Набор конфет \"DELUXE\"(кор) 260г",
	  "barcode": "^4810067108603",
	  "firstPrice": 7.38,
	  "priceWithoutVAT": 7.38,
	  "quantityOrdered": 6,
	  "currentStock": 9999,
	  "orderTurnover": "#DIV\/0!"
	 },
	 {
	  "productCode": 24688,
	  "productName": "Карамель \"ГРУША\" Спартак 150 г",
	  "barcode": "^4810067034032",
	  "firstPrice": 0.86,
	  "priceWithoutVAT": 0.86,
	  "quantityOrdered": 0,
	  "currentStock": 7.749509962,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 29624,
	  "productName": "Конфеты \"СПАРТАК\" 375г",
	  "barcode": "^4810067031215",
	  "firstPrice": 11.99,
	  "priceWithoutVAT": 11.99,
	  "quantityOrdered": 0,
	  "currentStock": 73.37526205,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 151318,
	  "productName": "Шок. батончик\"СПАРТАК\"(пом\/слив нач) 48г",
	  "barcode": "^4810067056027",
	  "firstPrice": 0.81,
	  "priceWithoutVAT": 0.81,
	  "quantityOrdered": 0,
	  "currentStock": 121.4031017,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 164531,
	  "productName": "Шок.батончик \"СПАРТАК\" (пом-шок.нач) 48г",
	  "barcode": "^4810067060161",
	  "firstPrice": 0.72,
	  "priceWithoutVAT": 0.72,
	  "quantityOrdered": 0,
	  "currentStock": 58.03735788,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 515715,
	  "productName": "Набор конфет \"АССОРТИ\" 171г",
	  "barcode": "^4810067078029",
	  "firstPrice": 4.75,
	  "priceWithoutVAT": 4.75,
	  "quantityOrdered": 0,
	  "currentStock": 70.4339645,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 574905,
	  "productName": "Конфеты \"ТРЮФЕЛИ НЕЖНЫЕ\" 255г",
	  "barcode": "^4810067080251",
	  "firstPrice": 5.72,
	  "priceWithoutVAT": 5.72,
	  "quantityOrdered": 0,
	  "currentStock": 203.28,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 656295,
	  "productName": "Набор конфет \"IMPRESSO\"(белый) 424г",
	  "barcode": "^4810067085263",
	  "firstPrice": 13.79,
	  "priceWithoutVAT": 13.79,
	  "quantityOrdered": 0,
	  "currentStock": 211.4844346,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 690152,
	  "productName": "Шоколад \"СПАРТАК\"(горький с миндал) 90г",
	  "barcode": "^4810067085942",
	  "firstPrice": 2.34,
	  "priceWithoutVAT": 2.34,
	  "quantityOrdered": 0,
	  "currentStock": 118.9209829,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 758748,
	  "productName": "Набор конфет \"IMPRESSO\"(бежевый) 424г",
	  "barcode": "^4810067086765",
	  "firstPrice": 13.79,
	  "priceWithoutVAT": 13.79,
	  "quantityOrdered": 0,
	  "currentStock": 214.6703083,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 758752,
	  "productName": "Набор конфет \"IMPRESSO\"(коричневый) 424г",
	  "barcode": "^4810067086758",
	  "firstPrice": 13.79,
	  "priceWithoutVAT": 13.79,
	  "quantityOrdered": 0,
	  "currentStock": 126,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 825987,
	  "productName": "Конфеты глазированные \"АЭРОФЛОТСКИЕ\" 35г",
	  "barcode": "^4810067092285",
	  "firstPrice": 0.46,
	  "priceWithoutVAT": 0.46,
	  "quantityOrdered": 0,
	  "currentStock": 84.16247355,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 922895,
	  "productName": "Конфеты \"АЭРОФЛОТСКИЕ\"(фл-п) 200г",
	  "barcode": "^4810067094463",
	  "firstPrice": 2.56,
	  "priceWithoutVAT": 2.56,
	  "quantityOrdered": 0,
	  "currentStock": 108.2220199,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 996793,
	  "productName": "Конфеты\"АЭРОФЛОТСКИЕ\"(флоу-пак, вес)1кг",
	  "barcode": "^4810067092568",
	  "firstPrice": 12.48,
	  "priceWithoutVAT": 12.48,
	  "quantityOrdered": 0,
	  "currentStock": 83.09333559,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1329514,
	  "productName": "Бат. \"ZA SPARTAK\"(нуг\/кар\/арах\/фун)50г",
	  "barcode": "^4810067099543",
	  "firstPrice": 0.62,
	  "priceWithoutVAT": 0.62,
	  "quantityOrdered": 0,
	  "currentStock": 57.25363778,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1329521,
	  "productName": "Батончик\"ZA SPARTAK\"(нуг\/карам\/какао)48г",
	  "barcode": "^4810067099567",
	  "firstPrice": 0.61,
	  "priceWithoutVAT": 0.61,
	  "quantityOrdered": 0,
	  "currentStock": 81.93999177,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1508748,
	  "productName": "Шоколад \"СПАРТАК\"(белый,конверт) 85г",
	  "barcode": "^4810067101802",
	  "firstPrice": 1.79,
	  "priceWithoutVAT": 1.79,
	  "quantityOrdered": 0,
	  "currentStock": 92.41045738,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1512440,
	  "productName": "Шоколад \"MILX\"(молоч.дроб.фунд) 90г",
	  "barcode": "^4810067101451",
	  "firstPrice": 2.1,
	  "priceWithoutVAT": 2.1,
	  "quantityOrdered": 0,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 1521986,
	  "productName": "Шоколад \"СПАРТАК\"(горьк.пор.пенал) 70г",
	  "barcode": "^4810067101833",
	  "firstPrice": 1.81,
	  "priceWithoutVAT": 1.81,
	  "quantityOrdered": 0,
	  "currentStock": 88.47942444,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1527452,
	  "productName": "Шок. \"СПАРТАК ГОРЬКИЙ\"(эт-крафт,56%) 85г",
	  "barcode": "^4810067101987",
	  "firstPrice": 1.8,
	  "priceWithoutVAT": 1.8,
	  "quantityOrdered": 0,
	  "currentStock": 102.9067877,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1527455,
	  "productName": "Шок. \"СПАРТАК\"(гор.элит.72%) 85г",
	  "barcode": "^4810067102021",
	  "firstPrice": 1.95,
	  "priceWithoutVAT": 1.95,
	  "quantityOrdered": 0,
	  "currentStock": 55.29072635,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1527462,
	  "productName": "Шоколад \"СПАРТАК\"(белый порист) 70г",
	  "barcode": "^4810067101826",
	  "firstPrice": 1.58,
	  "priceWithoutVAT": 1.58,
	  "quantityOrdered": 0,
	  "currentStock": 35.01907602,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1527466,
	  "productName": "Шоколад \"СПАРТАК\"(молоч.крафт) 85г",
	  "barcode": "^4810067101963",
	  "firstPrice": 1.8,
	  "priceWithoutVAT": 1.8,
	  "quantityOrdered": 0,
	  "currentStock": 189.7218425,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1620524,
	  "productName": "Шоколад \"АЛЕНКА\"(мол.конв,шоу-бокс) 85г",
	  "barcode": "^4810067101796",
	  "firstPrice": 1.84,
	  "priceWithoutVAT": 1.84,
	  "quantityOrdered": 0,
	  "currentStock": 95.3021953,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1670176,
	  "productName": "Набор конфет \"ПТИЧЬЕ МОЛОКО\" 205г",
	  "barcode": "^4810067102939",
	  "firstPrice": 5.12,
	  "priceWithoutVAT": 5.12,
	  "quantityOrdered": 0,
	  "currentStock": 154.0025455,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1146312,
	  "productName": "Набор конфет \"DELUXE\" 260г",
	  "barcode": "^4810067098973",
	  "firstPrice": 7.38,
	  "priceWithoutVAT": 7.38,
	  "quantityOrdered": 0,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 1527453,
	  "productName": "Шоколад \"СПАРТАК\"(пенал,гор.56%) 85г",
	  "barcode": "^4810067101970",
	  "firstPrice": 2.07,
	  "priceWithoutVAT": 2.07,
	  "quantityOrdered": 0,
	  "currentStock": 83.65957334,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1527458,
	  "productName": "Шоколад \"СПАРТАК\"(молоч.орех.порист) 70г",
	  "barcode": "^4810067101857",
	  "firstPrice": 1.81,
	  "priceWithoutVAT": 1.81,
	  "quantityOrdered": 0,
	  "currentStock": 100.6594972,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1801565,
	  "productName": "Шоколад \"СПАРТАК\"(горк.апелс,пенал) 95г",
	  "barcode": "^4810067103806",
	  "firstPrice": 2.53,
	  "priceWithoutVAT": 2.53,
	  "quantityOrdered": 0,
	  "currentStock": 106.2379286,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 607056,
	  "productName": "Шоколад \"СПАРТАК\"(молочн,с арах) 90г",
	  "barcode": "^4810067082231",
	  "firstPrice": 1.94,
	  "priceWithoutVAT": 1.94,
	  "quantityOrdered": 0,
	  "currentStock": 81.69100875,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 861991,
	  "productName": "Шоколад \"СПАРТАК\"(бел,фундук\/злаки) 90г",
	  "barcode": "^4810067092957",
	  "firstPrice": 2.34,
	  "priceWithoutVAT": 2.34,
	  "quantityOrdered": 0,
	  "currentStock": 91.97054837,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1846757,
	  "productName": "Шоколад \"СПАРТАК\"(малина\/фунд,пенал) 95г",
	  "barcode": "^4810067104223",
	  "firstPrice": 2.53,
	  "priceWithoutVAT": 2.53,
	  "quantityOrdered": 0,
	  "currentStock": 114.8617187,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1805504,
	  "productName": "Набор конфет \"СПАДЧЫНА БЕЛАРУСI\" 340г",
	  "barcode": "^4810067103448",
	  "firstPrice": 9.54,
	  "priceWithoutVAT": 9.54,
	  "quantityOrdered": 0,
	  "currentStock": 379.5555555,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 775611,
	  "productName": "Шоколад Молочный 20г",
	  "barcode": "^4810067091394",
	  "firstPrice": 0.45,
	  "priceWithoutVAT": 0.45,
	  "quantityOrdered": 0,
	  "currentStock": 64.82660614,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1815087,
	  "productName": "Шоколад \"МОЛОЧНЫЙ\"(пенал) 95г",
	  "barcode": "^4810067104155",
	  "firstPrice": 2.09,
	  "priceWithoutVAT": 2.09,
	  "quantityOrdered": 0,
	  "currentStock": 185.1220945,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 1815088,
	  "productName": "ШОКОЛАД \"СПАРТАК\" ГОРЬКИЙ 56% (пенал)95г",
	  "barcode": "^4810067104162",
	  "firstPrice": 2.07,
	  "priceWithoutVAT": 2.07,
	  "quantityOrdered": 0,
	  "currentStock": 120.7517547,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 969899,
	  "productName": "Набор конфет \"ИМПРЕССО\" (ж\/б) 423г",
	  "barcode": "^4810067095507",
	  "firstPrice": 16.29,
	  "priceWithoutVAT": 16.29,
	  "quantityOrdered": 0,
	  "currentStock": 59.22568461,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 51193,
	  "productName": "Конфеты \"БЕЛОВЕЖСКИЕ ЗУБРЫ\" 250г",
	  "barcode": "^4810067079040",
	  "firstPrice": 8.05,
	  "priceWithoutVAT": 8.05,
	  "quantityOrdered": 0,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 1990999,
	  "productName": "Шоколад \"СПАРТАК\" (горький,б\/сахара) 95г",
	  "barcode": "^4810067105855",
	  "firstPrice": 2.52,
	  "priceWithoutVAT": 2.52,
	  "quantityOrdered": 0,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 471130,
	  "productName": "Шоколад \"IMPRESSO\"(пралине) 200г",
	  "barcode": "^4810067076865",
	  "firstPrice": 3.81,
	  "priceWithoutVAT": 3.81,
	  "quantityOrdered": 0,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 516986,
	  "productName": "Набор конфет \"СПАРТАК\" 300г",
	  "barcode": "^4810067078852",
	  "firstPrice": 10.3,
	  "priceWithoutVAT": 10.3,
	  "quantityOrdered": 0,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 2070105,
	  "productName": "Конфеты \"БЕЛОВЕЖСКИЕ ЗУБРЫ\"(кор) 250г",
	  "barcode": "^4810067108320",
	  "firstPrice": 7.75,
	  "priceWithoutVAT": 7.75,
	  "quantityOrdered": 0,
	  "currentStock": 80.48780489,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 2060711,
	  "productName": "Конфеты \"АЛЁНКА\"(глазир,флоу-пак) 1 кг",
	  "barcode": "^4810067106975",
	  "firstPrice": 12.48,
	  "priceWithoutVAT": 12.48,
	  "quantityOrdered": 0,
	  "currentStock": 68.89225035,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 31034,
	  "productName": "Конфеты \"ГРИЛЬЯЖ В ШОКОЛАДЕ\" 290г",
	  "barcode": "^4810067001027",
	  "firstPrice": 7.07,
	  "priceWithoutVAT": 7.07,
	  "quantityOrdered": 0,
	  "currentStock": "#N\/A",
	  "orderTurnover": "#N\/A"
	 },
	 {
	  "productCode": 2060712,
	  "productName": "Конфеты \"АЛЁНКА\"(глазир,флоу-пак) 35г",
	  "barcode": "^4810067107132",
	  "firstPrice": 0.46,
	  "priceWithoutVAT": 0.46,
	  "quantityOrdered": 0,
	  "currentStock": 73.56705124,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 2070106,
	  "productName": "Конфеты \"ДАРЫ ПОЛЕСЬЯ\"(кор) 282г",
	  "barcode": "^4810067107163",
	  "firstPrice": 7.25,
	  "priceWithoutVAT": 7.25,
	  "quantityOrdered": 0,
	  "currentStock": 43.40997864,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 2060714,
	  "productName": "Конфеты \"АЛЁНКА\"(глазир,флоу-пак) 200г",
	  "barcode": "^4810067107149",
	  "firstPrice": 2.56,
	  "priceWithoutVAT": 2.56,
	  "quantityOrdered": 0,
	  "currentStock": 57.08437451,
	  "orderTurnover": 0
	 },
	 {
	  "productCode": 2073586,
	  "productName": "Набор конфет \"СПАРТАК\"(кор) 300г",
	  "barcode": "^4810067108290",
	  "firstPrice": 10.3,
	  "priceWithoutVAT": 10.3,
	  "quantityOrdered": 0,
	  "currentStock": 9999,
	  "orderTurnover": "#DIV\/0!"
	 },
	 {
	  "productCode": 1815090,
	  "productName": "ШОКОЛАД СПАРТАК (ГОРЬКИЙ-ЭЛИТНЫЙ 90%)95г",
	  "barcode": "^4810067104179",
	  "firstPrice": 1.8,
	  "priceWithoutVAT": 1.8,
	  "quantityOrdered": 0,
	  "currentStock": 9999,
	  "orderTurnover": "#DIV\/0!"
	 },
	 {
	  "productCode": 1991044,
	  "productName": "Шоколад \"СПАРТАК\"(горький,элит,68%) 95г",
	  "barcode": "^4810067105589",
	  "firstPrice": 2.26,
	  "priceWithoutVAT": 2.26,
	  "quantityOrdered": 0,
	  "currentStock": 9999,
	  "orderTurnover": "#DIV\/0!"
	 }
]


const loadExcelUrl = '../../api/slots/'

const PAGE_NAME = 'excerpt'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)


let error = false
let table
let scheduleData
const stocks = ['1700', '1250', '1200']

const columnDefs = [
	{
		headerName: 'Код товара', field: 'productCode',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Наименование товара', field: 'productName',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Штрих-код', field: 'barcode',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Цена первая', field: 'firstPrice',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Цена без НДС', field: 'priceWithoutVAT',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Количество заказано', field: 'quantityOrdered',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Текущий сток РЦ', field: 'currentStock',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Оборач-ть заказа, дн.', field: 'orderTurnover',
		cellClass: 'px-1 py-0 text-center',
	},
]


const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2',
		flex: 1,
		resizable: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	suppressRowClickSelection: true,
	suppressDragLeaveHidesColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,
	sideBar: {
		toolPanels: [
			{
				id: 'columns',
				labelDefault: 'Columns',
				labelKey: 'columns',
				iconKey: 'columns',
				toolPanel: 'agColumnsToolPanel',
				toolPanelParams: {
					suppressRowGroups: true,
					suppressValues: true,
					suppressPivots: true,
					suppressPivotMode: true,
				},
			},
			{
				id: 'filters',
				labelDefault: 'Filters',
				labelKey: 'filters',
				iconKey: 'filter',
				toolPanel: 'agFiltersToolPanel',
			},
			{
				id: 'resetState',
				iconKey: 'menu',
				labelDefault: 'Сброс настроек',
				toolPanel: ResetStateToolPanel,
				toolPanelParams: {
					localStorageKey: LOCAL_STORAGE_KEY,
				},
			},
		],
	},
}

window.onload = async () => {
	const gridDiv = document.querySelector('#myGrid')

	// форма загрузки графика из Эксель
	const sendExcelForm = document.querySelector("#sendExcelForm")
	sendExcelForm && sendExcelForm.addEventListener("submit", sendExcelFormHandler)

	// выпадающий список выбора отображаемого склада
	const numStockSelect = document.querySelector("#numStockSelect")
	createNumStockOptions(numStockSelect)
	numStockSelect && numStockSelect.addEventListener('change', onNumStockSelectChangeHandler)

	const excelNumStock = document.querySelector('#sendExcelModal #numStock')


	// создаем опции складов
	createOptions(stocks, excelNumStock)

	// изменение отступа для таблицы
	changeGridTableMarginTop()
	// создание таблицы
	renderTable(gridDiv, gridOptions, testData)
	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()


	// очистка форм при закрытии модалки
	$('#addScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, addScheduleItemForm))
	$('#editScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, editScheduleItemForm))
}


function renderTable(gridDiv, gridOptions, data) {
	table = new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)
	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
async function updateTable() {
	if (!testData || !testData.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(testData)
	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

function getMappingData(data) {
	return data.map(getMappingItem)
}
function getMappingItem(item) {
	return {
		...item,
	}
}
function getContextMenuItems(params) {
	const rowNode = params.node

	const result = [
		
		"separator",
		"copy",
		"export",
	]

	return result
}


// обработчик смены склада
function onNumStockSelectChangeHandler(e) {
	const numStock = Number(e.target.value)

}



// обработчик отправки формы загрузки таблицы эксель
function sendExcelFormHandler(e) {
	e.preventDefault()

	if (!isAdmin(role)) return

	const submitButton = e.submitter
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: loadExcelUrl,
		token: token,
		data: file,
		successCallback: (res) => {
			snackbar.show(res[200])
			updateTable()
			$(`#sendExcelModal`).modal('hide')
			hideLoadingSpinner(submitButton, 'Загрузить')
		},
		errorCallback: () => hideLoadingSpinner(submitButton, 'Загрузить')
	})
}


// создание опций складов
function createNumStockOptions(numStockSelect) {
	if (!numStockSelect) return
	stocks.forEach((stock) => {
		const option = document.createElement("option")
		option.value = stock
		option.text = `Склад ${stock}`
		numStockSelect.append(option)
	})
}

// создание опций
function createOptions(optionData, select) {
	optionData.forEach((option) => {
		const optionElement = document.createElement('option')
		optionElement.value = option
		optionElement.text = option
		select.append(optionElement)
	})
}

// очистка формы
function clearForm(e, form) {
	form.reset()
}

// функции управления состоянием колонок
function saveColumnState() {
	gridColumnLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreColumnState() {
	gridColumnLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}

// функции управления фильтрами колонок
function saveFilterState() {
	gridFilterLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreFilterState() {
	gridFilterLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}

// отображение модального окна с сообщением
function showMessageModal(message) {
	const messageContainer = document.querySelector('#messageContainer')
	messageContainer.innerText = message
	$('#displayMessageModal').modal('show')
}

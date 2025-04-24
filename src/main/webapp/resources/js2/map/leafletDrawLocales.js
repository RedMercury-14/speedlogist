// -------------------------------------------------------------------------------//
// -------------- Локализация для библиотеки рисования leafletDraw ---------------//
// -------------------------------------------------------------------------------//

var i = {
        draw: {
            toolbar: {
                actions: { title: "Cancel drawing", text: "Cancel" },
                finish: { title: "Finish drawing", text: "Finish" },
                undo: { title: "Delete last point drawn", text: "Delete last point" },
                buttons: {
                    polyline: "Draw a polyline",
                    polygon: "Draw a polygon",
                    rectangle: "Draw a rectangle",
                    circle: "Draw a circle",
                    marker: "Draw a marker",
                    circlemarker: "Draw a circlemarker",
                },
            },
            handlers: {
                circle: { tooltip: { start: "Click and drag to draw circle." }, radius: "Radius" },
                circlemarker: { tooltip: { start: "Click map to place circle marker." } },
                marker: { tooltip: { start: "Click map to place marker." } },
                polygon: {
                    tooltip: {
                        start: "Click to start drawing shape.",
                        cont: "Click to continue drawing shape.",
                        end: "Click first point to close this shape.",
                    },
                },
                polyline: {
                    error: "<strong>Error:</strong> shape edges cannot cross!",
                    tooltip: {
                        start: "Click to start drawing line.",
                        cont: "Click to continue drawing line.",
                        end: "Click last point to finish line.",
                    },
                },
                rectangle: { tooltip: { start: "Click and drag to draw rectangle." } },
                simpleshape: { tooltip: { end: "Release mouse to finish drawing." } },
            },
        },
        edit: {
            toolbar: {
                actions: {
                    save: { title: "Save changes.", text: "Save" },
                    cancel: { title: "Cancel editing, discards all changes.", text: "Cancel" },
                    clearAll: { title: "Clear all layers.", text: "Clear All" },
                },
                buttons: {
                    edit: "Edit layers.",
                    editDisabled: "No layers to edit.",
                    remove: "Delete layers.",
                    removeDisabled: "No layers to delete.",
                },
            },
            handlers: {
                edit: {
                    tooltip: {
                        text: "Drag handles, or marker to edit feature.",
                        subtext: "Click cancel to undo changes.",
                    },
                },
                remove: { tooltip: { text: "Click on a feature to remove" } },
            },
        },
    },
    m = {
        draw: {
            toolbar: {
                actions: { title: "Отменить рисование", text: "Отмена" },
                finish: { title: "Завершить рисование", text: "Завершить" },
                undo: { title: "Удалить последнюю нарисованную точку", text: "Удалить последнюю точку" },
                buttons: {
                    polyline: "Нарисовать полилинию",
                    polygon: "Нарисовать полигон",
                    rectangle: "Нарисовать прямоугольник",
                    circle: "Нарисовать круг",
                    marker: "Нарисовать точку",
                    circlemarker: "Нарисовать точку(в виде круга)",
                },
            },
            handlers: {
                circle: {
                    tooltip: { start: "Кликните и перетащите для того, чтобы нарисовать круг." },
                    radius: "Радиус",
                },
                circlemarker: { tooltip: { start: "Кликните на карту для установки точки." } },
                marker: { tooltip: { start: "Кликните на карту для установки точки." } },
                polygon: {
                    tooltip: {
                        start: "Кликните, чтобы начать рисовать контур.",
                        cont: "Кликните, чтобы завершить рисовать контур.",
                        end: "Кликните на первую точку, чтобы завершить рисование контура.",
                    },
                },
                polyline: {
                    error: "<strong>Ошибка:</strong> линия не может самопересекаться!",
                    tooltip: {
                        start: "Кликните, чтобы начать рисовать линию.",
                        cont: "Кликните, чтобы завершить рисование линии.",
                        end: "Кликните на последнюю точку, чтобы завершить рисование линии.",
                    },
                },
                rectangle: { tooltip: { start: "Кликните и перетащите, чтобы нарисовать прямоугольник." } },
                simpleshape: { tooltip: { end: "Отпустите кнопку мыши для завершения рисования." } },
            },
        },
        edit: {
            toolbar: {
                actions: {
                    save: { title: "Сохранить изменения.", text: "Сохранить" },
                    cancel: { title: "Отменить редактирование, откатить все изменения.", text: "Отмена" },
                    clearAll: { title: "Очистить все редактируемые слои.", text: "Очистить всё" },
                },
                buttons: {
                    edit: "Редактировать.",
                    editDisabled: "Нет слоёв для редактирования.",
                    remove: "Удалить.",
                    removeDisabled: "Нет слоёв для удаления.",
                },
            },
            handlers: {
                edit: {
                    tooltip: {
                        text: "Перетащите вершины или точки для редактирования фигуры.",
                        subtext: 'Нажмите "Отмена", чтобы откатить изменения.',
                    },
                },
                remove: { tooltip: { text: "Кликните на фигуру для удаления" } },
            },
        },
    },
    x = [
        "en",
        "ru",
    ],
    f = function (x) {
        var f = i;
        switch (x.toLocaleLowerCase()) {
            case "en":
            case "en-us":
            case "en-ca":
            case "en-gb":
            case "en-us.utf-8":
            case "english":
                f = i;
                break;
            case "ru":
            case "ru-ru":
            case "ru-ru.utf-8":
            case "russian":
                f = m;
                break;
            default:
                throw new Error("[language] not found");
        }
        try {
            L && L.drawLocal && (L.drawLocal = f);
        } catch (e) {}
        return f;
    };
export { f as default, f as drawLocales, x as languages };

window.dateFnsLocaleRu = {
    code: "ru",
    formatDistance: (token, count, options) => {
        const formatDistanceLocale = {
            lessThanXSeconds: "меньше {{count}} секунд",
            xSeconds: "{{count}} секунд",
            halfAMinute: "полминуты",
            lessThanXMinutes: "меньше {{count}} минут",
            xMinutes: "{{count}} минут",
            aboutXHours: "около {{count}} часов",
            xHours: "{{count}} часов",
            xDays: "{{count}} дней",
            aboutXMonths: "около {{count}} месяцев",
            xMonths: "{{count}} месяцев",
            aboutXYears: "около {{count}} лет",
            xYears: "{{count}} лет",
            overXYears: "более {{count}} лет",
            almostXYears: "почти {{count}} лет",
        };
        return formatDistanceLocale[token].replace("{{count}}", count);
    },
    formatLong: {
        date: () => "dd MMMM yyyy",
        time: () => "HH:mm",
        dateTime: () => "dd MMMM yyyy, HH:mm",
    },
    formatRelative: (token) => {
        const formatRelativeLocale = {
            lastWeek: "'в прошлый' eeee 'в' p",
            yesterday: "'вчера в' p",
            today: "'сегодня в' p",
            tomorrow: "'завтра в' p",
            nextWeek: "eeee 'в' p",
            other: "P",
        };
        return formatRelativeLocale[token];
    },
    localize: {
        ordinalNumber: (dirtyNumber) => `${dirtyNumber}-й`,
        era: (value) => (value === "AD" ? "н. э." : "до н. э."),
        quarter: (quarter) => `${quarter}-й квартал`,
        month: (value) =>
            [
                "январь",
                "февраль",
                "март",
                "апрель",
                "май",
                "июнь",
                "июль",
                "август",
                "сентябрь",
                "октябрь",
                "ноябрь",
                "декабрь",
            ][value],
        day: (value) => ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"][value],
        dayPeriod: (value) => (value === "am" ? "AM" : "PM"),
    },
    match: {
        ordinalNumber: (str) => parseInt(str),
        era: (str) => (str === "н. э." ? "AD" : "BC"),
        quarter: (str) => parseInt(str),
        month: (str) =>
            [
                "январь",
                "февраль",
                "март",
                "апрель",
                "май",
                "июнь",
                "июль",
                "август",
                "сентябрь",
                "октябрь",
                "ноябрь",
                "декабрь",
            ].indexOf(str),
        day: (str) => ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"].indexOf(str),
        dayPeriod: (str) => (str === "AM" ? "am" : "pm"),
    },
    options: {
        weekStartsOn: 1,
        firstWeekContainsDate: 4,
    },
};

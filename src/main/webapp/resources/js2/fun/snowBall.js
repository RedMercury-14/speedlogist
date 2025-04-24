document.addEventListener('DOMContentLoaded', () => {
    const style = document.createElement('style');
    style.innerHTML = `
        body {
            margin: 0;
            padding: 0;
            overflow: hidden;
            background-color: #000;
        }

        .snow-container {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 9999;
        }

        .snowflake {
            position: absolute;
            top: -10px;
            font-size: 24px; /* Size of the snowflake */
            opacity: 0.8;
            animation-name: fall;
            animation-timing-function: linear;
            animation-iteration-count: infinite;
        }

        @keyframes fall {
            to {
                transform: translateY(100vh);
            }
        }
    `;
    document.head.appendChild(style);

    // Создаем контейнер для снежинок
    const snowContainer = document.createElement('div');
    snowContainer.classList.add('snow-container');
    document.body.appendChild(snowContainer);

    const numSnowflakes = 100;

    for (let i = 0; i < numSnowflakes; i++) {
        createSnowflake();
    }

    function createSnowflake() {
        const snowflake = document.createElement('div');
        snowflake.classList.add('snowflake');
        snowflake.innerHTML = '&#128405;'; // Snowflake symbol

        // Random font size for snowflake
        const size = Math.random() * 10 + 50; // Random size between 10px and 20px
        snowflake.style.fontSize = `${size}px`;

        // Random horizontal position
        snowflake.style.left = `${Math.random() * window.innerWidth}px`;

        // Random animation duration
        snowflake.style.animationDuration = `${Math.random() * 5 + 5}s`;

        // Random animation delay
        snowflake.style.animationDelay = `${Math.random() * 5}s`;

        snowContainer.appendChild(snowflake);
    }
});
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
            top: 0;
            left: 0;
            font-size: 24px;
            opacity: 0.8;
            will-change: transform;
        }
    `;
    document.head.appendChild(style);

    const snowContainer = document.createElement('div');
    snowContainer.classList.add('snow-container');
    document.body.appendChild(snowContainer);

    const numSnowflakes = 100;
    const snowflakes = [];

    for (let i = 0; i < numSnowflakes; i++) {
        createSnowflake();
    }

    function createSnowflake() {
        const snowflake = document.createElement('div');
        snowflake.classList.add('snowflake');
        snowflake.innerHTML = '&#127804;'; // Snowflake symbol

        const size = Math.random() * 10 + 10; // Random size between 10px and 20px
        snowflake.style.fontSize = `${size}px`;

        const initialX = Math.random() * window.innerWidth;
        const initialY = Math.random() * window.innerHeight;
        const velocityX = (Math.random() - 0.5) * 2;
        const velocityY = (Math.random() - 0.5) * 2;

        snowflake.style.transform = `translate(${initialX}px, ${initialY}px)`;

        snowflakes.push({
            element: snowflake,
            x: initialX,
            y: initialY,
            vx: velocityX,
            vy: velocityY,
            size: size
        });

        snowContainer.appendChild(snowflake);
    }

    function animateSnowflakes() {
        snowflakes.forEach(snowflake => {
            snowflake.x += snowflake.vx;
            snowflake.y += snowflake.vy;

            if (snowflake.x <= 0 || snowflake.x >= window.innerWidth - snowflake.size) {
                snowflake.vx *= -1;
            }
            if (snowflake.y <= 0 || snowflake.y >= window.innerHeight - snowflake.size) {
                snowflake.vy *= -1;
            }

            snowflake.element.style.transform = `translate(${snowflake.x}px, ${snowflake.y}px)`;
        });

        requestAnimationFrame(animateSnowflakes);
    }

    animateSnowflakes();
});
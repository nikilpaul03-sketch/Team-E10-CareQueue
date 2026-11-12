document.addEventListener("DOMContentLoaded", () => {
    const clockElement = document.getElementById("liveClock");
    if (clockElement) {
        const updateClock = () => {
            const now = new Date();
            clockElement.textContent = now.toLocaleDateString("en-US", {
                weekday: "long",
                year: "numeric",
                month: "long",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
                hour12: false
            });
        };
        updateClock();
        window.setInterval(updateClock, 1000);
    }

    const button = document.getElementById("btnSimulateToken");
    const currentToken = document.getElementById("currentTokenDisplay");
    const nextTokens = document.getElementById("nextTokenDisplay");
    const announcement = document.getElementById("simAlert");

    if (!button || !currentToken) return;

    let tokenIndex = 108;
    button.addEventListener("click", () => {
        tokenIndex += 1;
        const calledToken = `A-${tokenIndex}`;
        currentToken.textContent = calledToken;
        if (nextTokens) {
            nextTokens.textContent = `A-${tokenIndex + 1}, A-${tokenIndex + 2}`;
        }
        if (announcement) {
            announcement.textContent = `Sample announcement: calling ${calledToken} to Room 104. This demo is not connected to the live queue.`;
        }
    });
});

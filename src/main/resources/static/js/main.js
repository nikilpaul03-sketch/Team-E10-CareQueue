/**
 * Team E10 - Smart Public Healthcare Appointment and Queue Management System
 * Client-Side JavaScript (Phase 1 Baseline)
 */

document.addEventListener("DOMContentLoaded", () => {
    console.log("Team E10: Smart Public Healthcare System - Frontend Initialized (Phase 1)");

    // Live Clock Update
    const clockElement = document.getElementById("liveClock");
    if (clockElement) {
        const updateClock = () => {
            const now = new Date();
            const options = {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: false
            };
            clockElement.textContent = now.toLocaleDateString('en-US', options);
        };
        setInterval(updateClock, 1000);
    }

    // Token Simulation Demo for Phase 1
    let tokenIndex = 108;
    const btnSimulate = document.getElementById("btnSimulateToken");
    const currentTokenDisplay = document.getElementById("currentTokenDisplay");
    const nextTokenDisplay = document.getElementById("nextTokenDisplay");
    const simAlert = document.getElementById("simAlert");

    if (btnSimulate && currentTokenDisplay) {
        btnSimulate.addEventListener("click", () => {
            tokenIndex++;
            const currentToken = `A-${tokenIndex}`;
            const nextTokens = `A-${tokenIndex + 1}, A-${tokenIndex + 2}`;

            currentTokenDisplay.textContent = currentToken;
            if (nextTokenDisplay) {
                nextTokenDisplay.textContent = nextTokens;
            }

            if (simAlert) {
                simAlert.textContent = `Token announcement sounded: Calling Token ${currentToken} to Consultation Room 104.`;
                simAlert.style.display = "block";
                setTimeout(() => {
                    simAlert.style.display = "none";
                }, 4000);
            }
        });
    }
});

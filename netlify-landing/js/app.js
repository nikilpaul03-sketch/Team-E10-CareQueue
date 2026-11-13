
document.addEventListener("DOMContentLoaded", () => {
    console.log("CareQueue E10 frontend initialized");

    // Live clock
    const clock = document.getElementById("liveClock");

    function updateClock() {
        if (clock) {
            clock.textContent = new Date().toLocaleString("en-IN", {
                dateStyle: "medium",
                timeStyle: "medium"
            });
        }
    }

    updateClock();
    setInterval(updateClock, 1000);

    // Demo token simulator
    let tokenIndex = 108;

    const button = document.getElementById("btnSimulateToken");
    const current = document.getElementById("currentTokenDisplay");
    const next = document.getElementById("nextTokenDisplay");
    const alertBox = document.getElementById("simAlert");

    if (button && current) {
        button.addEventListener("click", () => {
            tokenIndex++;

            current.textContent = `A-${tokenIndex}`;

            if (next) {
                next.textContent =
                    `A-${tokenIndex + 1}, A-${tokenIndex + 2}`;
            }

            if (alertBox) {
                alertBox.textContent =
                    `Demo: Calling token A-${tokenIndex}`;
                alertBox.style.display = "block";
            }
        });
    }
});

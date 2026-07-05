const analyzeButton = document.getElementById("analyzeButton");
const outputSection = document.getElementById("outputSection");
const navLinks = document.querySelectorAll(".navlinks a");

const ding = new Audio("Windows_10_ding.mp3");
ding.volume = .05;
analyzeButton.addEventListener("click", () => {
    outputSection.style.display = "flex";
    ding.currentTime = 0;
    ding.play();
    outputSection.scrollIntoView({ behavior: "smooth" });

    const code = document.getElementById("inputArea").value;
    fetch("https://time-complexity-analyzer-kaos.onrender.com/api/analyze", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            code: code
        })
    })
        .then(res => res.json())
        .then(data => {
            document.getElementById("timeArea").value = "Time Complexity: " + data.complexity;
            document.getElementById("spaceArea").value = "Depth: " + data.depth;
            document.getElementById("comment").innerText = "Analysis complete.";
        })
        .catch(err => console.log(err));
});
navLinks.forEach(link => {
    link.addEventListener("click", () => {
        ding.play();
    });
});


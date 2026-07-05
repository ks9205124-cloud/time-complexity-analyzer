function escapeHtml(str) {
    const div = document.createElement("div");
    div.innerText = str;
    return div.innerHTML;
}

function loadHistory() {
    fetch("https://time-complexity-analyzer-kaos.onrender.com/api/submissions")
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById("historyBody");
            const emptyState = document.getElementById("emptyState");
            if (!tbody) return;

            if (!data || data.length === 0) {
                if (emptyState) emptyState.style.display = "block";
                return;
            }

            let rows = "";
            data.slice().reverse().forEach(sub => {
                rows += `<tr>
                    <td>${sub.id}</td>
                    <td class="code-cell">${escapeHtml(sub.userString)}</td>
                    <td class="complexity-cell">${sub.complexity}</td>
                    <td>${sub.depth}</td>
                </tr>`;
            });

            tbody.innerHTML = rows;
        })
        .catch(err => console.error("Error fetching history:", err));
}

document.addEventListener("DOMContentLoaded", () => {
    loadHistory();
});
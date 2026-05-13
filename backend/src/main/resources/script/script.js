document.addEventListener("DOMContentLoaded", () => {

    const buttons = document.querySelectorAll(".openModal");

    buttons.forEach(button => {
        button.addEventListener("click", () => {

            const modalId = button.getAttribute("data-modal");
            const modal = document.getElementById(modalId);

            if (!modal) return;

            modal.style.display = "block";

            const closeBtn = modal.querySelector(".close");

            closeBtn.onclick = () => {
                modal.style.display = "none";
            };

        });
    });

    window.addEventListener("click", (event) => {
        document.querySelectorAll(".modal").forEach(modal => {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });
    });

});
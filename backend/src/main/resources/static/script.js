document.addEventListener("DOMContentLoaded", () => {

    // modal: home caderno perfil

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

    // sidebar

    const btnSidebar = document.getElementById("btnSidebar");
    const sidebar = document.getElementById("aside");
    const secao = document.getElementsByTagName("section")[0];
    const logoImg = document.getElementsByClassName("imagemLogo")[0];

    btnSidebar.addEventListener("click", () => {

        if (sidebar.classList.contains("colapsed")) {

            sidebar.classList.remove("colapsed");
            secao.classList.remove("fullOpen");
            logoImg.src = "../img/logo.png";

        } else {

            sidebar.classList.add("colapsed");
            secao.classList.add("fullOpen");

            logoImg.src = "../img/NoteLabIcone.png";

        }

    });

    const btnDarkMode = document.getElementById("btnDarkmode");
    const main = document.querySelector("main");
    const header = document.querySelector("header");
    const sunDarkmode = document.getElementById("sunDarkmode");

    btnDarkMode.addEventListener("click", () => {
        main.classList.add("darkMode");
        header.classList.add("darkHeader");

            sunDarkmode.style.display = "block";
            btnDarkMode.style.display = "none";

    })

    sunDarkmode.addEventListener("click", () => {
            main.classList.toggle("darkMode");
            header.classList.toggle("darkHeader");

            sunDarkmode.style.display = "none";
            btnDarkMode.style.display = "block";
        });

});
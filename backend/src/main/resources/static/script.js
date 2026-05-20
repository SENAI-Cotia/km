document.addEventListener("DOMContentLoaded", () => {
<<<<<<< HEAD

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
=======
    const body = document.body;
    const aside = document.getElementById("aside");
    const mainSection = document.querySelector(".fullOpen");
    const sidebarButton = document.getElementById("btnSidebar");
    const darkModeButton = document.getElementById("btnDarkmode");
    const lightModeButton = document.getElementById("sunDarkmode");
    const modalTriggers = document.querySelectorAll(".openModal[data-modal]");
    const modals = document.querySelectorAll(".modal");

    const storage = {
        get(key) {
            try {
                return localStorage.getItem(key);
            } catch (error) {
                return null;
            }
        },
        set(key, value) {
            try {
                localStorage.setItem(key, value);
            } catch (error) {
                return;
            }
        }
    };

    const setSidebarState = (state) => {
        const isClosed = state === "closed";

        if (aside) {
            aside.classList.toggle("closed", isClosed);
        }

        if (mainSection) {
            mainSection.classList.toggle("expanded", isClosed);
        }

        storage.set("sidebarState", isClosed ? "closed" : "open");
    };

    const toggleSidebar = () => {
        const isClosed = aside ? aside.classList.contains("closed") : false;
        setSidebarState(isClosed ? "open" : "closed");
    };

    const setTheme = (theme) => {
        const isDark = theme === "dark";

        body.classList.toggle("dark-mode", isDark);
        storage.set("theme", isDark ? "dark" : "light");
    };

    const toggleTheme = () => {
        const isDark = body.classList.contains("dark-mode");
        setTheme(isDark ? "light" : "dark");
    };

    const closeModal = (modal) => {
        if (!modal) {
            return;
        }

        modal.classList.remove("is-open");
    };

    const openModal = (modalId) => {
        if (!modalId) {
            return;
        }

        const modal = document.getElementById(modalId);

        if (!modal || !modal.classList.contains("modal")) {
            return;
        }

        modal.classList.add("is-open");
    };

    const savedSidebarState = storage.get("sidebarState");
    const savedTheme = storage.get("theme");

    setSidebarState(savedSidebarState === "closed" ? "closed" : "open");
    setTheme(savedTheme === "dark" ? "dark" : "light");

    if (sidebarButton) {
        sidebarButton.addEventListener("click", toggleSidebar);
    }

    [darkModeButton, lightModeButton].forEach((button) => {
        if (!button) {
            return;
        }

        button.addEventListener("click", toggleTheme);
    });

    modalTriggers.forEach((trigger) => {
        trigger.addEventListener("click", (event) => {
            event.preventDefault();
            openModal(trigger.dataset.modal);
        });
    });

    modals.forEach((modal) => {
        const closeButtons = modal.querySelectorAll(".close");

        closeButtons.forEach((button) => {
            button.addEventListener("click", () => closeModal(modal));
        });

        modal.addEventListener("click", (event) => {
            if (event.target === modal) {
                closeModal(modal);
>>>>>>> 213a3de8754a84072ff2845fdb6b137b1cebb992
            }
        });
    });

<<<<<<< HEAD
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
=======
    document.addEventListener("click", (event) => {
        const clickedTrigger = event.target.closest(".openModal[data-modal]");
        const clickedModal = event.target.closest(".modal");

        if (clickedTrigger || clickedModal) {
            return;
        }

        document.querySelectorAll(".modal.is-open").forEach(closeModal);
    });

    document.addEventListener("keydown", (event) => {
        if (event.key !== "Escape") {
            return;
        }

        modals.forEach(closeModal);
    });
});
>>>>>>> 213a3de8754a84072ff2845fdb6b137b1cebb992

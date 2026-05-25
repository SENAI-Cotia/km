document.addEventListener("DOMContentLoaded", () => {
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
            }
        });
    });

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

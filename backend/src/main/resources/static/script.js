document.addEventListener("DOMContentLoaded", () => {
    const body = document.body;
    const aside = document.getElementById("aside");
    const mainSection = document.querySelector(".fullOpen");
    const sidebarButton = document.getElementById("btnSidebar");
    const darkModeButton = document.getElementById("btnDarkmode");
    const lightModeButton = document.getElementById("sunDarkmode");
    const modalTriggers = document.querySelectorAll(".openModal[data-modal]");
    const modals = document.querySelectorAll(".modal");
    const matterInput = document.getElementById("nomeMateria");
    const matterButton = document.querySelector(".btnCriarMateria");
    const matterList = document.getElementById("listaMaterias");
    const cadernoNameInput = document.getElementById("nomeCaderno");
    const cadernoDescriptionInput = document.getElementById("descricaoCaderno");
    const cadernoMatterSelect = document.getElementById("materias");
    const cadernoButton = document.querySelector(".btnCriarCaderno");
    const cadernoList = document.getElementById("listaCadernos");
    const emptyCadernoState = document.querySelector(".ContainerCriarNota");
    const searchInput = document.getElementById("busca");
    const colorButtons = document.querySelectorAll(".containerCores .cores[data-color]");
    const cadernoId = document.body.dataset.cadernoId;
    const noteList = document.getElementById("listaNotas");
    const newNoteButton = document.getElementById("btnNovaNota");
    const noteTitleInput = document.getElementById("noteTitle");
    const noteEditor = document.getElementById("noteEditor");
    const noteEditDate = document.getElementById("noteEditDate");
    const autosaveStatus = document.getElementById("autosaveStatus");
    const emptyNotesEditorState = document.getElementById("emptyNotesState");
    const editorToolbar = document.querySelector(".editorToolbar");
    const notesSidebar = document.getElementById("notesSidebar");
    const notesSidebarToggle = document.getElementById("notesSidebarToggle");
    const notesSidebarOverlay = document.getElementById("notesSidebarOverlay");

    let matters = [];
    let cadernos = [];
    let notes = [];
    let selectedMatterId = null;
    let selectedMatterColor = "#F43545";
    let activeNote = null;
    let saveTimer = null;
    let isHydratingNote = false;

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

    const getCsrfHeaders = () => {
        const token = document.querySelector('meta[name="_csrf"]')?.content;
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;

        return token && header ? { [header]: token } : {};
    };

    const apiFetch = async (url, options = {}) => {
        const response = await fetch(url, {
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json",
                ...getCsrfHeaders(),
                ...(options.headers || {})
            },
            ...options
        });

        const contentType = response.headers.get("content-type") || "";
        const data = contentType.includes("application/json") ? await response.json() : null;

        if (!response.ok) {
            throw new Error(data?.error || "Nao foi possivel concluir a operacao");
        }

        return data;
    };

    const setFeedback = (type, message = "", isError = false) => {
        const feedback = document.querySelector(`[data-feedback="${type}"]`);
        if (!feedback) {
            return;
        }

        feedback.textContent = message;
        feedback.classList.toggle("is-error", isError);
        feedback.classList.toggle("is-success", Boolean(message) && !isError);
    };

    const setLoading = (button, loading, text) => {
        if (!button) {
            return;
        }

        if (loading) {
            button.dataset.originalText = button.textContent;
            button.textContent = text;
            button.disabled = true;
            return;
        }

        button.textContent = button.dataset.originalText || button.textContent;
        button.disabled = false;
    };

    const escapeHtml = (value) => String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");

    const getMatterName = (matterId) => {
        const matter = matters.find((item) => String(item.id) === String(matterId));
        return matter?.name || "Materia";
    };

    const getMatterColor = (matterId) => {
        const matter = matters.find((item) => String(item.id) === String(matterId));
        return matter?.color || "#F43545";
    };

    const formatDate = (date) => {
        if (!date) {
            return "Agora";
        }

        const parsed = new Date(`${date}T00:00:00`);
        if (Number.isNaN(parsed.getTime())) {
            return date;
        }

        return parsed.toLocaleDateString("pt-BR", {
            day: "2-digit",
            month: "short",
            year: "numeric"
        });
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

    const setNotesSidebarOpen = (open) => {
        if (!notesSidebar || !notesSidebarToggle) {
            return;
        }

        body.classList.toggle("notes-sidebar-open", open);
        notesSidebarToggle.setAttribute("aria-expanded", open ? "true" : "false");

        if (notesSidebarOverlay) {
            notesSidebarOverlay.hidden = !open;
        }
    };

    const toggleNotesSidebar = () => {
        setNotesSidebarOpen(!body.classList.contains("notes-sidebar-open"));
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
        if (modalId === "modalCaderno") {
            renderMatterOptions();
        }
    };

    const renderMatters = (items = matters) => {
        if (!matterList) {
            return;
        }

        if (!items.length) {
            matterList.innerHTML = "";
            return;
        }

        matterList.innerHTML = items.map((matter) => `
            <div class="cardMateria" data-matter-id="${matter.id}">
                <div class="infoCard">
                    <span class="corCardMateria" style="background-color: ${escapeHtml(matter.color || "#F43545")}"></span>
                    <p>${escapeHtml(matter.name)}</p>
                </div>
                <button class="deleteIconButton deleteMatterButton" type="button" data-matter-id="${matter.id}" title="Excluir matéria">×</button>
            </div>
        `).join("");
    };

    const renderMatterOptions = () => {
        if (!cadernoMatterSelect) {
            return;
        }

        if (!matters.length) {
            cadernoMatterSelect.innerHTML = '<option value="">Crie uma materia primeiro</option>';
            return;
        }

        cadernoMatterSelect.innerHTML = [
            '<option value="">Selecione uma materia</option>',
            ...matters.map((matter) => (
                `<option value="${matter.id}">${escapeHtml(matter.name)}</option>`
            ))
        ].join("");
    };

    const renderCadernos = (items = cadernos) => {
        if (!cadernoList || !emptyCadernoState) {
            return;
        }

        const hasCadernos = items.length > 0;
        emptyCadernoState.hidden = hasCadernos;
        cadernoList.hidden = !hasCadernos;

        if (!hasCadernos) {
            cadernoList.innerHTML = "";
            return;
        }

        cadernoList.innerHTML = items.map((caderno) => `
            <div class="cardCaderno" data-caderno-id="${caderno.id}">
                <button class="deleteIconButton deleteCadernoButton" type="button" data-caderno-id="${caderno.id}" title="Excluir caderno">×</button>
                <div class="tituloCaderno">
                    <span class="cadernoMatterColor" style="background-color: ${escapeHtml(getMatterColor(caderno.matterId))}"></span>
                    <div>
                        <h4>${escapeHtml(caderno.name)}</h4>
                        <h6>${escapeHtml(getMatterName(caderno.matterId))}</h6>
                    </div>
                </div>
                <p>${escapeHtml(caderno.description)}</p>
            </div>
        `).join("");
    };

    const createMatter = async () => {
        const name = matterInput?.value.trim();
        setFeedback("matter");

        if (!name) {
            setFeedback("matter", "Informe o nome da materia.", true);
            return;
        }

        try {
            setLoading(matterButton, true, "Criando...");
            await apiFetch("/matter/create", {
                method: "POST",
                body: JSON.stringify({ name, color: selectedMatterColor })
            });

            matterInput.value = "";
            selectedMatterColor = "#F43545";
            colorButtons.forEach((button) => {
                button.classList.toggle("is-selected", button.dataset.color === selectedMatterColor);
            });
            await loadMatters();
            closeModal(document.getElementById("modalMateria"));
            setFeedback("matter");
        } catch (error) {
            setFeedback("matter", error.message, true);
        } finally {
            setLoading(matterButton, false);
        }
    };

    const loadMatters = async () => {
        try {
            if (matterList) {
                matterList.innerHTML = '<p class="loadingText">Carregando...</p>';
            }

            matters = await apiFetch("/matter/user/me", { method: "GET" });
            renderMatters();
            renderMatterOptions();
            if (cadernos.length) {
                renderCadernos();
            }
        } catch (error) {
            if (matterList) {
                matterList.innerHTML = `<p class="formFeedback is-error">${escapeHtml(error.message)}</p>`;
            }
        }
    };

    const createCaderno = async () => {
        const name = cadernoNameInput?.value.trim();
        const description = cadernoDescriptionInput?.value.trim();
        const matterId = cadernoMatterSelect?.value;
        setFeedback("caderno");

        if (!name || !description || !matterId) {
            setFeedback("caderno", "Preencha nome, descricao e materia.", true);
            return;
        }

        try {
            setLoading(cadernoButton, true, "Criando...");
            await apiFetch("/caderno/create", {
                method: "POST",
                body: JSON.stringify({ name, description, matterId: Number(matterId) })
            });

            selectedMatterId = matterId;
            cadernoNameInput.value = "";
            cadernoDescriptionInput.value = "";
            cadernoMatterSelect.value = "";
            await loadCadernos(selectedMatterId);
            closeModal(document.getElementById("modalCaderno"));
            setFeedback("caderno");
        } catch (error) {
            setFeedback("caderno", error.message, true);
        } finally {
            setLoading(cadernoButton, false);
        }
    };

    const loadCadernos = async (matterId = null) => {
        try {
            cadernos = await apiFetch(
                matterId ? `/caderno/matter/${matterId}` : "/caderno/user/me",
                { method: "GET" }
            );
            renderCadernos();
        } catch (error) {
            if (cadernoList) {
                cadernoList.hidden = false;
                cadernoList.innerHTML = `<p class="formFeedback is-error">${escapeHtml(error.message)}</p>`;
            }
            if (emptyCadernoState) {
                emptyCadernoState.hidden = true;
            }
        }
    };

    const deleteMatter = async (matterId) => {
        if (!matterId) {
            return;
        }

        const confirmed = window.confirm("Tem certeza que deseja excluir esta matéria? Todos os cadernos e notas serão removidos.");
        if (!confirmed) {
            return;
        }

        try {
            await apiFetch(`/matter/${matterId}`, { method: "DELETE" });
            matters = matters.filter((matter) => String(matter.id) !== String(matterId));
            cadernos = cadernos.filter((caderno) => String(caderno.matterId) !== String(matterId));
            renderMatters();
            renderMatterOptions();
            renderCadernos();

            if (window.location.pathname === `/matter/${matterId}`) {
                window.location.href = "/home";
            }
        } catch (error) {
            window.alert(error.message);
        }
    };

    const deleteCaderno = async (cadernoId) => {
        if (!cadernoId) {
            return;
        }

        const confirmed = window.confirm("Tem certeza que deseja excluir este caderno? Todas as notas dele serão removidas.");
        if (!confirmed) {
            return;
        }

        try {
            await apiFetch(`/caderno/${cadernoId}`, { method: "DELETE" });
            cadernos = cadernos.filter((caderno) => String(caderno.id) !== String(cadernoId));
            document.querySelectorAll(".cardCaderno[data-caderno-id]").forEach((card) => {
                if (String(card.dataset.cadernoId) === String(cadernoId)) {
                    card.remove();
                }
            });
            renderCadernos();

            if (String(document.body.dataset.cadernoId || "") === String(cadernoId)) {
                window.location.href = "/home";
            }
        } catch (error) {
            window.alert(error.message);
        }
    };

    const deleteNote = async (noteId) => {
        if (!noteId) {
            return;
        }

        const confirmed = window.confirm("Tem certeza que deseja excluir esta nota?");
        if (!confirmed) {
            return;
        }

        try {
            await apiFetch(`/note/${noteId}`, { method: "DELETE" });
            notes = notes.filter((note) => String(note.id) !== String(noteId));
            if (activeNote && String(activeNote.id) === String(noteId)) {
                activeNote = null;
                if (notes.length) {
                    openNote(notes[0]);
                } else {
                    if (noteTitleInput) {
                        noteTitleInput.value = "Nota sem título";
                    }
                    if (noteEditor) {
                        noteEditor.innerHTML = "";
                    }
                    if (noteEditDate) {
                        noteEditDate.textContent = "";
                    }
                    setAutosaveStatus("Selecione ou crie uma nota");
                    showEmptyNoteState(true);
                }
            }
            renderNotes();
        } catch (error) {
            window.alert(error.message);
        }
    };

    const setEditorEnabled = (enabled) => {
        if (noteTitleInput) {
            noteTitleInput.disabled = !enabled;
        }
        if (noteEditor) {
            noteEditor.contentEditable = enabled ? "true" : "false";
        }
        if (editorToolbar) {
            editorToolbar.querySelectorAll("button").forEach((button) => {
                button.disabled = !enabled;
            });
        }
    };

    const setAutosaveStatus = (message) => {
        if (autosaveStatus) {
            autosaveStatus.textContent = message;
        }
    };

    const renderNotes = () => {
        if (!noteList) {
            return;
        }

        if (!notes.length) {
            noteList.innerHTML = '<p class="loadingText">Nenhuma nota criada.</p>';
            return;
        }

        noteList.innerHTML = notes.map((note) => `
            <button class="nomeNota ${activeNote?.id === note.id ? "is-active" : ""}" type="button" data-note-id="${note.id}">
                <span>
                    <h4>${escapeHtml(note.name)}</h4>
                    <p>Editado ${escapeHtml(formatDate(note.noteEditDate))}</p>
                </span>
                <span class="deleteNoteButton" data-note-id="${note.id}" title="Excluir nota">×</span>
            </button>
        `).join("");
    };

    const showEmptyNoteState = (show) => {
        if (emptyNotesEditorState) {
            emptyNotesEditorState.hidden = !show;
        }
        if (noteEditor) {
            noteEditor.hidden = show;
        }
        if (editorToolbar) {
            editorToolbar.hidden = show;
        }
        if (noteEditDate) {
            noteEditDate.hidden = show;
        }
        setEditorEnabled(!show);
    };

    const openNote = (note) => {
        if (!note || !noteTitleInput || !noteEditor) {
            return;
        }

        isHydratingNote = true;
        activeNote = note;
        noteTitleInput.value = note.name || "Nota sem título";
        noteEditor.innerHTML = note.noteContent || "";
        if (noteEditDate) {
            noteEditDate.textContent = `Editado ${formatDate(note.noteEditDate)}`;
        }
        showEmptyNoteState(false);
        setAutosaveStatus("Salvo");
        renderNotes();
        if (window.matchMedia("(max-width: 768px)").matches) {
            setNotesSidebarOpen(false);
        }
        isHydratingNote = false;
    };

    const loadNotes = async () => {
        if (!cadernoId || !noteList) {
            return;
        }

        try {
            noteList.innerHTML = '<p class="loadingText">Carregando notas...</p>';
            notes = await apiFetch(`/note/caderno/${cadernoId}`, { method: "GET" });
            renderNotes();
            if (notes.length) {
                openNote(notes[0]);
            } else {
                activeNote = null;
                showEmptyNoteState(true);
            }
        } catch (error) {
            noteList.innerHTML = `<p class="formFeedback is-error">${escapeHtml(error.message)}</p>`;
            showEmptyNoteState(true);
        }
    };

    const createNote = async () => {
        if (!cadernoId) {
            return;
        }

        try {
            setLoading(newNoteButton, true, "Criando...");
            const note = await apiFetch("/note/create", {
                method: "POST",
                body: JSON.stringify({
                    idCaderno: Number(cadernoId),
                    name: "Nota sem título",
                    noteContent: ""
                })
            });

            notes = [note, ...notes.filter((item) => item.id !== note.id)];
            openNote(note);
        } catch (error) {
            setAutosaveStatus(error.message);
        } finally {
            setLoading(newNoteButton, false);
        }
    };

    const saveActiveNote = async () => {
        if (!activeNote || !noteTitleInput || !noteEditor) {
            return;
        }

        const name = noteTitleInput.value.trim() || "Nota sem título";
        const noteContent = noteEditor.innerHTML;

        try {
            setAutosaveStatus("Salvando...");
            const updated = await apiFetch(`/note/${activeNote.id}`, {
                method: "PUT",
                body: JSON.stringify({ name, noteContent })
            });

            activeNote = updated;
            notes = notes.map((note) => note.id === updated.id ? updated : note);
            if (noteTitleInput.value.trim() !== updated.name) {
                noteTitleInput.value = updated.name;
            }
            if (noteEditDate) {
                noteEditDate.textContent = `Editado ${formatDate(updated.noteEditDate)}`;
            }
            renderNotes();
            setAutosaveStatus("Salvo");
        } catch (error) {
            setAutosaveStatus("Erro ao salvar");
        }
    };

    const scheduleAutosave = () => {
        if (isHydratingNote || !activeNote) {
            return;
        }

        window.clearTimeout(saveTimer);
        setAutosaveStatus("Alterações pendentes");
        saveTimer = window.setTimeout(saveActiveNote, 1200);
    };

    window.createMatter = createMatter;
    window.loadMatters = loadMatters;
    window.createCaderno = createCaderno;
    window.loadCadernos = loadCadernos;
    window.loadNotes = loadNotes;

    const savedSidebarState = storage.get("sidebarState");
    const savedTheme = storage.get("theme");

    setSidebarState(savedSidebarState === "closed" ? "closed" : "open");
    setTheme(savedTheme === "dark" ? "dark" : "light");

    if (sidebarButton) {
        sidebarButton.addEventListener("click", toggleSidebar);
    }

    if (notesSidebarToggle) {
        notesSidebarToggle.addEventListener("click", toggleNotesSidebar);
    }

    if (notesSidebarOverlay) {
        notesSidebarOverlay.addEventListener("click", () => setNotesSidebarOpen(false));
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

    if (matterButton) {
        matterButton.addEventListener("click", createMatter);
    }

    if (matterInput) {
        matterInput.addEventListener("keydown", (event) => {
            if (event.key === "Enter") {
                createMatter();
            }
        });
    }

    if (cadernoButton) {
        cadernoButton.addEventListener("click", createCaderno);
    }

    if (newNoteButton) {
        newNoteButton.addEventListener("click", createNote);
    }

    if (noteList) {
        noteList.addEventListener("click", (event) => {
            if (event.target.closest(".deleteNoteButton[data-note-id]")) {
                return;
            }

            const noteButton = event.target.closest(".nomeNota[data-note-id]");
            if (!noteButton) {
                return;
            }

            const note = notes.find((item) => String(item.id) === noteButton.dataset.noteId);
            openNote(note);
        });
    }

    if (noteTitleInput) {
        noteTitleInput.addEventListener("input", scheduleAutosave);
    }

    if (noteEditor) {
        noteEditor.addEventListener("input", scheduleAutosave);
    }

    if (editorToolbar) {
        editorToolbar.addEventListener("click", (event) => {
            const button = event.target.closest("button[data-command]");
            if (!button || button.disabled) {
                return;
            }

            event.preventDefault();
            noteEditor?.focus();
            document.execCommand(button.dataset.command, false, button.dataset.value || null);
            scheduleAutosave();
        });
    }

    colorButtons.forEach((button) => {
        button.addEventListener("click", () => {
            selectedMatterColor = button.dataset.color || "#F43545";
            colorButtons.forEach((item) => {
                item.classList.toggle("is-selected", item === button);
            });
        });
    });

    if (matterList) {
        matterList.addEventListener("click", (event) => {
            const deleteButton = event.target.closest(".deleteMatterButton[data-matter-id]");
            if (deleteButton) {
                event.preventDefault();
                event.stopPropagation();
                deleteMatter(deleteButton.dataset.matterId);
                return;
            }

            const card = event.target.closest(".cardMateria[data-matter-id]");
            if (!card) {
                return;
            }

            window.location.href = `/matter/${card.dataset.matterId}`;
        });
    }

    if (cadernoList) {
        cadernoList.addEventListener("click", (event) => {
            const deleteButton = event.target.closest(".deleteCadernoButton[data-caderno-id]");
            if (deleteButton) {
                event.preventDefault();
                event.stopPropagation();
                deleteCaderno(deleteButton.dataset.cadernoId);
                return;
            }

            const card = event.target.closest(".cardCaderno[data-caderno-id]");
            if (!card) {
                return;
            }

            window.location.href = `/caderno/${card.dataset.cadernoId}`;
        });
    }

    if (searchInput) {
        searchInput.addEventListener("input", () => {
            const term = searchInput.value.trim().toLowerCase();
            const filtered = matters.filter((matter) => matter.name.toLowerCase().includes(term));
            renderMatters(filtered);
        });
    }

    document.addEventListener("click", (event) => {
        const staticMatterDelete = event.target.closest(".deleteMatterButton[data-matter-id]");
        if (staticMatterDelete && !matterList?.contains(staticMatterDelete)) {
            event.preventDefault();
            event.stopPropagation();
            deleteMatter(staticMatterDelete.dataset.matterId);
            return;
        }

        const staticCadernoDelete = event.target.closest(".deleteCadernoButton[data-caderno-id]");
        if (staticCadernoDelete && !cadernoList?.contains(staticCadernoDelete)) {
            event.preventDefault();
            event.stopPropagation();
            deleteCaderno(staticCadernoDelete.dataset.cadernoId);
            return;
        }

        const staticCadernoCard = event.target.closest(".cardCaderno[data-caderno-id]");
        if (staticCadernoCard && !cadernoList?.contains(staticCadernoCard)) {
            window.location.href = `/caderno/${staticCadernoCard.dataset.cadernoId}`;
            return;
        }

        const noteDelete = event.target.closest(".deleteNoteButton[data-note-id]");
        if (noteDelete) {
            event.preventDefault();
            event.stopPropagation();
            deleteNote(noteDelete.dataset.noteId);
            return;
        }

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

        setNotesSidebarOpen(false);
        modals.forEach(closeModal);
    });

    if (matterList || cadernoMatterSelect) {
        loadMatters();
    }

    if (cadernoList) {
        loadCadernos();
    }

    if (noteList) {
        loadNotes();
    }
});

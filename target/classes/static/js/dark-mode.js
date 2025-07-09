document.addEventListener("DOMContentLoaded", function () {
    // Flag to track if dark mode has been activated
    const darkModeActivated = localStorage.getItem("darkMode") === "enabled";

    // Apply dark mode immediately if previously enabled (no transitions applied initially)
    if (darkModeActivated) {
        document.body.classList.add("dark-mode");
    }

    // Toggle button event listener
    const toggleButton = document.getElementById("toggle-dark-mode");
    toggleButton.addEventListener("click", function () {
        // Toggle dark mode class and save state in localStorage
        document.body.classList.toggle("dark-mode");

        // Store dark mode state in localStorage
        if (document.body.classList.contains("dark-mode")) {
            localStorage.setItem("darkMode", "enabled");
        } else {
            localStorage.removeItem("darkMode");
        }
    });
});

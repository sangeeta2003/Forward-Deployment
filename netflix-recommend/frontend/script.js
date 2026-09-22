const API_BASE_URL = "http://localhost:8080";

const searchInput = document.getElementById("searchInput");
const searchButton = document.getElementById("searchButton");

const movieGrid = document.getElementById("movieGrid");

const similarGrid = document.getElementById("similarGrid");
const similarSection = document.getElementById("similarSection");

const sectionTitle = document.getElementById("sectionTitle");
const similarTitle = document.getElementById("similarTitle");

const loading = document.getElementById("loading");
const errorMessage = document.getElementById("errorMessage");


/* ================= SEARCH ================= */

searchButton.addEventListener("click", searchMovies);

searchInput.addEventListener("keydown", function (event) {

    if (event.key === "Enter") {
        searchMovies();
    }

});


async function searchMovies() {

    const query = searchInput.value.trim();

    if (!query) {
        showError("Please enter something to search.");
        return;
    }

    showLoading();

    try {

        const response = await fetch(
            `${API_BASE_URL}/movies/search?query=${encodeURIComponent(query)}`
        );

        if (!response.ok) {
            throw new Error("Search request failed.");
        }

        const movies = await response.json();

        sectionTitle.textContent = `Results for "${query}"`;

        displayMovies(movies);

        similarSection.classList.add("hidden");

        document
            .getElementById("recommendations")
            .scrollIntoView({
                behavior: "smooth"
            });

    } catch (error) {

        console.error(error);

        showError(
            "Could not connect to the recommendation server."
        );

    } finally {

        hideLoading();

    }

}


/* ================= DISPLAY MOVIES ================= */

function displayMovies(movies) {

    movieGrid.innerHTML = "";

    if (!movies || movies.length === 0) {

        movieGrid.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">😕</div>

                <h3>No movies found</h3>

                <p>
                    Try another search.
                </p>
            </div>
        `;

        return;
    }


    movies.forEach((movie, index) => {

        const card = createMovieCard(
            movie,
            index
        );

        movieGrid.appendChild(card);

    });

}


/* ================= MOVIE CARD ================= */

function createMovieCard(movie, index) {

    const card = document.createElement("div");

    card.className = "movie-card";


    const matchPercentage = Math.round(
        movie.match * 100
    );


    card.innerHTML = `

        <div class="movie-number">
            ${String(index + 1).padStart(2, "0")}
        </div>

        <h3 class="movie-title">
            ${escapeHtml(movie.title)}
        </h3>

        <p class="movie-description">
            ${escapeHtml(movie.description)}
        </p>

        <div class="movie-footer">

            <span class="match">
                ${matchPercentage}% Match
            </span>

            <button class="similar-button">
                Similar
            </button>

        </div>

    `;


    const similarButton =
        card.querySelector(".similar-button");


    similarButton.addEventListener(
        "click",
        function (event) {

            event.stopPropagation();

            loadSimilarMovies(movie.title);

        }
    );


    card.addEventListener(
        "click",
        function () {

            loadSimilarMovies(movie.title);

        }
    );


    return card;

}


/* ================= SIMILAR MOVIES ================= */

async function loadSimilarMovies(title) {

    showLoading();

    try {

        const response = await fetch(
            `${API_BASE_URL}/movies/${encodeURIComponent(title)}/similar`
        );

        if (!response.ok) {
            throw new Error(
                "Could not find similar movies."
            );
        }

        const movies = await response.json();

        similarTitle.textContent =
            `Movies Similar to ${title}`;

        similarGrid.innerHTML = "";

        movies.forEach((movie, index) => {

            const card = createMovieCard(
                movie,
                index
            );

            similarGrid.appendChild(card);

        });

        similarSection.classList.remove(
            "hidden"
        );


        similarSection.scrollIntoView({
            behavior: "smooth"
        });


    } catch (error) {

        console.error(error);

        showError(
            "Could not load similar movies."
        );

    } finally {

        hideLoading();

    }

}


/* ================= LOADING ================= */

function showLoading() {

    loading.classList.remove("hidden");

}

function hideLoading() {

    loading.classList.add("hidden");

}


/* ================= ERROR ================= */

function showError(message) {

    errorMessage.textContent = message;

    errorMessage.classList.remove(
        "hidden"
    );


    setTimeout(() => {

        errorMessage.classList.add(
            "hidden"
        );

    }, 4000);

}


/* ================= SECURITY ================= */

function escapeHtml(text) {

    const div = document.createElement("div");

    div.textContent = text;

    return div.innerHTML;

}
const API_URL = "http://localhost:8080/api";


// -----------------------------
// LOCATION SEARCH
// -----------------------------

function searchFood() {

    const location = document.getElementById("location").value;

    if (location.trim() === "") {
        alert("Please enter your location.");
        return;
    }

    alert("Finding food near " + location + " 🍅");
}


// -----------------------------
// CATEGORY
// -----------------------------

function selectCategory(category) {

    alert("Showing " + category + " restaurants 🍴");
}


// -----------------------------
// ORDER
// -----------------------------

function orderFood(restaurant) {

    alert(
        "You selected " +
        restaurant +
        ". Ordering feature coming soon! 🍅"
    );
}


// -----------------------------
// CHAT
// -----------------------------

async function sendMessage() {

    const input = document.getElementById("chatInput");

    const message = input.value.trim();

    if (message === "") {
        return;
    }


    // Show user message

    addMessage(message, "user");


    // Clear input

    input.value = "";


    try {

        const response = await fetch(
            `${API_URL}/chat`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "text/plain"
                },

                body: message
            }
        );


        if (!response.ok) {

            throw new Error(
                "Server error: " + response.status
            );

        }


        const data = await response.text();


        // Show AI response

        addMessage(data, "bot");


    } catch (error) {

        console.error(error);

        addMessage(
            "Sorry, I couldn't connect to the Tomato AI server. Make sure your Spring Boot application is running.",
            "bot"
        );

    }

}


// -----------------------------
// ADD MESSAGE
// -----------------------------

function addMessage(message, type) {

    const chatMessages =
        document.getElementById("chatMessages");


    const messageDiv =
        document.createElement("div");


    messageDiv.classList.add("message");


    if (type === "user") {

        messageDiv.classList.add("user-message");

    } else {

        messageDiv.classList.add("bot-message");

    }


    messageDiv.textContent = message;


    chatMessages.appendChild(messageDiv);


    // Automatically scroll to bottom

    chatMessages.scrollTop =
        chatMessages.scrollHeight;

}


// -----------------------------
// ENTER KEY
// -----------------------------

function handleEnter(event) {

    if (event.key === "Enter") {

        sendMessage();

    }

}


// -----------------------------
// CLEAR CHAT
// -----------------------------

async function clearChat() {

    try {

        await fetch(
            `${API_URL}`,
            {
                method: "DELETE"
            }
        );


        document.getElementById(
            "chatMessages"
        ).innerHTML = `

            <div class="message bot-message">

                Chat cleared! 👋
                How can I help you?

            </div>

        `;


    } catch (error) {

        console.error(error);

    }

}
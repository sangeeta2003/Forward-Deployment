# 🍅 Tomcato AI Food Support Chatbot

Tomcato is an AI-powered food delivery customer-support chatbot built using **Spring Boot, Spring AI, and Google Gemini**.

## 🚀 How It Works

```text
User
 ↓
Frontend (HTML + CSS + JavaScript)
 ↓
Spring Boot REST API
 ↓
ChatService
 ↓
Spring AI + Gemini
 ↓
AI Response
 ↓
Frontend
```

### Features

* 💬 AI-powered customer support
* 🧠 Maintains conversation history
* 📦 Handles food-order related queries
* 💰 Refund and order-tracking support
* 🛡️ System prompt controls chatbot behavior
* 🗑️ Clear conversation history

## 🛠️ Technologies

* Java
* Spring Boot
* Spring AI
* Google Gemini
* HTML
* CSS
* JavaScript
* Maven

## 📌 API Endpoints

### Chat

```http
POST /api/chat
```

Sends the user's message to Gemini and returns the AI response.

### Clear Chat

```http
DELETE /api
```

Clears the conversation history.

## 🔑 Configuration

Add your Gemini API key in:

```text
src/main/resources/application.properties
```

The API key should be kept private and should **not be committed to GitHub**.

## 👩‍💻 Author

**Sangeeta Mishra**

Java & AI Full-Stack Developer

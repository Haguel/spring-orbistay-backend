# 🌐 Orbistay Backend


## 🏡 About the Project

**Orbistay backend** is a robust and scalable backend for the Orbistay platform, built with **Spring Boot** and **Java 17**. It serves as the core API to manage property listings, bookings, and user interactions.

> 🏨 **Inspired by Booking.com**: This project is a functional clone of Booking.com with similar core features like property management and booking.

### 🚀 Key Features

- 🛏️ **Property Listings**: Manage properties for rent.
- 🗓️ **Booking System**: Secure booking and reservation handling.
- 👤 **User Management**: Modify & manage user data.
- 🔐 **Authentication**: Two types - Email/Password and Google OAuth 2.0.
- ❤️ **Favorites**: Add and manage favorite hotels.
- 📝 **Reviews**: Leave and view hotel reviews.
- 📡 **API Documentation**: Swagger integration for easy endpoint testing.
- 🛠️ **Docker Integration**: Run the application effortlessly with Docker.
- 🔗 **Frontend Integration**: Works seamlessly with the **[Angular Orbistay Frontend](https://github.com/moseeeu/angular-orbistay-frontend)**.

---

## ⚙️ Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security (JWT)**
- **Spring Data JPA**
- **PostgreSQL**
- **Redis**
- **Docker**
- **Azure Services**
- **Swagger UI**

---

## 🛠️ Installation & Setup

### 1️⃣ Prerequisites

- [Docker](https://www.docker.com/)

### 2️⃣ Clone the Repository

```bash
git clone https://github.com/Haguel/spring-orbistay-backend.git
cd spring-orbistay-backend
```

### 3️⃣ Configure Environment Variables

Open `docker-compose.yml` file in the root directory and fill in the required fields:

```bash
AZURE_BLOB-STORAGE_CONNECTION-STRING: FILL_THIS
SPRING_CLOUD_AZURE_STORAGE_BLOB_ACCOUNT_NAME: FILL_THIS
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID: FILL_THIS
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET: FILL_THIS
FRONTEND_HOST: FILL_THIS
FRONTEND_HOST_ADDITIONAL: FILL_THIS
SPRING_MAIL_HOST: FILL_THIS
SPRING_MAIL_PORT: FILL_THIS
SPRING_MAIL_USERNAME: FILL_THIS
SPRING_MAIL_PASSWORD: FILL_THIS
```

### 4️⃣ Run with Docker

1. Open `src/main/resources/application.properties`.
2. Set the Spring profile to `docker`.
3. Run the following command:

```bash
docker-compose up -d
```

---


## 📖 API Documentation

Access Swagger UI for testing APIs:

- **Local URL:** `http://localhost:8080/swagger-ui.html`
- **Production URL:** `https://orbistay-frontend-f3dhcscsacgug5gt.northeurope-01.azurewebsites.net/swagger-ui.html`

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---

🎯 **Happy Coding!** 😊


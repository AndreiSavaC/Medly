# Medly

Medly is a mobile application designed to streamline interactions between patients and family doctors through digital technologies. It simplifies appointment scheduling, enhances communication, and incorporates AI-powered features to provide a personalized healthcare experience.

## Features

- **Easy Appointment Scheduling:** Quickly book appointments with family doctors, eliminating traditional administrative barriers.
- **AI Virtual Assistant:** Assess symptoms before consultations with the help of an integrated AI assistant.
- **Centralized Dashboard for Doctors:** View all appointments in one place and access detailed reports based on patient interactions.
- **Secure Authentication:** Utilizes Keycloak for managing user identities and secure access.
- **Scalable Architecture:** Built with modular components to ensure easy maintenance and scalability.

## Architecture

Medly follows a modular architecture comprising the following components:

- **Android Application:** 
  - Developed in Kotlin.
  - Serves as the user interface for patients and doctors.
  - Handles authentication, appointment scheduling, and AI interactions.

- **Backend Services:**
  - **Ktor API:** 
    - Built with Kotlin.
    - Manages business logic and interacts with the PostgreSQL database.
  - **Flask API:** 
    - Built with Python.
    - Handles AI functionalities using GROQ and Llama 3.2 for chat and report generation.

- **Database:** 
  - **PostgreSQL:** 
    - Stores user data, appointments, and medical history.

- **Authentication:** 
  - **Keycloak:** 
    - Manages user authentication and authorization.

- **Containerization:** 
  - **Docker & Docker Compose:** 
    - Containerize all components for consistent deployment across different environments.

## Technologies Used

- **Frontend:**
  - **Kotlin**
  - **Android Studio**

- **Backend:**
  - **Ktor (Kotlin)**
  - **Flask (Python)**

- **Database:**
  - **PostgreSQL**

- **Authentication & Security:**
  - **Keycloak**

- **AI & Processing:**
  - **GROQ + Llama 3.2**

- **Containerization & Deployment:**
  - **Docker**
  - **Docker Compose**

- **Development Tools:**
  - **IntelliJ IDEA**
  - **VSCode**
  - **Postman**

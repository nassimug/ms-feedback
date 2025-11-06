# 🤖 ms-feedback - Microservice de Gestion des Feedbacks

## 📖 Vue d'ensemble

Le **microservice Feedback** gère les retours utilisateurs sur les recettes de l'application **SmartDish**. Il communique avec le microservice Persistance pour stocker et récupérer les données.

### Responsabilités

- 📝 Gestion des feedbacks utilisateurs
- ⭐ Système de notation (1 à 5 étoiles)
- 📊 Calcul des notes moyennes par recette
- 🤖 Envoi des données au service de recommandation RL

## 🏗️ Architecture

```
┌─────────────────┐      HTTP REST      ┌────────────────────┐
│   ms-feedback   │ ──────────────────> │  ms-persistance    │
│   (Port 8091)   │                     │   (Port 8090)      │
└─────────────────┘                     └─────────┬──────────┘
                                                  │
                                                  ▼
                                        ┌────────────────────┐
                                        │   MySQL Database   │
                                        └────────────────────┘
```

### Stack Technologique

- **Framework** : Spring Boot 3.5.6
- **Langage** : Java 21
- **Base de données** : MySQL (via ms-persistance)
- **Build** : Maven 3.8+
- **Documentation** : Swagger/OpenAPI

## 🚀 Installation

### Prérequis

- Java 21+
- Maven 3.8+
- ms-persistance démarré (**obligatoire**)

### Démarrage

#### 1. Cloner le projet

```bash
git clone https://github.com/nassimug/ms-feedback.git
cd ms-feedback
```

#### 2. Configurer l'environnement

Récupérer le fichier `.env` auprès de l'administrateur et le placer à la racine du projet.

#### 3. Démarrer ms-persistance

⚠️ **IMPORTANT** : Démarrer ms-persistance en premier !

```bash
cd ../ms-persistance
mvn spring-boot:run
```

#### 4. Compiler et lancer

```bash
cd ../ms-feedback
mvn clean install
mvn spring-boot:run
```


## 🔗 Accès aux services

| Service | URL |
|---------|-----|
| **Swagger UI** | http://localhost:8091/swagger-ui.html |
| **Health Check** | http://localhost:8091/api/feedbacks/health |

## 📡 API Endpoints

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/feedbacks` | Créer un feedback |
| `GET` | `/api/feedbacks` | Lister tous les feedbacks |
| `GET` | `/api/feedbacks/{id}` | Obtenir un feedback |
| `GET` | `/api/feedbacks/user/{userId}` | Feedbacks d'un utilisateur |
| `GET` | `/api/feedbacks/recette/{recetteId}` | Feedbacks d'une recette |
| `GET` | `/api/feedbacks/recette/{recetteId}/average` | Note moyenne d'une recette |
| `PUT` | `/api/feedbacks/{id}` | Mettre à jour un feedback |
| `DELETE` | `/api/feedbacks/{id}` | Supprimer un feedback |


## 🗂️ Structure du projet

```
ms-feedback/
├── src/main/java/.../
│   ├── client/
│   │   └── PersistanceClient.java      • Communication HTTP avec ms-persistance
│   ├── config/
│   ├── controller/
│   │   └── FeedbackController.java
│   ├── dto/
│   ├── exception/
│   ├── model/
│   │   └── Feedback.java               
│   └── service/
│       └── FeedbackServiceImpl.java    • Utilise PersistanceClient
├── .env                                 # Fourni par l'admin (non versionné)
└── pom.xml
```



### Validation

- **utilisateurId** : Obligatoire, doit exister
- **recetteId** : Obligatoire, doit exister
- **evaluation** : Obligatoire, entre 1 et 5
- **commentaire** : Optionnel, max 1000 caractères
- **Règle** : Un utilisateur ne peut noter qu'une fois une recette


## 🚀 Build production

```bash
# Créer le JAR
mvn clean package -DskipTests

# Lancer
java -jar target/ms-feedback-1.0.0.jar
```

## 📚 Ressources

- [Documentation Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Swagger/OpenAPI](https://swagger.io/docs/)
- [Documentation ms-persistance](../ms-persistance/README.md)

---

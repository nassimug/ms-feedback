# 🤖 ms-feedback - Microservice de Gestion des Feedbacks

## 📖 Vue d'ensemble

Le **microservice Feedback** est un composant essentiel de l'application **SmartDish**. Il gère les retours utilisateurs sur les recettes et alimente le moteur de recommandation intelligent basé sur l'apprentissage par renforcement (RL).

### Responsabilités principales

- 📝 **Gestion des feedbacks** - Création, lecture, mise à jour et suppression des retours utilisateurs
- ⭐ **Système de notation** - Notes de 1 à 5 étoiles avec commentaires optionnels
- 📊 **Statistiques** - Calcul de notes moyennes et agrégation des retours par recette
- 🤖 **Intégration IA** - Envoi des données au service de recommandation RL
- 📈 **Analyse des tendances** - Suivi de l'évolution des préférences utilisateurs

## 🏗️ Architecture Technique

### Stack Technologique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| **Framework** | Spring Boot | 3.5.6 |
| **Langage** | Java | 21 |
| **Base de données** | MongoDB | 7.0 |
| **Build Tool** | Maven | 3.8+ |
| **Documentation API** | Swagger/OpenAPI | 2.8.4 |
| **Tests** | JUnit 5 + Mockito | - |

### Choix de MongoDB

MongoDB a été choisi pour ce microservice car :

- ✅ **Flexibilité du schéma** - Les feedbacks peuvent évoluer sans migration complexe
- ✅ **Performance en lecture** - Agrégations rapides pour les statistiques
- ✅ **Scalabilité horizontale** - Sharding facile pour gérer la croissance
- ✅ **Intégration ML** - Format JSON natif pour le pipeline de Machine Learning
- ✅ **Requêtes complexes** - Framework d'agrégation puissant pour les analyses

## 🚀 Démarrage Rapide

### Prérequis

- ☕ Java 21+ (JDK Eclipse Adoptium recommandé)
- 📦 Maven 3.8+
- 🐳 Docker & Docker Compose
- 🔧 Git

### Installation

#### 1. Cloner le repository

```bash
git clone https://github.com/nassimug/ms-feedback.git
cd ms-feedback
```

#### 2. Récupérer le fichier .env

Le fichier `.env` contenant les configurations sensibles sera fourni par l'administrateur projet.

```bash
# Placer le fichier .env reçu à la racine du projet
# Structure attendue :
ms-feedback/
├── .env                 # ← Fichier fourni par l'admin
├── pom.xml
├── docker-compose.yml
└── src/
```

#### 3. Démarrer l'infrastructure Docker

```bash
# Démarrer MongoDB et Mongo Express
docker-compose up -d

# Vérifier que les services sont en ligne
docker-compose ps
```

#### 4. Compiler et lancer l'application

```bash
# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

#### 5. Vérifier le démarrage

```bash
# Health check
curl http://localhost:8091/api/feedbacks/health

# Réponse attendue :
# ✅ Microservice Feedback is healthy
```

## 🔗 Accès aux Services

| Service | URL                                   | Credentials | Description |
|---------|---------------------------------------|-------------|-------------|
| **Swagger UI** | http://localhost:8090/swagger-ui.html | - | Documentation interactive |
| **Mongo Express** | http://localhost:8081                 | admin / admin | Interface MongoDB |
## 📡 Endpoints API

### Feedbacks

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
| `POST` | `/api/feedbacks/send-to-recommendation` | Envoyer au service RL |
| `GET` | `/api/feedbacks/health` | Health check |

### Exemples d'utilisation

#### Créer un feedback

```bash
curl -X POST http://localhost:8090/api/feedbacks \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "recetteId": "recette456",
    "evaluation": 5,
    "commentaire": "Délicieuse recette !"
  }'
```

#### Obtenir la note moyenne d'une recette

```bash
curl http://localhost:8090/api/feedbacks/recette/recette456/average
```

Réponse :
```json
{
  "recetteId": "recette456",
  "averageRating": 4.67,
  "totalFeedbacks": 15
}
```

## 🗂️ Structure du Projet

```
ms-feedback/
├── src/
│   ├── main/
│   │   ├── java/com/springbootTemplate/univ/soa/
│   │   │   ├── Application.java
│   │   │   ├── config/
│   │   │   │   ├── DotenvConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   └── FeedbackController.java
│   │   │   ├── dto/
│   │   │   │   ├── AverageRatingResponse.java
│   │   │   │   ├── FeedbackCreateRequest.java
│   │   │   │   ├── FeedbackResponse.java
│   │   │   │   └── FeedbackUpdateRequest.java
│   │   │   ├── exception/
│   │   │   │   ├── FeedbackNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── factory/
│   │   │   │   └── FeedbackFactory.java
│   │   │   ├── model/
│   │   │   │   └── Feedback.java
│   │   │   ├── repository/
│   │   │   │   └── FeedbackRepository.java
│   │   │   └── service/
│   │   │       ├── FeedbackService.java
│   │   │       └── FeedbackServiceImpl.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── META-INF/
│   │           └── spring.factories
│   └── test/
│       └── java/com/springbootTemplate/univ/soa/
│           ├── FeedbackControllerTest.java
│           ├── FeedbackServiceTest.java
│           └── MsFeedbackApplicationTests.java
├── .env                    # Fichier de configuration (fourni par l'admin)
├── .gitignore
├── docker-compose.yml      # Configuration Docker (partagée avec le template)
├── pom.xml
└── README.md
```

## 🎨 Design Patterns Utilisés

### Builder Pattern

Utilisé pour la construction fluide des objets DTO et entités :

```java
Feedback feedback = Feedback.builder()
    .userId("user123")
    .recetteId("recette456")
    .evaluation(5)
    .commentaire("Excellent!")
    .build();
```

### Factory Pattern

Centralise la création des objets pour éviter la duplication :

```java
public class FeedbackFactory {
    public Feedback createFeedback(FeedbackCreateRequest request) { ... }
    public FeedbackResponse createResponse(Feedback feedback) { ... }
    public List<FeedbackResponse> createResponseList(List<Feedback> feedbacks) { ... }
}
```

### Repository Pattern

Abstraction de la couche d'accès aux données avec Spring Data MongoDB :

```java
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByUserIdOrderByDateFeedbackDesc(String userId);
    List<Feedback> findByRecetteIdOrderByDateFeedbackDesc(String recetteId);
}
```

## 🧪 Tests et Couverture

### Lancer les tests

```bash
# Tous les tests
mvn test

# Tests avec rapport de couverture
mvn test jacoco:report

# Ouvrir le rapport de couverture
start target\site\jacoco\index.html
```

### Couverture actuelle

| Package | Couverture             |
|---------|------------------------|
| **Service** | ~73%                   |
| **Controller** | ~90%                   |
| **Factory** | ~100%                  |
| **Repository** | ~100% |
| **Global** | ~87%                   |

#### Tests Unitaires
- ✅ **FeedbackServiceTest** - 12 tests unitaires du service
- ✅ **FeedbackControllerTest** - 12 tests unitaires du contrôleur (MockMvc)
- ✅ **FeedbackFactoryTest** - 15 tests de la factory
- ✅ **GlobalExceptionHandlerTest** - 13 tests du gestionnaire d'exceptions
- ✅ **FeedbackNotFoundExceptionTest** - 8 tests de l'exception personnalisée


### Profils Spring Boot

Le microservice supporte plusieurs profils :

```bash
# Développement (par défaut)
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Tests
mvn test -Dspring.profiles.active=test
```

## 🐳 Docker

### Démarrer uniquement MongoDB

```bash
docker-compose up -d mongodb mongo-express
```

### Logs des conteneurs

```bash
# MongoDB
docker-compose logs -f mongodb

# Mongo Express
docker-compose logs -f mongo-express
```

### Arrêter les services

```bash
# Arrêter sans supprimer les données
docker-compose down

# Arrêter et supprimer les volumes (⚠️ perte de données)
docker-compose down -v
```

## 📊 Modèle de Données

### Entité Feedback

```json
{
  "_id": "507f1f77bcf86cd799439011",
  "userId": "user123",
  "recetteId": "recette456",
  "evaluation": 5,
  "commentaire": "Excellente recette !",
  "dateFeedback": "2025-10-14T14:30:00",
  "dateModification": "2025-10-14T14:30:00",
  "_class": "com.springbootTemplate.univ.soa.model.Feedback"
}
```

### Validation

- `userId` : Obligatoire, non vide
- `recetteId` : Obligatoire, non vide
- `evaluation` : Obligatoire, entre 1 et 5
- `commentaire` : Optionnel, max 1000 caractères

## 🔄 Intégration avec le Template Parent

### Synchronisation avec le template

```bash
# Configurer le template comme remote upstream (une seule fois)
git remote add upstream https://github.com/EmilieHascoet/SmartDish.git

# Récupérer les mises à jour
git fetch upstream

# Rebaser sur le template
git rebase upstream/main

# Pousser les changements
git push origin main --force-with-lease
```

### Fréquence de mise à jour recommandée

- 🔄 **Hebdomadaire** - Vérification des mises à jour
- 📅 **Avant chaque release** - Obligatoire
- 🚨 **Immédiatement** - En cas d'alerte de sécurité

## 🔐 Sécurité

### Bonnes pratiques appliquées

- ✅ Variables sensibles dans `.env` (hors Git)
- ✅ Validation des entrées avec `@Valid`
- ✅ Gestion globale des exceptions
- ✅ Pas de données sensibles dans les logs
- ✅ MongoDB avec authentification


## 📈 Monitoring et Observabilité

### Actuator Endpoints

- `/actuator/health` - État de santé
- `/actuator/info` - Informations sur l'application
- `/actuator/metrics` - Métriques de performance

### Logs

Les logs sont configurés avec différents niveaux :

```properties
logging.level.root=INFO
logging.level.com.springbootTemplate.univ.soa=DEBUG
logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
```

## 🚀 Déploiement

### Build pour la production

```bash
# Créer le JAR
mvn clean package -DskipTests

# Le JAR se trouve dans :
target/ms-feedback-1.0.0.jar

# Lancer en production
java -jar -Dspring.profiles.active=prod target/ms-feedback-1.0.0.jar
```

### Docker Build (à venir)

```bash
# Build de l'image Docker
docker build -t ms-feedback:1.0.0 .

# Lancer le conteneur
docker run -p 8091:8091 --env-file .env ms-feedback:1.0.0
```

## 🤝 Contribution

### Workflow de développement

1. Créer une branche depuis `main`
```bash
git checkout -b feat/nouvelle-fonctionnalite
```

2. Développer et tester localement
```bash
mvn test
mvn spring-boot:run
```

3. Vérifier la couverture de tests
```bash
mvn clean test jacoco:report
```

4. Commiter avec des messages clairs
```bash
git commit -m "feat: ajout de la fonctionnalité X"
```

5. Pousser et créer une Pull Request
```bash
git push origin feat/nouvelle-fonctionnalite
```

### Conventions de commit

- `feat:` - Nouvelle fonctionnalité
- `fix:` - Correction de bug
- `docs:` - Documentation
- `test:` - Ajout/modification de tests
- `refactor:` - Refactoring du code
- `chore:` - Tâches diverses

## Ressources

- 📚 [Documentation Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- 📚 [Documentation MongoDB](https://docs.mongodb.com/)
- 📚 [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- 📚 [Swagger/OpenAPI](https://swagger.io/docs/)

## 📝 Changelog

### Version 1.0.0 (2025-10-14)

- ✅ Implémentation des patterns Builder et Factory
- ✅ Ajout de la couverture de tests (87%)
- ✅ Documentation API avec Swagger
- ✅ Configuration Docker Compose
- ✅ Intégration avec le template parent


## 📄 Licence

Ce projet fait partie de l'application SmartDish et est soumis aux termes de la licence du projet parent.

---

**🎯 ms-feedback v1.0.0** - Propulsé par Spring Boot 3.5.6 et MongoDB 7.0
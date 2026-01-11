# 🚀 Guide de Démarrage Rapide

Ce guide vous permet de lancer la plateforme e-commerce en quelques minutes.

## Prérequis

Vérifiez que vous avez installé :

```bash
java -version    # Java 17+
mvn -version     # Maven 3.9+
node -v          # Node.js 18+
docker --version # Docker Desktop
```

## Installation en 5 Étapes

### 1️⃣ Cloner le Repository

```bash
git clone https://github.com/ghalilahlou/final-project-jee-.git
cd final-project-jee-
```

### 2️⃣ Démarrer l'Infrastructure Docker

```bash
cd infrastructure/docker
docker-compose up -d
```

**Attendez 30-60 secondes** que tous les services soient prêts.

### 3️⃣ Configurer Keycloak

1. Ouvrir **http://localhost:8080**
2. Login : `admin` / `admin123`
3. Créer le realm : `ecommerce-realm`
4. Créer les rôles : `ADMIN`, `CUSTOMER`
5. Créer les utilisateurs :
   - **admin** (password: admin123) → Rôle: ADMIN
   - **customer** (password: customer123) → Rôle: CUSTOMER

📸 _Voir les screenshots dans `/screenshots` pour guider la configuration_

### 4️⃣ Lancer les Services Backend

**Terminal 1 - Gateway :**
```bash
cd backend/gateway-service
mvn spring-boot:run
```

**Terminal 2 - Product Service :**
```bash
cd backend/product-service
mvn spring-boot:run
```

**Terminal 3 - Chatbot Service (optionnel) :**
```bash
cd backend/chatbot-service
mvn spring-boot:run
```

### 5️⃣ Lancer le Frontend Angular

**Terminal 4 - Frontend :**
```bash
cd ecommerce-frontend
npm install
npm start
```

## ✅ Vérification

Une fois tous les services démarrés, ouvrez :

- 🎨 **Frontend** : http://localhost:4200
- 🔐 **Keycloak** : http://localhost:8080
- 🌐 **API Gateway** : http://localhost:8081
- 📊 **Kafka UI** : http://localhost:8090
- 🔍 **Zipkin** : http://localhost:9411

## 🧪 Test Rapide

1. Ouvrir http://localhost:4200
2. Cliquer sur **"Produits"**
3. Voir le catalogue affiché
4. Cliquer sur **"Mon Compte"** → Se connecter avec `customer` / `customer123`
5. Ajouter des produits au panier

## 📚 Documentation Complète

Pour plus de détails, consultez le [README.md](README.md) complet.

## 🆘 Problèmes Fréquents

### Les produits ne s'affichent pas
```bash
# Vérifier que Product Service fonctionne
curl http://localhost:8082/api/products
```

### Erreur d'authentification
- Vérifier que Keycloak est démarré : http://localhost:8080
- Vérifier que le realm `ecommerce-realm` existe

### Port déjà utilisé
```bash
# Windows - Trouver le processus sur le port 8081
netstat -ano | findstr :8081
# Tuer le processus
taskkill /PID <PID> /F
```

---

**Bon développement ! 🚀**

_Pour toute question : [Voir README.md](README.md)_

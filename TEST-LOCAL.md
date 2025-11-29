# 🧪 Test Local Rapide - Minikube + MySQL + phpMyAdmin

## Prérequis
- Docker Desktop installé et démarré
- Minikube installé
- kubectl installé

## Étapes

### 1. Démarrer Minikube
```powershell
minikube start --driver=docker --memory=4096 --cpus=2
minikube status
```

### 2. Charger l'image Docker
```powershell
# Build l'application
mvn clean package -DskipTests

# Build l'image Docker
docker build -t univ-soa:latest .

# Charger dans Minikube
minikube image load univ-soa:latest

# Vérifier
minikube image ls | Select-String "univ-soa"
```

### 3. Créer le namespace
```powershell
kubectl create namespace soa-integration
kubectl config set-context --current --namespace=soa-integration
```

### 4. Déployer les manifests
```powershell
kubectl apply -f k8s/minikube/ -n soa-integration

# Vérifier
kubectl get all -n soa-integration
```

### 5. Attendre que les pods soient prêts
```powershell
# Attendre MySQL
kubectl wait --for=condition=ready pod -l app=mysql -n soa-integration --timeout=120s

# Attendre l'application
kubectl wait --for=condition=ready pod -l app=univ-soa -n soa-integration --timeout=180s

# Attendre phpMyAdmin
kubectl wait --for=condition=ready pod -l app=phpmyadmin -n soa-integration --timeout=60s
```

### 6. Obtenir les URLs
```powershell
# Minikube IP
$MINIKUBE_IP = minikube ip

# API Service
$API_PORT = kubectl get svc univ-soa -n soa-integration -o jsonpath='{.spec.ports[0].nodePort}'
$API_URL = "http://${MINIKUBE_IP}:${API_PORT}"

Write-Host "🚀 API URL: $API_URL" -ForegroundColor Green
Write-Host "📍 Health: $API_URL/actuator/health" -ForegroundColor Cyan
Write-Host "📍 Database Test: $API_URL/api/database/test" -ForegroundColor Cyan

# phpMyAdmin
$PMA_PORT = kubectl get svc phpmyadmin -n soa-integration -o jsonpath='{.spec.ports[0].nodePort}'
$PMA_URL = "http://${MINIKUBE_IP}:${PMA_PORT}"

Write-Host ""
Write-Host "💾 phpMyAdmin URL: $PMA_URL" -ForegroundColor Green
Write-Host "👤 Username: root" -ForegroundColor Cyan
Write-Host "🔑 Password: password" -ForegroundColor Cyan
```

### 7. Tester l'API
```powershell
# Health check
curl.exe $API_URL/actuator/health

# Database test
curl.exe $API_URL/api/database/test
```

### 8. Déboguer si nécessaire
```powershell
# Voir les pods
kubectl get pods -n soa-integration -o wide

# Logs MySQL
kubectl logs -l app=mysql -n soa-integration --tail=50

# Logs Application
kubectl logs -l app=univ-soa -n soa-integration --tail=50

# Logs phpMyAdmin
kubectl logs -l app=phpmyadmin -n soa-integration --tail=50

# Describe pod si problème
kubectl describe pod -l app=univ-soa -n soa-integration
```

### 9. Nettoyer (optionnel)
```powershell
# Supprimer le namespace
kubectl delete namespace soa-integration

# Arrêter Minikube
minikube stop

# Supprimer Minikube
minikube delete
```

## Problèmes fréquents

### MySQL ne démarre pas
```powershell
# Vérifier les événements
kubectl get events -n soa-integration --sort-by='.lastTimestamp'

# Vérifier les logs
kubectl logs -l app=mysql -n soa-integration --tail=100
```

### Application crash au démarrage
```powershell
# Vérifier que MySQL est bien ready
kubectl get pods -l app=mysql -n soa-integration

# Vérifier les variables d'environnement
kubectl describe configmap univ-soa-config -n soa-integration

# Vérifier les logs de l'application
kubectl logs -l app=univ-soa -n soa-integration --tail=100
```

### Image pas trouvée
```powershell
# Recharger l'image
minikube image load univ-soa:latest

# Forcer le redéploiement
kubectl rollout restart deployment/univ-soa -n soa-integration
```


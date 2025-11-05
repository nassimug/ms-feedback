# Script PowerShell de déploiement pour l'environnement Integration
# Ce script déploie tous les microservices de l'environnement Integration

Write-Host "🧪 Déploiement sur l'environnement INTEGRATION" -ForegroundColor Cyan
Write-Host ""

# Configuration
$namespace = "soa-integration"

# Vérifier que Minikube est démarré
Write-Host "📊 Vérification de Minikube..." -ForegroundColor Yellow
$minikubeStatus = minikube status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Minikube n'est pas démarré. Démarrage..." -ForegroundColor Red
    minikube start
}
Write-Host "✅ Minikube opérationnel" -ForegroundColor Green
Write-Host ""

# Créer le namespace s'il n'existe pas
Write-Host "📦 Création du namespace $namespace..." -ForegroundColor Yellow
kubectl create namespace $namespace 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Namespace créé" -ForegroundColor Green
} else {
    Write-Host "ℹ️  Namespace existe déjà" -ForegroundColor Gray
}
Write-Host ""

# Vérifier Vault
Write-Host "🔐 Vérification de Vault..." -ForegroundColor Yellow
$vaultPods = kubectl get pods -n vault -l app.kubernetes.io/name=vault -o jsonpath='{.items[0].status.phase}' 2>$null
if ($vaultPods -ne "Running") {
    Write-Host "⚠️  Vault n'est pas opérationnel" -ForegroundColor Yellow
} else {
    Write-Host "✅ Vault opérationnel" -ForegroundColor Green
}
Write-Host ""

# Déployer tous les microservices
Write-Host "🚀 Déploiement des microservices..." -ForegroundColor Cyan

# Déployer le manifeste principal
if (Test-Path "deployment.yaml") {
    Write-Host "   ✓ Déploiement du service principal..." -ForegroundColor Green
    kubectl apply -f deployment.yaml
}

# Déployer tous les microservices dans le dossier microservices/
if (Test-Path "microservices") {
    Get-ChildItem "microservices" -Directory | ForEach-Object {
        $servicePath = Join-Path $_.FullName "deployment.yaml"
        if (Test-Path $servicePath) {
            Write-Host "   ✓ Déploiement de $($_.Name)..." -ForegroundColor Green
            kubectl apply -f $servicePath
        }
    }
}

Write-Host ""
Write-Host "⏳ Attente de la disponibilité des pods..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "✅ Déploiement terminé sur Integration" -ForegroundColor Green
Write-Host ""

Write-Host "📊 État des pods:" -ForegroundColor Cyan
kubectl get pods -n $namespace

Write-Host ""
Write-Host "🌐 Services:" -ForegroundColor Cyan
kubectl get svc -n $namespace

Write-Host ""
Write-Host "🔗 Ingress:" -ForegroundColor Cyan
kubectl get ingress -n $namespace

Write-Host ""
Write-Host "💡 Pour voir les logs d'un pod:" -ForegroundColor Yellow
Write-Host "   kubectl logs -f <pod-name> -n $namespace" -ForegroundColor Gray
Write-Host ""
Write-Host "💡 Pour accéder via port-forward:" -ForegroundColor Yellow
Write-Host "   kubectl port-forward -n $namespace svc/<service-name> 8080:8080" -ForegroundColor Gray


#!/bin/bash

# Script de déploiement pour l'environnement Integration
# Ce script déploie tous les microservices de l'environnement Integration

set -e

echo "🧪 Déploiement sur l'environnement INTEGRATION"
echo ""

# Configuration
NAMESPACE="soa-integration"
VAULT_ADDR="http://localhost:8200"

# Vérifier que le namespace existe
echo "📦 Vérification du namespace $NAMESPACE..."
kubectl create namespace $NAMESPACE 2>/dev/null || echo "Namespace existe déjà"

# Déployer tous les microservices
echo ""
echo "🚀 Déploiement des microservices..."

# Déployer le manifeste principal
if [ -f "deployment.yaml" ]; then
    echo "   ✓ Déploiement du service principal..."
    kubectl apply -f deployment.yaml
fi

# Déployer tous les microservices dans le dossier microservices/
if [ -d "microservices" ]; then
    for service in microservices/*/; do
        if [ -f "${service}deployment.yaml" ]; then
            service_name=$(basename "$service")
            echo "   ✓ Déploiement de $service_name..."
            kubectl apply -f "${service}deployment.yaml"
        fi
    done
fi

echo ""
echo "✅ Déploiement terminé sur Integration"
echo ""
echo "📊 État des pods:"
kubectl get pods -n $NAMESPACE

echo ""
echo "🌐 Services:"
kubectl get svc -n $NAMESPACE

echo ""
echo "🔗 Ingress:"
kubectl get ingress -n $NAMESPACE


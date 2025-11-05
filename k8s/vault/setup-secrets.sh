#!/bin/bash

# Script de configuration des secrets dans Vault pour RecipeYouLove
# Ce script configure tous les secrets nécessaires pour les environnements integration et production

set -e

echo "🔐 Configuration des secrets Vault pour RecipeYouLove..."

# Configuration de l'adresse Vault
export VAULT_ADDR='http://localhost:8200'
export VAULT_TOKEN='root'

echo "📝 Activation du moteur KV v2 si nécessaire..."
vault secrets enable -version=2 -path=secret kv 2>/dev/null || echo "KV engine already enabled"

# ========================================
# SECRETS INTEGRATION ENVIRONMENT
# ========================================
echo ""
echo "🔧 Configuration des secrets INTEGRATION..."

# MySQL Integration
vault kv put secret/integration/mysql \
  host="mysql-service.databases.svc.cluster.local" \
  port="3306" \
  database="recipeyoulove_integration" \
  username="integration_user" \
  password="integration_password_2024" \
  root_password="root_integration_2024"

# MongoDB Integration
vault kv put secret/integration/mongodb \
  host="mongodb-service.databases.svc.cluster.local" \
  port="27017" \
  database="recipeyoulove_integration" \
  username="integration_mongo_user" \
  password="mongo_integration_2024" \
  root_username="admin" \
  root_password="mongo_root_integration_2024"

# Application Integration
vault kv put secret/integration/application \
  server_port="8080" \
  jpa_ddl_auto="update" \
  jpa_show_sql="true" \
  jpa_format_sql="true" \
  jpa_use_sql_comments="true" \
  log_level_jdbc="DEBUG" \
  log_level_hibernate_sql="DEBUG" \
  log_level_hibernate_binder="TRACE" \
  actuator_endpoints="health,info,metrics,prometheus" \
  actuator_health_details="always" \
  environment="integration"

# ========================================
# SECRETS PRODUCTION ENVIRONMENT
# ========================================
echo ""
echo "🚀 Configuration des secrets PRODUCTION..."

# MySQL Production
vault kv put secret/production/mysql \
  host="mysql-service.databases.svc.cluster.local" \
  port="3306" \
  database="recipeyoulove_production" \
  username="production_user" \
  password="Pr0d_P@ssw0rd_2024!" \
  root_password="R00t_Pr0d_P@ssw0rd_2024!"

# MongoDB Production
vault kv put secret/production/mongodb \
  host="mongodb-service.databases.svc.cluster.local" \
  port="27017" \
  database="recipeyoulove_production" \
  username="production_mongo_user" \
  password="M0ng0_Pr0d_P@ssw0rd_2024!" \
  root_username="admin" \
  root_password="M0ng0_R00t_Pr0d_2024!"

# Application Production
vault kv put secret/production/application \
  server_port="8080" \
  jpa_ddl_auto="validate" \
  jpa_show_sql="false" \
  jpa_format_sql="false" \
  jpa_use_sql_comments="false" \
  log_level_jdbc="INFO" \
  log_level_hibernate_sql="WARN" \
  log_level_hibernate_binder="WARN" \
  actuator_endpoints="health,info,metrics,prometheus" \
  actuator_health_details="when-authorized" \
  environment="production"

# ========================================
# CONFIGURATION KUBERNETES AUTH
# ========================================
echo ""
echo "🔑 Configuration de l'authentification Kubernetes..."

# Activer l'authentification Kubernetes
vault auth enable kubernetes 2>/dev/null || echo "Kubernetes auth already enabled"

# Configurer l'authentification Kubernetes
vault write auth/kubernetes/config \
  kubernetes_host="https://kubernetes.default.svc:443"

# Créer une politique pour l'environnement integration
vault policy write integration-policy - <<EOF
path "secret/data/integration/*" {
  capabilities = ["read", "list"]
}
EOF

# Créer une politique pour l'environnement production
vault policy write production-policy - <<EOF
path "secret/data/production/*" {
  capabilities = ["read", "list"]
}
EOF

# Créer les rôles Kubernetes pour integration
vault write auth/kubernetes/role/integration-role \
  bound_service_account_names=recipeyoulove-sa \
  bound_service_account_namespaces=soa-integration \
  policies=integration-policy \
  ttl=24h

# Créer les rôles Kubernetes pour production
vault write auth/kubernetes/role/production-role \
  bound_service_account_names=recipeyoulove-sa \
  bound_service_account_namespaces=soa-production \
  policies=production-policy \
  ttl=24h

echo ""
echo "✅ Configuration des secrets Vault terminée avec succès!"
echo ""
echo "📋 Secrets configurés:"
echo "  - secret/integration/mysql"
echo "  - secret/integration/mongodb"
echo "  - secret/integration/application"
echo "  - secret/production/mysql"
echo "  - secret/production/mongodb"
echo "  - secret/production/application"
echo ""
echo "🔐 Politiques créées:"
echo "  - integration-policy (accès aux secrets integration)"
echo "  - production-policy (accès aux secrets production)"
echo ""
echo "🎯 Rôles Kubernetes créés:"
echo "  - integration-role (namespace: soa-integration)"
echo "  - production-role (namespace: soa-production)"


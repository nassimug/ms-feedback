# Script PowerShell de configuration des secrets dans Vault pour RecipeYouLove
# Ce script configure tous les secrets nécessaires pour les environnements integration et production

Write-Host "🔐 Configuration des secrets Vault pour RecipeYouLove..." -ForegroundColor Cyan

# Configuration de l'adresse Vault
$env:VAULT_ADDR = 'http://localhost:8200'
$env:VAULT_TOKEN = 'root'

Write-Host "📝 Activation du moteur KV v2 si nécessaire..." -ForegroundColor Yellow
vault secrets enable -version=2 -path=secret kv 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "KV engine already enabled" -ForegroundColor Gray
}

# ========================================
# SECRETS INTEGRATION ENVIRONMENT
# ========================================
Write-Host ""
Write-Host "🔧 Configuration des secrets INTEGRATION..." -ForegroundColor Green

# MySQL Integration
vault kv put secret/integration/mysql `
  host="mysql-service.databases.svc.cluster.local" `
  port="3306" `
  database="recipeyoulove_integration" `
  username="integration_user" `
  password="integration_password_2024" `
  root_password="root_integration_2024"

# MongoDB Integration
vault kv put secret/integration/mongodb `
  host="mongodb-service.databases.svc.cluster.local" `
  port="27017" `
  database="recipeyoulove_integration" `
  username="integration_mongo_user" `
  password="mongo_integration_2024" `
  root_username="admin" `
  root_password="mongo_root_integration_2024"

# Application Integration
vault kv put secret/integration/application `
  server_port="8080" `
  jpa_ddl_auto="update" `
  jpa_show_sql="true" `
  jpa_format_sql="true" `
  jpa_use_sql_comments="true" `
  log_level_jdbc="DEBUG" `
  log_level_hibernate_sql="DEBUG" `
  log_level_hibernate_binder="TRACE" `
  actuator_endpoints="health,info,metrics,prometheus" `
  actuator_health_details="always" `
  environment="integration"

# ========================================
# SECRETS PRODUCTION ENVIRONMENT
# ========================================
Write-Host ""
Write-Host "🚀 Configuration des secrets PRODUCTION..." -ForegroundColor Green

# MySQL Production
vault kv put secret/production/mysql `
  host="mysql-service.databases.svc.cluster.local" `
  port="3306" `
  database="recipeyoulove_production" `
  username="production_user" `
  password="Pr0d_P@ssw0rd_2024!" `
  root_password="R00t_Pr0d_P@ssw0rd_2024!"

# MongoDB Production
vault kv put secret/production/mongodb `
  host="mongodb-service.databases.svc.cluster.local" `
  port="27017" `
  database="recipeyoulove_production" `
  username="production_mongo_user" `
  password="M0ng0_Pr0d_P@ssw0rd_2024!" `
  root_username="admin" `
  root_password="M0ng0_R00t_Pr0d_2024!"

# Application Production
vault kv put secret/production/application `
  server_port="8080" `
  jpa_ddl_auto="validate" `
  jpa_show_sql="false" `
  jpa_format_sql="false" `
  jpa_use_sql_comments="false" `
  log_level_jdbc="INFO" `
  log_level_hibernate_sql="WARN" `
  log_level_hibernate_binder="WARN" `
  actuator_endpoints="health,info,metrics,prometheus" `
  actuator_health_details="when-authorized" `
  environment="production"

# ========================================
# CONFIGURATION KUBERNETES AUTH
# ========================================
Write-Host ""
Write-Host "🔑 Configuration de l'authentification Kubernetes..." -ForegroundColor Yellow

# Activer l'authentification Kubernetes
vault auth enable kubernetes 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Kubernetes auth already enabled" -ForegroundColor Gray
}

# Configurer l'authentification Kubernetes
vault write auth/kubernetes/config `
  kubernetes_host="https://kubernetes.default.svc:443"

# Créer une politique pour l'environnement integration
$integrationPolicy = @"
path "secret/data/integration/*" {
  capabilities = ["read", "list"]
}
"@
$integrationPolicy | vault policy write integration-policy -

# Créer une politique pour l'environnement production
$productionPolicy = @"
path "secret/data/production/*" {
  capabilities = ["read", "list"]
}
"@
$productionPolicy | vault policy write production-policy -

# Créer les rôles Kubernetes pour integration
vault write auth/kubernetes/role/integration-role `
  bound_service_account_names=recipeyoulove-sa `
  bound_service_account_namespaces=soa-integration `
  policies=integration-policy `
  ttl=24h

# Créer les rôles Kubernetes pour production
vault write auth/kubernetes/role/production-role `
  bound_service_account_names=recipeyoulove-sa `
  bound_service_account_namespaces=soa-production `
  policies=production-policy `
  ttl=24h

Write-Host ""
Write-Host "✅ Configuration des secrets Vault terminée avec succès!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Secrets configurés:" -ForegroundColor Cyan
Write-Host "  - secret/integration/mysql"
Write-Host "  - secret/integration/mongodb"
Write-Host "  - secret/integration/application"
Write-Host "  - secret/production/mysql"
Write-Host "  - secret/production/mongodb"
Write-Host "  - secret/production/application"
Write-Host ""
Write-Host "🔐 Politiques créées:" -ForegroundColor Cyan
Write-Host "  - integration-policy (accès aux secrets integration)"
Write-Host "  - production-policy (accès aux secrets production)"
Write-Host ""
Write-Host "🎯 Rôles Kubernetes créés:" -ForegroundColor Cyan
Write-Host "  - integration-role (namespace: soa-integration)"
Write-Host "  - production-role (namespace: soa-production)"


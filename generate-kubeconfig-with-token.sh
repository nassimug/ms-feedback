#!/bin/bash
# Script pour générer un kubeconfig avec token pour OKE
# À exécuter dans OCI Cloud Shell

set -e

CLUSTER_ID="ocid1.cluster.oc1.eu-paris-1.aaaaaaaakhi5xnahycf4ozq2vinwsf3t6hbiwmomgq5quiqsvcq3gvzgw3tq"
REGION="eu-paris-1"

echo "=========================================="
echo "Génération du kubeconfig avec token"
echo "=========================================="
echo ""

# Récupérer les informations du cluster
echo "1. Récupération des informations du cluster..."
CLUSTER_INFO=$(oci ce cluster get --cluster-id "$CLUSTER_ID" --region "$REGION")

CLUSTER_NAME=$(echo "$CLUSTER_INFO" | jq -r '.data.name')
CLUSTER_ENDPOINT=$(echo "$CLUSTER_INFO" | jq -r '.data.endpoints["public-endpoint"]')
CLUSTER_CA_DATA=$(echo "$CLUSTER_INFO" | jq -r '.data["kubernetes-network-config"]["cluster-pod-cidr"]')

# Ajouter https:// si absent
if [[ ! "$CLUSTER_ENDPOINT" =~ ^https:// ]]; then
    CLUSTER_ENDPOINT="https://$CLUSTER_ENDPOINT"
fi

echo "Nom du cluster: $CLUSTER_NAME"
echo "Endpoint: $CLUSTER_ENDPOINT"
echo ""

# Générer un token - essayer différentes méthodes
echo "2. Génération d'un token d'authentification..."

# Méthode 1: generate-token direct
TOKEN_RESPONSE=$(oci ce cluster generate-token --cluster-id "$CLUSTER_ID" --region "$REGION" 2>&1)
echo "Token response: $TOKEN_RESPONSE" | head -c 200
echo ""

# Essayer d'extraire le token de différentes façons
TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.token // .data.token // empty' 2>/dev/null)

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
    echo "⚠️  Méthode generate-token a échoué, tentative avec exec credential..."
    # Méthode 2: utiliser l'exec pour obtenir le token
    TOKEN=$(oci ce cluster generate-token --cluster-id "$CLUSTER_ID" --region "$REGION" --output json 2>/dev/null | jq -r 'if type=="object" then (.token // .data.token // .status.token) else . end' | head -1)
fi

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
    echo "❌ Erreur: impossible de générer un token valide"
    echo "Token reçu: '$TOKEN'"
    echo ""
    echo "Essayez manuellement:"
    echo "oci ce cluster generate-token --cluster-id $CLUSTER_ID --region $REGION"
    exit 1
fi

echo "✅ Token généré (longueur: ${#TOKEN})"
echo ""

# Récupérer le certificat CA du cluster
echo "3. Récupération du certificat CA..."
CLUSTER_CA_CERT=$(oci ce cluster get --cluster-id "$CLUSTER_ID" --region "$REGION" | jq -r '.data["kubernetes-network-config"]["cluster-pod-cidr"]' 2>/dev/null || echo "")

# Si pas de CA disponible via API, désactiver la vérification SSL (pas recommandé en prod)
if [ -z "$CLUSTER_CA_CERT" ] || [ "$CLUSTER_CA_CERT" = "null" ]; then
    echo "⚠️  Certificat CA non disponible, utilisation de insecure-skip-tls-verify"
    CA_CONFIG="insecure-skip-tls-verify: true"
else
    CA_CONFIG="certificate-authority-data: $CLUSTER_CA_CERT"
fi

# Créer le kubeconfig
echo "4. Création du kubeconfig..."
mkdir -p ~/.kube

cat > ~/.kube/config << EOF
apiVersion: v1
clusters:
- cluster:
    $CA_CONFIG
    server: $CLUSTER_ENDPOINT
  name: cluster-$CLUSTER_ID
contexts:
- context:
    cluster: cluster-$CLUSTER_ID
    user: user-$CLUSTER_ID
  name: context-$CLUSTER_ID
current-context: context-$CLUSTER_ID
kind: Config
preferences: {}
users:
- name: user-$CLUSTER_ID
  user:
    token: $TOKEN
EOF

chmod 600 ~/.kube/config

echo "✅ Kubeconfig créé dans ~/.kube/config"
echo ""

# Vérifier
echo "5. Vérification..."
echo ""
cat ~/.kube/config | grep -A 3 "user:"
echo ""

# Tester la connexion
echo "6. Test de connexion au cluster..."
if kubectl get nodes; then
    echo ""
    echo "✅ Connexion au cluster réussie!"
    echo ""
    echo "7. Encodage du kubeconfig en base64..."
    cat ~/.kube/config | base64 -w 0 > ~/kubeconfig-base64.txt
    echo ""
    echo "✅ Fichier encodé créé: ~/kubeconfig-base64.txt"
    echo ""
    echo "Exécutez: cat ~/kubeconfig-base64.txt"
    echo "Copiez la sortie et mettez à jour le secret OCI_KUBECONFIG dans GitHub"
else
    echo ""
    echo "❌ Erreur de connexion au cluster"
    exit 1
fi


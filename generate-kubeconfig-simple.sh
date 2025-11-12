#!/bin/bash
# Script simplifié pour créer un kubeconfig avec token pour GitHub Actions
# À exécuter dans OCI Cloud Shell

set -e

CLUSTER_ID="ocid1.cluster.oc1.eu-paris-1.aaaaaaaakhi5xnahycf4ozq2vinwsf3t6hbiwmomgq5quiqsvcq3gvzgw3tq"
REGION="eu-paris-1"

echo "=========================================="
echo "Génération du kubeconfig pour GitHub"
echo "=========================================="
echo ""

# Générer un kubeconfig standard avec exec plugin
echo "1. Génération du kubeconfig avec exec plugin..."
oci ce cluster create-kubeconfig \
  --cluster-id "$CLUSTER_ID" \
  --file ~/.kube/config-temp \
  --region "$REGION" \
  --kube-endpoint PUBLIC_ENDPOINT \
  --overwrite

echo "✅ Kubeconfig temporaire créé"
echo ""

# Obtenir un token via l'exec plugin
echo "2. Extraction d'un token valide..."

# Le token est généré via l'exec command dans le kubeconfig
# On l'exécute manuellement pour obtenir le token
TOKEN=$(oci ce cluster generate-token --cluster-id "$CLUSTER_ID" --region "$REGION" 2>&1)

echo "Réponse brute de la génération de token:"
echo "$TOKEN"
echo ""

# Essayer de parser le token de différentes façons
if echo "$TOKEN" | jq . >/dev/null 2>&1; then
    # C'est du JSON
    TOKEN_VALUE=$(echo "$TOKEN" | jq -r '.token // .data.token // .status.token // empty')
    if [ -z "$TOKEN_VALUE" ] || [ "$TOKEN_VALUE" = "null" ]; then
        # Chercher dans toutes les clés qui contiennent "token"
        TOKEN_VALUE=$(echo "$TOKEN" | jq -r '.. | .token? // empty' | head -1)
    fi
else
    # Ce n'est pas du JSON, peut-être juste le token
    TOKEN_VALUE="$TOKEN"
fi

if [ -z "$TOKEN_VALUE" ] || [ "$TOKEN_VALUE" = "null" ]; then
    echo "❌ Impossible d'obtenir un token valide"
    echo ""
    echo "SOLUTION ALTERNATIVE:"
    echo "Le kubeconfig avec exec plugin fonctionne dans GitHub Actions SI OCI CLI est installé."
    echo "Mais cela nécessite la configuration des secrets API keys OCI."
    echo ""
    echo "Utilisons plutôt le kubeconfig actuel et configurons les secrets OCI dans GitHub:"
    echo ""
    echo "Vous devez configurer ces secrets dans GitHub:"
    echo "  - OCI_CLI_USER"
    echo "  - OCI_CLI_FINGERPRINT"
    echo "  - OCI_CLI_TENANCY"
    echo "  - OCI_CLI_REGION"
    echo "  - OCI_CLI_KEY_CONTENT"
    echo ""
    echo "Pour obtenir ces valeurs:"
    echo "  cat ~/.oci/config"
    echo "  cat ~/.oci/oci_api_key.pem"
    echo ""

    # Afficher les valeurs actuelles si le fichier existe
    if [ -f ~/.oci/config ]; then
        echo "Votre configuration OCI actuelle:"
        echo "================================"
        cat ~/.oci/config
        echo ""
        echo "Pour la clé privée (à mettre dans OCI_CLI_KEY_CONTENT):"
        echo "cat ~/.oci/oci_api_key.pem"
        echo ""
    else
        echo "⚠️  ~/.oci/config n'existe pas"
        echo "Créez une configuration OCI avec: oci setup config"
    fi

    exit 1
fi

echo "✅ Token obtenu (longueur: ${#TOKEN_VALUE})"
echo "Début du token: ${TOKEN_VALUE:0:50}..."
echo ""

# Récupérer les infos du cluster depuis le kubeconfig temporaire
echo "3. Extraction des informations du cluster..."

CLUSTER_SERVER=$(grep "server:" ~/.kube/config-temp | awk '{print $2}')
CLUSTER_CA=$(grep "certificate-authority-data:" ~/.kube/config-temp | awk '{print $2}')

echo "Server: $CLUSTER_SERVER"
echo "CA présent: $([ -n "$CLUSTER_CA" ] && echo 'Oui' || echo 'Non')"
echo ""

# Créer le nouveau kubeconfig avec token
echo "4. Création du kubeconfig avec token..."

if [ -n "$CLUSTER_CA" ]; then
    CA_LINE="    certificate-authority-data: $CLUSTER_CA"
else
    CA_LINE="    insecure-skip-tls-verify: true"
fi

cat > ~/.kube/config << EOF
apiVersion: v1
clusters:
- cluster:
$CA_LINE
    server: $CLUSTER_SERVER
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
    token: $TOKEN_VALUE
EOF

chmod 600 ~/.kube/config
echo "✅ Kubeconfig créé"
echo ""

# Vérifier
echo "5. Test de connexion au cluster..."
if kubectl get nodes 2>&1; then
    echo ""
    echo "✅ Connexion réussie!"
    echo ""
    echo "6. Encodage en base64..."
    cat ~/.kube/config | base64 -w 0 > ~/kubeconfig-base64.txt
    echo ""
    echo "✅ Fichier créé: ~/kubeconfig-base64.txt"
    echo ""
    echo "═══════════════════════════════════════════"
    echo "PROCHAINE ÉTAPE:"
    echo "═══════════════════════════════════════════"
    echo ""
    echo "1. Exécutez: cat ~/kubeconfig-base64.txt"
    echo "2. Copiez TOUTE la sortie"
    echo "3. Allez sur: https://github.com/AbdBoutchichi/SmartDish/settings/secrets/actions"
    echo "4. Cliquez sur OCI_KUBECONFIG > Update"
    echo "5. Collez la valeur et sauvegardez"
    echo ""
else
    echo ""
    echo "❌ La connexion a échoué"
    echo ""
    echo "Détails du kubeconfig créé:"
    cat ~/.kube/config
    echo ""
    exit 1
fi

# Nettoyage
rm -f ~/.kube/config-temp


# Script de Vérification de la Pipeline
# Usage: .\verify-pipeline.ps1

Write-Host "`n╔═══════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   🔍 VERIFICATION DE LA PIPELINE CI/CD           ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

$errors = 0
$warnings = 0

# 1. Vérifier la structure des fichiers
Write-Host "1️⃣ Vérification de la structure des fichiers..." -ForegroundColor Yellow

$expectedFiles = @(
    "pipeline-orchestrator.yml",
    "config-vars.yml",
    "build-maven.yml",
    "check-coverage.yml",
    "build-docker-image.yml",
    "check-conformity-image.yml",
    "deploy-kubernetes.yml",
    "integration-tests.yml"
)

$actualFiles = Get-ChildItem ".github\workflows" -Filter "*.yml" | Select-Object -ExpandProperty Name

foreach ($file in $expectedFiles) {
    if ($actualFiles -contains $file) {
        Write-Host "  ✓ $file" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $file MANQUANT!" -ForegroundColor Red
        $errors++
    }
}

# 2. Vérifier qu'il n'y a pas d'anciens fichiers
Write-Host "`n2️⃣ Vérification des anciens fichiers..." -ForegroundColor Yellow

$oldFiles = @(
    "ci-cd-pipeline.yml",
    "pipeline-manual.yml"
)

foreach ($file in $oldFiles) {
    if ($actualFiles -contains $file) {
        Write-Host "  ⚠️ $file devrait être supprimé!" -ForegroundColor Red
        $errors++
    } else {
        Write-Host "  ✓ $file supprimé" -ForegroundColor Green
    }
}

# 3. Vérifier la syntaxe YAML basique
Write-Host "`n3️⃣ Vérification de la syntaxe YAML..." -ForegroundColor Yellow

foreach ($file in $expectedFiles) {
    $filePath = ".github\workflows\$file"
    if (Test-Path $filePath) {
        try {
            $content = Get-Content $filePath -Raw
            if ($content -match "^name:") {
                Write-Host "  ✓ $file - Syntaxe OK" -ForegroundColor Green
            } else {
                Write-Host "  ⚠️ $file - Pas de 'name:' trouvé" -ForegroundColor Yellow
                $warnings++
            }
        } catch {
            Write-Host "  ✗ $file - Erreur de lecture" -ForegroundColor Red
            $errors++
        }
    }
}

# 4. Vérifier les dépendances
Write-Host "`n4️⃣ Vérification des dépendances..." -ForegroundColor Yellow

$orchestratorContent = Get-Content ".github\workflows\pipeline-orchestrator.yml" -Raw

$requiredWorkflows = @(
    "config-vars.yml",
    "build-maven.yml",
    "check-coverage.yml",
    "build-docker-image.yml",
    "check-conformity-image.yml",
    "deploy-kubernetes.yml",
    "integration-tests.yml"
)

foreach ($workflow in $requiredWorkflows) {
    if ($orchestratorContent -match $workflow) {
        Write-Host "  ✓ $workflow référencé" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $workflow NON référencé!" -ForegroundColor Red
        $errors++
    }
}

# 5. Vérifier la présence de workflow_call
Write-Host "`n5️⃣ Vérification des workflow_call..." -ForegroundColor Yellow

$reusableWorkflows = @(
    "config-vars.yml",
    "build-maven.yml",
    "check-coverage.yml",
    "build-docker-image.yml",
    "check-conformity-image.yml",
    "deploy-kubernetes.yml",
    "integration-tests.yml"
)

foreach ($workflow in $reusableWorkflows) {
    $content = Get-Content ".github\workflows\$workflow" -Raw
    if ($content -match "workflow_call") {
        Write-Host "  ✓ $workflow est réutilisable" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️ $workflow n'a pas workflow_call" -ForegroundColor Yellow
        $warnings++
    }
}

# 6. Résumé
Write-Host "`n╔═══════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   📊 RÉSUMÉ DE LA VÉRIFICATION                    ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

Write-Host "Workflows attendus : $($expectedFiles.Count)" -ForegroundColor White
Write-Host "Workflows trouvés  : $($actualFiles.Count)" -ForegroundColor White
Write-Host "Erreurs            : $errors" -ForegroundColor $(if ($errors -eq 0) { "Green" } else { "Red" })
Write-Host "Avertissements     : $warnings" -ForegroundColor $(if ($warnings -eq 0) { "Green" } else { "Yellow" })

Write-Host ""

if ($errors -eq 0 -and $warnings -eq 0) {
    Write-Host "✅ Tout est OK! La pipeline est prête." -ForegroundColor Green
    Write-Host "`nCommandes suivantes:" -ForegroundColor Cyan
    Write-Host "  git add -A" -ForegroundColor White
    Write-Host "  git commit -m 'fix: pipeline modulaire corrigée'" -ForegroundColor White
    Write-Host "  git push" -ForegroundColor White
    exit 0
} elseif ($errors -eq 0) {
    Write-Host "⚠️ La pipeline fonctionne mais il y a des avertissements." -ForegroundColor Yellow
    exit 0
} else {
    Write-Host "❌ Il y a des erreurs à corriger!" -ForegroundColor Red
    exit 1
}


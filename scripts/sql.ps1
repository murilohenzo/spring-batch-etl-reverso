param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('Schema', 'D0', 'D1', 'Repeat', 'Validate', 'Explain', 'Cleanup', 'VolumeD0', 'VolumeD1')]
    [string]$Action
)
$ErrorActionPreference = 'Stop'
$scripts = @{
    Schema = 'ddl/00-schema.sql'
    D0 = 'dml/01-d0.sql'
    D1 = 'dml/02-d1.sql'
    Repeat = 'dml/03-idempotencia.sql'
    Validate = '04-validacao.sql'
    Explain = '05-explain.sql'
    Cleanup = '99-cleanup.sql'
    VolumeD0 = 'volume/10-volume-d0.sql'
    VolumeD1 = 'volume/11-volume-d1.sql'
}
$projectRoot = Split-Path -Parent $PSScriptRoot
$sqlFile = Join-Path $projectRoot ('src/main/resources/mysql/' + $scripts[$Action])
Push-Location $projectRoot
try {
    Get-Content -LiteralPath $sqlFile -Raw -Encoding utf8 |
        & docker compose exec -T mysql mysql --default-character-set=utf8mb4 -ubatch -pbatch etl_reverso
    if ($LASTEXITCODE -ne 0) { throw "Falha ao executar $Action no MySQL da simulacao." }
} finally {
    Pop-Location
}

# Validação da implementação

Executada em 07/09/2026 sobre este projeto.

- `mvn -B verify`: **BUILD SUCCESS**, 2 testes unitários + 14 testes de integração, zero falhas/erros/skips.
- Runtime disponível: JBR 25.0.4; Maven 3.9.16; compilação `release 21`; MySQL 8.4 real via Testcontainers. A execução específica em JDK 21 está configurada no CI, mas não foi executada nesta máquina.
- Paginação de teste: página 3 / chunk 2; cenário de 37 itens sem perda ao atualizar elegibilidade.
- Watermark confirmado exatamente, incluindo alteração de **1 microssegundo**. Gravação por `LocalDateTime` JDBC preserva o `DATETIME(6)` sem aplicar deslocamento de fuso da JVM.
- Falha do processor após primeiro chunk: 2 sucessos persistidos; mesma instância retomada escreve somente os 3 restantes. Nenhuma tentativa confirmada duplicada.
- Falha JDBC por FK: nenhuma linha do chunk permanece no controle.
- Scripts de volume: **50.000/50.000 → 6.200/6.200 → 0/0**, total físico 55.000, página/chunk 1.000. Plano e contagens em `evidencias/volume-result.txt`.

O teste externo do JAR utilizou outro projeto Compose com porta aleatória e volume novo. O DDL apareceu antes da primeira execução Java. O roteiro documentado produziu:

```text
D0                 readCount=5  writeCount=5  exitCode=0
Repeticao D0        readCount=0  writeCount=0  exitCode=0
D1                 readCount=4  writeCount=4  exitCode=0
Repeticao D1        readCount=0  writeCount=0  exitCode=0
Dado invalido                                 exitCode=5
Retomada corrigida readCount=1  writeCount=1  exitCode=0
Cleanup            origem=0     controle=0
```

As execuções separadas do JAR também comprovaram a preservação do controle e dos metadados entre processos. O container e o volume temporários desse aceite foram removidos ao terminar; o banco local do usuário não foi utilizado.

O plano capturado no teste de volume examinou 7.610 linhas da origem para devolver os primeiros 1.000 elegíveis. Isso demonstra a distinção entre itens entregues ao Batch e trabalho do banco. O teste confirma funcionalidade, não um SLA ou benchmark isolado de desempenho.

Não houve integração HTTP, BigQuery ou SaaS: são explicitamente abstraídos nesta simulação.

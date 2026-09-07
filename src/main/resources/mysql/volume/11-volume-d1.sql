-- Apos VolumeD0 processado: 5.000 novos + 1.200 alterados; 6.200 elegiveis.
INSERT INTO servico_governanca (cdServicoOrigem, dsNomeProduto, dsStatus, dtModificacao)
WITH digito AS (
    SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
), numeros AS (
    SELECT 1 + a.n + 10*b.n + 100*c.n + 1000*d.n + 10000*e.n AS n
    FROM digito a CROSS JOIN digito b CROSS JOIN digito c CROSS JOIN digito d CROSS JOIN digito e
)
SELECT CONCAT('10000000-0000-0000-0000-', LPAD(n, 12, '0')),
       CONCAT('Produto ', n), 'ATIVO', '2026-09-16 09:00:00.000000'
FROM numeros WHERE n BETWEEN 50001 AND 55000
ON DUPLICATE KEY UPDATE cdServicoOrigem = servico_governanca.cdServicoOrigem;

UPDATE servico_governanca
SET dsNomeProduto = CONCAT('Produto ', CAST(RIGHT(cdServicoOrigem, 12) AS UNSIGNED), ' alterado'),
    dtModificacao = '2026-09-16 09:00:00.000000'
WHERE cdServicoOrigem BETWEEN '10000000-0000-0000-0000-000000000001'
                         AND '10000000-0000-0000-0000-000000001200';

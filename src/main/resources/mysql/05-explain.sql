-- A consulta e executada por EXPLAIN ANALYZE. Rodar apenas na simulacao.
EXPLAIN ANALYZE
SELECT s.cdServicoGovernanca, s.cdServicoOrigem, s.dsNomeProduto, s.dsStatus, s.dtModificacao
FROM servico_governanca s
LEFT JOIN processamento_saas p ON p.cdServicoOrigem = s.cdServicoOrigem
WHERE (p.cdServicoOrigem IS NULL OR p.dtUltimoSucesso IS NULL OR s.dtModificacao > p.dtUltimoSucesso)
ORDER BY s.cdServicoGovernanca;

-- Primeira pagina; para paginas seguintes, acrescentar s.cdServicoGovernanca > ultima_pk.
EXPLAIN ANALYZE
SELECT s.cdServicoGovernanca, s.cdServicoOrigem, s.dsNomeProduto, s.dsStatus, s.dtModificacao
FROM servico_governanca s
LEFT JOIN processamento_saas p ON p.cdServicoOrigem = s.cdServicoOrigem
WHERE (p.cdServicoOrigem IS NULL OR p.dtUltimoSucesso IS NULL OR s.dtModificacao > p.dtUltimoSucesso)
ORDER BY s.cdServicoGovernanca LIMIT 1000;

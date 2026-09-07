SELECT COUNT(*) AS qtServicos FROM servico_governanca;
SELECT COUNT(*) AS qtSucessos FROM processamento_saas WHERE dsStatusProcessamento = 'SUCESSO';
SELECT cdServicoOrigem, COUNT(*) AS qtRegistros
FROM servico_governanca GROUP BY cdServicoOrigem HAVING COUNT(*) > 1;
SELECT s.cdServicoGovernanca, s.cdServicoOrigem, s.dsNomeProduto, s.dsStatus, s.dtModificacao
FROM servico_governanca s
LEFT JOIN processamento_saas p ON p.cdServicoOrigem = s.cdServicoOrigem
WHERE (p.cdServicoOrigem IS NULL OR p.dtUltimoSucesso IS NULL OR s.dtModificacao > p.dtUltimoSucesso)
ORDER BY s.cdServicoGovernanca;

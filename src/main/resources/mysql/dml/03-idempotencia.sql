-- Reaplicar apos D1: total permanece 7 e o batch nao encontra novas versoes.
INSERT INTO servico_governanca (cdServicoOrigem, dsNomeProduto, dsStatus, dtModificacao)
VALUES
    ('00000000-0000-0000-0000-000000000006', 'Produto 006', 'ATIVO', '2026-09-16 09:20:00.000000'),
    ('00000000-0000-0000-0000-000000000007', 'Produto 007', 'ATIVO', '2026-09-16 09:30:00.000000') AS novo
ON DUPLICATE KEY UPDATE
    dsNomeProduto = novo.dsNomeProduto,
    dsStatus = novo.dsStatus,
    dtModificacao = novo.dtModificacao;

INSERT INTO servico_governanca (
    cdServicoOrigem,
    dsNomeProduto,
    dsStatus,
    dtModificacao
)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Produto 001', 'ATIVO',   '2026-09-01 08:00:00.000000'),
    ('00000000-0000-0000-0000-000000000002', 'Produto 002', 'ATIVO',   '2026-09-01 08:00:00.000000'),
    ('00000000-0000-0000-0000-000000000003', 'Produto 003', 'ATIVO',   '2026-09-01 08:00:00.000000'),
    ('00000000-0000-0000-0000-000000000004', 'Produto 004', 'INATIVO', '2026-09-01 08:00:00.000000'),
    ('00000000-0000-0000-0000-000000000005', 'Produto 005', 'ATIVO',   '2026-09-01 08:00:00.000000')
    AS novo
ON DUPLICATE KEY UPDATE
                     dsNomeProduto = novo.dsNomeProduto,
                     dsStatus = novo.dsStatus,
                     dtModificacao = novo.dtModificacao;
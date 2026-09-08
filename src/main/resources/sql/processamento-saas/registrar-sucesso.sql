INSERT INTO processamento_saas (
    cdServicoOrigem,
    dtModificacaoProcessada,
    dtUltimoProcessamento,
    dtUltimoSucesso,
    dsStatusProcessamento,
    qtTentativas
)
VALUES (
       :cdServicoOrigem,
       :dtModificacaoProcessada,
       CURRENT_TIMESTAMP(6),
       CURRENT_TIMESTAMP(6),
       'SUCESSO',
       1
) AS novo

ON DUPLICATE KEY UPDATE
     qtTentativas =
     CASE
        WHEN processamento_saas.dtModificacaoProcessada
        <> novo.dtModificacaoProcessada
     THEN 1
        ELSE processamento_saas.qtTentativas + 1
     END,

    dtModificacaoProcessada =novo.dtModificacaoProcessada,
    dtUltimoProcessamento = novo.dtUltimoProcessamento,
    dtUltimoSucesso = novo.dtUltimoSucesso,
    dsStatusProcessamento = 'SUCESSO';
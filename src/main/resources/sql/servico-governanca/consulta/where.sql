WHERE
    p.cdServicoOrigem IS NULL
    OR p.dtModificacaoProcessada IS NULL
    OR s.dtModificacao <> p.dtModificacaoProcessada
    OR (
        p.dtUltimoSucesso IS NULL
        AND p.qtTentativas < :qtMaxTentativas
    )
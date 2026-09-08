FROM servico_governanca s
LEFT JOIN processamento_saas p
    ON p.cdServicoOrigem = s.cdServicoOrigem
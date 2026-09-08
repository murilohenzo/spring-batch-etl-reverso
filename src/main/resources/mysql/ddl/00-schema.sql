CREATE TABLE IF NOT EXISTS servico_governanca (
    cdServicoGovernanca BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    cdServicoOrigem     CHAR(36)        NOT NULL,
    dsNomeProduto       VARCHAR(255)    NOT NULL,
    dsStatus            VARCHAR(50)     NOT NULL,
    dtModificacao       DATETIME(6)     NOT NULL,
    dtCriacao           DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    dtAtualizacao       DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
    ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_servico_governanca
    PRIMARY KEY (cdServicoGovernanca),

    CONSTRAINT uk_servico_governanca_cd_servico_origem
    UNIQUE (cdServicoOrigem),

    INDEX ix_servico_governanca_dt_modificacao (dtModificacao)
);

CREATE TABLE IF NOT EXISTS processamento_saas (
    cdProcessamentoSaas       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    cdServicoOrigem           CHAR(36)        NOT NULL,
    dtModificacaoProcessada   DATETIME(6)     NULL,
    dtUltimoProcessamento     DATETIME(6)     NULL,
    dtUltimoSucesso           DATETIME(6)     NULL,
    dsStatusProcessamento     VARCHAR(30)     NOT NULL,
    qtTentativas              INT UNSIGNED    NOT NULL DEFAULT 0,

    CONSTRAINT pk_processamento_saas
    PRIMARY KEY (cdProcessamentoSaas),

    CONSTRAINT uk_processamento_saas_cd_servico_origem
    UNIQUE (cdServicoOrigem),

    CONSTRAINT fk_processamento_saas_servico
    FOREIGN KEY (cdServicoOrigem)
    REFERENCES servico_governanca (cdServicoOrigem)
);
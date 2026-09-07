-- Exclusivo do banco desta simulacao. Parar o batch antes de executar.
-- DELETE respeita a FK, e a transacao evita limpeza parcial.
START TRANSACTION;
DELETE FROM processamento_saas;
DELETE FROM servico_governanca;
COMMIT;
-- Metadados Batch e AUTO_INCREMENT sao preservados. Usar nova carga ao reiniciar.

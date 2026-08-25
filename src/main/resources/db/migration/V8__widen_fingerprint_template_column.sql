-- V8: Data iliyofichwa (encrypted) ni ndefu zaidi ya maandishi ya kawaida -
-- ongeza urefu wa column ili ibebe ciphertext nzima (IV + tag + data + Base64 overhead)
ALTER TABLE nida_mock_records MODIFY fingerprint_template VARCHAR(500);

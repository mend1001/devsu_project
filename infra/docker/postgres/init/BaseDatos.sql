-- =========================================
-- BaseDatos.sql
-- PostgreSQL
-- =========================================

-- =========================================
-- SCHEMAS
-- =========================================
CREATE SCHEMA IF NOT EXISTS customer_service;
CREATE SCHEMA IF NOT EXISTS account_service;

-- =========================================
-- CUSTOMER SERVICE
-- =========================================

CREATE TABLE customer_service.country (
    cou_id BIGSERIAL PRIMARY KEY,
    cou_name VARCHAR(100) NOT NULL,
    cou_iso2 VARCHAR(2) NOT NULL,
    cou_iso3 VARCHAR(3) NOT NULL,
    cou_numeric_code VARCHAR(3),
    cou_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    cou_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cou_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_country_cou_name UNIQUE (cou_name),
    CONSTRAINT uk_country_cou_iso2 UNIQUE (cou_iso2),
    CONSTRAINT uk_country_cou_iso3 UNIQUE (cou_iso3)
);

CREATE TABLE customer_service.country_phone_code (
    cpc_id BIGSERIAL PRIMARY KEY,
    cou_id BIGINT NOT NULL,
    cpc_phone_code VARCHAR(10) NOT NULL,
    cpc_label VARCHAR(50),
    cpc_is_default BOOLEAN NOT NULL DEFAULT FALSE,
    cpc_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    cpc_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cpc_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_country_phone_code_cou_id
        FOREIGN KEY (cou_id)
        REFERENCES customer_service.country (cou_id)
);

CREATE TABLE customer_service.role (
    rol_id BIGSERIAL PRIMARY KEY,
    rol_code VARCHAR(30) NOT NULL,
    rol_name VARCHAR(60) NOT NULL,
    rol_description VARCHAR(255),
    rol_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    rol_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    rol_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_rol_code UNIQUE (rol_code)
);

CREATE TABLE customer_service.person (
    per_id BIGSERIAL PRIMARY KEY,
    per_identification_type VARCHAR(20) NOT NULL,
    per_identification_number VARCHAR(30) NOT NULL,
    per_first_name VARCHAR(60) NOT NULL,
    per_middle_name VARCHAR(60),
    per_last_name VARCHAR(60) NOT NULL,
    per_second_last_name VARCHAR(60),
    per_full_name VARCHAR(180) NOT NULL,
    per_gender VARCHAR(20) NOT NULL,
    per_birth_date DATE,
    per_age INTEGER,
    per_email VARCHAR(120),
    per_phone_number VARCHAR(30),
    per_mobile_number VARCHAR(30),
    per_address_line_1 VARCHAR(150) NOT NULL,
    per_address_line_2 VARCHAR(150),
    cou_id BIGINT NOT NULL,
    cpc_id BIGINT,
    per_city VARCHAR(80),
    per_state_region VARCHAR(80),
    per_postal_code VARCHAR(20),
    per_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    per_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    per_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    per_created_by VARCHAR(60),
    per_updated_by VARCHAR(60),
    CONSTRAINT uk_person_per_identification_number UNIQUE (per_identification_number),
    CONSTRAINT uk_person_per_email UNIQUE (per_email),
    CONSTRAINT fk_person_cou_id
        FOREIGN KEY (cou_id)
        REFERENCES customer_service.country (cou_id),
    CONSTRAINT fk_person_cpc_id
        FOREIGN KEY (cpc_id)
        REFERENCES customer_service.country_phone_code (cpc_id),
    CONSTRAINT ck_person_per_age CHECK (per_age IS NULL OR per_age >= 0)
);

CREATE TABLE customer_service.client (
    cli_id BIGSERIAL PRIMARY KEY,
    per_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    cli_code VARCHAR(30) NOT NULL,
    cli_password_hash VARCHAR(255) NOT NULL,
    cli_password_salt VARCHAR(255),
    cli_status VARCHAR(20) NOT NULL,
    cli_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    cli_last_login_at TIMESTAMP,
    cli_failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    cli_is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    cli_locked_until TIMESTAMP,
    cli_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cli_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cli_created_by VARCHAR(60),
    cli_updated_by VARCHAR(60),
    CONSTRAINT uk_client_per_id UNIQUE (per_id),
    CONSTRAINT uk_client_cli_code UNIQUE (cli_code),
    CONSTRAINT fk_client_per_id
        FOREIGN KEY (per_id)
        REFERENCES customer_service.person (per_id),
    CONSTRAINT fk_client_rol_id
        FOREIGN KEY (rol_id)
        REFERENCES customer_service.role (rol_id),
    CONSTRAINT ck_client_cli_status
        CHECK (cli_status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'SUSPENDED'))
);

CREATE SEQUENCE customer_service.client_code_seq
START WITH 1
INCREMENT BY 1
MINVALUE 1
NO MAXVALUE
CACHE 10;

CREATE TABLE customer_service.user_session (
    uss_id BIGSERIAL PRIMARY KEY,
    cli_id BIGINT NOT NULL,
    uss_session_token VARCHAR(255) NOT NULL,
    uss_refresh_token VARCHAR(255),
    uss_login_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uss_last_activity_at TIMESTAMP,
    uss_expires_at TIMESTAMP NOT NULL,
    uss_ip_address VARCHAR(64),
    uss_user_agent VARCHAR(255),
    uss_device_name VARCHAR(120),
    uss_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    uss_closed_at TIMESTAMP,
    uss_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uss_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_session_uss_session_token UNIQUE (uss_session_token),
    CONSTRAINT uk_user_session_uss_refresh_token UNIQUE (uss_refresh_token),
    CONSTRAINT fk_user_session_cli_id
        FOREIGN KEY (cli_id)
        REFERENCES customer_service.client (cli_id)
);

CREATE TABLE customer_service.client_status_history (
    csh_id BIGSERIAL PRIMARY KEY,
    cli_id BIGINT NOT NULL,
    csh_old_status VARCHAR(20),
    csh_new_status VARCHAR(20) NOT NULL,
    csh_reason VARCHAR(255),
    csh_changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    csh_changed_by VARCHAR(60),
    CONSTRAINT fk_client_status_history_cli_id
        FOREIGN KEY (cli_id)
        REFERENCES customer_service.client (cli_id)
);

CREATE TABLE customer_service.client_event_outbox (
    ceo_id BIGSERIAL PRIMARY KEY,
    cli_id BIGINT NOT NULL,
    ceo_event_type VARCHAR(50) NOT NULL,
    ceo_payload JSONB NOT NULL,
    ceo_status VARCHAR(20) NOT NULL,
    ceo_retry_count INTEGER NOT NULL DEFAULT 0,
    ceo_next_retry_at TIMESTAMP,
    ceo_published_at TIMESTAMP,
    ceo_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ceo_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_event_outbox_cli_id
        FOREIGN KEY (cli_id)
        REFERENCES customer_service.client (cli_id),
    CONSTRAINT ck_client_event_outbox_ceo_status
        CHECK (ceo_status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

CREATE INDEX idx_country_phone_code_cou_id
    ON customer_service.country_phone_code (cou_id);

CREATE INDEX idx_person_cou_id
    ON customer_service.person (cou_id);

CREATE INDEX idx_person_cpc_id
    ON customer_service.person (cpc_id);

CREATE INDEX idx_client_rol_id
    ON customer_service.client (rol_id);

CREATE INDEX idx_user_session_cli_id
    ON customer_service.user_session (cli_id);

CREATE INDEX idx_client_status_history_cli_id
    ON customer_service.client_status_history (cli_id);

CREATE INDEX idx_client_event_outbox_cli_id
    ON customer_service.client_event_outbox (cli_id);

CREATE INDEX idx_client_event_outbox_ceo_status
    ON customer_service.client_event_outbox (ceo_status);

-- =========================================
-- ACCOUNT SERVICE
-- =========================================

CREATE TABLE account_service.client_snapshot (
    cls_id BIGSERIAL PRIMARY KEY,
    cli_id BIGINT NOT NULL,
    per_id BIGINT,
    rol_id BIGINT,
    cls_client_code VARCHAR(30),
    cls_full_name VARCHAR(180) NOT NULL,
    cls_identification_number VARCHAR(30),
    cls_email VARCHAR(120),
    cls_phone_number VARCHAR(30),
    cls_status VARCHAR(20) NOT NULL,
    cls_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    cls_source_event VARCHAR(50),
    cls_last_event_at TIMESTAMP,
    cls_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cls_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_client_snapshot_cli_id UNIQUE (cli_id)
);

CREATE TABLE account_service.account_type (
    act_id BIGSERIAL PRIMARY KEY,
    act_code VARCHAR(20) NOT NULL,
    act_name VARCHAR(50) NOT NULL,
    act_description VARCHAR(255),
    act_allows_overdraft BOOLEAN NOT NULL DEFAULT FALSE,
    act_default_currency VARCHAR(3) NOT NULL DEFAULT 'COP',
    act_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    act_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    act_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_account_type_act_code UNIQUE (act_code)
);

CREATE TABLE account_service.transaction_channel (
    tch_id BIGSERIAL PRIMARY KEY,
    tch_code VARCHAR(20) NOT NULL,
    tch_name VARCHAR(50) NOT NULL,
    tch_description VARCHAR(255),
    tch_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    tch_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tch_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_transaction_channel_tch_code UNIQUE (tch_code)
);

CREATE TABLE account_service.movement_type (
    mvt_id BIGSERIAL PRIMARY KEY,
    mvt_code VARCHAR(30) NOT NULL,
    mvt_name VARCHAR(50) NOT NULL,
    mvt_sign SMALLINT NOT NULL,
    mvt_affects_balance BOOLEAN NOT NULL DEFAULT TRUE,
    mvt_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    mvt_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mvt_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_movement_type_mvt_code UNIQUE (mvt_code),
    CONSTRAINT ck_movement_type_mvt_sign CHECK (mvt_sign IN (-1, 1))
);

CREATE TABLE account_service.account (
    acc_id BIGSERIAL PRIMARY KEY,
    cli_id BIGINT NOT NULL,
    act_id BIGINT NOT NULL,
    acc_number VARCHAR(20) NOT NULL,
    acc_iban VARCHAR(34),
    acc_currency_code VARCHAR(3) NOT NULL DEFAULT 'COP',
    acc_opened_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acc_closed_at TIMESTAMP,
    acc_status VARCHAR(20) NOT NULL,
    acc_available_balance NUMERIC(18,2) NOT NULL DEFAULT 0,
    acc_current_balance NUMERIC(18,2) NOT NULL DEFAULT 0,
    acc_initial_balance NUMERIC(18,2) NOT NULL DEFAULT 0,
    acc_blocked_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    acc_overdraft_limit NUMERIC(18,2) NOT NULL DEFAULT 0,
    acc_interest_rate NUMERIC(8,4),
    acc_monthly_fee NUMERIC(18,2) NOT NULL DEFAULT 0,
    acc_minimum_balance NUMERIC(18,2) NOT NULL DEFAULT 0,
    acc_statement_day SMALLINT,
    acc_branch_code VARCHAR(20),
    acc_alias VARCHAR(50),
    acc_is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    acc_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    acc_version BIGINT NOT NULL DEFAULT 0,
    acc_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acc_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acc_created_by VARCHAR(60),
    acc_updated_by VARCHAR(60),
    CONSTRAINT uk_account_acc_number UNIQUE (acc_number),
    CONSTRAINT uk_account_acc_iban UNIQUE (acc_iban),
    CONSTRAINT fk_account_act_id
        FOREIGN KEY (act_id)
        REFERENCES account_service.account_type (act_id),
    CONSTRAINT ck_account_acc_status
        CHECK (acc_status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'CLOSED')),
    CONSTRAINT ck_account_acc_available_balance CHECK (acc_available_balance >= 0),
    CONSTRAINT ck_account_acc_current_balance CHECK (acc_current_balance >= 0),
    CONSTRAINT ck_account_acc_initial_balance CHECK (acc_initial_balance >= 0),
    CONSTRAINT ck_account_acc_blocked_amount CHECK (acc_blocked_amount >= 0),
    CONSTRAINT ck_account_acc_overdraft_limit CHECK (acc_overdraft_limit >= 0),
    CONSTRAINT ck_account_acc_monthly_fee CHECK (acc_monthly_fee >= 0),
    CONSTRAINT ck_account_acc_minimum_balance CHECK (acc_minimum_balance >= 0),
    CONSTRAINT ck_account_acc_statement_day
        CHECK (acc_statement_day IS NULL OR acc_statement_day BETWEEN 1 AND 31)
);

CREATE TABLE account_service.account_holder (
    ach_id BIGSERIAL PRIMARY KEY,
    acc_id BIGINT NOT NULL,
    cli_id BIGINT NOT NULL,
    ach_holder_type VARCHAR(20) NOT NULL,
    ach_is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    ach_is_active BOOLEAN NOT NULL DEFAULT TRUE,
    ach_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ach_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_holder_acc_id
        FOREIGN KEY (acc_id)
        REFERENCES account_service.account (acc_id),
    CONSTRAINT ck_account_holder_ach_holder_type
        CHECK (ach_holder_type IN ('OWNER', 'CO_OWNER', 'AUTHORIZED'))
);

CREATE TABLE account_service.movement (
    mov_id BIGSERIAL PRIMARY KEY,
    acc_id BIGINT NOT NULL,
    mvt_id BIGINT NOT NULL,
    tch_id BIGINT,
    mov_reference VARCHAR(40) NOT NULL,
    mov_external_reference VARCHAR(60),
    mov_description VARCHAR(255),
    mov_transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mov_posted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mov_amount NUMERIC(18,2) NOT NULL,
    mov_previous_balance NUMERIC(18,2) NOT NULL,
    mov_available_balance NUMERIC(18,2) NOT NULL,
    mov_currency_code VARCHAR(3) NOT NULL DEFAULT 'COP',
    mov_status VARCHAR(20) NOT NULL,
    mov_is_reverted BOOLEAN NOT NULL DEFAULT FALSE,
    mov_reverted_at TIMESTAMP,
    mov_parent_movement_id BIGINT,
    mov_notes VARCHAR(255),
    mov_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mov_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mov_created_by VARCHAR(60),
    mov_updated_by VARCHAR(60),
    CONSTRAINT uk_movement_mov_reference UNIQUE (mov_reference),
    CONSTRAINT fk_movement_acc_id
        FOREIGN KEY (acc_id)
        REFERENCES account_service.account (acc_id),
    CONSTRAINT fk_movement_mvt_id
        FOREIGN KEY (mvt_id)
        REFERENCES account_service.movement_type (mvt_id),
    CONSTRAINT fk_movement_tch_id
        FOREIGN KEY (tch_id)
        REFERENCES account_service.transaction_channel (tch_id),
    CONSTRAINT fk_movement_mov_parent_movement_id
        FOREIGN KEY (mov_parent_movement_id)
        REFERENCES account_service.movement (mov_id),
    CONSTRAINT ck_movement_mov_status
        CHECK (mov_status IN ('PENDING', 'POSTED', 'REJECTED', 'REVERSED'))
);

CREATE TABLE account_service.account_status_history (
    ash_id BIGSERIAL PRIMARY KEY,
    acc_id BIGINT NOT NULL,
    ash_old_status VARCHAR(20),
    ash_new_status VARCHAR(20) NOT NULL,
    ash_reason VARCHAR(255),
    ash_changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ash_changed_by VARCHAR(60),
    CONSTRAINT fk_account_status_history_acc_id
        FOREIGN KEY (acc_id)
        REFERENCES account_service.account (acc_id)
);

CREATE TABLE account_service.account_statement_request (
    asr_id BIGSERIAL PRIMARY KEY,
    cli_id BIGINT NOT NULL,
    asr_request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    asr_start_date DATE NOT NULL,
    asr_end_date DATE NOT NULL,
    asr_status VARCHAR(20) NOT NULL,
    asr_response_format VARCHAR(20) NOT NULL DEFAULT 'JSON',
    asr_requested_by VARCHAR(60),
    asr_generated_at TIMESTAMP,
    asr_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    asr_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_account_statement_request_asr_status
        CHECK (asr_status IN ('REQUESTED', 'GENERATED', 'FAILED')),
    CONSTRAINT ck_account_statement_request_asr_response_format
        CHECK (asr_response_format IN ('JSON', 'PDF', 'CSV')),
    CONSTRAINT ck_account_statement_request_date_range
        CHECK (asr_end_date >= asr_start_date)
);

CREATE TABLE account_service.movement_event_outbox (
    meo_id BIGSERIAL PRIMARY KEY,
    mov_id BIGINT NOT NULL,
    meo_event_type VARCHAR(50) NOT NULL,
    meo_payload JSONB NOT NULL,
    meo_status VARCHAR(20) NOT NULL,
    meo_retry_count INTEGER NOT NULL DEFAULT 0,
    meo_next_retry_at TIMESTAMP,
    meo_published_at TIMESTAMP,
    meo_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    meo_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movement_event_outbox_mov_id
        FOREIGN KEY (mov_id)
        REFERENCES account_service.movement (mov_id),
    CONSTRAINT ck_movement_event_outbox_meo_status
        CHECK (meo_status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

CREATE INDEX idx_account_cli_id
    ON account_service.account (cli_id);

CREATE INDEX idx_account_act_id
    ON account_service.account (act_id);

CREATE INDEX idx_account_holder_acc_id
    ON account_service.account_holder (acc_id);

CREATE INDEX idx_account_holder_cli_id
    ON account_service.account_holder (cli_id);

CREATE INDEX idx_movement_acc_id
    ON account_service.movement (acc_id);

CREATE INDEX idx_movement_mvt_id
    ON account_service.movement (mvt_id);

CREATE INDEX idx_movement_tch_id
    ON account_service.movement (tch_id);

CREATE INDEX idx_movement_mov_transaction_date
    ON account_service.movement (mov_transaction_date);

CREATE INDEX idx_account_status_history_acc_id
    ON account_service.account_status_history (acc_id);

CREATE INDEX idx_account_statement_request_cli_id
    ON account_service.account_statement_request (cli_id);

CREATE INDEX idx_movement_event_outbox_mov_id
    ON account_service.movement_event_outbox (mov_id);

CREATE INDEX idx_movement_event_outbox_meo_status
    ON account_service.movement_event_outbox (meo_status);

-- =========================================
-- SEED DATA
-- =========================================

INSERT INTO customer_service.country (
    cou_name, cou_iso2, cou_iso3, cou_numeric_code
) VALUES
('Colombia', 'CO', 'COL', '170'),
('Ecuador', 'EC', 'ECU', '218'),
('México', 'MX', 'MEX', '484');

INSERT INTO customer_service.country_phone_code (
    cou_id, cpc_phone_code, cpc_label, cpc_is_default
)
SELECT cou_id, '+57', 'Colombia', TRUE
FROM customer_service.country
WHERE cou_iso2 = 'CO';

INSERT INTO customer_service.country_phone_code (
    cou_id, cpc_phone_code, cpc_label, cpc_is_default
)
SELECT cou_id, '+593', 'Ecuador', TRUE
FROM customer_service.country
WHERE cou_iso2 = 'EC';

INSERT INTO customer_service.country_phone_code (
    cou_id, cpc_phone_code, cpc_label, cpc_is_default
)
SELECT cou_id, '+52', 'México', TRUE
FROM customer_service.country
WHERE cou_iso2 = 'MX';

INSERT INTO customer_service.role (
    rol_code, rol_name, rol_description
) VALUES
('CLIENT', 'Cliente', 'Cliente estándar bancario'),
('ADMIN', 'Administrador', 'Administrador del sistema'),
('SUPPORT', 'Soporte', 'Usuario de soporte operativo');

INSERT INTO account_service.account_type (
    act_code, act_name, act_description, act_allows_overdraft, act_default_currency
) VALUES
('SAVINGS', 'Cuenta de Ahorros', 'Cuenta bancaria de ahorros', FALSE, 'COP'),
('CHECKING', 'Cuenta Corriente', 'Cuenta bancaria corriente', TRUE, 'COP');

INSERT INTO account_service.transaction_channel (
    tch_code, tch_name, tch_description
) VALUES
('BRANCH', 'Oficina', 'Transacción realizada en oficina'),
('ATM', 'Cajero', 'Transacción realizada en cajero automático'),
('WEB', 'Web', 'Transacción realizada por banca web'),
('MOBILE', 'Móvil', 'Transacción realizada por banca móvil'),
('API', 'API', 'Transacción realizada por integración de sistema');

INSERT INTO account_service.movement_type (
    mvt_code, mvt_name, mvt_sign, mvt_affects_balance
) VALUES
('DEPOSIT', 'Depósito', 1, TRUE),
('WITHDRAWAL', 'Retiro', -1, TRUE),
('TRANSFER_IN', 'Transferencia Entrante', 1, TRUE),
('TRANSFER_OUT', 'Transferencia Saliente', -1, TRUE),
('ADJUSTMENT_CREDIT', 'Ajuste Crédito', 1, TRUE),
('ADJUSTMENT_DEBIT', 'Ajuste Débito', -1, TRUE);

INSERT INTO customer_service.person
(
per_id,
per_identification_type,
per_identification_number,
per_first_name,
per_middle_name,
per_last_name,
per_second_last_name,
per_full_name,
per_gender,
per_email,
per_phone_number,
per_mobile_number,
per_address_line_1,
per_address_line_2,
cou_id,
cpc_id,
per_city,
per_state_region,
per_postal_code,
per_is_active,
per_created_at,
per_updated_at,
per_created_by,
per_updated_by
)
VALUES
(
1,
'CC',
'123456789',
'Miguel',
'Angel',
'Mendigano',
'Arismendy',
'Miguel Angel Mendigano Arismendy',
'MALE',
'miguel@test.com',
'6010000000',
'3000000000',
'Calle 1 #10-20',
NULL,
1,
1,
'Bogota',
'Cundinamarca',
'110111',
true,
now(),
now(),
'SYSTEM',
'SYSTEM'
),
(
2,
'CC',
'987654321',
'Ana',
'Maria',
'Lopez',
'Diaz',
'Ana Maria Lopez Diaz',
'FEMALE',
'ana@test.com',
'6011111111',
'3001111111',
'Carrera 20 #30-40',
NULL,
1,
1,
'Medellin',
'Antioquia',
'050001',
true,
now(),
now(),
'SYSTEM',
'SYSTEM'
);

INSERT INTO customer_service.client
(
cli_id,
per_id,
rol_id,
cli_code,
cli_password_hash,
cli_password_salt,
cli_status,
cli_is_active,
cli_failed_login_attempts,
cli_is_locked,
cli_created_at,
cli_updated_at,
cli_created_by,
cli_updated_by
)
VALUES
(
1,
1,
1,
'CLI001',
'1234',
NULL,
'ACTIVE',
true,
0,
false,
now(),
now(),
'SYSTEM',
'SYSTEM'
),
(
2,
2,
1,
'CLI002',
'1234',
NULL,
'ACTIVE',
true,
0,
false,
now(),
now(),
'SYSTEM',
'SYSTEM'
);
SELECT setval(
    'customer_service.person_per_id_seq',
    (SELECT COALESCE(MAX(per_id), 1) FROM customer_service.person),
    true
);

SELECT setval(
    'customer_service.client_cli_id_seq',
    (SELECT COALESCE(MAX(cli_id), 1) FROM customer_service.client),
    true
);

SELECT setval(
    'customer_service.country_cou_id_seq',
    (SELECT COALESCE(MAX(cou_id), 1) FROM customer_service.country),
    true
);

SELECT setval(
    'customer_service.country_phone_code_cpc_id_seq',
    (SELECT COALESCE(MAX(cpc_id), 1) FROM customer_service.country_phone_code),
    true
);

SELECT setval(
    'customer_service.role_rol_id_seq',
    (SELECT COALESCE(MAX(rol_id), 1) FROM customer_service.role),
    true
);

SELECT setval(
    'customer_service.client_cli_id_seq',
    (SELECT COALESCE(MAX(cli_id), 1) FROM customer_service.client),
    true
);

SELECT setval(
    'customer_service.country_cou_id_seq',
    (SELECT COALESCE(MAX(cou_id), 1) FROM customer_service.country),
    true
);

SELECT setval(
    'customer_service.country_phone_code_cpc_id_seq',
    (SELECT COALESCE(MAX(cpc_id), 1) FROM customer_service.country_phone_code),
    true
);

SELECT setval(
    'customer_service.role_rol_id_seq',
    (SELECT COALESCE(MAX(rol_id), 1) FROM customer_service.role),
    true
);

SELECT setval(
    'customer_service.person_per_id_seq',
    (SELECT COALESCE(MAX(per_id), 1) FROM customer_service.person),
    true
);

SELECT setval(
    'customer_service.client_cli_id_seq',
    (SELECT COALESCE(MAX(cli_id), 1) FROM customer_service.client),
    true
);

SELECT setval(
    'customer_service.user_session_uss_id_seq',
    (SELECT COALESCE(MAX(uss_id), 1) FROM customer_service.user_session),
    true
);

SELECT setval(
    'customer_service.client_status_history_csh_id_seq',
    (SELECT COALESCE(MAX(csh_id), 1) FROM customer_service.client_status_history),
    true
);

SELECT setval(
    'customer_service.client_event_outbox_ceo_id_seq',
    (SELECT COALESCE(MAX(ceo_id), 1) FROM customer_service.client_event_outbox),
    true
);

ALTER TABLE customer_service.client
ADD CONSTRAINT uk_client_code UNIQUE (cli_code);
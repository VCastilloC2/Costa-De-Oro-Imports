USE db_beer;

-- Insertar roles
INSERT INTO rol (rol_id, rol) VALUES
(1, 'ADMIN'),
(2, 'INVITADO'),
(3, 'PERSONA_CONTACTO'),
(4, 'PERSONA_JURIDICA'),
(5, 'PERSONA_NATURAL'),
(6, 'PROVEEDOR');

-- Insertar empresa para Theresa
INSERT INTO empresa (
    empresa_id, activo, nit, telefono, correo, razon_social, ciudad, direccion, imagen, e_sector
) VALUES (
     1, TRUE, '9876543210', '3204569875', 'aurum@tech.com', 'Aurum Tech',
     'Cartagena', 'Barrio Mz40 Lt70 Et4', 'gestion-ventas/perfil-empresa/perfil-empresa', 'BAR'
 );

-- Insertar usuario Admin (Jose)
INSERT INTO usuario (
    usuario_id, tipo_identificacion, numero_identificacion, imagen, nombres, apellidos,
    telefono, correo, password, rol_id, account_non_expired, account_non_locked,
    credentials_non_expired, is_enabled
) VALUES (
    1, 'CC', '123456789', 'gestion-ventas/perfil-usuario/perfil-user', 'Jose Andres', 'Torres Diaz',
    '310 2233445', 'jose@mail.com',
    '$2a$10$rj3PmRqB76o2VrobVRdCf.s2Q4S3HDnvVHeAmi8Uxdp.GWrLoqiMq',
    (SELECT rol_id FROM rol WHERE rol = 'ADMIN'), TRUE, TRUE, TRUE, TRUE
);

-- Insertar usuario Persona Contacto (Theresa) con empresa
INSERT INTO usuario (
    usuario_id, tipo_identificacion, numero_identificacion, imagen, nombres, apellidos,
    telefono, correo, password, rol_id, empresa_id, account_non_expired,
    account_non_locked, credentials_non_expired, is_enabled
) VALUES (
    2, 'CC', '987654321', 'gestion-ventas/perfil-usuario/perfil-user', 'Theresa Andrea', 'Torres Diaz',
    '320 2233445', 'theresa@mail.com',
    '$2a$10$rj3PmRqB76o2VrobVRdCf.s2Q4S3HDnvVHeAmi8Uxdp.GWrLoqiMq',
    (SELECT rol_id FROM rol WHERE rol = 'PERSONA_CONTACTO'),
    (SELECT empresa_id FROM empresa WHERE empresa_id = 1), TRUE, TRUE, TRUE, TRUE
);

-- Insertar usuario Persona Natural (Elysia)
INSERT INTO usuario (
    usuario_id, tipo_identificacion, numero_identificacion, imagen, nombres, apellidos,
    telefono, correo, password, rol_id, account_non_expired, account_non_locked,
    credentials_non_expired, is_enabled
) VALUES (
    3, 'CC', '1234567890', 'gestion-ventas/perfil-usuario/perfil-user', 'Elysia Andrea', 'Torres Diaz',
    '330 2233445', 'elysia@mail.com',
    '$2a$10$rj3PmRqB76o2VrobVRdCf.s2Q4S3HDnvVHeAmi8Uxdp.GWrLoqiMq',
    (SELECT rol_id FROM rol WHERE rol = 'PERSONA_NATURAL'), TRUE, TRUE, TRUE, TRUE
);
-- Datos de prueba para bd_tp_oo2.

CREATE DATABASE IF NOT EXISTS bd_tp_oo2;
USE bd_tp_oo2;

START TRANSACTION;

-- Usuarios de prueba: administrador, responsables y empleados.
INSERT INTO usuario (id, nombre, email, contrasenia, rol, activo)
VALUES
    (1, 'admin', 'admin@epicentrogourmet.com', '$2a$10$qcfF/FnU8ksf4NBMsTvx/evJqEq9zkLpU4NxdZThViRBS0acXCOZK', 'ADMINISTRADOR', 1),
    (2, 'responsable.foodtruck', 'responsable.foodtruck@epicentrogourmet.com', '$2a$10$4TQHA0ubcvTAunvORRRY6uboENMg4jYx6XOEPKkw/UyXSVs0mnJ3G', 'RESPONSABLE', 1),
    (3, 'responsable.puesto', 'responsable.puesto@epicentrogourmet.com', '$2a$10$eDHGMYpECwPyTlYc6GdlT.S1WTrPUaZ1WrdmuIHze9KDrcNchscWS', 'RESPONSABLE', 1),
    (4, 'cajero.demo', 'cajero.demo@epicentrogourmet.com', '$2a$10$etE6gPLmbc58nTQ17.2Oj.9bdOKMV.VL6tYZXamDxrg8TOnjvxW1S', 'EMPLEADO', 1),
    (5, 'cocinero.demo', 'cocinero.demo@epicentrogourmet.com', '$2a$10$awN/NSLzBXIYQ1DcITTIa.8nHojnK.Jn2Ah6/QSjIHMBPIKTMEjPK', 'EMPLEADO', 1);

-- Costos utilizados por los festivales.
INSERT INTO costo (
    id_costo,
    costo_por_superficie,
    costo_por_montaje,
    plus_uso_electricidad,
    sueldo_base,
    plus_cocinero,
    plus_cocinero_ayudante,
    plus_lavaplatos
)
VALUES (
    1,
    1500.00,
    8000.00,
    3500.00,
    250000.00,
    45000.00,
    30000.00,
    20000.00
);

-- Festival principal de prueba.
INSERT INTO festival (
    id_festival,
    nombre,
    temporada,
    fecha_inicio,
    fecha_fin,
    id_costo
)
VALUES (
    1,
    'Sabores de Primavera',
    'PRIMAVERA',
    '2026-10-24',
    '2026-10-26',
    1
);

-- Unidades de venta. La tabla base se carga antes que las tablas hijas.
INSERT INTO unidad_venta (
    id_unidad_venta,
    nombre_comercial,
    id_usuario,
    superficie,
    codigo_unico,
    id_festival
)
VALUES
    (1, 'Sabor Rodante', 2, 24, 'FT-001', 1),
    (2, 'La Esquina Gourmet', 3, 16, 'PD-001', 1);

INSERT INTO food_truck (
    id_unidad_venta,
    patente,
    conexion_electrica
)
VALUES (
    1,
    'AB123CD',
    1
);

INSERT INTO puesto_desarmable (
    id_unidad_venta,
    cantidad_carpas,
    tiempo_montaje
)
VALUES (
    2,
    2,
    90
);

-- Personal. Las credenciales apuntan a usuarios de rol EMPLEADO.
INSERT INTO personal (
    id_personal,
    nombre,
    apellido,
    dni,
    email,
    id_usuario_credenciales,
    fecha_nacimiento,
    fecha_ingreso,
    id_unidad_venta
)
VALUES
    (1, 'Lucía', 'Gómez', 30111222, 'lucia.gomez@epicentrogourmet.com', 4,
     '1990-05-12', '2026-01-15', 1),
    (2, 'Mateo', 'Fernández', 32999888, 'mateo.fernandez@epicentrogourmet.com', 5,
     '1988-09-23', '2026-02-01', 1);

INSERT INTO cajero (
    id_personal,
    turno_trabajo
)
VALUES (
    1,
    'TURNO_MANANA'
);

INSERT INTO cocinero (
    id_personal,
    especialidad_culinaria,
    categoria
)
VALUES (
    2,
    'Cocina regional',
    'COCINERO'
);

-- Platos ofrecidos por las unidades de venta.
INSERT INTO plato (
    id,
    nombre_plato,
    precio_venta,
    costo_produccion,
    id_unidad_venta
)
VALUES
    (1, 'Hamburguesa Criolla', 8500.00, 3200.00, 1),
    (2, 'Empanadas de Carne', 4500.00, 1600.00, 1),
    (3, 'Tacos de Verdura', 7000.00, 2500.00, 2);

-- Pedido con sus items.
INSERT INTO pedido (
    id,
    fecha_pedido,
    id_unidad_venta
)
VALUES (
    1,
    '2026-10-24',
    1
);

INSERT INTO detalle_pedido (
    id_detalle,
    id_plato,
    id_pedido,
    cantidad,
    subtotal
)
VALUES
    (1, 1, 1, 2, 17000.00),
    (2, 2, 1, 1, 4500.00);

COMMIT;
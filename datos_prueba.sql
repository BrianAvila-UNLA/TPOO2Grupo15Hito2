-- Datos de prueba para bd_tp_oo2.

CREATE DATABASE IF NOT EXISTS bd_tp_oo2;
USE bd_tp_oo2;

START TRANSACTION;

-- Usuarios de prueba: administrador, responsables y empleados.
INSERT INTO usuario (id, nombre, email, contrasenia, rol, activo)
VALUES
    (1, 'admin', 'admin@epicentrogourmet.com', '$2a$10$qcfF/FnU8ksf4NBMsTvx/evJqEq9zkLpU4NxdZThViRBS0acXCOZK', 'ADMINISTRADOR', 1),
    (2, 'Carolina Benítez', 'carolina.benitez@epicentrogourmet.com', '$2a$10$4TQHA0ubcvTAunvORRRY6uboENMg4jYx6XOEPKkw/UyXSVs0mnJ3G', 'RESPONSABLE', 1),
    (3, 'Santiago Morales', 'santiago.morales@epicentrogourmet.com', '$2a$10$eDHGMYpECwPyTlYc6GdlT.S1WTrPUaZ1WrdmuIHze9KDrcNchscWS', 'RESPONSABLE', 1),
    (4, 'Lucía', 'lucia.gomez@epicentrogourmet.com', '$2a$10$etE6gPLmbc58nTQ17.2Oj.9bdOKMV.VL6tYZXamDxrg8TOnjvxW1S', 'EMPLEADO', 1),
    (5, 'Mateo', 'mateo.fernandez@epicentrogourmet.com', '$2a$10$awN/NSLzBXIYQ1DcITTIa.8nHojnK.Jn2Ah6/QSjIHMBPIKTMEjPK', 'EMPLEADO', 1);

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
    150000.00,
    100000.00,
    50000.00
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
    id_festival,
    fecha_creacion
)
VALUES
    (1, 'Sabor Rodante', 2, 24, 'FT-001', 1, '2026-10-20'),
    (2, 'La Esquina Gourmet', 3, 16, 'PD-001', 1, '2026-10-20');

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
    eliminado,
    fecha_nacimiento,
    fecha_ingreso,
    id_unidad_venta
)
VALUES
    (1, 'Lucía', 'Gómez', 30111222, 'lucia.gomez@epicentrogourmet.com', 4, false,
     '1990-05-12', '2026-01-15', 1),
    (2, 'Mateo', 'Fernández', 32999888, 'mateo.fernandez@epicentrogourmet.com', 5, false,
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

-- Historial inicial de las asignaciones del personal a los festivales.
INSERT INTO asignacion_personal (
    id_personal,
    id_unidad_venta,
    id_festival,
    fecha_inicio,
    fecha_fin
)
SELECT
    p.id_personal,
    p.id_unidad_venta,
    uv.id_festival,
    f.fecha_inicio,
    NULL
FROM personal p
JOIN unidad_venta uv ON uv.id_unidad_venta = p.id_unidad_venta
JOIN festival f ON f.id_festival = uv.id_festival;

-- Platos ofrecidos por las unidades de venta.
INSERT INTO plato (
    id,
    nombre_plato,
    precio_venta,
    costo_produccion,
    id_unidad_venta,
    activo
)
VALUES
    (1, 'Hamburguesa Criolla', 8500.00, 3200.00, 1, TRUE),
    (2, 'Empanadas de Carne', 4500.00, 1600.00, 1, TRUE),
    (3, 'Tacos de Verdura', 7000.00, 2500.00, 2, TRUE),
    (4, 'Lomito Completo', 9800.00, 3900.00, 1, TRUE),
    (5, 'Papas Fritas Especiales', 5200.00, 1800.00, 1, TRUE),
    (6, 'Pollo Grillado', 9000.00, 3600.00, 1, TRUE),
    (7, 'Choripán Gourmet', 6500.00, 2400.00, 1, TRUE),
    (8, 'Pizza Individual', 7800.00, 2900.00, 1, TRUE),
    (9, 'Arepas Rellenas', 6200.00, 2200.00, 2, TRUE),
    (10, 'Milanesa con Papas', 8800.00, 3500.00, 2, TRUE),
    (11, 'Wok de Verduras', 7300.00, 2700.00, 2, TRUE),
    (12, 'Ravioles Caseros', 8200.00, 3100.00, 2, TRUE),
    (13, 'Tarta de Verduras', 5600.00, 1900.00, 2, TRUE);

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
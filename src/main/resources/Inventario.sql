/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  azulc
 * Created: 20 jun 2026
 */

CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'Activo' -- Para manejar los estados del ComboBox
);

ALTER TABLE productos 
ADD COLUMN categoria_id INTEGER REFERENCES categorias(id) ON DELETE SET NULL;


CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Activo'
);

CREATE TABLE movimientos_inventario (
    id SERIAL PRIMARY KEY,
    producto_id INT REFERENCES productos(id) ON DELETE CASCADE,
    tipo_movimiento VARCHAR(20) NOT NULL, -- 'ENTRADA' o 'SALIDA'
    cantidad INT NOT NULL,
    motivo VARCHAR(255),
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

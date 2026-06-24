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

CREATE TABLE public.proveedores (
    id serial NOT NULL,
    nombre character varying(100) NOT NULL,
    contacto character varying(100), -- Nombre de la persona de contacto
    telefono character varying(20),
    email character varying(100),
    estado character varying(20) NOT NULL DEFAULT 'ACTIVO', -- 'ACTIVO' o 'INACTIVO'
    PRIMARY KEY (id)
);

-- OPCIONAL: Para relacionarlo con tus productos existentes
ALTER TABLE public.productos 
ADD COLUMN proveedor_id integer REFERENCES public.proveedores(id) ON DELETE SET NULL;

CREATE TABLE public.clientes (
    id serial NOT NULL,
    nombre character varying(100) NOT NULL,
    rfc character varying(20), -- O identificación fiscal según tu país
    telefono character varying(20),
    email character varying(100),
    direccion character varying(200),
    estado character varying(20) NOT NULL DEFAULT 'ACTIVO', -- 'ACTIVO' o 'INACTIVO'
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.clientes
    OWNER to postgres;


-- 1. Tabla Cabecera de la Venta
CREATE TABLE public.ventas (
    id serial NOT NULL,
    cliente_id integer NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    total numeric(10,2) NOT NULL,
    estado character varying(20) NOT NULL DEFAULT 'COMPLETADA', -- 'COMPLETADA' o 'ANULADA'
    PRIMARY KEY (id),
    CONSTRAINT fk_ventas_cliente FOREIGN KEY (cliente_id)
        REFERENCES public.clientes (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- 2. Tabla Detalle de la Venta (Los renglones del carrito)
CREATE TABLE public.detalle_ventas (
    id serial NOT NULL,
    venta_id integer NOT NULL,
    producto_id integer NOT NULL,
    cantidad integer NOT NULL,
    precio_unitario numeric(10,2) NOT NULL,
    subtotal numeric(10,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id)
        REFERENCES public.ventas (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE, -- Si se borra la venta, se borra su detalle
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id)
        REFERENCES public.productos (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

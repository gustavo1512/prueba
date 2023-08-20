
Create Database sireho;
Use sireho;

-- Create jhi_user table
CREATE TABLE jhi_user (
    id BIGINT PRIMARY KEY IDENTITY(1050, 1),
    login VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(60),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(191) UNIQUE,
    image_url VARCHAR(256),
    activated BIT NOT NULL DEFAULT 0,
    lang_key VARCHAR(10),
    activation_key VARCHAR(20),
    reset_key VARCHAR(20),
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    reset_date TIMESTAMP NULL,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP
);


-- Create jhi_authority table
CREATE TABLE jhi_authority (
    name VARCHAR(50) PRIMARY KEY NOT NULL
);

-- Create jhi_user_authority table
CREATE TABLE jhi_user_authority (
    user_id BIGINT NOT NULL,
    authority_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, authority_name),
    FOREIGN KEY (authority_name) REFERENCES jhi_authority(name),
    FOREIGN KEY (user_id) REFERENCES jhi_user(id)
);

-- Crear tabla Cliente
CREATE TABLE Cliente (
    id INT PRIMARY KEY,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    direccion VARCHAR(255),
    correo VARCHAR(60),
    telefono VARCHAR(50)
);

-- Crear tabla Tarifa
CREATE TABLE Tarifa (
    id INT PRIMARY KEY,
    tipoTarifa VARCHAR(50),
    tarifaAdulto FLOAT,
    tarifaMenor FLOAT
);

-- Crear tabla Habitacion
CREATE TABLE Habitacion (
    id INT PRIMARY KEY,
    tipo VARCHAR(50),
    capacidadAdulto BIGINT,
    capacidadMenor BIGINT,
    tarifaId INT, -- FK tarifa
    disponible BIT
);

-- Crear tabla TipoCargo
CREATE TABLE TipoCargo (
    id INT PRIMARY KEY,
    nombreCargo VARCHAR(50),
    precioHora FLOAT
);

-- Crear tabla Colaborador
CREATE TABLE Colaborador (
    id INT PRIMARY KEY,
    tipoCargoId INT, -- FK TipoCargo
    nombreColaborador VARCHAR(50),
    numTelefono BIGINT,
    correo VARCHAR(255)
);

-- Crear tabla Persona
CREATE TABLE Persona (
    id INT PRIMARY KEY,
    fechaNacimiento DATETIME,
    tipo VARCHAR(100)
);

-- Crear tabla ReservarHabitacion
CREATE TABLE ReservarHabitacion (
    id INT PRIMARY KEY,
    fechaReserva DATETIME,
    fechaInicio DATETIME,
    fechaFinal DATETIME,
    personaId INT, -- FK Personas
    colaboradorId INT, -- FK Colaborador
    clienteId INT, -- FK Cliente
    habitacionId INT, -- FK Habitacion
    totalReservacion FLOAT
);

-- Crear tabla Evento
CREATE TABLE Evento (
    id INT PRIMARY KEY,
    nombreEvento VARCHAR(50),
    fechaHora DATETIME,
    colaboradorId INT, -- FK Colaborador ->persona que organiza el evento 
    capacidadAdulto BIGINT,
    capacidadMenor BIGINT,
    tarifaId INT -- FK Tarifa
);

-- Crear tabla ReservarEvento
CREATE TABLE ReservarEvento (
    id INT PRIMARY KEY,
    fechaReservacion DATETIME,
    personaId INT, -- FK Personas
    colaboradorId INT, -- FK Colaborador
    clienteId INT, -- FK Cliente
    eventoId INT, -- FK Evento
    totalReservacion FLOAT
);

-- Crear tabla DetalleFactura
CREATE TABLE DetalleFactura (
    id INT PRIMARY KEY,
    reservarEventoId INT, -- FK ReservarEvento
    reservarHabitacionId INT, -- FK ReservarHabitacion
    fechaEmitido BIGINT
);

-- Crear tabla Factura
CREATE TABLE Factura (
    id INT PRIMARY KEY,
    detalleFacturaId INT, -- FK DetalleFactura
    colaboradorId INT, -- FK Colaborador
    clienteId INT, -- FK Cliente
    metodoPago VARCHAR(50),
    subtotal FLOAT,
    montoTotal FLOAT,
    impuesto FLOAT
);

--------------------------------------------------------------------------------------
--PROCEDIMIENTOS ALMACENADOS DE CADA TABLA
--------------------------------------------------------------------------------------
--CREAR USUARIO
CREATE PROCEDURE sp_CrearUsuario
    @login VARCHAR(50),
    @password_hash VARCHAR(60),
    @first_name VARCHAR(50),
    @last_name VARCHAR(50),
    @email VARCHAR(191),
    @image_url VARCHAR(256),
    @activated BIT,
    @lang_key VARCHAR(10),
    @activation_key VARCHAR(20),
    @reset_key VARCHAR(20),
    @created_by VARCHAR(50),
    @created_date TIMESTAMP,
    @reset_date TIMESTAMP,
    @last_modified_by VARCHAR(50),
    @last_modified_date TIMESTAMP
AS
BEGIN
    INSERT INTO jhi_user (login, password_hash, first_name, last_name, email, image_url, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date)
    VALUES (@login, @password_hash, @first_name, @last_name, @email, @image_url, @activated, @lang_key, @activation_key, @reset_key, @created_by, @created_date, @reset_date, @last_modified_by, @last_modified_date);
END;

--MOSTRAR
CREATE PROCEDURE sp_ObtenerTodosUsuarios
AS
BEGIN
    SELECT * FROM jhi_user;
END;
--OBTENER UN REGISTRO
CREATE PROCEDURE sp_ObtenerUsuarioPorId
    @id BIGINT
AS
BEGIN
    SELECT * FROM jhi_user WHERE id = @id;
END;
--ACTUALIZAR
CREATE PROCEDURE sp_ActualizarUsuario
    @id BIGINT,
    @login VARCHAR(50),
    @password_hash VARCHAR(60),
    @first_name VARCHAR(50),
    @last_name VARCHAR(50),
    @email VARCHAR(191),
    @image_url VARCHAR(256),
    @activated BIT,
    @lang_key VARCHAR(10),
    @activation_key VARCHAR(20),
    @reset_key VARCHAR(20),
    @created_by VARCHAR(50),
    @created_date TIMESTAMP,
    @reset_date TIMESTAMP,
    @last_modified_by VARCHAR(50),
    @last_modified_date TIMESTAMP
AS
BEGIN
    UPDATE jhi_user
    SET login = @login,
        password_hash = @password_hash,
        first_name = @first_name,
        last_name = @last_name,
        email = @email,
        image_url = @image_url,
        activated = @activated,
        lang_key = @lang_key,
        activation_key = @activation_key,
        reset_key = @reset_key,
        created_by = @created_by,
        created_date = @created_date,
        reset_date = @reset_date,
        last_modified_by = @last_modified_by,
        last_modified_date = @last_modified_date
    WHERE id = @id;
END;

--ELIMINAR
CREATE PROCEDURE sp_EliminarUsuario
    @id BIGINT
AS
BEGIN
    DELETE FROM jhi_user WHERE id = @id;
END;

----------------------------------------------------------------------------------

-- Procedimiento almacenado para crear un registro en la tabla jhi_authority
CREATE PROCEDURE sp_CrearAuthority
    @name VARCHAR(50)
AS
BEGIN
    INSERT INTO jhi_authority (name)
    VALUES (@name);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla jhi_authority
CREATE PROCEDURE sp_ObtenerTodosAuthorities
AS
BEGIN
    SELECT * FROM jhi_authority;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla jhi_authority por su name
CREATE PROCEDURE sp_ObtenerAuthorityPorNombre
    @name VARCHAR(50)
AS
BEGIN
    SELECT * FROM jhi_authority WHERE name = @name;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla jhi_authority
CREATE PROCEDURE sp_ActualizarAuthority
    @name VARCHAR(50)
AS
BEGIN
    UPDATE jhi_authority
    SET name = @name
    WHERE name = @name;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla jhi_authority
CREATE PROCEDURE sp_EliminarAuthority
    @name VARCHAR(50)
AS
BEGIN
    DELETE FROM jhi_authority WHERE name = @name;
END;
-----------------------------------------------------------------------------------------
-- Procedimiento almacenado para crear un registro en la tabla jhi_user_authority
CREATE PROCEDURE sp_CrearUserAuthority
    @user_id BIGINT,
    @authority_name VARCHAR(50)
AS
BEGIN
    INSERT INTO jhi_user_authority (user_id, authority_name)
    VALUES (@user_id, @authority_name);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla jhi_user_authority
CREATE PROCEDURE sp_ObtenerTodosUserAuthorities
AS
BEGIN
    SELECT * FROM jhi_user_authority;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla jhi_user_authority por su user_id y authority_name
CREATE PROCEDURE sp_ObtenerUserAuthorityPorIdNombre
    @user_id BIGINT,
    @authority_name VARCHAR(50)
AS
BEGIN
    SELECT * FROM jhi_user_authority WHERE user_id = @user_id AND authority_name = @authority_name;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla jhi_user_authority
CREATE PROCEDURE sp_ActualizarUserAuthority
    @user_id BIGINT,
    @authority_name VARCHAR(50)
AS
BEGIN
    UPDATE jhi_user_authority
    SET authority_name = @authority_name
    WHERE user_id = @user_id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla jhi_user_authority
CREATE PROCEDURE sp_EliminarUserAuthority
    @user_id BIGINT,
    @authority_name VARCHAR(50)
AS
BEGIN
    DELETE FROM jhi_user_authority WHERE user_id = @user_id AND authority_name = @authority_name;
END;
----------------------------------------------
--CLIENTE SP
-- Procedimiento almacenado para crear un registro en la tabla Cliente
CREATE PROCEDURE sp_CrearCliente
    @id INT,
    @nombre VARCHAR(50),
    @apellido VARCHAR(50),
    @direccion VARCHAR(255),
    @correo VARCHAR(60),
    @telefono VARCHAR(50)
AS
BEGIN
    INSERT INTO Cliente (id, nombre, apellido, direccion, correo, telefono)
    VALUES (@id, @nombre, @apellido, @direccion, @correo, @telefono);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla Cliente
CREATE PROCEDURE sp_ObtenerTodosClientes
AS
BEGIN
    SELECT * FROM Cliente;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla Cliente por su id
CREATE PROCEDURE sp_ObtenerClientePorId
    @id INT
AS
BEGIN
    SELECT * FROM Cliente WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla Cliente
CREATE PROCEDURE sp_ActualizarCliente
    @id INT,
    @nombre VARCHAR(50),
    @apellido VARCHAR(50),
    @direccion VARCHAR(255),
    @correo VARCHAR(60),
    @telefono VARCHAR(50)
AS
BEGIN
    UPDATE Cliente
    SET nombre = @nombre,
        apellido = @apellido,
        direccion = @direccion,
        correo = @correo,
        telefono = @telefono
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla Cliente
CREATE PROCEDURE sp_EliminarCliente
    @id INT
AS
BEGIN
    DELETE FROM Cliente WHERE id = @id;
END;
------------------------------------------
--TARIFA SP
-- Procedimiento almacenado para crear un registro en la tabla Tarifa
CREATE PROCEDURE sp_CrearTarifa
    @id INT,
    @tipoTarifa VARCHAR(50),
    @tarifaAdulto FLOAT,
    @tarifaMenor FLOAT
AS
BEGIN
    INSERT INTO Tarifa (id, tipoTarifa, tarifaAdulto, tarifaMenor)
    VALUES (@id, @tipoTarifa, @tarifaAdulto, @tarifaMenor);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla Tarifa
CREATE PROCEDURE sp_ObtenerTodasTarifas
AS
BEGIN
    SELECT * FROM Tarifa;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla Tarifa por su id
CREATE PROCEDURE sp_ObtenerTarifaPorId
    @id INT
AS
BEGIN
    SELECT * FROM Tarifa WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla Tarifa
CREATE PROCEDURE sp_ActualizarTarifa
    @id INT,
    @tipoTarifa VARCHAR(50),
    @tarifaAdulto FLOAT,
    @tarifaMenor FLOAT
AS
BEGIN
    UPDATE Tarifa
    SET tipoTarifa = @tipoTarifa,
        tarifaAdulto = @tarifaAdulto,
        tarifaMenor = @tarifaMenor
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla Tarifa
CREATE PROCEDURE sp_EliminarTarifa
    @id INT
AS
BEGIN
    DELETE FROM Tarifa WHERE id = @id;
END;
--HABITACION
-- Procedimiento almacenado para crear un registro en la tabla Habitacion
CREATE PROCEDURE sp_CrearHabitacion
    @id INT,
    @tipo VARCHAR(50),
    @capacidadAdulto BIGINT,
    @capacidadMenor BIGINT,
    @tarifaId INT,
    @disponible BIT
AS
BEGIN
    INSERT INTO Habitacion (id, tipo, capacidadAdulto, capacidadMenor, tarifaId, disponible)
    VALUES (@id, @tipo, @capacidadAdulto, @capacidadMenor, @tarifaId, @disponible);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla Habitacion
CREATE PROCEDURE sp_ObtenerTodasHabitaciones
AS
BEGIN
    SELECT * FROM Habitacion;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla Habitacion por su id
CREATE PROCEDURE sp_ObtenerHabitacionPorId
    @id INT
AS
BEGIN
    SELECT * FROM Habitacion WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla Habitacion
CREATE PROCEDURE sp_ActualizarHabitacion
    @id INT,
    @tipo VARCHAR(50),
    @capacidadAdulto BIGINT,
    @capacidadMenor BIGINT,
    @tarifaId INT,
    @disponible BIT
AS
BEGIN
    UPDATE Habitacion
    SET tipo = @tipo,
        capacidadAdulto = @capacidadAdulto,
        capacidadMenor = @capacidadMenor,
        tarifaId = @tarifaId,
        disponible = @disponible
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla Habitacion
CREATE PROCEDURE sp_EliminarHabitacion
    @id INT
AS
BEGIN
    DELETE FROM Habitacion WHERE id = @id;
END;
---- TIPO CARGO SP
-- Procedimiento almacenado para crear un registro en la tabla TipoCargo
CREATE PROCEDURE sp_CrearTipoCargo
    @id INT,
    @nombreCargo VARCHAR(50),
    @precioHora FLOAT
AS
BEGIN
    INSERT INTO TipoCargo (id, nombreCargo, precioHora)
    VALUES (@id, @nombreCargo, @precioHora);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla TipoCargo
CREATE PROCEDURE sp_ObtenerTodosTipoCargos
AS
BEGIN
    SELECT * FROM TipoCargo;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla TipoCargo por su id
CREATE PROCEDURE sp_ObtenerTipoCargoPorId
    @id INT
AS
BEGIN
    SELECT * FROM TipoCargo WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla TipoCargo
CREATE PROCEDURE sp_ActualizarTipoCargo
    @id INT,
    @nombreCargo VARCHAR(50),
    @precioHora FLOAT
AS
BEGIN
    UPDATE TipoCargo
    SET nombreCargo = @nombreCargo,
        precioHora = @precioHora
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla TipoCargo
CREATE PROCEDURE sp_EliminarTipoCargo
    @id INT
AS
BEGIN
    DELETE FROM TipoCargo WHERE id = @id;
END;
--- COLABORADOR
-- Procedimiento almacenado para crear un registro en la tabla Colaborador
CREATE PROCEDURE sp_CrearColaborador
    @id INT,
    @tipoCargoId INT,
    @nombreColaborador VARCHAR(50),
    @numTelefono BIGINT,
    @correo VARCHAR(255)
AS
BEGIN
    INSERT INTO Colaborador (id, tipoCargoId, nombreColaborador, numTelefono, correo)
    VALUES (@id, @tipoCargoId, @nombreColaborador, @numTelefono, @correo);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla Colaborador
CREATE PROCEDURE sp_ObtenerTodosColaboradores
AS
BEGIN
    SELECT * FROM Colaborador;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla Colaborador por su id
CREATE PROCEDURE sp_ObtenerColaboradorPorId
    @id INT
AS
BEGIN
    SELECT * FROM Colaborador WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla Colaborador
CREATE PROCEDURE sp_ActualizarColaborador
    @id INT,
    @tipoCargoId INT,
    @nombreColaborador VARCHAR(50),
    @numTelefono BIGINT,
    @correo VARCHAR(255)
AS
BEGIN
    UPDATE Colaborador
    SET tipoCargoId = @tipoCargoId,
        nombreColaborador = @nombreColaborador,
        numTelefono = @numTelefono,
        correo = @correo
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla Colaborador
CREATE PROCEDURE sp_EliminarColaborador
    @id INT
AS
BEGIN
    DELETE FROM Colaborador WHERE id = @id;
END;
--- PERSONA SP
-- Procedimiento almacenado para crear un registro en la tabla Persona
CREATE PROCEDURE sp_CrearPersona
    @id INT,
    @fechaNacimiento DATETIME,
    @tipo VARCHAR(100)
AS
BEGIN
    INSERT INTO Persona (id, fechaNacimiento, tipo)
    VALUES (@id, @fechaNacimiento, @tipo);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla Persona
CREATE PROCEDURE sp_ObtenerTodosPersonas
AS
BEGIN
    SELECT * FROM Persona;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla Persona por su id
CREATE PROCEDURE sp_ObtenerPersonaPorId
    @id INT
AS
BEGIN
    SELECT * FROM Persona WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla Persona
CREATE PROCEDURE sp_ActualizarPersona
    @id INT,
    @fechaNacimiento DATETIME,
    @tipo VARCHAR(100)
AS
BEGIN
    UPDATE Persona
    SET fechaNacimiento = @fechaNacimiento,
        tipo = @tipo
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla Persona
CREATE PROCEDURE sp_EliminarPersona
    @id INT
AS
BEGIN
    DELETE FROM Persona WHERE id = @id;
END;
---RESERVAR HABITACION SP
-- Procedimiento almacenado para crear un registro en la tabla ReservarHabitacion
CREATE PROCEDURE sp_CrearReservarHabitacion
    @id INT,
    @fechaReserva DATETIME,
    @fechaInicio DATETIME,
    @fechaFinal DATETIME,
    @personaId INT,
    @colaboradorId INT,
    @clienteId INT,
    @habitacionId INT,
    @totalReservacion FLOAT
AS
BEGIN
    INSERT INTO ReservarHabitacion (id, fechaReserva, fechaInicio, fechaFinal, personaId, colaboradorId, clienteId, habitacionId, totalReservacion)
    VALUES (@id, @fechaReserva, @fechaInicio, @fechaFinal, @personaId, @colaboradorId, @clienteId, @habitacionId, @totalReservacion);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla ReservarHabitacion
CREATE PROCEDURE sp_ObtenerTodosReservarHabitacion
AS
BEGIN
    SELECT * FROM ReservarHabitacion;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla ReservarHabitacion por su id
CREATE PROCEDURE sp_ObtenerReservarHabitacionPorId
    @id INT
AS
BEGIN
    SELECT * FROM ReservarHabitacion WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla ReservarHabitacion
CREATE PROCEDURE sp_ActualizarReservarHabitacion
    @id INT,
    @fechaReserva DATETIME,
    @fechaInicio DATETIME,
    @fechaFinal DATETIME,
    @personaId INT,
    @colaboradorId INT,
    @clienteId INT,
    @habitacionId INT,
    @totalReservacion FLOAT
AS
BEGIN
    UPDATE ReservarHabitacion
    SET fechaReserva = @fechaReserva,
        fechaInicio = @fechaInicio,
        fechaFinal = @fechaFinal,
        personaId = @personaId,
        colaboradorId = @colaboradorId,
        clienteId = @clienteId,
        habitacionId = @habitacionId,
        totalReservacion = @totalReservacion
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla ReservarHabitacion
CREATE PROCEDURE sp_EliminarReservarHabitacion
    @id INT
AS
BEGIN
    DELETE FROM ReservarHabitacion WHERE id = @id;
END;
--- EVENTO SP
-- Procedimiento almacenado para crear un registro en la tabla Evento
CREATE PROCEDURE sp_CrearEvento
    @id INT,
    @nombreEvento VARCHAR(50),
    @fechaHora DATETIME,
    @colaboradorId INT,
    @capacidadAdulto BIGINT,
    @capacidadMenor BIGINT,
    @tarifaId INT
AS
BEGIN
    INSERT INTO Evento (id, nombreEvento, fechaHora, colaboradorId, capacidadAdulto, capacidadMenor, tarifaId)
    VALUES (@id, @nombreEvento, @fechaHora, @colaboradorId, @capacidadAdulto, @capacidadMenor, @tarifaId);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla Evento
CREATE PROCEDURE sp_ObtenerTodosEventos
AS
BEGIN
    SELECT * FROM Evento;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla Evento por su id
CREATE PROCEDURE sp_ObtenerEventoPorId
    @id INT
AS
BEGIN
    SELECT * FROM Evento WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla Evento
CREATE PROCEDURE sp_ActualizarEvento
    @id INT,
    @nombreEvento VARCHAR(50),
    @fechaHora DATETIME,
    @colaboradorId INT,
    @capacidadAdulto BIGINT,
    @capacidadMenor BIGINT,
    @tarifaId INT
AS
BEGIN
    UPDATE Evento
    SET nombreEvento = @nombreEvento,
        fechaHora = @fechaHora,
        colaboradorId = @colaboradorId,
        capacidadAdulto = @capacidadAdulto,
        capacidadMenor = @capacidadMenor,
        tarifaId = @tarifaId
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla Evento
CREATE PROCEDURE sp_EliminarEvento
    @id INT
AS
BEGIN
    DELETE FROM Evento WHERE id = @id;
END;
--- RESERVAR EVENTO SP
-- Procedimiento almacenado para crear un registro en la tabla ReservarEvento
CREATE PROCEDURE sp_CrearReservarEvento
    @id INT,
    @fechaReservacion DATETIME,
    @personaId INT,
    @colaboradorId INT,
    @clienteId INT,
    @eventoId INT,
    @totalReservacion FLOAT
AS
BEGIN
    INSERT INTO ReservarEvento (id, fechaReservacion, personaId, colaboradorId, clienteId, eventoId, totalReservacion)
    VALUES (@id, @fechaReservacion, @personaId, @colaboradorId, @clienteId, @eventoId, @totalReservacion);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla ReservarEvento
CREATE PROCEDURE sp_ObtenerTodosReservarEventos
AS
BEGIN
    SELECT * FROM ReservarEvento;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla ReservarEvento por su id
CREATE PROCEDURE sp_ObtenerReservarEventoPorId
    @id INT
AS
BEGIN
    SELECT * FROM ReservarEvento WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla ReservarEvento
CREATE PROCEDURE sp_ActualizarReservarEvento
    @id INT,
    @fechaReservacion DATETIME,
    @personaId INT,
    @colaboradorId INT,
    @clienteId INT,
    @eventoId INT,
    @totalReservacion FLOAT
AS
BEGIN
    UPDATE ReservarEvento
    SET fechaReservacion = @fechaReservacion,
        personaId = @personaId,
        colaboradorId = @colaboradorId,
        clienteId = @clienteId,
        eventoId = @eventoId,
        totalReservacion = @totalReservacion
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla ReservarEvento
CREATE PROCEDURE sp_EliminarReservarEvento
    @id INT
AS
BEGIN
    DELETE FROM ReservarEvento WHERE id = @id;
END;
-- DETALLE FACTURA
-- Procedimiento almacenado para crear un registro en la tabla DetalleFactura
CREATE PROCEDURE sp_CrearDetalleFactura
    @id INT,
    @reservarEventoId INT,
    @reservarHabitacionId INT,
    @fechaEmitido BIGINT
AS
BEGIN
    INSERT INTO DetalleFactura (id, reservarEventoId, reservarHabitacionId, fechaEmitido)
    VALUES (@id, @reservarEventoId, @reservarHabitacionId, @fechaEmitido);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla DetalleFactura
CREATE PROCEDURE sp_ObtenerTodosDetallesFactura
AS
BEGIN
    SELECT * FROM DetalleFactura;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla DetalleFactura por su id
CREATE PROCEDURE sp_ObtenerDetalleFacturaPorId
    @id INT
AS
BEGIN
    SELECT * FROM DetalleFactura WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla DetalleFactura
CREATE PROCEDURE sp_ActualizarDetalleFactura
    @id INT,
    @reservarEventoId INT,
    @reservarHabitacionId INT,
    @fechaEmitido BIGINT
AS
BEGIN
    UPDATE DetalleFactura
    SET reservarEventoId = @reservarEventoId,
        reservarHabitacionId = @reservarHabitacionId,
        fechaEmitido = @fechaEmitido
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla DetalleFactura
CREATE PROCEDURE sp_EliminarDetalleFactura
    @id INT
AS
BEGIN
    DELETE FROM DetalleFactura WHERE id = @id;
END;

---FACTURA
-- Procedimiento almacenado para crear un registro en la tabla Factura
CREATE PROCEDURE sp_CrearFactura
    @id INT,
    @detalleFacturaId INT,
    @colaboradorId INT,
    @clienteId INT,
    @metodoPago VARCHAR(50),
    @subtotal FLOAT,
    @montoTotal FLOAT,
    @impuesto FLOAT
AS
BEGIN
    INSERT INTO Factura (id, detalleFacturaId, colaboradorId, clienteId, metodoPago, subtotal, montoTotal, impuesto)
    VALUES (@id, @detalleFacturaId, @colaboradorId, @clienteId, @metodoPago, @subtotal, @montoTotal, @impuesto);
END;

-- Procedimiento almacenado para obtener todos los registros de la tabla Factura
CREATE PROCEDURE sp_ObtenerTodasFacturas
AS
BEGIN
    SELECT * FROM Factura;
END;

-- Procedimiento almacenado para obtener un registro específico de la tabla Factura por su id
CREATE PROCEDURE sp_ObtenerFacturaPorId
    @id INT
AS
BEGIN
    SELECT * FROM Factura WHERE id = @id;
END;

-- Procedimiento almacenado para actualizar un registro en la tabla Factura
CREATE PROCEDURE sp_ActualizarFactura
    @id INT,
    @detalleFacturaId INT,
    @colaboradorId INT,
    @clienteId INT,
    @metodoPago VARCHAR(50),
    @subtotal FLOAT,
    @montoTotal FLOAT,
    @impuesto FLOAT
AS
BEGIN
    UPDATE Factura
    SET detalleFacturaId = @detalleFacturaId,
        colaboradorId = @colaboradorId,
        clienteId = @clienteId,
        metodoPago = @metodoPago,
        subtotal = @subtotal,
        montoTotal = @montoTotal,
        impuesto = @impuesto
    WHERE id = @id;
END;

-- Procedimiento almacenado para eliminar un registro de la tabla Factura
CREATE PROCEDURE sp_EliminarFactura
    @id INT
AS
BEGIN
    DELETE FROM Factura WHERE id = @id;
END;
-------------------------------------------------------------------------------
--INSERTS
INSERT INTO jhi_user_authority (user_id, authority_name)
VALUES 
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER')

	insert into jhi_authority VALUES ('ROLE_ADMIN')
insert into jhi_authority VALUES ('ROLE_USER')

INSERT INTO jhi_user (id,login, password_hash, first_name, last_name, email, image_url, activated, lang_key, activation_key, created_by, created_date, last_modified_by, last_modified_date)
VALUES 
    (1,'admin', 'admin', 'Nombre1', 'Administrador', 'admin@localhost', 'url_imagen1', 1, 'es', 'clave_activacion1', 'admin', GETDATE(), 'admin', GETDATE()),
	 (2,'user', 'user', 'User', 'User', 'user@localhost', 'url_imagen1', 1, 'es', 'clave_activacion1', 'user', GETDATE(), 'user', GETDATE())

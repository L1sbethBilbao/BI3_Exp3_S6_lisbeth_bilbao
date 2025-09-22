#Creacion de base de datos
CREATE DATABASE Cine_DB;

# Usar base de datos
USE Cine_DB;

#creaacion de usuario
CREATE USER IF NOT EXISTS 'cine_user' @'%' IDENTIFIED BY 'cine_pass';

GRANT ALL PRIVILEGES ON Cine_DB.* TO 'cine_user' @'%';

FLUSH PRIVILEGES;

# confirmar en que bd estoy posicionado
SELECT
  DATABASE();

# Creacion de tabla cartelera
DROP TABLE IF EXISTS Cartelera;

CREATE TABLE Cartelera (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(150) NOT NULL,
  director VARCHAR(50) NOT NULL,
  anio INT NOT NULL,
  duracion INT UNSIGNED NOT NULL,
  genero ENUM(
    'ACCION',
    'DRAMA',
    'COMEDIA',
    'AVENTURA',
    'ANIMACION',
    'CIENCIA_FICCION',
    'TERROR',
    'ROMANCE'
  ) NOT NULL,
  UNIQUE KEY uk_titulo_director_anio (titulo, director, anio),
  -- Se elimina la siguiente línea que causa el error:
  -- CHECK (anio BETWEEN 1888 AND YEAR(CURDATE())), 
  CHECK (
    duracion BETWEEN 1
    AND 999
  )
) ENGINE = InnoDB;

#insert de data de prueba
INSERT INTO
  Cartelera (titulo, director, anio, duracion, genero)
VALUES
  (
    'La Ciudad Invisible',
    'A. Gómez',
    2022,
    115,
    'DRAMA'
  ),
  (
    'Risas en 3 actos',
    'M. Pérez',
    2021,
    97,
    'COMEDIA'
  ),
  ('Misión Polar', 'C. Ruiz', 2023, 129, 'ACCION');

# Selecion de tabla cartelera
select
  *
from
  Cartelera;

#selecion de usuario actual
SELECT
  CURRENT_USER() AS usuario_conectado;

#seleccion de base de datos seleccionada
SELECT
  DATABASE() AS base_actual;

#mostrar todas las bd disponibles
SHOW DATABASES;

# usar base de datos Cine_DB
USE Cine_DB;
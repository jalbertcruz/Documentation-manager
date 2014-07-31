CREATE TABLE public.index_folder (
  dir VARCHAR NOT NULL, 
  index VARCHAR, 
  CONSTRAINT index_folder_pkey PRIMARY KEY(dir)
) WITHOUT OIDS;

COMMENT ON TABLE public.index_folder
IS '
- Tabla para almacenar por cada directorio un fichero JSON con el listado de los ficheros y carpetas indexadas para acelerar el proceso de reindexado.';


CREATE TABLE public.signs (
  file_path VARCHAR NOT NULL, 
  file_name VARCHAR NOT NULL, 
  md5 VARCHAR, 
  sha VARCHAR, 
  size INTEGER, 
  CONSTRAINT signs_idx PRIMARY KEY(file_path, file_name)
) WITHOUT OIDS;


CREATE OR REPLACE VIEW public.contar_iguales(
    md5,
    cantidad)
AS
  SELECT s.md5,
         count(s.md5) AS cantidad
  FROM signs s
  GROUP BY s.md5;


CREATE VIEW public.con_repeticiones (
    md5,
    cantidad)
AS
SELECT contar_iguales.md5, contar_iguales.cantidad
FROM contar_iguales
WHERE contar_iguales.cantidad > 1;

ALTER TABLE public.index_folder
  ADD CONSTRAINT index_folder_pkey 
    PRIMARY KEY (dir);

ALTER TABLE public.signs
  ADD CONSTRAINT signs_idx 
    PRIMARY KEY (file_path, file_name);
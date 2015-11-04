USE [TCADBNAC]
GO
/****** Object:  StoredProcedure [dbo].[Insertar_Fijos_En_EntradaSemanal]    Script Date: 11/04/2015 15:55:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Francisco Javier Lamas Green
-- Create date: 18 de Septiembre de 2015
-- Description:	Seleccionar los empleados en la tabla RHNomEmpleados e ingresarlo en la tabla de horario semanal.
--			    El propósito es evitar crear una plantilla de excel que se tenga que utilizar cada semana inncesesariamente.
-- =============================================
CREATE PROCEDURE [dbo].[Insertar_Fijos_En_EntradaSemanal]

AS
BEGIN
DECLARE @ClaveNomina AS VARCHAR(8)
DECLARE @TotalEmpleadosEnNomina AS INTEGER
DECLARE @Contador AS INTEGER

SELECT @TotalEmpleadosEnNomina = COUNT(*) FROM RHNomEmpleados WHERE Indicador = 1 AND Departamento = 501
--Seleccionar todas las areas que tengan horario fijo; Recepcion de Mercancia tiene horario fijo, de 7 a 5. 
--La tabla nomdiv muestra el origen de los datos.

SET @Contador = 0

--Recorrer con un contador hasta el número de empleados fijos con alta en la nómina
WHILE @Contador < @TotalEmpleadosEnNomina BEGIN
--Crear tabla a partir de RHNomEmpleados con un campo ordenado para poder recorrerla
WITH EntradaOrdenada AS (
  SELECT ClaveNomina,
  ROW_NUMBER() OVER (ORDER BY ClaveNomina * 1) AS Fila
  FROM RHNomEmpleados WHERE IndicadorAlta = 1 AND Departamento = 501
)
--Obtener la clave del empleado
SELECT @ClaveNomina = ClaveNomina FROM EntradaOrdenada WHERE Fila = @Contador + 1
--Insertar los campos fijos en la tabla entrada semanal
IF (@ClaveNomina NOT IN (SELECT ClaveEmpleado FROM Entrada_Semanal)) BEGIN
INSERT INTO Entrada_Semanal VALUES (@ClaveNomina, '09', '09', '09', '09', '09', '09', 'D')
END 
SET @Contador = @Contador + 1
--Finalizar Ciclo
END
--Finalizar Procedimiento 
END

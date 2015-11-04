USE [TCADBNAC]
GO
/****** Object:  StoredProcedure [dbo].[Justificacion_De_Faltas]    Script Date: 11/04/2015 16:07:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Autor:		Francisco Javier Lamas Green
-- Fecha de Creación: 12 de Septiembre de 2015
-- Descripción:	Procedimiento para cambiar el estado de una falta, es decir, justificar una falta.
--              Recibe la clave del empleado y un rango de fechas o un día en específico en el cual
--              aplicar cambios.
--              Finalmente envía un correo para aclarar al jefe lo sucedido. 
-- =============================================
CREATE PROCEDURE [dbo].[Justificacion_De_Faltas]
	@ClaveNomina AS VARCHAR(8),
	@FechaInicio AS VARCHAR(10),
	@FechaFin AS VARCHAR(10)
AS
BEGIN
SET LANGUAGE 'Spanish'

DECLARE @CorreoDeJefe VARCHAR(40)    --Correo del jefe/gerente etc. al cual se enviará el reporte.
DECLARE @Nombre_Subordinado VARCHAR(90) --Nombre del empleado a reportar.
DECLARE @Cuerpo_Mensaje_UnaFalta NVARCHAR(1000)      --Cuerpo del correo, falta de un día
DECLARE @Cuerpo_Mensaje_MultiplesFaltas NVARCHAR(1000)      --Cuerpo del correo, falta en un rango de tiempo
DECLARE @Puesto_de_Empleado VARCHAR(30) --Puesto que ocupa el empleado en la organización.

--Obtención de valores de variables-------------------------------------------------------------------
--Buscar el nombre del empleado con su clave de nómina; Concatenar nombres y apellidos.
-- Se utiliza like debido a que se buscan con char(3) en un campo char(8) y tiene valores diferentes.
--SELECT @Nombre_Subordinado = Nombre + ' ' + Paterno + ' ' + Materno FROM RHNomEmpleados
--WHERE ClaveNomina LIKE '%' + LTRIM(RTRIM(@ClaveNomina)) + '%'
 
 --Buscar el correo del jefe utilizando la tabla Relacion_Jefe_Emp con el dato de la clave de nomina del empleado.
--SELECT @CorreoDeJefe = Correo FROM Correo_Jefes WHERE Jefe = 
--(SELECT Jefe FROM Relacion_Jefe_Emp WHERE @ClaveNominaEmpleado LIKE '%' + LTRIM(RTRIM(Subordinado)) + '%');

--Buscar el puesto del empleado.
--SELECT @Puesto_de_Empleado = Nombre FROM RHNomPuestos WHERE Puesto = (SELECT Puesto FROM RHNomEmpleados WHERE 
--ClaveNomina LIKE '%' + LTRIM(RTRIM(@ClaveNominaEmpleado)) + '%');
------------------------------------------------------------------------------------------------------
--Formular el cuerpo del correo
SET @Cuerpo_Mensaje_UnaFalta = 'El Empleado ' + 'asdf' + ', el cual tiene el puesto de: ' + 'asdf' +
    ', ha justificado su falta del día ' +  CAST(DATEPART(DAY, CAST(@FechaInicio AS DATE)) AS VARCHAR(10)) + ' de ' + 
    UPPER(DATENAME([MONTH], MONTH(GETDATE()))) + ' de ' + CAST(DATEPART(YEAR, CAST(@FechaInicio AS DATE)) AS VARCHAR(4))
    

------------------------------------------------------------------------------------------------------
--UPDATE Alerta SET Justificada = 1 WHERE ClaveNomina = @ClaveNomina AND Fecha >= @FechaInicio AND Fecha <= @FechaFin
    EXEC msdb.dbo.sp_send_dbmail @profile_name ='Profile Francisco',
      @recipients ='lamas_green_fj@hotmail.com',
      @subject ='Reporte J',
      @body = @Cuerpo_Mensaje_UnaFalta
      
PRINT '--'
END

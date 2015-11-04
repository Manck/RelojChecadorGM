USE [TCADBNAC]
GO
/****** Object:  StoredProcedure [dbo].[Envio_Correos_SACPA]    Script Date: 11/04/2015 15:51:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Autor:		Francisco Javier Lamas Green
-- Fecha de Creación: 31 de Agosto de 2015
-- Descripción:	Procedimiento Prueba #1 para el envío de correos mediante SQL Server para 
--              el Sistema de Alertas de Control de Puntualidad y Asistencia.
-- =============================================

CREATE PROCEDURE [dbo].[Envio_Correos_SACPA] 
	
@ClaveNominaEmpleado char(8)     -- Clave de nomina del empleado 

AS

BEGIN

DECLARE @CorreoDeJefe varchar(40)    --Correo del jefe/gerente etc. al cual se enviará el reporte.
DECLARE @Nombre_Subordinado VARchar(90) --Nombre del empleado a reportar.
DECLARE @Mensaje_Alerta_Estandar nvarchar(1000)      --Cuerpo del correo electrónico
DECLARE @Mensaje_Alerta_Acumulada nvarchar(1000)      --Cuerpo del correo electrónico
DECLARE @Puesto_de_Empleado VARchar(30) --Puesto que ocupa el empleado en la organización.
DECLARE @Periodo int
DECLARE @NumeroRetardos int

--Buscar el nombre del empleado con su clave de nómina; Concatenar nombres y apellidos.
-- Se utiliza like debido a que se buscan con char(3) en un campo char(8) y tiene valores diferentes.

SELECT @Nombre_Subordinado = Nombre + ' ' + Paterno + ' ' + Materno FROM RHNomEmpleados
 WHERE ClaveNomina LIKE '%' + LTRIM(RTRIM(@ClaveNominaEmpleado)) + '%'

--Buscar el correo del jefe utilizando la tabla Relacion_Jefe_Emp con el dato de la clave de nomina del empleado.
SELECT @CorreoDeJefe = Correo FROM Correo_Jefes WHERE Jefe = 
(SELECT Jefe FROM Relacion_Jefe_Emp WHERE @ClaveNominaEmpleado LIKE '%' + LTRIM(RTRIM(Subordinado)) + '%'); -- AQUI OCURRE EL ERROR DE COMPARACION --

--Buscar el puesto del empleado.
SELECT @Puesto_de_Empleado = Nombre FROM nompue WHERE ClavePuesto = (SELECT Puesto FROM RHNomEmpleados WHERE 
ClaveNomina LIKE '%' + LTRIM(RTRIM(@ClaveNominaEmpleado)) + '%');

--Buscar el periodo.
SELECT @Periodo = Periodo FROM H_Periodos WHERE FInicio < SYSDATETIME() AND FFin > SYSDATETIME()

--Inicio de transacción para insertar Campo en la tabla Alerta.

BEGIN TRANSACTION Insersion_Alertas
--Inicio Try-Catch.
BEGIN TRY

--Insertar Registro en la Tabla Alerta.
INSERT INTO Alerta (ClaveNomina, Fecha, Hora, Periodo, Tipo_Alerta, Justificada) VALUES
(@ClaveNominaEmpleado, CAST(GETDATE() AS DATE),CAST(GETDATE() AS TIME(0)), @Periodo, 0, 0);

COMMIT TRANSACTION Insersion_Alertas
END TRY
BEGIN CATCH
  ROLLBACK TRANSACTION Insersion_Alertas
  PRINT ERROR_MESSAGE()
END CATCH
--Fin de Transacción y try-catch.

--Obtener el numero de retardos.
SELECT @NumeroRetardos = COUNT(*) FROM Alerta WHERE ClaveNomina = @ClaveNominaEmpleado AND Tipo_Alerta = 0

--Formular el cuerpo del correo en caso que sea una alerta estándar
SET @Mensaje_Alerta_Estandar = 'El Empleado ' + @Nombre_Subordinado + ', el cual tiene el puesto de: ' + @Puesto_de_Empleado +
    ', no se ha presentado a laborar y su hora de entrada es: 09:00 am.' + CHAR(13) + 
    ' Favor de Tomar Esta Alerta en Cuenta.' + CHAR(13)+ CHAR(13)+
    'Gracias.'
 
--En Caso de que sea una alerta acumulada
SET @Mensaje_Alerta_Acumulada = 'El Empleado ' + @Nombre_Subordinado + ', el cual tiene el puesto de: ' + @Puesto_de_Empleado +
    ', no se ha presentado a laborar y su hora de entrada es: 09:00 am.' + CHAR(13) + 
    'Este comportamiento ha ocurrido un total de ' +
    CONVERT(Varchar(10), @NumeroRetardos) + ' ocasiones.' + CHAR(13) + 
    'Se le pide por favor, tomar una medida para evitar que esta conducta persista.' + CHAR(13)+CHAR(13)+
    'Gracias.'

--Impresiones de debug
PRINT @Mensaje_Alerta_Acumulada
PRINT '--'



IF (@NumeroRetardos >= 3) BEGIN
-- Incio de Sección de Envio de Correo

    EXEC msdb.dbo.sp_send_dbmail @profile_name ='Profile Francisco',
      @recipients =@CorreoDeJefe,
      @subject ='Incidencias en la Puntualidad de Subordinado',
      @body = @Mensaje_Alerta_Acumulada
      
-- Fin de Sección de Envio de Correo
PRINT 'Alerta Acumulada'
END
ELSE
BEGIN
-- Incio de Sección de Envio de Correo

    EXEC msdb.dbo.sp_send_dbmail @profile_name ='Profile Francisco',
      @recipients =@CorreoDeJefe,
      @subject ='Reporte de Retardo de Subordinado',
      @body = @Mensaje_Alerta_Estandar
      
-- Fin de Sección de Envio de Correo
PRINT 'Alerta Estándar'
END
END

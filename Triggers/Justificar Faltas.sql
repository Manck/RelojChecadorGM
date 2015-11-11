USE [TCADBNAC]
GO
/****** Object:  Trigger [dbo].[JustificarFaltas]    Script Date: 11/04/2015 16:46:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Francisco Javier Lamas Green
-- Create date: 16/09/15
-- Description:	Toma los datos que son insertados en la tabla de Justificaciones del MOPER 
--				y los utiliza para actualizar la tabla Alerta, colocando el campo justificada 
--				como verdadero (1).
--Modificación: Se ha añadido la función del envío de correos.
--Modificación: 23 de Septiembre de 2015. Se ha cambiado para que que solo envíe el correo de justificación
--              si la justificación ocurre después de que ocurre el retardo o la falta.
--              Esto con el propósito de evitar que llegue un correo justificando un retardo o falta que jamás ocurrió.
-- =============================================
CREATE TRIGGER [dbo].[JustificarFaltas]
   ON  [dbo].[H_MOPER_RJ]
   AFTER INSERT
AS 
BEGIN
--Colocar el idioma en español para poder visualizar los meses con letra en español
SET LANGUAGE 'Spanish'

--Datos necesarios para actualizar la tabla Alerta---
DECLARE @Clave AS CHAR(8)
DECLARE @FechaInicio AS DATE
DECLARE @FechaFin AS DATE
-----------------------------------------------------

--Datos para el envío de correo para justificar la falta.---------------------------------------------------
DECLARE @CorreoDeJefe VARCHAR(40)    --Correo del jefe/gerente etc. al cual se enviará el reporte.
DECLARE @Nombre_Subordinado VARCHAR(90) --Nombre del empleado a reportar.
DECLARE @Cuerpo_Mensaje_UnaFalta NVARCHAR(500)      --Cuerpo del correo, falta de un día
DECLARE @Cuerpo_Mensaje_MultiplesFaltas NVARCHAR(500)  --Multiples Faltas
DECLARE @Puesto_de_Empleado VARCHAR(30) --Puesto que ocupa el empleado en la organización.
------------------------------------------------------------------------------------------------------------

--Obtener los datos de los campos insertados--------
SELECT @Clave = Clavenomina FROM inserted
SELECT @FechaInicio = Desde FROM inserted
SELECT @FechaFin = Hasta FROM inserted
----------------------------------------------------

--Actualizar la falta del empleado; colocar justificada en 1.
UPDATE Alerta SET Justificada = 1 WHERE ClaveNomina = @Clave AND 
Fecha >= @FechaInicio AND Fecha <= @FechaFin AND Tipo_Alerta = 1;
	
--Buscar el nombre del empleado con su clave de nómina; Concatenar nombres y apellidos.
-- Se utiliza like debido a que se buscan con char(3) en un campo char(8) y tiene valores diferentes.
SELECT @Nombre_Subordinado = Nombre + ' ' + Paterno + ' ' + Materno FROM RHNomEmpleadosP
WHERE ClaveNomina = LTRIM(RTRIM(@Clave)) 
 
 --Buscar el puesto del empleado.
SELECT @Puesto_de_Empleado = Nombre FROM RHNomPuestos WHERE Puesto = (SELECT Puesto FROM RHNomEmpleadosP WHERE 
ClaveNomina = LTRIM(RTRIM(@Clave)));

 --Buscar el correo del jefe utilizando la tabla Relacion_Jefe_Emp con el dato de la clave de nomina del empleado.
SELECT @CorreoDeJefe = Correo FROM Correo_Jefes WHERE Jefe IN
(SELECT Jefe FROM Relacion_Jefe_Emp WHERE Subordinado = @Puesto_de_Empleado)

--Formular el cuerpo del correo en caso de falta de un solo día
SET @Cuerpo_Mensaje_UnaFalta = 'El Empleado ' + @Nombre_Subordinado +
 ', el cual tiene el puesto de: ' + @Puesto_de_Empleado +
    ', ha justificado su falta del día ' +

    CAST(DATEPART(DAY, CAST(@FechaInicio AS DATE)) AS VARCHAR(10)) + ' de ' + 
    DATENAME([MONTH], CAST(   @FechaInicio   AS DATE) )  + 
    ' de ' + CAST(DATEPART(YEAR, CAST(@FechaInicio AS DATE)) AS VARCHAR(4)) + 
    '.' + CHAR(13)

--Formular el cuerpo del correo en caso de que sea un rango de días
SET @Cuerpo_Mensaje_MultiplesFaltas = 'El Empleado ' + @Nombre_Subordinado +
 ', el cual tiene el puesto de: ' + @Puesto_de_Empleado +
    ', ha justificado sus faltas del ' +
    
    CAST(DATEPART(DAY, CAST(@FechaInicio AS DATE)) AS VARCHAR(10)) + ' de ' + 
    DATENAME([MONTH], CAST(   @FechaInicio   AS DATE) )  + ' de ' +
    CAST(DATEPART(YEAR, CAST(@FechaInicio AS DATE)) AS VARCHAR(4)) + ' al ' +

    CAST(DATEPART(DAY, CAST(@FechaFin AS DATE)) AS VARCHAR(10)) + ' de ' + 
    DATENAME([MONTH], CAST(   @FechaFin   AS DATE) )  + ' de ' +
    CAST(DATEPART(YEAR, CAST(@FechaFin AS DATE)) AS VARCHAR(4)) + 
    '.' + CHAR(13);


--Si el procedimiento se ejecuta después de que se ha elaborado la alerta de faltas, entonces se envía, de lo contrario, 
--simplemente se omite. Se decide que se ha enviado cuando son 22 minutos después de la hora de entrada (en el caso de los horarios 
--en punto, o 52 en caso de los horarios a la media hora.
IF( ((DATEPART(MINUTE, GETDATE()) > 22) AND (DATEPART(MINUTE, GETDATE()) < 30)) OR (DATEPART(MINUTE, GETDATE()) > 52) ) BEGIN
--Si la fecha del último día de falta es mayor (y por ende diferente) entonces 
IF @FechaFin = @FechaInicio BEGIN
	--Ejecutar envío de correo de una falta
    EXEC msdb.dbo.sp_send_dbmail @profile_name ='Profile Francisco',
      @recipients ='lamas_green_fj@hotmail.com',
      @subject ='Se ha Justificado Correctamente la Falta de un Empleado',
      @body = @Cuerpo_Mensaje_UnaFalta
END ELSE BEGIN
	--Ejecutar envío de correo de una falta
    EXEC msdb.dbo.sp_send_dbmail @profile_name ='Profile Francisco',
      @recipients ='lamas_green_fj@hotmail.com',
      @subject ='Se han Justificado las Faltas de un Empleado',
      @body = @Cuerpo_Mensaje_MultiplesFaltas
END
--Finalizar IF de horas
END
--Finalizar Disparador
END
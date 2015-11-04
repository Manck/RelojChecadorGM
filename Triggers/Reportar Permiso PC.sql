USE [TCADBNAC]
GO
/****** Object:  Trigger [dbo].[Reportar_Permiso]    Script Date: 11/04/2015 16:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Francisco Javier Lamas Green
-- Create date: 23 de Septiembre de 2015
-- Description:	Envia un correo diciendo que un empleado ha solicitado un permiso y por ello ha ocurrido la falta 
--              anteriormente reportada.
-- =============================================
CREATE TRIGGER [dbo].[Reportar_Permiso]
   ON  [dbo].[H_MOPER_PC]
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
DECLARE @Cuerpo_Mensaje_UnDia NVARCHAR(500)      --Cuerpo del correo, permiso de un día
DECLARE @Cuerpo_Mensaje_VariosDias NVARCHAR(500)  --Multiples Dias
DECLARE @Puesto_de_Empleado VARCHAR(30) --Puesto que ocupa el empleado en la organización.
------------------------------------------------------------------------------------------------------------

--Ejecutar solo cuando ya ha pasado la ejecución del procedimiento de buscar faltas.
--DATO FINAL CAMBIADO PARA PRUEBAS
IF( ((DATEPART(MINUTE, GETDATE()) > 22) AND (DATEPART(MINUTE, GETDATE()) < 30)) OR (DATEPART(MINUTE, GETDATE()) > 52) ) BEGIN

--Obtener los datos de los campos insertados--------
SELECT @Clave = Clavenomina FROM inserted
SELECT @FechaInicio = Desde FROM inserted
SELECT @FechaFin = Hasta FROM inserted
----------------------------------------------------


--Actualizar la falta del empleado; colocar justificada en 1
UPDATE Alerta SET Justificada = 1 WHERE ClaveNomina = @Clave AND 
Fecha >= @FechaInicio AND Fecha <= @FechaFin AND Tipo_Alerta = 1;

--Buscar el nombre del empleado con su clave de nómina; Concatenar nombres y apellidos.
-- Se utiliza like debido a que se buscan con char(3) en un campo char(8) y tiene valores diferentes.
SELECT @Nombre_Subordinado = Nombre + ' ' + Paterno + ' ' + Materno FROM RHNomEmpleados
WHERE ClaveNomina LIKE '%' + LTRIM(RTRIM(@Clave)) + '%'
 
 --Buscar el correo del jefe utilizando la tabla Relacion_Jefe_Emp con el dato de la clave de nomina del empleado.
--SELECT @CorreoDeJefe = Correo FROM Correo_Jefes WHERE Jefe = 
--(SELECT Jefe FROM Relacion_Jefe_Emp WHERE @Clave LIKE '%' + LTRIM(RTRIM(Subordinado)) + '%');

--Buscar el puesto del empleado.
SELECT @Puesto_de_Empleado = Nombre FROM RHNomPuestos WHERE Puesto = (SELECT Puesto FROM RHNomEmpleados WHERE 
ClaveNomina LIKE '%' + LTRIM(RTRIM(@Clave)) + '%');

--Formular el cuerpo del correo en caso de falta de un solo día
SET @Cuerpo_Mensaje_UnDia = 'El Empleado ' + @Nombre_Subordinado +
 ', el cual tiene el puesto de: ' + @Puesto_de_Empleado +
    ', ha justificado su falta del día ' +

    CAST(DATEPART(DAY, CAST(@FechaInicio AS DATE)) AS VARCHAR(10)) + ' de ' + 
    DATENAME([MONTH], CAST(   @FechaInicio   AS DATE) )  + 
    ' de ' + CAST(DATEPART(YEAR, CAST(@FechaInicio AS DATE)) AS VARCHAR(4)) + 
    '.' + CHAR(13) + 'El empleado ha solicitado un permiso y se le ha concedido.'

--Formular el cuerpo del correo en caso de que sea un rango de días
SET @Cuerpo_Mensaje_VariosDias = 'El Empleado ' + @Nombre_Subordinado +
 ', el cual tiene el puesto de: ' + @Puesto_de_Empleado +
    ', ha justificado sus faltas.'+ CHAR(13)+'Ya que ha solicitado un permiso desde el  ' +
    
    CAST(DATEPART(DAY, CAST(@FechaInicio AS DATE)) AS VARCHAR(10)) + ' de ' + 
    DATENAME([MONTH], CAST(   @FechaInicio   AS DATE) )  + ' de ' +
    CAST(DATEPART(YEAR, CAST(@FechaInicio AS DATE)) AS VARCHAR(4)) + ' al ' +

    CAST(DATEPART(DAY, CAST(@FechaFin AS DATE)) AS VARCHAR(10)) + ' de ' + 
    DATENAME([MONTH], CAST(   @FechaFin   AS DATE) )  + ' de ' +
    CAST(DATEPART(YEAR, CAST(@FechaFin AS DATE)) AS VARCHAR(4)) + 
    ' y se le ha concedido.' ;


--Si el procedimiento se ejecuta después de que se ha elaborado la alerta de faltas, entonces se envía, de lo contrario, 
--simplemente se omite. Se decide que se ha enviado cuando son 22 minutos después de la hora de entrada (en el caso de los horarios 
--en punto, o 52 en caso de los horarios a la media hora.

--Si la fecha del último día de falta es mayor (y por ende diferente) entonces 
IF @FechaFin = @FechaInicio BEGIN
	--Ejecutar envío de correo de una falta
    EXEC msdb.dbo.sp_send_dbmail @profile_name ='Profile Francisco',
      @recipients ='lamas_green_fj@hotmail.com',
      @subject ='Se ha Justificado Correctamente la Falta de un Empleado',
      @body = @Cuerpo_Mensaje_UnDia
END ELSE BEGIN
	--Ejecutar envío de correo de una falta
    EXEC msdb.dbo.sp_send_dbmail @profile_name ='Profile Francisco',
      @recipients ='lamas_green_fj@hotmail.com',
      @subject ='Se han Justificado las Faltas de un Empleado',
      @body = @Cuerpo_Mensaje_VariosDias
END

--Finalizar IF de Horas
END
--Finalizar Disparador
END
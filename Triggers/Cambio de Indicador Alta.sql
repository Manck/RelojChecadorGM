USE [TCADBNAC]
GO
/****** Object:  Trigger [dbo].[Cambios_De_IndicadorAlta]    Script Date: 11/04/2015 16:18:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Francisco Javier Lamas Green 
-- Create date: 21 de Septiembre de 2015
-- Description:	Disparador ejecutado al actualizar la tabla "RHNomEmpleados".
--              Si el cambio realizado fue cambiar el IndicadorAlta de 1 a 0,
--              entonces da de baja al empleado y no lo cuenta para el chequeo de entrada.
-- NOTA:        Si un empleado puede de darse de alta una vez más entonces dar de alta en el checador.
-- Alteración:  Si el valor ya existe en la tabla Entrada_Semanal, Omitir. //Fecha 22 de Septiembre de 2015
-- NOTA:        Una vez con todos los horarios fijos identificados, incluirlos.
-- =============================================
CREATE TRIGGER [dbo].[Cambios_De_IndicadorAlta]
   ON  [dbo].[RHNomEmpleados]
   AFTER UPDATE
AS 
BEGIN

--Variables---------------------
DECLARE @ClaveNomina AS CHAR(8)
DECLARE @IndicadorAlta AS CHAR(1)
DECLARE @Departamento AS CHAR(6)

--Inicialización de valores-------------------------------------------------------
SELECT @ClaveNomina = ClaveNomina FROM inserted
SELECT @IndicadorAlta = Indicador FROM inserted
SELECT @Departamento = Departamento FROM RHNomEmpleados WHERE ClaveNomina = @ClaveNomina

--Si la clave de nomina del empleado que se actualiza no existe en entrada_semanal, alterar la tabla-------------
IF @ClaveNomina NOT IN(SELECT ClaveEmpleado FROM Entrada_Semanal) BEGIN 

IF @IndicadorAlta = 0 BEGIN
DELETE FROM Entrada_Semanal WHERE ClaveEmpleado = @ClaveNomina
END

IF @IndicadorAlta = 1 AND (@Departamento = 501) BEGIN
INSERT INTO Entrada_Semanal VALUES(@ClaveNomina, '09', '09', '09', '09', '09', '09', 'D')
END
--Fin if de clavenomina
END
--Fin de disparador
END

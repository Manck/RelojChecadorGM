USE [TCADBNAC]
GO
/****** Object:  Trigger [dbo].[Registrar_Fijo_Conforme_Entrada]    Script Date: 11/04/2015 16:30:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Francisco Javier Lamas Green
-- Create date: 21 de Septiembre de 2015
-- Description:	Al insertar un valor en la tabla RHNomEmpleados, si este es de horario fijo, 
--              llama al procedimiento Insertar_Fijos_En_EntradaSemanal para 
--              llevar su registro.
-- =============================================
CREATE TRIGGER [dbo].[Registrar_Fijo_Conforme_Entrada]
   ON  [dbo].[RHNomEmpleados]
   AFTER INSERT
AS 
BEGIN

DECLARE @Clave AS CHAR(8)
DECLARE @Departamento AS CHAR(6)
DECLARE @Alta AS CHAR(1)
DECLARE @ChecaTarjeta AS CHAR(1)

SELECT @Clave = ClaveNomina FROM inserted
SELECT @Departamento = Departamento FROM inserted
SELECT @Alta = Indicador FROM inserted
SELECT @ChecaTarjeta = ChecaTarjeta FROM inserted

--CAMBIAR A QUE BUSQUE A TODOS LOS DEPARTAMENTOS CON HORARIO FIJO 
--Si pertenece a un departamento con horario fijo, se registra con alta y no existe ya en la tabla
IF(@Departamento = 501 AND @Alta = '1' AND @ChecaTarjeta = 'S') BEGIN
INSERT INTO Entrada_Semanal VALUES (@Clave, '09', '09', '09', '09', '09', '09', 'D')
END
PRINT ''
END

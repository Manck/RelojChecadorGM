USE [TCADBNAC]
GO
/****** Object:  StoredProcedure [dbo].[Buscador_Justificaciones]    Script Date: 11/04/2015 15:49:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Francisco Javier Lamas Green
-- Create date: 23 de Septiembre de 2015
-- Description:	Busca si ha ocurrido una entrada en H_MOPER_RJ con la clave del empleado.
--              Esto se debe ya que en muchos casos ocurre un error de entrada con el checador,
--              el empleado se enferma etc. Y en caso de que el empleado haya realizado el proceso de justificación
--              antes de que ocurra la alerta, es posible que no tenga registro en H_Reloj, mas si se cuenta su asistencia,
--              y por ello, verificar esto, evita enviar una alerta innecesaria.
-- =============================================
CREATE PROCEDURE [dbo].[Buscador_Justificaciones]
    --Parámetro de Entrada que recibe; clavenomina del empleado a verificar.
	@ClaveNomina AS CHAR(8), 
	--Parámetro de Salida que retorna; el empleado está justificado o no lo está.
	@Justificado AS BIT OUTPUT
AS
    --Variable para comprobar si existe registro del empleado que concuerde con la fecha actual.
    DECLARE @Desde AS DATETIME
    
    --Busca el valor del campo desde cuando la fecha actual es mayor o igual a desde y 
    --cuando la fecha actual es menor al campo Hasta.
    SELECT @Desde = Desde FROM H_MOPER_RJ WHERE Clavenomina = @ClaveNomina AND ( 
    (CAST(GETDATE() AS DATE) >= CAST(Desde AS DATE)) AND (CAST(GETDATE() AS DATE) <= CAST(Hasta AS DATE)) )
       
    --Si la búsqueda enterior obtuvo un resultado, entonces no será null y por ende significa que tiene un 
    --registro de justificación activo de lo contrario @Justificado = 0.
    PRINT 'Desde; Buscador Justificaciones; ' + CAST(@Desde AS VARCHAR(20))
	IF( @Desde IS NOT NULL ) BEGIN
		SET @Justificado = 1 
    END ELSE BEGIN
		SET @Justificado = 0
	END 
	
RETURN

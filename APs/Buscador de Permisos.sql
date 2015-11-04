USE [TCADBNAC]
GO
/****** Object:  StoredProcedure [dbo].[Buscador_De_Permisos]    Script Date: 11/03/2015 14:54:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Francisco Javier Lamas Green
-- Create date: 23 de Septiembre de 2015
-- Description:	Busca si el empleado en las tablas H_MOPER_PC, H_MOPER_PS y si tiene un registro de permiso en alguna
--              de esas tablas, entonces retorna TRUE (1).
-- =============================================
CREATE PROCEDURE [dbo].[Buscador_De_Permisos]
	--Parámetro de Entrada; Clave del empleado.
	@ClaveNomina AS CHAR(8),
	--Variable de Salida; Si el empleado tiene permiso.
	@TienePermiso AS BIT OUTPUT
AS
	--Variable para comparar con la tabla H_MOPER_PC
	DECLARE @DesdePC AS DATE
	--Variable para comparar con la tabla H_MOPER_PS
	DECLARE @DesdePS AS DATE
	--Variable para comparar con la tabla H_MOPER_PSOL
	DECLARE @DesdePSOL AS DATE
	
	--Busca si tiene permiso en la tabla H_MOPER_PC
	SELECT @DesdePC = Desde FROM H_MOPER_PC WHERE ClaveNomina = @ClaveNomina AND ( 
	(CAST(GETDATE() AS DATE) >= Desde) AND (CAST(GETDATE() AS DATE) <= Hasta) );
	--Busca si tiene permiso en la tabla H_MOPER_PS
	SELECT @DesdePS = Desde FROM H_MOPER_PS WHERE ClaveNomina = @ClaveNomina AND ( 
	(CAST(GETDATE() AS DATE) >= Desde) AND (CAST(GETDATE() AS DATE) <= Hasta) );
	--Busca si tiene permiso en la tabla H_MOPER_PSOL
	SELECT @DesdePSOL = Desde FROM H_MOPER_PSOL WHERE ClaveNomina = @ClaveNomina AND ( 
	(CAST(GETDATE() AS DATE) >= Desde) AND (CAST(GETDATE() AS DATE) <= Hasta) );
	
	--Si tiene registro de un permiso en alguna de las tres tablas, entonces retorna TRUE (1)
	IF( (@DesdePC IS NOT NULL) OR (@DesdePS IS NOT NULL) OR (@DesdePSOL IS NOT NULL) )BEGIN
		SET @TienePermiso = 1
	END ELSE BEGIN
		SET @TienePermiso = 0
	END
	
RETURN

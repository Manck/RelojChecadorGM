USE [TCADBNAC]
GO
/****** Object:  StoredProcedure [dbo].[Buscador_De_Retardos_EntradaSemanal]    Script Date: 11/03/2015 12:14:27 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Francisco Javier Lamas Green
-- Create date: 2 de Septiembre de 2015
-- Fecha de modificación para horarios variables: 8 de septiembre de 2015
-- Description:	Procedimeinto para revisar si los empleados han realizado chequeo 
--				en el reloj checador en el horario que les corresponde.
-- =============================================
CREATE PROCEDURE [dbo].[Buscador_De_Retardos_EntradaSemanal]

AS

BEGIN
   -------------------------------------------------
    DECLARE @ClaveEmpleado INT                    -- Clave del Empleado
	DECLARE @Periodo VARCHAR(2)                   -- Periodo actual de la empresa
	DECLARE @Recorrido_E INT                      -- Contador para el recorrido de E
	DECLARE @Recorrido_ClaveEmpleado INT          -- Contador para el recorrido de empleados
	DECLARE @Numero_Empleados INT                 -- Número total de empleados
	DECLARE @Semi_Alerta BIT                      -- Status de alertar o no alertar
	DECLARE @Valor_En_E0N DATETIME                -- Valor en el campo E de entradas, E01, E02, E03 etc.
	DECLARE @Expresion_E VARCHAR(3)               -- Nombre del campo en SQL Dinámico 
	DECLARE @ClaveEmpleadoChar VARCHAR(8)         -- Clave del empleado en char
	DECLARE @ComandoSQL_Buscar_E NVARCHAR(500)    -- Comando a ejecutar para encontrar el valor en el campo E
	DECLARE @Festivo AS DATE                      -- Almacena una fecha cuando es un día festivo
	DECLARE @Departamento_De_Empleado AS CHAR(6)  -- Departamento al cual pertenece el empleado para definir si descansa en festivo
	DECLARE @ChecaTarjeta AS CHAR(1)              -- Revisa si el empleado checa tarjeta y por ende si lo debe revisar      
	DECLARE @RevisarEntrada AS BIT                -- Variable Final para decidir si revisa entrada a un empleado
	------------------------------------------------
	DECLARE @Dia_Semana INT                       -- Día de la semana actual
	DECLARE @Hora_Entrada_Empleado VARCHAR(2)     -- Hora de entrada del empleado registrada en la tabla Entrada_Semanal
	-------------------------------------------------
	DECLARE @TVacaciones AS BIT                   -- Almacena si tiene vacaciones en base a BuscarVacaciones_H_MOPER_Vacaciones
	DECLARE @EstaJustificado AS BIT               -- Valor 1 si está justificado o 0 si no está justificado.
	DECLARE @TienePermiso AS BIT                  -- Variable para verificar si un usuario pidió un permiso.
	------------------------------------------------
	SET @Semi_Alerta = 0
	SET @Recorrido_E = 0
	SET @Recorrido_ClaveEmpleado = 0
	------------------------------------------------
	SET @Dia_Semana = DATEPART(DW, GETDATE())
	IF(@Dia_Semana = 1) BEGIN
	SET @Dia_Semana = 7
	END
	ELSE BEGIN
	SET @Dia_Semana = @Dia_Semana - 1
	END 
	
	--Buscar el Numero de Empleados, ahora en la tabla de horarios, entrada semanal.
	SELECT @Numero_Empleados = COUNT(*) FROM Entrada_Semanal 
	
	--Buscar el Periodo actual y convertirlo a VARCHAR(2)
    SELECT @Periodo = CONVERT(VARCHAR(2), Periodo) FROM H_Periodos
    WHERE FInicio < SYSDATETIME() AND FFin > SYSDATETIME()
    
    --Seleccionar el valor del día festivo 
	SELECT @Festivo = CONVERT(DATE,(CONVERT(VARCHAR(4),DATEPART(YEAR,GETDATE())) + '/' + Fecha)) FROM H_Festivos WHERE 
    CONVERT(DATE,(CONVERT(VARCHAR(4),DATEPART(YEAR,GETDATE())) + '/' + Fecha)) = CAST(GETDATE()+1 AS DATE)
    
WHILE @Recorrido_ClaveEmpleado < @Numero_Empleados BEGIN

--Tabla virtual creada para obtener los datos con un campo ordenado llamado Fila 
WITH EntradaOrdenada AS (
  SELECT ClaveEmpleado,
  ROW_NUMBER() OVER (ORDER BY ClaveEmpleado * 1) AS Fila
  FROM Entrada_Semanal
)
	--Obtener la clave del empleado
	SELECT @ClaveEmpleado = ClaveEmpleado FROM EntradaOrdenada
	WHERE Fila = @Recorrido_ClaveEmpleado + 1
    --Convertir ClaveEmpleado a VARCHAR(8)
    SET @ClaveEmpleadoChar = CONVERT(VARCHAR(8), @ClaveEmpleado)
------------------------------------------------------------------------------------------------------------------------------
        --Seleccionar el departamento al cual pertenece el empleado
    SELECT @Departamento_De_Empleado = Departamento FROM RHNomEmpleados WHERE
    LTRIM(RTRIM(replace(replace(replace(ClaveNomina, char(9),''), char(10),''), char(13),''))) = @ClaveEmpleadoChar
    
    --Busacar si checa tarjeta
    SELECT @ChecaTarjeta = ChecaTarjeta FROM RHNomEmpleados WHERE 
    LTRIM(RTRIM(replace(replace(replace(ClaveNomina, char(9),''), char(10),''), char(13),''))) = @ClaveEmpleadoChar
 -----------------------------------------------------------------------------------------------------------------------------
 
 --Asignar al valor a @RevisarEntrada
    IF (@ChecaTarjeta = 'N') OR (@Departamento_De_Empleado = '501' AND @Festivo IS NOT NULL) BEGIN
		SET @RevisarEntrada = 0
    END ELSE BEGIN

		--Buscar la hora de entrada que debe tener el empleado segun la tabla Entrada_Semanal
		IF(@Dia_Semana = 1) BEGIN
			SELECT @Hora_Entrada_Empleado = Lunes FROM Entrada_Semanal WHERE ClaveEmpleado = @ClaveEmpleado
		END
		IF(@Dia_Semana = 2) BEGIN
			SELECT @Hora_Entrada_Empleado = Martes FROM Entrada_Semanal WHERE ClaveEmpleado = @ClaveEmpleado
		END
		IF(@Dia_Semana = 3) BEGIN
			SELECT @Hora_Entrada_Empleado = Miercoles FROM Entrada_Semanal WHERE ClaveEmpleado = @ClaveEmpleado
		END
		IF(@Dia_Semana = 4) BEGIN
			SELECT @Hora_Entrada_Empleado = Jueves FROM Entrada_Semanal WHERE ClaveEmpleado = @ClaveEmpleado
		END
		IF(@Dia_Semana = 5) BEGIN
			SELECT @Hora_Entrada_Empleado = Viernes FROM Entrada_Semanal WHERE ClaveEmpleado = @ClaveEmpleado
		END
		IF(@Dia_Semana =6) BEGIN
			SELECT @Hora_Entrada_Empleado = Sabado FROM Entrada_Semanal WHERE ClaveEmpleado = @ClaveEmpleado
		END
		IF(@Dia_Semana = 7) BEGIN
			SELECT @Hora_Entrada_Empleado = Domingo FROM Entrada_Semanal WHERE ClaveEmpleado = @ClaveEmpleado
		END
    
        --Buscar si el empleado tiene vacaciones en base al procedimiento BuscarVacaciones_H_MOPER_Vacaciones
        EXEC BuscarVacaciones_H_MOPER_Vacaciones @ClaveEmpleadoChar, @TieneVacaciones = @TVacaciones OUTPUT;
        --Buscar si el empleado está justificado 
        EXEC Buscador_Justificaciones @ClaveEmpleadoChar, @Justificado = @EstaJustificado OUTPUT;
        --Buscar si el empleado solicitó un permiso en las tablas H_MOPER_PC, H_MOPER_PS o H_MOPER_PSOL
        EXEC Buscador_De_Permisos @ClaveEmpleadoChar, @TienePermiso = @TienePermiso OUTPUT;
        
		--Si es igual a D (Descanso) o V (Vacaciones especificadas desde Excel) o Vacaciones desde------------------
		--la tabla H_MOPER_Vacaciones, O si está justificado, Colocar @RevisarEntrada en 0
		IF( (@Hora_Entrada_Empleado LIKE '%D%') OR (@Hora_Entrada_Empleado LIKE '%V%') OR
		  (@TVacaciones = 1) OR (@EstaJustificado = 1) OR(@TienePermiso = 1) ) BEGIN
			SET @RevisarEntrada = 0
		END ELSE BEGIN
			SET @RevisarEntrada = 1
		END
    -- Fin Else de @RevisarEntrada
    END
    
        PRINT 'Clave : ' + @ClaveEmpleadoChar
		PRINT 'Revisar Entrada' + CAST(@RevisarEntrada AS VARCHAR(1))
		PRINT 'Vacaciones ' + CAST(@TVacaciones AS VARCHAR(1))
		PRINT 'Justificado ' + CAST(@EstaJustificado AS VARCHAR(1))
		PRINT 'Permiso ' + CAST(@TienePermiso AS VARCHAR(1))
		IF @RevisarEntrada = 0 BEGIN
		PRINT '------------------------------'
		END
    
    
--Si se va a checar la entrada de ese empleado -------------------------------------------------------------------------   
    IF @RevisarEntrada = 1 BEGIN
    
	--Buscar por E01, E02, E03... E16 para verificar retardos.
    -- PRINT @ClaveEmpleadoChar + 'Clave en el ciclo de empleados'
    	    WHILE @Recorrido_E < 16 BEGIN 
			    --PRINT @Recorrido_E 
			    --Asignar la columna a buscar
				IF( ((@Recorrido_E + 1) < 10) )
				BEGIN
				SET @Expresion_E = 'E0'+CAST(@Recorrido_E+1 AS VARCHAR(1))
				END
				ELSE BEGIN
				SET @Expresion_E = 'E'+CAST(@Recorrido_E+1 AS VARCHAR(2))
                END
                
                --PRINT @Expresion_E
                
                --Preparar comando para buscar el valor de E0N
                SET @ComandoSQL_Buscar_E  = 'SELECT @Valor_En_E0N = '+@Expresion_E+' 
                                            FROM H_Reloj WHERE Anio = ' +
		                                    ' YEAR(GETDATE()) AND Periodo = @Periodo AND
		                                    ClaveNomina = @ClaveEmpleadoChar';
                --Ejecutar la busqueda
		        EXECUTE sp_executesql @ComandoSQL_Buscar_E ,
		                                    N'@ClaveEmpleadoChar VARCHAR(8), 
											@Periodo VARCHAR(2),
											@Valor_En_E0N DATETIME OUTPUT', 
											@ClaveEmpleadoChar = @ClaveEmpleadoChar,
											@Periodo = @Periodo,
											@Valor_En_E0N = @Valor_En_E0N OUTPUT     
											            
	            --Interpretacion: Si el dia actual es el mismo que el que tiene registrado en E0N Y
	            --La hora registrada es menor o igual que a la que debe registrarse Y
	            --Los minutos a los que realizó el registro son 15 o menores,
	            --desactivar Semi_Alerta y romper el ciclo con BREAK;
	            --Se añade :00 a la hora de entrada de empleado debido a que se almacena como 09 y por ende
	            --es imposible hacer el cast.
	            IF( (CAST(@Valor_En_E0N AS DATE) = CAST(GETDATE() AS DATE)) AND
	            (DATEPART(HOUR, @Valor_En_E0N) <= DATEPART(HOUR,CAST((@Hora_Entrada_Empleado + ':00') AS TIME(0)))) AND
                (DATEPART(MINUTE, CAST(@Valor_En_E0N AS TIME(0)) ) < 59) ) --*CAMBIAR DESPUES A LOS MINUTOS DE TOLERANCIA CORRECTOS*--
	            BEGIN
	            SET @Semi_Alerta = 0
	            BREAK;
	            END
	            ELSE BEGIN
	            --Si no se ha registrado y NO es su día de descanso
	            IF(@Valor_En_E0N IS NULL AND (DATEPART(HOUR, GETDATE()) >= DATEPART(HOUR,CAST((@Hora_Entrada_Empleado + ':00') AS TIME(0))))) BEGIN
	            SET @Semi_Alerta = 1
	            END -- Fin tercer if, antes de modificacion
	            END -- Fin segundo else, antes de modificacion
			    
				--Incrementar @Recorrido_E.
				SET @Recorrido_E = @Recorrido_E + 1
				--Fin While para recorrer las columnas con los datos de entrada.
				END
				
                --Si Semi_Alerta es 1, activar la alerta de retraso.
	            IF(@Semi_Alerta = 1)
                BEGIN
                PRINT 'Alerta' + @ClaveEmpleadoChar
                PRINT '------------------------------'
                --EXEC Envio_Correos_SACPA @ClaveEmpleadoChar 
                END
                
                IF(@Semi_Alerta = 0) BEGIN
                PRINT 'No Alerta ' + @ClaveEmpleadoChar
                PRINT'-------------------------------'
                END
                
    --Fin de ciclo de @RevisarEntrada = 1-----------------------------------------------------------------------------
    END
------------Finalizar y formatear variables.----------------------
--Incrementar El Contador de Recorrido de Calves de Empleado
SET @Recorrido_E = 0
--Incrementar el contador para recorrer empleados
SET @Recorrido_ClaveEmpleado = @Recorrido_ClaveEmpleado + 1
--Colocar semi_alerta en 0 
SET @Semi_Alerta = 0
--Aunque no afecte en la operación, se colocan estos valores en 0 para evitar confusiones durante el desarrollo
SET @EstaJustificado = 0
SET @TienePermiso = 0
------------------------------------------------------------------

--Terminar el Ciclo De Busqueda por Empleado
END
--Fin del Procedimiento
END
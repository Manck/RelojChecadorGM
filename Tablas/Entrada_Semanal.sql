USE [TCADBNAC]
GO

/****** Object:  Table [dbo].[Entrada_Semanal]    Script Date: 11/06/2015 10:23:28 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Entrada_Semanal](
	[ClaveEmpleado] [char](8) NOT NULL,
	[Lunes] [varchar](5) NULL,
	[Martes] [varchar](5) NULL,
	[Miercoles] [varchar](5) NULL,
	[Jueves] [varchar](5) NULL,
	[Viernes] [varchar](5) NULL,
	[Sabado] [varchar](5) NULL,
	[Domingo] [varchar](5) NULL,
 CONSTRAINT [PK_Entrada_Semanal] PRIMARY KEY CLUSTERED 
(
	[ClaveEmpleado] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

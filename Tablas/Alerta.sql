USE [TCADBNAC]
GO

/****** Object:  Table [dbo].[Alerta]    Script Date: 11/04/2015 16:14:03 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Alerta](
	[ID_Alerta] [int] IDENTITY(1,1) NOT NULL,
	[ClaveNomina] [char](8) NOT NULL,
	[Fecha] [date] NOT NULL,
	[Hora] [time](0) NOT NULL,
	[Periodo] [int] NOT NULL,
	[Tipo_Alerta] [bit] NOT NULL,
	[Justificada] [bit] NOT NULL,
 CONSTRAINT [PK_Alerta] PRIMARY KEY CLUSTERED 
(
	[ID_Alerta] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


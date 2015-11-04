USE [TCADBNAC]
GO

/****** Object:  Table [dbo].[Huella]    Script Date: 11/04/2015 16:15:23 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Huella](
	[ClaveNomina] [char](8) NOT NULL,
	[indiceDerecho] [varbinary](max) NULL,
	[medioDerecho] [varbinary](max) NULL,
	[anularDerecho] [varbinary](max) NULL,
	[meniqueDerecho] [varbinary](max) NULL,
	[pulgarDerecho] [varbinary](max) NULL,
	[indiceIzquierdo] [varbinary](max) NULL,
	[medioIzquierdo] [varbinary](max) NULL,
	[anularIzquierdo] [varbinary](max) NULL,
	[meniqueIzquierdo] [varbinary](max) NULL,
	[pulgarIzquierdo] [varbinary](max) NULL,
 CONSTRAINT [PK_Huella] PRIMARY KEY CLUSTERED 
(
	[ClaveNomina] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO



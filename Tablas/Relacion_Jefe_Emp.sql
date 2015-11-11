USE [TCADBNAC]
GO

/****** Object:  Table [dbo].[Relacion_Jefe_Emp]    Script Date: 11/04/2015 16:17:49 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Relacion_Jefe_Emp](
	[Jefe] [char](6) NOT NULL,
	[Subordinado] [char](8) NOT NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO



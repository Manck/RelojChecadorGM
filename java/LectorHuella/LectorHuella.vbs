Dim shell
Set Shell = CreateObject("WScript.Shell")

Shell.run "cmd /c start javaw -jar ""C:\Program Files\LectorHuella\LectorEntrada.jar"" "

WScript.Quit
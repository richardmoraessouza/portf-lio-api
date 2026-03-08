@echo off
cd /d C:\Users\Pichau\Desktop\programacao\IAportfolio

echo Removendo repositorio Git antigo...
rmdir /s /q .git 2>nul

echo Inicializando novo repositorio Git...
git init

echo Configurando usuario...
git config user.name "Richard Moraes Souza"
git config user.email "richard@example.com"

echo Adicionando arquivos...
git add .

echo Fazendo commit inicial...
git commit -m "Initial commit - IA Portfolio Backend"

echo Verificando historico...
git log --oneline

echo.
echo ✅ Repositorio Git limpo criado com sucesso!
echo Agora voce pode conectar ao GitHub sem problemas de historico antigo.
echo.
echo Para conectar ao GitHub, execute:
echo git remote add origin https://github.com/SEU_USUARIO/SEU_REPO.git
echo git push -u origin main
echo.
pause

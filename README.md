# TPOO2Grupo15Hito2

Trabajo Práctico de la catedra de Orientación a Objetos 2 de la Universidad Nacional de Lanús (Hito 2)

Grupo 15
Miembros de este grupo:
 * Avila, Brian
 * Casco, Tomas Andres
 * Romero, Gabriel Omar
 * Vega, Ignacio David

Para iniciar la aplicación:
Paso 1:
    Crear solo la BD que se encuentra en la carpeta raíz.
Paso 2:
    Ejecutar Spring con la version de Java 17 con estos comandos:
        $env:JAVA_HOME="C:\Program Files\Java\jdk-17.0.20.1" (Generalmente se guarda aca el JDK, si no es así, modificar el campo)
        $env:Path="$env:JAVA_HOME\bin;$env:Path"
        $env:DB_PASSWORD="" (Password de MySQL)
        $env:MAIL_USERNAME="ignaciodavidvega@gmail.com" (Configuracion del correo del Proyecto para el envio de correos)
        $env:MAIL_PASSWORD="iskhrpsanbolaufw" (Contraseña)
        .\grupo15\mvnw.cmd -f .\grupo15\pom.xml spring-boot:run (Ejecuta Spring)
Paso 3: 
    Ejecutar el resto del script de la BD.
Paso 4:
    Ir a http://localhost:8080/
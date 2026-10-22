# TPOO2Grupo15Hito2

Trabajo Práctico de la cátedra de Orientación a Objetos 2 de la Universidad Nacional de Lanús (Hito 2).

## Grupo 15

Miembros de este grupo:

* Avila, Brian
* Romero, Gabriel Omar
* Vega, Ignacio David

## Para iniciar la aplicación

### Paso 1: Crear la base de datos

Crear únicamente la base de datos utilizando el script que se encuentra en la carpeta raíz del proyecto.

### Paso 2: Ejecutar Spring con Java 17

Ejecutar los siguientes comandos en PowerShell:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17.0.20.1"

$env:Path="$env:JAVA_HOME\bin;$env:Path"

$env:DB_URL="jdbc:mysql://localhost:3306/bd_tp_oo2"

$env:DB_USERNAME="TU_USUARIO_MYSQL"

$env:DB_PASSWORD="TU_PASSWORD_MYSQL"

$env:MAIL_USERNAME="TU_CORREO@gmail.com"

$env:MAIL_PASSWORD="TU_PASSWORD_DE_APLICACION"

.\grupo15\mvnw.cmd -f .\grupo15\pom.xml spring-boot:run
```

> **Nota:** Generalmente el JDK se encuentra en `C:\Program Files\Java\`.  
> Si Java 17 está instalado en otra ubicación, modificar el valor de `JAVA_HOME`.

### Paso 3: Ejecutar el resto del script de la base de datos

Una vez iniciada la aplicación, ejecutar el resto del script correspondiente a la base de datos.

### Paso 4: Abrir la aplicación

Ingresar desde el navegador a:

http://localhost:8080/


## Casos de Uso Implementados - Pull Request

https://github.com/BrianAvila-UNLA/TPOO2Grupo15Hito2/pull/1 (Ignacio David Vega)
https://github.com/BrianAvila-UNLA/TPOO2Grupo15Hito2/pull/2 (Romero Gabriel Omar)
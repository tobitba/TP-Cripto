# TP Criptogracía y Seguridad - Esteganografía

## 👋 Introducción

En este trabajo práctico de la materia de Criptografía y Seguridad se buscó aplicar los conceptos de encriptación y esteganografía para hacer una herramienta que permita ocultar archivos dentro de otros archivos con extensión bmp. 

### ❗ Requisitos:
- Java 21
- [Maven](https://maven.apache.org/download.cgi)
- Terminal estilo Unix

## 🛠️ Compilación 
Desde la terminal y parándose en la carpeta raíz del proyecto correr el siguiente comando:
```shell
mvn clean install
```

## 🏃 Ejecución

### 🎈 Modos
**Modo ocultamiento de un archivo en un .bmp**

Parámetros:
- `-embed`: Indica que se va a ocultar información.
- `-in file`: Archivo que se va a ocultar.
- `-p bitmapfile`: Archivo bmp que será el portador.
- `-out bitmapfile`: Archivo bmp de salida con la información de file incrustada.
- `-steg <LSB1 | LSB4 | LSBI>`: Algoritmo de esteganografiado.
- `-a <aes128 | aes192 | aes256 | 3des>`: Algoritmo de encripción.
- `-m <ecb | cfb | ofb | cbc>`: Algoritmo de encadenamiento.
- `-pass password`: password de encripción.

Ejemplo:
```shell
./run.sh -embed –in "mensaje1.txt" –p "imagen1.bmp" -out "imagenmas1.bmp" –steg LSBI –a 3des –m cbc -pass "oculto"
```

**Extraer de un archivo .bmp un archivo oculto**

Parámetros:
- `-extract`: Indica que se va a extraer información.
- `-p bitmapfile`: Archivo bmp portador.
- `-out file`: Archivo de salida obtenido.
- `-steg <LSB1 | LSB4 | LSBI>`: Algoritmo de esteganografiado.
- `-a <aes128 | aes192 | aes256 | 3des>`: Algoritmo de encripción.
- `-m <ecb | cfb | ofb | cbc>`: Algoritmo de encadenamiento.
- `-pass password`: password de encripción.

Ejemplo
```shell
–extract –p "imagenmas1.bmp" -out "mensaje1" –steg LSBI –a 3des –m cbc -pass "oculto"
```


# Crop Route 1.3.0 (Minecraft 1.20.4 / Fabric)

Mod cliente para grabar rutas y reproducirlas automáticamente, manteniendo el avance,
el sprint y el ataque, con seguimiento de cámara.

## Teclas

| Tecla | Acción |
|---|---|
| J | Comenzar o terminar una grabación |
| K | Abrir el menú de rutas, Temporizador, Vueltas P y Pausas P |
| O | Comenzar o detener el recorrido normal, con temporizador |
| P | Comenzar o detener el ciclo infinito configurable |

## Temporizador de O

Desde `K > Temporizador` se configura el total de horas y minutos. El tiempo
empieza al presionar **O**, se anuncia por chat cuando se cambia y se guarda en
`config/crop-route/timer.json`. No se aplica a P.

## Ciclo P

P comienza siempre por `RTAFK` y reproduce este bucle:

1. **RTAFK**: 40 vueltas por defecto, punto X=5237, Z=4621.
   Al terminar, pausa aleatoria **05:42 a 06:18**.
2. **verruga11**: 40 vueltas por defecto, punto X=5237, Z=4621.
   Al terminar, pausa aleatoria **05:42 a 06:18**.
3. **verruga1**: 40 vueltas por defecto, punto X=5292, Z=4796.
   Al terminar, pausa aleatoria **20:42 a 21:18**.
4. Regresa a `RTAFK` y repite indefinidamente hasta volver a presionar P.

El cambio de ruta es directo, sin crear rutas adicionales de traslado ni enviar
comandos `/is go` o `/back`.

Las vueltas se cuentan al pasar por un radio horizontal de **2 bloques** del
punto de control. Para evitar duplicados, hay que haberse alejado 12 bloques
antes de poder sumar otra. No se exige recorrer el 75 % del trazado. Los
movimientos tipo teletransporte (más de 5 bloques entre dos ticks) no suman
vueltas.

En `K > Vueltas P` se introducen cantidades independientes de 1 a 100000 para
cada ruta. En `K > Pausas P` se introducen los tiempos mínimo y máximo de cada
ruta en minutos:segundos (por ejemplo, `05:42`). No se permiten segundos mayores
a 59 ni mínimos mayores que sus máximos.

Estos ajustes quedan guardados en `config/crop-route/p-settings.json` y generan
un mensaje en el chat al guardar. Los cambios se aplican en el próximo inicio
de P, no durante el ciclo en curso. Durante las pausas se sueltan las teclas
simuladas hasta reanudar la ruta.

La detección de mensajes/eventos de competencia de versiones antiguas queda
eliminada.

## Rutas guardadas

El mod espera encontrar `RTAFK`, `verruga11` y `verruga1` en el archivo
`config/crop-route/routes.json`. **No sustituyas ni borres tus rutas** al
actualizar el código. Haz una copia de seguridad antes de la primera prueba.

## Requisitos

- Minecraft 1.20.4
- Fabric Loader 0.15.11 o compatible
- Fabric API 0.97.2+1.20.4
- Java 17

## Compilación en GitHub

Consulta `GITHUB-PASOS.txt` para ver exactamente los **5 archivos** que debes
reemplazar en el repositorio y cómo ejecutar GitHub Actions para producir
`crop-route-1.3.0.jar`.

El archivo ZIP entregado es código fuente, no un JAR compilado. Se comprobó
localmente su compilación Java con clases simuladas y una prueba de lógica
RTAFK → verruga11 → verruga1 → RTAFK. Falta la compilación real con Fabric en
GitHub Actions y la prueba dentro del juego.

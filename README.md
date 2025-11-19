# Simulador de Torre de Control Aéreo

Aplicación de consola que simula el funcionamiento básico de una torre de control. Se gestionan aterrizajes y despegues concurrentes mediante hilos, colas de prioridad y `Lock/Condition` de Java para sincronizar el acceso a las pistas disponibles.

## Requisitos
- Java 17 o superior
- Maven 3.9+

## Ejecución
Al iniciar se solicitará:
1. Número de pistas disponibles (mínimo 2).
2. Número de aviones a simular (mínimo 10).

Cada avión se crea en un hilo independiente con un estado inicial (`EN_VUELO` o `EN_TERMINAL`) y un nivel de combustible aleatorio. El monitor principal imprime cada segundo la situación de los aviones y de las pistas.

## Estructura principal
- `Main`: actúa como punto de entrada. Solicita por consola el número de pistas y de aviones, crea la instancia de `ControlTower` y genera cada `Plane` en su propio hilo. Además lanza un hilo monitor que imprime cada segundo el estado de todos los aviones y las pistas disponibles, ayudando a visualizar la simultaneidad.
- `Plane`: implementa `Runnable`. Cada avión lleva un identificador (`code`), un estado (`condition`), banderas de aterrizaje/despegue y un nivel de combustible aleatorio. En `run()` decide si debe pedir aterrizaje o despegue según su estado inicial, espera el tiempo simulado y actualiza su condición. Expone getters/setters para que la torre controle su flujo.
- `ControlTower`: encapsula la lógica de sincronización. Mantiene colas de espera para aterrizar/despegar, un `ReentrantLock` y dos `Condition` para coordinar a los hilos. Asigna pistas disponibles (`runwaysInUse`) y da prioridad al avión con menos combustible mediante `getHighestPriorityPlane()`. Los métodos `requestLanding` y `requestTakeoff` bloquean hasta disponer de pista, simulan el proceso y liberan la pista notificando a los demás hilos.
- `AircraftCondition`: enum que normaliza los posibles estados (`EN_VUELO`, `ESPERANDO`, `ATERRIZAR`, `EN_TERMINAL`, `DESPEGAR`), evitando valores mágicos y facilitando la lectura del flujo de estados.


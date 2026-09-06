# Ajedrez

Juego de **Ajedrez** en Java que se juega contra la computadora, con interfaz gráfica **Swing** e **inteligencia artificial basada en minimax con poda alfa-beta**.

## Características

- Tablero estándar **8 x 8** con todas las piezas (torre, caballo, alfil, reina, rey y peón)
- Jugador contra la computadora con IA minimax + poda alfa-beta (`IAAjedrez.java`)
- Interfaz gráfica en Swing (`TableroAjedrez.java`)
- Lógica del juego y movimientos de piezas separados y testeables (`Ajedrez.java`, `Pieza.java`)
- Pruebas unitarias con **JUnit 5**

## Compilar y ejecutar

Requisitos: Java 17+ y Maven.

```bash
mvn clean package          # compila y ejecuta las pruebas
java -jar target/ajedrez-1.0.jar   # ejecuta el juego
```

También puedes iniciarlo directamente con:

```bash
mvn exec:java
```

## Probar

```bash
mvn test
```

Incluye pruebas unitarias para la lógica del juego, la IA y los movimientos de las piezas.
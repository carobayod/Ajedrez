# AGENTS.md — Ajedrez

Juego de **ajedrez** en Java Swing con IA minimax. Último de la serie
**Triqui → Damas → Ajedrez → Tetris** (cuarto de la serie). Nació el 2026-08-31.

> Hereda de Triqui y Damas la separación lógica/vista y las pruebas JUnit 5.
> A diferencia de Tetris, aquí la IA es **el núcleo**: el código más pesado
> son las reglas de ajedrez (`Ajedrez.java`, 591 líneas).

---

## Fuente única

| Archivo | Rol | Líneas |
|---|---|---|
| `src/main/java/Ajedrez.java` | **todas las reglas** — movimientos legales, jaque/mate/ahogado, enroque, en passant, promoción, evaluación | 591 |
| `src/main/java/IAAjedrez.java` | **minimax con poda alfa-beta** (73 líneas) | 73 |
| `src/main/java/TableroAjedrez.java` | la ventana Swing: selección, turnos, diagonales | 318 |
| `src/main/java/Pieza.java` | valor de pieza: tipo, color, `seHaMovido`, valor, símbolo | 91 |
| `src/main/java/PosMatris.java` | coordenada (fila, columna), usada para jugadas y casillas | 44 |
| `src/main/java/TipoPieza.java` | enum: PEON, TORRE, CABALLO, ALFIL, DAMA, REY | 8 |
| `src/main/java/ColorPieza.java` | enum: BLANCO, NEGRO (con `opuesto()`) | 8 |

Regla: **la lógica nunca toca Swing.** `Ajedrez` no importa nada de `javax.swing`.
Si una regla de ajedrez cambia, va en `Ajedrez.java`. La UI (`TableroAjedrez`)
solo refleja y delega.

## Compilar, probar, ejecutar

```bash
mvn clean package                          # compila y corre las pruebas (46 tests)
java -jar target/ajedrez-1.0.jar           # ejecuta el juego
```

Java 17+, Maven, JUnit 5. Tests:
```bash
rg 'Tests run' target/surefire-reports/*.txt
```
Reparto actual: `TestAjedrez` 33, `TestPieza` 7, `TestIAAjedrez` 6.

## Cómo funciona la IA

- `IAAjedrez.mejorJugada(juego)` genera **todas** las jugadas legales, las
  evalúa con minimax + poda alfa-beta, y entre las empatadas **elige al azar**
  (para que no sea siempre la misma jugada).
- Profundidad según el combo de dificultad (`TableroAjedrez:105`): fácil=2,
  media=3, difícil=4.
- El turno de la computadora corre en un **`SwingWorker`** para no congelar la
  interfaz (`TableroAjedrez:270`).
- **Clonación:** cada nodo del árbol copia el juego con
  `new Ajedrez(tablero, turno, casillaEnPassant)`. Si clonás mal, se pierde
  estado y el minimax razona sobre una posición falsa (ver `copiarJuego`).

## Evaluación de posición (`Ajedrez.evaluar`)

- Mate a la IA = **+100000**; pierde = **−100000**; empate = 0.
- Si no: suma `valor + bonusPosicional` de sus piezas y resta el del rival.
- `bonusPosicional`: peones incentivados hacia el centro, caballos/alfiles en el
  centro (+15).
- En jaque al rival **+30**; en jaque propio **−30**.

## Reglas de ajedrez: dónde vive cada cosa (en `Ajedrez.java`)

Todo esto está en la lógica, no en la vista:
- `esMovimientoValido` — valida cualquier movimiento.
- `esEnroqueValido` — enroque corto y largo (comprueba color de torre y que
  no se haya movido).
- `casillaEnPassant` — captura al peón al vuelo (se conserva al clonar).
- `ejecutarMovimiento(f,c,f,c,TipoPieza)` — la sobrecarga con pieza es para la
  **promoción** del peón.
- `estaEnJaque` / `esJaqueMate` / `esAhogado` / `tieneMovimientosLegales`.
- `obtenerMovimientosLegales` / `obtenerTodasLasJugadas(color)` — la IA usa
  estas dos.

## Convenciones

- Estilo **español**: `iFila`, `iColumna`, `posicionMatris`, comentarios en
  español, mensajes de interfaz en español.
- Los enums son la fuente de verdad de piezas y colores; no usar cadenas.
- `Pieza.getValor()` y `getSymbolo()` devuelven Unicode (♞♝♛♜♟) para pintar.

## Pruebas (46, todas verdes)

`TestAjedrez` (33) es el grueso: movimientos de cada pieza, jaque/mate/ahogado,
enroque (incluido el largo y el inválido con torre enemiga), en passant,
promoción. `TestPieza` (7): valores, símbolos, clonado. `TestIAAjedrez` (6):
la IA elige una jugada legal y respeta la profundidad.

## Trampas / decisiones de diseño

- **La interfaz se controla por selección de casilla** (clic origen → clic
  destino), no arrastrando. La promoción abre un diálogo (`mostrarDialogoPromocion`),
  que **devuelve boolean** y debe continuar el flujo.
- La diagonal de una casilla es `dirigirSeleccionNueva`, que calcula los destinos
  a partir del tipo de la pieza.
- El tablero se pinta con **`JButton`**, igual que Tetris/Damas/Triqui (no canvas).
  Cambiar a canvas es un rediseño, no un arreglo.

## Estado

- Repo limpio. Último commit `fa7023a`.
- **Lógica de ajedrez completa**: movimientos, jaque/mate/ahogado, enroque,
  en passant, promoción. IA minimax con poda alfa-beta.
- **Falta** (si algún día esto se convierte en producto, como Triqui): manual,
  diagramas, `AGENTS.md` de pipeline, portada. La lógica está; lo demás no.

## Ver también

- Repos hermanos: `../Triqui` (el único con libro y pipeline completo),
  `../Damas`, `../Tetris`.
- `README.md` — descripción y compilación.
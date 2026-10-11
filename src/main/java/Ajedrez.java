import java.util.ArrayList;
import java.util.List;

public class Ajedrez {

    public static final int TAMANIO = 8;
    private Pieza[][] tablero;
    private ColorPieza turnoActual;
    private boolean juegoTerminado;
    private ColorPieza ganador;
    private String motivoFin;
    private int contadorMovimientos;
    private int contadorMedioMovimientos;
    private PosMatris casillaEnPassant;
    private List<String> historial;

    public Ajedrez() {
        tablero = new Pieza[TAMANIO][TAMANIO];
        turnoActual = ColorPieza.BLANCO;
        juegoTerminado = false;
        ganador = null;
        motivoFin = "";
        contadorMovimientos = 0;
        contadorMedioMovimientos = 0;
        casillaEnPassant = null;
        historial = new ArrayList<>();
        inicializarTablero();
    }

    public Ajedrez(Pieza[][] tablero, ColorPieza turno) {
        this(tablero, turno, null);
    }

    public Ajedrez(Pieza[][] tablero, ColorPieza turno, PosMatris enPassant) {
        this.tablero = new Pieza[TAMANIO][TAMANIO];
        for (int i = 0; i < TAMANIO; i++) {
            for (int j = 0; j < TAMANIO; j++) {
                this.tablero[i][j] = tablero[i][j] != null ? tablero[i][j].clonar() : null;
            }
        }
        this.turnoActual = turno;
        this.juegoTerminado = false;
        this.ganador = null;
        this.motivoFin = "";
        this.contadorMovimientos = 0;
        this.contadorMedioMovimientos = 0;
        this.casillaEnPassant = enPassant != null
            ? new PosMatris(enPassant.getiFila(), enPassant.getiColumna())
            : null;
        this.historial = new ArrayList<>();
    }

    private void inicializarTablero() {
        for (int i = 0; i < TAMANIO; i++) {
            for (int j = 0; j < TAMANIO; j++) {
                tablero[i][j] = null;
            }
        }
        ColorPieza[] colores = {ColorPieza.NEGRO, ColorPieza.BLANCO};
        int[] filasPiezas = {0, 7};
        int[] filasPeones = {1, 6};

        TipoPieza[] ordenPiezas = {
            TipoPieza.TORRE, TipoPieza.CABALLO, TipoPieza.ALFIL, TipoPieza.DAMA,
            TipoPieza.REY, TipoPieza.ALFIL, TipoPieza.CABALLO, TipoPieza.TORRE
        };

        for (int c = 0; c < 2; c++) {
            for (int col = 0; col < TAMANIO; col++) {
                tablero[filasPiezas[c]][col] = new Pieza(ordenPiezas[col], colores[c]);
                tablero[filasPeones[c]][col] = new Pieza(TipoPieza.PEON, colores[c]);
            }
        }
    }

    public Pieza getPieza(int fila, int columna) {
        if (fila < 0 || fila >= TAMANIO || columna < 0 || columna >= TAMANIO) return null;
        return tablero[fila][columna];
    }

    public void setPieza(int fila, int columna, Pieza pieza) {
        if (fila >= 0 && fila < TAMANIO && columna >= 0 && columna < TAMANIO) {
            tablero[fila][columna] = pieza;
        }
    }

    public boolean esMovimientoValido(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        if (!enRango(filaOrigen, colOrigen) || !enRango(filaDestino, colDestino)) return false;
        if (filaOrigen == filaDestino && colOrigen == colDestino) return false;

        Pieza pieza = tablero[filaOrigen][colOrigen];
        if (pieza == null) return false;
        if (pieza.getColor() != turnoActual) return false;

        Pieza destino = tablero[filaDestino][colDestino];
        if (destino != null && destino.getColor() == turnoActual) return false;
        if (destino != null && destino.getTipo() == TipoPieza.REY) return false;

        int diffFila = filaDestino - filaOrigen;
        int diffCol = colDestino - colOrigen;
        int absDiffFila = Math.abs(diffFila);
        int absDiffCol = Math.abs(diffCol);

        switch (pieza.getTipo()) {
            case PEON:
                return esMovimientoPeonValido(filaOrigen, colOrigen, filaDestino, colDestino, pieza, destino, diffFila, diffCol, absDiffFila, absDiffCol);
            case TORRE:
                return (absDiffFila == 0 || absDiffCol == 0) && !hayPiezasEnLinea(filaOrigen, colOrigen, filaDestino, colDestino);
            case CABALLO:
                return (absDiffFila == 2 && absDiffCol == 1) || (absDiffFila == 1 && absDiffCol == 2);
            case ALFIL:
                return absDiffFila == absDiffCol && !hayPiezasEnLinea(filaOrigen, colOrigen, filaDestino, colDestino);
            case DAMA:
                return ((absDiffFila == 0 || absDiffCol == 0) || (absDiffFila == absDiffCol))
                    && !hayPiezasEnLinea(filaOrigen, colOrigen, filaDestino, colDestino);
            case REY:
                return esMovimientoReyValido(filaOrigen, colOrigen, filaDestino, colDestino, absDiffFila, absDiffCol);
            default:
                return false;
        }
    }

    private boolean esMovimientoPeonValido(int fO, int cO, int fD, int cD, Pieza peon, Pieza destino, int diffFila, int diffCol, int absDiffFila, int absDiffCol) {
        int direccion = peon.getColor() == ColorPieza.BLANCO ? -1 : 1;
        int filaInicio = peon.getColor() == ColorPieza.BLANCO ? 6 : 1;

        if (diffCol == 0 && destino == null) {
            if (diffFila == direccion) return true;
            if (fO == filaInicio && diffFila == 2 * direccion && tablero[fO + direccion][cO] == null) return true;
        }

        if (absDiffCol == 1 && diffFila == direccion) {
            if (destino != null) return true;
            if (casillaEnPassant != null && fD == casillaEnPassant.getiFila() && cD == casillaEnPassant.getiColumna()) return true;
        }

        return false;
    }

    private boolean esMovimientoReyValido(int fO, int cO, int fD, int cD, int absDiffFila, int absDiffCol) {
        if (absDiffFila <= 1 && absDiffCol <= 1) return true;
        return esEnroqueValido(fO, cO, fD, cD);
    }

    public boolean esEnroqueValido(int fO, int cO, int fD, int cD) {
        Pieza rey = tablero[fO][cO];
        if (rey == null || rey.getTipo() != TipoPieza.REY || rey.isSeHaMovido()) return false;
        if (estaEnJaque(rey.getColor())) return false;

        int fila = rey.getColor() == ColorPieza.BLANCO ? 7 : 0;
        if (fO != fila || fD != fila) return false;

        if (cD == 6) {
            Pieza torre = tablero[fila][7];
            if (torre == null || torre.getTipo() != TipoPieza.TORRE || torre.getColor() != rey.getColor() || torre.isSeHaMovido()) return false;
            if (tablero[fila][5] != null || tablero[fila][6] != null) return false;
            if (casillaAmenazadaPor(fila, 5, rey.getColor().opuesto())) return false;
            if (casillaAmenazadaPor(fila, 6, rey.getColor().opuesto())) return false;
            return true;
        }

        if (cD == 2) {
            Pieza torre = tablero[fila][0];
            if (torre == null || torre.getTipo() != TipoPieza.TORRE || torre.getColor() != rey.getColor() || torre.isSeHaMovido()) return false;
            if (tablero[fila][1] != null || tablero[fila][2] != null || tablero[fila][3] != null) return false;
            if (casillaAmenazadaPor(fila, 3, rey.getColor().opuesto())) return false;
            if (casillaAmenazadaPor(fila, 2, rey.getColor().opuesto())) return false;
            return true;
        }

        return false;
    }

    private boolean hayPiezasEnLinea(int fO, int cO, int fD, int cD) {
        int dirF = Integer.signum(fD - fO);
        int dirC = Integer.signum(cD - cO);
        int f = fO + dirF;
        int c = cO + dirC;

        while (f != fD || c != cD) {
            if (tablero[f][c] != null) return true;
            f += dirF;
            c += dirC;
        }
        return false;
    }

    public boolean estaEnJaque(ColorPieza color) {
        PosMatris posRey = buscarRey(color);
        if (posRey == null) return false;
        ColorPieza rival = color.opuesto();
        return casillaAmenazadaPor(posRey.getiFila(), posRey.getiColumna(), rival);
    }

    public boolean casillaAmenazadaPor(int fila, int columna, ColorPieza colorAtacante) {
        for (int f = 0; f < TAMANIO; f++) {
            for (int c = 0; c < TAMANIO; c++) {
                Pieza pieza = tablero[f][c];
                if (pieza != null && pieza.getColor() == colorAtacante) {
                    int diffF = Math.abs(fila - f);
                    int diffC = Math.abs(columna - c);
                    if (pieza.getTipo() == TipoPieza.PEON) {
                        int dir = pieza.getColor() == ColorPieza.BLANCO ? -1 : 1;
                        if (f + dir == fila && (c - 1 == columna || c + 1 == columna)) return true;
                    } else if (pieza.getTipo() == TipoPieza.CABALLO) {
                        if ((diffF == 2 && diffC == 1) || (diffF == 1 && diffC == 2)) return true;
                    } else if (pieza.getTipo() == TipoPieza.REY) {
                        if (diffF <= 1 && diffC <= 1) return true;
                    } else {
                        boolean enLinea = false;
                        if (pieza.getTipo() == TipoPieza.TORRE || pieza.getTipo() == TipoPieza.DAMA) {
                            if (f == fila || c == columna) enLinea = true;
                        }
                        if (pieza.getTipo() == TipoPieza.ALFIL || pieza.getTipo() == TipoPieza.DAMA) {
                            if (diffF == diffC) enLinea = true;
                        }
                        if (enLinea && !hayPiezasEnLinea(f, c, fila, columna)) return true;
                    }
                }
            }
        }
        return false;
    }

    private PosMatris buscarRey(ColorPieza color) {
        for (int f = 0; f < TAMANIO; f++) {
            for (int c = 0; c < TAMANIO; c++) {
                Pieza p = tablero[f][c];
                if (p != null && p.getTipo() == TipoPieza.REY && p.getColor() == color) {
                    return new PosMatris(f, c);
                }
            }
        }
        return null;
    }

    public boolean esJaqueMate(ColorPieza color) {
        return estaEnJaque(color) && !tieneMovimientosLegales(color);
    }

    public boolean esAhogado(ColorPieza color) {
        return !estaEnJaque(color) && !tieneMovimientosLegales(color);
    }

    public boolean tieneMovimientosLegales(ColorPieza color) {
        for (int f = 0; f < TAMANIO; f++) {
            for (int c = 0; c < TAMANIO; c++) {
                Pieza pieza = tablero[f][c];
                if (pieza != null && pieza.getColor() == color) {
                    List<PosMatris> movimientos = obtenerMovimientosLegales(f, c);
                    if (!movimientos.isEmpty()) return true;
                }
            }
        }
        return false;
    }

    public List<PosMatris> obtenerMovimientosLegales(int fila, int columna) {
        List<PosMatris> movimientos = new ArrayList<>();
        Pieza pieza = tablero[fila][columna];
        if (pieza == null || pieza.getColor() != turnoActual) return movimientos;

        List<PosMatris> candidatos = obtenerMovimientosCandidatos(fila, columna);
        for (PosMatris destino : candidatos) {
            if (!dejaReyEnJaque(fila, columna, destino.getiFila(), destino.getiColumna())) {
                movimientos.add(destino);
            }
        }

        ColorPieza color = pieza.getColor();
        if (!pieza.isSeHaMovido() && pieza.getTipo() == TipoPieza.REY && !estaEnJaque(color)) {
            int filaRey = color == ColorPieza.BLANCO ? 7 : 0;
            if (fila == filaRey && columna == 4) {
                if (esEnroqueValido(fila, columna, filaRey, 6)) movimientos.add(new PosMatris(filaRey, 6));
                if (esEnroqueValido(fila, columna, filaRey, 2)) movimientos.add(new PosMatris(filaRey, 2));
            }
        }

        return movimientos;
    }

    public List<PosMatris> obtenerMovimientosCandidatos(int fila, int columna) {
        List<PosMatris> movimientos = new ArrayList<>();
        Pieza pieza = tablero[fila][columna];
        if (pieza == null) return movimientos;

        switch (pieza.getTipo()) {
            case PEON:
                agregarMovimientosPeon(fila, columna, pieza, movimientos);
                break;
            case TORRE:
                agregarMovimientosLinea(fila, columna, pieza, movimientos, new int[][]{{0,1},{0,-1},{1,0},{-1,0}});
                break;
            case CABALLO:
                agregarMovimientosCaballo(fila, columna, pieza, movimientos);
                break;
            case ALFIL:
                agregarMovimientosLinea(fila, columna, pieza, movimientos, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
                break;
            case DAMA:
                agregarMovimientosLinea(fila, columna, pieza, movimientos, new int[][]{{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}});
                break;
            case REY:
                agregarMovimientosRey(fila, columna, pieza, movimientos);
                break;
        }

        if (casillaEnPassant != null && pieza.getTipo() == TipoPieza.PEON) {
            int dir = pieza.getColor() == ColorPieza.BLANCO ? -1 : 1;
            if (fila + dir == casillaEnPassant.getiFila() && Math.abs(columna - casillaEnPassant.getiColumna()) == 1) {
                movimientos.add(new PosMatris(casillaEnPassant.getiFila(), casillaEnPassant.getiColumna()));
            }
        }

        return movimientos;
    }

    private void agregarMovimientosPeon(int f, int c, Pieza peon, List<PosMatris> movimientos) {
        int dir = peon.getColor() == ColorPieza.BLANCO ? -1 : 1;
        int filaInicio = peon.getColor() == ColorPieza.BLANCO ? 6 : 1;

        if (enRango(f + dir, c) && tablero[f + dir][c] == null) {
            movimientos.add(new PosMatris(f + dir, c));
            if (f == filaInicio && tablero[f + 2 * dir][c] == null) {
                movimientos.add(new PosMatris(f + 2 * dir, c));
            }
        }

        for (int dc : new int[]{-1, 1}) {
            int nf = f + dir;
            int nc = c + dc;
            if (enRango(nf, nc)) {
                Pieza dest = tablero[nf][nc];
                if (dest != null && dest.getColor() != peon.getColor()) {
                    movimientos.add(new PosMatris(nf, nc));
                }
            }
        }
    }

    private void agregarMovimientosLinea(int f, int c, Pieza pieza, List<PosMatris> movimientos, int[][] direcciones) {
        for (int[] dir : direcciones) {
            int nf = f + dir[0];
            int nc = c + dir[1];
            while (enRango(nf, nc)) {
                Pieza dest = tablero[nf][nc];
                if (dest == null) {
                    movimientos.add(new PosMatris(nf, nc));
                } else {
                    if (dest.getColor() != pieza.getColor()) {
                        movimientos.add(new PosMatris(nf, nc));
                    }
                    break;
                }
                nf += dir[0];
                nc += dir[1];
            }
        }
    }

    private void agregarMovimientosCaballo(int f, int c, Pieza pieza, List<PosMatris> movimientos) {
        int[][] saltos = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] s : saltos) {
            int nf = f + s[0];
            int nc = c + s[1];
            if (enRango(nf, nc)) {
                Pieza dest = tablero[nf][nc];
                if (dest == null || dest.getColor() != pieza.getColor()) {
                    movimientos.add(new PosMatris(nf, nc));
                }
            }
        }
    }

    private void agregarMovimientosRey(int f, int c, Pieza rey, List<PosMatris> movimientos) {
        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (df == 0 && dc == 0) continue;
                int nf = f + df;
                int nc = c + dc;
                if (enRango(nf, nc)) {
                    Pieza dest = tablero[nf][nc];
                    if (dest == null || dest.getColor() != rey.getColor()) {
                        movimientos.add(new PosMatris(nf, nc));
                    }
                }
            }
        }
    }

    public boolean dejaReyEnJaque(int fO, int cO, int fD, int cD) {
        Pieza piezaOrigen = tablero[fO][cO];
        Pieza piezaDestino = tablero[fD][cD];

        Pieza capturadaEnPassant = null;
        PosMatris posCapturaEP = null;
        if (piezaOrigen.getTipo() == TipoPieza.PEON && casillaEnPassant != null
            && fD == casillaEnPassant.getiFila() && cD == casillaEnPassant.getiColumna()
            && tablero[fD][cD] == null && cO != cD) {
            posCapturaEP = new PosMatris(fO, cD);
            capturadaEnPassant = tablero[fO][cD];
            tablero[fO][cD] = null;
        }

        tablero[fD][cD] = piezaOrigen;
        tablero[fO][cO] = null;

        boolean enJaque = estaEnJaque(piezaOrigen.getColor());

        tablero[fO][cO] = piezaOrigen;
        tablero[fD][cD] = piezaDestino;

        if (capturadaEnPassant != null && posCapturaEP != null) {
            tablero[posCapturaEP.getiFila()][posCapturaEP.getiColumna()] = capturadaEnPassant;
        }

        return enJaque;
    }

    public boolean ejecutarMovimiento(int fO, int cO, int fD, int cD) {
        return ejecutarMovimiento(fO, cO, fD, cD, TipoPieza.DAMA);
    }

    public boolean ejecutarMovimiento(int fO, int cO, int fD, int cD, TipoPieza piezaPromocion) {
        if (!esMovimientoValido(fO, cO, fD, cD)) return false;
        List<PosMatris> legales = obtenerMovimientosLegales(fO, cO);
        boolean esLegal = false;
        for (PosMatris m : legales) {
            if (m.getiFila() == fD && m.getiColumna() == cD) { esLegal = true; break; }
        }
        if (!esLegal) return false;

        Pieza pieza = tablero[fO][cO];
        Pieza capturada = tablero[fD][cD];
        PosMatris antiguoEP = casillaEnPassant;
        casillaEnPassant = null;

        if (pieza.getTipo() == TipoPieza.PEON) {
            if (fD == 0 || fD == 7) {
                tablero[fD][cD] = new Pieza(piezaPromocion, pieza.getColor());
                tablero[fO][cO] = null;
            } else if (capturada == null && cO != cD) {
                tablero[fD][cD] = pieza;
                tablero[fO][cO] = null;
                tablero[fO][cD] = null;
            } else {
                int diffFila = fD - fO;
                if (Math.abs(diffFila) == 2) {
                    casillaEnPassant = new PosMatris((fO + fD) / 2, cD);
                }
                tablero[fD][cD] = pieza;
                tablero[fO][cO] = null;
            }
        } else if (pieza.getTipo() == TipoPieza.REY && Math.abs(cD - cO) == 2) {
            int fila = pieza.getColor() == ColorPieza.BLANCO ? 7 : 0;
            tablero[fD][cD] = pieza;
            tablero[fO][cO] = null;
            if (cD == 6) {
                tablero[fila][5] = tablero[fila][7];
                tablero[fila][7] = null;
                if (tablero[fila][5] != null) tablero[fila][5].setSeHaMovido(true);
            } else if (cD == 2) {
                tablero[fila][3] = tablero[fila][0];
                tablero[fila][0] = null;
                if (tablero[fila][3] != null) tablero[fila][3].setSeHaMovido(true);
            }
        } else {
            tablero[fD][cD] = pieza;
            tablero[fO][cO] = null;
        }

        pieza.setSeHaMovido(true);
        if (capturada != null || pieza.getTipo() == TipoPieza.PEON) {
            contadorMedioMovimientos = 0;
        } else {
            contadorMedioMovimientos++;
        }
        contadorMovimientos++;

        ColorPieza rival = turnoActual.opuesto();
        turnoActual = rival;

        if (esJaqueMate(turnoActual)) {
            juegoTerminado = true;
            ganador = rival;
            motivoFin = "Jaque mate";
        } else if (esAhogado(turnoActual)) {
            juegoTerminado = true;
            motivoFin = "Ahogado - Tablas";
        } else if (contadorMedioMovimientos >= 100) {
            juegoTerminado = true;
            motivoFin = "Regla de 50 movimientos - Tablas";
        }

        return true;
    }

    public List<int[]> obtenerTodasLasJugadas(ColorPieza color) {
        List<int[]> jugadas = new ArrayList<>();
        for (int f = 0; f < TAMANIO; f++) {
            for (int c = 0; c < TAMANIO; c++) {
                Pieza pieza = tablero[f][c];
                if (pieza != null && pieza.getColor() == color) {
                    ColorPieza turnoGuardado = turnoActual;
                    turnoActual = color;
                    List<PosMatris> movimientos = obtenerMovimientosLegales(f, c);
                    turnoActual = turnoGuardado;
                    for (PosMatris m : movimientos) {
                        jugadas.add(new int[]{f, c, m.getiFila(), m.getiColumna()});
                    }
                }
            }
        }
        return jugadas;
    }

    public int evaluar(ColorPieza colorIA) {
        if (juegoTerminado) {
            if (ganador == colorIA) return 100000;
            if (ganador != null) return -100000;
            return 0;
        }

        int puntaje = 0;
        for (int f = 0; f < TAMANIO; f++) {
            for (int c = 0; c < TAMANIO; c++) {
                Pieza pieza = tablero[f][c];
                if (pieza != null) {
                    int valor = pieza.getValor();
                    int bonusPosicion = bonusPosicional(pieza, f, c);
                    if (pieza.getColor() == colorIA) {
                        puntaje += valor + bonusPosicion;
                    } else {
                        puntaje -= valor + bonusPosicion;
                    }
                }
            }
        }

        if (estaEnJaque(colorIA.opuesto())) puntaje += 30;
        if (estaEnJaque(colorIA)) puntaje -= 30;

        return puntaje;
    }

    private int bonusPosicional(Pieza pieza, int fila, int columna) {
        if (pieza.getTipo() == TipoPieza.PEON) {
            int filaCentro = pieza.getColor() == ColorPieza.BLANCO ? 3 : 4;
            int bonus = (filaCentro - fila) * (pieza.getColor() == ColorPieza.BLANCO ? -5 : 5);
            if (columna >= 2 && columna <= 5) bonus += 10;
            return Math.abs(bonus);
        }
        if (pieza.getTipo() == TipoPieza.CABALLO || pieza.getTipo() == TipoPieza.ALFIL) {
            int centro = (fila >= 2 && fila <= 5 && columna >= 2 && columna <= 5) ? 15 : 0;
            return centro;
        }
        return 0;
    }

    private boolean enRango(int fila, int columna) {
        return fila >= 0 && fila < TAMANIO && columna >= 0 && columna < TAMANIO;
    }

    public Pieza[][] getTablero() { return tablero; }
    public ColorPieza getTurnoActual() { return turnoActual; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public ColorPieza getGanador() { return ganador; }
    public String getMotivoFin() { return motivoFin; }
    public int getContadorMovimientos() { return contadorMovimientos; }
    public PosMatris getCasillaEnPassant() { return casillaEnPassant; }
    public List<String> getHistorial() { return historial; }

    public void setTurnoActual(ColorPieza turno) { this.turnoActual = turno; }

    public void imprimirTablero() {
        System.out.println("  a b c d e f g h");
        for (int f = 0; f < TAMANIO; f++) {
            System.out.print((8 - f) + " ");
            for (int c = 0; c < TAMANIO; c++) {
                Pieza p = tablero[f][c];
                if (p == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(p.getSymbolo() + " ");
                }
            }
            System.out.println((8 - f));
        }
        System.out.println("  a b c d e f g h");
    }
}

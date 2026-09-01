import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestAjedrez {

    private Ajedrez juego;

    @BeforeEach
    public void setup() {
        juego = new Ajedrez();
    }

    @Test
    public void inicializarTablero() {
        assertEquals(TipoPieza.TORRE, juego.getPieza(0, 0).getTipo());
        assertEquals(ColorPieza.NEGRO, juego.getPieza(0, 0).getColor());
        assertEquals(TipoPieza.TORRE, juego.getPieza(7, 0).getTipo());
        assertEquals(ColorPieza.BLANCO, juego.getPieza(7, 0).getColor());
        assertEquals(TipoPieza.REY, juego.getPieza(7, 4).getTipo());
        assertEquals(ColorPieza.BLANCO, juego.getPieza(7, 4).getColor());
        assertEquals(TipoPieza.REY, juego.getPieza(0, 4).getTipo());
        assertEquals(ColorPieza.NEGRO, juego.getPieza(0, 4).getColor());
        assertEquals(TipoPieza.PEON, juego.getPieza(1, 0).getTipo());
        assertEquals(ColorPieza.NEGRO, juego.getPieza(1, 0).getColor());
        assertEquals(TipoPieza.PEON, juego.getPieza(6, 0).getTipo());
        assertEquals(ColorPieza.BLANCO, juego.getPieza(6, 0).getColor());
        assertNull(juego.getPieza(4, 4));
    }

    @Test
    public void turnoInicialBlanco() {
        assertEquals(ColorPieza.BLANCO, juego.getTurnoActual());
        assertFalse(juego.isJuegoTerminado());
        assertNull(juego.getGanador());
    }

    @Test
    public void peonBlancoAvanzaUnaCasilla() {
        assertTrue(juego.esMovimientoValido(6, 4, 5, 4));
    }

    @Test
    public void peonBlancoAvanzaDosCasillasInicial() {
        assertTrue(juego.esMovimientoValido(6, 4, 4, 4));
    }

    @Test
    public void peonBlancoNoPuedeAvanzarDosDespues() {
        juego.ejecutarMovimiento(6, 4, 5, 4);
        assertTrue(juego.getTurnoActual() == ColorPieza.NEGRO);
        juego.ejecutarMovimiento(1, 4, 2, 4);
        assertTrue(juego.getTurnoActual() == ColorPieza.BLANCO);
        assertTrue(juego.esMovimientoValido(5, 4, 4, 4));
        assertFalse(juego.esMovimientoValido(5, 4, 3, 4));
    }

    @Test
    public void peonNoPuedeAvanzarHaciaAtras() {
        assertFalse(juego.esMovimientoValido(1, 4, 2, 4));
    }

    @Test
    public void peonNoPuedeCapturarEnBlando() {
        assertFalse(juego.esMovimientoValido(6, 0, 5, 1));
    }

    @Test
    public void peonCapturaEnDiagonal() {
        juego = tableroPersonalizado();
        juego.setPieza(4, 4, new Pieza(TipoPieza.PEON, ColorPieza.BLANCO));
        juego.setPieza(3, 5, new Pieza(TipoPieza.PEON, ColorPieza.NEGRO));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertTrue(juego.esMovimientoValido(4, 4, 3, 5));
    }

    @Test
    public void caballoSaltoEnL() {
        assertFalse(juego.esMovimientoValido(7, 1, 6, 1));
        assertTrue(juego.esMovimientoValido(7, 1, 5, 2));
        assertTrue(juego.esMovimientoValido(7, 1, 5, 0));
        assertTrue(juego.esMovimientoValido(7, 6, 5, 5));
    }

    @Test
    public void torreHorizontal() {
        juego = tableroPersonalizado();
        juego.setPieza(4, 4, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertTrue(juego.esMovimientoValido(4, 4, 4, 7));
        assertTrue(juego.esMovimientoValido(4, 4, 4, 0));
    }

    @Test
    public void torreBloqueadaPorPiezaPropia() {
        juego = tableroPersonalizado();
        juego.setPieza(4, 4, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO));
        juego.setPieza(4, 6, new Pieza(TipoPieza.PEON, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertFalse(juego.esMovimientoValido(4, 4, 4, 7));
    }

    @Test
    public void alfilDiagonal() {
        juego = tableroPersonalizado();
        juego.setPieza(4, 4, new Pieza(TipoPieza.ALFIL, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertTrue(juego.esMovimientoValido(4, 4, 1, 1));
        assertTrue(juego.esMovimientoValido(4, 4, 6, 6));
        assertFalse(juego.esMovimientoValido(4, 4, 4, 5));
    }

    @Test
    public void damaMovimiento() {
        juego = tableroPersonalizado();
        juego.setPieza(4, 4, new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertTrue(juego.esMovimientoValido(4, 4, 4, 7));
        assertTrue(juego.esMovimientoValido(4, 4, 1, 1));
        assertTrue(juego.esMovimientoValido(4, 4, 0, 4));
        assertFalse(juego.esMovimientoValido(4, 4, 6, 5));
    }

    @Test
    public void reyUnaCasilla() {
        juego = tableroPersonalizado();
        juego.setPieza(4, 4, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertTrue(juego.esMovimientoValido(4, 4, 5, 5));
        assertTrue(juego.esMovimientoValido(4, 4, 3, 4));
        assertFalse(juego.esMovimientoValido(4, 4, 6, 6));
    }

    @Test
    public void detectarJaque() {
        juego = tableroPersonalizado();
        juego.setPieza(4, 4, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setPieza(2, 2, new Pieza(TipoPieza.ALFIL, ColorPieza.NEGRO));
        assertTrue(juego.estaEnJaque(ColorPieza.BLANCO));
    }

    @Test
    public void movimientosLegalesFueraDeJaque() {
        juego = tableroPersonalizado();
        juego.setPieza(0, 0, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setPieza(1, 0, new Pieza(TipoPieza.TORRE, ColorPieza.NEGRO));
        juego.setTurnoActual(ColorPieza.BLANCO);

        assertTrue(juego.estaEnJaque(ColorPieza.BLANCO));

        List<PosMatris> mov = juego.obtenerMovimientosLegales(0, 0);
        boolean puedeMover = false;
        for (PosMatris m : mov) {
            if (m.getiFila() == 0 && m.getiColumna() == 1) puedeMover = true;
        }
        assertTrue(puedeMover);
    }

    @Test
    public void jaqueMate() {
        juego = tableroPersonalizado();
        juego.setPieza(0, 4, new Pieza(TipoPieza.REY, ColorPieza.NEGRO));
        juego.setPieza(1, 3, new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO));
        juego.setPieza(2, 4, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO));
        juego.setPieza(3, 4, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO));

        assertTrue(juego.estaEnJaque(ColorPieza.NEGRO));
        assertTrue(juego.esJaqueMate(ColorPieza.NEGRO));
    }

    @Test
    public void ahogado() {
        juego = tableroPersonalizado();
        juego.setPieza(0, 0, new Pieza(TipoPieza.REY, ColorPieza.NEGRO));
        juego.setPieza(1, 2, new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO));

        assertFalse(juego.estaEnJaque(ColorPieza.NEGRO));
        assertTrue(juego.esAhogado(ColorPieza.NEGRO));
    }

    @Test
    public void enroqueCorto() {
        juego = tableroPersonalizado();
        juego.setPieza(7, 4, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setPieza(7, 7, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.BLANCO);

        assertTrue(juego.esEnroqueValido(7, 4, 7, 6));
        boolean resultado = juego.ejecutarMovimiento(7, 4, 7, 6);
        assertTrue(resultado);
        assertEquals(TipoPieza.REY, juego.getPieza(7, 6).getTipo());
        assertEquals(TipoPieza.TORRE, juego.getPieza(7, 5).getTipo());
        assertNull(juego.getPieza(7, 4));
        assertNull(juego.getPieza(7, 7));
    }

    @Test
    public void enroqueInvalidoSiReyMovido() {
        juego = tableroPersonalizado();
        Pieza rey = new Pieza(TipoPieza.REY, ColorPieza.BLANCO, true);
        juego.setPieza(7, 4, rey);
        juego.setPieza(7, 7, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertFalse(juego.esEnroqueValido(7, 4, 7, 6));
    }

    @Test
    public void enroqueInvalidoSiTorreMovida() {
        juego = tableroPersonalizado();
        juego.setPieza(7, 4, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setPieza(7, 7, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO, true));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertFalse(juego.esEnroqueValido(7, 4, 7, 6));
    }

    @Test
    public void enroqueInvalidoPasaPorJaque() {
        juego = tableroPersonalizado();
        juego.setPieza(7, 4, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setPieza(7, 7, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO));
        juego.setPieza(6, 5, new Pieza(TipoPieza.TORRE, ColorPieza.NEGRO));
        juego.setTurnoActual(ColorPieza.BLANCO);
        assertFalse(juego.esEnroqueValido(7, 4, 7, 6));
    }

    @Test
    public void promocionPeon() {
        juego = tableroPersonalizado();
        juego.setPieza(1, 0, new Pieza(TipoPieza.PEON, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.BLANCO);

        boolean resultado = juego.ejecutarMovimiento(1, 0, 0, 0, TipoPieza.DAMA);
        assertTrue(resultado);
        assertEquals(TipoPieza.DAMA, juego.getPieza(0, 0).getTipo());
        assertEquals(ColorPieza.BLANCO, juego.getPieza(0, 0).getColor());
        assertNull(juego.getPieza(1, 0));
    }

    @Test
    public void capturaEnPassant() {
        juego = tableroPersonalizado();
        juego.setPieza(1, 4, new Pieza(TipoPieza.PEON, ColorPieza.NEGRO));
        juego.setPieza(3, 3, new Pieza(TipoPieza.PEON, ColorPieza.BLANCO));
        juego.setTurnoActual(ColorPieza.NEGRO);

        juego.ejecutarMovimiento(1, 4, 3, 4, TipoPieza.DAMA);
        assertEquals(ColorPieza.BLANCO, juego.getTurnoActual());
        assertNotNull(juego.getCasillaEnPassant());
        assertEquals(2, juego.getCasillaEnPassant().getiFila());
        assertEquals(4, juego.getCasillaEnPassant().getiColumna());

        boolean resultado = juego.ejecutarMovimiento(3, 3, 2, 4, TipoPieza.DAMA);
        assertTrue(resultado);
    }

    @Test
    public void movimientoInvalidoFueraDeRango() {
        assertFalse(juego.esMovimientoValido(-1, 0, 2, 0));
        assertFalse(juego.esMovimientoValido(0, 0, 8, 0));
        assertFalse(juego.esMovimientoValido(6, 4, 6, 4));
    }

    @Test
    public void movimientoDePiezaVacia() {
        assertFalse(juego.esMovimientoValido(4, 4, 3, 4));
    }

    @Test
    public void noMoverPiezaDelRival() {
        assertFalse(juego.esMovimientoValido(1, 0, 2, 0));
    }

    @Test
    public void capturarPiezaDelMismoColor() {
        assertFalse(juego.esMovimientoValido(6, 0, 5, 1));
    }

    @Test
    public void noSePuedeCapturarAlRey() {
        juego = tableroPersonalizado();
        juego.setPieza(7, 4, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setPieza(0, 4, new Pieza(TipoPieza.REY, ColorPieza.NEGRO));
        juego.setPieza(6, 5, new Pieza(TipoPieza.DAMA, ColorPieza.NEGRO));
        juego.setTurnoActual(ColorPieza.NEGRO);

        assertFalse(juego.esMovimientoValido(6, 5, 7, 4));
        assertFalse(juego.ejecutarMovimiento(6, 5, 7, 4));
        assertEquals(TipoPieza.REY, juego.getPieza(7, 4).getTipo());
    }

    @Test
    public void jugadaCompletaCambioDeTurno() {
        assertTrue(juego.ejecutarMovimiento(6, 4, 5, 4));
        assertEquals(ColorPieza.NEGRO, juego.getTurnoActual());
        assertTrue(juego.ejecutarMovimiento(1, 4, 2, 4));
        assertEquals(ColorPieza.BLANCO, juego.getTurnoActual());
    }

    private Ajedrez tableroPersonalizado() {
        Ajedrez juego = new Ajedrez();
        for (int f = 0; f < Ajedrez.TAMANIO; f++) {
            for (int c = 0; c < Ajedrez.TAMANIO; c++) {
                juego.setPieza(f, c, null);
            }
        }
        return juego;
    }
}

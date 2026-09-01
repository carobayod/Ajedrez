import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TestIAAjedrez {

    @Test
    public void mejorJugadaExisteAlInicio() {
        Ajedrez juego = new Ajedrez();
        IAAjedrez ia = new IAAjedrez(2);
        int[] jugada = ia.mejorJugada(juego);
        assertNotNull(jugada);
        assertTrue(juego.esMovimientoValido(jugada[0], jugada[1], jugada[2], jugada[3]));
    }

    @Test
    public void mejorJugadaEsNulaSiNoHayJugadas() {
        Ajedrez juego = new Ajedrez();
        IAAjedrez ia = new IAAjedrez(2);
        for (int f = 0; f < Ajedrez.TAMANIO; f++) {
            for (int c = 0; c < Ajedrez.TAMANIO; c++) {
                juego.setPieza(f, c, null);
            }
        }
        int[] jugada = ia.mejorJugada(juego);
        assertNull(jugada);
    }

    @Test
    public void evaluaPosicionInicialComoEquilibrada() {
        Ajedrez juego = new Ajedrez();
        int puntaje = juego.evaluar(ColorPieza.BLANCO);
        assertTrue(puntaje == 0 || Math.abs(puntaje) < 100);
    }

    @Test
    public void evaluacionVentajaBlancas() {
        Ajedrez juego = new Ajedrez();
        for (int f = 0; f < Ajedrez.TAMANIO; f++) {
            for (int c = 0; c < Ajedrez.TAMANIO; c++) {
                juego.setPieza(f, c, null);
            }
        }
        juego.setPieza(4, 4, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setPieza(0, 0, new Pieza(TipoPieza.REY, ColorPieza.NEGRO));
        juego.setPieza(2, 2, new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO));
        juego.setPieza(6, 6, new Pieza(TipoPieza.DAMA, ColorPieza.NEGRO));
        juego.setPieza(3, 3, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO));

        int puntaje = juego.evaluar(ColorPieza.BLANCO);
        assertTrue(puntaje > 0);
    }

    @Test
    public void laIAEligeJugadaQueComaDama() {
        Ajedrez juego = new Ajedrez();
        for (int f = 0; f < Ajedrez.TAMANIO; f++) {
            for (int c = 0; c < Ajedrez.TAMANIO; c++) {
                juego.setPieza(f, c, null);
            }
        }
        juego.setPieza(7, 0, new Pieza(TipoPieza.REY, ColorPieza.BLANCO));
        juego.setPieza(6, 7, new Pieza(TipoPieza.REY, ColorPieza.NEGRO));
        juego.setPieza(4, 4, new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO));
        juego.setPieza(4, 6, new Pieza(TipoPieza.DAMA, ColorPieza.NEGRO));
        juego.setTurnoActual(ColorPieza.BLANCO);

        IAAjedrez ia = new IAAjedrez(2);
        int[] jugada = ia.mejorJugada(juego);

        boolean comeDama = jugada[2] == 4 && jugada[3] == 6;
        assertTrue(comeDama, "La IA debería capturar la dama");
    }

    @Test
    public void obtenerTodasLasJugadasIniciales() {
        Ajedrez juego = new Ajedrez();
        List<int[]> jugadas = juego.obtenerTodasLasJugadas(ColorPieza.BLANCO);
        assertTrue(!jugadas.isEmpty());
    }
}

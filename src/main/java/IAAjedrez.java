import java.util.List;

public class IAAjedrez {

    private int profundidadMax;
    private java.util.Random aleatorio = new java.util.Random();

    public IAAjedrez(int profundidadMax) {
        this.profundidadMax = profundidadMax;
    }

    public int[] mejorJugada(Ajedrez juego) {
        List<int[]> jugadas = juego.obtenerTodasLasJugadas(juego.getTurnoActual());
        if (jugadas.isEmpty()) return null;

        int mejorPuntaje = Integer.MIN_VALUE;
        List<int[]> mejoresJugadas = new java.util.ArrayList<>();
        ColorPieza colorIA = juego.getTurnoActual();

        for (int[] jugada : jugadas) {
            Ajedrez copia = copiarJuego(juego);
            copia.ejecutarMovimiento(jugada[0], jugada[1], jugada[2], jugada[3]);
            int puntaje = minimax(copia, profundidadMax - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, colorIA);
            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejoresJugadas.clear();
                mejoresJugadas.add(jugada);
            } else if (puntaje == mejorPuntaje) {
                mejoresJugadas.add(jugada);
            }
        }

        if (mejoresJugadas.isEmpty()) return null;
        return mejoresJugadas.get(aleatorio.nextInt(mejoresJugadas.size()));
    }

    private int minimax(Ajedrez juego, int profundidad, int alfa, int beta, boolean esMaximizando, ColorPieza colorIA) {
        if (juego.isJuegoTerminado() || profundidad == 0) {
            return juego.evaluar(colorIA);
        }

        List<int[]> jugadas = juego.obtenerTodasLasJugadas(juego.getTurnoActual());
        if (jugadas.isEmpty()) return juego.evaluar(colorIA);

        if (esMaximizando) {
            int mejorPuntaje = Integer.MIN_VALUE;
            for (int[] jugada : jugadas) {
                Ajedrez copia = copiarJuego(juego);
                copia.ejecutarMovimiento(jugada[0], jugada[1], jugada[2], jugada[3]);
                int puntaje = minimax(copia, profundidad - 1, alfa, beta, false, colorIA);
                mejorPuntaje = Math.max(mejorPuntaje, puntaje);
                alfa = Math.max(alfa, puntaje);
                if (beta <= alfa) break;
            }
            return mejorPuntaje;
        } else {
            int mejorPuntaje = Integer.MAX_VALUE;
            for (int[] jugada : jugadas) {
                Ajedrez copia = copiarJuego(juego);
                copia.ejecutarMovimiento(jugada[0], jugada[1], jugada[2], jugada[3]);
                int puntaje = minimax(copia, profundidad - 1, alfa, beta, true, colorIA);
                mejorPuntaje = Math.min(mejorPuntaje, puntaje);
                beta = Math.min(beta, puntaje);
                if (beta <= alfa) break;
            }
            return mejorPuntaje;
        }
    }

    private Ajedrez copiarJuego(Ajedrez original) {
        return new Ajedrez(original.getTablero(), original.getTurnoActual(), original.getCasillaEnPassant());
    }
}

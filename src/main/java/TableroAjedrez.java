import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class TableroAjedrez extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final Color COLOR_CLARO = new Color(238, 238, 210);
    private static final Color COLOR_OSCURO = new Color(118, 150, 86);
    private static final Color COLOR_SELECCION = new Color(130, 200, 130);
    private static final Color COLOR_MOVIMIENTO = new Color(170, 220, 170);
    private static final Color COLOR_JAQUE = new Color(255, 100, 100);

    private JButton[][] mBotones;
    private JPanel pTablero;
    private JPanel pSur;
    private JButton bNuevoJuego;
    private JLabel lMensaje;
    private JLabel lEstado;
    private JComboBox<String> cbDificultad;
    private Ajedrez juego;
    private IAAjedrez ia;
    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;
    private boolean jugadorEsBlanco = true;
    private int iPuntosJugador = 0;
    private int iPuntosComputadora = 0;

    public TableroAjedrez() {
        this.setTitle("Ajedrez - carobayo");

        pTablero = new JPanel();
        pTablero.setLayout(new GridLayout(Ajedrez.TAMANIO, Ajedrez.TAMANIO));

        pSur = new JPanel();
        pSur.setLayout(new BorderLayout());

        lMensaje = new JLabel("Tu turno (Blancas) - Selecciona una pieza");
        lMensaje.setHorizontalAlignment(JLabel.CENTER);
        lMensaje.setFont(new Font("Arial", Font.BOLD, 14));
        lMensaje.setForeground(Color.DARK_GRAY);

        lEstado = new JLabel("Jugador: 0 | Computadora: 0");
        lEstado.setHorizontalAlignment(JLabel.CENTER);
        lEstado.setFont(new Font("Arial", Font.PLAIN, 13));
        lEstado.setForeground(Color.GRAY);

        String[] dificultades = {"Fácil (2 niveles)", "Medio (3 niveles)", "Difícil (4 niveles)"};
        cbDificultad = new JComboBox<>(dificultades);
        cbDificultad.setSelectedIndex(1);
        cbDificultad.setFont(new Font("Arial", Font.PLAIN, 12));

        bNuevoJuego = new JButton("Nuevo juego");
        bNuevoJuego.setFont(new Font("Arial", Font.BOLD, 12));
        bNuevoJuego.addActionListener(this);

        pSur.add(lEstado, BorderLayout.NORTH);
        pSur.add(lMensaje, BorderLayout.CENTER);

        JPanel pSurInferior = new JPanel();
        pSurInferior.setLayout(new BorderLayout());
        pSurInferior.add(cbDificultad, BorderLayout.WEST);
        pSurInferior.add(bNuevoJuego, BorderLayout.EAST);
        pSur.add(pSurInferior, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        mBotones = new JButton[Ajedrez.TAMANIO][Ajedrez.TAMANIO];
        for (int fila = 0; fila < Ajedrez.TAMANIO; fila++) {
            for (int columna = 0; columna < Ajedrez.TAMANIO; columna++) {
                mBotones[fila][columna] = new JButton();
                mBotones[fila][columna].setFont(new Font("Serif", Font.BOLD, 48));
                mBotones[fila][columna].setPreferredSize(new Dimension(80, 80));
                mBotones[fila][columna].addActionListener(this);
                pTablero.add(mBotones[fila][columna]);
            }
        }

        this.add(pTablero, BorderLayout.CENTER);
        this.add(pSur, BorderLayout.SOUTH);

        this.setSize(700, 800);
        this.setMinimumSize(new Dimension(600, 700));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();
    }

    public void init() {
        juego = new Ajedrez();
        int profundidad = cbDificultad.getSelectedIndex() == 0 ? 2 : cbDificultad.getSelectedIndex() == 1 ? 3 : 4;
        ia = new IAAjedrez(profundidad);
        filaSeleccionada = -1;
        colSeleccionada = -1;
        lMensaje.setText("Tu turno (Blancas) - Selecciona una pieza");
        lMensaje.setForeground(Color.DARK_GRAY);
        actualizarTablero();
    }

    private void actualizarTablero() {
        Pieza[][] tablero = juego.getTablero();
        int filaRey = -1, colRey = -1;
        ColorPieza turno = juego.getTurnoActual();

        if (juego.estaEnJaque(turno)) {
            for (int f = 0; f < Ajedrez.TAMANIO; f++) {
                for (int c = 0; c < Ajedrez.TAMANIO; c++) {
                    Pieza p = tablero[f][c];
                    if (p != null && p.getTipo() == TipoPieza.REY && p.getColor() == turno) {
                        filaRey = f;
                        colRey = c;
                    }
                }
            }
        }

        for (int fila = 0; fila < Ajedrez.TAMANIO; fila++) {
            for (int columna = 0; columna < Ajedrez.TAMANIO; columna++) {
                boolean esOscura = (fila + columna) % 2 != 0;
                JButton b = mBotones[fila][columna];
                b.setBackground(esOscura ? COLOR_OSCURO : COLOR_CLARO);

                Pieza pieza = tablero[fila][columna];
                if (pieza != null) {
                    b.setText(pieza.getSymbolo());
                    b.setForeground(pieza.getColor() == ColorPieza.BLANCO ? Color.WHITE : Color.BLACK);
                } else {
                    b.setText("");
                }
            }
        }

        if (filaSeleccionada >= 0) {
            mBotones[filaSeleccionada][colSeleccionada].setBackground(COLOR_SELECCION);
            List<PosMatris> movimientos = juego.obtenerMovimientosLegales(filaSeleccionada, colSeleccionada);
            for (PosMatris m : movimientos) {
                mBotones[m.getiFila()][m.getiColumna()].setBackground(COLOR_MOVIMIENTO);
            }
        }

        if (filaRey >= 0) {
            mBotones[filaRey][colRey].setBackground(COLOR_JAQUE);
        }

        lEstado.setText("Jugador: " + iPuntosJugador + " | Computadora: " + iPuntosComputadora);
    }

    private void mostrarDialogoPromocion(int fila, int columna) {
        String[] opciones = {"Dama", "Torre", "Alfil", "Caballo"};
        TipoPieza[] tipos = {TipoPieza.DAMA, TipoPieza.TORRE, TipoPieza.ALFIL, TipoPieza.CABALLO};
        int eleccion = JOptionPane.showOptionDialog(this,
            "Elige la pieza de promoción",
            "Promoción de Peón",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null, opciones, opciones[0]);
        if (eleccion < 0) eleccion = 0;
        juego.ejecutarMovimiento(filaSeleccionada, colSeleccionada, fila, columna, tipos[eleccion]);
    }

    public static void main(String[] args) {
        TableroAjedrez t = new TableroAjedrez();
        t.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(bNuevoJuego)) {
            init();
            return;
        }

        if (juego.isJuegoTerminado()) return;
        if (juego.getTurnoActual() != ColorPieza.BLANCO) return;

        for (int fila = 0; fila < Ajedrez.TAMANIO; fila++) {
            for (int columna = 0; columna < Ajedrez.TAMANIO; columna++) {
                if (e.getSource().equals(mBotones[fila][columna])) {

                    if (filaSeleccionada == -1) {
                        Pieza pieza = juego.getTablero()[fila][columna];
                        if (pieza != null && pieza.getColor() == ColorPieza.BLANCO) {
                            List<PosMatris> movimientos = juego.obtenerMovimientosLegales(fila, columna);
                            if (!movimientos.isEmpty()) {
                                filaSeleccionada = fila;
                                colSeleccionada = columna;
                                lMensaje.setText("Pieza seleccionada - Elige destino");
                                actualizarTablero();
                            }
                        }
                    } else {
                        Pieza piezaMover = juego.getTablero()[filaSeleccionada][colSeleccionada];
                        boolean esPromocion = piezaMover != null && piezaMover.getTipo() == TipoPieza.PEON
                            && fila == 0;

                        if (esPromocion) {
                            mostrarDialogoPromocion(fila, columna);
                        } else if (juego.ejecutarMovimiento(filaSeleccionada, colSeleccionada, fila, columna)) {
                            lMensaje.setText("Turno de la computadora...");
                            actualizarTablero();
                            filaSeleccionada = -1;
                            colSeleccionada = -1;

                            if (juego.isJuegoTerminado()) {
                                manejarFinJuego();
                                return;
                            }

                            ejecutarTurnoComputadora();
                        }

                        dirigirSeleccionNueva(fila, columna);
                    }
                    return;
                }
            }
        }
    }

    private void dirigirSeleccionNueva(int fila, int columna) {
        Pieza pieza = juego.getTablero()[fila][columna];
        if (pieza != null && pieza.getColor() == ColorPieza.BLANCO) {
            List<PosMatris> movimientos = juego.obtenerMovimientosLegales(fila, columna);
            if (!movimientos.isEmpty()) {
                filaSeleccionada = fila;
                colSeleccionada = columna;
                lMensaje.setText("Pieza seleccionada - Elige destino");
                actualizarTablero();
            } else {
                filaSeleccionada = -1;
                colSeleccionada = -1;
                lMensaje.setText("Tu turno - Selecciona una pieza");
                actualizarTablero();
            }
        } else {
            filaSeleccionada = -1;
            colSeleccionada = -1;
            lMensaje.setText("Tu turno - Selecciona una pieza");
            actualizarTablero();
        }
    }

    private void ejecutarTurnoComputadora() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    int[] jugadaIA = ia.mejorJugada(juego);
                    if (jugadaIA != null) {
                        juego.ejecutarMovimiento(jugadaIA[0], jugadaIA[1], jugadaIA[2], jugadaIA[3]);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                actualizarTablero();
                if (juego.isJuegoTerminado()) {
                    manejarFinJuego();
                } else {
                    lMensaje.setText("Tu turno - Selecciona una pieza");
                    lMensaje.setForeground(Color.DARK_GRAY);
                }
            }
        };
        worker.execute();
    }

    private void manejarFinJuego() {
        ColorPieza ganador = juego.getGanador();
        String motivo = juego.getMotivoFin();
        if (ganador == null) {
            iPuntosJugador++;
            iPuntosComputadora++;
            lMensaje.setText("Tablas - " + motivo);
            lMensaje.setForeground(Color.DARK_GRAY);
        } else if (ganador == ColorPieza.BLANCO) {
            iPuntosJugador++;
            lMensaje.setText("Ganaste! (" + motivo + ") - Click 'Nuevo juego'");
            lMensaje.setForeground(new Color(0, 100, 0));
        } else {
            iPuntosComputadora++;
            lMensaje.setText("Perdiste! (" + motivo + ") - Click 'Nuevo juego'");
            lMensaje.setForeground(Color.RED);
        }
        actualizarTablero();
        JOptionPane.showMessageDialog(this, lMensaje.getText(), "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
    }
}

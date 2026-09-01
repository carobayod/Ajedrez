import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestPieza {

    @Test
    public void crearPiezaPorDefecto() {
        Pieza p = new Pieza(TipoPieza.PEON, ColorPieza.BLANCO);
        assertEquals(TipoPieza.PEON, p.getTipo());
        assertEquals(ColorPieza.BLANCO, p.getColor());
        assertFalse(p.isSeHaMovido());
    }

    @Test
    public void crearPiezaConEstadoMovida() {
        Pieza p = new Pieza(TipoPieza.TORRE, ColorPieza.NEGRO, true);
        assertTrue(p.isSeHaMovido());
        assertEquals(TipoPieza.TORRE, p.getTipo());
        assertEquals(ColorPieza.NEGRO, p.getColor());
    }

    @Test
    public void colorOpuesto() {
        assertEquals(ColorPieza.NEGRO, ColorPieza.BLANCO.opuesto());
        assertEquals(ColorPieza.BLANCO, ColorPieza.NEGRO.opuesto());
    }

    @Test
    public void igualdadPiezas() {
        Pieza p1 = new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO);
        Pieza p2 = new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO);
        Pieza p3 = new Pieza(TipoPieza.DAMA, ColorPieza.NEGRO);
        Pieza p4 = new Pieza(TipoPieza.REY, ColorPieza.BLANCO);

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, p4);
    }

    @Test
    public void valoresPiezas() {
        assertEquals(100, new Pieza(TipoPieza.PEON, ColorPieza.BLANCO).getValor());
        assertEquals(320, new Pieza(TipoPieza.CABALLO, ColorPieza.BLANCO).getValor());
        assertEquals(330, new Pieza(TipoPieza.ALFIL, ColorPieza.BLANCO).getValor());
        assertEquals(500, new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO).getValor());
        assertEquals(900, new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO).getValor());
        assertEquals(20000, new Pieza(TipoPieza.REY, ColorPieza.BLANCO).getValor());
    }

    @Test
    public void simbolosUnicode() {
        assertEquals("\u2654", new Pieza(TipoPieza.REY, ColorPieza.BLANCO).getSymbolo());
        assertEquals("\u265A", new Pieza(TipoPieza.REY, ColorPieza.NEGRO).getSymbolo());
        assertEquals("\u2655", new Pieza(TipoPieza.DAMA, ColorPieza.BLANCO).getSymbolo());
        assertEquals("\u265F", new Pieza(TipoPieza.PEON, ColorPieza.NEGRO).getSymbolo());
    }

    @Test
    public void clonarPieza() {
        Pieza original = new Pieza(TipoPieza.CABALLO, ColorPieza.BLANCO, false);
        Pieza clon = original.clonar();
        assertEquals(original, clon);
        assertNotEquals(original, null);
        clon.setSeHaMovido(true);
        assertFalse(original.isSeHaMovido());
        assertTrue(clon.isSeHaMovido());
    }
}

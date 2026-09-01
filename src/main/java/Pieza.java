public class Pieza {

    private TipoPieza tipo;
    private ColorPieza color;
    private boolean seHaMovido;

    public Pieza(TipoPieza tipo, ColorPieza color) {
        this.tipo = tipo;
        this.color = color;
        this.seHaMovido = false;
    }

    public Pieza(TipoPieza tipo, ColorPieza color, boolean seHaMovido) {
        this.tipo = tipo;
        this.color = color;
        this.seHaMovido = seHaMovido;
    }

    public TipoPieza getTipo() {
        return tipo;
    }

    public ColorPieza getColor() {
        return color;
    }

    public boolean isSeHaMovido() {
        return seHaMovido;
    }

    public void setSeHaMovido(boolean seHaMovido) {
        this.seHaMovido = seHaMovido;
    }

    public Pieza clonar() {
        return new Pieza(tipo, color, seHaMovido);
    }

    public String getSymbolo() {
        if (color == ColorPieza.BLANCO) {
            switch (tipo) {
                case REY:    return "\u2654";
                case DAMA:   return "\u2655";
                case TORRE:  return "\u2656";
                case ALFIL:  return "\u2657";
                case CABALLO:return "\u2658";
                case PEON:   return "\u2659";
            }
        } else {
            switch (tipo) {
                case REY:    return "\u265A";
                case DAMA:   return "\u265B";
                case TORRE:  return "\u265C";
                case ALFIL:  return "\u265D";
                case CABALLO:return "\u265E";
                case PEON:   return "\u265F";
            }
        }
        return "";
    }

    public int getValor() {
        switch (tipo) {
            case PEON:    return 100;
            case CABALLO: return 320;
            case ALFIL:   return 330;
            case TORRE:   return 500;
            case DAMA:    return 900;
            case REY:     return 20000;
            default:      return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pieza pieza = (Pieza) o;
        return tipo == pieza.tipo && color == pieza.color;
    }

    @Override
    public int hashCode() {
        return 31 * tipo.hashCode() + color.hashCode();
    }

    @Override
    public String toString() {
        return getSymbolo();
    }
}

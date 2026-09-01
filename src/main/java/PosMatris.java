public class PosMatris {

    private int iFila;
    private int iColumna;

    public PosMatris(int iFila, int iColumna) {
        this.iFila = iFila;
        this.iColumna = iColumna;
    }

    public int getiFila() {
        return iFila;
    }

    public void setiFila(int iFila) {
        this.iFila = iFila;
    }

    public int getiColumna() {
        return iColumna;
    }

    public void setiColumna(int iColumna) {
        this.iColumna = iColumna;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosMatris posMatris = (PosMatris) o;
        return iFila == posMatris.iFila && iColumna == posMatris.iColumna;
    }

    @Override
    public int hashCode() {
        return 31 * iFila + iColumna;
    }

    @Override
    public String toString() {
        return "(" + iFila + ", " + iColumna + ")";
    }
}

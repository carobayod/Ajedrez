public enum ColorPieza {
    BLANCO,
    NEGRO;

    public ColorPieza opuesto() {
        return this == BLANCO ? NEGRO : BLANCO;
    }
}

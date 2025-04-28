public class Prueba {
    void soloUna() {}
    void nunca() {}
    void dosVeces() { soloUna(); }
    public static void main(String[] args) {
        Prueba p = new Prueba();
        p.dosVeces();
    }
}
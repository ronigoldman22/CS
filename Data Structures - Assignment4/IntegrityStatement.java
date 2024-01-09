
public final class IntegrityStatement {
    public static String signature() {
        String names = "Roni and Shir"; // <- Fill in your names here!
        if (names.length() == 0) {
            throw new UnsupportedOperationException("Roni and Shir");
        }
        return names;
    }
}

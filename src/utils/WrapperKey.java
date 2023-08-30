package utils;

public record WrapperKey(String key1, String key2) {

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WrapperKey key)) return false;
        if (this == object) return true;
        return key1.equals(key.key1()) && key2.equals(key.key2());
    }

    @Override
    public int hashCode() {
        return (key1 + key2).hashCode();
    }

    public String toString() {
        StringBuilder output = new StringBuilder(new String(new char[200]).replace("\0", "-")).append("\n\n");
        output.append("Wrapper Key: ").append("\n\n");

        output.append(key1).append("\n");
        output.append(key2).append("\n\n");
        output.append(new String(new char[150]).replace("\0", "-"));

        return output.toString();
    }
}

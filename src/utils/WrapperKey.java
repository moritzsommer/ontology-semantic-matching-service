package utils;

import java.util.HashSet;

public record WrapperKey(String key1, String key2) {

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WrapperKey key)) return false;
        if (this == object) return true;
        HashSet<String> keySet1 = new HashSet<>();
        HashSet<String> keySet2 = new HashSet<>();
        keySet1.add(key1);
        keySet1.add(key2);
        keySet2.add(key.key1);
        keySet2.add(key.key2);
        return (keySet1.equals(keySet2));
    }

    @Override
    public int hashCode() {
        HashSet<String> keySet1 = new HashSet<>();
        keySet1.add(key1);
        keySet1.add(key2);
        return keySet1.hashCode();
    }

    public String toString() {
        return new String(new char[200]).replace("\0", "-") + "\n\n" +
                "Wrapper Key: " + "\n\n" +
                key1 + "\n" +
                key2 + "\n\n" +
                new String(new char[150]).replace("\0", "-");
    }
}
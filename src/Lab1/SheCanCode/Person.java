package Lab1.SheCanCode;

import java.util.Objects;

/** Base class: shared identity fields live here (inheritance + encapsulation). */
public abstract class Person {
    private final int id;
    private String name;
    private String email;

    protected Person(int id, String name, String email) {
        this.id = id;
        setName(name);
        setEmail(email);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        this.name = name.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email: " + email);
        this.email = email.trim();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person p = (Person) o;
        return id == p.id;
    }

    @Override public int hashCode() { return Objects.hash(getClass(), id); }

    @Override public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
package Lab3.service;

/** A rule was broken by the user's input (not a database failure). */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}

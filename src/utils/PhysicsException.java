package utils;

public class PhysicsException extends Exception {

    private final String message;

	@SuppressWarnings("unused")
    public PhysicsException() {
        this.message = null;
    }

    public PhysicsException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    private static final long serialVersionUID = 1L;

}

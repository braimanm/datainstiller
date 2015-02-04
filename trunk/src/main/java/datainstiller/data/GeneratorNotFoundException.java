package datainstiller.data;

public class GeneratorNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public GeneratorNotFoundException() {
		super();
	}

	public GeneratorNotFoundException(String message) {
		super(message);
	}

	public GeneratorNotFoundException(Throwable cause) {
		super(cause);
	}

	public GeneratorNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}


}

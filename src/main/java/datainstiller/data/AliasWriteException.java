package datainstiller.data;

public class AliasWriteException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AliasWriteException() {
		super();
	}

	public AliasWriteException(String message) {
		super(message);
	}

	public AliasWriteException(Throwable cause) {
		super(cause);
	}

	public AliasWriteException(String message, Throwable cause) {
		super(message, cause);
	}


}

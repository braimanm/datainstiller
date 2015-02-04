package datainstiller.data;

public class AnnotationProcessingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AnnotationProcessingException() {
		super();
	}

	public AnnotationProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationProcessingException(String message) {
		super(message);
	}

	public AnnotationProcessingException(Throwable cause) {
		super(cause);
	}
}

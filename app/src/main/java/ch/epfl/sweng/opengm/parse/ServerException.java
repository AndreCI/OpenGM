package ch.epfl.sweng.opengm.parse;

public class ServerException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Throwable throwable) {
        super(throwable);
    }

}

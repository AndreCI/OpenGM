package ch.epfl.sweng.opengm.parse;

public class PFEvent extends PFEntity {

    public PFEvent() {
        super(null, null);
    }

    @Override
    protected void updateToServer(int idx) throws PFException {

    }

    public static class Builder extends PFEntity.Builder {
        @Override
        protected void retrieveFromServer() throws PFException {

        }
    }
}

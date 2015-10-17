package ch.epfl.sweng.opengm.parse;

public class PFEvent extends PFEntity {

    public PFEvent() {
        super(null, null);
    }

    @Override
    protected void updateToServer(String entry) throws PFException {

    }

    public static class Builder extends PFEntity.Builder {

        public Builder(String id) {
            super(id);
        }

        @Override
        protected void retrieveFromServer() throws PFException {

        }

        public PFEvent build() {
            return new PFEvent();
        }
    }
}

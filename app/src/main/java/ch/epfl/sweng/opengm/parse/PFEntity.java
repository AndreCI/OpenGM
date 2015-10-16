package ch.epfl.sweng.opengm.parse;

abstract class PFEntity {

    private final String mId;
    private final String mParseTable;

    PFEntity(String id, String tableName) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Id is null or empty");
        }
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Table name is null or empty");
        }
        mId = id;
        mParseTable = tableName;
    }

    String getId() {
        return this.mId;
    }

    protected abstract void updateToServer() throws PFException;

    @Override
    public String toString() {
        return "[parseTable = " + mParseTable + " | id = " + mId + " ]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PFEntity that = (PFEntity) o;
        return mId.equals(that.mId) && mParseTable.equals(that.mParseTable);

    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + (mParseTable != null ? mParseTable.hashCode() : 0);
        return result;
    }

    public static abstract class Builder {

        protected String mId;

        public Builder() {
            mId = null;
        }

        public Builder(String id) {
            mId = id;
        }

        public void setId(String id) {
            mId = id;
        }

        protected abstract void retrieveFromServer() throws PFException;

    }

}

package ch.epfl.sweng.opengm.parse;

abstract class ParseEntity {

    private final String mId;
    private final String mParseTable;

    ParseEntity(String id, String tableName) {
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

        ParseEntity that = (ParseEntity) o;

        return mId.equals(that.mId) && mParseTable.equals(that.mParseTable);

    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + (mParseTable != null ? mParseTable.hashCode() : 0);
        return result;
    }

    public static abstract class Builder {

        private final String mParseTable;
        String mId;

        public Builder(String parseTable) {
            this(null, parseTable);
        }

        public Builder(String id, String parseTable) {
            mId = id;
            mParseTable = parseTable;
        }

        public boolean setId(String id) {
            if (mId.equals(id)) {
                return true;
            }
            mId = id;
            return false;
        }

        public abstract void retrieveFromParse() throws ServerException;

    }

}

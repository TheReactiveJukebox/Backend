package de.reactivejukebox.core;

import de.reactivejukebox.database.PreparedStatementBuilder;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import static org.testng.Assert.assertEquals;

public class PreparedStatementBuilderTest {

    // TODO Use mocking framework to avoid hundreds of lines of empty interfaces

    public class MockConnection implements Connection {

        @Override
        public PreparedStatement prepareStatement(String s) throws SQLException {
            return new MockPreparedStatement(s);
        }

        @Override
        public Statement createStatement() throws SQLException {
            return null;
        }

        @Override
        public CallableStatement prepareCall(String s) throws SQLException {
            return null;
        }

        @Override
        public String nativeSQL(String s) throws SQLException {
            return null;
        }

        @Override
        public void setAutoCommit(boolean b) throws SQLException {

        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return false;
        }

        @Override
        public void commit() throws SQLException {

        }

        @Override
        public void rollback() throws SQLException {

        }

        @Override
        public void close() throws SQLException {

        }

        @Override
        public boolean isClosed() throws SQLException {
            return false;
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return null;
        }

        @Override
        public void setReadOnly(boolean b) throws SQLException {

        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return false;
        }

        @Override
        public void setCatalog(String s) throws SQLException {

        }

        @Override
        public String getCatalog() throws SQLException {
            return null;
        }

        @Override
        public void setTransactionIsolation(int i) throws SQLException {

        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return 0;
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return null;
        }

        @Override
        public void clearWarnings() throws SQLException {

        }

        @Override
        public Statement createStatement(int i, int i1) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(String s, int i, int i1) throws SQLException {
            return null;
        }

        @Override
        public CallableStatement prepareCall(String s, int i, int i1) throws SQLException {
            return null;
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return null;
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

        }

        @Override
        public void setHoldability(int i) throws SQLException {

        }

        @Override
        public int getHoldability() throws SQLException {
            return 0;
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return null;
        }

        @Override
        public Savepoint setSavepoint(String s) throws SQLException {
            return null;
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {

        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {

        }

        @Override
        public Statement createStatement(int i, int i1, int i2) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException {
            return null;
        }

        @Override
        public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(String s, int i) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(String s, int[] ints) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException {
            return null;
        }

        @Override
        public Clob createClob() throws SQLException {
            return null;
        }

        @Override
        public Blob createBlob() throws SQLException {
            return null;
        }

        @Override
        public NClob createNClob() throws SQLException {
            return null;
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return null;
        }

        @Override
        public boolean isValid(int i) throws SQLException {
            return false;
        }

        @Override
        public void setClientInfo(String s, String s1) throws SQLClientInfoException {

        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {

        }

        @Override
        public String getClientInfo(String s) throws SQLException {
            return null;
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return null;
        }

        @Override
        public Array createArrayOf(String s, Object[] objects) throws SQLException {
            return null;
        }

        @Override
        public Struct createStruct(String s, Object[] objects) throws SQLException {
            return null;
        }

        @Override
        public void setSchema(String s) throws SQLException {

        }

        @Override
        public String getSchema() throws SQLException {
            return null;
        }

        @Override
        public void abort(Executor executor) throws SQLException {

        }

        @Override
        public void setNetworkTimeout(Executor executor, int i) throws SQLException {

        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return 0;
        }

        @Override
        public <T> T unwrap(Class<T> aClass) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> aClass) throws SQLException {
            return false;
        }
    }

    public class MockPreparedStatement implements PreparedStatement {

        public String query;

        public MockPreparedStatement(String query) {
            this.query = query;
        }

        @Override
        public void setString(int i, String s) throws SQLException {
            query = query.replaceFirst("\\?", "'" + s + "'");
        }

        @Override
        public ResultSet executeQuery() throws SQLException {
            return null;
        }

        @Override
        public int executeUpdate() throws SQLException {
            return 0;
        }

        @Override
        public void setNull(int i, int i1) throws SQLException {

        }

        @Override
        public void setBoolean(int i, boolean b) throws SQLException {

        }

        @Override
        public void setByte(int i, byte b) throws SQLException {

        }

        @Override
        public void setShort(int i, short i1) throws SQLException {

        }

        @Override
        public void setInt(int i, int i1) throws SQLException {

        }

        @Override
        public void setLong(int i, long l) throws SQLException {

        }

        @Override
        public void setFloat(int i, float v) throws SQLException {

        }

        @Override
        public void setDouble(int i, double v) throws SQLException {

        }

        @Override
        public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {

        }

        @Override
        public void setBytes(int i, byte[] bytes) throws SQLException {

        }

        @Override
        public void setDate(int i, Date date) throws SQLException {

        }

        @Override
        public void setTime(int i, Time time) throws SQLException {

        }

        @Override
        public void setTimestamp(int i, Timestamp timestamp) throws SQLException {

        }

        @Override
        public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {

        }

        @Override
        public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {

        }

        @Override
        public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {

        }

        @Override
        public void clearParameters() throws SQLException {

        }

        @Override
        public void setObject(int i, Object o, int i1) throws SQLException {

        }

        @Override
        public void setObject(int i, Object o) throws SQLException {

        }

        @Override
        public boolean execute() throws SQLException {
            return false;
        }

        @Override
        public void addBatch() throws SQLException {

        }

        @Override
        public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {

        }

        @Override
        public void setRef(int i, Ref ref) throws SQLException {

        }

        @Override
        public void setBlob(int i, Blob blob) throws SQLException {

        }

        @Override
        public void setClob(int i, Clob clob) throws SQLException {

        }

        @Override
        public void setArray(int i, Array array) throws SQLException {

        }

        @Override
        public ResultSetMetaData getMetaData() throws SQLException {
            return null;
        }

        @Override
        public void setDate(int i, Date date, Calendar calendar) throws SQLException {

        }

        @Override
        public void setTime(int i, Time time, Calendar calendar) throws SQLException {

        }

        @Override
        public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {

        }

        @Override
        public void setNull(int i, int i1, String s) throws SQLException {

        }

        @Override
        public void setURL(int i, URL url) throws SQLException {

        }

        @Override
        public ParameterMetaData getParameterMetaData() throws SQLException {
            return null;
        }

        @Override
        public void setRowId(int i, RowId rowId) throws SQLException {

        }

        @Override
        public void setNString(int i, String s) throws SQLException {

        }

        @Override
        public void setNCharacterStream(int i, Reader reader, long l) throws SQLException {

        }

        @Override
        public void setNClob(int i, NClob nClob) throws SQLException {

        }

        @Override
        public void setClob(int i, Reader reader, long l) throws SQLException {

        }

        @Override
        public void setBlob(int i, InputStream inputStream, long l) throws SQLException {

        }

        @Override
        public void setNClob(int i, Reader reader, long l) throws SQLException {

        }

        @Override
        public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {

        }

        @Override
        public void setObject(int i, Object o, int i1, int i2) throws SQLException {

        }

        @Override
        public void setAsciiStream(int i, InputStream inputStream, long l) throws SQLException {

        }

        @Override
        public void setBinaryStream(int i, InputStream inputStream, long l) throws SQLException {

        }

        @Override
        public void setCharacterStream(int i, Reader reader, long l) throws SQLException {

        }

        @Override
        public void setAsciiStream(int i, InputStream inputStream) throws SQLException {

        }

        @Override
        public void setBinaryStream(int i, InputStream inputStream) throws SQLException {

        }

        @Override
        public void setCharacterStream(int i, Reader reader) throws SQLException {

        }

        @Override
        public void setNCharacterStream(int i, Reader reader) throws SQLException {

        }

        @Override
        public void setClob(int i, Reader reader) throws SQLException {

        }

        @Override
        public void setBlob(int i, InputStream inputStream) throws SQLException {

        }

        @Override
        public void setNClob(int i, Reader reader) throws SQLException {

        }

        @Override
        public ResultSet executeQuery(String s) throws SQLException {
            return null;
        }

        @Override
        public int executeUpdate(String s) throws SQLException {
            return 0;
        }

        @Override
        public void close() throws SQLException {

        }

        @Override
        public int getMaxFieldSize() throws SQLException {
            return 0;
        }

        @Override
        public void setMaxFieldSize(int i) throws SQLException {

        }

        @Override
        public int getMaxRows() throws SQLException {
            return 0;
        }

        @Override
        public void setMaxRows(int i) throws SQLException {

        }

        @Override
        public void setEscapeProcessing(boolean b) throws SQLException {

        }

        @Override
        public int getQueryTimeout() throws SQLException {
            return 0;
        }

        @Override
        public void setQueryTimeout(int i) throws SQLException {

        }

        @Override
        public void cancel() throws SQLException {

        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return null;
        }

        @Override
        public void clearWarnings() throws SQLException {

        }

        @Override
        public void setCursorName(String s) throws SQLException {

        }

        @Override
        public boolean execute(String s) throws SQLException {
            return false;
        }

        @Override
        public ResultSet getResultSet() throws SQLException {
            return null;
        }

        @Override
        public int getUpdateCount() throws SQLException {
            return 0;
        }

        @Override
        public boolean getMoreResults() throws SQLException {
            return false;
        }

        @Override
        public void setFetchDirection(int i) throws SQLException {

        }

        @Override
        public int getFetchDirection() throws SQLException {
            return 0;
        }

        @Override
        public void setFetchSize(int i) throws SQLException {

        }

        @Override
        public int getFetchSize() throws SQLException {
            return 0;
        }

        @Override
        public int getResultSetConcurrency() throws SQLException {
            return 0;
        }

        @Override
        public int getResultSetType() throws SQLException {
            return 0;
        }

        @Override
        public void addBatch(String s) throws SQLException {

        }

        @Override
        public void clearBatch() throws SQLException {

        }

        @Override
        public int[] executeBatch() throws SQLException {
            return new int[0];
        }

        @Override
        public Connection getConnection() throws SQLException {
            return null;
        }

        @Override
        public boolean getMoreResults(int i) throws SQLException {
            return false;
        }

        @Override
        public ResultSet getGeneratedKeys() throws SQLException {
            return null;
        }

        @Override
        public int executeUpdate(String s, int i) throws SQLException {
            return 0;
        }

        @Override
        public int executeUpdate(String s, int[] ints) throws SQLException {
            return 0;
        }

        @Override
        public int executeUpdate(String s, String[] strings) throws SQLException {
            return 0;
        }

        @Override
        public boolean execute(String s, int i) throws SQLException {
            return false;
        }

        @Override
        public boolean execute(String s, int[] ints) throws SQLException {
            return false;
        }

        @Override
        public boolean execute(String s, String[] strings) throws SQLException {
            return false;
        }

        @Override
        public int getResultSetHoldability() throws SQLException {
            return 0;
        }

        @Override
        public boolean isClosed() throws SQLException {
            return false;
        }

        @Override
        public void setPoolable(boolean b) throws SQLException {

        }

        @Override
        public boolean isPoolable() throws SQLException {
            return false;
        }

        @Override
        public void closeOnCompletion() throws SQLException {

        }

        @Override
        public boolean isCloseOnCompletion() throws SQLException {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> aClass) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> aClass) throws SQLException {
            return false;
        }
    }

    @Test
    public void testPrepare() throws Exception {
        MockPreparedStatement mps = (MockPreparedStatement) new PreparedStatementBuilder()
                .select("col1, col2")
                .from("tbl1, tbl2")
                .addFilter("tbl1.col1=tbl2.col1")
                .addFilter("col2=?", (query, i) -> query.setString(i, "Test"))
                .prepare(new MockConnection());
        assertEquals(mps.query, "SELECT col1, col2 FROM tbl1, tbl2 WHERE tbl1.col1=tbl2.col1 AND col2='Test' ");
    }

}

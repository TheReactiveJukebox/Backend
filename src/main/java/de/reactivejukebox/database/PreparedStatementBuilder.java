package de.reactivejukebox.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class PreparedStatementBuilder {

    /**
     * With prepared statements, the statement skeleton needs to be sent to the server.
     * After that, the values are set individually. A PreparedStatementSetter is an
     * a callback that allows the values to be set before prepare() is called.
     *
     * Can be written in lambda notation like so:
     *
     *   (dbQuery, i) -> dbQuery.setString(i, "myvalue")
     *
     * To be used as a parameter for PreparedStatementBuilder.addFilter().
     */
    public interface PreparedStatementSetter {

        /**
         * Callback where the parameter of the PreparedStatement is set.
         * Possible implementation: (dbQuery, i) -> dbQuery.setString(i, "myvalue")
         * @param query PreparedStatement reference
         * @param i parameter index
         * @throws SQLException when the dbQuery.set*() call throws an SQL Exception
         */
        void execute(PreparedStatement query, int i) throws SQLException;
    }

    private Queue<PreparedStatementSetter> valueSetters;
    private StringBuilder selectString;
    private StringBuilder query;
    private boolean firstExpressionSpecified = false;

    public PreparedStatementBuilder() {
        valueSetters = new LinkedList<>();
        query = new StringBuilder();
    }

    public PreparedStatementBuilder selectFrom(String projection, String databaseObjects) {
        selectString = new StringBuilder()
                .append("SELECT ")
                .append(projection)
                .append(" FROM ")
                .append(databaseObjects)
                .append(" WHERE ");
        return this;
    }

    public PreparedStatementBuilder addFilter(String expressionString, PreparedStatementSetter... setter) {
        if (firstExpressionSpecified) {
            query.append("AND ");
        } else {
            firstExpressionSpecified = true;
        }
        query.append(expressionString).append(" ");
        valueSetters.addAll(Arrays.asList(setter));
        return this;
    }

    public PreparedStatement prepare(Connection con) throws SQLException {
        PreparedStatement stmnt = con.prepareStatement(selectString.toString() + query.toString());
        PreparedStatementSetter cmd;
        for (int i = 1; (cmd = valueSetters.poll()) != null; ++i) {
            cmd.execute(stmnt, i);
        }
        return stmnt;
    }
}

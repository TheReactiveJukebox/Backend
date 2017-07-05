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
     * <p>
     * Can be written in lambda notation like so:
     * <p>
     * (dbQuery, i) -> dbQuery.setString(i, "myvalue")
     * <p>
     * To be used as a parameter for PreparedStatementBuilder.addFilter().
     */
    public interface PreparedStatementSetter {

        /**
         * Callback where the parameter of the PreparedStatement is set.
         * Possible implementation: (dbQuery, i) -> dbQuery.setString(i, "myvalue")
         *
         * @param query PreparedStatement reference
         * @param i     parameter index
         * @throws SQLException when the dbQuery.set*() call throws an SQL Exception
         */
        void execute(PreparedStatement query, int i) throws SQLException;
    }

    private Queue<PreparedStatementSetter> valueSetters;
    private String projection;
    private String databaseObjects;
    private StringBuilder query;
    private boolean firstExpressionSpecified = false;


    public PreparedStatementBuilder() {
        valueSetters = new LinkedList<>();
        query = new StringBuilder();
    }

    /**
     * Sets the projection part of the SQL Query. Subsequent calls will replace what was set before.
     *
     * @param projection the columns present in the result
     * @return this PreparedStatementBuilder to enable fluent calls
     */
    public PreparedStatementBuilder select(String projection) {
        this.projection = projection;
        return this;
    }

    /**
     * Sets the "FROM" part of the SQL Query. Subsequent calls will replace what was set before.
     *
     * @param databaseObjects a list of database objects that should be queried
     * @return this PreparedStatementBuilder to enable fluent calls
     */
    public PreparedStatementBuilder from(String databaseObjects) {
        this.databaseObjects = databaseObjects;
        return this;
    }

    /**
     * Adds a filter to the selection part of the SQL Query. Subsequent calls will add more filters.
     * The filters are always chained using a boolean AND.
     *
     * @param expressionString an SQL boolean expression used in the "WHERE" part of the query
     * @param setter           a PreparedStatementSetter for each ? in the expression string
     * @return this PreparedStatementBuilder to enable fluent calls
     */
    public PreparedStatementBuilder addFilter(String expressionString, PreparedStatementSetter... setter) {
        if (firstExpressionSpecified) {
            query.append("AND ");
        } else {
            query.append("WHERE ");
            firstExpressionSpecified = true;
        }
        query.append(expressionString).append(" ");
        valueSetters.addAll(Arrays.asList(setter));
        return this;
    }

    /**
     * Creates the PreparedStatement using the given Connection object and sets its parameters
     * @param con Connection to the database
     * @return a PreparedStatement object with all parameters set
     * @throws SQLException when con.prepareStatement or any of the PreparedStatementSetter.execute()
     * calls throw an SQLException
     */
    public PreparedStatement prepare(Connection con) throws SQLException {
        String queryString = "SELECT " + projection + " FROM " + databaseObjects + " " + query;
        PreparedStatement stmnt = con.prepareStatement(queryString);
        PreparedStatementSetter cmd;
        for (int i = 1; (cmd = valueSetters.poll()) != null; ++i) {
            cmd.execute(stmnt, i);
        }
        return stmnt;
    }
}

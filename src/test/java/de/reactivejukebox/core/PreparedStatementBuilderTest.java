package de.reactivejukebox.core;

import de.reactivejukebox.database.PreparedStatementBuilder;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class PreparedStatementBuilderTest {

    @Test
    public void testPrepare() throws Exception {
        // Arrange
        String expectedQuery = "SELECT col1, col2 FROM tbl1, tbl2 WHERE tbl1.col1=tbl2.col1 AND col2='Test' ";

        PreparedStatement expectedStatement = mock(PreparedStatement.class);
        when(expectedStatement.toString()).thenReturn(expectedQuery);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(any(String.class)))
                .thenReturn(expectedStatement);
        // Act
        PreparedStatement mps = new PreparedStatementBuilder()
                .select("col1, col2")
                .from("tbl1, tbl2")
                .addFilter("tbl1.col1=tbl2.col1")
                .addFilter("col2=?", (query, i) -> query.setString(i, "Test"))
                .prepare(con);
        // Assert
        assertEquals(mps.toString(), expectedQuery);
    }

}

package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseImpl;
import de.reactivejukebox.database.DatabaseProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Playlists {

    public PlaylistPlain add(PlaylistPlain p) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement ps = con.prepareStatement(
                "insert into playlist(title, createdAt, editedAt, userid, tracks, isPublic) values(?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, p.getTitle());
        ps.setTimestamp(2, new Timestamp(p.getCreated().getTime()));
        ps.setTimestamp(3, new Timestamp(p.getEdited().getTime()));
        ps.setInt(4, p.getUserId());

        // We can not use createArrayOf from c3p0 in version 0.9.1.2. Workaround:
        final Array tracksArray;
        {
            final int[] tracks = p.getTracks();
            final Integer[] tracksInteger = Arrays.stream(tracks).boxed().toArray(Integer[]::new);
            Connection dummyCon = DriverManager.getConnection(
                    DatabaseImpl.DB_URL,
                    DatabaseImpl.DB_USER,
                    DatabaseImpl.DB_PASSWORD);
            tracksArray = dummyCon.createArrayOf("INTEGER", tracksInteger);
        }
        ps.setArray(5, tracksArray);
        ps.setBoolean(6, p.isPublic());

        int affected = ps.executeUpdate();
        if (affected == 0) {
            throw new SQLException("Playlist could not be created, no rows affected");
        }
        ResultSet rs = ps.getGeneratedKeys();
        if (!rs.next()) {
            throw new SQLException("Playlist was created, ID could not be fetched");
        }
        p.setId(rs.getInt("id"));
        con.close();
        return p;
    }

    public boolean remove(int playlistId) {
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from playlist where id=?");
            ps.setInt(1, playlistId);
            ps.execute();
            con.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error while removing playlist " + playlistId + ":");
            e.printStackTrace();
            return false;
        }

    }

    public boolean remove(PlaylistPlain p) {
        return remove(p.getId());
    }

    public PlaylistPlain getById(int playlistId) {
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from playlist where id=?");
            ps.setInt(1, playlistId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { // playlist with this id dose not exist
                return null;
            }
            final Integer[] tracksArray = (Integer[]) rs.getArray("tracks").getArray();
            // convert Integer Array in a int Array
            final int[] tracks = Arrays.stream(tracksArray).mapToInt(Integer::intValue).toArray();
            PlaylistPlain p = new PlaylistPlain(
                    playlistId,
                    tracks,
                    rs.getString("title"),
                    new java.util.Date(rs.getTimestamp("createdAt").getTime()),
                    new java.util.Date(rs.getTimestamp("editedAt").getTime()),
                    rs.getInt("userId"),
                    rs.getBoolean("isPublic")
            );
            con.close();
            return p;
        } catch (SQLException e) {
            System.err.println("Error while getting playlist " + playlistId + ":");
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<PlaylistPlain> getByUser(int userId) {
        ArrayList<PlaylistPlain> results = new ArrayList<>();
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from playlist where userid=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer[] tracksArray = (Integer[]) rs.getArray("tracks").getArray();
                // convert Integer Array in a int Array
                int[] tracks = Arrays.stream(tracksArray).mapToInt(Integer::intValue).toArray();
                PlaylistPlain p = new PlaylistPlain(
                        rs.getInt("id"),
                        tracks,
                        rs.getString("title"),
                        new java.util.Date(rs.getTimestamp("createdAt").getTime()),
                        new java.util.Date(rs.getTimestamp("editedAt").getTime()),
                        userId,
                        rs.getBoolean("isPublic")
                );
                results.add(p);
            }
            con.close();
        } catch (SQLException e) {
            // result will be empty
            System.err.println("Error while getting playlist for user " + userId + ":");
            e.printStackTrace();
        }
        return results;
    }

    public boolean update(PlaylistPlain p) {
        PlaylistPlain oldPlaylist = getById(p.getId());
        return update(p, !oldPlaylist.getTitle().equals(p.getTitle()));
    }

    public boolean update(PlaylistPlain p, boolean titelChanged) {
        boolean result = false;
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            String query;
            if (titelChanged) {
                query = "update playlist " +
                        "set title=?, tracks=?, userid=?, isPublic=?, createdAt=?, editedAt=? " +
                        "where id=?";
            } else {
                query = "update playlist " +
                        "set tracks=?, userid=?, isPublic=?, createdAt=?, editedAt=? " +
                        "where id=?";
            }
            PreparedStatement ps = con.prepareStatement(query);
            int i = 1;
            if (titelChanged) {
                ps.setString(i++, p.getTitle());
            }
            // We can not use createArrayOf from c3p0 in version 0.9.1.2. Workaround:
            final Array tracksArray;
            {
                final Integer[] tracksInteger = Arrays.stream(p.getTracks()).boxed().toArray(Integer[]::new);
                Connection dummyCon = DriverManager.getConnection(
                        DatabaseImpl.DB_URL,
                        DatabaseImpl.DB_USER,
                        DatabaseImpl.DB_PASSWORD);
                tracksArray = dummyCon.createArrayOf("INTEGER", tracksInteger);
                dummyCon.close();
            }
            ps.setArray(i++, tracksArray);
            ps.setInt(i++, p.getUserId());
            ps.setBoolean(i++, p.isPublic());
            ps.setTimestamp(i++, new Timestamp(p.getCreated().getTime()));
            ps.setTimestamp(i++, new Timestamp(p.getEdited().getTime()));
            ps.setInt(i, p.getId());

            final int affected = ps.executeUpdate();
            result = affected == 1;
            con.close();
        } catch (SQLException e) {
            System.err.println("Error while updating playlist "+p.getId()+":");
            e.printStackTrace();
        }
        return result;
    }
}

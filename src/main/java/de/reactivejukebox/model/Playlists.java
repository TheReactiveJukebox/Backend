package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Playlists {

    public PlaylistPlain add(PlaylistPlain p) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement ps = con.prepareStatement(
                "insert into playlist(title, createdAt, editedAt, userid, tracks, isPublic) values(?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, p.getTitle());
        ps.setTimestamp(2, new Timestamp(p.getCreated().getTime()));
        ps.setTimestamp(3, new Timestamp(p.getEdited().getTime()));
        ps.setInt(4, p.getUserId());

        int[] tracks = p.getTracks();
        Object[] javaSucks = new Object[tracks.length];
        for (int i = 0; i < tracks.length; ++i) {
            javaSucks[i] = tracks[i];
        }

        // TODO can we not use createArrayOf? Always fails on me for some reason.
        ps.setArray(5, con.createArrayOf("INTEGER", javaSucks));
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
        return p;
    }

    public boolean remove(int playlistId) {
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement ps = con.prepareStatement("delete from playlist where id=?");
            ps.setInt(1, playlistId);
            return ps.execute();
        } catch (SQLException e) {
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
            rs.next();
            PlaylistPlain p = new PlaylistPlain(
                    playlistId,
                    (int[]) rs.getArray("tracks").getArray(),
                    rs.getString("title"),
                    new java.util.Date(rs.getTimestamp("createdAt").getTime()),
                    new java.util.Date(rs.getTimestamp("editedAt").getTime()),
                    rs.getInt("userId"),
                    rs.getBoolean("isPublic")
            );
            return p;
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            // result will be empty
        }
        return results;
    }

    public boolean update(PlaylistPlain p) {
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement ps = con.prepareStatement("update playlist " +
                    "set title=?, tracks=?, userid=?, isPublic=?, createdAt=?, editedAt=? " +
                    "where id=?"
            );
            ps.setString(1, p.getTitle());
            ps.setObject(2, p.getTracks());
            ps.setInt(3, p.getUserId());
            ps.setBoolean(4, p.isPublic());
            ps.setTimestamp(5, new Timestamp(p.getCreated().getTime()));
            ps.setTimestamp(6, new Timestamp(p.getEdited().getTime()));
            ps.setInt(7, p.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }
}

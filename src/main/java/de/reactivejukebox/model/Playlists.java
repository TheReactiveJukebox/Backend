package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;

import java.sql.*;
import java.util.ArrayList;

public class Playlists {

    public boolean add(PlaylistPlain p) {
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "insert into playlist(title, createdAt, editedAt, userid, tracks) values(?, ?, ?, ?, ?)");
            ps.setString(1, p.getTitle());
            ps.setTimestamp(2, new Timestamp(p.getCreated().getTime()));
            ps.setTimestamp(3, new Timestamp(p.getEdited().getTime()));
            ps.setInt(4, p.getUserId());
            ps.setObject(5, p.getTracks());

            return ps.execute();
        } catch (SQLException e) {
            return false;
        }
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
                PlaylistPlain p = new PlaylistPlain(
                        rs.getInt("id"),
                        (int[]) rs.getArray("tracks").getArray(),
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

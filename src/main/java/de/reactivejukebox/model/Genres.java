package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Genres implements Iterable<String>{
    private ConcurrentHashMap<String,String> genreToMetagenre;
    private ConcurrentHashMap<String,ArrayList<String>> metagenreToGenre;
    private static String SQL_QUERY = "SELECT g.name as metagenre, genre.name FROM genre JOIN genre g ON genre.metagenreid = g.id";

    public Genres() throws SQLException {
        genreToMetagenre = new ConcurrentHashMap<>();
        metagenreToGenre = new ConcurrentHashMap<>();
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement stmnt = con.prepareStatement(SQL_QUERY);
        ResultSet rs = stmnt.executeQuery();
        String genre;
        String metagenre;
        while(rs.next()) {
            genre = rs.getString("name");
            metagenre  = rs.getString("metagenre");
            if(!genreToMetagenre.containsKey(genre)){
                genreToMetagenre.putIfAbsent(genre, metagenre);
            }
            if(!metagenreToGenre.containsKey(metagenre)){
                ArrayList<String> metalist = new ArrayList<>();
                metalist.add(metagenre);
                metagenreToGenre.putIfAbsent(metagenre, metalist);
            }
            metagenreToGenre.get(metagenre).add(genre);
        }
    }

    public String getMetaGenre(String genreName){
        return genreToMetagenre.get(genreName);
    }

    public ArrayList<String> getGenre(String metaGenreName){
        return metagenreToGenre.get(metaGenreName);
    }

    public List<String> metaList(){
        Enumeration<String> e = metagenreToGenre.keys();
        return Collections.list(e);
    }

    public List<String> genreList(){
        Enumeration<String> e = genreToMetagenre.keys();
        return Collections.list(e);
    }

    @Override
    public Iterator<String> iterator() {
        return genreToMetagenre.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super String> consumer) {
        genreToMetagenre.values().forEach(consumer);
    }

    @Override
    public Spliterator<String> spliterator() {
        return genreToMetagenre.values().spliterator();
    }

    public Stream<String> stream() {
        return StreamSupport.stream(spliterator(), false);
    }


}
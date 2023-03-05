package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile)
    {
        User u = new User(name, mobile);
        users.add(u);
        return u;
        //users.add(new User(name, mobile));
    }

    public Artist createArtist(String name)
    {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName)
    {
        Artist a = new Artist(artistName);
        if (! artists.contains(artistName)) artists.add(a);
        if (! albums.contains(title)) albums.add(new Album(title));

        artistAlbumMap.put(a, albums);
        return null;
    }

    public Song createSong(String title, String albumName, int length) throws Exception
    {
        Album a = new Album(albumName);
        if (! albums.contains(albumName)) throw new Exception("Album does not exist");

        Song s = new Song(title, length);
        songs.add(s);

        List<Song> songList = new ArrayList<>();
        if (albumSongMap.containsKey(a)) songList = albumSongMap.get(a);

        songList.add(s);
        albumSongMap.put(a, songList);

        return  s;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception
    {
        Playlist pl = new Playlist(title);
        if (!playlists.contains(title)) playlists.add(pl);

        List<Song> songList = new ArrayList<>();
        if (playlistSongMap.containsKey(pl)) songList = playlistSongMap.get(pl);
        songList.add(new Song(title, length));
        playlistSongMap.put(pl, songList);


    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {

    }

    public String mostPopularArtist() {
    }

    public String mostPopularSong() {
    }
}

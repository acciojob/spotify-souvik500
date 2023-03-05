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
        Artist artist = new Artist(artistName);
        if (!artists.contains(artist)) {
            artists.add(artist);
        }
        Album album = new Album(title);
        if (!albums.contains(album)) {
            albums.add(album);
        }
        List<Album> albumList = new ArrayList<>();
        if (artistAlbumMap.containsKey(artist)) {
            albumList = artistAlbumMap.get(artist);
        }
        albumList.add(album);
        artistAlbumMap.put(artist, albumList);
        return album;
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
        User user = findUser(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        if (!playlists.contains(playlist)) {
            playlists.add(playlist);
        }
        List<Song> songList = new ArrayList<>();
        for (Song song : songs) {
            if (song.getLength() == length) {
                songList.add(song);
            }
        }
        playlistSongMap.put(playlist, songList);
        creatorPlaylistMap.put(user, playlist);
        List<User> listenerList = new ArrayList<>();
        listenerList.add(user);
        playlistListenerMap.put(playlist, listenerList);
        List<Playlist> playlistList = new ArrayList<>();
        if (userPlaylistMap.containsKey(user)) {
            playlistList = userPlaylistMap.get(user);
        }
        playlistList.add(playlist);
        userPlaylistMap.put(user, playlistList);
        return playlist;
    }

    private User findUser(String mobile) {
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                return user;
            }
        }
        return null;
    }


    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception
    {
//        Playlist pl = new Playlist(title);
//        if (!playlists.contains(title)) playlists.add(pl);
//
//        List<Song> songList = new ArrayList<>();
//        if (playlistSongMap.containsKey(pl)) songList = playlistSongMap.get(pl);
//        songList.add(new Song(title, songList.size()));
//        playlistSongMap.put(pl, songList);
        User user = findUser(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }

        Playlist playlist = new Playlist(title);
        if (!playlists.contains(playlist)) {
            playlists.add(playlist);
        }

        List<Song> songList = new ArrayList<>();
        for (String songTitle : songTitles) {
            Song song = getSongByTitle(songTitle);
            if (song == null) {
                throw new Exception("Song does not exist: " + songTitle);
            }
            songList.add(song);
        }
        playlistSongMap.put(playlist, songList);
        creatorPlaylistMap.put(user, playlist);

        List<User> listenerList = new ArrayList<>();
        listenerList.add(user);
        playlistListenerMap.put(playlist, listenerList);

        List<Playlist> playlistList = new ArrayList<>();
        if (userPlaylistMap.containsKey(user)) {
            playlistList = userPlaylistMap.get(user);
        }
        playlistList.add(playlist);
        userPlaylistMap.put(user, playlistList);

        return playlist;
    }

    private Song getSongByTitle(String songTitle) {
        for (Song s : songs) {
            if (s.getTitle().equals(songTitle)) {
                return s;
            }
        }
        return null;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception
    {
        User user = getUserByMobile(mobile);
        if (user == null) throw new Exception("User does not exist");

        for (Playlist pl : playlists) {
            if (pl.getTitle().equals(playlistTitle) && pl.getCreator().equals(user)) {
                return pl;
            }
        }

        throw new Exception("Playlist not found");
    }

    private User getUserByMobile(String mobile) {
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                return user;
            }
        }

        return null;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception
    {
        User user = getUserByMobile(mobile);
        if (user == null) throw new Exception("User does not exist");

        Song song = getSongByTitle(songTitle);
        if (song == null) throw new Exception("Song not found");

        List<User> likedByUsers = songLikeMap.getOrDefault(song, new ArrayList<>());
        if (!likedByUsers.contains(user)) {
            likedByUsers.add(user);
            songLikeMap.put(song, likedByUsers);
        }

        return song;
    }

    public List<String> getArtists(Song song) {
        List<String> artists = new ArrayList<>();
        for (Map.Entry<String, List<Song>> entry : artistSongMap.entrySet()) {
            if (entry.getValue().contains(song)) {
                artists.add(entry.getKey());
            }
        }
        return artists;
    }

    public String mostPopularArtist() {
        Map<String, Integer> artistCountMap = new HashMap<>();
        int maxCount = 0;
        String mostPopularArtist = null;
        for (Song song : songs) {
            List<String> artists = getArtists(song);
            for (String artist : artists) {
                int count = artistCountMap.getOrDefault(artist, 0) + 1;
                artistCountMap.put(artist, count);
                if (count > maxCount) {
                    maxCount = count;
                    mostPopularArtist = artist;
                }
            }
        }
        return mostPopularArtist;
    }




    public String mostPopularSong()
    {
        Map<Song, Integer> songCountMap = new HashMap<>();

        for (Playlist playlist : playlists) {
            for (Song song : playlistSongMap.get(playlist)) {
                if (!songCountMap.containsKey(song)) {
                    songCountMap.put(song, 0);
                }

                int currentCount = songCountMap.get(song);
                songCountMap.put(song, currentCount + 1);
            }
        }

        int maxCount = 0;
        Song mostPopularSong = null;

        for (Map.Entry<Song, Integer> entry : songCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPopularSong = entry.getKey();
            }
        }

        return mostPopularSong.getTitle();
    }

}

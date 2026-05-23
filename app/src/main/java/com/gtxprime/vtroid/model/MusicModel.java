package com.gtxprime.vtroid.model;

import android.net.Uri;

public class MusicModel {

    String title;
    String artistName;
    Uri uri;
    Uri artworkUri;

    public MusicModel(String title, Uri uri, Uri artworkUri, String artistName) {
        this.title = title;
        this.uri = uri;
        this.artworkUri = artworkUri;
        this.artistName = artistName;
    }

    public MusicModel() {
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getArtworkUri() {
        return artworkUri;
    }

    public void setArtworkUri(Uri artworkUri) {
        this.artworkUri = artworkUri;
    }
}

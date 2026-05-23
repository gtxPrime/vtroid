package com.gtxprime.vtroid.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class WhatsappStatusModel implements Parcelable {
    private String name;
    private Uri uri;
    private String path;
    private String filename;
    public boolean selected = false;

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected2) {
        this.selected = selected2;
    }

    public WhatsappStatusModel(String name, Uri uri, String path, String filename) {
        this.name = name;
        this.uri = uri;
        this.path = path;
        this.filename = filename;
    }

    public static final Creator<WhatsappStatusModel> CREATOR = new Creator<WhatsappStatusModel>() {
        @Override
        public WhatsappStatusModel createFromParcel(Parcel in) {
            return new WhatsappStatusModel(in);
        }

        @Override
        public WhatsappStatusModel[] newArray(int size) {
            return new WhatsappStatusModel[size];
        }
    };

    public WhatsappStatusModel(Parcel in) {
        name = in.readString();
        path = in.readString();
        filename = in.readString();
        uri = Uri.parse(in.readString());
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(String.valueOf(uri));
        dest.writeString(filename);
    }
}

package com.nailiqi.travellingpaws.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Photo implements Parcelable {

    private String photo_id;
    private String user_id;
    private String caption;
    private String data_created;
    private String img_path;
    private double gps_longitude;
    private double gps_latitude;
    private List<Like> likes;


    public Photo() {

    }

    public Photo(String photo_id, String user_id, String caption, String data_created, String img_path, double gps_longitude, double gps_latitude, List<Like> likes) {
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.caption = caption;
        this.data_created = data_created;
        this.img_path = img_path;
        this.gps_longitude = gps_longitude;
        this.gps_latitude = gps_latitude;
        this.likes = likes;
    }

    protected Photo(Parcel in) {
        caption = in.readString();
        data_created = in.readString();
        img_path = in.readString();
        photo_id = in.readString();
        user_id = in.readString();
        gps_longitude = in.readDouble();
        gps_latitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(data_created);
        dest.writeString(img_path);
        dest.writeString(photo_id);
        dest.writeString(user_id);
        dest.writeDouble(gps_longitude);
        dest.writeDouble(gps_latitude);
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getData_created() {
        return data_created;
    }

    public void setData_created(String data_created) {
        this.data_created = data_created;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public double getGps_longitude() {
        return gps_longitude;
    }

    public void setGps_longitude(double gps_longitude) {
        this.gps_longitude = gps_longitude;
    }

    public double getGps_latitude() {
        return gps_latitude;
    }

    public void setGps_latitude(double gps_latitude) {
        this.gps_latitude = gps_latitude;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", caption='" + caption + '\'' +
                ", data_created='" + data_created + '\'' +
                ", img_path='" + img_path + '\'' +
                ", gps_longitude=" + gps_longitude +
                ", gps_latitude=" + gps_latitude +
                ", likes=" + likes +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }


}

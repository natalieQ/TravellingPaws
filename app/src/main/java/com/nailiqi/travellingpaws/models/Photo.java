package com.nailiqi.travellingpaws.models;

public class Photo {

    private String photo_id;
    private String user_id;
    private String caption;
    private String data_created;
    private String img_path;
    private double gps_longitude;
    private double gps_latitude;

    public Photo() {

    }

    public Photo(String photo_id, String user_id, String caption, String data_created, String img_path, double gps_longitude, double gps_latitude) {
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.caption = caption;
        this.data_created = data_created;
        this.img_path = img_path;
        this.gps_longitude = gps_longitude;
        this.gps_latitude = gps_latitude;
    }

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

    @Override
    public String toString() {
        return "Photo{" +
                "photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", caption='" + caption + '\'' +
                ", data_created='" + data_created + '\'' +
                ", img_path='" + img_path + '\'' +
                ", gps_longitude='" + gps_longitude + '\'' +
                ", gps_latitude='" + gps_latitude + '\'' +
                '}';
    }
}

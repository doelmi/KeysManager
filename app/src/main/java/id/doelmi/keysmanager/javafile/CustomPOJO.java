package id.doelmi.keysmanager.javafile;

import java.util.ArrayList;

/**
 * Created by abdul on 02/07/2017.
 */

public class CustomPOJO {
    private String name, time, content, gambar;
    private int id;
    private ArrayList<CustomPOJO> customPOJOs = new ArrayList<>();

    public CustomPOJO(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

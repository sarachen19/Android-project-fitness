package com.sarachen.androidappkeep.model;
import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Course {
    int id;
    String title, detail, image, type;
    List<Exercise> exercises;

    public Course() {
    }
    public Course(int id, String title, String detail, String image, String type, List<Exercise> exercises) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.image = image;
        this.type = type;
        this.exercises = exercises;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }
}

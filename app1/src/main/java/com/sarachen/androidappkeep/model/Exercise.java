package com.sarachen.androidappkeep.model;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Exercise {
    int id;
    String name, imageUrl, videoUrl;
    List<String> steps, breathes, commonMistakes, moveFeelingPictures, movementFeelinigs;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public List<String> getSteps() {
        return steps;
    }

    public List<String> getBreathes() {
        return breathes;
    }

    public List<String> getCommonMistakes() {
        return commonMistakes;
    }

    public List<String> getMoveFeelingPictures() {
        return moveFeelingPictures;
    }

    public List<String> getMovementFeelinigs() {
        return movementFeelinigs;
    }

    public Exercise() {
    }

    public Exercise(int id, String name, String imageUrl, String videoUrl, List<String> steps, List<String> breathes, List<String> commonMistakes, List<String> moveFeelingPictures, List<String> movementFeelinigs) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.steps = steps;
        this.breathes = breathes;
        this.commonMistakes = commonMistakes;
        this.moveFeelingPictures = moveFeelingPictures;
        this.movementFeelinigs = movementFeelinigs;
    }

}

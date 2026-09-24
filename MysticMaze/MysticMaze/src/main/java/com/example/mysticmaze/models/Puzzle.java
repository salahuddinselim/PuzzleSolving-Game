package com.example.mysticmaze.models;

public class Puzzle {
    // level, type, puzzle_data,solution, minimum_moves, minimum_time_taken, difficulty
    private int puzzleId;
    private int level;
    private String type;
    private String puzzleData;
    private String solution;
    private  int minimum_moves;
    private  float minimum_time_taken;
    private  String difficulty;

    public Puzzle(int puzzleId, int level, String type, String puzzleData, String solution, int minimum_moves, float minimum_time_taken, String difficulty) {
        this.puzzleId = puzzleId;
        this.level = level;
        this.type = type;
        this.puzzleData = puzzleData;
        this.solution = solution;
        this.minimum_moves = minimum_moves;
        this.minimum_time_taken = minimum_time_taken;
        this.difficulty = difficulty;

    }

    // Getters and Setters
    public int getPuzzleId() { return puzzleId; }
    public void setPuzzleId(int puzzleId) { this.puzzleId = puzzleId; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPuzzleData() { return puzzleData; }
    public void setPuzzleData(String puzzleData) { this.puzzleData = puzzleData; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }

    public int getMinimum_moves() { return minimum_moves; }
    public float getMinimum_time_taken() {return minimum_time_taken; }
    public String getDifficulty() {return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setMinimum_moves(int minimum_moves) {this.minimum_moves = minimum_moves;}
    public void setMinimum_time_taken(float minimum_time_taken) {this.minimum_time_taken = minimum_time_taken;}
}

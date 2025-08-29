package com.finserv.demo.dto;

public class SolutionRequest {
    private String finalQuery;

    // Default constructor
    public SolutionRequest() {}

    // Parameterized constructor
    public SolutionRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    // Getters and Setters
    public String getFinalQuery() {
        return finalQuery;
    }

    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    @Override
    public String toString() {
        return "SolutionRequest{" +
                "finalQuery='" + finalQuery + '\'' +
                '}';
    }
}

package com.finserv.demo.dto;

public class WebhookResponse {
    private String webhook;
    private String accessToken;

    // Default constructor
    public WebhookResponse() {}

    // Parameterized constructor
    public WebhookResponse(String webhook, String accessToken) {
        this.webhook = webhook;
        this.accessToken = accessToken;
    }

    // Getters and Setters
    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "WebhookResponse{" +
                "webhook='" + webhook + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}

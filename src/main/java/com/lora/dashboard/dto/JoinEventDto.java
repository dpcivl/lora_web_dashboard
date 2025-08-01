package com.lora.dashboard.dto;

import com.lora.dashboard.entity.SignalQuality;
import com.lora.dashboard.entity.JoinEvent;


public class JoinEventDto {
    private Long id;
    private String timestamp;
    private String applicationId;
    private String deviceId;
    private String devEui;
    private String joinEui;
    private String devAddr;
    private Integer frequency;
    private Integer dataRate;
    private Float rssi;
    private Float snr;
    private Float latitude;
    private Float longitude;
    private String hostname;
    private String rawTopic;
    private String createdAt;
    private String signalQuality;

    public JoinEventDto() {}

    public JoinEventDto(JoinEvent entity) {
        this.id = entity.getId();
        this.timestamp = entity.getTimestamp();
        this.applicationId = entity.getApplicationId();
        this.deviceId = entity.getDeviceId();
        this.devEui = entity.getDevEui();
        this.joinEui = entity.getJoinEui();
        this.devAddr = entity.getDevAddr();
        this.frequency = entity.getFrequency();
        this.dataRate = entity.getDataRate();
        this.rssi = entity.getRssi();
        this.snr = entity.getSnr();
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
        this.hostname = entity.getHostname();
        this.rawTopic = entity.getRawTopic();
        this.createdAt = entity.getCreatedAt();
        
        // 신호 품질 계산
        if (entity.getRssi() != null && entity.getSnr() != null) {
            Float rssi = entity.getRssi();
            Float snr = entity.getSnr();
            if (rssi > -70 && snr > 10) {
                this.signalQuality = SignalQuality.EXCELLENT.name();
            } else if (rssi > -85 && snr > 5) {
                this.signalQuality = SignalQuality.GOOD.name();
            } else if (rssi > -100 && snr > 0) {
                this.signalQuality = SignalQuality.FAIR.name();
            } else {
                this.signalQuality = SignalQuality.POOR.name();
            }
        } else {
            this.signalQuality = SignalQuality.POOR.name();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDevEui() { return devEui; }
    public void setDevEui(String devEui) { this.devEui = devEui; }

    public String getJoinEui() { return joinEui; }
    public void setJoinEui(String joinEui) { this.joinEui = joinEui; }

    public String getDevAddr() { return devAddr; }
    public void setDevAddr(String devAddr) { this.devAddr = devAddr; }

    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }

    public Integer getDataRate() { return dataRate; }
    public void setDataRate(Integer dataRate) { this.dataRate = dataRate; }

    public Float getRssi() { return rssi; }
    public void setRssi(Float rssi) { this.rssi = rssi; }

    public Float getSnr() { return snr; }
    public void setSnr(Float snr) { this.snr = snr; }

    public Float getLatitude() { return latitude; }
    public void setLatitude(Float latitude) { this.latitude = latitude; }

    public Float getLongitude() { return longitude; }
    public void setLongitude(Float longitude) { this.longitude = longitude; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getRawTopic() { return rawTopic; }
    public void setRawTopic(String rawTopic) { this.rawTopic = rawTopic; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getSignalQuality() { return signalQuality; }
    public void setSignalQuality(String signalQuality) { this.signalQuality = signalQuality; }
}
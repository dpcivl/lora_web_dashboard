package com.lora.dashboard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "join_events")
public class JoinEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "application_id", nullable = false)
    private String applicationId;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "dev_eui", nullable = false)
    private String devEui;
    
    @Column(name = "join_eui")
    private String joinEui;
    
    @Column(name = "dev_addr")
    private String devAddr;
    
    @Column(name = "frequency")
    private Integer frequency;
    
    @Column(name = "data_rate")
    private Integer dataRate;
    
    @Column(name = "rssi")
    private Float rssi;
    
    @Column(name = "snr")
    private Float snr;
    
    @Column(name = "latitude")
    private Float latitude;
    
    @Column(name = "longitude")
    private Float longitude;
    
    @Column(name = "hostname")
    private String hostname;
    
    @Column(name = "raw_topic")
    private String rawTopic;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor
    public JoinEvent() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevEui() {
        return devEui;
    }

    public void setDevEui(String devEui) {
        this.devEui = devEui;
    }

    public String getJoinEui() {
        return joinEui;
    }

    public void setJoinEui(String joinEui) {
        this.joinEui = joinEui;
    }

    public String getDevAddr() {
        return devAddr;
    }

    public void setDevAddr(String devAddr) {
        this.devAddr = devAddr;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getDataRate() {
        return dataRate;
    }

    public void setDataRate(Integer dataRate) {
        this.dataRate = dataRate;
    }

    public Float getRssi() {
        return rssi;
    }

    public void setRssi(Float rssi) {
        this.rssi = rssi;
    }

    public Float getSnr() {
        return snr;
    }

    public void setSnr(Float snr) {
        this.snr = snr;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getRawTopic() {
        return rawTopic;
    }

    public void setRawTopic(String rawTopic) {
        this.rawTopic = rawTopic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "JoinEvent{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", deviceId='" + deviceId + '\'' +
                ", devEui='" + devEui + '\'' +
                ", devAddr='" + devAddr + '\'' +
                '}';
    }
}
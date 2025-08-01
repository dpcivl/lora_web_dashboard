package com.lora.dashboard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "uplink_messages")
public class UplinkMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "timestamp", nullable = false)
    private String timestamp;
    
    @Column(name = "application_id", nullable = false)
    private String applicationId;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "dev_eui")
    private String devEui;
    
    @Column(name = "payload_base64")
    private String payloadBase64;
    
    @Column(name = "payload_hex")
    private String payloadHex;
    
    @Column(name = "payload_text")
    private String payloadText;
    
    @Column(name = "payload_size")
    private Integer payloadSize;
    
    @Column(name = "frame_count")
    private Integer frameCount;
    
    @Column(name = "f_port")
    private Integer fPort;
    
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
    private String createdAt;

    // Default constructor
    public UplinkMessage() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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

    public String getPayloadBase64() {
        return payloadBase64;
    }

    public void setPayloadBase64(String payloadBase64) {
        this.payloadBase64 = payloadBase64;
    }

    public String getPayloadHex() {
        return payloadHex;
    }

    public void setPayloadHex(String payloadHex) {
        this.payloadHex = payloadHex;
    }

    public String getPayloadText() {
        return payloadText;
    }

    public void setPayloadText(String payloadText) {
        this.payloadText = payloadText;
    }

    public Integer getPayloadSize() {
        return payloadSize;
    }

    public void setPayloadSize(Integer payloadSize) {
        this.payloadSize = payloadSize;
    }

    public Integer getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(Integer frameCount) {
        this.frameCount = frameCount;
    }

    public Integer getfPort() {
        return fPort;
    }

    public void setfPort(Integer fPort) {
        this.fPort = fPort;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "UplinkMessage{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", deviceId='" + deviceId + '\'' +
                ", payloadText='" + payloadText + '\'' +
                ", rssi=" + rssi +
                ", snr=" + snr +
                '}';
    }
}
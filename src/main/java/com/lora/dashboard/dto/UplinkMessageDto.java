package com.lora.dashboard.dto;

import com.lora.dashboard.entity.SignalQuality;
import com.lora.dashboard.entity.UplinkMessage;

import java.time.LocalDateTime;

public class UplinkMessageDto {
    private Long id;
    private LocalDateTime timestamp;
    private String applicationId;
    private String deviceId;
    private String devEui;
    private String payloadBase64;
    private String payloadHex;
    private String payloadText;
    private Integer payloadSize;
    private Integer frameCount;
    private Integer fPort;
    private Integer frequency;
    private Integer dataRate;
    private Float rssi;
    private Float snr;
    private Float latitude;
    private Float longitude;
    private String hostname;
    private String rawTopic;
    private LocalDateTime createdAt;
    private String signalQuality;

    public UplinkMessageDto() {}

    public UplinkMessageDto(UplinkMessage entity) {
        this.id = entity.getId();
        this.timestamp = entity.getTimestamp();
        this.applicationId = entity.getApplicationId();
        this.deviceId = entity.getDeviceId();
        this.devEui = entity.getDevEui();
        this.payloadBase64 = entity.getPayloadBase64();
        this.payloadHex = entity.getPayloadHex();
        this.payloadText = entity.getPayloadText();
        this.payloadSize = entity.getPayloadSize();
        this.frameCount = entity.getFrameCount();
        this.fPort = entity.getfPort();
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

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDevEui() { return devEui; }
    public void setDevEui(String devEui) { this.devEui = devEui; }

    public String getPayloadBase64() { return payloadBase64; }
    public void setPayloadBase64(String payloadBase64) { this.payloadBase64 = payloadBase64; }

    public String getPayloadHex() { return payloadHex; }
    public void setPayloadHex(String payloadHex) { this.payloadHex = payloadHex; }

    public String getPayloadText() { return payloadText; }
    public void setPayloadText(String payloadText) { this.payloadText = payloadText; }

    public Integer getPayloadSize() { return payloadSize; }
    public void setPayloadSize(Integer payloadSize) { this.payloadSize = payloadSize; }

    public Integer getFrameCount() { return frameCount; }
    public void setFrameCount(Integer frameCount) { this.frameCount = frameCount; }

    public Integer getfPort() { return fPort; }
    public void setfPort(Integer fPort) { this.fPort = fPort; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSignalQuality() { return signalQuality; }
    public void setSignalQuality(String signalQuality) { this.signalQuality = signalQuality; }
}
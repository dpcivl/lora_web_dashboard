package com.lora.dashboard.dto;

import java.util.List;
import java.util.Map;

public class StatisticsDto {
    private Long totalMessages;
    private Long last24HourMessages;
    private Long activeDevices;
    private Long totalJoinEvents;
    private Long recentJoinEvents;
    private List<DeviceCountDto> deviceCounts;
    private SignalQualityStatsDto signalQuality;
    private List<HourlyCountDto> hourlyCounts;

    public StatisticsDto() {}

    // Getters and Setters
    public Long getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(Long totalMessages) {
        this.totalMessages = totalMessages;
    }

    public Long getLast24HourMessages() {
        return last24HourMessages;
    }

    public void setLast24HourMessages(Long last24HourMessages) {
        this.last24HourMessages = last24HourMessages;
    }

    public Long getActiveDevices() {
        return activeDevices;
    }

    public void setActiveDevices(Long activeDevices) {
        this.activeDevices = activeDevices;
    }

    public Long getTotalJoinEvents() {
        return totalJoinEvents;
    }

    public void setTotalJoinEvents(Long totalJoinEvents) {
        this.totalJoinEvents = totalJoinEvents;
    }

    public Long getRecentJoinEvents() {
        return recentJoinEvents;
    }

    public void setRecentJoinEvents(Long recentJoinEvents) {
        this.recentJoinEvents = recentJoinEvents;
    }

    public List<DeviceCountDto> getDeviceCounts() {
        return deviceCounts;
    }

    public void setDeviceCounts(List<DeviceCountDto> deviceCounts) {
        this.deviceCounts = deviceCounts;
    }

    public SignalQualityStatsDto getSignalQuality() {
        return signalQuality;
    }

    public void setSignalQuality(SignalQualityStatsDto signalQuality) {
        this.signalQuality = signalQuality;
    }

    public List<HourlyCountDto> getHourlyCounts() {
        return hourlyCounts;
    }

    public void setHourlyCounts(List<HourlyCountDto> hourlyCounts) {
        this.hourlyCounts = hourlyCounts;
    }

    // Inner classes for nested data
    public static class DeviceCountDto {
        private String deviceId;
        private Long count;

        public DeviceCountDto(String deviceId, Long count) {
            this.deviceId = deviceId;
            this.count = count;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    public static class SignalQualityStatsDto {
        private Long excellent;
        private Long good;
        private Long fair;
        private Long poor;

        public SignalQualityStatsDto(Long excellent, Long good, Long fair, Long poor) {
            this.excellent = excellent;
            this.good = good;
            this.fair = fair;
            this.poor = poor;
        }

        public Long getExcellent() {
            return excellent;
        }

        public void setExcellent(Long excellent) {
            this.excellent = excellent;
        }

        public Long getGood() {
            return good;
        }

        public void setGood(Long good) {
            this.good = good;
        }

        public Long getFair() {
            return fair;
        }

        public void setFair(Long fair) {
            this.fair = fair;
        }

        public Long getPoor() {
            return poor;
        }

        public void setPoor(Long poor) {
            this.poor = poor;
        }
    }

    public static class HourlyCountDto {
        private String hour;
        private Long count;

        public HourlyCountDto(String hour, Long count) {
            this.hour = hour;
            this.count = count;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
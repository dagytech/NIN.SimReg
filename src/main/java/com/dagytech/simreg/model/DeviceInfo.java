package com.dagytech.simreg.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_info")
public class DeviceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sim_registration_id")
    private SimRegistration simRegistration;

    private String deviceFingerprint;
    private String ipAddress;
    private LocalDateTime recordedAt = LocalDateTime.now();

    public DeviceInfo() {
    }

    public DeviceInfo(SimRegistration simRegistration, String deviceFingerprint, String ipAddress) {
        this.simRegistration = simRegistration;
        this.deviceFingerprint = deviceFingerprint;
        this.ipAddress = ipAddress;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SimRegistration getSimRegistration() { return simRegistration; }
    public void setSimRegistration(SimRegistration simRegistration) { this.simRegistration = simRegistration; }
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}

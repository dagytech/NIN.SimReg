package com.dagytech.simreg.repository;

import com.dagytech.simreg.model.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
// inteface segregation/ hii ni interface ndogo yenye jukumu moja tu kuhusu device information


public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> {
    long countByDeviceFingerprintAndRecordedAtAfter(String deviceFingerprint, LocalDateTime since);
}

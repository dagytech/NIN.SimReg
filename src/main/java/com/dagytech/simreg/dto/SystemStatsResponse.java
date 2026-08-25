package com.dagytech.simreg.dto;

public class SystemStatsResponse {
    private long totalCustomers;
    private long totalRegistrations;
    private long completedRegistrations;
    private long pendingRegistrations;
    private long securityEventsLast50;

    public SystemStatsResponse(long totalCustomers, long totalRegistrations, long completedRegistrations,
                                long pendingRegistrations, long securityEventsLast50) {
        this.totalCustomers = totalCustomers;
        this.totalRegistrations = totalRegistrations;
        this.completedRegistrations = completedRegistrations;
        this.pendingRegistrations = pendingRegistrations;
        this.securityEventsLast50 = securityEventsLast50;
    }

    public long getTotalCustomers() { return totalCustomers; }
    public long getTotalRegistrations() { return totalRegistrations; }
    public long getCompletedRegistrations() { return completedRegistrations; }
    public long getPendingRegistrations() { return pendingRegistrations; }
    public long getSecurityEventsLast50() { return securityEventsLast50; }
}

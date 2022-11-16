package org.monitor.quartz.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name="DISK")
public class Disk extends PanacheEntity {
    private final Instant createdAt;
    private String diskName;
    private long diskSize;
    private long totalReads;
    private long totalWrites;
    private long readBytes;
    private long writeBytes;

    public Disk() {
        createdAt = Instant.now();
    }

    public Disk(String diskName, long diskSize, long totalReads, long totalWrites, long readBytes, long writeBytes) {
        this.createdAt = Instant.now();
        this.diskName = diskName;
        this.diskSize = diskSize;
        this.totalReads = totalReads;
        this.totalWrites = totalWrites;
        this.readBytes = readBytes;
        this.writeBytes = writeBytes;
    }
}

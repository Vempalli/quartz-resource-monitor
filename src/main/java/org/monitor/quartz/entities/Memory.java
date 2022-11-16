package org.monitor.quartz.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name="MEMORY")
public class Memory extends PanacheEntity {
    private long availableMemory;
    private long totalMemory;
    private long usedMemory;
    private double memoryUtilization;
    private Instant createdAt;

    public Memory() {
        createdAt = Instant.now();
    }

    public Memory(long availableMemory, long totalMemory) {
        this.createdAt = Instant.now();
        this.availableMemory = availableMemory;
        this.totalMemory = totalMemory;
        this.usedMemory = this.totalMemory - this.availableMemory;
        this.memoryUtilization = (this.usedMemory / (double) this.totalMemory) * 100;
    }
}

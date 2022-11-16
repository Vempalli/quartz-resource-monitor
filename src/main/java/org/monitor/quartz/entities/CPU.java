package org.monitor.quartz.entities;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name="CPU")
public class CPU extends PanacheEntity {
    private Instant createdAt;
    private long userTick;
    private long nice;
    private long systemTick;
    private long idle;
    private long ioWait;
    private long irq;
    private long softIrq;
    private long totalCPU;

    protected CPU () {}

    private CPU(long userTick, long nice, long systemTick, long idle, long ioWait, long irq, long softIrq) {
        this.userTick = userTick;
        this.nice = nice;
        this.systemTick = systemTick;
        this.idle = idle;
        this.ioWait = ioWait;
        this.irq = irq;
        this.softIrq = softIrq;
        this.totalCPU = nice + irq + softIrq + ioWait + systemTick + userTick + idle;
        this.createdAt = Instant.now();
    }

    public static final class CPUBuilder {
        private long userTick;
        private long nice;
        private long systemTick;
        private long idle;
        private long ioWait;
        private long irq;
        private long softIrq;

        public CPUBuilder() {}

        public static CPUBuilder aCPU() {
            return new CPUBuilder();
        }

        public CPUBuilder withUserTick(long val) {
            userTick = val;
            return this;
        }

        public CPUBuilder withNice(long val) {
            nice = val;
            return this;
        }

        public CPUBuilder withSystemTick(long val) {
            systemTick = val;
            return this;
        }

        public CPUBuilder withIdle(long val) {
            idle = val;
            return this;
        }

        public CPUBuilder withIoWait(long val) {
            ioWait = val;
            return this;
        }

        public CPUBuilder withIrq(long val) {
            irq = val;
            return this;
        }

        public CPUBuilder withSoftIqr(long val) {
            softIrq = val;
            return this;
        }

        public CPU build() {
            return new CPU(userTick, nice, systemTick, idle, ioWait, irq, softIrq);
        }
    }
}

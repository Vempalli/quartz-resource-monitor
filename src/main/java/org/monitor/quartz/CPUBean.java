package org.monitor.quartz;

import io.quarkus.scheduler.Scheduled;
import org.monitor.quartz.entities.CPU;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class CPUBean {
    private static long[] oldTicks;

    @Transactional
    @Scheduled(every = "${my.interval-cpu-job}", identity = "cpu-job")
    /**
     * periodically persist the cpu metrics to database
     */
    void schedule() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        CPU cpu = fetchCPUMetrics(hardwareAbstractionLayer.getProcessor());
        cpu.persist();
    }

    /**
     * use oshi library to fetch required metrics
     * Ex:
     *  id |         createdat          | idle  | iowait | irq | nice | softirq | systemtick | totalcpu | usertick
     * ----+----------------------------+-------+--------+-----+------+---------+------------+----------+----------
     *   6 | 2022-11-15 18:23:40.873139 | 14699 |      0 |   0 |    0 |       0 |        464 |    15794 |      631
     */
    private CPU fetchCPUMetrics(CentralProcessor processor) {
        long[] ticks = processor.getSystemCpuLoadTicks();
        if (oldTicks == null) {
            oldTicks = ticks;
        }
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - oldTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - oldTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - oldTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - oldTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - oldTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        oldTicks = ticks;
        return new CPU.CPUBuilder().withNice(nice)
                .withIoWait(ioWait)
                .withIrq(irq)
                .withSoftIqr(softIrq)
                .withUserTick(user)
                .withSystemTick(sys)
                .withIdle(idle)
                .build();
    }
}

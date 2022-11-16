package org.monitor.quartz;

import io.quarkus.scheduler.Scheduled;
import org.monitor.quartz.entities.Memory;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class MemoryBean {

    @Transactional
    @Scheduled(every = "${my.interval-memory-job}", identity = "memory-job")
    void schedule() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        Memory memory = fetchMemoryMetrics(hardwareAbstractionLayer.getMemory());
        memory.persist();
    }
    /*
    Sample memory metric:
     id | availablememory |         createdat          | memoryutilization | totalmemory | usedmemory
    ----+-----------------+----------------------------+-------------------+-------------+------------
      2 |       634757120 | 2022-11-16 02:23:30.856368 | 69.50207918005718 |  2081312768 | 1446555648
     */
    private Memory fetchMemoryMetrics(GlobalMemory memory) {
        return new Memory(memory.getAvailable(), memory.getTotal());
    }
}

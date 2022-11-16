package org.monitor.quartz;

import io.quarkus.scheduler.Scheduled;
import org.monitor.quartz.entities.Disk;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DiskBean {

    @Transactional
    @Scheduled(every = "${my.interval-disk-job}", identity = "disk-job")
    void schedule() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        List<Disk> disks = fetchDiskMetrics(hardwareAbstractionLayer.getDiskStores());
        for (Disk disk : disks) {
            disk.persist();
        }
    }

    /**
     * fetch disk metrics for every disk present in the system
     * Ex record:
     * id |         createdat          | diskname |  disksize   | readbytes | totalreads | totalwrites | writebytes
     * ----+----------------------------+----------+-------------+-----------+------------+-------------+------------
     *   1 | 2022-11-16 02:23:30.839714 | /dev/vda | 63999836160 | 619736064 |      15089 |      164952 | 2596454400
     */
    private List<Disk> fetchDiskMetrics(List<HWDiskStore> diskStores) {
        List<Disk> disks = new ArrayList<>();
        for (HWDiskStore disk : diskStores) {
            // Noticed in certain cases - like docker running, there are several empty disks shown
            // so removing them for maintaining data quality
            if (disk.getSize() > 0) {
                disks.add(new Disk(disk.getName(), disk.getSize(), disk.getReads(), disk.getWrites(), disk.getReadBytes(),
                        disk.getWriteBytes()));
            }
        }
        return disks;
    }
}

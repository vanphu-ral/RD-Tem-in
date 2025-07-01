package com.mycompany.myapp.domain;

import javax.persistence.*;

@Entity
@Table(name = "scan_machines")
public class scanMachines {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_id")
    private Integer machineId;

    @Column(name = "machine_name")
    private String machineName;

    @Column(name = "group_id")
    private Integer groupId;

    public scanMachines() {}

    public scanMachines(Integer machineId, String machineName, Integer groupId) {
        this.machineId = machineId;
        this.machineName = machineName;
        this.groupId = groupId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}

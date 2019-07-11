package com.daishuai.jpa.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/7 17:03
 */
@Data
@Entity
@Table(name = "vehicle_info")
public class VehicleInfo {
    
    @Id
    private Integer id;
    
    private String carCode;
    
    private Integer exist;
}

package com.hm.ieam.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wepon on 2017/12/4.
 * 数据模型
 */

public class YwpAddressBean implements Serializable {

    private List<AddressItemBean> area;
    private List<AddressItemBean> community;
    private List<AddressItemBean> building;
    private List<AddressItemBean> unit;
    private List<AddressItemBean> floor;
    private List<AddressItemBean> house;


    public List<AddressItemBean> getArea() {
        return area;
    }

    public void setArea(List<AddressItemBean> area) {
        this.area = area;
    }

    public List<AddressItemBean> getCommunity() {
        return community;
    }

    public void setCommunity(List<AddressItemBean> community) {
        this.community = community;
    }

    public List<AddressItemBean> getBuilding() {
        return building;
    }

    public void setBuilding(List<AddressItemBean> building) {
        this.building = building;
    }

    public List<AddressItemBean> getUnit() {
        return unit;
    }

    public void setUnit(List<AddressItemBean> unit) {
        this.unit = unit;
    }

    public List<AddressItemBean> getFloor() {
        return floor;
    }

    public void setFloor(List<AddressItemBean> floor) {
        this.floor = floor;
    }

    public List<AddressItemBean> getHouse() {
        return house;
    }

    public void setHouse(List<AddressItemBean> house) {
        this.house = house;
    }

    public static class AddressItemBean implements Serializable {
        private String i;
        private String n;
        private String p;

        public String getI() {
            return i;
        }

        public void setI(String i) {
            this.i = i;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }
    }
}

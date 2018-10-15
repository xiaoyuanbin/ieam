package com.hm.ieam.bean;

import java.io.Serializable;
import java.util.List;

public class AddressBean {
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

    }
}

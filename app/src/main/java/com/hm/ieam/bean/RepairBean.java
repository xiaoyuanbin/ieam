package com.hm.ieam.bean;

import android.net.Uri;

import java.io.Serializable;

public class RepairBean implements Serializable{
    private String id;
    private String tbname;
    private String images;

    private String repair_type;
    private String repair_address;
    private String repair_laborbudget;
    private String repair_stuffbudget;
    private String repair_costbudget;
    private String repair_deal;
    private String repair_remark;
    private String repair_state;

    private String report_firm;
    private String report_type;
    private String report_address;
    private String report_date;
    private String report_position;

    public String getRepair_state() {
        return repair_state;
    }

    public void setRepair_state(String repair_state) {
        this.repair_state = repair_state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTbname() {
        return tbname;
    }

    public void setTbname(String tbname) {
        this.tbname = tbname;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getRepair_type() {
        return repair_type;
    }

    public void setRepair_type(String repair_type) {
        this.repair_type = repair_type;
    }

    public String getRepair_address() {
        return repair_address;
    }

    public void setRepair_address(String repair_address) {
        this.repair_address = repair_address;
    }

    public String getRepair_laborbudget() {
        return repair_laborbudget;
    }

    public void setRepair_laborbudget(String repair_laborbudget) {
        this.repair_laborbudget = repair_laborbudget;
    }

    public String getRepair_stuffbudget() {
        return repair_stuffbudget;
    }

    public void setRepair_stuffbudget(String repair_stuffbudget) {
        this.repair_stuffbudget = repair_stuffbudget;
    }

    public String getRepair_costbudget() {
        return repair_costbudget;
    }

    public void setRepair_costbudget(String repair_costbudget) {
        this.repair_costbudget = repair_costbudget;
    }

    public String getRepair_deal() {
        return repair_deal;
    }

    public void setRepair_deal(String repair_deal) {
        this.repair_deal = repair_deal;
    }

    public String getRepair_remark() {
        return repair_remark;
    }

    public void setRepair_remark(String repair_remark) {
        this.repair_remark = repair_remark;
    }

    public String getReport_firm() {
        return report_firm;
    }

    public void setReport_firm(String report_firm) {
        this.report_firm = report_firm;
    }

    public String getReport_type() {
        return report_type;
    }

    public void setReport_type(String report_type) {
        this.report_type = report_type;
    }

    public String getReport_address() {
        return report_address;
    }

    public void setReport_address(String report_address) {
        this.report_address = report_address;
    }

    public String getReport_date() {
        return report_date;
    }

    public void setReport_date(String report_date) {
        this.report_date = report_date;
    }

    public String getReport_position() {
        return report_position;
    }

    public void setReport_position(String report_position) {
        this.report_position = report_position;
    }
}

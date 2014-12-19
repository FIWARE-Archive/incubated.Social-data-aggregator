package com.tilab.ca.sda.ctw.hibernate.mapping;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="on_monitoring_geo")
public class OnMonitoringGeo implements Serializable{

	private static final long serialVersionUID = 8484954139984452386L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="latitude_from")
	private double latitudeFrom;
	
	@Column(name="latitude_to")
	private double latitudeTo;
	
	@Column(name="longitude_from")
	private double longitudeFrom;
	
	@Column(name="longitude_to")
	private double longitudeTo;
	
	@Column(name="tw_count")
	private int twCount;
	
	@Column(name="tw_count_week")
	private int twCountWeek;
	
	@Column(name="tw_count_month")
	private int twCountMonth;
	
	@Column(name="on_monitoring_from")
	private Date onMonitoringFrom=null;
	
	@Column(name="monitor_from_node")
	private String monitorFromNode=null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLatitudeFrom() {
		return latitudeFrom;
	}

	public void setLatitudeFrom(double latitudeFrom) {
		this.latitudeFrom = latitudeFrom;
	}

	public double getLatitudeTo() {
		return latitudeTo;
	}

	public void setLatitudeTo(double latitudeTo) {
		this.latitudeTo = latitudeTo;
	}

	public double getLongitudeFrom() {
		return longitudeFrom;
	}

	public void setLongitudeFrom(double longitudeFrom) {
		this.longitudeFrom = longitudeFrom;
	}

	public double getLongitudeTo() {
		return longitudeTo;
	}

	public void setLongitudeTo(double longitudeTo) {
		this.longitudeTo = longitudeTo;
	}

	public int getTwCount() {
		return twCount;
	}

	public void setTwCount(int twCount) {
		this.twCount = twCount;
	}

	public int getTwCountWeek() {
		return twCountWeek;
	}

	public void setTwCountWeek(int twCountWeek) {
		this.twCountWeek = twCountWeek;
	}

	public int getTwCountMonth() {
		return twCountMonth;
	}

	public void setTwCountMonth(int twCountMonth) {
		this.twCountMonth = twCountMonth;
	}

	public Date getOnMonitoringFrom() {
		return onMonitoringFrom;
	}

	public void setOnMonitoringFrom(Date onMonitoringFrom) {
		this.onMonitoringFrom = onMonitoringFrom;
	}

	public String getMonitorFromNode() {
		return monitorFromNode;
	}

	public void setMonitorFromNode(String monitorFromNode) {
		this.monitorFromNode = monitorFromNode;
	}
}

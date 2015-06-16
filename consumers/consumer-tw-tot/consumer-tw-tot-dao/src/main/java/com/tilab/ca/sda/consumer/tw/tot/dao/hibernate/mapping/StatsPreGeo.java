package com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping;


import com.tilab.ca.sda.sda.model.keys.GeoLocTruncTimeKey;
import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="ss_stats_pre_geo")
public class StatsPreGeo implements Serializable{
	
	private static final long serialVersionUID = -1968411105206976328L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="lat_trunc")
	private double latTrunc;
	
	@Column(name="long_trunc")
	private double longTrunc;
	
	@Column(name="created_at")
	private Date createdAt=null;
	
	@Column(name="num_tw")
	private int numTw=0;
	
	@Column(name="num_rtw")
	private int numRtw=0;
	
	@Column(name="num_rply")
	private int numReply=0;
	
	@Column(name="tot_tw")
	private int totTw=0;

	
	public StatsPreGeo(){}
	
	public StatsPreGeo(GeoLocTruncTimeKey gltc,StatsCounter sgc){
		super();
		this.latTrunc = gltc.getGeoLocTruncKey().getLatTrunc();
		this.longTrunc = gltc.getGeoLocTruncKey().getLongTrunc();
		this.createdAt = gltc.getDate();
		this.numTw = sgc.getNumTw();
		this.numRtw = sgc.getNumRtw();
		this.numReply = sgc.getNumReply();
		this.totTw = numTw+numRtw+numReply;
	}
	
	public StatsPreGeo(double latTrunc, double longTrunc, Date createdAt,
			int numTw, int numRtw, int numReply, int totTw) {
		super();
		this.latTrunc = latTrunc;
		this.longTrunc = longTrunc;
		this.createdAt = createdAt;
		this.numTw = numTw;
		this.numRtw = numRtw;
		this.numReply = numReply;
		this.totTw = totTw;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLatTrunc() {
		return latTrunc;
	}

	public void setLatTrunc(double latTrunc) {
		this.latTrunc = latTrunc;
	}

	public double getLongTrunc() {
		return longTrunc;
	}

	public void setLongTrunc(double longTrunc) {
		this.longTrunc = longTrunc;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getNumTw() {
		return numTw;
	}

	public void setNumTw(int numTw) {
		this.numTw = numTw;
	}

	public int getNumRtw() {
		return numRtw;
	}

	public void setNumRtw(int numRtw) {
		this.numRtw = numRtw;
	}

	public int getNumReply() {
		return numReply;
	}

	public void setNumReply(int numReply) {
		this.numReply = numReply;
	}

	public int getTotTw() {
		return totTw;
	}

	public void setTotTw(int totTw) {
		this.totTw = totTw;
	}
}

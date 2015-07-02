package com.tilab.ca.sda.consumer.tw.tot.dao.hibernate.mapping;

import com.tilab.ca.sda.consumer.tw.tot.core.data.StatsCounter;
import com.tilab.ca.sda.ctw.utils.Utils;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ss_stats_pre_hts_bound")
public class StatsPreHtsBound extends StatsPre {

    private static final long serialVersionUID = -1545245637924683920L;

    
    @Column(name = "hash_tag")
    private String hashTag = null;
	
@Column(name = "from_time")
    private Date from = null;

	@Column(name = "to_time")
    private Date to = null;


    public StatsPreHtsBound() {
        super();
    }

    public StatsPreHtsBound(Date from, Date to, String ht, StatsCounter sgc) {
        super();
        this.hashTag = ht;
        this.from = from;
        this.to = to;
        this.numTw = sgc.getNumTw();
        this.numRtw = sgc.getNumRtw();
        this.numReply = sgc.getNumReply();
        this.totTw = numTw + numRtw + numReply;
    }

   

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
    
    @Override
    public String toString(){
        return String.format("%s,%s,%s,%d,%d,%d,%d",hashTag,Utils.Time.date2ZonedDateTime(from).toString(),
                                                    Utils.Time.date2ZonedDateTime(to).toString(),
                                                    numTw,numRtw,numReply);
    }

}

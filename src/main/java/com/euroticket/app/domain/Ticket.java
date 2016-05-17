package com.euroticket.app.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Ticket.
 */
@Entity
@Table(name = "ticket")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "ticket")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "location")
    private String location;

    @Column(name = "match_date")
    private ZonedDateTime matchDate;

    @Column(name = "unit_price", precision=10, scale=2)
    private BigDecimal unitPrice;

    @Column(name = "available_qtt", precision=10, scale=2)
    private BigDecimal availableQtt;

    @Column(name = "total_qtt", precision=10, scale=2)
    private BigDecimal totalQtt;

    @OneToOne
    @JoinColumn(unique = true)
    private Team homeTeam;

    @OneToOne
    @JoinColumn(unique = true)
    private Team awayTeam;

    @OneToOne
    @JoinColumn(unique = true)
    private MatchGroup matchGroup;

    @OneToOne
    @JoinColumn(unique = true)
    private Phase phase;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ZonedDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(ZonedDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAvailableQtt() {
        return availableQtt;
    }

    public void setAvailableQtt(BigDecimal availableQtt) {
        this.availableQtt = availableQtt;
    }

    public BigDecimal getTotalQtt() {
        return totalQtt;
    }

    public void setTotalQtt(BigDecimal totalQtt) {
        this.totalQtt = totalQtt;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team team) {
        this.homeTeam = team;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team team) {
        this.awayTeam = team;
    }

    public MatchGroup getMatchGroup() {
        return matchGroup;
    }

    public void setMatchGroup(MatchGroup matchGroup) {
        this.matchGroup = matchGroup;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        if(ticket.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ticket{" +
            "id=" + id +
            ", location='" + location + "'" +
            ", matchDate='" + matchDate + "'" +
            ", unitPrice='" + unitPrice + "'" +
            ", availableQtt='" + availableQtt + "'" +
            ", totalQtt='" + totalQtt + "'" +
            '}';
    }
}

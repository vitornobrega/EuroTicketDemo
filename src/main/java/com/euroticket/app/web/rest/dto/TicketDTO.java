package com.euroticket.app.web.rest.dto;

import java.time.ZonedDateTime;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Ticket entity.
 */
public class TicketDTO implements Serializable {

    private Long id;

    private String location;


    private ZonedDateTime matchDate;


    private BigDecimal unitPrice;


    private BigDecimal availableQtt;


    private BigDecimal totalQtt;


    private Long homeTeamId;
    private Long awayTeamId;
    private Long matchGroupId;
    private Long phaseId;
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

    public Long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(Long teamId) {
        this.homeTeamId = teamId;
    }
    public Long getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(Long teamId) {
        this.awayTeamId = teamId;
    }
    public Long getMatchGroupId() {
        return matchGroupId;
    }

    public void setMatchGroupId(Long matchGroupId) {
        this.matchGroupId = matchGroupId;
    }
    public Long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Long phaseId) {
        this.phaseId = phaseId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TicketDTO ticketDTO = (TicketDTO) o;

        if ( ! Objects.equals(id, ticketDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TicketDTO{" +
            "id=" + id +
            ", location='" + location + "'" +
            ", matchDate='" + matchDate + "'" +
            ", unitPrice='" + unitPrice + "'" +
            ", availableQtt='" + availableQtt + "'" +
            ", totalQtt='" + totalQtt + "'" +
            '}';
    }
}

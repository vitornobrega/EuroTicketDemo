package com.euroticket.app.web.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Item entity.
 */
public class ItemDTO implements Serializable {

    private Long id;

    private Integer quantity;

    private Long ticketId;
    private String ticketLocation;


    private ZonedDateTime ticketMatchDate;


    private BigDecimal unitPrice;


    private BigDecimal ticketAvailableQtt;


    private BigDecimal ticketTotalQtt;


    private Long ticketHomeTeamId;
    private Long ticketAwayTeamId;
    private Long ticketMatchGroupId;
    private Long ticketPhaseId;
    
    private String ticketHomeTeamName;
    private String ticketAwayTeamName;
    private String ticketPhaseName;
    private String ticketMatchGroupName;
    private Long saleId;
    
    
    
    public String getTicketLocation() {
		return ticketLocation;
	}

	public void setTicketLocation(String ticketLocation) {
		this.ticketLocation = ticketLocation;
	}

	public ZonedDateTime getTicketMatchDate() {
		return ticketMatchDate;
	}

	public void setTicketMatchDate(ZonedDateTime ticketMatchDate) {
		this.ticketMatchDate = ticketMatchDate;
	}

	public BigDecimal getTicketAvailableQtt() {
		return ticketAvailableQtt;
	}

	public void setTicketAvailableQtt(BigDecimal ticketAvailableQtt) {
		this.ticketAvailableQtt = ticketAvailableQtt;
	}

	public BigDecimal getTicketTotalQtt() {
		return ticketTotalQtt;
	}

	public void setTicketTotalQtt(BigDecimal ticketTotalQtt) {
		this.ticketTotalQtt = ticketTotalQtt;
	}

	public Long getTicketHomeTeamId() {
		return ticketHomeTeamId;
	}

	public void setTicketHomeTeamId(Long ticketHomeTeamId) {
		this.ticketHomeTeamId = ticketHomeTeamId;
	}

	public Long getTicketAwayTeamId() {
		return ticketAwayTeamId;
	}

	public void setTicketAwayTeamId(Long ticketAwayTeamId) {
		this.ticketAwayTeamId = ticketAwayTeamId;
	}

	public Long getTicketMatchGroupId() {
		return ticketMatchGroupId;
	}

	public void setTicketMatchGroupId(Long ticketMatchGroupId) {
		this.ticketMatchGroupId = ticketMatchGroupId;
	}

	public Long getTicketPhaseId() {
		return ticketPhaseId;
	}

	public void setTicketPhaseId(Long ticketPhaseId) {
		this.ticketPhaseId = ticketPhaseId;
	}

	public String getTicketHomeTeamName() {
		return ticketHomeTeamName;
	}

	public void setTicketHomeTeamName(String ticketHomeTeamName) {
		this.ticketHomeTeamName = ticketHomeTeamName;
	}

	public String getTicketAwayTeamName() {
		return ticketAwayTeamName;
	}

	public void setTicketAwayTeamName(String ticketAwayTeamName) {
		this.ticketAwayTeamName = ticketAwayTeamName;
	}

	public String getTicketPhaseName() {
		return ticketPhaseName;
	}

	public void setTicketPhaseName(String ticketPhaseName) {
		this.ticketPhaseName = ticketPhaseName;
	}

	public String getTicketMatchGroupName() {
		return ticketMatchGroupName;
	}

	public void setTicketMatchGroupName(String ticketMatchGroupName) {
		this.ticketMatchGroupName = ticketMatchGroupName;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemDTO itemDTO = (ItemDTO) o;

        if ( ! Objects.equals(id, itemDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ItemDTO{" +
            "id=" + id +
            ", quantity='" + quantity + "'" +
            '}';
    }
}

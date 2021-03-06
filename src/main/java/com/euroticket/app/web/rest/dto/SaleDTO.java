package com.euroticket.app.web.rest.dto;

import java.time.ZonedDateTime;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Sale entity.
 */
public class SaleDTO implements Serializable {

    private Long id;

    private ZonedDateTime saleDate;


    private Long saleStatusId;  
    private String saleStatusName;
    private PaymentDTO payment;
    private Long userId;
    private List<ItemDTO> items;
    
    
    public String getSaleStatusName() {
		return saleStatusName;
	}

	public void setSaleStatusName(String saleStatusName) {
		this.saleStatusName = saleStatusName;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
    public List<ItemDTO> getItems() {
		return items;
	}
    
    
	public PaymentDTO getPayment() {
		return payment;
	}

	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
	}

	public void setItems(List<ItemDTO> items) {
		this.items = items;
	}

	public ZonedDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(ZonedDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public Long getSaleStatusId() {
        return saleStatusId;
    }

    public void setSaleStatusId(Long saleStatusId) {
        this.saleStatusId = saleStatusId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SaleDTO saleDTO = (SaleDTO) o;

        if ( ! Objects.equals(id, saleDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SaleDTO{" +
            "id=" + id +
            ", saleDate='" + saleDate + "'" +
            '}';
    }
}

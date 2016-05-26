package com.euroticket.app.service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.euroticket.app.config.Constants;
import com.euroticket.app.domain.Item;
import com.euroticket.app.domain.Payment;
import com.euroticket.app.domain.Sale;
import com.euroticket.app.domain.SaleStatus;
import com.euroticket.app.domain.Team;
import com.euroticket.app.domain.Ticket;
import com.euroticket.app.domain.User;
import com.euroticket.app.repository.ItemRepository;
import com.euroticket.app.repository.PaymentRepository;
import com.euroticket.app.repository.SaleRepository;
import com.euroticket.app.repository.SaleStatusRepository;
import com.euroticket.app.repository.TicketRepository;
import com.euroticket.app.repository.UserRepository;
import com.euroticket.app.repository.search.TeamSearchRepository;
import com.euroticket.app.security.SecurityUtils;
import com.euroticket.app.web.rest.dto.SaleDTO;
import com.euroticket.app.web.rest.dto.TeamDTO;
import com.euroticket.app.web.rest.mapper.ItemMapper;
import com.euroticket.app.web.rest.mapper.PaymentMapper;
import com.euroticket.app.web.rest.mapper.SaleMapper;

@Service
@Transactional
public class SaleService {
	 private final Logger log = LoggerFactory.getLogger(SaleService.class);
	 
	    @Inject
	    private UserRepository userRepository;

	   @Inject
	    private SaleMapper saleMapper;

	   @Inject
	    private PaymentMapper paymentMapper;
	   
	   @Inject
	    private ItemMapper itemMapper;
	    
	    @Inject
	    private PaymentRepository paymentRepository;
	    
	    @Inject
	    private SaleRepository saleRepository;
	    
	    @Inject
	    private TicketRepository ticketRepository;
	    
	    @Inject
	    private ItemRepository itemRepository;

	    @Inject
	    private SaleStatusRepository saleStatusRepository;
	 
	 
	 /**
	     * Save a team.
	     * 
	     * @param teamDTO the entity to save
	     * @return the persisted entity
	     */
	    public SaleDTO save(SaleDTO saleDTO) {
	        log.debug("Request to save Sale : {}", saleDTO);
	        
	        User user = userRepository.findOneByLogin( SecurityUtils.getCurrentUserLogin()).get();
	        SaleStatus saleStatus= saleStatusRepository.getOne(Constants.SALE_STATUS_PAID_ID);

	        Sale sale = saleMapper.saleDTOToSale(saleDTO);
	        sale.setSaleDate(ZonedDateTime.now(ZoneId.systemDefault()));
	        sale.setSaleStatus(saleStatus);
	        sale.setUser(user);
	        BigDecimal  purchasedTickets = new BigDecimal("0");
	     
	        Payment payment = paymentMapper.paymentDTOToPayment(saleDTO.getPayment());
	        payment = paymentRepository.save(payment);
	        sale.setPayment(payment);
	        sale = saleRepository.save(sale);
	        
	        List<Item> items = itemMapper.itemDTOsToItems(saleDTO.getItems());
	        for(Item item : items) {
	        	Long ticketId = item.getTicket().getId();
	        	Ticket ticketToUpdate = ticketRepository.findOne(ticketId);
	        	purchasedTickets = purchasedTickets.add(new BigDecimal(item.getQuantity()));
	        	if(ticketToUpdate != null) {
	        		ticketToUpdate.setAvailableQtt(ticketToUpdate.getAvailableQtt().subtract(purchasedTickets));
		        	ticketRepository.save(ticketToUpdate);
	        	}
	        	
	        	item.setSale(sale);
	        }
	        user.setPurchasedTickets(user.getPurchasedTickets().add(purchasedTickets));
	        itemRepository.save(items);
	        userRepository.save(user);
	        SaleDTO result = saleMapper.saleToSaleDTO(sale);
	   
	        return result;
	        
	    }


	public SaleDTO get(Long id) {
		SaleDTO result = null;
		try {
			  Sale sale = saleRepository.findUserSaleById(id);
		      result = saleMapper.saleToSaleDTO(sale);
		} catch(Exception e) {
			
		}
      
		return result;
	}
}

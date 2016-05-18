package com.euroticket.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.euroticket.app.domain.Item;
import com.euroticket.app.repository.ItemRepository;
import com.euroticket.app.repository.search.ItemSearchRepository;
import com.euroticket.app.web.rest.util.HeaderUtil;
import com.euroticket.app.web.rest.dto.ItemDTO;
import com.euroticket.app.web.rest.mapper.ItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Item.
 */
@RestController
@RequestMapping("/api")
public class ItemResource {

    private final Logger log = LoggerFactory.getLogger(ItemResource.class);
        
    @Inject
    private ItemRepository itemRepository;
    
    @Inject
    private ItemMapper itemMapper;
    
    @Inject
    private ItemSearchRepository itemSearchRepository;
    
    /**
     * POST  /items : Create a new item.
     *
     * @param itemDTO the itemDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new itemDTO, or with status 400 (Bad Request) if the item has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/items",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) throws URISyntaxException {
        log.debug("REST request to save Item : {}", itemDTO);
        if (itemDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("item", "idexists", "A new item cannot already have an ID")).body(null);
        }
        Item item = itemMapper.itemDTOToItem(itemDTO);
        item = itemRepository.save(item);
        ItemDTO result = itemMapper.itemToItemDTO(item);
        itemSearchRepository.save(item);
        return ResponseEntity.created(new URI("/api/items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("item", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /items : Updates an existing item.
     *
     * @param itemDTO the itemDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated itemDTO,
     * or with status 400 (Bad Request) if the itemDTO is not valid,
     * or with status 500 (Internal Server Error) if the itemDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/items",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ItemDTO> updateItem(@RequestBody ItemDTO itemDTO) throws URISyntaxException {
        log.debug("REST request to update Item : {}", itemDTO);
        if (itemDTO.getId() == null) {
            return createItem(itemDTO);
        }
        Item item = itemMapper.itemDTOToItem(itemDTO);
        item = itemRepository.save(item);
        ItemDTO result = itemMapper.itemToItemDTO(item);
        itemSearchRepository.save(item);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("item", itemDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /items : get all the items.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of items in body
     */
    @RequestMapping(value = "/items",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<ItemDTO> getAllItems() {
        log.debug("REST request to get all Items");
        List<Item> items = itemRepository.findAll();
        return itemMapper.itemsToItemDTOs(items);
    }

    /**
     * GET  /items/:id : get the "id" item.
     *
     * @param id the id of the itemDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the itemDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/items/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ItemDTO> getItem(@PathVariable Long id) {
        log.debug("REST request to get Item : {}", id);
        Item item = itemRepository.findOne(id);
        ItemDTO itemDTO = itemMapper.itemToItemDTO(item);
        return Optional.ofNullable(itemDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /items/:id : delete the "id" item.
     *
     * @param id the id of the itemDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/items/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        log.debug("REST request to delete Item : {}", id);
        itemRepository.delete(id);
        itemSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("item", id.toString())).build();
    }

    /**
     * SEARCH  /_search/items?query=:query : search for the item corresponding
     * to the query.
     *
     * @param query the query of the item search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/items",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<ItemDTO> searchItems(@RequestParam String query) {
        log.debug("REST request to search Items for query {}", query);
        return StreamSupport
            .stream(itemSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(itemMapper::itemToItemDTO)
            .collect(Collectors.toList());
    }

}

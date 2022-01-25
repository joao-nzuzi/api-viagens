package com.joao.nzuzi.travel_demo.controller;

import com.joao.nzuzi.travel_demo.model.Travel;
import com.joao.nzuzi.travel_demo.service.TravelService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api-travels/travels")
public class TravelController {
    private static final Logger logger = Logger.getLogger(String.valueOf(TravelController.class));

    @Autowired
    private TravelService travelService;

    @GetMapping
    public ResponseEntity<List<Travel>> find() {
        if(travelService.find().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        logger.info((Supplier<String>) travelService.find());
        return ResponseEntity.ok(travelService.find());
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete() {
        try {
            travelService.delete();
            return ResponseEntity.noContent().build();
        }catch(Exception e) {
//            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Travel> create(@RequestBody JSONObject travel) {
        try {
            if(travelService.isJSONValid(travel.toString())) {
                Travel travelCreated = travelService.create(travel);
                URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path(travelCreated.getOrderNumber()).build().toUri();

                if(travelService.isStartDateGreaterThanEndDate(travelCreated)){
//                    System.out.println("The start date is greater than end date.");
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
                }else {
                    travelService.add(travelCreated);
                    return ResponseEntity.created(uri).body(null);
                }
            }else {
                return ResponseEntity.badRequest().body(null);
            }
        }catch(Exception e) {
//            System.out.println("JSON fields are not parsable. " + e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }

    @PutMapping(path = "/{id}", produces = { "application/json" })
    public ResponseEntity<Travel> update(@PathVariable("id") long id, @RequestBody JSONObject travel) {
        Travel travelToUpdate;
        try {
            if(travelService.isJSONValid(travel.toString())) {
                travelToUpdate = travelService.findById(id);
                if(travelToUpdate == null){
                    System.out.println("Travel not found.");
                    return ResponseEntity.notFound().build();
                }else {
                    travelToUpdate = travelService.update(travelToUpdate, travel);
                    return ResponseEntity.ok(travelToUpdate);
                }
            }else {
                return ResponseEntity.badRequest().body(null);
            }
        }catch(Exception e) {
            System.out.println("JSON fields are not parsable." + e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }
}

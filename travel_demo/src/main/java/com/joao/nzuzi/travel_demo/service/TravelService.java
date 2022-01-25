package com.joao.nzuzi.travel_demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.joao.nzuzi.travel_demo.model.Travel;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelService {
    private TravelFactory factory;
    private List<Travel> travels;

    public void createTravelFactory(){
        if(factory == null){
            factory = new TravelFactoryImpl();
        }
    }

    public void createTravelList(){
        if (factory == null){
            travels = new ArrayList<>();
        }
    }

    public  boolean isJSONValid(String jsonInString){
        try{
            return new ObjectMapper().readTree(jsonInString) != null;
        }catch (IOException e){
            return false;
        }
    }

    private long parseId(JSONObject travel) throws JSONException {
        return Long.valueOf((int) travel.get("id"));
    }

    private BigDecimal parseAmount(JSONObject travel) throws JSONException {
        return BigDecimal.valueOf(Long.parseLong((String) travel.get("amount")));
    }

    private LocalDateTime parseStartDate(JSONObject travel) throws JSONException {
        String startDate = (String) travel.get("startDate");
        return ZonedDateTime.parse(startDate).toLocalDateTime();
    }

    private LocalDateTime parseEndDate(JSONObject travel) throws JSONException {
        String endDate = (String) travel.get("endDate");
        return ZonedDateTime.parse(endDate).toLocalDateTime();
    }

    public boolean isStartDateGreaterThanEndDate(Travel travel){
        if(travel.getEndDate() == null) return false;
        return travel.getStartDate().isAfter(travel.getEndDate());
    }

    private void setTravelValues(JSONObject jsonTravel, Travel travel) throws JSONException {

        String orderNumber = (String) jsonTravel.get("orderNumber");

        String type = (String) jsonTravel.get("type");

        travel.setOrderNumber(orderNumber != null ? orderNumber : travel.getOrderNumber());
        travel.setAmount(jsonTravel.get("amount") != null ? parseAmount(jsonTravel) : travel.getAmount());
        travel.setStartDate(jsonTravel.get("initialDate") != null ? parseStartDate(jsonTravel) : travel.getStartDate());
        travel.setEndDate(jsonTravel.get("finalDate") != null ? parseEndDate(jsonTravel) : travel.getEndDate());
        travel.setType(type != null ? TravelTypeEnum.getEnum(type) : travel.getType());
    }

    public Travel create(JSONObject jsonTravel) throws JSONException {

        createFactory();

        Travel travel = factory.createTravel((String) jsonTravel.get("type"));
        travel.setId(parseId(jsonTravel));
        setTravelValues(jsonTravel, travel);

        return travel;
    }

    public Travel update(Travel travel, JSONObject jsonTravel) {

        setTravelValues(jsonTravel, travel);
        return travel;
    }

    public void add(Travel travel) {
        createTravelList();
        travels.add(travel);
    }
    public List<Travel> find() {
        createTravelList();
        return travels;
    }
    public Travel findById(long id) {
        return travels.stream().filter(t -> id == t.getId()).collect(Collectors.toList()).get(0);
    }

    public void delete() {
        travels.clear();
    }

    public void clearObjects() {
        travels = null;
        factory = null;
    }
}

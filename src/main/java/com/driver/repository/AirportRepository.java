package com.driver.repository;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AirportRepository {
    HashMap<String, Airport> storeAirport = new HashMap<>();
    //mapping of airport name to airport

    HashMap<String, List<Flight>> storeFlightoAirport= new HashMap<>();
    //mapping of airport name to list of flight

    HashMap<Integer,Flight> storeFlight = new HashMap<>();
    // mapping of flight id to flight

    HashMap<Integer , List<Passenger>> storeFlightToPassenger = new HashMap<>();
    // map of flight id to passenger

    HashMap<Integer, Passenger> storePassenger = new HashMap<>();
    // store passenger id to passenger

    final int intial_price = 3000;
    //intital price = 3000

    public void addAirport(Airport airport) {
        storeAirport.put(airport.getAirportName(),airport);
    }

    public String getLargestAirportName() {
        //tick
        int maxTerminal = 0;
        String name = null;
        for(Airport airport : storeAirport.values()){
            if(airport.getNoOfTerminals() >= maxTerminal){
                maxTerminal = airport.getNoOfTerminals();
                if(name == null || airport.getAirportName().compareTo(name) < 0) {
                    name = airport.getAirportName();
                }
            }
        }
        return name;
    }


    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        //tick
        double duration = 0.0;
        for(Flight flight : storeFlight.values()){
            if(flight.getFromCity() == fromCity && flight.getToCity() == toCity){
                duration = flight.getDuration();
            }
        }
        return duration == 0.0 ? -1.0 : duration;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        // logic is wrong not implemented the flight comming to this airport at given time
//        This includes both the people who have come for a flight and who have landed on an airport after their flight
        int total_no_of_people = 0;

        if(!storeFlightoAirport.containsKey(airportName))return total_no_of_people;

        Airport airport = storeAirport.get(airportName);

        for(Flight flight : storeFlightoAirport.get(airportName)){
            if(flight.getFlightDate().equals(date) && flight.getFromCity().equals(airport.getCity()))
                if(storeFlightToPassenger.containsKey(flight.getFlightId())){
                    int size_of_flight = storeFlightToPassenger.get(flight.getFlightId()).size();
                    total_no_of_people += size_of_flight ;
                }
        }

        return total_no_of_people;
    }


    public void addFlight(Flight flight) {
        //change the logic
        // map with the city
        //from_city to to_city
        storeFlight.put(flight.getFlightId(),flight);
        // now map the flight with the airport using city
        City city = flight.getFromCity();
        List<Flight> curr_flight = new ArrayList<>();
        for(Airport airport : storeAirport.values()){
            if(airport.getCity() == city){
                if(storeFlightoAirport.containsKey(airport.getAirportName())){
                    curr_flight = storeFlightoAirport.get(airport.getAirportName());
                }
                curr_flight.add(flight);
                storeFlightoAirport.put(airport.getAirportName(),curr_flight);
            }
        }
    }

    public void addPassenger(Passenger passenger) {
        storePassenger.put(passenger.getPassengerId(),passenger);
    }

    public int calculateFlightFare(Integer flightId) {
        //tick
        if(storeFlight.containsKey(flightId)){
            List<Passenger> curr_pass = storeFlightToPassenger.get(flightId);
            if(curr_pass == null)return intial_price;
            int curr_price = intial_price + (curr_pass.size())*50;
            return curr_price;
        }
        return 0;
    }


    public boolean bookATicket(Integer flightId, Integer passengerId) {
        //current checking
        Flight curr_flight = new Flight();
        if(storeFlight.containsKey(flightId)){
            curr_flight = storeFlight.get(flightId);
        }

        List<Passenger> curr_total_passenger = new ArrayList<>();
        if(storeFlightToPassenger.containsKey(flightId)){
            curr_total_passenger = storeFlightToPassenger.get(flightId);
        }

        int max_capacity_of_flight = curr_flight.getMaxCapacity();
        int curr_capacity = curr_total_passenger.size();

        for(Passenger curr_passenger : curr_total_passenger){
            //checking the passenger present on the flight or not
            if(curr_passenger.getPassengerId() == passengerId){
                return false;
            }
        }

        //checking the max capacity
        if(max_capacity_of_flight <= curr_capacity){
            return false;
        }

        // adding passenger
        Passenger passenger = storePassenger.get(passengerId);
        List<Passenger> curr_pass = new ArrayList<>();
        if(storeFlightToPassenger.containsKey(flightId)){
            curr_pass = storeFlightToPassenger.get(flightId);
        }
        curr_pass.add(passenger);
        storeFlightToPassenger.put(flightId,curr_pass);
        return true;
    }

    public boolean cancel_output(Integer flightId, Integer passengerId) {
        if(!storeFlight.containsKey(flightId)){
            return false;
        }


        // paasenger book or not
        List<Passenger> total_passenger = storeFlightToPassenger.get(flightId);

        for(Passenger passenger : total_passenger){
            if(passenger.getPassengerId() == passengerId){
                total_passenger.remove(passenger);
                return true;
            }
        }

        return false;
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        // count total number of flight booked by passenger
        int cnt = 0;
        for(List<Passenger> passengerList : storeFlightToPassenger.values()){
            for(Passenger passenger : passengerList){
                if(passenger.getPassengerId() == passengerId){
                    cnt++;
                }
            }
        }
        return cnt;
    }


    public String getAirportNameFromFlightId(Integer flightId) {
        // taking out staring city
        // cheking for valid flight id
        if(!storeFlight.containsKey(flightId)){
            return null;
        }

        City city = storeFlight.get(flightId).getFromCity();

        for(Airport airport : storeAirport.values()){
            if(airport.getCity() == city){
                return airport.getAirportName();
            }
        }
        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int total_revenue = 0;
        if(!storeFlightToPassenger.containsKey(flightId))return total_revenue;

        int total_boooking = storeFlightToPassenger.get(flightId).size();

        for(int pass = 0 ; pass < total_boooking ; pass++){
            total_revenue += (intial_price + (pass) * 50);
        }
        return total_revenue;
    }
}

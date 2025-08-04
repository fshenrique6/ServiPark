package com.parkinglot.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.parkinglot.model.Vehicle;

public class VehicleDAO {

    private static VehicleDAO instance;

    // Using HashMap to store vehicles with plate as key
    private Map<String, Vehicle> vehicles;

    private VehicleDAO(){
        vehicles = new HashMap<>();
        // Sample data
    }

    public static synchronized VehicleDAO getInstance(){
        if (instance == null) {
            instance = new VehicleDAO();
        }

        return instance;
    }

    public boolean addVehicle(Vehicle vehicle){
        if (vehicle.getPlate() == null || vehicle.getPlate().trim().isEmpty()) {
            return false;
        }
        vehicles.put(vehicle.getPlate(), vehicle);
        return true;
    }

    public List<Vehicle> getAllVehicles(){
        return new ArrayList<>(vehicles.values());
    } 

    public Vehicle getVehicle(String plate){
        return vehicles.get(plate);
    }

    public boolean updateVehicle(Vehicle vehicle){
        if (vehicle.getPlate() == null || !vehicles.containsKey(vehicle.getPlate())) {
            return false;
        }
        vehicles.put(vehicle.getPlate(), vehicle);
        return true;
    }

    public boolean deleteVehicle(String plate){
        if (plate == null || !vehicles.containsKey(plate)) {
            return false;
        }
        vehicles.remove(plate);
        return true;
    }
}

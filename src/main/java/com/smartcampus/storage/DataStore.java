/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.storage;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static DataStore instance;
    
    // Thread-safe collections
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    private DataStore() {
        initializeSampleData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Add sample rooms
        Room room1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room room2 = new Room("LAB-101", "Computer Science Lab", 30);
        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);

        // Add sample sensors
        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001", "CO2", "ACTIVE", 450.0, "LIB-301");
        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);
        
        // Link sensors to rooms
        room1.addSensorId("TEMP-001");
        room1.addSensorId("CO2-001");

        // Initialize reading lists
        sensorReadings.put("TEMP-001", new ArrayList<>());
        sensorReadings.put("CO2-001", new ArrayList<>());
    }

    // Room operations
    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public void deleteRoom(String id) {
        rooms.remove(id);
    }

    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    // Sensor operations
    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        sensorReadings.putIfAbsent(sensor.getId(), new ArrayList<>());
        
        // Add sensor ID to room
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.addSensorId(sensor.getId());
        }
    }

    public void deleteSensor(String id) {
        Sensor sensor = sensors.get(id);
        if (sensor != null) {
            // Remove from room
            Room room = rooms.get(sensor.getRoomId());
            if (room != null) {
                room.removeSensorId(id);
            }
            sensors.remove(id);
            sensorReadings.remove(id);
        }
    }

    public boolean sensorExists(String id) {
        return sensors.containsKey(id);
    }

    // Sensor Reading operations
    public List<SensorReading> getReadings(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }
}
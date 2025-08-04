package com.parkinglot;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.parkinglot.dao.VehicleDAO;
import com.parkinglot.model.Vehicle;

@WebServlet("/ServiPark")
public class ServiPark extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VehicleDAO vehicleDAO;

	public ServiPark() {
		super();
		vehicleDAO = VehicleDAO.getInstance();
	}

	// convert vehicle to JSON format
	private String toJson(Vehicle vehicle) {
		if (vehicle == null) {
			return "null";
		}
		
		StringBuilder json = new StringBuilder();
		json.append("{");
        json.append("\"plate\":\"").append(vehicle.getPlate()).append("\",");
        json.append("\"model\":\"").append(vehicle.getModel()).append("\",");
        json.append("\"year\":").append(vehicle.getYear()).append(",");
        json.append("\"ownerName\":\"").append(vehicle.getOwnerName()).append("\"");
        json.append("}");
		
		return json.toString();
	}
}

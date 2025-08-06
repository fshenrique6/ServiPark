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


		private String toJson(List<Vehicle> vehicles) {
        	if (vehicles == null) {
            	return "[]";
        	}
        
        	StringBuilder json = new StringBuilder();
        	json.append("[");
        
        	for (int i = 0; i < vehicles.size(); i++) {
            	json.append(toJson(vehicles.get(i)));
            	if (i < vehicles.size() - 1) {
                	json.append(",");
            	}
        	}
        
        	json.append("]");
        	return json.toString();
		}


		private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }
        return buffer.toString();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		
		// get all vehicles
		if (pathInfo == null || pathInfo.equals("/")) {
			List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
			out.print(toJson(vehicles));
		
		// get vehicle by plate
		} else {
			String plate = pathInfo.substring(1);
			Vehicle vehicle = vehicleDAO.getVehicle(plate);

			if (vehicle == null) {
				out.print(toJson(vehicle));
			} else{
				response.setStatus(404); // Not Found
				out.print("{\"error\":\"Vehicle not found\"}");
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		
		try{
			System.out.println("===== BEGIN DEBUG POST =====");

			// Read the request body
			String jsonData = readRequestBody(request);
			System.out.println("Json data received: " + jsonData);

			// Check if json is empty
			if (jsonData == null || jsonData.trim().isEmpty()) {
				response.setStatus(400);
				out.print("{\"success\": false, \"message\": \"JSON data empty\"}");
				return;
			}

			// Create vehicle and extract data manually
			Vehicle vehicle = new Vehicle();

			// Manual parse JSON
			String plate = extractStringField(jsonData, "plate");
			String model = extractStringField(jsonData, "model");
			int year = extractIntField(jsonData, "year");
			String ownerName = extractStringField(jsonData, "ownerName");

			vehicle.setPlate(plate);
			vehicle.setModel(model);
			vehicle.setYear(year);
			vehicle.setOwnerName(ownerName);

			// Data validation
			if (vehicle.getPlate() == null || vehicle.getPlate().trim().isEmpty()) {
				response.setStatus(400);
				out.print("{\"success\": false, \"message\": \"Plate is required\"}");
				return;

			}

			if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
				response.setStatus(400);
				out.print("{\"success\": false, \"message\": \"Model is required\"}");
				return;
				
			}

			if (vehicle.getYear() <= 0) {
				response.setStatus(400);
				out.print("{\"success\": false, \"message\": \"Year must be a valid number\"}");
				return;
				
			}

			if (vehicle.getOwnerName() == null || vehicle.getOwnerName().trim().isEmpty()) {
				response.setStatus(400);
				out.print("{\"success\": false, \"message\": \"Owner name is required\"}");
				return;
				
			}

			System.out.println("Vehicle validated successfully: " + vehicle.getPlate() + ", " + vehicle.getModel());

			// create and add new vehicle
			boolean success = vehicleDAO.addVehicle(vehicle);
			if (success) {
				response.setStatus(201); // Created
				out.print("{\"success\": true, \"message\": \"Vehicle added successfully\", \"vehicle\": " + toJson(vehicle) + "}");
			} else {
				response.setStatus(500); // Internal Server Error
				out.print("{\"success\": false, \"message\": \"Failed to add vehicle\"}");
		}

			System.out.println("===== END DEBUG POST =====");

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500); // Internal Server Error
			out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
		} 
		
	}

	private String extractStringField(String jsonData, String field) {
		String completeField = "\"" + field + "\":\"";
		int beginIndex = jsonData.indexOf(completeField);
		if (beginIndex < 0) return null;

		beginIndex = jsonData.indexOf(":", beginIndex) + 1;
		// skip leading spaces
		while (beginIndex < jsonData.length() && jsonData.charAt(beginIndex) == ' ' || jsonData.charAt(beginIndex) == '\t') {
			beginIndex++;
		}

		if(jsonData.charAt(beginIndex) == '"'){
			beginIndex++;
			int endIndex = jsonData.indexOf("\"", beginIndex);
			if (endIndex > beginIndex) {
				return jsonData.substring(beginIndex, endIndex);
			}
		}

		return null;
	}

	private int extractIntField(String jsonData, String field) {
		String completeField = "\"" + field + "\":";
		int beginIndex = jsonData.indexOf(completeField);
		if (beginIndex < 0) return 0;

		beginIndex = jsonData.indexOf(":", beginIndex) + 1;
		// skip leading spaces
		while (beginIndex < jsonData.length() && (jsonData.charAt(beginIndex) == ' ' || jsonData.charAt(beginIndex) == '\t')) {
			beginIndex++;
		}


		//find the end of the number
		int endIndex = beginIndex;
		while (endIndex < jsonData.length() && (Character.isDigit(jsonData.charAt(endIndex)) || jsonData.charAt(endIndex) == '.')) {
			endIndex++;
		}

		if (endIndex > beginIndex) {
			try {
				return Integer.parseInt(jsonData.substring(beginIndex, endIndex));
			} catch (NumberFormatException e) {
				System.out.println("Error while converting to int: " + jsonData.substring(beginIndex, endIndex));
			}
		}

		return 0;
	}

		@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		
		if (pathInfo != null && !pathInfo.equals("/")) {
			try {
				// Get plate from URL path
				String plate = pathInfo.substring(1);
				System.out.println("===== BEGIN DEBUG PUT =====");
				
				// Read JSON request body
				String jsonData = readRequestBody(request);
				System.out.println("Received JSON data (PUT): " + jsonData);
				
				// Check if JSON is empty
				if (jsonData == null || jsonData.trim().isEmpty()) {
					response.setStatus(400);
					out.print("{\"success\": false, \"message\": \"Empty JSON data\"}");
					return;
				}
				
				try {
					// Create a Vehicle instance and set the plate
					Vehicle vehicle = new Vehicle();
					vehicle.setPlate(plate);
					
					// Parse JSON manually
					if (jsonData.contains("\"model\"")) {
						String model = extractStringField(jsonData, "model");
						vehicle.setModel(model);
					}
					
					if (jsonData.contains("\"year\"")) {
						int year = extractIntField(jsonData, "year");
						vehicle.setYear(year);
					}
					
					if (jsonData.contains("\"ownerName\"")) {
						String ownerName = extractStringField(jsonData, "ownerName");
						vehicle.setOwnerName(ownerName);
					}
					
					System.out.println("Updating vehicle: " + vehicle.getPlate() + ", " + vehicle.getModel() + 
					                   ", " + vehicle.getYear() + ", " + vehicle.getOwnerName());
					
					// Validação dos dados
					if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
						response.setStatus(400);
						out.print("{\"success\": false, \"message\": \"Modelo is required\"}");
						return;
					}
					
					if (vehicle.getYear() <= 0) {
						response.setStatus(400);
						out.print("{\"success\": false, \"message\": \"Year must be a valid number\"}");
						return;
					}
					
					if (vehicle.getOwnerName() == null || vehicle.getOwnerName().trim().isEmpty()) {
						response.setStatus(400);
						out.print("{\"success\": false, \"message\": \"Owner name is required\"}");
						return;
					}
					
					// Update vehicle
					boolean success = vehicleDAO.updateVehicle(vehicle);
					
					if (success) {
						out.print("{\"success\": true, \"message\": \"Vehicle updated successfully\"}");
					} else {
						response.setStatus(404); // NOT_FOUND
						out.print("{\"success\": false, \"message\": \"Vehicle not found\"}");
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("ERROR PROCESSING JSON PUT: " + e.getMessage());
					response.setStatus(400);
					out.print("{\"success\": false, \"message\": \"Error while processing JSON: " + e.getMessage() + "\"}");
				}
				
				System.out.println("===== END DEBUG PUT =====");
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("GENERAL ERROR IN PUT: " + e.getMessage());
				response.setStatus(400);
				out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			}
		} else {
			response.setStatus(400); // BAD_REQUEST
			out.print("{\"success\": false, \"message\": \"Vehicle plate not provided\"}");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		if (pathInfo != null && !pathInfo.equals("/")) {
			String plate = pathInfo.substring(1);
			boolean success = vehicleDAO.deleteVehicle(plate);
			
			PrintWriter out = response.getWriter();
			if (success) {
				out.print("{\"success\": true, \"message\": \"Vehicle deleted successfully\"}");
			} else {
				response.setStatus(404); // NOT_FOUND
				out.print("{\"success\": false, \"message\": \"Vehicle not found\"}");
			}
		} else {
			response.setStatus(400); // BAD_REQUEST
			response.getWriter().print("{\"success\": false, \"message\": \"Vehicle plate not provided\"}");
		}
	}

}

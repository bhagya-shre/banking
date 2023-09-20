package Infinite.parking;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

public class ParkingMain {
    private static List<String> history = new ArrayList<>();

    public static void main(String[] args) {
    	  SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        parkingDAO parkingService = new parkingDAO(sessionFactory);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Park Vehicle");
            System.out.println("2. Exit Parking");
            System.out.println("3. Get Parking History");
            System.out.println("4. Get Vehicle Count by Type");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            String choicee = scanner.nextLine();
            if (!choicee.matches("\\d+")) {
                System.out.println("Invalid input choice. Please enter a number.");
                continue;
            }
           int choice = Integer.parseInt(choicee);

           // scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                	
                    System.out.print("Enter the Vehicle Number: ");
                    String vehicleNumber = scanner.nextLine();
                    System.out.print("Enter the Vehicle Type (2wheeler/4wheeler/truck): ");
                    String vehicleType = scanner.nextLine();
                    
                    parkingService.parkVehicle(vehicleNumber, vehicleType);
                    history.add("Parked vehicle: " + vehicleNumber + ", Type: " + vehicleType);
                	break;
                case 2:
                    System.out.print("Enter the Vehicle Number: ");
                    vehicleNumber = scanner.nextLine();
                    parkingService.exitParking(vehicleNumber);
                    history.add("Exited vehicle: " + vehicleNumber);
                    break;
                case 3:
                    System.out.print("Enter the Date (YYYY-MM-DD): ");
                    String dateStr = scanner.nextLine();
                    LocalDate date = LocalDate.parse(dateStr);
                    parkingService.getParkingHistory(date);
                    break;
                case 4:
                    System.out.print("Enter the Vehicle Type (2wheeler/4wheeler/truck): ");
                    vehicleType = scanner.nextLine();
                    parkingService.getVehicleCountByType(vehicleType);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    //printHistory();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        
        
      

    }

    }  
}



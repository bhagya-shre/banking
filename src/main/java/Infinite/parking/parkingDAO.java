package Infinite.parking;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import Infinite.parking.Parking;
import Infinite.parking.ParkingFee;

public class parkingDAO {
    private SessionFactory sessionFactory;
    Parking parking = new Parking();
    ParkingFee parkingfee = new ParkingFee();

    public parkingDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    private Timestamp localDateTimeToSQL(LocalDateTime localDateTime){
    	Timestamp timestamp = Timestamp.valueOf(localDateTime);
    	return timestamp;
    }
    
    //method to park vehicle..
    public void parkVehicle(String vehicleNumber, String vehicleType) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        LocalDateTime entryTime = currentTimestamp.toLocalDateTime();
        
        try {
            Query query = session.createQuery("FROM Parking WHERE vehicleNumber = :vehicleNumber");
            query.setParameter("vehicleNumber", vehicleNumber);
            List<Parking> existingVehicleNumber = query.list();
            if (!existingVehicleNumber.isEmpty()) {
                System.out.println("Vehicle Number " + vehicleNumber + " already exists");
                session.close();
                return;
            }

        Parking parking = new Parking();
        parking.setVehicleNumber(vehicleNumber);
        parking.setVehicleType(vehicleType);
       
        parking.setEntryTime(localDateTimeToSQL(entryTime));


        session.save(parking);
        session.getTransaction().commit();
        session.close();

        System.out.println("Vehicle parked successfully. Parking ID: " + parking.getId());
        addToHistory("Parked vehicle: " + vehicleNumber + ", Type: " + vehicleType);
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.out.println("Error occurred while parking the vehicle: " + e.getMessage());
        }
    }

    public void exitParking(String vehicleNumber) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        LocalDateTime exitTime = currentTimestamp.toLocalDateTime();

        Query query = session.createQuery
     ("SELECT p FROM Parking p WHERE p.vehicleNumber = :vehicleNumber AND p.exitTime IS NULL");
        query.setParameter("vehicleNumber", vehicleNumber);
        Parking parking = (Parking) query.uniqueResult();
        
        if (parking != null) {
            parking.setExitTime(localDateTimeToSQL(exitTime));

            double fee = calculateFee(parking.getVehicleType(), parking.getEntryTime(), parking.getExitTime());
            parkingfee.setFee(fee);

            session.update(parking);
            session.getTransaction().commit();
            session.close();

            System.out.println("Vehicle exited successfully. Vehicle Number: " + vehicleNumber);
            System.out.println();
            System.out.println("Receipt:");
            System.out.println("----------");
            

            System.out.printf("%-15s %-20s%n", "Vehicle Number:", parking.getVehicleNumber());
            System.out.printf("%-15s %-20s%n", "Entry Time:", parking.getEntryTime());
            System.out.printf("%-15s %-20s%n", "Exit Time:", parking.getExitTime());
            System.out.printf("%-15s %-20s%n", "Fee:", parkingfee.getFee());
            
            System.out.println();
            addToHistory("Exited vehicle: " + vehicleNumber);
        } else {
            session.getTransaction().rollback();
            session.close();
            System.out.println("Vehicle with number " + vehicleNumber + " not found in the parking lot.");
        }
    }

    
    public void getParkingHistory(LocalDate date) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Query query = session.createQuery("FROM Parking WHERE entryTime >= :startOfDay AND entryTime < :endOfDay");
        query.setParameter("startOfDay", localDateTimeToSQL(startOfDay));
        query.setParameter("endOfDay", localDateTimeToSQL(endOfDay));

        List<Parking> parkingList = query.list();

        System.out.println("Parking History for " + date + ":");
        System.out.println();
        if (parkingList.isEmpty()) {
            System.out.println("No parking history found for the given date.");
        }else{
            System.out.printf("%-15s %-15s %-15s %-25s %-25s%n","Vehicle Id","Vehicle Type", "Vehicle Number", "Entry Time", "Exit Time");
          for (Parking parking : parkingList) {

        	  System.out.println("--------------------------------------------------------------------------------------------");
        	  System.out.printf("%-15s %-15s %-15s %-25s %-25s%n",parking.getId(),parking.getVehicleType(),
                      parking.getVehicleNumber(), parking.getEntryTime(), parking.getExitTime());
        }
        }

        session.getTransaction().commit();
        session.close();
    }


    
 
    public void getVehicleCountByType(String vehicleType) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query query = session.createQuery(
                "SELECT COUNT(p) FROM Parking p WHERE p.vehicleType = :vehicleType AND p.exitTime IS NULL");
        query.setParameter("vehicleType", vehicleType);

        Long count = (Long) query.uniqueResult();
        System.out.println("Number of vehicles of type " + vehicleType + " in the parking lot: " + count);
        System.out.println("-----------------------------------------------");
        System.out.println();

        Query listQuery = session.createQuery(
                "SELECT p FROM Parking p WHERE p.vehicleType = :vehicleType AND p.exitTime IS NULL");
        listQuery.setParameter("vehicleType", vehicleType);

        List<Parking> parkingList = listQuery.list();

        if (!parkingList.isEmpty()) {
            System.out.println("List of vehicles of type " + vehicleType + " in the parking lot:");
            System.out.println("--------------------------------------------");
            System.out.println(String.format("%-15s %-15s %-15s","Vehicle Id", "Vehicle Number", "Entry Time"));
         System.out.println("-------------------------------------------------------------");
            
            for (Parking parking : parkingList) {
            	System.out.println(String.format("%-15s %-15s %-15s",parking.getId(),
                        parking.getVehicleNumber(), parking.getEntryTime()));
            }
        } else {
            System.out.println("No vehicles of type " + vehicleType + " found in the parking lot.");
        }

        session.getTransaction().commit();
        session.close();
    }
      


    private double calculateFee(String vehicleType, Timestamp entryTime, Timestamp exitTime) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query query = session.createQuery(
                "SELECT pf FROM ParkingFee pf WHERE pf.vehicleType = :vehicleType");
        query.setParameter("vehicleType", vehicleType);
        ParkingFee parkingfee = (ParkingFee) query.uniqueResult();

        double fee = parkingfee != null ? parkingfee.getFee() : 0;

        session.getTransaction().commit();
        session.close();

        

        long durationInMinutes = java.time.Duration.between(entryTime.toLocalDateTime(), exitTime.toLocalDateTime()).toMinutes();
  
       // double feePerMinute = fee / 60;
        double feePerMinute;
        if (durationInMinutes <= 360) { // <= 6 hours
            feePerMinute = 50.0 / 60.0;
        } else if (durationInMinutes <= 720) { // <= 12 hours
            feePerMinute = 100.0 / 60.0;
        } else { // >= 24 hours
            feePerMinute = 150.0 / 60.0;
        }

        return durationInMinutes * feePerMinute;
    }

    private void addToHistory(String message) {
       
        System.out.println("History: " + message);
    }
}


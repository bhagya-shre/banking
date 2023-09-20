package Infinite.parking;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="parking")
public class Parking {

@Id
@Column(name="id")
@GeneratedValue(strategy=GenerationType.IDENTITY)
private int id;

@Column(name="vehicleNumber",unique=true)
private String vehicleNumber;

@Column(name="vehicleType")
private String vehicleType;

@Column(name="entryTime")
private Timestamp entryTime;

@Column(name="exitTime")
private Timestamp exitTime;

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getVehicleNumber() {
	return vehicleNumber;
}

public void setVehicleNumber(String vehicleNumber) {
	this.vehicleNumber = vehicleNumber;
}

public String getVehicleType() {
	return vehicleType;
}

public void setVehicleType(String vehicleType) {
	this.vehicleType = vehicleType;
}

public Timestamp getEntryTime() {
	return entryTime;
}

public void setEntryTime(Timestamp entryTime) {
	this.entryTime = entryTime;
}

public Timestamp getExitTime() {
	return exitTime;
}

public void setExitTime(Timestamp exitTime) {
	this.exitTime = exitTime;
}

public Parking(int id, String vehicleNumber, String vehicleType, Timestamp entryTime, Timestamp exitTime) {
	super();
	this.id = id;
	this.vehicleNumber = vehicleNumber;
	this.vehicleType = vehicleType;
	this.entryTime = entryTime;
	this.exitTime = exitTime;
}

@Override
public String toString() {
	return "Parking [id=" + id + ", vehicleNumber=" + vehicleNumber + ", vehicleType=" + vehicleType + ", entryTime="
			+ entryTime + ", exitTime=" + exitTime + "]";
}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((entryTime == null) ? 0 : entryTime.hashCode());
	result = prime * result + ((exitTime == null) ? 0 : exitTime.hashCode());
	result = prime * result + id;
	result = prime * result + ((vehicleNumber == null) ? 0 : vehicleNumber.hashCode());
	result = prime * result + ((vehicleType == null) ? 0 : vehicleType.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Parking other = (Parking) obj;
	if (entryTime == null) {
		if (other.entryTime != null)
			return false;
	} else if (!entryTime.equals(other.entryTime))
		return false;
	if (exitTime == null) {
		if (other.exitTime != null)
			return false;
	} else if (!exitTime.equals(other.exitTime))
		return false;
	if (id != other.id)
		return false;
	if (vehicleNumber == null) {
		if (other.vehicleNumber != null)
			return false;
	} else if (!vehicleNumber.equals(other.vehicleNumber))
		return false;
	if (vehicleType == null) {
		if (other.vehicleType != null)
			return false;
	} else if (!vehicleType.equals(other.vehicleType))
		return false;
	return true;
}

public Parking() {
	super();
	
}



}

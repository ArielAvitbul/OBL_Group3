package common;

import java.sql.Date;

public class Report {

	private String reportID;
	private Date dateIssued;
	
	public Report(String reportID, Date dateIssued) 
	{
		this.reportID = reportID;
		this.dateIssued = dateIssued;
	}
	
	public String getReportID() 
	{
		return reportID;
	}
	
	public void setReportID(String reportID) 
	{
		this.reportID = reportID;
	}
	
	public Date getDateIssued() 
	{
		return dateIssued;
	}
	
	public void setDateIssued(Date dateIssued) 
	{
		this.dateIssued = dateIssued;
	}
	
	
}

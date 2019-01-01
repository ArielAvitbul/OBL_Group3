package common;

import java.sql.Date;

public class LateReturnReport extends Report{

	private float lateMedian;
	private float lateDistribution;
	private float lateAvg;
	
	public LateReturnReport(String reportID, Date dateIssued, float lateMedian, float lateDistribution, float lateAvg) 
	{
		super(reportID, dateIssued);
		this.lateMedian = lateMedian;
		this.lateDistribution = lateDistribution;
		this.lateAvg = lateAvg;
	}
	
	public float getLateMedian() 
	{
		return lateMedian;
	}
	
	public void setLateMedian(float lateMedian) 
	{
		this.lateMedian = lateMedian;
	}
	
	public float getLateDistribution() 
	{
		return lateDistribution;
	}
	
	public void setLateDistribution(float lateDistribution) 
	{
		this.lateDistribution = lateDistribution;
	}
	
	public float getLateAvg() 
	{
		return lateAvg;
	}
	
	public void setLateAvg(float lateAvg) 
	{
		this.lateAvg = lateAvg;
	}
	
}

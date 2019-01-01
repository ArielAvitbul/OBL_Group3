package common;

import java.sql.Date;

public class BorrowReport extends Report{

	private float popularBookAvgBorrowTime;
	private float popularBookMedianBorrowTime;
	private float popularBookDistributionBorrowTime;
	private float regularBookAvgBorrowTime;
	private float regularBookMedianBorrowTime;
	private float regularBookDistributionBorrowTime;
	
	public BorrowReport(String reportID, Date dateIssued, float popularBookAvgBorrowTime,
			float popularBookMedianBorrowTime, float popularBookDistributionBorrowTime, float regularBookAvgBorrowTime,
			float regularBookMedianBorrowTime, float regularBookDistributionBorrowTime) 
	{
		super(reportID, dateIssued);
		this.popularBookAvgBorrowTime = popularBookAvgBorrowTime;
		this.popularBookMedianBorrowTime = popularBookMedianBorrowTime;
		this.popularBookDistributionBorrowTime = popularBookDistributionBorrowTime;
		this.regularBookAvgBorrowTime = regularBookAvgBorrowTime;
		this.regularBookMedianBorrowTime = regularBookMedianBorrowTime;
		this.regularBookDistributionBorrowTime = regularBookDistributionBorrowTime;
	}

	public float getPopularBookAvgBorrowTime() 
	{
		return popularBookAvgBorrowTime;
	}

	public void setPopularBookAvgBorrowTime(float popularBookAvgBorrowTime) 
	{
		this.popularBookAvgBorrowTime = popularBookAvgBorrowTime;
	}

	public float getPopularBookMedianBorrowTime() 
	{
		return popularBookMedianBorrowTime;
	}

	public void setPopularBookMedianBorrowTime(float popularBookMedianBorrowTime) 
	{
		this.popularBookMedianBorrowTime = popularBookMedianBorrowTime;
	}

	public float getPopularBookDistributionBorrowTime() 
	{
		return popularBookDistributionBorrowTime;
	}

	public void setPopularBookDistributionBorrowTime(float popularBookDistributionBorrowTime) 
	{
		this.popularBookDistributionBorrowTime = popularBookDistributionBorrowTime;
	}

	public float getRegularBookAvgBorrowTime() 
	{
		return regularBookAvgBorrowTime;
	}

	public void setRegularBookAvgBorrowTime(float regularBookAvgBorrowTime) 
	{
		this.regularBookAvgBorrowTime = regularBookAvgBorrowTime;
	}

	public float getRegularBookMedianBorrowTime() 
	{
		return regularBookMedianBorrowTime;
	}

	public void setRegularBookMedianBorrowTime(float regularBookMedianBorrowTime) 
	{
		this.regularBookMedianBorrowTime = regularBookMedianBorrowTime;
	}

	public float getRegularBookDistributionBorrowTime() 
	{
		return regularBookDistributionBorrowTime;
	}

	public void setRegularBookDistributionBorrowTime(float regularBookDistributionBorrowTime) 
	{
		this.regularBookDistributionBorrowTime = regularBookDistributionBorrowTime;
	}
	
}

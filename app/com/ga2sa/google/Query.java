package com.ga2sa.google;
/**
 * 
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class Query {
	public String profile;
	public String startDate;
	public String endDate;
	public String[] dimensions;
	public String[] metrics;

	public String toString() {
		return profile + "\n" + dimensions.toString() + "\n" + metrics.toString() + "\n" + startDate + "\n" + endDate;
	}
}

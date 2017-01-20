package com.nlp.task.twitter.analysis.model.dto;

import java.net.URL;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EventDto {

	private String eventId;
	private String postingId;
	private String postingTitle;
	private List<String> serviceLocations;
	private List<String> productCategories;
	private OpportunityAmtDto opportunityAmt;
	private String responseDeadline;
	private String description;
	private String buyerANID;
	private String awardDate;
	private String startDate;
	private String referenceNumber;
	private URL discoveryURL;

	/**
	 * Default constructor
	 */
	public EventDto() {
		super();
	}

	/**
	 * Returns eventId
	 *
	 * @return eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * Sets eventId
	 * 
	 * @param eventId
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * Returns postingId
	 *
	 * @return postingId
	 */
	public String getPostingId() {
		return postingId;
	}

	/**
	 * Sets postingId
	 * 
	 * @param postingId
	 */
	public void setPostingId(String postingId) {
		this.postingId = postingId;
	}

	/**
	 * Returns postingTitle
	 *
	 * @return postingTitle
	 */
	public String getPostingTitle() {
		return postingTitle;
	}

	/**
	 * Sets postingTitle
	 * 
	 * @param postingTitle
	 */
	public void setPostingTitle(String postingTitle) {
		this.postingTitle = postingTitle;
	}

	/**
	 * Returns serviceLocations
	 *
	 * @return serviceLocations
	 */
	public List<String> getServiceLocations() {
		return serviceLocations;
	}

	/**
	 * Sets serviceLocations
	 * 
	 * @param serviceLocations
	 */
	public void setServiceLocations(List<String> serviceLocations) {
		this.serviceLocations = serviceLocations;
	}

	/**
	 * Returns productCategories
	 *
	 * @return productCategories
	 */
	public List<String> getProductCategories() {
		return productCategories;
	}

	/**
	 * Sets productCategories
	 * 
	 * @param productCategories
	 */
	public void setProductCategories(List<String> productCategories) {
		this.productCategories = productCategories;
	}

	/**
	 * Returns opportunityAmt
	 *
	 * @return opportunityAmt
	 */
	public OpportunityAmtDto getOpportunityAmt() {
		return opportunityAmt;
	}

	/**
	 * Sets opportunityAmt
	 * 
	 * @param opportunityAmt
	 */
	public void setOpportunityAmt(OpportunityAmtDto opportunityAmt) {
		this.opportunityAmt = opportunityAmt;
	}

	/**
	 * Returns responseDeadline
	 *
	 * @return responseDeadline
	 */
	public String getResponseDeadline() {
		return responseDeadline;
	}

	/**
	 * Sets responseDeadline
	 * 
	 * @param responseDeadline
	 */
	public void setResponseDeadline(String responseDeadline) {
		this.responseDeadline = responseDeadline;
	}

	/**
	 * Returns description
	 *
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets description
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns buyerANID
	 *
	 * @return buyerANID
	 */
	public String getBuyerANID() {
		return buyerANID;
	}

	/**
	 * Sets buyerANID
	 * 
	 * @param buyerANID
	 */
	public void setBuyerANID(String buyerANID) {
		this.buyerANID = buyerANID;
	}

	/**
	 * Returns awardDate
	 *
	 * @return awardDate
	 */
	public String getAwardDate() {
		return awardDate;
	}

	/**
	 * Sets awardDate
	 * 
	 * @param awardDate
	 */
	public void setAwardDate(String awardDate) {
		this.awardDate = awardDate;
	}

	/**
	 * Returns startDate
	 *
	 * @return startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * Sets startDate
	 * 
	 * @param startDate
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * Returns referenceNumber
	 *
	 * @return referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * Sets referenceNumber
	 * 
	 * @param referenceNumber
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * Returns discoveryURL
	 *
	 * @return discoveryURL
	 */
	public URL getDiscoveryURL() {
		return discoveryURL;
	}

	/**
	 * Sets discoveryURL
	 * 
	 * @param discoveryURL
	 */
	public void setDiscoveryURL(URL discoveryURL) {
		this.discoveryURL = discoveryURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Event [eventId=" + eventId + ", postingId=" + postingId + ", postingTitle="
				+ postingTitle + ", serviceLocations=" + serviceLocations + ", productCategories=" + productCategories
				+ ", opportunityAmt=" + opportunityAmt + ", responseDeadline=" + responseDeadline + ", description="
				+ description + ", buyerANID=" + buyerANID + ", awardDate=" + awardDate + ", startDate=" + startDate
				+ ", referenceNumber=" + referenceNumber + ", discoveryURL=" + discoveryURL + "]";
	}
}
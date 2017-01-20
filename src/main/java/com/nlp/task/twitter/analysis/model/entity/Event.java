package com.nlp.task.twitter.analysis.model.entity;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * Entity for event
 *
 */
@Entity
@NamedQueries({ @NamedQuery(name = Event.QUERY_FIND_BY_EVENT_ID, query = "SELECT a FROM Event a WHERE a.eventId = :"
		+ Event.QUERY_PARAM_FIND_BY_EVENT_ID) })
public class Event implements Serializable {

	private static final long serialVersionUID = -8189224760313859993L;

	public static final String QUERY_FIND_BY_EVENT_ID = "findById";
	public static final String QUERY_PARAM_FIND_BY_EVENT_ID = "eventId";

	@Id
	@GeneratedValue
	private String id;

	@Column(unique = true, nullable = false)
	private String eventId;

	private String postingId;
	private String postingTitle;
	private List<String> serviceLocations;
	private List<String> productCategories;

	@OneToOne(cascade = CascadeType.ALL)
	private OpportunityAmt opportunityAmt;

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
	public Event() {
		super();
	}

	/**
	 * Returns id
	 *
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets id
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
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
	public OpportunityAmt getOpportunityAmt() {
		return opportunityAmt;
	}

	/**
	 * Sets opportunityAmt
	 * 
	 * @param opportunityAmt
	 */
	public void setOpportunityAmt(OpportunityAmt opportunityAmt) {
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
		return "Event [id=" + id + ", eventId=" + eventId + ", postingId=" + postingId + ", postingTitle="
				+ postingTitle + ", serviceLocations=" + serviceLocations + ", productCategories=" + productCategories
				+ ", opportunityAmt=" + opportunityAmt + ", responseDeadline=" + responseDeadline + ", description="
				+ description + ", buyerANID=" + buyerANID + ", awardDate=" + awardDate + ", startDate=" + startDate
				+ ", referenceNumber=" + referenceNumber + ", discoveryURL=" + discoveryURL + "]";
	}

}
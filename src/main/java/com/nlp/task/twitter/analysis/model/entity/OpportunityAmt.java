package com.nlp.task.twitter.analysis.model.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class OpportunityAmt implements Serializable {

	private static final long serialVersionUID = 4937823377025500097L;

	@Id
	@GeneratedValue
	private String id;

	private String lowerEnd;
	private String upperEnd;

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
	 * Returns lowerEnd
	 *
	 * @return lowerEnd
	 */
	public String getLowerEnd() {
		return lowerEnd;
	}

	/**
	 * Sets lowerEnd
	 * 
	 * @param lowerEnd
	 */
	public void setLowerEnd(String lowerEnd) {
		this.lowerEnd = lowerEnd;
	}

	/**
	 * Returns upperEnd
	 *
	 * @return upperEnd
	 */
	public String getUpperEnd() {
		return upperEnd;
	}

	/**
	 * Sets upperEnd
	 * 
	 * @param upperEnd
	 */
	public void setUpperEnd(String upperEnd) {
		this.upperEnd = upperEnd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OpportunityAmt [id=" + id + ", lowerEnd=" + lowerEnd + ", upperEnd=" + upperEnd + "]";
	}

}

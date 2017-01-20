package com.nlp.task.twitter.analysis.model.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OpportunityAmtDto {

	private String lowerEnd;
	private String upperEnd;

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
		return "OpportunityAmt [lowerEnd=" + lowerEnd + ", upperEnd=" + upperEnd + "]";
	}

}

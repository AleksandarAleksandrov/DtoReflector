package com.a.a.dtoreflector.domain;

import java.util.List;

public class Producer {
	
	private String companyName;
	private String companyId;
	private int foundingYear;
	private List<String> aliases;
	
	public Producer() {
		super();
	}
	
	public Producer(String companyName, String companyId, int foundingYear, List<String> aliases) {
		super();
		this.companyName = companyName;
		this.companyId = companyId;
		this.foundingYear = foundingYear;
		this.aliases = aliases;
	}



	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public int getFoundingYear() {
		return foundingYear;
	}
	public void setFoundingYear(int foundingYear) {
		this.foundingYear = foundingYear;
	}
	public List<String> getAliases() {
		return aliases;
	}
	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + foundingYear;
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
		Producer other = (Producer) obj;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (foundingYear != other.foundingYear)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Producer [companyName=" + companyName + ", companyId=" + companyId + ", foundingYear=" + foundingYear
				+ ", aliases=" + aliases + "]";
	}
	
	
}

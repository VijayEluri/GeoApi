package gov.nysenate.sage.model.districts;

@Deprecated
public class Census {
	String fips;
	
	public Census() {
		
	}
	
	public Census(String fips) {
		this.fips = fips;
	}
	
	public String getFips() {
		return fips;
	}
	
	public void setFips(String fips) {
		this.fips = fips;
	}
}

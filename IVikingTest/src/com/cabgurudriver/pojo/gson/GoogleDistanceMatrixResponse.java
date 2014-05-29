package com.cabgurudriver.pojo.gson;

public class GoogleDistanceMatrixResponse {

	
	public String [] destination_addresses;
	public String [] origin_addresses;
	public rows[] rows;
	public String status;

	public GoogleDistanceMatrixResponse() {
	}

	/*public class destination_addresses {
		public String[] destinations;
	}
	
	public class origin_addresses {
		public String[] origins;
	}*/
	
	public class rows {
		
		public elements [] elements;
		
	}
	
	public class elements {
		public distance distance;
		public duration duration;
		public String status;
	}
	
	public class distance{
		public String text;
		public String value;
	}
	
	public class duration{
		public String text;
		public String value;
	}
	

}

package com.localmatters.serializer.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A dummy Object used for testing the formatting
 */
public class DummyObject {
	private String id;
	private String name;
	private Orders orders;
	private Map<String, Address> addresses;
	
	/**
	 * @return The id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id The id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The orders
	 */
	public Orders getOrders() {
		return orders;
	}

	/**
	 * @return The orders
	 */
	public List<String> getOrdersAsList() {
		return orders;
	}
	
	/**
	 * @param orders The orders to set
	 */
	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	/**
	 * @return The addresses
	 */
	public Map<String, Address> getAddresses() {
		return addresses;
	}

	/**
	 * @param addresses The addresses to set
	 */
	public void setAddresses(Map<String, Address> addresses) {
		this.addresses = addresses;
	}

	/**
	 * The address class
	 */
	public class Address {
		private String street;
		private String zip;
		private String city;
		private String state;
		/**
		 * @return The street
		 */
		public String getStreet() {
			return street;
		}
		/**
		 * @param street The street to set
		 */
		public void setStreet(String street) {
			this.street = street;
		}
		/**
		 * @return The zip
		 */
		public String getZip() {
			return zip;
		}
		/**
		 * @param zip The zip to set
		 */
		public void setZip(String zip) {
			this.zip = zip;
		}
		/**
		 * @return The city
		 */
		public String getCity() {
			return city;
		}
		/**
		 * @param city The city to set
		 */
		public void setCity(String city) {
			this.city = city;
		}
		/**
		 * @return The state
		 */
		public String getState() {
			return state;
		}
		/**
		 * @param state The state to set
		 */
		public void setState(String state) {
			this.state = state;
		}
	}
	
	public class Orders extends ArrayList<String> {
		private static final long serialVersionUID = 1L;
		
	}
}

package edu.mum.cs544;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements ICustomerService {
	/*
	 * 1. Setter based DI START
	 * */
//	private ICustomerDAO customerDAO;
//	private IEmailSender emailSender;
//
//	public void setCustomerDAO(ICustomerDAO customerDAO) {
//		System.out.println("Setter based DI:setCustomerDAO() injected bean " + customerDAO);
//		this.customerDAO = customerDAO;
//	}
//
//	public void setEmailSender(IEmailSender emailSender) {
//		System.out.println("Setter based DI:setEmailSender() injected bean " + emailSender);
//		this.emailSender = emailSender;
//	}
	/*
	 * 1. Setter based DI END
	 * */

	/*
	 * 2. Property based DI START
	 * */
//	@Autowired
//	private ICustomerDAO customerDAO;
//	@Autowired
//	private IEmailSender emailSender;
	/*
	 * Property based DI END
	 * */

	/*
	 * 3. Constructor based DI START
	 * */
	private ICustomerDAO customerDAO;
	private IEmailSender emailSender;

	public CustomerService(ICustomerDAO customerDAO, IEmailSender emailSender) {
		this.customerDAO = customerDAO;
		this.emailSender = emailSender;
		System.out.println("Constructor based DI, injected beans are: " + customerDAO + ", " +emailSender);
	}
	/*
	* Constructor based DI END
	* */

	public void addCustomer(String name, String email, String street,
							String city, String zip) {
		Customer customer = new Customer(name, email);
		Address address = new Address(street, city, zip);
		customer.setAddress(address);
		customerDAO.save(customer);
		emailSender.sendEmail(email, "Welcome " + name + " as a new customer");
	}
}

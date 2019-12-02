package edu.mum.cs544.bank.jms;

import org.springframework.stereotype.Component;

public interface IJMSSender {
	public void sendJMSMessage (String text);
}

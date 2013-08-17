package com.demo.service;

/**
 * a callback interface used by dvbService to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 */
oneway interface IServiceCallback {
	
	/*
	 * handler common message from service
	 */
    void handlerCommEvent(int msgID, int param);
    
    /*
	 * handler search message from service
	 */
    void handlerSearchEvent(int msgID, in List<String> strList);
}
package com.demo.service;

import com.demo.service.Freq;
import com.demo.service.IServiceCallback;

interface ServiceDemoAidl {

	boolean isInited();
	
	void registerCallback(IServiceCallback cb);
    void unregisterCallback(IServiceCallback cb);
    
	Freq getFreq();
	void setFreq(in Freq freq);
}  
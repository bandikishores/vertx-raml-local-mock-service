package com.bandi.log;

import java.io.IOException;
import java.util.HashMap;

import com.bandi.data.ResponseData;

public class Logger {
	
	public static void log(String log)
	{
		System.out.println(log);
	}

	public static void log(IOException e) {
		System.out.println(e);
	}

	public static void log(Object cacheofRAML) {
		System.out.println(cacheofRAML.toString());
	}

}
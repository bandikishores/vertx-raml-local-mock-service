package com.bandi.main;

import com.bandi.verticle.MyVerticle;
import io.vertx.core.Vertx;

public class VertxMain {
	
	public static void main(String[] args) {
		 Vertx vertx = Vertx.vertx();
	     vertx.deployVerticle(new MyVerticle());
	     
	     vertx.deployVerticle(new MyVerticle(), stringAsyncResult -> {
	         System.out.println("BasicVerticle deployment complete");
	     });
	}

}
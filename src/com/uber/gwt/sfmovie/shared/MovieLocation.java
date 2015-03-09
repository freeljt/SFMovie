package com.uber.gwt.sfmovie.shared;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
//An overlay type
public class MovieLocation extends JavaScriptObject {
	// Overlay types always have protected, zero-arg ctors
	  protected MovieLocation() { }
	  /*
	   * Takes in a JSON String and evals it.
	   * @param JSON String that you trust
	   * @return JavaScriptObject that you can cast to an Overlay Type
	   */
	  // Typically, methods on overlay types are JSNI
	  //Get data from the JavaScriptObject
	  public final native String getTitle() /*-{ return this.title; }-*/;
	  public final native String getLocation()  /*-{ return this.locations;  }-*/;
	  public final native String getFunFacts()  /*-{ return this.fun_facts;  }-*/; 
	  public final native String getYear()  /*-{ return this.release_year;  }-*/; 
	  public final native String getCompany()  /*-{ return this.production_company;  }-*/; 
	  public final native String getWriter()  /*-{ return this.writer;  }-*/; 
	  public final native String getDirector()  /*-{ return this.director;  }-*/; 
	  public final native String getActor1()  /*-{ return this.actor_1;  }-*/; 
	  public final native String getActor2()  /*-{ return this.actor_2;  }-*/;
	  public final native String getActor3()  /*-{ return this.actor_3;  }-*/;
	  public final ArrayList<String> getActors() {
		  ArrayList<String> res = new ArrayList<String>();
		  if(this.getActor1()!=null) res.add(this.getActor1());
		  if(this.getActor2()!=null) res.add(this.getActor2());
		  if(this.getActor3()!=null) res.add(this.getActor3());
		  return res;
	  }
}

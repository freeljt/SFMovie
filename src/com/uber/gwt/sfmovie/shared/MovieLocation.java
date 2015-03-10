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
	  public final String getActors() {
		  String res = "N/A";
		  if(this.getActor1()!=null) res=this.getActor1();
		  if(this.getActor2()!=null) res+=","+this.getActor2();
		  if(this.getActor3()!=null) res+=","+this.getActor3();
		  return res;
	  }
	  public final ArrayList<String> getActorList() {
		  ArrayList<String> list = new ArrayList<String>();
		  if(this.getActor1()!=null) list.add(this.getActor1());
		  if(this.getActor2()!=null) list.add(this.getActor2());
		  if(this.getActor3()!=null) list.add(this.getActor3());
		  return list;
	  }
	  public final String getFunFact() {
		  if(this.getFunFacts()==null) return "N/A";
		  else return this.getFunFacts();
	  }
	  //Output the proper format of address for geocoder to translate 
	public final String getAddress() {
		if(this.getLocation()!=null) {
			String s = this.getLocation().replaceAll("\\(", ",");
			//s = s.replaceAll(" ", "+");
			s = s.replaceAll("\\)", "");
			s = s.replaceAll("\\&", "at");
			String[] array = s.split(",");
			if(array.length>1) {
				for(int i =0; i< array.length; i++) {
					if(array[i].matches("\\d")) {
						return array[i];
					}
					if(array[i].contains(" at ")) {
						return array[i];
					}
				}
			} 
			return s;
		}
		return null;
	}
}

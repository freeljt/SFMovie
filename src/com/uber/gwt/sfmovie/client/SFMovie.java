package com.uber.gwt.sfmovie.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.uber.gwt.sfmovie.shared.MovieLocation;
import com.uber.gwt.sfmovie.shared.movieAttribute;
import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SFMovie implements EntryPoint {
	/**
	 * Application will retrieve SF movies data from JSON URL below 
	 * which DataSF provided
	 */
	private static final String JSON_URL = "https://data.sfgov.org/resource/yitu-d5am.json?";
	private static final String JSON_GOOGLE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
	private static final String APP_Token = "&$$app_token=k7gOzG1DpYat5I5QObk5mfYRY";
	private static JsArray<MovieLocation> array = null;
	private static Stack<MovieLocation> toBeTranslated = new Stack<MovieLocation>();
	private static MultiWordSuggestOracle autoCompleteValues = new MultiWordSuggestOracle(); 
	private static HashMap<Marker,MovieLocation> hashtable = new HashMap<Marker,MovieLocation>();
	private static HTMLPanel resultPanel = new HTMLPanel("");
	private static int totalPins = 0;
	GoogleMap theMap =  null;
	/**
	 * Data structures to display the movie information when pin is clicked 
	 */
	CellTable<movieAttribute> movieAttrTable = new CellTable<movieAttribute>();
	HashMap<Marker, List<movieAttribute>> movieAttrMap = new HashMap<Marker, List<movieAttribute>>();
	/**
	 * This method parse the json text 
	 */
	public static final native JsArray<MovieLocation> buildMLArray(String json) /*-{      
    return eval('(' + json + ')');
}-*/;
	/**
	 * This method displays the movie details when the corresponding pin 
	 * was clicked in the map
	 * @param Marker
	 */
	private void displayDetail(Marker pin) {
		movieAttrMap.clear();
		if(pin==null) {
			movieAttribute[] nullInfo = {
					new movieAttribute("Movie Title", "(Please click pin in the map)"),
					new movieAttribute("Location", "N/A"),
					new movieAttribute("Director", "N/A"),
					new movieAttribute("Writers", "N/A"),
					new movieAttribute("Stars", "N/A"), 
					new movieAttribute("Fun Fact", "N/A")
					};
			movieAttrMap.put(pin, Arrays.asList(nullInfo));
		} else {
			movieAttribute[] NewInfo = {
					new movieAttribute("Movie Title", hashtable.get(pin).getTitle()+"("+hashtable.get(pin).getYear()+")"),
					new movieAttribute("Location", hashtable.get(pin).getLocation()),
					new movieAttribute("Director", hashtable.get(pin).getDirector()),
					new movieAttribute("Writers", hashtable.get(pin).getWriter()),
					new movieAttribute("Stars", hashtable.get(pin).getActors()), 
					new movieAttribute("Fun Fact", hashtable.get(pin).getFunFact())
					};
			movieAttrMap.put(pin, Arrays.asList(NewInfo));
		}
		movieAttrTable.setRowCount(movieAttrMap.get(pin).size(), true);
		movieAttrTable.setRowData(0, movieAttrMap.get(pin));
	}
	/**
	 * This method retrieves data from DataSF and put all of the movie locations  
	 * in the map
	 * @param theMap 
	 */
	private void collectMovieLoactions(final GoogleMap theMap) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(JSON_URL+APP_Token));
		try {
		  Request request = builder.sendRequest(null, new RequestCallback() {
		    public void onError(Request request, Throwable exception) {
		    	Window.alert("Could not retrieve data from DataSF");  
		    }
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (200 == response.getStatusCode()) {	
					//Translate JSON to JavaScriptObject
					array = buildMLArray(response.getText());
					for(int i=0; i<array.length();i++) {
						if(array.get(i).getLocation()!=null) toBeTranslated.push(array.get(i));
						//System.out.println("Added "+toBeTranslated.get(i).getAddress());
					}
					System.out.println(toBeTranslated.size()+" positions in total");
					timerToCreatePins.scheduleRepeating(2000);
			      } else {
			    	  Window.alert("Could not retrieve data from DataSF, error:"+response.getStatusText()); 
			      }
			}
			
		  });
		} catch (RequestException e) {
			Window.alert("Could not retrieve data from DataSF");
		}
	}
	/**
	 * This method tries the translate the movie location to (lat,lng) pin 
	 * in the map via geocoder service
	 * @param theMap 
	 */
	private void markPinOnMap(final String address, final MovieLocation ml) {
		
		GeocoderRequest request1 = GeocoderRequest.create();
        request1.setAddress(address);
        Geocoder geoCoder = Geocoder.create();
        geoCoder.geocode(request1, new Geocoder.Callback() {
			@Override
			public void handle(JsArray<GeocoderResult> a,
					GeocoderStatus b) {
                if (b == GeocoderStatus.OK) {
                    GeocoderResult result = a.shift();
                    MarkerOptions mo = MarkerOptions.create();
            	    final Marker pin = Marker.create(mo);
            	    pin.setPosition(result.getGeometry().getLocation());
            	    pin.setMap(theMap);
            	    totalPins++;
            	    autoCompleteValues.add(ml.getDirector());
        			autoCompleteValues.add(ml.getYear());
        			autoCompleteValues.add(ml.getWriter());
        			autoCompleteValues.add(ml.getTitle());
        			for(String s: ml.getActorList()) autoCompleteValues.add(s);
            	    Marker.ClickHandler h = new Marker.ClickHandler(){
            			@Override
            			public void handle(MouseEvent event) {
            				displayDetail(pin);
            			}
            	    };
            	    pin.addClickListener(h);
            	    hashtable.put(pin, ml);
                } else {
                	if(b==GeocoderStatus.OVER_QUERY_LIMIT) 
                	System.out.println("OVER_QUERY_LIMIT: Could not translate "+ address);
                }
			}      
        });
		
	}
	/**
	 * This is a mothod for retry the translate service
	 * 
	 */
	public final Timer timerToCreatePins = new Timer() {
		@Override
		public void run() {
        	createTenPins();
        }
     };
    public void createTenPins() {
    	int size = toBeTranslated.size();
    	for(int i=0;i<5&&i<size; i++) {
			final MovieLocation ml = toBeTranslated.pop();
			if(ml.getAddress()==null) continue;
			final String address = ml.getAddress()+",SF";
			markPinOnMap(address,ml); 
		}
    	if(0 == toBeTranslated.size()) {
    		timerToCreatePins.cancel();
    		resultPanel.clear();
    		resultPanel.add(new HTML("<h5>All "+totalPins+" pins marked in the map"));
    	}
    }
	/**
	 * This method update the markers in the map when user uses the   
	 * filter
	 * @param String
	 * @param GoogleMap 
	 */
	private void updateMarkers(String text) {
		int c = 0;
		for(Marker pin: hashtable.keySet()) {
			if(!match(hashtable.get(pin),text)) {
				pin.setVisible(false);
			} else {
				c++;
				pin.setVisible(true);
			}
		}
		resultPanel.clear();
		resultPanel.add(new HTML("<h5>"+c+" pins marked in the map"));
	}
	private boolean match(MovieLocation m, String text) {
		if(m.getDirector().contains(text)||m.getWriter().contains(text)
				||m.getActorList().contains(text)||m.getTitle().contains(text)||m.getYear().contains(text))
			return true;
		return false;
	}
	private void disPlayAllMarkers() {
		int c = 0;
		for(Marker pin: hashtable.keySet()) {
				pin.setVisible(true);
		}
		resultPanel.clear();
		resultPanel.add(new HTML("<h5>All "+c+" pins marked in the map"));
	}
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		
		//Maps.loadMapsApi(mapAPIKey, "2", false, new Runnable() {  
		//     public void run() {  
		      // logic of building the map, goes here  
		//     }  
		//});
		/**
		 * Initialize the map that has movie locations marked
		 */
		MapOptions options  = MapOptions.create() ;
		VerticalPanel widg = new VerticalPanel() ;
	    options.setCenter(LatLng.create( 37.7833, -122.4167 ));   
	    options.setZoom( 12 ) ;
	    options.setMapTypeId( MapTypeId.ROADMAP );
	    options.setDraggable(true);
	    options.setMapTypeControl(true);
	    options.setScaleControl(true) ;
	    options.setScrollwheel(true) ;
	    widg.setSize("80%","80%");
	    widg.addStyleName("center");
	    theMap=GoogleMap.create( widg.getElement(), options );
	    collectMovieLoactions(theMap);


		/*
		 * Create multiple value auto-complete text box
		 */
	    final SuggestBox filterBox = new SuggestBox(autoCompleteValues);
	    final Button filterButton = new Button("GO");
	    filterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateMarkers(filterBox.getText());
			}
		});

		VerticalPanel detail = new VerticalPanel();
		detail.add(new HTML("<h3>Movie Information</h3>"));
		detail.add(movieAttrTable);
		detail.addStyleName("center");
		displayDetail(null);
		movieAttrTable.setWidth("300px");
		TextColumn<movieAttribute> keyCol = new TextColumn<movieAttribute>() {
			@Override
			public String getValue(movieAttribute attr) {
				return attr.key;
			}
		};
		movieAttrTable.addColumn(keyCol, "Key");
		keyCol.setCellStyleNames("TableKey");
		
		TextColumn<movieAttribute> valueCol = new TextColumn<movieAttribute>() {
			@Override
			public String getValue(movieAttribute attr) {
				return attr.value;
			}
		};
		movieAttrTable.addColumn(valueCol, "Value");
		

	    Grid grid = new Grid(2,2);
	    grid.setWidget(0, 0, filterBox);
	    grid.setWidget(0, 1, filterButton);
	    grid.setWidget(1, 0, resultPanel);
	    grid.addStyleName("center");
	    HTML foot = new HTML("<h4>Created by <a style='text-decoration:none;' href='mailto:juntaolee515@gmail.com'>Juntao Li</a>");

	    DockLayoutPanel appLayout = new DockLayoutPanel(Unit.PCT);

	    appLayout.addNorth(new HTML("<h1>SF Movie Shoot Locations</h1>"),15);
	    appLayout.addNorth(new HTML("<h4>View Filter of movie title, director, writer, actor(actress) or release year"),5);
	    appLayout.addNorth(grid,12);
	    appLayout.addSouth(foot,5);
	    appLayout.addWest(detail, 50);
	    
	    appLayout.add(widg);

	    
	    resultPanel.add(new HTML("<h5>Marking the movie locations in the map......"));
	    //widg.getElement().getStyle().setMarginLeft(680, Unit.PX);
	    //widg.getElement().getStyle().setMarginTop(550, Unit.PX);
	    //p.setWidgetHorizontalPosition(widg, CENTER);
	    //p.setWidgetRightWidth(nameField, 0, Unit.PCT, 50, Unit.PCT);
	    //p.setWidgetRightWidth(widg, 0, Unit.PCT, 50, Unit.PCT);
	    //RootPanel.get("nameFieldContainer").add(p);
		RootLayoutPanel.get().add(appLayout);
		//appLayout.setWidgetLeftRight(filterBox, 5, Unit.EM, 5, Unit.EM);     // Center panel
		//appLayout.setWidgetTopBottom(filterBox, 5, Unit.EM, 5, Unit.EM);
		//appLayout.setWidgetVerticalPosition(filterBox,Layout.Alignment.BEGIN);
		//appLayout.setWidgetVerticalPosition(widg,Layout.Alignment.END);
	    
		

		
		
	}

}

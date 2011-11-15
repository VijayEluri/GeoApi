<html>
	<body>
		<% if(request.getParameter("y") != null) { %>
			<div id="mapdiv" style="height:<%=request.getParameter("y") %>px;width:<%=request.getParameter("x")%>px;"></div>
		<% }
		else { %>
					<div id="mapdiv" style="height:600px;width:860px;"></div>
		<% } %>
		
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.js"></script>
		<script src="OpenLayers.js"></script>
		<script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
		<script>
			var map;
			var layer;
			var markers;
			var mfunc;
			var popup;			
			var zoom = <%=request.getParameter("z")%>;
			var district = <%=request.getParameter("sd")%>;  

			$(document).ready(function(){
				mfunc = function(dnum) {
					if(map != null) {
						layer.removeAllFeatures();
						markers.clearMarkers();
					}
					else {
						map = new OpenLayers.Map("mapdiv");

							var gmap = new OpenLayers.Layer.Google(
							    "Google Streets", // the default
							    {numZoomLevels: 20}
							    // default type, no change needed here
							);
				            map.addLayers([gmap]);
						
						//map.addLayer(new OpenLayers.Layer.Bing());
					}
					
					district = dnum;
					$.getJSON("json/sd" + district + ".json",function(data){
						var json = data;
						var lat = json.lat;
						var lon = json.lon;
						
						if(zoom == null) {
							zoom = json.zoom;
						}
						
						var lonLat = null;
						if(json.offsetLon && json.offsetLat) {
							var lonLat = new OpenLayers.LonLat(json.offsetLon, json.offsetLat);
						}
						else {
							var lonLat = new OpenLayers.LonLat(lon, lat).transform(
									new OpenLayers.Projection("EPSG:4326"),
									map.getProjectionObject());
						}
						map.setCenter(lonLat, zoom);
						
						layer = new OpenLayers.Layer.Vector("KML", {
							strategies : [ new OpenLayers.Strategy.Fixed() ],
							protocol : new OpenLayers.Protocol.HTTP({
								url : "kml/sd" + district + ".kml",
								format : new OpenLayers.Format.KML})});
						
						//allows cursor to click and navigate over kml layer
						layer.events.fallThrough = true;
						map.addLayer(layer);
						
						<% if(request.getParameter("nonav") != null) { %>
							map.removeControl(map.getControl('OpenLayers.Control.Navigation_4'));
							map.removeControl(map.getControl('OpenLayers.Control.ArgParser_6'));
							map.removeControl(map.getControl('OpenLayers.Control.PanZoom_5'));
							map.removeControl(map.getControl('OpenLayers.Control.Attribution_7'));
						<% }
						else { %>
							markers = new OpenLayers.Layer.Markers("Markers");
							map.addLayer(markers);
							//markers hide under the kml layer otherwise
							markers.setZIndex(layer.getZIndex());
							for(var i in json.senate.senator.offices) {
								var office = json.senate.senator.offices[i];
								var tMarker = new OpenLayers.Marker(new OpenLayers.LonLat(office.lat, office.lon).transform(
										new OpenLayers.Projection("EPSG:4326"),
										map.getProjectionObject()));
								//assign the marker it's office information, used for
								//rendering the popups
								tMarker.office = office;
								
								markers.addMarker(tMarker);				
								tMarker.events.register('mousedown',tMarker, mousedown);
							}
						<% } %>
						
						//the attribution by default hangs out in an awkward part of the map
						$('.olControlAttribution').css('bottom','0px');
					});
				};
				
				if(district != null) {
					mfunc(district);
				}
			});
			
			function mousedown(evt) {
				destroyLocalPopup();
				
				var o = evt.object.office;
				
				popup = new OpenLayers.Popup.FramedCloud("data", 
						new OpenLayers.LonLat(evt.object.office.lat, evt.object.office.lon).transform(
								new OpenLayers.Projection("EPSG:4326"),
								map.getProjectionObject()),
								new OpenLayers.Size(200,200), 
								o.officeName + "<br>" + o.street + "<br>" + o.city + ", " + o.state 
								+ "<br>Phone: " +o.phone + "<br>Fax: " + o.fax, 
								null, true, function() {popup.toggle();});
				markers.map.addPopup(popup);
				
				OpenLayers.Event.stop(evt);
			}
			
			function destroyLocalPopup() {
				if(popup != null) {
					popup.destroy();
		            popup = null;
		        }
			}
		</script>
	</body>
</html>
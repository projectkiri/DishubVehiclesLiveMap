package travel.kiri.dishubvehicleslivemap.protocols;

import android.os.AsyncTask;
import android.util.Xml;

import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import travel.kiri.dishubvehicleslivemap.models.VehicleInfo;

/**
 * Created by PascalAlfadian on 30/6/2015.
 */
public class DataIdRetriever {
    public static final String SERVICE_URL = "http://id-gpstracker.com/webservice/tracknow.asmx/getDeviceListWithLocation?APIKey=2243a9fcdca0f45232fb0f94d99c5a2d";

    public interface DataIdReadyHandler {
        public void dataIdReady(List<VehicleInfo> vehicles);
    }

    public void retrieveVehiclesInfo(final DataIdReadyHandler handler) {
        new AsyncTask<Object, Object, List<VehicleInfo>>() {

            @Override
            protected List<VehicleInfo> doInBackground(Object... params) {
                InputStream inputStream = null;
                BufferedReader reader = null;
                try {
                    List<VehicleInfo> vehicleInfoList = new ArrayList<VehicleInfo>();
                    HttpURLConnection connection = (HttpURLConnection) (new URL(SERVICE_URL)).openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("GET");
                    connection.connect();
                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    XmlPullParser xmlParser = Xml.newPullParser();
                    xmlParser.setInput(reader);
                    xmlParser.nextTag();
                    xmlParser.require(XmlPullParser.START_TAG, null, "ArrayOfDeviceLastLocation");
                    while (xmlParser.next() != XmlPullParser.END_TAG) {
                        VehicleInfo vehicleInfo = new VehicleInfo();
                        xmlParser.next();
                        xmlParser.require(XmlPullParser.START_TAG, null, "DeviceLastLocation");
                        while (xmlParser.next() != XmlPullParser.END_TAG) {
                            xmlParser.next(); // start tag
                            while (xmlParser.isEmptyElementTag()) {
                                // skip empty tags
                                xmlParser.next();
                            }
                            String tagName = xmlParser.getName();
                            xmlParser.next(); // read value
                            String text = xmlParser.getText();
                            switch (tagName) {
                                case "TE_UID":
                                    vehicleInfo.setUniqueId(text);
                                    break;
                                case "TE_NAME":
                                    vehicleInfo.setName(text);
                                    break;
                                case "GPS_Longitude":
                                    vehicleInfo.setLongitude(Double.parseDouble(text));
                                    break;
                                case "GPS_Latitude":
                                    vehicleInfo.setLatitude(Double.parseDouble(text));
                                    break;
                                case "GPS_State":
                                    vehicleInfo.setGpsActive(text.equals("1"));
                                    break;
                                case "GPS_Direction":
                                    vehicleInfo.setDirection(Double.parseDouble(text));
                                    break;
                            }
                            xmlParser.next(); // skip end tag
                        }
                        vehicleInfoList.add(vehicleInfo);
                    }
                    return vehicleInfoList;
                } catch (MalformedURLException mue) {
                    throw new RuntimeException("Internal error: " + mue);
                } catch (IOException e) {
                    return null;
                } catch (XmlPullParserException e) {
                    return null;
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException ioe) {
                        // void
                    }
                }
            }

            @Override
            protected void onPostExecute(List<VehicleInfo> result) {
                handler.dataIdReady(result);
            }
        }.execute();

    }
}

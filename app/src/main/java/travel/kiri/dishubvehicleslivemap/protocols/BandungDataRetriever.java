package travel.kiri.dishubvehicleslivemap.protocols;

import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import travel.kiri.dishubvehicleslivemap.models.VehicleInfo;

/**
 * Created by PascalAlfadian on 30/6/2015.
 */
public class BandungDataRetriever {
    public static final String SERVICE_URL = "http://id-gpstracker.com/webservice/tracknow.asmx/getDeviceListWithLocation?APIKey=2243a9fcdca0f45232fb0f94d99c5a2d";

    public void retrieveVehiclesInfo(final DataReadyHandler handler) {
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

                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = builder.parse(inputStream);
                    Element root = document.getDocumentElement();
                    if (!root.getTagName().equals("ArrayOfDeviceLastLocation")) {
                        throw new IOException("Root element is not ArrayOfDeviceLastLocation");
                    }
                    NodeList nodes = root.getElementsByTagName("DeviceLastLocation");
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node = nodes.item(i);
                        NodeList properties = node.getChildNodes();
                        VehicleInfo vehicleInfo = new VehicleInfo();
                        for (int j = 0; j < properties.getLength(); j++) {
                            Node property = properties.item(j);
                            String tagName = property.getNodeName();
                            String text = property.getTextContent();
                            switch (tagName) {
                                case "TE_UID":
                                    vehicleInfo.setUniqueId(text);
                                    break;
                                case "TE_NAME":
                                    vehicleInfo.setName(text);
                                    break;
                                case "TE_OWNER":
                                    vehicleInfo.setIconName(ownerToIcon(text));
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
                        }
                        if (vehicleInfo.isGpsActive()) {
                            vehicleInfoList.add(vehicleInfo);
                        }
                    }
                    return vehicleInfoList;
                } catch (MalformedURLException mue) {
                    throw new RuntimeException("Internal error: " + mue);
                } catch (IOException e) {
                    return null;
                } catch (ParserConfigurationException e) {
                    return null;
                } catch (SAXException e) {
                    return null;
                } catch (Exception e) {
                    return null;
                } finally{
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

    private static String ownerToIcon(String owner) {
        if (owner == null) {
            return null;
        }
        switch (owner) {
            case "Dinas Kesehatan":
                return "ambulance";
            case "DBMP":
                return null;
            case "Dinas Perhubungan":
                return "bus";
            case "Pemadam Kebakaran":
                return "firetruck";
            case "Satpol PP":
                return null;
            case "PD Kebersihan":
                return null;
            default:
                return null;
        }
    }
}

package travel.kiri.dishubvehicleslivemap.protocols;

import java.util.List;

import travel.kiri.dishubvehicleslivemap.models.VehicleInfo;

/**
 * Created by PascalAlfadian on 3/7/2015.
 */
public interface DataReadyHandler {
    public void dataIdReady(List<VehicleInfo> vehicles);

}

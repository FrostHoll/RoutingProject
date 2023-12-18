package com.frostholl.routingproject.interfaces;

import com.frostholl.routingproject.models.BusRoute;
import com.frostholl.routingproject.models.BusStop;
import com.frostholl.routingproject.models.House;
import com.frostholl.routingproject.models.Joint;

import java.util.List;

public interface InitEventListener {
    void onInit(List<House> houses, List<Joint> joints, List<BusStop> busStops, List<BusRoute> busRoutes);
}

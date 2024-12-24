package com.example.runalyze.service.location.models

import com.google.android.gms.maps.model.LatLng

sealed interface PathPoint{
    class LocationPoint(val latLng: LatLng): PathPoint
    object EmptyLocationPoint: PathPoint
}
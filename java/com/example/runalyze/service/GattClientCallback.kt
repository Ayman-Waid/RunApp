package com.example.runalyze.service

import android.annotation.SuppressLint
import android.bluetooth.*
import android.util.Log
import com.example.runalyze.viewmodel.RunViewModel
import java.util.*


class GattClientCallback(val model: RunViewModel) : BluetoothGattCallback() {
    private val tag = "Runalyze Debug"
    private var index = 0
    private var index2 = 0

    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (status == BluetoothGatt.GATT_FAILURE) {
            Log.d(tag, "GATT connection failure")
            return
        } else if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(tag, "GATT connection success")
        }
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d(tag, "Connected GATT service")
            gatt.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d(tag, "onConnectionStateChange: disconnect")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        if (status != BluetoothGatt.GATT_SUCCESS) {
            return
        }
        Log.d(tag, "onServicesDiscovered()")
        for (gattService in gatt.services) {
            Log.d(tag, "Service ${gattService.uuid}")
            if (gattService.uuid == UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")) {
                Log.d(tag, "BINGO!!!")
                for (gattCharacteristic in gattService.characteristics)
                    Log.d(
                        "DBG",
                        "Characteristic ${gattCharacteristic.uuid} and name is ${gattCharacteristic.describeContents()} and ${gattCharacteristic.properties}"
                    )
                /* setup the system for the notification messages */
                val characteristic =
                    gatt.getService(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"))
                        .getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"))
                gatt.readCharacteristic(characteristic)
                if (gatt.setCharacteristicNotification(characteristic, true)) {
                    // then enable them on the server
                    Log.d(tag, "onServicesDiscovered: start to write notification")
                    for (des in characteristic.descriptors)
                        Log.d(
                            tag,
                            "onServicesDiscovered: description is ${des.uuid} permission${des.permissions} value ${des.value} "
                        )
                    Log.d(tag, "onServicesDiscovered: finish des")
                    val descriptor =
                        characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                } else Log.d(tag, "onServicesDiscovered: fail to write notification")
            }
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int,
    ) {
        super.onDescriptorWrite(gatt, descriptor, status)
        Log.d(
            tag,
            "onDescriptorWrite status is $status and descriptor is ${descriptor.value}  ${descriptor.permissions}"
        )
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
    ) {
        val bpm = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1)
        Log.d(tag, "BPM: $bpm")
        if (bpm != null) {
            model.mBPM.postValue(bpm)
            model.listBPM.value?.add(bpm)
            if ((model.highmBPM.value?.compareTo(bpm) ?: -1) < 0) {
                model.highmBPM.postValue(bpm)
                index2++
            }
            if ((model.lowmBPM.value?.compareTo(bpm) ?: 1) > 0) model.lowmBPM.postValue(bpm)
            Log.d(tag, "heart rate is ${model.mBPM.value}")
            index++
        }
    }
}
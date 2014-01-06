package com.asiaonline.tobaccoassistant.tool;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

public class BluetoothTool {
	private BluetoothAdapter mBluetoothAdapter;
	public static final int TURN_ON_BLUETOOTH = 1;
	public BluetoothTool(){
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	public BluetoothDevice getIdentityCardDivece(){
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				 	String str;
				 	str = device.getName().substring(0, 3);
					if(str.equalsIgnoreCase("SYN")){	
						return device;	
					} 
			}
		}
		mBluetoothAdapter.cancelDiscovery();
		return null;
	}
	
}

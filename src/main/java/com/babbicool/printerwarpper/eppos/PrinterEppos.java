package com.babbicool.printerwarpper.eppos;


import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.babbicool.lib.printerwrapper.Printer;
import com.babbicool.lib.printerwrapper.utils.BluetoothHandler;
import com.babbicool.lib.printerwrapper.utils.BluetoothService;

import java.io.ByteArrayOutputStream;

public class PrinterEppos extends Printer {

    private static String TAG = "PrinterEppos";

    private boolean isPrinterConnected = false;
    private BluetoothService bluetoothService;
    private BluetoothHandler handler;
    private BluetoothDevice bluetoothDevice;

    private BluetoothHandler.HandlerInterfce listener = new BluetoothHandler.HandlerInterfce() {
        @Override
        public void onDeviceConnected() {
            isPrinterConnected = true;
            try {
                getPrinterConnectedListener().onPrinterConnected(bluetoothDevice.getAddress());
            } catch (Exception e) {
                e.printStackTrace();
                bluetoothService.stop();
                if(getPrinterConnectedListener()!=null)
                    getPrinterConnectedListener().onError();
                isPrinterConnected = false;
            }
        }

        @Override
        public void onDeviceConnecting() {
            Log.d(TAG, "onDeviceConnecting");
        }

        @Override
        public void onDeviceConnectionLost() {
            Log.d(TAG, "onDeviceConnectionLost");
            isPrinterConnected = false;
            if(getPrinterConnectionLostListener()!=null)
                getPrinterConnectionLostListener().onPrinterConnectionLost();
        }

        @Override
        public void onDeviceUnableToConnect() {
            Log.d(TAG, "onDeviceUnableToConnect");
            if(getPrinterConnectedListener()!=null)
                getPrinterConnectedListener().onError();

            isPrinterConnected = false;

        }
    };

    @Override
    public boolean isConnected() {
        return isPrinterConnected;
    }

    @Override
    public void connect(String address) {

        if(isPrinterConnected)
            return ;

        if(bluetoothService==null){
            handler = new BluetoothHandler(getContext(),listener);
            bluetoothService = new BluetoothService(getContext(),handler);
        }

        bluetoothDevice = bluetoothService.getDevByMac(address);
        bluetoothService.connect(bluetoothDevice);
    }

    @Override
    public void connectByName(String name) {

        if(isPrinterConnected)
            return ;

        if(bluetoothService==null){
            handler = new BluetoothHandler(getContext(),listener);
            bluetoothService = new BluetoothService(getContext(),handler);
        }

        bluetoothDevice = bluetoothService.getDevByName(name);
        bluetoothService.connect(bluetoothDevice);
    }

    @Override
    public void disconnect() {
        isPrinterConnected = false;
        bluetoothService.stop();
    }

    @Override
    public void printString(String text) {
        bluetoothService.write(PrinterCommands.ESC_ALIGN_CENTER);
        bluetoothService.sendMessage(text,"");
    }

    @Override
    public void printLine() {
        bluetoothService.write(PrinterCommands.FEED_LINE);
    }

    @Override
    public void printImage(Bitmap bitmap) {

        byte[] img = ImageUtils.decodeBitmap(bitmap);
        bluetoothService.write(PrinterCommands.ESC_ALIGN_CENTER);
        bluetoothService.write(img);

    }

    @Override
    public void reset() {

    }

    @Override
    public void flush() {

    }

    @Override
    public void printStringBold(String text) {
        bluetoothService.write(PrinterCommands.ESC_ALIGN_CENTER);
        bluetoothService.sendMessage(text,"");
    }
}

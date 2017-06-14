package com.tw.tw_ble2_module.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;


import com.tw.tw_common_module.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/28
 * Time:上午11:45
 */
public class BluetoothChatService {

    private static final String NAME = "BluetoothChat";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_START = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_FAILE = 4;
    public static final int STATE_LOST = 5;
    public static final int STATE_WRITE = 6;

    private boolean isStop = false;

    private BluetoothSocket socket = null;
    private BluetoothSocket socket_two = null;
    private BluetoothSocket socket_three = null;
    private BluetoothSocket socket_four = null;


    private OnReadBluetoothListener mOnReadBluetoothListener;
    private OnBluetoothStatusMsgListener mOnBluetoothStatusMsgListener;
    private OnConnectedListener mOnConnectedListener;

    public interface OnReadBluetoothListener{
        void onReadBluetooth(BluetoothSocket socket, byte[] bytes);
    }

    public interface OnBluetoothStatusMsgListener{
        void onBluetoothStatusMsg(int state, String msg);
    }

    public interface OnConnectedListener{
        void onConnected(BluetoothSocket socket, BluetoothDevice device);
    }

    public void setOnReadBluetoothListener(OnReadBluetoothListener onReadBluetoothListener) {
        mOnReadBluetoothListener = onReadBluetoothListener;
    }

    public void setOnBluetoothStatusMsgListener(OnBluetoothStatusMsgListener onBluetoothStatusMsgListener) {
        mOnBluetoothStatusMsgListener = onBluetoothStatusMsgListener;
    }

    public void setOnConnectedListener(OnConnectedListener onConnectedListener) {
        mOnConnectedListener = onConnectedListener;
    }

    public BluetoothChatService() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    private synchronized void setState(int state,String msg) {
        mState = state;
        if(mOnBluetoothStatusMsgListener != null){
            mOnBluetoothStatusMsgListener.onBluetoothStatusMsg(state,msg);
        }
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        LogUtils.d("start");

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_START,"start");
    }

    public synchronized void connect(BluetoothDevice device) {
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING,device.getAddress());
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        isStop = false;
        LogUtils.d("连接设备 name = "+device.getName()+",address = "+device.getAddress()+",socket = "+socket);

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        if(mOnConnectedListener != null){
            mOnConnectedListener.onConnected(socket,device);
        }

        setState(STATE_CONNECTED,device.getAddress());
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        LogUtils.d("stop");
        isStop = true;
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE,"none");
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private void connectionFailed(BluetoothDevice device) {
        setState(STATE_FAILE,device.getAddress());
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     * @param device
     */
    private void connectionLost(BluetoothDevice device) {
        setState(STATE_LOST,device.getAddress());
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e("sxd", " ======== ufcomm exception =======" , e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            LogUtils.d("BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            while (true) {
                try {
                    LogUtils.d("thread is start and accept");
                    if (socket == null) {
                        socket = mmServerSocket.accept();

                    }
//                    if (socket_two == null) {
//                        LogUtils.d("wait two");
//                        socket_two = mmServerSocket.accept();
//
//                    }
//                    if (socket_three == null) {
//                        socket_three = mmServerSocket.accept();
//
//                    }
//                    if (socket_four == null) {
//                        socket_four = mmServerSocket.accept();
//                    }
                } catch (IOException e) {
                    break;
                }

            }




            LogUtils.d("END mAcceptThread");
        }

        public void cancel() {
            LogUtils.d("cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                LogUtils.d("close() of server failed "+e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                LogUtils.d("create() failed "+e);
            }
            mmSocket = tmp;
        }

        public void run() {
            LogUtils.d("BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                Log.e("sxd", "disconnect",e);
                connectionFailed(mmDevice);
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    LogUtils.d("unable to close() socket during connection failure "+e2);
                }
                // Start the service over to restart listening mode
                BluetoothChatService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                LogUtils.d("close() of connect socket failed "+e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     *
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private String temp;

        public ConnectedThread(BluetoothSocket socket) {
            LogUtils.d("create ConnectedThread socket = "+socket);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                LogUtils.d("temp sockets not created "+e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){

            byte[] buffer = new byte[1024];
            byte[] buf_data = new byte[0];
            int bytes;
            int count = 0;
            int status = 0;
            int length = 0;

            while (true){

                if(mOnReadBluetoothListener != null){

                    if(isStop || mmSocket == null || mmInStream == null || !mmSocket.isConnected()){
                        return;
                    }

                    try {

                        bytes = mmInStream.read(buffer);

                        if(bytes > 0) {

                            for (int i = 0; i < bytes; i++) {
                                if(status == 0 && (buffer[i] & 0xFF) == 0xA5){
                                    status = 1;
                                    count = 0;
                                }else if(status == 1 && (buffer[i] & 0xFF) == 0x21){
                                    status = 2;
                                }else if(status == 2 && (buffer[i] & 0xFF) == 0x20){
                                    status = 3;
                                }else if(status == 3){
                                    length = (buffer[i] & 0xFF);
                                    buf_data = new byte[length];
                                    status = 4;
                                }else if(status == 4){
                                    buf_data[count] = buffer[i];
                                    count++;
                                    if(count == length && count != 0){

//                                        LogUtils.d("设备:"+mmSocket.getRemoteDevice().getName()+
//                                                "  正在读写中...,buf_data = "+Arrays.toString(buf_data)+
//                                                ",读取数据长度 = "+bytes);
                                        mOnReadBluetoothListener.onReadBluetooth(mmSocket,buf_data);

                                        status = 0;
                                        count = 0;
                                        length = 0;

                                    }
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
//                        try {
//                            if(mmInStream != null){
//                                mmInStream.close();
//                            }
//
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
                        LogUtils.d("disconnected "+e);
                        connectionLost(mmSocket.getRemoteDevice());
                        break;
                    }
                }

            }
        }


        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer)
        {
            try
            {

                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                setState(STATE_WRITE,"write");
            }
            catch (IOException e) {
                LogUtils.d("Exception during write "+e);
            }
        }

        public void cancel()
        {
            try {
                mmSocket.close();
                mmInStream.close();
            } catch (IOException e) {
                LogUtils.d("close() of connect socket failed "+e);
            }
        }
    }
}



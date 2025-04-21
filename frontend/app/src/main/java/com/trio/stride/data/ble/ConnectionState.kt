package com.trio.stride.data.ble

interface ConnectionState {
    object Connected: ConnectionState
    object Disconnected: ConnectionState
    object  Uninitialized: ConnectionState
    object CurrentlyInitializing: ConnectionState
}
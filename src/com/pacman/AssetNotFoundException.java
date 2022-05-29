package com.pacman;

import java.io.IOException;
import java.util.logging.Logger;

public class AssetNotFoundException extends IOException {

    public AssetNotFoundException() {
        super();
    }

    public AssetNotFoundException(String msg) {
        super(msg);
    }

    public AssetNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public AssetNotFoundException(Throwable t) {
        super(t);
    }
}

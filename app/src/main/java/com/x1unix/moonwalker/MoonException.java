package com.x1unix.moonwalker;

import java.io.IOException;

public class MoonException extends IOException {
    public MoonException() { super(); }
    public MoonException(String message) { super(message); }
    public MoonException(String message, Throwable cause) { super(message, cause); }
    public MoonException(Throwable cause) { super(cause); }
}

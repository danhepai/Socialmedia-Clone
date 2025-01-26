package org.example.mysocialnetworkgui.exceptions;

public class DatabaseConnectionException extends RuntimeException{
    public DatabaseConnectionException(String message){
        super(message);
    }
}

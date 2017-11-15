package Beans;

import Enums.ConnectionResult;
import Enums.ErrorField;
import Enums.Form;

import java.io.Serializable;

public class ConnectionB implements Serializable {
    private ConnectionResult Result = null;
    private ErrorField Field = null;
    private String ErrorMessage = "";
    private Form Place = null;

    public ConnectionB() {
    }

    public Form getPlace() {
        return Place;
    }

    public void setPlace(Form place) {
        Place = place;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public ErrorField getField() {
        return Field;
    }

    public void setField(ErrorField field) {
        Field = field;
    }

    public ConnectionResult getResult() {
        return Result;
    }

    public void setResult(ConnectionResult result) {
        Result = result;
    }
}

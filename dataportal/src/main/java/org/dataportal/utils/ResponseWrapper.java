package org.dataportal.utils;

import net.sf.json.JSONObject;

public class ResponseWrapper {
    private boolean success;
    private String message;
    private Object data;
       
    public ResponseWrapper(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }
    
    public ResponseWrapper(boolean success, Object data) {
        this.success = success;
        this.message = "";
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
    
    public String asJSON() {
        return JSONObject.fromObject(this).toString();
    }
    
}

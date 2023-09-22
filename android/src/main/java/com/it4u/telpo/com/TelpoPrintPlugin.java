package com.it4u.telpo.com;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.printer.UsbThermalPrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@CapacitorPlugin(name = "TelpoPrint")
public class TelpoPrintPlugin extends Plugin {
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int OVERHEAT = 12;
    private TelpoPrint implementation = new TelpoPrint();
    UsbThermalPrinter printer;
    @Override
    public void load() {
        printer = new UsbThermalPrinter(this.getActivity().getApplicationContext());
        super.load();
    }


    @PluginMethod
    public void print(PluginCall call) {
        JSObject data = call.getObject("receipt");
        try {
            printer.setMonoSpace(true);
            printer.setGray(7);
            printReceipt(data);
        }catch (TelpoException t){
            String result = t.toString();
            if (result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
            call.reject("NOPAPER");
            } else if (result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                call.reject("OVERHEAT");
            }
        }
        call.resolve();
    }
    void printReceipt(JSONObject data){
        try {
            printer.reset();
            printer.start(1);
            try {
                String title = data.getString("title");
                String footer = data.getString("footer");
                JSONArray lines = data.getJSONArray("lines");
                JSONObject header = data.getJSONObject("header");
                String agencyName = header.getString("agencyName");
                String agencyContact = header.getString("agencyContact");
                String agencyAdress = header.getString("agencyAdress");
                printer.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                printer.setTextSize(30);
                printer.addString(agencyName +"\n");
                printer.setTextSize(20);
                printer.addString("Tel: "+agencyContact + "\n");
                printer.addString("Adresse: "+agencyAdress + "\n");
                printer.setLineSpace(15);
                printer.addString(title +"\n");
                for (int i=0; i < lines.length(); i++){
                    if(i==0){
                        printer.walkPaper( 15);
                    }
                    if(i == (lines.length() - 1)){
                        printer.walkPaper( 30);
                    }
                    JSONObject val = lines.getJSONObject(i);
                    String key = val.getString("title");
                    String value = val.getString("value");
                    printer.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                    printer.addString(key);
                    printer.setAlgin(UsbThermalPrinter.ALGIN_RIGHT);
                    printer.addString(value+"\n");
                }
                printer.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                printer.addString(footer+"\n");
                printer.printString();
                printer.walkPaper(10);

            }catch (JSONException e){

            }
        }catch (TelpoException telpoException){
            telpoException.printStackTrace();
        }
    }
}

package ch.unisg.ems.eventprocessor.loadprofile;

import ch.unisg.ems.eventprocessor.serialization.ProductionEvent;

public class UnitConverter {

    public ProductionEvent convertToKW(ProductionEvent event) {
        double convertedLoad = 0;

        if (event.getUnitLoad().equals("W")) { // watt
            convertedLoad = event.getLoad() / 1000.0;
        } else if (event.getUnitLoad().equals("daW")) { // decawatt
            convertedLoad = event.getLoad() / 100.0;
        } else if (event.getUnitLoad().equals("hW")) { // hectowatt
            convertedLoad = event.getLoad() / 10.0;
        } else if (event.getUnitLoad().equals("MW")) { // megawatt
            convertedLoad = event.getLoad() * 1000.0;
        } else {
            System.out.println("Unable to convert unit to kW: " + event.getUnitLoad());
        }

        event.setLoad(convertedLoad);
        return event;
    }
}

package eu.fbk.mpba.sensorsflowsa.stubs;

import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import eu.fbk.mpba.sensorsflowsa.OutputPlugIn;
import eu.fbk.mpba.sensorsflowsa.SensorComponent;
import eu.fbk.mpba.sensorsflowsa.base.SensorDataEntry;
import eu.fbk.mpba.sensorsflowsa.base.SensorEventEntry;

/**
 * Comma Separated Values Output Plug-In
 *
 * This plug-in saves the data in a CSV file. The table is composed by the timestamp column and a
 * column for each float value in the array (the ValueT type is specified).
 */
public class CsvOutput extends OutputPlugIn<Long, float[]> {

    String _name;
    DataSaver _sav;
    List<List<Object>> headers = new ArrayList<List<Object>>();
    List<SensorComponent<Long, float[]>> _linkedSensors = new ArrayList<SensorComponent<Long, float[]>>();

    public CsvOutput(String name) {
        _name = name;
    }

    public List<String> getFiles() {
        List<String> a = new ArrayList<String>(_sav._files.length);
        for (int i = 0; i < _sav._files.length; i++) {
            a.add(_sav._files[i].getAbsolutePath());
        }
        return a;
    }

    @Override
    public void setLinkedSensors(List<SensorComponent> linkedSensors) {
        _sav = new DataSaver(Environment.getExternalStorageDirectory().getPath() +
                        "/eu.fbk.mpba.sensorsflows.stubs/" +
                        DataSaver.getHumanDateTimeName() + "/",
                        linkedSensors.toArray(), ".csv", ";", "\n");
        _linkedSensors.addAll(linkedSensors);
        for (SensorComponent l : linkedSensors) {
            List<Object> h = new ArrayList<Object>();
            h.add("timestamp");
            h.addAll(l.getValuesDescriptors());
            headers.add(h);
        }
    }

    @Override
    protected void pluginInitialize() {
        _sav.initFS(headers);
    }

    @Override
    protected void pluginFinalize() {

    }

    @Override
    protected void newSensorEvent(SensorEventEntry event) {

    }

    @Override
    protected void newSensorData(SensorDataEntry<Long, float[]> data) {
        List<Object> line = new ArrayList<Object>();
        line.add(data.time.toString());
        for (int i = 0; i < data.value.length; i++)
            line.add(data.value[i]);
        //noinspection SuspiciousMethodCalls
        _sav.writeCSV(_linkedSensors.indexOf(data.sensor), line);
    }

    @Override
    public String toString() {
        return "CsvOutput-" + _name;
    }
}
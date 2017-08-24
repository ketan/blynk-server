package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.widgets.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.04.17.
 */
public class WebDataStream implements CopyObject<WebDataStream> {

    public final int id;

    public final String name;

    public final MeasurementUnit units;

    public final double min;

    public final double max;

    public final byte pin;

    @JsonCreator
    public WebDataStream(@JsonProperty("id") int id,
                         @JsonProperty("name") String name,
                         @JsonProperty("units") MeasurementUnit units,
                         @JsonProperty("min") double min,
                         @JsonProperty("max") double max,
                         @JsonProperty("pin") byte pin) {
        this.id = id;
        this.name = name;
        this.units = units;
        this.min = min;
        this.max = max;
        this.pin = pin;
    }

    @Override
    public WebDataStream copy() {
        return new WebDataStream(id, name, units, min, max, pin);
    }
}

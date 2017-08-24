package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.widgets.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 5/12/17.
 */
public class EventReceiver implements CopyObject<EventReceiver> {

    public final int metaFieldId;

    public final MetadataType type;

    public final String value;

    @JsonCreator
    public EventReceiver(@JsonProperty("id") int metaFieldId,
                         @JsonProperty("type") MetadataType type,
                         @JsonProperty("value") String value) {
        this.metaFieldId = metaFieldId;
        this.type = type;
        this.value = value;
    }

    @Override
    public EventReceiver copy() {
        return new EventReceiver(metaFieldId, type, value);
    }

}

package cc.blynk.server.core.protocol.model.messages.appllication;

import cc.blynk.server.core.protocol.model.messages.StringMessage;

import static cc.blynk.server.core.protocol.enums.Command.GET_ENERGY;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class GetEnergy extends StringMessage {

    public GetEnergy(int messageId, String body) {
        super(messageId, GET_ENERGY, body.length(), body);
    }

    @Override
    public String toString() {
        return "GetEnergy{" + super.toString() + "}";
    }
}

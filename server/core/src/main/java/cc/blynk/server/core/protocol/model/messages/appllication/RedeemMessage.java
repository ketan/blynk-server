package cc.blynk.server.core.protocol.model.messages.appllication;

import cc.blynk.server.core.protocol.model.messages.StringMessage;

import static cc.blynk.server.core.protocol.enums.Command.REDEEM;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class RedeemMessage extends StringMessage {

    public RedeemMessage(int messageId, String body) {
        super(messageId, REDEEM, body.length(), body);
    }

    @Override
    public String toString() {
        return "RedeemMessage{" + super.toString() + "}";
    }
}

package ru.nanit.limbo.protocol;

public class PreRenderedPacket implements PacketOut {

    private final PacketOut packet;
    private byte[] message;

    public PreRenderedPacket(PacketOut packet){
        this.packet = packet;
    }

    public PacketOut getWrappedPacket(){
        return packet;
    }

    public PreRenderedPacket render(){
        ByteMessage renderedMessage = ByteMessage.create();
        packet.encode(renderedMessage);
        this.message = renderedMessage.toByteArray();
        return this;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeBytes(message);
    }

    public static PreRenderedPacket of(PacketOut packet){
        return new PreRenderedPacket(packet).render();
    }
}

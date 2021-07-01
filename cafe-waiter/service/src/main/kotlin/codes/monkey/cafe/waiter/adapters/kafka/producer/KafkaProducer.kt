package codes.monkey.cafe.waiter.adapters.kafka.producer

import codes.monkey.cafe.waiter.domain.model.OrderDeliveredEvent
import codes.monkey.cafe.waiter.domain.model.OrderTakenEvent
import codes.monkey.cafe.waiter.events.Item
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.EventMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(val kafkaTemplate: KafkaTemplate<String, SpecificRecordBase>,
                    @Value(value = "\${kafka.producer.topic}")
                    val producerTopic: String) {

    @EventHandler(payloadType = OrderTakenEvent::class)
    fun onOrderTakenEvent(event: EventMessage<OrderTakenEvent>) {
        val publicEvent = codes.monkey.cafe.waiter.events.OrderTakenEvent.newBuilder()
                .setEventId(event.identifier)
                .setWaiterId(event.payload.waiterId.toString())
                .setOrderId(event.payload.orderId.toString())
                .setItems(
                        event.payload.items.map {
                            Item.newBuilder()
                                    .setName(it.name)
                                    .setQuantity(it.quantity)
                                    .build()
                        }
                ).build()
        kafkaTemplate.send(producerTopic, event.payload.waiterId.toString(), publicEvent)
    }

    @EventHandler(payloadType = OrderDeliveredEvent::class)
    fun onOrderDeliveredEvent(event: EventMessage<OrderDeliveredEvent>) {
        val publicEvent = codes.monkey.cafe.waiter.events.OrderDeliveredEvent.newBuilder()
                .setEventId(event.identifier)
                .setWaiterId(event.payload.waiterId.toString())
                .setOrderId(event.payload.orderId.toString())
                .build()
        kafkaTemplate.send(producerTopic, event.payload.waiterId.toString(), publicEvent)
    }

}
package codes.monkey.cafe.kitchen.adapters.kafka.producer

import codes.monkey.cafe.kitchen.domain.model.OrderPreparationCompletedEvent
import codes.monkey.cafe.kitchen.domain.model.OrderPreparationStartedEvent
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

    @EventHandler(payloadType = OrderPreparationStartedEvent::class)
    fun onOrderPreparationStartedEvent(event: EventMessage<OrderPreparationStartedEvent>) {
        val payload = event.payload
        val publicEvent = codes.monkey.cafe.kitchen.events.OrderPreparationStartedEvent.newBuilder()
                .setEventId(event.identifier)
                .setCookId(payload.cookId.toString())
                .setWaiterId(payload.waiterId.toString())
                .setOrderId(payload.orderId.toString())
                .setItems(
                        payload.items.map {
                            codes.monkey.cafe.kitchen.events.Item.newBuilder()
                                    .setName(it.name)
                                    .setQuantity(it.quantity)
                                    .build()
                        }
                ).build()
        kafkaTemplate.send(producerTopic, payload.cookId.toString(), publicEvent)
    }

    @EventHandler(payloadType = OrderPreparationCompletedEvent::class)
    fun onOrderPreparationCompletedEvent(event: EventMessage<OrderPreparationCompletedEvent>) {
        val payload = event.payload
        val publicEvent = codes.monkey.cafe.kitchen.events.OrderPreparationCompletedEvent.newBuilder()
                .setEventId(event.identifier)
                .setCookId(payload.cookId.toString())
                .setWaiterId(payload.waiterId.toString())
                .setOrderId(payload.orderId.toString())
                .build()
        kafkaTemplate.send(producerTopic, payload.cookId.toString(), publicEvent)
    }
}
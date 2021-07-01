package codes.monkey.cafe.cashier.adapters.kafka.producer

import codes.monkey.cafe.cashier.domain.model.BillCreatedEvent
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

    @EventHandler(payloadType = BillCreatedEvent::class)
    fun onBillCreatedEvent(event: EventMessage<BillCreatedEvent>) {
        val payload = event.payload
        val publicEvent = codes.monkey.cafe.cashier.events.BillCreatedEvent.newBuilder()
                .setEventId(event.identifier)
                .setOrderId(payload.orderId.toString())
                .setItems(
                        payload.items.map {
                            codes.monkey.cafe.cashier.events.BillItem.newBuilder()
                                    .setName(it.name)
                                    .setQuantity(it.quantity)
                                    .setTotal(it.total)
                                    .build()
                        }
                ).build()
        kafkaTemplate.send(producerTopic, payload.orderId.toString(), publicEvent)
    }

}
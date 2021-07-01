package codes.monkey.cafe.stockroom.adapters.kafka.producer

import codes.monkey.cafe.stockroom.domain.model.OutOfStockEvent
import codes.monkey.cafe.stockroom.domain.model.StockroomReStockedEvent
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.EventMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class KafkaProducer(val kafkaTemplate: KafkaTemplate<String, SpecificRecordBase>,
                    @Value(value = "\${kafka.producer.topic}")
                    val producerTopic: String) {

    @EventHandler(payloadType = OutOfStockEvent::class)
    fun onOutOfStockEvent(event: EventMessage<OutOfStockEvent>) {
        val publicEvent = codes.monkey.cafe.stockroom.events.OutOfStockEvent.newBuilder()
                .setEventId(event.identifier)
                .setStock(event.payload.stock)
                .build()

        kafkaTemplate.send(producerTopic, event.payload.id.toString(), publicEvent)

    }

    @EventHandler(payloadType = StockroomReStockedEvent::class)
    fun onStockroomReStockedEvent(event: EventMessage<StockroomReStockedEvent>) {
        val publicEvent = codes.monkey.cafe.stockroom.events.StockroomReStockedEvent.newBuilder()
                .setEventId(event.identifier)
                .setNewStock(event.payload.newStock.map { it.name })
                .build()

        kafkaTemplate.send(producerTopic, event.payload.id.toString(), publicEvent)
    }
}
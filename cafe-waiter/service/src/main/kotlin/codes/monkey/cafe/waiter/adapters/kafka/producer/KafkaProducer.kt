package codes.monkey.cafe.waiter.adapters.kafka.producer

import codes.monkey.cafe.waiter.domain.model.OrderTakenEvent
import codes.monkey.cafe.waiter.domain.model.WaiterHiredEvent
import codes.monkey.cafe.waiter.events.Item
import codes.monkey.cafe.waiter.events.OrderDeliveredEvent
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.EventMessage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.kafka.annotation.KafkaListener




@Service
class KafkaProducer(val kafkaTemplate: KafkaTemplate<String, SpecificRecordBase>) {

    @EventHandler(payloadType = OrderTakenEvent::class)
    fun on(event: EventMessage<OrderTakenEvent>) {
        val publicEvent = codes.monkey.cafe.waiter.events.OrderTakenEvent.newBuilder()
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
        kafkaTemplate.send("cafe.waiter.events", publicEvent)
    }

    @EventHandler(payloadType = WaiterHiredEvent::class)
    fun onTest(event: EventMessage<WaiterHiredEvent>) {
        val publicEvent = codes.monkey.cafe.waiter.events.OrderTakenEvent.newBuilder()
                .setWaiterId("testWaiterId")
                .setOrderId("testOrderId")
                .setItems(
                            listOf( Item.newBuilder()
                                    .setName("testItem")
                                    .setQuantity(1)
                                    .build())


                ).build()
        val result = kafkaTemplate.send("cafe.waiter.events", publicEvent).get()
        println(result)

        val orderDeliveredEvent = OrderDeliveredEvent.newBuilder()
                .setWaiterId("waiter2")
                .setOrderId("orderId2")
                .build()
        val result1 = kafkaTemplate.send("cafe.waiter.events", orderDeliveredEvent).get()
        println(result1)
    }

    @KafkaListener(topics = ["cafe.waiter.events"], groupId = "waiterService")
    fun listenGroupFoo(message: SpecificRecordBase) {
        throw Exception("oh crap")
//        println("Received Message in group foo: $message (${message.javaClass.simpleName})")
    }

}
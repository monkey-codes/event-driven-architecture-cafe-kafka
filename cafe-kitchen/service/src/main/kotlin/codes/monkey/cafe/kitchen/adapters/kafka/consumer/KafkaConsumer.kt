package codes.monkey.cafe.kitchen.adapters.kafka.consumer

import codes.monkey.cafe.kitchen.domain.model.Item
import codes.monkey.cafe.kitchen.domain.model.KitchenConstants
import codes.monkey.cafe.kitchen.domain.model.QueueOrderCommand
import codes.monkey.cafe.waiter.events.OrderTakenEvent
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaConsumer(
        val commandGateway: CommandGateway) {

    @KafkaListener(topics = ["cafe.waiter.events"])
    fun handlePublicEvents(event: SpecificRecordBase) {
        when (event) {
            is OrderTakenEvent -> commandGateway.sendAndWait<Any>(QueueOrderCommand(queueId = KitchenConstants.ORDER_QUEUE_ID,
                    orderId = UUID.fromString(event.orderId.toString()),
                    waiterId = UUID.fromString(event.waiterId.toString()),
                    items = event.items.map { Item(name = it.name.toString(), quantity = it.quantity) }))
        }
    }
}
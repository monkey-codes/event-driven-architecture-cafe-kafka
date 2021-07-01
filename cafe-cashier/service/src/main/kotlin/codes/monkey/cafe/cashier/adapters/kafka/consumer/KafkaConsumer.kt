package codes.monkey.cafe.cashier.adapters.kafka.consumer

import codes.monkey.cafe.cashier.domain.model.CreateBillCommand
import codes.monkey.cafe.cashier.domain.model.Item
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
            is OrderTakenEvent -> commandGateway.sendAndWait<Any>(
                    CreateBillCommand(id = UUID.randomUUID(),
                            orderId = UUID.fromString(event.orderId.toString()),
                            items = event.items.map { Item(name = it.name.toString(), quantity = it.quantity) })
            )
        }
    }
}
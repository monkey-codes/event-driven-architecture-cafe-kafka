package codes.monkey.cafe.stockroom.adapters.kafka.consumer

import codes.monkey.cafe.kitchen.events.OrderPreparationStartedEvent
import codes.monkey.cafe.stockroom.domain.model.Item
import codes.monkey.cafe.stockroom.domain.model.StockroomConstants
import codes.monkey.cafe.stockroom.domain.model.UseStockCommand
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer(
        val commandGateway: CommandGateway) {

    @KafkaListener(topics = ["cafe.kitchen.events"])
    fun handlePublicEvents(event: SpecificRecordBase) {
        when (event) {
            is OrderPreparationStartedEvent -> commandGateway.sendAndWait<Any>(UseStockCommand(
                    id = StockroomConstants.STOCKROOM_ID,
                    stock = event.items.map { Item(name = it.name.toString(), quantity = it.quantity) }
            ))
        }
    }
}